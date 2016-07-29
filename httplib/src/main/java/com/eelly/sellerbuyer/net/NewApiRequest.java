package com.eelly.sellerbuyer.net;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.android.volley.Cache.Entry;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.eelly.framework.util.DeviceUtil;
import com.eelly.framework.util.EncryptUtil;
import com.eelly.framework.util.IOUtil;
import com.eelly.framework.util.LogUtil;
import com.eelly.framework.util.StringUtil;
import com.eelly.sellerbuyer.net.volley.JsonElementRequest;
import com.eelly.sellerbuyer.net.volley.NewEellyRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 服务端API请求类
 *
 * @author 林钊平
 */
public class NewApiRequest<T> {

    private static final String TAG = "ApiRequest";

    /**
     * Http请求方式
     *
     * @author 李欣
     */
    public static interface Method {

        public static final int GET = com.android.volley.Request.Method.GET;

        public static final int POST = com.android.volley.Request.Method.POST;

        public static final int DEPRECATED_GET_OR_POST = com.android.volley.Request.Method.DEPRECATED_GET_OR_POST;
    }

    /**
     * 提交数据的字符编码
     */
    private static final String GET_ENCODE = "utf8";

    /**
     * 请求地址
     */
    private String mRequestUrl;

    /**
     * 请求方法
     */
    private int mMethod;

    /**
     * 请求所需参数args
     */
    private HashMap<String, String> mArgsParams;

    /**
     * Http请求头信息
     */
    private HashMap<String, String> mRequestHeaders;

    /**
     * 新版接口系统的服务参数
     */
    private HashMap<String, String> mServerParams;

    private ApiListener<T> mListener;

    /**
     * 请求监听器
     */
    private ApiRequestListener mRequestListener;

    /**
     * Volley框架的请求队列
     */
    private RequestQueue mRequestQueue;

    /**
     * Volley请求对象
     */
    private JsonElementRequest mRequest;

    /**
     * 是否对请求结果缓存
     */
    private boolean mShouldCache = false;

    /**
     * 缓存时间(单位:毫秒)
     */
    private long mCacheTime = 0L;

    /**
     * 软缓存时间(单位:毫秒)
     */
    private long mSoftCacheTime = 0L;

    /**
     * 请求是否已取消
     */
    private volatile boolean mIsCancel = false;

    /**
     * 请求是否静默，即没有错误提示
     */
    private boolean mSilent = false;

    /**
     * Volley超时重试实例, 默认20秒超时
     */
    private RetryPolicy mRetryPolicy = new DefaultRetryPolicy(20000, 0, 0);

    /**
     * 请求所属标签
     */
    private Object mTag;

    /**
     * 当前上下文
     */
    private Context mContext;

    /**
     * 服务端返回的数据字段
     */
    private static final String JSON_RESULT_RETVAL = "retval";

    /**
     * 服务端返回的数据字段
     */
    private static final String JSON_RESULT_STATUS = "statusCode";

    /**
     * 3Des加密用的向量
     */
    private String mEncryptIv = "";

    /**
     * 3Des加密用的密钥
     */
    private String mEncryptKey = "";

    /**
     * 执行模拟请求的Handler
     */
    private Handler mMockhandler;
    private static Gson mGson;

    static {
        HttpURLConnection.setDefaultAllowUserInteraction(false);
        mGson = new Gson();
    }

    NewApiRequest(int method, String url, RequestQueue requestQueue, ApiListener<T> listener, ApiRequestListener apiRequestListener, Context context) {
        mMethod = method;
        mRequestUrl = url;
        mRequestQueue = requestQueue;
        mListener = listener;
        mRequestListener = apiRequestListener;
        mRequestHeaders = new HashMap<>();
        mServerParams = new HashMap<>();
        mContext = context;
        mEncryptIv = StringUtil.getRandomString(8);
        mServerParams.put("device_number", DeviceUtil.getIMEI(context));
    }

    /**
     * 设置超时时间
     *
     * @param timeout 毫秒
     */
    public void setTimeOut(int timeout) {
        mRetryPolicy = new DefaultRetryPolicy(timeout, 1, 0);
    }

    /**
     * 设置超时时间
     *
     * @param timeout 毫秒
     * @param retry   自动重试次数
     */
    public void setTimeOut(int timeout, int retry) {
        mRetryPolicy = new DefaultRetryPolicy(timeout, retry, 0);
    }

    /**
     * 设置打开关闭缓存
     *
     * @param shouldCache
     */
    public void setCache(boolean shouldCache) {
        mShouldCache = shouldCache;
    }

    /**
     * 设置缓存时间
     *
     * @param cacheTime
     */
    public void setCacheTime(long cacheTime) {
        mCacheTime = cacheTime;
    }

