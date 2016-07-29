package com.eelly.sellerbuyer.net.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

/**
 * json数组请求
 * 
 * @author 李欣
 * 
 */
public class JsonArrayRequest extends BaseRequest<JSONArray> {

	private Listener<JSONArray> mListener;

	public JsonArrayRequest(int method, String url, Listener<JSONArray> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
	}

	@Override
	protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(new JSONArray(jsonString), parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

	@Override
	protected void deliverResponse(JSONArray response) {
		mListener.onResponse(response);
	}

}
