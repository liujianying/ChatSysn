package com.eelly.sellerbuyer.net;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.android.volley.Cache.Entry;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.eelly.framework.util.IOUtil;
import com.eelly.framework.util.LogUtil;
import com.eelly.sellerbuyer.net.volley.EellyRequest;
import com.eelly.sellerbuyer.net.volley.JsonElementRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端API请求类
 * 
 * @author 李欣
 */
public class ApiRequest<T> {

	private static final String TAG = "ApiRequest";

	/**
	 * Http请求方式
	 * 
	 * @author 李欣
	 */
	public static interface Method {

		public static final int GET = com.android.volley.Request.Method.GET;

		public static final int POST = com.android.volley.Request.Method.POST;

		public static final int DEPRECATED_GET_OR_POST = com.android.volley.Request.Method.DEPRECATED_GET_OR_POST;
	}

	/**
	 * 提交数据的字符编码
	 */
	private static final String GET_ENCODE = "utf8";

	/**
	 * 请求地址
	 */
	private String mRequestUrl;

	/**
	 * 请求方法
	 */
	private int mMethod;

	/**
	 * Http请求参数
	 */
	private HashMap<String, String> mRequestParams;

	/**
	 * Http请求头信息
	 */
	private HashMap<String, String> mRequestHeaders;

	private ApiListener<T> mListener;

	/**
	 * 请求监听器
	 */
	private ApiRequestListener mRequestListener;

	/**
	 * Volley框架的请求队列
	 */
	private RequestQueue mRequestQueue;

	/**
	 * Volley请求对象
	 */
	private JsonElementRequest mRequest;

	/**
	 * 是否对请求结果缓存
	 */
	private boolean mShouldCache = false;

	/**
	 * 缓存时间(单位:毫秒)
	 */
	private long mCacheTime = 0L;

	/**
	 * 软缓存时间(单位:毫秒)
	 */
	private long mSoftCacheTime = 0L;

	/**
	 * 请求是否已取消
	 */
	private volatile boolean mIsCancel = false;

	/**
	 * 请求是否静默，即没有错误提示
	 */
	private boolean mSilent = false;

	/**
	 * Volley超时重试实例, 默认20秒超时
	 */
	private RetryPolicy mRetryPolicy = new DefaultRetryPolicy(20000, 0, 0);

	/**
	 * 请求所属标签
	 */
	private Object mTag;

	/**
	 * 当前上下文
	 */
	private Context mContext;

	/**
	 * 服务端返回的数据字段
	 */
	private static final String JSON_RESULT_DATA = "data";

	/**
	 * 服务端返回的数据字段
	 */
	private static final String JSON_RESULT_STATUS = "statusCode";

	/**
	 * 执行模拟请求的Handler
	 */
	private Handler mMockhandler;

	static {
		HttpURLConnection.setDefaultAllowUserInteraction(false);
	}

	ApiRequest(int method, String url, RequestQueue requestQueue, ApiListener<T> listener, ApiRequestListener apiRequestListener, Context context) {
		mMethod = method;
		mRequestUrl = url;
		mRequestQueue = requestQueue;
		mListener = listener;
		mRequestListener = apiRequestListener;
		mRequestHeaders = new HashMap<>();
		mContext = context;
	}

	/**
	 * 设置超时时间
	 * 
	 * @param timeout 毫秒
	 */
	public void setTimeOut(int timeout) {
		mRetryPolicy = new DefaultRetryPolicy(timeout, 1, 0);
	}

	/**
	 * 设置超时时间
	 * 
	 * @param timeout 毫秒
	 * @param retryNum 自动重试次数
	 */
	public void setTimeOut(int timeout, int retry) {
		mRetryPolicy = new DefaultRetryPolicy(timeout, retry, 0);
	}

	/**
	 * 设置打开关闭缓存
	 * 
	 * @param shouldCache
	 */
	public void setCache(boolean shouldCache) {
		mShouldCache = shouldCache;
	}

	/**
	 * 设置缓存时间
	 * 
	 * @param shouldCache
	 */
	public void setCacheTime(long cacheTime) {
		mCacheTime = cacheTime;
	}

	/**
	 * 设置软缓存时间
	 * 
	 * @param shouldCache
	 */
	public void setSoftCacheTime(long cacheTime) {
		mSoftCacheTime = cacheTime;
	}

	/**
	 * @see #addParam(String, String)
	 * @param param
	 * @param value
	 */
	public void addParam(String param, boolean value) {
		addParam(param, String.valueOf(value));
	}

	/**
	 * @see #addParam(String, String)
	 * @param param
	 * @param value
	 */
	public void addParam(String param, int value) {
		addParam(param, String.valueOf(value));
	}

	/**
	 * @see #addParam(String, String)
	 * @param param
	 * @param value
	 */
	public void addParam(String param, long value) {
		addParam(param, String.valueOf(value));
	}

	/**
	 * @see #addParam(String, String)
	 * @param param
	 * @param value
	 */
	public void addParam(String param, float value) {
		addParam(param, String.valueOf(value));
	}

