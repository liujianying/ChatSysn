package com.eelly.seller.common.util;

import android.content.Context;

/**
 * textview多种颜色
 *
 * @author 杨情红
 */
public class MultiColorTextUtil {
    private MultiColorTextUtil() {
    }

    private static StringBuilder temp;

    public static String multiColor(Context context, String source, int colorId) {
        if (context == null || colorId <= 0) {
            return source;
        }
        if (temp == null) {
            temp = new StringBuilder(32);
        } else {
            temp.setLength(0);
        }
        temp.append("<font color='");
        temp.append(context.getResources().getColor(colorId));
        temp.append("'>").append(source).append("</font>");
        return temp.toString();
    }

}