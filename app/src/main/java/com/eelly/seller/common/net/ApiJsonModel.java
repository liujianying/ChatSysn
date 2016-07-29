package com.eelly.seller.common.net;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 服务器返回结果模型
 * 
 * @author 李欣
 */
public class ApiJsonModel {

	/**
	 * 服务端返回的正常状态码
	 */
	private static final int STATUS_CODE_OK = 0;

	/**
	 * 服务端返回的状态码字段
	 */
	private static final String JSON_STATUSCODE = "statusCode";

	/**
	 * 服务端返回的数据字段
	 */
	private static final String JSON_DATA = "data";

	/**
	 * 服务端返回的错误信息字段
	 */
	private static final String JSON_ERROR_MESSAGE = "message";

	/**
	 * json解析类
	 */
	private static JsonParser JsonParser;

	/**
	 * 服务端返回的状态码
	 */
	private int mStatusCode = -1;

	/**
	 * 服务端返回数据
	 */
	private JsonElement mData;

	/**
	 * 错误信息
	 */
	private String mErrorMessage;

	static {
		JsonParser = new JsonParser();
	}

	/**
	 * @param resultJsonString 服务端返回的Json字符串
	 */
	public ApiJsonModel(String resultJsonString) {
		try {
			// 解析服务端返回json
			JsonObject mResultJson = JsonParser.parse(resultJsonString).getAsJsonObject();
			mStatusCode = mResultJson.get(JSON_STATUSCODE).getAsInt();
			if (mResultJson.has(JSON_DATA)) {
				mData = mResultJson.get(JSON_DATA);
			}
			if (mResultJson.has(JSON_ERROR_MESSAGE)) {
				mErrorMessage = mResultJson.getAsJsonPrimitive(JSON_ERROR_MESSAGE).getAsString();
			}
		} catch (Exception ex) {
			mErrorMessage = ex.toString();
		}
	}

	/**
	 * 服务端返回的状态码
	 * 
	 * @return
	 */
	public int getStatusCode() {
		return mStatusCode;
	}

	/**
	 * 是否有错误
	 * 
	 * @return
	 */
	public boolean hasError() {
		return mErrorMessage != null;
	}

	/**
	 * 是否接口返回错误
	 * 
	 * @return
	 */
	public boolean isApiError() {
		return getStatusCode() != STATUS_CODE_OK;
	}

	/**
	 * 获取错误信息
	 * 
	 * @return
	 */
	public String getErrorMsg() {
		return mErrorMessage;
	}

	/**
	 * 服务端返回json数据
	 * 
	 * @return
	 */
	public JsonElement getData() {
		return mData;
	}

}
