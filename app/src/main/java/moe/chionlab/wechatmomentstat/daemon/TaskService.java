package moe.chionlab.wechatmomentstat.daemon;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.eelly.seller.common.net.AccessToken;
import com.eelly.seller.common.net.AppStartApi;
import com.eelly.seller.common.net.NetConfig;
import com.eelly.seller.common.net.StringRequestTask;
import com.eelly.seller.common.util.PreferencesUtil;
import com.eelly.seller.constants.Constants;
import com.eelly.sellerbuyer.net.ApiListener;
import com.eelly.sellerbuyer.net.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import moe.chionlab.wechatmomentstat.AppConfig;
import moe.chionlab.wechatmomentstat.Model.FriendData;
import moe.chionlab.wechatmomentstat.Model.FriendItemData;
import moe.chionlab.wechatmomentstat.Model.SnsInfo;
import moe.chionlab.wechatmomentstat.Model.UploadResult;
import moe.chionlab.wechatmomentstat.parser.Config;
import moe.chionlab.wechatmomentstat.parser.SnsStat;
import moe.chionlab.wechatmomentstat.parser.Task;
import moe.chionlab.wechatmomentstat.parser.WXDataApi;
import moe.chionlab.wechatmomentstat.util.GetWXNumUtil;
import moe.chionlab.wechatmomentstat.util.LogUtil;
import moe.chionlab.wechatmomentstat.util.SnsInfoUtil;
import moe.chionlab.wechatmomentstat.util.UploadDataUtil;

/**
 * 保持长久运行的服务：在这里做你想做的任务
 * <p>
 * This Service is Persistent Service. Do some what you want to do here.<br/>
 * <p>
 * Created by Mars on 12/24/15.
 *
 * @author Mars, 杨情红
 */
public class TaskService extends Service {

    /****
     * 同步朋友圈数据的间隔时间
     */
    public static int TASK_UPLOAD_WX_MOMENT_PERIOD = AppConfig.TASK_UPLOAD_WX_MOMENT_PERIOD;
    /****
     * 同步微信好友的间隔时间
     */
    public static int TASK_UPLOAD_WX_FRIEND_PERIOD = AppConfig.TASK_UPLOAD_WX_FRIEND_PERIOD;//1分钟
    /****
     * 上传朋友圈最新数据的分页：每页数量
     */
    public static int UPLOAD_WX_MOMENT_PAGE_COUNT = AppConfig.UPLOAD_WX_MOMENT_PAGE_COUNT;
    /****
     * 上传好友数据的分页：每页数量
     */
    public static int UPLOAD_WX_FRIEND_PAGE_COUNT = AppConfig.UPLOAD_WX_FRIEND_PAGE_COUNT;
    public static final String GET_TIME_URL = "/api/time";

    /***
     * 线程池数量
     */
    public static int THREAD_POOL_COUNT = 5;
    //
    private WXDataApi wxDataApi;
    private Gson mGson;
    private ExecutorService uploadFailThreadPool;//上传之前失败数据的线程池
    private ExecutorService uploadNewTaskThreadPool;//执行上传最新数据任务的线程池
    private ExecutorService uploadNewThreadPool;//上传最新数据的线程池
    private ExecutorService uploadFriendThreadPool;//上传微信好友的线程池
    /****
     * 是否已经开始上传微信好友数据：防止数据库文件还没有复制到SD卡
     */
    private boolean isStartUploadFriendDataTask = false;

    //
    private Timer uploadWXMomentTimer;
    private Timer uploadWXMomentFailTimer;
    private Timer uploadWXFriendTimer;

    private AppStartApi mStartApi;
    private StringRequestTask mGetTimeTask;

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(TaskService.class.getSimpleName().hashCode(), new Notification());
        //
        wxDataApi = new WXDataApi(this);
        mGson = new Gson();
        mStartApi = new AppStartApi(this);
        uploadFailThreadPool = Executors.newFixedThreadPool(THREAD_POOL_COUNT);
        uploadNewTaskThreadPool = Executors.newFixedThreadPool(1);
        uploadNewThreadPool = Executors.newFixedThreadPool(THREAD_POOL_COUNT);
        uploadFriendThreadPool = Executors.newFixedThreadPool(THREAD_POOL_COUNT);

