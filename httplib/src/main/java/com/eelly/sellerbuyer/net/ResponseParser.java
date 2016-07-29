package com.eelly.sellerbuyer.net;

import com.google.gson.JsonElement;

/**
 * 返回数据解析接口
 * 
 * @author 李欣
 * @param <T>
 */
public interface ResponseParser<T> {

	public T parse(JsonElement je);

}