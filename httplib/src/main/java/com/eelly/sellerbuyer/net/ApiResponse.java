package com.eelly.sellerbuyer.net;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * 服务端API响应类
 * 
 * @author 李欣
 * @param <T>
 */
public class ApiResponse<T> {

	private T mData;

	private Exception mError;

	private JsonElement mJsonElement;

	/**
	 * 缓存响应标示
	 */
	private boolean mIsCache;

	/**
	 * 刷新响应标示
	 */
	private boolean mIsRefresh;

	private int statusCode = -1;

	/**
	 * 设置返回数据
	 * 
	 * @param t
	 */
	void setResponse(T t) {
		mData = t;
	}

	/**
	 * 获取对象数据
	 * 
	 * @return
	 */
	public T get() {
		return mData;
	}

	/**
	 * 获取JsonElement
	 * 
	 * @return
	 */
	public JsonElement getJson() {
		return mJsonElement;
	}

	/**
	 * 设置缓存标示
	 */
	void setIsCache(boolean isCache) {
		mIsCache = isCache;
	}

	/**
	 * 当前的响应是否来自缓存
	 * 
	 * @return
	 */
	public boolean isCache() {
		return mIsCache;
	}

	/**
	 * 设置缓存标示
	 */
	void setIsRefresh(boolean isRefresh) {
		mIsRefresh = isRefresh;
	}

	/**
	 * 当前的响应是否来自缓存刷新请求
	 * 
	 * @return
	 */
	public boolean isRefresh() {
		return mIsRefresh;
	}

	/**
	 * 设置错误异常
	 * 
	 * @param error
	 */
	void setError(Exception error) {
		mError = error;
	}

	/**
	 * 是否出现请求错误
	 * 
	 * @return
	 */
	public boolean hasError() {
		return mError != null;
	}

	/**
	 * 是否超时错误
	 * 
	 * @return
	 */
	public boolean isTimeoutError() {
		return (hasError() && mError instanceof TimeoutError) ? true : false;
	}

	/**
	 * 是否网络错误
	 * 
	 * @return
	 */
	public boolean isNetworkError() {
		return (hasError() && mError instanceof NetworkError) ? true : false;
	}

	/**
	 * 是否服务端异常错误
	 * 
	 * @return
	 */
	public boolean isServerError() {
		return (hasError() && mError instanceof ServerError) ? true : false;
	}

	/**
	 * 是否服务端返回数据解析错误
	 * 
	 * @return
	 */
	public boolean isParseError() {
		return (hasError() && (mError instanceof ParseError || mError instanceof JsonParseException)) ? true : false;
	}

	/**
	 * 是否服务端正常错误
	 * 
	 * @return
	 */
	public boolean isApiError() {
		return (hasError() && mError instanceof ApiError) ? true : false;
	}

	/**
	 * 网络未连接错误
	 * 
	 * @return
	 */
	public boolean isNoConnectionError() {
		return (hasError() && mError instanceof NoConnectionError) ? true : false;
	}

	/**
	 * 获取错误对象
	 * 
	 * @return
	 */
	public <T> T getError() {
		return (T) mError;
	}

	/**
	 * 获取错误信息
	 * 
	 * @return
	 */
	public String getErrorMsg() {
		return hasError() ? mError.toString() : null;
	}

	/**
	 * @param statusCode2
	 */
	public void setstatusCode(int statusCode2) {
		this.statusCode = statusCode2;

	}

	/**
	 * @param statusCode2
	 */
	public int getstatusCode() {
		return statusCode;
	}
}