    /**
     * 设置软缓存时间
     *
     * @param cacheTime
     */
    public void setSoftCacheTime(long cacheTime) {
        mSoftCacheTime = cacheTime;
    }

    /**
     * @param param
     * @param value
     * @see #addParam(String, String)
     */
    @Deprecated
    public void addParam(String param, boolean value) {
        addParam(param, String.valueOf(value));
    }

    /**
     * @param param
     * @param value
     * @see #addParam(String, String)
     */
    @Deprecated
    public void addParam(String param, int value) {
        addParam(param, String.valueOf(value));
    }

    /**
     * @param param
     * @param value
     * @see #addParam(String, String)
     */
    @Deprecated
    public void addParam(String param, long value) {
        addParam(param, String.valueOf(value));
    }

    /**
     * @param param
     * @param value
     * @see #addParam(String, String)
     */
    @Deprecated
    public void addParam(String param, float value) {
        addParam(param, String.valueOf(value));
    }

    /**
     * @param param
     * @param value
     * @see #addParam(String, String)
     */
    @Deprecated
    public void addParam(String param, double value) {
        addParam(param, String.valueOf(value));
    }

    /**
     * 方法已过期，请使用{@link #setArgs(String)}传递参数，传递的字符串为json字符串<br>
     * 添加请求参数
     *
     * @param param
     * @param value
     */
    @Deprecated
    public void addParam(String param, String value) {
        if (mArgsParams == null)
            mArgsParams = new LinkedHashMap<>();
        mArgsParams.put(param, value == null ? "" : value);
    }

    /**
     * 服务系统
     *
     * @param app
     */
    public void setApp(String app) {
        mServerParams.put("app", app);
    }

    /**
     * 服务名
     *
     * @param serverName
     */
    public void setServerName(String serverName) {
        mServerParams.put("service_name", serverName);
    }

    /**
     * 接口名
     *
     * @param method
     */
    public void setMethod(String method) {
        mServerParams.put("method", method);
    }

    /**
     * 时间戳
     *
     * @param time
     */
    public void setTime(String time) {
        mServerParams.put("time", time);
    }

    /**
     * 服务的版本号
     *
     * @param version
     */
    public void setServerVersion(String version) {
        mServerParams.put("version", version);
    }

    /**
     * 服务的客户端
     *
     * @param client
     */
    public void setServerClient(String client) {
        mServerParams.put("client", client);
    }

    /**
     * 客户端版本号
     *
     * @param clientVersion
     */
    public void setClientVerion(String clientVersion) {
        mServerParams.put("client_version", clientVersion);
    }

    /**
     * 客户端名字
     *
     * @param clientName
     */
    public void setClientName(String clientName) {
        mServerParams.put("client_name", clientName);
    }

    /**
     * 设置App客户端的类型
     *
     * @param clientUserType
     */
    public void setClientUserType(String clientUserType) {
        mServerParams.put("client_user_type", clientUserType);
    }

    /**
     * 需要登录的服务传递的登陆token
     *
     * @param token
     */
    public void setUserToken(String token) {
        mServerParams.put("user_login_token", token);
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        mServerParams.put("user_id", userId);
    }

    /**
     * 请求参数
     *
     * @param args 把请求的model对象转为json
     */
    public void setArgs(String args) {
        mServerParams.put("args", args);
    }

    /**
     * 添加请求头信息
     *
     * @param header 头信息名
     * @param value  头信息值
     */
    public void addHeader(String header, String value) {
        mRequestHeaders.put(header, value);
    }

    public void setEncryptKey(String key) {
        this.mEncryptKey = key;
    }

    public String getEncryptKey() {
        return mEncryptKey;
    }

    /**
     * 取消请求
     */
    public void cancel() {
        // 设置请求取消标记
        mIsCancel = true;
        // 如果请求对象不为空并且未取消,取消请求
        if (mRequest != null && !mRequest.isCanceled())
            mRequest.cancel();
    }

    /**
     * 设置请求所属标签,用于批量清除请求
     */
    void setTag(Object obj) {
        mTag = obj;
    }

    /**
     * 是否开启模拟
     */
    private boolean mMockEnable = false;

    /**
     * 模拟数据
     */
    private String mMockAssetsPath;

    /**
     * 模拟请求延时执行时间
     */
    private final static long MOCK_DELAY_TIME = 1500;

    /**
     * 打开模拟请求
     *
     * @param enable     是否开启模拟
     * @param assetsPath 模拟用数据的assets资源路径
     */
    public void enableMock(boolean enable, String assetsPath) {
        mMockEnable = enable;
        mMockAssetsPath = assetsPath;
    }

