package com.eelly.seller.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.eelly.framework.util.LogUtil;
import com.eelly.seller.common.net.AccessToken;
import com.eelly.seller.constants.Constants;
import com.google.gson.Gson;

/**
 * SharedPreferences 工具
 *
 * @author 苏腾
 */
public class PreferencesUtil {

    private static final String FILE_NAME = "preferences";

    private static final String KEY_TIME_ROCK = "key_time_rock";

    private static final String KEY_ACCESS = "key_access";

    private static final String KEY_SERVER_TIME = "key_server_time";

    private static final String KEY_ENTRY = "key_entry";

    /**
     * 获取SharedPreferences实例
     *
     * @param context
     * @param needUserId 保存的信息是否与用户相关
     * @return
     */
    private static SharedPreferences getPreferences(Context context, boolean needUserId) {
        String uid = "0";
        if (needUserId) {
            try {
                uid = "111";
            } catch (Exception e) {
            }
        }
        return context.getSharedPreferences(FILE_NAME + uid, Context.MODE_PRIVATE);
    }

    /**
     * 设置发送短信计时时间
     *
     * @param context
     * @param time
     * @param mode
     */
    public static void saveCountingBeginTime(Context context, long time, int mode) {
        SharedPreferences preferences = getPreferences(context, true);
        preferences.edit().putLong(KEY_TIME_ROCK + mode, time).commit();
    }

    /**
     * 获取发送短信计时时间
     *
     * @param context
     * @param mode
     * @return
     */
    public static long getSavedCountingBeginTime(Context context, int mode) {
        return getPreferences(context, true).getLong(KEY_TIME_ROCK + mode, 0);
    }

    /**
     * 设置密码修改的入口页面，用于修改成功后页面跳转
     *
     * @param clazz
     */
    public static void setPasswordManageEntry(@NonNull Context context, @NonNull Class<? extends Activity> clazz) {
        String entry = clazz.getName();
        SharedPreferences preferences = getPreferences(context, false);
        preferences.edit().putString(KEY_ENTRY, entry).commit();
    }

    /**
     * 获取密码修改的入口页面
     *
     * @param context
     * @param defaultClazz
     * @return className
     */
    public static String getPasswordManageEntry(@NonNull Context context, @NonNull Class<? extends Activity> defaultClazz) {
        String defaultEntry = defaultClazz.getName();
        SharedPreferences preferences = getPreferences(context, false);
        return preferences.getString(KEY_ENTRY, defaultEntry);
    }

    /**
     * 获取AccessToken
     *
     * @param context
     * @return
     */
    public static AccessToken getAccessToken(Context context) {
        SharedPreferences sp = getPreferences(context, false);
        String json = sp.getString(KEY_ACCESS, "");
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            return new Gson().fromJson(json, AccessToken.class);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 保存AccessToken
     *
     * @param context
     * @param token
     */
    public static void saveAccessToken(Context context, AccessToken token) {
        SharedPreferences.Editor editor = getPreferences(context, false).edit();
        editor.putString(KEY_ACCESS, new Gson().toJson(token));
        editor.commit();
    }

    /**
     * 获取准确的时间
     *
     * @param context
     * @return
     */
    public static long getServerTime(Context context) {
        SharedPreferences sp = getPreferences(context, false);
        Long time = sp.getLong(KEY_SERVER_TIME, 0L);
        return time;
    }

    /**
     * 保存跟服务器的时间差
     *
     * @param context
     * @param time
     */
    public static void saveDifferTime(Context context, String time) {
        long differTime = 0L;
        if (!TextUtils.isEmpty(time) && TextUtils.isDigitsOnly(time)) {
            long serverTime = Long.valueOf(time);
            differTime = System.currentTimeMillis() - serverTime * 1000L;
        }
        Constants.differTime = differTime;
        SharedPreferences.Editor editor = getPreferences(context, false).edit();
        editor.putLong(KEY_SERVER_TIME, differTime);
        LogUtil.v("mine", "我去获取了服务器的时间:" + differTime);
        editor.commit();
    }

}
