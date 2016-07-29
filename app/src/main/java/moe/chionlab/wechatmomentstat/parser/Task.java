package moe.chionlab.wechatmomentstat.parser;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Toast;


import com.eelly.seller.wechat.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;
import moe.chionlab.wechatmomentstat.Model.SnsInfo;
import moe.chionlab.wechatmomentstat.util.LogUtil;

/**
 * 任务类
 * <p/>
 * Created by chiontang on 2/17/16.
 */
public class Task {

    protected Context context = null;
    public SnsReader snsReader = null;
    public Method loadMethod;
    public Object receiver;

    public Task(Context context) {
        this.context = context;
        this.makeExtDir();
    }

    public void restartWeChat() throws Throwable {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> pids = am.getRunningAppProcesses();
        int pid = -1;
        for (int i = 0; i < pids.size(); i++) {
            ActivityManager.RunningAppProcessInfo info = pids.get(i);
            if (info.processName.equalsIgnoreCase(Config.WECHAT_PACKAGE)) {
                pid = info.pid;
            }
        }
        if (pid != -1) {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
            outputStream.writeBytes("kill " + pid + "\n");
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            outputStream.close();
        }
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(Config.WECHAT_PACKAGE);
        context.startActivity(launchIntent);

    }

    /****
     * 复制微信朋友圈的数据库到sdcard
     *
     * @throws Throwable
     */
    public void copySnsDB() throws Throwable {
        String dataDir = Environment.getDataDirectory().getAbsolutePath();
        String destDir = Config.EXT_DIR;

        /*File outputAPKFile = new File(Config.EXT_DIR + "/SnsMicroMsg.db");
        if (outputAPKFile.exists()) {
            outputAPKFile.delete();
        }*/

        Process su = Runtime.getRuntime().exec("su");
        DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
        outputStream.writeBytes("mount -o remount,rw " + dataDir + "\n");
        outputStream.writeBytes("cd " + dataDir + "/data/" + Config.WECHAT_PACKAGE + "/MicroMsg\n");
        outputStream.writeBytes("ls | while read line; do cp ${line}/SnsMicroMsg.db " + destDir + "/ ; done \n");
        outputStream.writeBytes("sleep 1\n");
        outputStream.writeBytes("chmod 777 " + destDir + "/SnsMicroMsg.db\n");
        outputStream.writeBytes("exit\n");
        outputStream.flush();
        outputStream.close();

        su = Runtime.getRuntime().exec("su");
        outputStream = new DataOutputStream(su.getOutputStream());
        outputStream.writeBytes("mount -o remount,rw " + dataDir + "\n");
        outputStream.writeBytes("cd " + dataDir + "/data/" + Config.WECHAT_PACKAGE + "/MicroMsg\n");
        outputStream.writeBytes("ls | while read line; do cp ${line}/EnMicroMsg.db " + destDir + "/ ; done \n");
        outputStream.writeBytes("sleep 1\n");
        outputStream.writeBytes("chmod 777 " + destDir + "/EnMicroMsg.db\n");
        outputStream.writeBytes("exit\n");
        outputStream.flush();
        outputStream.close();

        Thread.sleep(1000);

    }

