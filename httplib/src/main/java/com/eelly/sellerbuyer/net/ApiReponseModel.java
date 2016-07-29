package com.eelly.sellerbuyer.net;

import com.eelly.framework.app.NonProguard;

import java.io.Serializable;

/**
 * 服务端统一的返回数据格式<br>
 * 
 * @author 林钊平
 */
public class ApiReponseModel implements Serializable, NonProguard {

	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * 返回的状态码<br>
	 * 200 为正常返回，其他则为服务端返回错误
	 */
	private int status = 0;

	/**
	 * 服务端错误时返回的错误信息
	 */
	private String info = "";

	/**
	 * 返回的数据
	 */
	private Retval retval;

	/** @see #status */
	public int getStatus() {
		return status;
	}

	/** @see #status */
	public void setStatus(int status) {
		this.status = status;
	}

	/** @see #info */
	public String getInfo() {
		return info;
	}

	/** @see #info */
	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * {@link Retval#data}
	 */
	public String getData() {
		return retval == null ? "" : retval.data;
	}

	/**
	 * {@link Retval#signature}
	 */
	public String getSignature() {
		return retval == null ? "" : retval.signature;
	}

	/**
	 * {@link Retval#data}
	 */
	public void setData(String data) {
		if (retval != null) {
			retval.data = data;
		}
	}

	public static class Retval implements Serializable, NonProguard {

		/**  */
		private static final long serialVersionUID = 1L;

		/**
		 * 加密具体的数据<br>
		 * 返回是加密数据，加密算法为3DES，模式为CBC模式
		 */
		private String data = "";

		/**
		 * 签名信息
		 */
		private String signature = "";
	}
}
