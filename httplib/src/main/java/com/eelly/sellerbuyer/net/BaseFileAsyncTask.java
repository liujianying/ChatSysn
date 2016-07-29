package com.eelly.sellerbuyer.net;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 文件上传下载基类
 * 
 * @author 李欣
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 */
public abstract class BaseFileAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	/**
	 * Http编码
	 */
	protected final String ENCODE = "utf8";

	/**
	 * 请求方式
	 */
	public enum METHOD {
		GET, POST
	};

	/**
	 * Http请求方式
	 */
	private METHOD mMethod = METHOD.GET;

	/**
	 * Http头信息
	 */
	private HashMap<String, String> mHeaders;

	/**
	 * Http请求参数
	 */
	private HashMap<String, Object> mParams;

	/**
	 * 连接超时时间(ms)
	 */
	private int mConnectTimeout = 10000;

	/**
	 * 读取数据超时时间(ms)
	 */
	private int mReadTimeout = 10000;

	/**
	 * Http请求方式
	 * 
	 * @param method
	 */
	protected METHOD getMethod() {
		return mMethod;
	}

	/**
	 * 设置Http请求方式
	 * 
	 * @param method
	 */
	public void setMethod(METHOD method) {
		mMethod = method;
	}

	/**
	 * 获取连接超时时间(ms)
	 * 
	 * @return
	 */
	protected int getConnectTimeout() {
		return mConnectTimeout;
	}

	/**
	 * 设置连接超时时间(ms)
	 * 
	 * @param connectTimeout 超时时间(ms)
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.mConnectTimeout = connectTimeout;
	}

	/**
	 * 获取读取数据超时时间(ms)
	 * 
	 * @return
	 */
	public int getReadTimeout() {
		return mReadTimeout;
	}

	/**
	 * 设置读取数据超时时间(ms)
	 * 
	 * @param connectTimeout 超时时间(ms)
	 */
	public void setReadTimeout(int readTimeout) {
		this.mReadTimeout = readTimeout;
	}

	/**
	 * 添加Http头信息
	 * 
	 * @param header 头名称
	 * @param value 值
	 */
	public void addHeader(String header, String value) {
		if (mHeaders == null) {
			mHeaders = new HashMap<>();
		}
		mHeaders.put(header, value);
	}

	/**
	 * @see #addParam(String, String)
	 * @param param
	 * @param value
	 */
	public void addParam(String param, boolean value) {
		addParam(param, String.valueOf(value));
	}

	/**
	 * @see #addParam(String, String)
	 * @param param
	 * @param value
	 */
	public void addParam(String param, int value) {
		addParam(param, String.valueOf(value));
	}

	/**
	 * @see #addParam(String, String)
	 * @param param
	 * @param value
	 */
	public void addParam(String param, long value) {
		addParam(param, String.valueOf(value));
	}

	/**
	 * @see #addParam(String, String)
	 * @param param
	 * @param value
	 */
	public void addParam(String param, float value) {
		addParam(param, String.valueOf(value));
	}

	/**
	 * @see #addParam(String, String)
	 * @param param
	 * @param value
	 */
	public void addParam(String param, double value) {
		addParam(param, String.valueOf(value));
	}
	
	/**
	 * 添加Http请求参数
	 * 
	 * @param param 参数名
	 * @param value 值
	 */
	public void addParam(String param, String value) {
		if (mParams == null) {
			mParams = new HashMap<>();
		}
		Object obj = mParams.get(param);
		if(obj != null){// 如果参数字段已存在
			if(obj instanceof String){// 如果值为字符对象则改为数组
				ArrayList<String> values = new ArrayList<>();
				values.add(String.valueOf(obj));
				values.add(value);
				mParams.put(param, values);
			}else{// 否则加入参数数组
				ArrayList<String> values = (ArrayList<String>)obj;
				values.add(value);
			}
		}else{
			mParams.put(param, value);
		}
	}

	/**
	 * 设置的头信息
	 * 
	 * @return
	 */
	protected HashMap<String, String> getHeaders() {
		return mHeaders;
	}

	/**
	 * 设置的提交参数
	 * 
	 * @return
	 */
	protected HashMap<String, Object> getParams() {
		return mParams;
	}
}