    /****
     * 是否获取到root权限
     */
    public void testRoot() {
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(context, R.string.not_rooted, Toast.LENGTH_LONG).show();
        }
    }

    public String getWeChatVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(Config.WECHAT_PACKAGE, 0);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(e.getMessage());
            return null;
        }
        String wechatVersion = "";
        if (pInfo != null) {
            wechatVersion = pInfo.versionName;
            Config.initWeChatVersion(wechatVersion);
            return wechatVersion;
        }
        return null;
    }

    public void makeExtDir() {
        File extDir = new File(Config.EXT_DIR);
        if (!extDir.exists()) {
            extDir.mkdir();
        }
    }

    public void copyAPKFromAssets() {
        InputStream assetInputStream = null;
        File outputAPKFile = new File(Config.EXT_DIR + "/wechat.apk");
        if (outputAPKFile.exists())
            outputAPKFile.delete();
        byte[] buf = new byte[1024];
        try {
            outputAPKFile.createNewFile();
            assetInputStream = context.getAssets().open("wechat.apk");
            FileOutputStream outAPKStream = new FileOutputStream(outputAPKFile);
            int read;
            while ((read = assetInputStream.read(buf)) != -1) {
                outAPKStream.write(buf, 0, read);
            }
            assetInputStream.close();
            outAPKStream.close();
        } catch (Exception e) {
            LogUtil.e("exception:" + e.getMessage());
        }
    }

    /*public void initSnsReader() {
        File outputAPKFile = new File(Config.EXT_DIR + "/wechat.apk");
        if (!outputAPKFile.exists())
            copyAPKFromAssets();

        try {

            Config.initWeChatVersion("6.3.13.64_r4488992");
            DexClassLoader cl = new DexClassLoader(
                    outputAPKFile.getAbsolutePath(),
                    context.getDir("outdex", 0).getAbsolutePath(),
                    null,
                    ClassLoader.getSystemClassLoader());
            //
            Class SnsDetailParser = null;
            Class SnsDetail = null;
            Class SnsObject = null;
            SnsDetailParser = cl.loadClass(Config.SNS_XML_GENERATOR_CLASS);
            SnsDetail = cl.loadClass(Config.PROTOCAL_SNS_DETAIL_CLASS);
            SnsObject = cl.loadClass(Config.PROTOCAL_SNS_OBJECT_CLASS);
            snsReader = new SnsReader(SnsDetail, SnsDetailParser, SnsObject);

            //TODO 这里：对于需要由android系统调用，进行初始化的一些参数，值，下面的方法，是不能直接调用的（达不到初始化的效果）
            Class appClass = cl.loadClass("com.tencent.mm.app.MMApplication");
            Object receiver = appClass.newInstance();
            Method kx = appClass.getDeclaredMethod("kx");
            kx.setAccessible(true);
            kx.invoke(receiver);


            //刷新朋友圈数据
            Class snsLoad = null;
            Class snsLoadG = null;
            snsLoad = cl.loadClass(Config.PROTOCAL_SNS_LOAD_CLASS);
            snsLoadG = cl.loadClass("com.tencent.mm.plugin.sns.d.g");
            //snsLoad = cl.loadClass("com.tencent.mm.plugin.sns.ui.SnsTimeLineUI");
            //snsLoad = cl.loadClass("com.tencent.mm.plugin.sns.d.ad");
            //


            //遍历这个类的所有方法
            Method[] methods = snsLoad.getDeclaredMethods();
            if (methods != null) {
                for (Method method : methods) {
                    Log.e("test", "method name:" + method.getName());
                }
            }


            this.loadMethod = snsLoad.getDeclaredMethod(Config.PROTOCAL_SNS_LOAD_METHOD);
            this.receiver = snsLoad.newInstance();
            if (loadMethod != null && receiver != null) {
                this.loadMethod.setAccessible(true);
                Object obj = loadMethod.invoke(null);
                if (obj != null) {
                    snsLoadG.getDeclaredMethod("start").invoke(obj);
                }
            }

        } catch (Throwable e) {
            Log.e("wechatmomentstat", "exception", e);
        }
    }

    */

    /****
     * 更新数据
     *//*
    public void updateData() {

    }*/


    /****
     * 初始化数据库中表的解析器
     */
    public void initSnsReader() {
        File outputAPKFile = new File(Config.EXT_DIR + "/wechat.apk");
        if (!outputAPKFile.exists())
            copyAPKFromAssets();

        try {

            Config.initWeChatVersion("6.3.13.64_r4488992");
            DexClassLoader cl = new DexClassLoader(
                    outputAPKFile.getAbsolutePath(),
                    context.getDir("outdex", 0).getAbsolutePath(),
                    null,
                    ClassLoader.getSystemClassLoader());

            Class SnsDetailParser = null;
            Class SnsDetail = null;
            Class SnsObject = null;
            SnsDetailParser = cl.loadClass(Config.SNS_XML_GENERATOR_CLASS);
            SnsDetail = cl.loadClass(Config.PROTOCAL_SNS_DETAIL_CLASS);
            SnsObject = cl.loadClass(Config.PROTOCAL_SNS_OBJECT_CLASS);
            snsReader = new SnsReader(SnsDetail, SnsDetailParser, SnsObject);
        } catch (Throwable e) {
            LogUtil.e("exception:" + e.getMessage());
        }
    }


    public static void saveToJSONFile(ArrayList<SnsInfo> snsList, String fileName, boolean onlySelected) {
        JSONArray snsListJSON = new JSONArray();

        for (int snsIndex = 0; snsIndex < snsList.size(); snsIndex++) {
            SnsInfo currentSns = snsList.get(snsIndex);
            if (!currentSns.ready) {
                continue;
            }
            if (onlySelected && !currentSns.selected) {
                continue;
            }
            JSONObject snsJSON = new JSONObject();
            JSONArray commentsJSON = new JSONArray();
            JSONArray likesJSON = new JSONArray();
            JSONArray mediaListJSON = new JSONArray();
            try {
                snsJSON.put("isCurrentUser", currentSns.isCurrentUser);
                snsJSON.put("snsId", currentSns.id);
                snsJSON.put("authorName", currentSns.authorName);
                snsJSON.put("authorId", currentSns.authorId);
                snsJSON.put("content", currentSns.content);
                for (int i = 0; i < currentSns.comments.size(); i++) {
                    JSONObject commentJSON = new JSONObject();
                    commentJSON.put("isCurrentUser", currentSns.comments.get(i).isCurrentUser);
                    commentJSON.put("authorName", currentSns.comments.get(i).authorName);
                    commentJSON.put("authorId", currentSns.comments.get(i).authorId);
                    commentJSON.put("content", currentSns.comments.get(i).content);
                    commentJSON.put("toUserName", currentSns.comments.get(i).toUser);
                    commentJSON.put("toUserId", currentSns.comments.get(i).toUserId);
                    commentsJSON.put(commentJSON);
                }
                snsJSON.put("comments", commentsJSON);
                for (int i = 0; i < currentSns.likes.size(); i++) {
                    JSONObject likeJSON = new JSONObject();
                    likeJSON.put("isCurrentUser", currentSns.likes.get(i).isCurrentUser);
                    likeJSON.put("userName", currentSns.likes.get(i).userName);
                    likeJSON.put("userId", currentSns.likes.get(i).userId);
                    likesJSON.put(likeJSON);
                }
                snsJSON.put("likes", likesJSON);
                for (int i = 0; i < currentSns.mediaList.size(); i++) {
                    mediaListJSON.put(currentSns.mediaList.get(i));
                }
                snsJSON.put("mediaList", mediaListJSON);
                snsJSON.put("rawXML", currentSns.rawXML);
                snsJSON.put("timestamp", currentSns.timestamp);

                snsListJSON.put(snsJSON);

            } catch (Exception e) {
                LogUtil.e("exception:" + e.getMessage());
            }
        }

        File jsonFile = new File(fileName);
        if (!jsonFile.exists()) {
            try {
                jsonFile.createNewFile();
            } catch (IOException e) {
                LogUtil.e("exception:" + e.getMessage());
            }
        }

        try {
            FileWriter fw = new FileWriter(jsonFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(snsListJSON.toString());
            bw.close();
        } catch (IOException e) {
            LogUtil.e("exception:" + e.getMessage());
        }
    }

}
