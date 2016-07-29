package com.eelly.sellerbuyer.net.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.eelly.sellerbuyer.net.ApiError;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 新版接口系统<br>
 * 衣联网服务端接口请求实现类
 * 
 * @author 林钊平
 */
public class NewEellyRequest extends JsonElementRequest {

	public NewEellyRequest(int method, String url, Listener<JsonElement> listener, ErrorListener errorListener) {
		super(method, url, listener, errorListener);
	}

	/**
	 * 衣联服务端接口数据解析方法
	 * 
	 * @param response
	 * @return
	 * @throws ApiError
	 * @throws Exception
	 */
	public static JsonElement eellyParse(NetworkResponse response) throws ApiError, Exception {
		String jsonString = response.headers == null ? new String(response.data) : new String(response.data,
				HttpHeaderParser.parseCharset(response.headers));
		JsonElement je = new JsonParser().parse(jsonString);
		JsonObject jsonObject = je.getAsJsonObject();

		int statusCode = jsonObject.getAsJsonPrimitive("status").getAsInt();
		if (statusCode != 200) {// 检查服务端返回的状态码,如果不为200则返回错误
			String info = jsonObject.getAsJsonPrimitive("info").getAsString();
			throw new ApiError(statusCode, info);
		}
		return je;
	}

	@Override
	protected Response<JsonElement> parse(NetworkResponse response) {
		try {
			return Response.success(eellyParse(response), parseCacheHeaders(response));
		} catch (ApiError e) {
			return Response.error(e);
		} catch (Exception e) {
			return Response.error(new ParseError(e));
		}
	}

}
