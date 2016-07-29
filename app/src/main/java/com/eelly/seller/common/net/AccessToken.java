package com.eelly.seller.common.net;

import com.google.gson.annotations.SerializedName;

/**
 * @author 林钊平
 */
public class AccessToken {

	/**
	 * access_token
	 */
	@SerializedName(value = "access_token")
	private String accessToken = "";

	/**
	 * 生命周期
	 */
	@SerializedName(value = "life_time")
	private String lifeTime = "";

	/** @see #accessToken */
	public String getAccesstoken() {
		return accessToken;
	}

	/** @see #lifeTime */
	public String getLifeTime() {
		return lifeTime;
	}

}
