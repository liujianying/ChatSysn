/**
 *
 */
package moe.chionlab.wechatmomentstat.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


/**
 * 保存上一次的朋友圈id的工具类
 *
 * @author 杨情红
 */
@SuppressLint("CommitPrefEdits")
public class SnsInfoUtil {

    private static final String LAST_SNS_INFO = "last_sns_info";
    /****
     * 上一次上传的朋友圈id
     */
    private static final String KEY_LAST_ID = "last_id";
    /****
     * 上一次上传的朋友圈发布的最新时间
     */
    private static final String KEY_LAST_TIME = "last_time";


    private static SharedPreferences sp = null;
    private static byte[] lock = new byte[0];

    /**
     * 初始化
     *
     * @param ctx
     */
    private static void ensureInit(Context ctx) {
        if (sp == null) {
            synchronized (lock) {
                if (sp == null) {
                    sp = ctx.getSharedPreferences(LAST_SNS_INFO, Context.MODE_PRIVATE);
                }
            }
        }
    }


    /**
     * 获取: 上一次上传的朋友圈id
     *
     * @param ctx
     * @param userName 微信ID
     * @return
     */
    @Deprecated
    public static String getLastId(Context ctx, String userName) {
        if (ctx == null || TextUtils.isEmpty(userName)) {
            return "";
        }
        ensureInit(ctx);
        return sp.getString(KEY_LAST_ID + userName.hashCode(), "");
    }


    /*****
     * 保存:上传的朋友圈id
     *
     * @param ctx
     * @param userName  微信ID
     * @param snsInfoId 数据ID
     */
    @Deprecated
    public static void saveLastId(Context ctx, String userName, String snsInfoId) {
        if (ctx == null || TextUtils.isEmpty(userName)) {
            return;
        }
        ensureInit(ctx);
        sp.edit().putString(KEY_LAST_ID + userName.hashCode(), snsInfoId).commit();
    }

    /**
     * 获取: 上一次上传的朋友圈发布最新时间
     *
     * @param ctx
     * @param userName 微信ID
     * @return
     */
    public static long getLastTime(Context ctx, String userName) {
        if (ctx == null || TextUtils.isEmpty(userName)) {
            return 0;
        }
        ensureInit(ctx);
        return sp.getLong(KEY_LAST_TIME + userName.hashCode(), 0);
    }


    /*****
     * 保存: 上一次上传的朋友圈发布最新时间
     *
     * @param ctx
     * @param userName 微信ID
     * @param time     发布最新时间
     */
    public static void saveLastTime(Context ctx, String userName, long time) {
        if (ctx == null || TextUtils.isEmpty(userName)) {
            return;
        }
        ensureInit(ctx);
        sp.edit().putLong(KEY_LAST_TIME + userName.hashCode(), time).commit();
    }

}
