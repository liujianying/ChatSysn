package com.eelly.seller.common.net;

import com.eelly.sellerbuyer.net.BaseNetConfig;

/**
 * 网络配置类
 * 
 * @author 苏腾
 */
public class NetConfig extends BaseNetConfig {

	/**
	 * 初始化服务端连接环境
	 */
	public static void init() {
		defaultNetEnvironment = NET_ENVIRONMENT_TEST;
	}
}