	/**
	 * @see #addParam(String, String)
	 * @param param
	 * @param value
	 */
	public void addParam(String param, double value) {
		addParam(param, String.valueOf(value));
	}

	/**
	 * 添加请求参数
	 * 
	 * @param param
	 * @param value
	 */
	public void addParam(String param, String value) {
		if (mRequestParams == null)
			mRequestParams = new HashMap<>();
		mRequestParams.put(param, value == null ? "" : value);
	}

	/**
	 * 添加请求头信息
	 * 
	 * @param 头信息名
	 * @param 头信息值
	 */
	public void addHeader(String header, String value) {
		mRequestHeaders.put(header, value);
	}

	/**
	 * 取消请求
	 */
	public void cancel() {
		// 设置请求取消标记
		mIsCancel = true;
		// 如果请求对象不为空并且未取消,取消请求
		if (mRequest != null && !mRequest.isCanceled())
			mRequest.cancel();
	}

	/**
	 * 设置请求所属标签,用于批量清除请求
	 */
	void setTag(Object obj) {
		mTag = obj;
	}

	/**
	 * 是否开启模拟
	 */
	private boolean mMockEnable = false;

	/**
	 * 模拟数据
	 */
	private String mMockAssetsPath;

	/**
	 * 模拟请求延时执行时间
	 */
	private final static long MOCK_DELAY_TIME = 1500;

	/**
	 * 打开模拟请求
	 * 
	 * @param enable 是否开启模拟
	 * @param assetsPath 模拟用数据的assets资源路径
	 */
	public void enableMock(boolean enable, String assetsPath) {
		mMockEnable = enable;
		mMockAssetsPath = assetsPath;
	}

	/**
	 * 返回是否静默请求，默认否
	 * 
	 * @return
	 */
	public boolean isSilent() {
		return mSilent;
	}

	/**
	 * 设置是否静默请求
	 * 
	 * @param silent
	 */
	public void setSilent(boolean silent) {
		this.mSilent = silent;
	}

	/**
	 * 全局请求前事件
	 */
	private void preRequest() {
		if (mRequestListener != null) {
			mRequestListener.onPreRequest(this);
		}
	}

	/**
	 * 请求错误事件
	 */
	private void errorRequest(ApiResponse<T> apiResponse) {
		LogUtil.d(TAG, "server error:" + apiResponse.getErrorMsg());
		if (mRequest != null) {
			LogUtil.d(TAG, "cacheEntry:" + mRequest.getCacheEntry() + " isRefresh:" + mRequest.isRefresh());
		}
		// 全局请求错误事件
		if (mRequestListener != null) {
			mRequestListener.onError(this, apiResponse);
		}
		// 如果未取消则返回请求结果
		if (mListener != null && !mIsCancel) {
			if (mRequest != null) {
				apiResponse.setIsRefresh(mRequest.isRefresh());
			}
			mListener.onResponse(apiResponse);
		}
	}

