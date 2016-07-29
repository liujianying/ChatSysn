package com.eelly.seller.common.net;

import java.io.Serializable;

/**
 * 获取AccessToken的请求参数
 * 
 * @author 林钊平
 */
public class GetAccessToken implements Serializable {

	/***/
	private static final long serialVersionUID = 1L;

	private String appId = "";

	private String appSecret = "";
	private String appSecret2 = "";

	public GetAccessToken(String appId, String appSecret) {
		this.appId = appId;
		this.appSecret = appSecret;
	}

	/** @see #appId */
	public String getAppId() {
		return appId;
	}

	/** @see #appSecret */
	public String getAppSecret() {
		return appSecret;
	}

	public String getAppSecret2() {
		return appSecret2;
	}
}