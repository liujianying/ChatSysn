package com.eelly.sellerbuyer.net;

/**
 * 服务端API响应监听器
 * 
 * @author 李欣
 * @param <T> 返回结果类型
 */
public interface ApiListener<T> {

	/**
	 * @param response
	 */
	public void onResponse(ApiResponse<T> response);

}