    /**
     * 返回是否静默请求，默认否
     *
     * @return
     */
    public boolean isSilent() {
        return mSilent;
    }

    /**
     * 设置是否静默请求
     *
     * @param silent
     */
    public void setSilent(boolean silent) {
        this.mSilent = silent;
    }

    /**
     * 全局请求前事件
     */
    private void preRequest() {
        if (mRequestListener != null) {
            mRequestListener.onPreRequest(this);
        }
    }

    /**
     * 获取加密后的参数字符串,组成最终的参数键值对
     *
     * @return
     * @throws Exception
     */
    private HashMap<String, String> getEncryptParams() {
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            Gson gson = new Gson();
            String dataStr = gson.toJson(mServerParams);
            String encryptStr = EncryptUtil.des3EncodeCBC(mEncryptKey, mEncryptIv, dataStr);
            String data = ApiEncrypte.postParams(mEncryptIv, encryptStr);
            map.put("data", data);
        } catch (Exception e) {
            LogUtil.d(TAG, "参数有误");
        }
        return map;
    }

    /**
     * 请求错误事件
     */
    private void errorRequest(ApiResponse<T> apiResponse) {
        LogUtil.d(TAG, "server error:" + apiResponse.getErrorMsg());
        if (mRequest != null) {
            LogUtil.d(TAG, "cacheEntry:" + mRequest.getCacheEntry() + " isRefresh:" + mRequest.isRefresh());
        }
        // 全局请求错误事件
        if (mRequestListener != null) {
            mRequestListener.onError(this, apiResponse);
        }
        // 如果未取消则返回请求结果
        if (mListener != null && !mIsCancel) {
            if (mRequest != null) {
                apiResponse.setIsRefresh(mRequest.isRefresh());
            }
            mListener.onResponse(apiResponse);
        }
    }

    /**
     * 开始模拟请求
     *
     * @return
     */
    private NewApiRequest<?> startMock(final ResponseParser<T> parser) {
        if (mMockhandler == null) {
            mMockhandler = new Handler();
        }

        // 触发全局请求前监听事件
        preRequest();

        // 开始执行模拟请求
        mMockhandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                LogUtil.d(TAG, "start mock " + mMockAssetsPath);
                ApiResponse<T> apiResponse = new ApiResponse<>();
                String data = null;
                InputStream input = null;
                JsonElement je = null;

                try {
                    input = mContext.getAssets().open(mMockAssetsPath);
                    data = IOUtil.toString(input);
                    je = NewEellyRequest.eellyParse(new NetworkResponse(data.getBytes(), null));
                } catch (Exception e) {
                    apiResponse.setError(e);
                } finally {
                    IOUtil.closeQuietly(input);
                }

                if (apiResponse.hasError()) {
                    errorRequest(apiResponse);
                } else {
                    parseResponse(je, parser, apiResponse);
                }
            }
        }, MOCK_DELAY_TIME);
        return this;
    }

    /**
     * 获取请求地址
     *
     * @return
     */
    private String getRequestUrl() {
        if (mArgsParams != null) {
            if (!mShouldCache && mMethod != Method.GET) {
                return mRequestUrl;
            }
            // 检查并自动补全地址参数开始符号
            if (mRequestUrl.indexOf("?") == -1) {
                mRequestUrl += "?";
            }
            StringBuilder encodedParams = new StringBuilder(mRequestUrl);
            try {
                for (Map.Entry<String, String> entry : mArgsParams.entrySet()) {
                    encodedParams.append('&');
                    encodedParams.append(URLEncoder.encode(entry.getKey(), GET_ENCODE));
                    encodedParams.append('=');
                    encodedParams.append(URLEncoder.encode(entry.getValue(), GET_ENCODE));
                }
                return encodedParams.toString();
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException("Encoding not supported: " + GET_ENCODE, ex);
            }
        }
        return mRequestUrl;
    }

    /**
     * 解析服务端返回数据
     *
     * @param je          服务端返回的json数据
     * @param parser      数据解析器
     * @param apiResponse 处理好的Api响应信息
     */
    private void parseResponse(JsonElement je, ResponseParser<T> parser, ApiResponse<T> apiResponse) {
        LogUtil.d(TAG, "response:" + je.toString());
        if (mRequest != null) {
            LogUtil.d(TAG,
                    "onResponse cacheEntry:" + mRequest.getCacheEntry() + " isCache:" + mRequest.isCanceled() + " isRefresh:" + mRequest.isRefresh());
        }
        Gson gson = new Gson();

        try {
            // JsonObject jsonObject = je.getAsJsonObject();
            // if (parser != null) {
            // if (jsonObject.has(JSON_RESULT_RETVAL)) {
            // JsonElement dataJsonElement = jsonObject.get(JSON_RESULT_RETVAL);
            // T tmp = dataJsonElement.isJsonObject() ? parser.parse(dataJsonElement.getAsJsonObject()) : parser.parse(dataJsonElement
            // .getAsJsonArray());
            // apiResponse.setResponse(tmp);
            // }
            // }
            ApiReponseModel responseModel = gson.fromJson(je, ApiReponseModel.class);
            String dataStr = responseModel.getData();
            String data = dataStr;
            if (!mMockEnable) {
                data = EncryptUtil.des3DecodeCBC(mEncryptKey, mEncryptIv, dataStr);
            }
            LogUtil.e(TAG, "response:" + data);
            JsonElement dataJsonElement = new JsonParser().parse(data);
            T tmp = dataJsonElement.isJsonObject() ? parser.parse(dataJsonElement.getAsJsonObject()) : parser.parse(dataJsonElement.getAsJsonArray());
            apiResponse.setResponse(tmp);
        } catch (Exception ex) {
            apiResponse.setError(ex);
            errorRequest(apiResponse);
        }

        // 如果未取消则返回请求结果
        if (mListener != null && !mIsCancel) {
            mListener.onResponse(apiResponse);
        }
    }

    /**
     * 开始向服务端发起请求
     */
    public NewApiRequest<?> request(final ResponseParser<T> parser) {
        // 触发全局请求前监听事件
        preRequest();

        // 获取请求地址
        String requestUrl = getRequestUrl();
        LogUtil.i(TAG, "url:" + requestUrl + "\n" + "ServerParmas:" + mGson.toJson(mServerParams) + "\n" + "httpParams:" + getEncryptParams());

        // 判断是否执行模拟请求
        if (mMockEnable) {
            return startMock(parser);
        }

        mRequest = new NewEellyRequest(mMethod, requestUrl, new Listener<JsonElement>() {

            /**
             * 服务端正常响应返回数据
             *
             * @param je
             */
            @Override
            public void onResponse(JsonElement je) {
                ApiResponse<T> apiResponse = new ApiResponse<>();
                // 缓存数据不为空,说明数据来自缓存
                Entry cacheEntry = mRequest.getCacheEntry();
                if (cacheEntry != null && !mRequest.isRefresh()) {
                    apiResponse.setIsCache(true);
                    mRequest.setIsRefresh(true);
                } else if (cacheEntry != null) {
                    apiResponse.setIsRefresh(true);
                }
                // 解析服务端返回数据
                parseResponse(je, parser, apiResponse);
            }

        }, new ErrorListener() {

            /**
             * 服务端访问异常
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                ApiResponse<T> apiResponse = new ApiResponse<>();
                apiResponse.setError(error);
                // 如果当前请求没设置跳过错误请求监听器,触发全局错误监听事件
                errorRequest(apiResponse);
            }

        });

        // 在低版本中如果使用POST方法提交并且参数为空时会缺少Content-length头信息,有的服务端可能会认为不正常而返回411错误,所以需要手动设置Content-length头信息
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) && mMethod == Method.POST && mArgsParams == null) {
            mRequestHeaders.put("Content-length", "0");
        }

        // 设置请求所属标签
        if (mTag != null) {
            mRequest.setTag(mTag);
        }

        // 设置超时和自动重试
        mRequest.setRetryPolicy(mRetryPolicy);
        // 设置请求头信息
        mRequest.setHeaders(mRequestHeaders);
        // 设置请求参数
        mRequest.setParams(getEncryptParams());
        // 设置缓存开关
        mRequest.setCache(mShouldCache);
        // 设置缓存时间
        mRequest.setCacheTime(mCacheTime);
        // 设置软缓存时间
        mRequest.setSoftCacheTime(mSoftCacheTime);
        // 将请求加入Volley请求队列
        mRequestQueue.add(mRequest);

        return this;
    }

    /**
     * 请求监听器,用于在每个请求执行前执行后调用
     *
     * @author 李欣
     */
    public static abstract class ApiRequestListener {

        /**
         * 所有请求执行前会调用此方法
         *
         * @param apiRequest 请求对象
         */
        public void onPreRequest(NewApiRequest<?> apiRequest) {

        }

        /**
         * 所有请求执行错误后会调用此方法,可以通过复写此方法对连接超时、网络未连接等情况做统一提示处理
         *
         * @param apiResponse API响应对象,包含了请求错误信息
         */
        public void onError(NewApiRequest<?> apiRequest, ApiResponse<?> apiResponse) {

        }
    }

}