package com.eelly.framework.util;

import android.content.Context;

import java.io.IOException;

/**
 * Android资源工具类
 * 
 * @author 李欣
 */
public class ResourcesUtil {

	/**
	 * 获取Assets下的文件内容
	 * 
	 * @param assets资源路径
	 * @return
	 * @throws IOException
	 */
	public static String getAssets(Context context, String filePath) throws IOException {
		return IOUtil.toString(context.getAssets().open(filePath));
	}
}
