package com.eelly.seller.common.net;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.eelly.framework.util.DeviceUtil;
import com.eelly.framework.util.LogUtil;
import com.eelly.framework.util.StringUtil;
import com.eelly.seller.constants.Constants;
import com.eelly.sellerbuyer.net.ApiError;
import com.eelly.sellerbuyer.net.ApiRequest;
import com.eelly.sellerbuyer.net.ApiRequest.ApiRequestListener;
import com.eelly.sellerbuyer.net.ApiResponse;
import com.eelly.sellerbuyer.net.EellyServerApi;
import com.eelly.sellerbuyer.net.FileUploadAsyncTask;
import com.eelly.sellerbuyer.net.FileUploadAsyncTask.FileUploadListener;
import com.google.gson.JsonElement;

/**
 * 卖家网络请求基础API
 * 
 * @author 王晓明
 */
public class BaseServerApi extends EellyServerApi {

	/**
	 * http头：用户token
	 */
	public static final String HEADER_AUTHORIZATION = "AndroidAuthorization";

	public static final String APP_CLIENT = "android";

	public static final String APP_TYPE = "seller";

	public static final String APP_SECRET = "MBx*430(5V1zZ|aADF$@:afbvr^bgKv+";

	// 缓存时间
	private static final long CACHE_TIME = 30 * 24 * 3600 * 1000L;

	/**
	 * 获取服务端api基础地址
	 */
	protected final String getApiBaseURL() {
		return getApiBaseURL(getContext());
	}

	/**
	 * 获取服务端api基础地址
	 */
	public static final String getApiBaseURL(Context context) {
		return NetConfig.getApiBaseURL(context) + "/index.php?m=api";
	}

	/**
	 * 获取服务端api基础地址,如 http://172.18.107.96:8081/api/
	 */
	protected final String getApiBaseURL2() {
		return getApiBaseURL2(getContext());
	}

	/**
	 * 获取服务端api基础地址,如 http://172.18.107.96:8081/api/
	 * 
	 * @param context
	 * @return
	 */
	public static final String getApiBaseURL2(Context context) {
		return NetConfig.getApiBaseURL(context) + "/api";
	}

	public BaseServerApi(final Context context) {
		super(context);
		setApiRequestListener(new ApiRequestListener() {

			/**
			 * 提示信息对象
			 */
			private Toast mToast;

			@Override
			public void onPreRequest(ApiRequest<?> apiRequest) {
				// 设置头信息
				String appVersion = String.valueOf(Constants.packageInfo_versionCode);
				String appTimestamp = String.valueOf(System.currentTimeMillis());
				String appSign = StringUtil.md5Lcase(appVersion + APP_CLIENT + appTimestamp + APP_SECRET);
				apiRequest.addHeader("appClient", APP_CLIENT);
				apiRequest.addHeader("appType", APP_TYPE);
				apiRequest.addHeader("appVersion", appVersion);
				apiRequest.addHeader("appTimestamp", appTimestamp);
				apiRequest.addHeader("appSign", appSign);
				apiRequest.addHeader("deviceNumber", DeviceUtil.getIMEI(getContext()));

				// 设置缓存时间
				apiRequest.setCacheTime(CACHE_TIME);
				// 在所有请求执行前默认加入用户token信息
				/*User user = AccountManager.getInstance().getUser();
				if (user != null) {
					String userToken = user.getTokenKey();
					LogUtil.d("ApiRequestListener", "token:" + userToken);
					// 设置用户请求Token
					apiRequest.addHeader(HEADER_AUTHORIZATION, userToken);
					// 设置用户id用于区别不同用户的缓存
					apiRequest.addParam("_user_cacheid", user.getUid());
				}*/
				//apiRequest.setTimeOut(1*60000);
			}

			@Override
			public void onError(ApiRequest<?> apiRequest, ApiResponse<?> apiResponse) {
				if (mToast == null) {
					mToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
				}
				String msg = null;
				if (apiResponse.isNoConnectionError()) {
					msg = "网络未连接,请检查网络";
				} else if (apiResponse.isTimeoutError()) {
					msg = "当前网络不佳，请稍后重试";
				} else if (apiResponse.isNetworkError()) {
					msg = "请求失败,请重试";
				} else if (apiResponse.isServerError()) {
					msg = "服务器错误";
				} else if (apiResponse.isParseError()) {
					msg = "服务器返回异常";
				} else if (apiResponse.isApiError()) {
					ApiError apiError = apiResponse.getError();
					int errorCode = apiError.getStatusCode();
					LogUtil.d("test", "msg:" + apiError.getMessage() + " code:" + apiError.getStatusCode());
					if (errorCode == Constants.TOKEN_EXPIRE_CODE || errorCode == Constants.TOKEN_ERROR_CODE
							|| errorCode == Constants.TOKEN_EXPIRE_CODE1 || errorCode == Constants.TOKEN_EXPIRE_CODE2
							|| errorCode == Constants.TOKEN_EXPIRE_CODE3 || errorCode == Constants.TOKEN_EXPIRE_CODE4) {
						//MainBroadcastReceiver.relogin(getContext());
					} else if (errorCode == Constants.NO_PERMISSION_CODE) {
						msg = apiResponse.getErrorMsg();
						Context context = getContext();
						/*if (context instanceof Activity && !(context instanceof MainActivity)) {
							((Activity) context).finish();
						}*/
					}
				}
				if (!apiRequest.isSilent() && msg != null && msg.length() > 0) {
					mToast.setText(msg);
					mToast.show();
				}

				if (apiResponse.hasError()) {
					LogUtil.w("ApiRequestError", apiResponse.getErrorMsg());
					if (apiResponse.getError() instanceof VolleyError) {
						VolleyError error = apiResponse.getError();
						if (error.getMessage() != null)
							LogUtil.w("ApiRequestError", error.getMessage());
						if (error.networkResponse != null && error.networkResponse.data != null)
							LogUtil.w("ApiRequestError", new String(error.networkResponse.data));
					}
				}
			}

		});
	}

