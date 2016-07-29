package com.eelly.sellerbuyer.net;

import com.android.volley.VolleyError;

/**
 * 服务端正常返回错误
 * 
 * @author 李欣
 */
public class ApiError extends VolleyError {

	private static final long serialVersionUID = 2448213936630662771L;

	/**
	 * 错误码
	 */
	private int mStatusCode;

	/**
	 * 错误信息
	 */
	private String mMessage;

	public ApiError(int statusCode, String message) {
		super(message);
		mStatusCode = statusCode;
		mMessage = message;
	}

	/**
	 * 获取错误码
	 * 
	 * @return
	 */
	public int getStatusCode() {
		return mStatusCode;
	}

	/**
	 * 获取错误信息
	 * 
	 * @return
	 */
	public String getMessage() {
		return mMessage;
	}

	/**
	 * 没有设置支付密码
	 * @return
	 */
	public boolean noSetPayPassword() {
		return mStatusCode == 220002;
	}

	@Override
	public String toString() {
		return mMessage;
	}

}