        //
        doTask();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /***
     * 执行任务
     */
    private void doTask() {

        //同步朋友圈数据
        uploadWXMomentTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                getTime();
                //上传新的朋友圈数据
                if (uploadNewTaskThreadPool != null) {
                    uploadNewTaskThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            uploadNewDataTask();
                        }
                    });
                }

            }
        };
        uploadWXMomentTimer.schedule(task, 0, TASK_UPLOAD_WX_MOMENT_PERIOD);


        //同步朋友圈失败数据
        uploadWXMomentFailTimer = new Timer();
        TimerTask failTask = new TimerTask() {
            @Override
            public void run() {
                //上传之前失败的数据
                uploadFailDataTask();
            }
        };
        uploadWXMomentFailTimer.schedule(failTask, 0, TASK_UPLOAD_WX_MOMENT_PERIOD);
    }

    /***
     * 执行任务
     */
    private void doUploadFriendDataTask() {

        if (isStartUploadFriendDataTask) {
            return;
        }
        isStartUploadFriendDataTask = true;

        //同步微信好友
        uploadWXFriendTimer = new Timer();
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                uploadFriendData();
            }
        };

        uploadWXFriendTimer.schedule(task2, 0, TASK_UPLOAD_WX_FRIEND_PERIOD);
    }


    /*****
     * 同步微信好友
     */
    private void uploadFriendData() {
        ArrayList<FriendItemData> wxNums = GetWXNumUtil.getInstance(TaskService.this).getWxFriendList();
        //TODO 提交到服务器
        if (wxNums != null && wxNums.size() > 0) {
            int size = wxNums.size();
            LogUtil.e("同步微信好友的总数量: size=" + size);

            //
            //保存最新数据的发布时间
            final String currentUserId = GetWXNumUtil.getInstance(TaskService.this).getWxID();
            LogUtil.e("当前用户的内部微信id: currentUserId=" + currentUserId);

            //
            LogUtil.e("同步微信好友数据开始 : start uploading WX friends data!!!!!!>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n\n");
            int count = (size / UPLOAD_WX_FRIEND_PAGE_COUNT) + (size % UPLOAD_WX_FRIEND_PAGE_COUNT != 0 ? 1 : 0);

            LogUtil.e("同步微信好友数据开始:页数：count=" + count);

            for (int i = 0; i < count; i++) {
                ArrayList<FriendItemData> items = new ArrayList<FriendItemData>();
                if (i != count - 1) {
                    items.addAll(wxNums.subList(i * UPLOAD_WX_FRIEND_PAGE_COUNT, (i + 1) * UPLOAD_WX_FRIEND_PAGE_COUNT));
                } else {
                    items.addAll(wxNums.subList(i * UPLOAD_WX_FRIEND_PAGE_COUNT, size));
                }

                LogUtil.e("同步微信好友数据开始:页码：page=" + i + ", 数量为:" + items.size());

                //开始分页上传数据
                if (uploadFriendThreadPool != null) {
                    String operation = (i == 0 ? "start" : ((i == count - 1) ? "end" : ""));
                    final String currentWxNum = GetWXNumUtil.getInstance(TaskService.this).getWxNum(currentUserId);
                    uploadFriendThreadPool.execute(new UploadFriendRunnable(currentWxNum, size, items, i, operation));
                }
            }
        } else {
            LogUtil.e("同步微信好友数据为空");
            return;
        }

    }

    /****
     * 上传新的朋友圈的数据任务
     */
    private void uploadNewDataTask() {
        //执行任务
        SnsStat snsStat = null;
        Task task = null;
        try {
            task = new Task(TaskService.this);
            task.testRoot();//是否获取到root权限
            task.copySnsDB();//复制微信朋友圈的数据库到sdcard
            task.initSnsReader();//初始化数据库中表的解析器
            task.snsReader.run();//读取数据库数据
            //对数据库数据进行处理：加入评论信息，点赞信息，排名信息
            snsStat = new SnsStat(task.snsReader.getSnsList());
        } catch (Throwable e) {
            LogUtil.e("上传新的数据出现错误：error msg:" + e.getMessage());

            //TODO 删除 wechat.apk文件，重新启动服务：一般这里出现的异常是因为找不到apk中的某个类文件，所以
            //TODO 需要重新启动服务，重新复制wechat.apk文件到相应目录
            File outputAPKFile = new File(Config.EXT_DIR + "/wechat.apk");
            if (outputAPKFile.exists()) {
                outputAPKFile.delete();
            }

            //
            Intent service = new Intent(TaskService.this, TaskService.class);
            stopService(service);
            startService(service);

            return;
            //e.printStackTrace();
        }

        //TODO 在数据库文件复制过来，才开始 上传微信好友数据
        doUploadFriendDataTask();

        //
        if (snsStat == null) {
            LogUtil.e("上传新的数据为空：data is null");
            return;
        }

        //TODO
        ArrayList<SnsInfo> datas = snsStat.snsList;
        if (datas != null && datas.size() > 0) {
            int size = datas.size();
            LogUtil.e("上传新的数据的总数量: size=" + size);

            //
            //保存最新数据的发布时间
            final String currentUserId = GetWXNumUtil.getInstance(TaskService.this).getWxID();
            LogUtil.e("当前用户的内部微信id: currentUserId=" + currentUserId);
            SnsInfo newestInfo = datas.get(0);
            if (newestInfo != null) {
                LogUtil.e("最新数据的发布时间: time=" + newestInfo.timestamp);
                SnsInfoUtil.saveLastTime(TaskService.this, currentUserId, newestInfo.timestamp);
            }

            //
            LogUtil.e("上传新的数据开始 : start uploading WX circle data!!!!!!>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n\n");
            int count = (size / UPLOAD_WX_MOMENT_PAGE_COUNT) + (size % UPLOAD_WX_MOMENT_PAGE_COUNT != 0 ? 1 : 0);

            LogUtil.e("上传新的数据开始:页数：count=" + count);

            for (int i = 0; i < count; i++) {
                ArrayList<SnsInfo> items = new ArrayList<SnsInfo>();
                if (i != count - 1) {
                    items.addAll(datas.subList(i * UPLOAD_WX_MOMENT_PAGE_COUNT, (i + 1) * UPLOAD_WX_MOMENT_PAGE_COUNT));
                } else {
                    items.addAll(datas.subList(i * UPLOAD_WX_MOMENT_PAGE_COUNT, size));
                }

                LogUtil.e("上传新的数据开始:页码：page=" + i + ", 数量为:" + items.size());
                //开始分页上传数据
                if (uploadNewThreadPool != null) {
                    uploadNewThreadPool.execute(new UploadNewRunnable(items, i));
                }
            }
        } else {
            LogUtil.e("上传新的数据为空");
            return;
        }
    }

    /****
     * 上传之前失败的数据任务
     */

    private void uploadFailDataTask() {
        try {
            final String currentUserId = GetWXNumUtil.getInstance(TaskService.this).getWxID();
            HashMap<String, String> data = UploadDataUtil.getFailData(this, currentUserId);
            if (data == null || data.size() < 1) {
                LogUtil.e("上传之前的失败的数据为空");
                return;
            }
            if (uploadFailThreadPool == null) {
                uploadFailThreadPool = Executors.newFixedThreadPool(1);
            }
            for (Map.Entry<String, String> item : data.entrySet()) {
                String key = item.getKey();
                String value = item.getValue();
                uploadFailData(key, value);
            }
            data.clear();
        } catch (OutOfMemoryError error) {
            LogUtil.e("uploadFailData>> 内存溢出...");
        }

    }

    /****
     * 上传之前失败的数据
     *
     * @param key
     * @param datas
     */
    private void uploadFailData(final String key, final String datas) {

        if (wxDataApi == null) {
            wxDataApi = new WXDataApi(this);
        }
        LogUtil.e("uploadFailData>> 上传[之前上传失败的数据]:数据列表：key=" + key + "  ,data=" + datas);
        if (wxDataApi != null) {
            wxDataApi.uploadData(datas, new ApiListener<UploadResult>() {


                @Override
                public void onResponse(ApiResponse<UploadResult> response) {
                    if (response.hasError()) {
                        //上传出错
                        LogUtil.e("uploadFailData>>上传[之前上传失败的数据]：失败：upload fail data>> error: " + response.getErrorMsg());

                        try {
                            //TODO 这里不用保存了
                            /*final String currentUserId = GetWXNumUtil.getInstance(TaskService.this).getWxID();
                            UploadDataUtil.saveFailData(TaskService.this, currentUserId, System.currentTimeMillis(), datas);*/
                        } catch (Exception e) {
                            //
                            LogUtil.e("uploadFailData>>保存[之前上传失败的数据]：失败 upload fail data >> error: " + e.getMessage());
                        }
                    } else {

                        //TODO 请求成功
                        UploadResult result = response.get();
                        LogUtil.e("uploadFailData>> 请求成功：返回结果" + result);

                        if (result == null) {
                            LogUtil.e("uploadFailData>>[上传之前失败的数据]： 失败：upload fail data>> error: the UploadResult is null");
                            try {
                                //TODO 这里不用保存了
                                /*final String currentUserId = GetWXNumUtil.getInstance(TaskService.this).getWxID();
                                UploadDataUtil.saveFailData(TaskService.this, currentUserId, //
                                        System.currentTimeMillis(), datas);*/
                            } catch (Exception e) {
                                //
                                LogUtil.e("uploadFailData>>保存[上传之前失败的数据]失败 upload fail data>> error: " + e.getMessage());
                            }
                            return;
                        }

                        //HashSet<String> successSet = result.success;
                        HashSet<String> failSet = result.failure;

                        if (failSet == null || failSet.size() < 1) {
                            // LogUtil.e("uploadFailData>>保存[上传之前失败的数据] 全部保存成功");

                            //TODO 全部保存成功,删除之前的本地失败记录
                            final String currentUserId = GetWXNumUtil.getInstance(TaskService.this).getWxID();
                            UploadDataUtil.removeFailData(TaskService.this, currentUserId, key);
                            return;
                        } else {
                            // LogUtil.e("uploadFailData>>保存[上传之前失败的数据]失败，失败消息列表：" + failSet);
                        }

                        //
                        ArrayList<SnsInfo> items = null;
                        try {
                            items = mGson.fromJson(datas, new TypeToken<ArrayList<SnsInfo>>() {
                            }.getType());
                        } catch (Exception e) {
                            LogUtil.e("uploadFailData>>解析数据失败 >> error: " + e.getMessage());
                        }

                        if (items == null || items.size() < 1) {
                            return;
                        }


                        //TODO
                        ArrayList<SnsInfo> failItems = new ArrayList<SnsInfo>();
                        for (SnsInfo info : items) {
                            if (info != null && !TextUtils.isEmpty(info.id)//
                                    && failSet.contains(info.id)) {
                                failItems.add(info);

                            }
                        }
                        if (failItems == null || failItems.size() < 1) {
                            return;
                        }

                        //保存 【同步失败的数据】
                        try {
                            final String currentUserId = GetWXNumUtil.getInstance(TaskService.this).getWxID();
                            //删除之前的旧记录
                            UploadDataUtil.removeFailData(TaskService.this, currentUserId, key);
                            UploadDataUtil.saveFailData(TaskService.this, currentUserId,//
                                    System.currentTimeMillis(), mGson.toJson(failItems));
                        } catch (Exception e) {
                            //
                            LogUtil.e("uploadFailData>>保存：【同步失败的分页数据】 失败 upload fail data>> error: " + e.getMessage());
                        }
                        return;


                    }
                }
            });
        }
    }


    /***
     * 上传朋友圈数据的分页任务
     */
    public class UploadNewRunnable implements Runnable {
        private ArrayList<SnsInfo> items;
        private int index;

        /****
         * 上传朋友圈数据的分页任务
         *
         * @param items 上传的数据
         * @param index 上传的数据在分页的索引
         */
        public UploadNewRunnable(ArrayList<SnsInfo> items, int index) {
            this.items = items;
            this.index = index;
        }


        @Override
        public void run() {

            try {
                Thread.sleep(index * 100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (items == null || items.size() < 1) {
                LogUtil.e("UploadNewRunnable>>上传新数据：分页数据为空");
                return;
            }
            if (wxDataApi == null) {
                wxDataApi = new WXDataApi(TaskService.this);
            }
            LogUtil.e("UploadNewRunnable>> 上传新数据：index=" + index + " ，数据列表：" + items);
            if (wxDataApi != null) {
                wxDataApi.uploadData(items, new ApiListener<UploadResult>() {
                    @Override
                    public void onResponse(ApiResponse<UploadResult> response) {
                        if (response.hasError()) {
                            //上传出错
                            LogUtil.e("UploadNewRunnable>>上传新数据：分页数据失败：upload index=" + index + ">> error: " + response.getErrorMsg());

                            try {
                                final String currentUserId = GetWXNumUtil.getInstance(TaskService.this).getWxID();
                                UploadDataUtil.saveFailData(TaskService.this, currentUserId, System.currentTimeMillis(), mGson.toJson(items));
                            } catch (Exception e) {
                                //
                                LogUtil.e("UploadNewRunnable>>保存分页数据失败 index=" + index + ">> error: " + e.getMessage());
                            }
                        } else {

                            //TODO 请求成功
                            UploadResult result = response.get();
                            LogUtil.e("UploadNewRunnable>> 请求成功：返回结果" + result);
                            if (result == null) {
                                LogUtil.e("UploadNewRunnable>>上传新数据：分页数据失败：upload index=" + index + ">> error: the UploadResult is null");
                                try {
                                    final String currentUserId = GetWXNumUtil.getInstance(TaskService.this).getWxID();
                                    UploadDataUtil.saveFailData(TaskService.this, currentUserId, //
                                            System.currentTimeMillis(), mGson.toJson(items));
                                } catch (Exception e) {
                                    //
                                    LogUtil.e("UploadNewRunnable>>保存分页数据失败 index=" + index + ">> error: " + e.getMessage());
                                }
                                return;
                            }

                            //HashSet<String> successSet = result.success;
                            HashSet<String> failSet = result.failure;

                            if (failSet == null || failSet.size() < 1) {
                                //全部保存成功
                                LogUtil.e("UploadNewRunnable>>保存分页数据 全部保存成功");
                                return;
                            } else {
                                LogUtil.e("UploadNewRunnable>>保存分页数据失败: 失败列表：" + failSet);
                            }
                            ArrayList<SnsInfo> failItems = new ArrayList<SnsInfo>();
                            if (items == null || items.size() < 1) {
                                return;
                            }

                            for (SnsInfo info : items) {
                                if (info != null && !TextUtils.isEmpty(info.id)//
                                        && failSet.contains(info.id)) {
                                    failItems.add(info);

                                }
                            }
                            if (failItems == null || failItems.size() < 1) {
                                return;
                            }

                            //保存 【同步失败的数据】
                            try {
                                final String currentUserId = GetWXNumUtil.getInstance(TaskService.this).getWxID();
                                UploadDataUtil.saveFailData(TaskService.this, currentUserId,//
                                        System.currentTimeMillis(), mGson.toJson(failItems));
                            } catch (Exception e) {
                                //
                                LogUtil.e("UploadNewRunnable>>保存：【同步失败的分页数据】 失败 index=" + index + ">> error: " + e.getMessage());
                            }
                            return;

                        }
                    }
                });
            }

        }
    }

    /***
     * 上传微信好友数据的分页任务
     */
    public class UploadFriendRunnable implements Runnable {
        private final String currentWxNum;
        private final int allCount;
        private final String operation;
        private ArrayList<FriendItemData> items;
        private int index;

        /****
         * 上传微信好友数据的分页任务
         *
         * @param currentWxNum 当前用户的微信号
         * @param allCount     上传的所有数据的总数量
         * @param items        上传的数据
         * @param index        上传的数据在分页的索引
         */
        public UploadFriendRunnable(String currentWxNum, int allCount,//
                                    ArrayList<FriendItemData> items, int index, String operation) {
            this.currentWxNum = currentWxNum;
            this.allCount = allCount;
            this.items = items;
            this.index = index;
            this.operation = operation;
        }


        @Override
        public void run() {
            if (TextUtils.isEmpty(currentWxNum)) {
                LogUtil.e("UploadFriendRunnable>>上传微信好友数据：当前用户的微信号为null");
                return;
            }
            if (items == null || items.size() < 1) {
                LogUtil.e("UploadFriendRunnable>>上传微信好友数据：分页数据为空");
                return;
            }
            if (wxDataApi == null) {
                wxDataApi = new WXDataApi(TaskService.this);
            }
            if (wxDataApi != null) {
                FriendData data = new FriendData();
                data.setServiceNum(currentWxNum);
                data.setOperation(operation);
                data.setCount(allCount);
                data.setParams(items);
                LogUtil.e("UploadFriendRunnable>> 上传好友数据：index=" + index + " ，数据列表：" + data);
                wxDataApi.uploadFriendData(data, new ApiListener<Boolean>() {
                    @Override
                    public void onResponse(ApiResponse<Boolean> response) {
                        if (response.hasError()) {
                            //上传出错
                            LogUtil.e("UploadFriendRunnable>>上传微信好友数据：分页数据失败：upload index=" + index + ">> error: " + response.getErrorMsg());
                        } else {
                            boolean result = response.get();
                            if (result) {
                                LogUtil.e("UploadFriendRunnable>>上传微信好友数据(请求成功)：分页数据成功：upload index=" + index);
                            } else {
                                LogUtil.e("UploadFriendRunnable>>上传微信好友数据(请求成功)：分页数据失败：upload index=" + index);
                            }
                        }
                    }
                });
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //
        clear();
    }

    /****
     * 清理数据
     */
    private void clear() {
        //
        if (uploadWXFriendTimer != null) {
            uploadWXFriendTimer.cancel();
            uploadWXFriendTimer = null;
        }
        if (uploadWXMomentTimer != null) {
            uploadWXMomentTimer.cancel();
            uploadWXMomentTimer = null;
        }
        //
        if (wxDataApi != null) {
            wxDataApi.cancelAll();
            wxDataApi = null;
        }

        if (uploadFailThreadPool != null && uploadFailThreadPool.isShutdown()) {
            uploadFailThreadPool.shutdown();

        }
        if (uploadNewTaskThreadPool != null && uploadNewTaskThreadPool.isShutdown()) {
            uploadNewTaskThreadPool.shutdown();

        }
        if (uploadNewThreadPool != null && uploadNewThreadPool.isShutdown()) {
            uploadNewThreadPool.shutdown();

        }
        if (uploadFriendThreadPool != null && uploadFriendThreadPool.isShutdown()) {
            uploadFriendThreadPool.shutdown();

        }
    }

    /**
     * 获取系统时间
     */
    private void getTime() {
        mGetTimeTask = new StringRequestTask(NetConfig.getNewApiBaseURL(this) + GET_TIME_URL, new StringRequestTask.ResponseListener() {

            @Override
            public void reponse(String result) {
                PreferencesUtil.saveDifferTime(TaskService.this, result);
                getAccessToken();
            }

            @Override
            public void onError(int httpCode, String errorMsg) {
                String defaultTime = String.valueOf(System.currentTimeMillis() / 1000L);
                PreferencesUtil.saveDifferTime(TaskService.this, defaultTime);
                getAccessToken();
            }
        });
        mGetTimeTask.execute();
    }

    /**
     * 获取AccessToken
     */
    private void getAccessToken() {
        mStartApi.getAccessToken(new ApiListener<AccessToken>() {

            @Override
            public void onResponse(ApiResponse<AccessToken> response) {
                if (response.hasError()) {
                    if (response.isApiError()) {
                        Toast.makeText(TaskService.this, response.getErrorMsg(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    AccessToken token = response.get();
                    if (token != null) {
                        Constants.accessToken = token.getAccesstoken();
                        PreferencesUtil.saveAccessToken(TaskService.this, token);
                    }
                }
            }
        });
    }
}