	/**
	 * 获取新的上传任务，已经加入http头信息
	 * 
	 * @param url
	 * @param listener
	 * @return
	 */
	public static FileUploadAsyncTask getFileUploadAsyncTask(String url, FileUploadListener listener) {
		FileUploadAsyncTask task = new FileUploadAsyncTask(url, listener);
		// 设置头信息
		String appVersion = String.valueOf(Constants.packageInfo_versionCode);
		String appTimestamp = String.valueOf(System.currentTimeMillis());
		String appSign = StringUtil.md5Lcase(appVersion + APP_CLIENT + appTimestamp + APP_SECRET);
		task.addHeader("appClient", APP_CLIENT);
		task.addHeader("appType", APP_TYPE);
		task.addHeader("appVersion", appVersion);
		task.addHeader("appTimestamp", appTimestamp);
		task.addHeader("appSign", appSign);

		// 加入用户token信息
		/*User user = AccountManager.getInstance().getUser();
		if (user != null) {
			String userToken = user.getTokenKey();
			LogUtil.d("FileUploadAsyncTask", "token:" + userToken);
			// 设置用户请求Token
			task.addHeader(HEADER_AUTHORIZATION, userToken);
		}*/

		// 设置超时时间
		task.setConnectTimeout(60000); // 1min
		task.setReadTimeout(600000); // 10min
		return task;
	}

	/**
	 * 从服务端返回结果里解析结果字符串，形如 {"statusCode":0,"data":{"result":"取消订单成功"}}<br>
	 * 会依次尝试 result/message/msg/success 这些字段名
	 * 
	 * @param je ResponseParser.parse 传进来的参数
	 * @return 解析不了返回null
	 */
	public static String getStringResult(JsonElement je) {
		String res = getStringResult(je, "result");
		if (res == null)
			res = getStringResult(je, "message");
		if (res == null)
			res = getStringResult(je, "msg");
		if (res == null)
			res = getStringResult(je, "success");
		return res;
	}

	/**
	 * 从服务端返回结果里解析结果字符串，形如 {"statusCode":0,"data":{"result":"取消订单成功"}}
	 * 
	 * @param je ResponseParser.parse 传进来的参数
	 * @param fieldName 字符串字段名
	 * @return 解析不了返回null
	 */
	public static String getStringResult(JsonElement je, String fieldName) {
		if (je == null || !je.isJsonObject())
			return null;
		try {
			JsonElement je2 = je.getAsJsonObject().get(fieldName);
			if (je2 != null)
				return je2.getAsString();
		} catch (Exception e) {
		}
		return null;
	}


	/**
	 * 获取新的上传任务，已经加入http头信息
	 *
	 * @param url
	 * @param listener
	 * @return
	 */
	public static FileUploadAsyncTask getFileUploadAsyncTaskForShopSignBoard(String url, FileUploadListener listener,boolean isUpLoadShopBoard) {
		FileUploadAsyncTask task = new FileUploadAsyncTask(url, listener,isUpLoadShopBoard);
		// 设置头信息
		String appVersion = String.valueOf(Constants.packageInfo_versionCode);
		String appTimestamp = String.valueOf(System.currentTimeMillis());
		String appSign = StringUtil.md5Lcase(appVersion + APP_CLIENT + appTimestamp + APP_SECRET);
		task.addHeader("appClient", APP_CLIENT);
		task.addHeader("appType", APP_TYPE);
		task.addHeader("appVersion", appVersion);
		task.addHeader("appTimestamp", appTimestamp);
		task.addHeader("appSign", appSign);

		// 加入用户token信息
		/*User user = AccountManager.getInstance().getUser();
		if (user != null) {
			String userToken = user.getTokenKey();
			LogUtil.d("FileUploadAsyncTask", "token:" + userToken);
			// 设置用户请求Token
			task.addHeader(HEADER_AUTHORIZATION, userToken);
		}*/

		// 设置超时时间
		task.setConnectTimeout(60000); // 1min
		task.setReadTimeout(600000); // 10min
		return task;
	}

}
