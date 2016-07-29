/**
 *
 */
package moe.chionlab.wechatmomentstat.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * 上传朋友圈数据的工具类
 *
 * @author 杨情红
 */
@SuppressLint("CommitPrefEdits")
public class UploadDataUtil {

    private static final String UPLOAD_DATA = "upload_data";
    /****
     * 上一次上传失败的朋友圈数据 key
     */
    private static final String KEY_UPLOAD_FAIL = "upload_fail";

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
                    sp = ctx.getSharedPreferences(UPLOAD_DATA, Context.MODE_PRIVATE);
                }
            }
        }
    }


    /**
     * 获取: 上一次上传失败的朋友圈数据
     *
     * @param ctx
     * @param userName 微信ID
     * @return json形式的数据集合
     */
    public static HashMap<String, String> getFailData(Context ctx, String userName) {
        if (ctx == null || TextUtils.isEmpty(userName)) {
            return null;
        }
        ensureInit(ctx);
        String prefixStr = getKeyPrefix(userName);
        Map<String, ?> data = sp.getAll();
        if (data != null && data.size() > 0) {
            HashMap<String, String> result = new HashMap<String, String>();
            for (Map.Entry<String, ?> item : data.entrySet()) {
                String key = item.getKey();
                String value = item.getValue() + "";
                if (!TextUtils.isEmpty(key) && key.contains(prefixStr)//
                        && !TextUtils.isEmpty(value)) {
                    result.put(key, value);
                }
            }
            return result;
        }
        return null;
    }


    /****
     * 保存:上一次上传失败的朋友圈数据
     *
     * @param ctx
     * @param userName 微信ID
     * @param time     时间
     * @param data     失败的数据：json形式的数据
     */
    public static void saveFailData(Context ctx, String userName, long time, String data) {
        if (ctx == null || TextUtils.isEmpty(userName) || TextUtils.isEmpty(data)) {
            return;
        }
        ensureInit(ctx);
        sp.edit().putString(getKeyPrefix(userName) + time, data).commit();
    }

    /****
     * 删除:上一次上传失败的朋友圈数据
     *
     * @param ctx
     * @param userName 微信ID
     * @param key
     */
    public static void removeFailData(Context ctx, String userName, String key) {
        if (ctx == null || TextUtils.isEmpty(userName) || TextUtils.isEmpty(key)) {
            return;
        }
        ensureInit(ctx);
        sp.edit().remove(key).commit();
    }

    /***
     * 取得上传失败的key的前缀
     *
     * @param userName
     * @return
     */
    @NonNull
    private static String getKeyPrefix(String userName) {
        if (TextUtils.isEmpty(userName)) {
            return "";
        }
        return KEY_UPLOAD_FAIL + userName.hashCode();
    }


}
