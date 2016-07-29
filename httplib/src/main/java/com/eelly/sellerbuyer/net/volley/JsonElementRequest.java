package com.eelly.sellerbuyer.net.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.JsonElement;

/**
 * JsonElement对象请求
 * 
 * @author 李欣
 * 
 */
abstract public class JsonElementRequest extends BaseRequest<JsonElement> {

	private Listener<JsonElement> mListener;

	public JsonElementRequest(int method, String url, Listener<JsonElement> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
	}

	/**
	 * 服务端返回数据解析方法,由子类实现
	 * @param response
	 * @return
	 */
	abstract protected Response<JsonElement> parse(NetworkResponse response);
	
	@Override
	protected Response<JsonElement> parseNetworkResponse(NetworkResponse response) {
		return parse(response);
	}

	@Override
	protected void deliverResponse(JsonElement response) {
		mListener.onResponse(response);
	}

}