	/**
	 * 开始模拟请求
	 * 
	 * @return
	 */
	private ApiRequest<?> startMock(final ResponseParser<T> parser) {
		if (mMockhandler == null) {
			mMockhandler = new Handler();
		}

		// 触发全局请求前监听事件
		preRequest();

		// 开始执行模拟请求
		mMockhandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				LogUtil.d(TAG, "start mock " + mMockAssetsPath);
				ApiResponse<T> apiResponse = new ApiResponse<>();
				String data = null;
				InputStream input = null;
				JsonElement je = null;

				try {
					input = mContext.getAssets().open(mMockAssetsPath);
					data = IOUtil.toString(input);
					je = EellyRequest.eellyParse(new NetworkResponse(data.getBytes(), null));
				} catch (Exception e) {
					apiResponse.setError(e);
				} finally {
					IOUtil.closeQuietly(input);
				}

				if (apiResponse.hasError()) {
					errorRequest(apiResponse);
				} else {
					parseResponse(je, parser, apiResponse);
				}
			}
		}, MOCK_DELAY_TIME);
		return this;
	}

	/**
	 * 获取请求地址
	 * 
	 * @return
	 */
	private String getRequestUrl() {
		if (mRequestParams != null) {
			if (!mShouldCache && mMethod != Method.GET) {
				return mRequestUrl;
			}
			// 检查并自动补全地址参数开始符号
			if (mRequestUrl.indexOf("?") == -1) {
				mRequestUrl += "?";
			}
			StringBuilder encodedParams = new StringBuilder(mRequestUrl);
			try {
				for (Map.Entry<String, String> entry : mRequestParams.entrySet()) {
					encodedParams.append('&');
					encodedParams.append(URLEncoder.encode(entry.getKey(), GET_ENCODE));
					encodedParams.append('=');
					encodedParams.append(URLEncoder.encode(entry.getValue(), GET_ENCODE));
				}
				return encodedParams.toString();
			} catch (UnsupportedEncodingException ex) {
				throw new RuntimeException("Encoding not supported: " + GET_ENCODE, ex);
			}
		}
		return mRequestUrl;
	}

	/**
	 * 解析服务端返回数据
	 * 
	 * @param je 服务端返回的json数据
	 * @param parser 数据解析器
	 * @param apiResponse 处理好的Api响应信息
	 */
	private void parseResponse(JsonElement je, ResponseParser<T> parser, ApiResponse<T> apiResponse) {
		LogUtil.d(TAG, "response:" + je.toString());
		if (mRequest != null) {
			LogUtil.d(TAG,
					"onResponse cacheEntry:" + mRequest.getCacheEntry() + " isCache:" + mRequest.isCanceled() + " isRefresh:" + mRequest.isRefresh());
		}
		try {
			JsonObject jsonObject = je.getAsJsonObject();
			if (parser != null) {
				if (jsonObject.has(JSON_RESULT_DATA)) {
					JsonElement dataJsonElement = jsonObject.get(JSON_RESULT_DATA);
					T tmp = dataJsonElement.isJsonObject() ? parser.parse(dataJsonElement.getAsJsonObject()) : parser.parse(dataJsonElement
							.getAsJsonArray());
					apiResponse.setResponse(tmp);
				}
				/*if (jsonObject.has(JSON_RESULT_STATUS)) {
					int statusCode = jsonObject.get(JSON_RESULT_STATUS).getAsInt();
					// 根据状态码,直接判断上传到的数据是否成功
					apiResponse.setstatusCode(statusCode);
				}*/
			}
		} catch (Exception ex) {
			apiResponse.setError(ex);
			errorRequest(apiResponse);
		}

		// 如果未取消则返回请求结果
		if (mListener != null && !mIsCancel) {
			mListener.onResponse(apiResponse);
		}
	}

	/**
	 * 开始向服务端发起请求
	 */
	public ApiRequest<?> request(final ResponseParser<T> parser) {
		// 触发全局请求前监听事件
		preRequest();

		// 获取请求地址
		String requestUrl = getRequestUrl();
		LogUtil.d(TAG, "url:" + requestUrl + "\n" + "params:" + mRequestParams);

		// 判断是否执行模拟请求
		if (mMockEnable) {
			return startMock(parser);
		}

		mRequest = new EellyRequest(mMethod, requestUrl, new Listener<JsonElement>() {

			/**
			 * 服务端正常响应返回数据
			 * 
			 * @param jsonString
			 */
			@Override
			public void onResponse(JsonElement je) {
				ApiResponse<T> apiResponse = new ApiResponse<>();
				// 缓存数据不为空,说明数据来自缓存
				Entry cacheEntry = mRequest.getCacheEntry();
				if (cacheEntry != null && !mRequest.isRefresh()) {
					apiResponse.setIsCache(true);
					mRequest.setIsRefresh(true);
				} else if (cacheEntry != null) {
					apiResponse.setIsRefresh(true);
				}
				// 解析服务端返回数据
				parseResponse(je, parser, apiResponse);
			}

		}, new ErrorListener() {

			/**
			 * 服务端访问异常
			 */
			@Override
			public void onErrorResponse(VolleyError error) {
				ApiResponse<T> apiResponse = new ApiResponse<>();
				apiResponse.setError(error);
				// 如果当前请求没设置跳过错误请求监听器,触发全局错误监听事件
				errorRequest(apiResponse);
			}

		});

		// 在低版本中如果使用POST方法提交并且参数为空时会缺少Content-length头信息,有的服务端可能会认为不正常而返回411错误,所以需要手动设置Content-length头信息
		if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) && mMethod == Method.POST && mRequestParams == null) {
			mRequestHeaders.put("Content-length", "0");
		}

		// 设置请求所属标签
		if (mTag != null) {
			mRequest.setTag(mTag);
		}

		// 设置超时和自动重试
		mRequest.setRetryPolicy(mRetryPolicy);
		// 设置请求头信息
		mRequest.setHeaders(mRequestHeaders);
		// 设置请求参数
		mRequest.setParams(mRequestParams);
		// 设置缓存开关
		mRequest.setCache(mShouldCache);
		// 设置缓存时间
		mRequest.setCacheTime(mCacheTime);
		// 设置软缓存时间
		mRequest.setSoftCacheTime(mSoftCacheTime);
		// 将请求加入Volley请求队列
		mRequestQueue.add(mRequest);

		return this;
	}

	/**
	 * 请求监听器,用于在每个请求执行前执行后调用
	 * 
	 * @author 李欣
	 */
	public static abstract class ApiRequestListener {

		/**
		 * 所有请求执行前会调用此方法
		 * 
		 * @param apiRequest 请求对象
		 */
		public void onPreRequest(ApiRequest<?> apiRequest) {

		}

		/**
		 * 所有请求执行错误后会调用此方法,可以通过复写此方法对连接超时、网络未连接等情况做统一提示处理
		 * 
		 * @param apiResponse API响应对象,包含了请求错误信息
		 */
		public void onError(ApiRequest<?> apiRequest, ApiResponse<?> apiResponse) {

		}
	}
}