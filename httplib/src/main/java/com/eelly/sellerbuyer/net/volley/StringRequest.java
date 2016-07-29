package com.eelly.sellerbuyer.net.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

/**
 * 字符串请求
 * @author 李欣
 *
 */
public class StringRequest extends BaseRequest<String> {
	private Listener<String> mListener;
	
	public StringRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        
        return Response.success(parsed, parseCacheHeaders(response));
	}

	@Override
	protected void deliverResponse(String response) {
		mListener.onResponse(response);
	}

}
