package moe.chionlab.wechatmomentstat.daemon;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.eelly.seller.common.net.NetConfig;
import com.eelly.seller.common.net.NewBaseServerApi;
import com.eelly.seller.constants.Constants;
import com.marswin89.marsdaemon.DaemonApplication;
import com.marswin89.marsdaemon.DaemonConfigurations;

import java.io.File;
import java.util.LinkedHashMap;

import moe.chionlab.wechatmomentstat.parser.Config;

/**
 * 保持后台服务长久运行的application
 * <p/>
 * Implementation 1<br/>
 * override one method is ok.<br/>
 * <p/>
 * Created by Mars on 12/24/15.
 */
public class WXFriendDataApplication extends DaemonApplication {

    public static WXFriendDataApplication context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
//        PreferencesUtil.saveAccessToken(this, null);

        // 初始化全局异常捕获:在发布后，才打开全局异常捕获
        initCrashHandler();

        // 初始化服务端连接环境
        NetConfig.init();

        // 保存应用信息
        Constants.packageInfo_packageName = context.getPackageName();
        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            Constants.packageInfo_versionCode = packInfo.versionCode;
            Constants.packageInfo_versionName = packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

/***
 * TODO
 * ************************上传崩溃日志start**************************************
 * *
 */
    /****
     * 初始化全局异常捕获处理类
     */
    public void initCrashHandler() {
        // 初始化全局异常捕获
        CrashHandler.getInstance().init(getApplicationContext(), Config.EXT_CRASH_DIR, "crash", ".txt", new CrashHandler.CrashCallback() {

            @Override
            public void upload(File crashDir, File crashFile, String crashInfo) {
                //测试开发阶段：不上传到服务器，只在本地生成崩溃日志
                //
            }

            @Override
            public void addOtherCrashInfo(LinkedHashMap<String, String> map) {
                if (map == null) {
                    return;
                }
                // map.put("otherInfo", "this is additional information");
            }
        });
    }
    /***
     * ************************上传崩溃日志 end***************************************
     */
    /**
     * 全局资源释放方法,在退出应用时调用
     */
    public void destory() {
        // 停止Http请求服务
        NewBaseServerApi.stopServer();
    }

    /**
     * you can override this method instead of {@link android.app.Application attachBaseContext}
     *
     * @param base
     */
    @Override
    public void attachBaseContextByDaemon(Context base) {
        super.attachBaseContextByDaemon(base);
    }


    /**
     * give the configuration to lib in this callback
     *
     * @return
     */
    @Override
    protected DaemonConfigurations getDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "moe.chionlab.wechatmomentstat:process1",
                TaskService.class.getCanonicalName(),
                GuardReceiver1.class.getCanonicalName());

        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "moe.chionlab.wechatmomentstat:process2",
                GuardService2.class.getCanonicalName(),
                GuardReceiver2.class.getCanonicalName());

        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }


    class MyDaemonListener implements DaemonConfigurations.DaemonListener {
        @Override
        public void onPersistentStart(Context context) {
        }

        @Override
        public void onDaemonAssistantStart(Context context) {
        }

        @Override
        public void onWatchDaemonDaed() {
        }
    }

}
