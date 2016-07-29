package com.eelly.sellerbuyer.util;

import com.eelly.framework.util.StringUtil;

/**
 * 支付密码加密对象
 * 
 * @author 李欣
 */
public class SecurityPayPassword {

	/**
	 * 支付密码密钥
	 */
	private static String KEY = ">$$#@p^P!&";

	/**
	 * 加密过的支付密码
	 */
	private String mEncodedPassword;

	/**
	 * 加密用时间戳
	 */
	private long mTimestamp;

	/**
	 * @param payPassword 要进行加密的支付密码
	 */
	public SecurityPayPassword(String payPassword) {
		this(payPassword, "001");
	}

	/**
	 * @param payPassword 要进行加密的支付密码
	 * @param version 加密版本号
	 */
	public SecurityPayPassword(String payPassword, String version) {
		this(payPassword, System.currentTimeMillis() / 1000, version); // 当前时间(秒)
	}

	/**
	 * 用同一个时间戳加密多个密码时 使用
	 * 
	 * @param payPassword 要进行加密的支付密码
	 * @param timestamp 时间戳(秒)
	 */
	public SecurityPayPassword(String payPassword, long timestamp) {
		this(payPassword, timestamp, "001");
	}

	/**
	 * 用同一个时间戳加密多个密码时 使用
	 * 
	 * @param payPassword 要进行加密的支付密码
	 * @param timestamp 时间戳(秒)
	 * @param version 加密版本号
	 */
	public SecurityPayPassword(String payPassword, long timestamp, String version) {
		// 时间戳
		mTimestamp = timestamp;
		// 加密过的支付密码
		mEncodedPassword = StringUtil.base64EncodeNoCR(version + StringUtil.base64EncodeNoCR(KEY + payPassword)
				+ StringUtil.md5Lcase(KEY + mTimestamp));
	}

	/**
	 * 获取加密用时间戳
	 * 
	 * @return
	 */
	public long getTimestamp() {
		return mTimestamp;
	}

	/**
	 * 获取加密过的支付密码
	 * 
	 * @return
	 */
	public String getEncodedPassword() {
		return mEncodedPassword;
	}

	/**
	 * 提现表单加密
	 * 
	 * @param params
	 * @return
	 */
	public static String getWithdrawEncode(String... params) {
		if (params == null || params.length == 0)
			return null;
		int l = KEY.length();
		StringBuilder sb = new StringBuilder();
		for (int i = 0, c = 0; i < params.length; i++, c++) {
			if (c >= l)
				c = 0;
			sb.append(params[i]).append(KEY.charAt(c));
		}
		return StringUtil.md5Lcase(sb.toString());
	}
}