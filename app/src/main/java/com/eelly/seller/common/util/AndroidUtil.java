package com.eelly.seller.common.util;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 项目使用的一些工具方法
 *
 * @author 杨情红
 */
public class AndroidUtil {


    /****
     * 得到显示用户名
     * (若为真实姓名，需要将中间字用*替换，如 李*华)
     *
     * @param nickName   用户名称
     * @param isRealName 是否是真名
     * @return
     */
    public static String getShowNickName(String nickName, boolean isRealName) {
        if (!isRealName) {
            return nickName;
        }
        if (TextUtils.isEmpty(nickName) || "".equals(nickName.trim())) {
            return "";
        }

        nickName = nickName.trim();
        int length = nickName.length();
        if (length == 2) {
            return nickName.charAt(0) + "*";
        } else if (length == 3) {
            return nickName.charAt(0) + "*" + nickName.charAt(length - 1);
        } else if (length >= 4) {
            return nickName.charAt(0) + "**" + nickName.charAt(length - 1);
        }
        return nickName;
    }


    /****
     * 得到显示数量字符串
     *
     * @param count
     * @return
     */
    public static String getShowNum(int count) {
        if (count < 10) {
            return " " + count + " ";
        } else if (count > 99) {
            return "99+";
        } else {
            return String.valueOf(count);
        }
    }

    /****
     * 得到显示数量字符串
     *
     * @param str
     * @return
     */
    public static String getShowNum(String str) {
        int count = Integer.valueOf(str);
        return getShowNum(count);
    }

    public static Serializable getSerializable(Bundle savedInstanceState, Intent intent, String key) {
        return getSerializable(savedInstanceState, intent.getExtras(), key);
    }

    public static Serializable getSerializable(Bundle savedInstanceState, Bundle intentBundle, String key) {
        if (savedInstanceState == null) {
            if (intentBundle == null) {
                return null;
            }
            return intentBundle.getSerializable(key);
        } else {
            return savedInstanceState.getSerializable(key);
        }
    }

    public static String getString(Bundle savedInstanceState, Intent intent, String key) {
        return getString(savedInstanceState, intent.getExtras(), key);
    }

    public static String getString(Bundle savedInstanceState, Bundle intentBundle, String key) {
        if (savedInstanceState == null) {
            if (intentBundle == null) {
                return null;
            }
            return intentBundle.getString(key);
        } else {
            return savedInstanceState.getString(key);
        }
    }

    public static boolean getBoolean(Bundle savedInstanceState, Intent intent, String key, boolean defValue) {
        return getBoolean(savedInstanceState, intent.getExtras(), key, defValue);
    }

    public static boolean getBoolean(Bundle savedInstanceState, Bundle intentBundle, String key, boolean defValue) {
        if (savedInstanceState == null) {
            if (intentBundle == null) {
                return defValue;
            }
            return intentBundle.getBoolean(key);
        } else {
            return savedInstanceState.getBoolean(key);
        }
    }

    public static String[] getStringArray(Bundle savedInstanceState, Bundle intentBundle, String key) {
        if (savedInstanceState == null) {
            return intentBundle.getStringArray(key);
        } else {
            return savedInstanceState.getStringArray(key);
        }
    }

    public static String[] getStringArray(Bundle savedInstanceState, Intent intent, String key) {
        return getStringArray(savedInstanceState, intent.getExtras(), key);
    }

    public static ArrayList<String> getStringArrayList(Bundle savedInstanceState, Bundle intentBundle, String key) {
        if (savedInstanceState == null) {
            if (intentBundle == null) {
                return null;
            }
            return intentBundle.getStringArrayList(key);
        } else {
            return savedInstanceState.getStringArrayList(key);
        }
    }

    public static ArrayList<String> getStringArrayList(Bundle savedInstanceState, Intent intent, String key) {
        return getStringArrayList(savedInstanceState, intent.getExtras(), key);
    }

    public static int getInt(Bundle savedInstanceState, Bundle intentBundle, String key) {
        return getInt(savedInstanceState, intentBundle, key, 0);
    }

    public static int getInt(Bundle savedInstanceState, Intent intent, String key) {
        return getInt(savedInstanceState, intent.getExtras(), key, 0);
    }

    public static int getInt(Bundle savedInstanceState, Intent intent, String key, int defValue) {
        return getInt(savedInstanceState, intent.getExtras(), key, defValue);
    }

    public static int getInt(Bundle savedInstanceState, Bundle intentBundle, String key, int defValue) {
        if (savedInstanceState == null) {
            if (intentBundle == null) {
                return defValue;
            }
            return intentBundle.getInt(key, defValue);
        } else {
            return savedInstanceState.getInt(key, defValue);
        }
    }

    public static long getLong(Bundle savedInstanceState, Bundle intentBundle, String key) {
        return getLong(savedInstanceState, intentBundle, key, 0);
    }

    public static long getLong(Bundle savedInstanceState, Intent intent, String key) {
        return getLong(savedInstanceState, intent.getExtras(), key, 0);
    }

    public static long getLong(Bundle savedInstanceState, Intent intent, String key, long defValue) {
        return getLong(savedInstanceState, intent.getExtras(), key, defValue);
    }

    public static long getLong(Bundle savedInstanceState, Bundle intentBundle, String key, long defValue) {
        if (savedInstanceState == null) {
            if (intentBundle == null) {
                return defValue;
            }
            return intentBundle.getLong(key, defValue);
        } else {
            return savedInstanceState.getLong(key, defValue);
        }
    }

    public static float getFloat(Bundle savedInstanceState, Bundle intentBundle, String key) {
        return getFloat(savedInstanceState, intentBundle, key, 0);
    }

    public static float getFloat(Bundle savedInstanceState, Intent intent, String key) {
        return getFloat(savedInstanceState, intent.getExtras(), key, 0);
    }

    public static float getFloat(Bundle savedInstanceState, Intent intent, String key, float defValue) {
        return getFloat(savedInstanceState, intent.getExtras(), key, defValue);
    }

    public static float getFloat(Bundle savedInstanceState, Bundle intentBundle, String key, float defValue) {
        if (savedInstanceState == null) {
            if (intentBundle == null) {
                return defValue;
            }
            return intentBundle.getFloat(key, defValue);
        } else {
            return savedInstanceState.getFloat(key, defValue);
        }
    }

}
