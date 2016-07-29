package com.eelly.sellerbuyer.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Map;

/**
 * Volley请求基础类，主要加入了{@link BaseRequest#setParams(Map)}设置参数方法,解决Volley本身无法方便的设置提交参数问题
 * 
 * @author 李欣
 * @param <T>
 */
public abstract class BaseRequest<T> extends Request<T> {

	private static final String TAG = "BaseRequest";

	/**
	 * Http请求头信息
	 */
	private Map<String, String> mHeaders;

	/**
	 * Http请求参数
	 */
	private Map<String, String> mParams;

	/**
	 * 缓存时间(单位:毫秒)
	 */
	private long mCacheTime = 0L;

	/**
	 * 软缓存时间(单位:毫秒)
	 */
	private long mSoftCacheTime = 0L;
	
	/**
	 * 缓存刷新请求标示
	 */
	private boolean mIsRefresh = false;
	
	public BaseRequest(int method, String url, ErrorListener listener) {
		super(method, url, listener);
	}

	/**
	 * 设置Http请求参数
	 * 
	 * @param params
	 */
	public void setParams(Map<String, String> params) {
		mParams = params;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mParams;
	}

	/**
	 * 设置Http请求头信息
	 */
	public void setHeaders(Map<String, String> headers) {
		mHeaders = headers;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeaders;
	}

	/**
	 * 设置打开或关闭缓存
	 */
	public final void setCache(boolean cache){
		setShouldCache(cache);
	}
	
	/**
	 * 设置请求的缓存时间
	 * @param cacheTime 缓存时间
	 */
	public final void setCacheTime(long cacheTime) {
		mCacheTime = cacheTime;
	}
	
	/**
	 * 设置软缓存时间,当此缓存时间过期时依然会返回cache数据，同时会发送网络请求更新数据
	 * @param cacheTime 缓存时间
	 */
	public final void setSoftCacheTime(long cacheTime) {
		mSoftCacheTime = cacheTime;
	}
    
	/**
	 * 设置缓存刷新请求标示(本方法为框架内部使用,普通使用者不要使用本方法)
	 */
	public final void setIsRefresh(boolean isRefresh){
		mIsRefresh = isRefresh;
	}
	
	/**
	 * 当前请求是否缓存刷新请求(本方法为框架内部使用,普通使用者不要使用本方法)
	 * @return
	 */
	public final boolean isRefresh(){
		return mIsRefresh;
	}
    
	
	/**
	 * 解析Http头信息的缓存设置
	 * 
	 * @return
	 */
	protected Entry parseCacheHeaders(NetworkResponse response) {
		Entry entry = null;
		if (shouldCache()) {// 检查缓存是否打开
			// 根据服务端返回的Http信息生成缓存
			entry = HttpHeaderParser.parseCacheHeaders(response);
//			LogUtil.d(TAG, "开始缓存...");
//			LogUtil.d(TAG, "entry:"+entry);
//			LogUtil.d(TAG, "url:" + getUrl());
//			for (java.util.Map.Entry<String, String> one : response.headers.entrySet()) {
//				LogUtil.d(TAG, "header:" + one.getKey() + " = " + one.getValue());
//			}
//			LogUtil.d(TAG, "data:" + response.data.length + " notModified:" + response.notModified);
			if (entry == null) {// 服务端没有设置或关闭了缓存
				long now = System.currentTimeMillis();
				// 创建缓存对象
				entry = new Cache.Entry();
				//缓存数据
				entry.data = response.data;
				entry.etag = null;
				// 软过期时间
				entry.softTtl = now + mSoftCacheTime;
				// 过期时间
				entry.ttl = now + mCacheTime;
				// 服务端时间
				entry.serverDate = 0;
			}
//			LogUtil.d(TAG, "etag:" + entry.etag);
//			LogUtil.d(TAG, "serverDate:" + entry.serverDate);
//			LogUtil.d(TAG, "softTtl:" + entry.softTtl);
//			LogUtil.d(TAG, "ttl:" + entry.ttl);
//			LogUtil.d(TAG, "refreshNeeded:" + entry.refreshNeeded());
		}
		return entry;
	}
}
