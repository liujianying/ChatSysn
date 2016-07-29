package moe.chionlab.wechatmomentstat.util;

import android.util.Log;

import moe.chionlab.wechatmomentstat.AppConfig;

/**
 * 日志类
 *
 * @author 杨情红
 */
public final class LogUtil {


    private static final String TAG = "wechatmomentstat";

    /****
     * 打印日志
     *
     * @param msg
     */
    public static void e(String msg) {
        if (AppConfig.debug) {
            Log.e(TAG, msg);
        }
    }

}
