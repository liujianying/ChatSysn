package com.eelly.sellerbuyer.net;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 网络配置类
 * 
 * @author 苏腾
 */
public class BaseNetConfig {

	/** 网络环境-test */
	public static final String NET_ENVIRONMENT_TEST = "test";

	/** 网络环境-local */
	public static final String NET_ENVIRONMENT_LOCAL = "local";

	/** 网络环境-online */
	public static final String NET_ENVIRONMENT_ONLINE = "online";

	/** 网络环境数组 */
	public static final String[] NET_ENVIRONMENTS = { NET_ENVIRONMENT_TEST, NET_ENVIRONMENT_LOCAL, NET_ENVIRONMENT_ONLINE };

	/** test环境api基础地址 */
	public final static String API_BASE_URL_TEST = "http://172.18.107.96:8081";

	/** local环境api基础地址 */
	public final static String API_BASE_URL_LOCAL = "http://172.18.107.96:8001";

	/** online环境api基础地址 */
	private final static String API_BASE_URL_ONLINE = "http://pifaquan.eelly.com";

	/** im线上环境 */
	public static final String IM_ON_LINE_1 = "http://im.eelly.net:3000";

	/** im线上环境 */
	public static final String IM_ON_LINE_2 = "http://im2.eelly.net:3000";

	/** Im线下环境 */
	public static final String IM_OFFLINE = "http://172.18.107.96:3000";

	/** 网络环境-默认 */
	protected static String defaultNetEnvironment = NET_ENVIRONMENT_ONLINE;

	public static String getNetEnvironment(Context context) {
		SharedPreferences sp = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
		return sp.getString("NetEnvironment", defaultNetEnvironment);
	}

	public static void setNetEnvironment(Context context, String environment) {
		SharedPreferences.Editor editor = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE).edit();
		editor.putString("NetEnvironment", environment);
		editor.commit();
	}

	/**
	 * 是否test环境
	 */
	public static boolean isTest(String environment) {
		return NET_ENVIRONMENT_TEST.equalsIgnoreCase(environment);
	}

	/**
	 * 是否local环境
	 */
	public static boolean isLocal(String environment) {
		return NET_ENVIRONMENT_LOCAL.equalsIgnoreCase(environment);
	}

	/**
	 * 是否online环境
	 */
	public static boolean isOnline(String environment) {
		return NET_ENVIRONMENT_ONLINE.equalsIgnoreCase(environment);
	}

	/**
	 * 获取服务端api的基础地址，包含scheme和域名部分
	 */
	public static String getApiBaseURL(Context context) {
		String environment = getNetEnvironment(context);
		if (isOnline(environment)) {
			return API_BASE_URL_ONLINE;
		} else if (isLocal(environment)) {
			return API_BASE_URL_LOCAL;
		} else {
			return API_BASE_URL_TEST;
		}
	}

	/**
	 * 获取bbs论坛基础地址，包含scheme和域名部分
	 */
	public static String getBbsBaseURL(Context context) {
		String environment = getNetEnvironment(context);
		if (isOnline(environment)) {
			return "http://appbbs.eelly.com";
		} else if (isLocal(environment)) {
			return "http://appbbs.eelly.local";
		} else {
			return "http://appbbs.eelly.test";
		}
	}

	/**
	 * 获取wap页面基础地址，包含scheme和域名部分
	 */
	public static String getWapBaseURL(Context context) {
		String environment = getNetEnvironment(context);
		if (isOnline(environment)) {
			return "http://m.eelly.com";
		} else if (isLocal(environment)) {
			return "http://m.eelly.local";
		} else {
			return "http://m.eelly.test";
		}
	}

	/**
	 * 新接口系统服务端地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getNewServerURL(Context context) {
		String environment = getNetEnvironment(context);
		if (isOnline(environment)) {
			return "http://mall.eelly.com/service.php";
		} else if (isLocal(environment)) {
			return "http://mall.eelly.local/service.php";
		} else {
			return "http://mall.eelly.test/service.php";
		}
	}

	public static String getNewApiBaseURL(Context context) {
		String environment = getNetEnvironment(context);
		if (isOnline(environment)) {
			return "http://mall.eelly.com";
		} else if (isLocal(environment)) {
			return "http://mall.eelly.local";
		} else {
			return "http://mall.eelly.test";
		}
	}

	/**
	 * IM是否线上环境，true 为线上，false 则是线下
	 */
	public static boolean isIMOnline(Context context) {
		String environment = getNetEnvironment(context);
		return isOnline(environment);
	}

	public static String getIMUploadUrl(Context context) {
		String url = getApiBaseURL(context);
		return url + "/api/Upfileforim/upFileIOS";
	}


	public static String getBaseBBSURL(Context context) {
		String environment = getNetEnvironment(context);
		if (isOnline(environment)) {
			return "http://bbs.eelly.com";
		} else if (isLocal(environment)) {
			return "http://bbs.eelly.local";
		} else {
			return "http://bbs.eelly.test";
		}
	}

}
