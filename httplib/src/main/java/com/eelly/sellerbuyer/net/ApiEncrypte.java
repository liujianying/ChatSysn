package com.eelly.sellerbuyer.net;

import com.eelly.framework.util.StringUtil;

/**
 * 传送数据的加密
 * 
 * @author 林钊平
 */
public class ApiEncrypte {

	/**
	 * 上传的密文字符串<br>
	 * 8位加密向量+加密后的密文+6位随机数，组成的字符串的 反转字符串
	 * 
	 * @param iv
	 * @param dataStr
	 * @return
	 */
	public static String postParams(String iv, String dataStr) {
		String str = iv + dataStr + StringUtil.getRandomString(6);
		return StringUtil.reverseString(str);
	}
}
