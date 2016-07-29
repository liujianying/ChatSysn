package com.eelly.sellerbuyer.net;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Network;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.eelly.framework.app.ActivityListener;
import com.eelly.framework.app.ActivityListener.ActivityListenerManager;
import com.eelly.framework.util.FileUtils;
import com.eelly.framework.util.LogUtil;
import com.eelly.sellerbuyer.net.ApiRequest.ApiRequestListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.File;
import java.lang.reflect.Type;

/**
 * 服务端Api类,负责所有与服务端的通讯 注意:本类和当前上下文有关 可在退出当前上下文时通过{@link ApiRequest#cancelAll()} 方法取消所有当前的API请求 或通过返回的 {@link ApiRequest#cancel()}方法取消单个API请求
 *
 * @author 李欣
 */
public abstract class EellyServerApi {

    /**
     * Http请求队列
     */
    private static RequestQueue mRequestQueue;

    /**
     * 缓存目录
     */
    private static File mCacheDir;

    /**
     * 请求队列停止标示
     */
    private static boolean isStop = false;

    /**
     * 当前上下文
     */
    private Context mContext;

    /**
     * GSON对象
     */
    protected Gson mGson;

    /**
     * 请求监听器
     */
    private ApiRequestListener mApiRequestListener;

    private NewApiRequest.ApiRequestListener mNewApiRequestListener;

    /**
     * request的标志，用于取消请求
     */
    private Object mTag;

    /**
     * 注意:传入的Context应该为Activity或Service等对象，不要使用BaseContext或ApplicationContext等通用上下文对象，这样在调用{@link #cancelAll()}方法时才能将相关的请求清除
     *
     * @param context
     */
    protected EellyServerApi(Context context) {
        //这里只对activity做处理，不能对Service取getApplicationContext(),由于多进程问题，可能为null
        //TODO 如果在外面初始化，context还没有和activity绑定
        //TODO context.getApplicationContext()有可能报null指针
        if (context instanceof Activity) {
            try {
                mContext = context.getApplicationContext();
            } catch (Exception e) {
                mContext = context;
            }
        } else {
            mContext = context;
        }
        // mGson = new GsonBuilder().registerTypeAdapter(int.class, new IntegerDeserializer()).registerTypeAdapter(Integer.class, new
        // IntegerDeserializer()).create();
        mGson = new GsonBuilder().create();
        mTag = this.hashCode() + mContext.hashCode();
        if (context instanceof ActivityListenerManager) {
            ((ActivityListenerManager) context).addActivityListener(new ActivityListener() {

                @Override
                public void onDestroy() {
                    cancelAll();
                    super.onDestroy();
                }
            });
        }
    }

    /**
     * 获取当前上下文对象
     */
    final protected Context getContext() {
        return mContext;
    }

    /**
     * 创建一个新的请求对象
     *
     * @param method
     * @param url
     * @param listener
     * @return
     */
    protected <T> ApiRequest<T> newRequest(int method, String url, ApiListener<T> listener) {
        ApiRequest<T> apiRequest = new ApiRequest<>(method, url, getRequestQueue(), listener, mApiRequestListener, mContext);
        // 设置当前上下文做为请求的标齐,用于在退出上下文时调用cancelAll清除相关请求
        apiRequest.setTag(mTag);
        return apiRequest;
    }

    /**
     * 创建一个新的请求对象<br>
     * 新版
     *
     * @param method
     * @param url
     * @param listener
     * @return
     */
    protected <T> NewApiRequest<T> newApiRequest(int method, String url, ApiListener<T> listener) {
        NewApiRequest<T> apiRequest = new NewApiRequest<>(method, url, getRequestQueue(), listener, mNewApiRequestListener, mContext);
        // 设置当前上下文做为请求的标齐,用于在退出上下文时调用cancelAll清除相关请求
        apiRequest.setTag(mTag);
        return apiRequest;
    }

    /**
     * 创建一个新的请求对象<br>
     * 新版
     *
     * @param listener
     * @return
     */
    protected <T> NewApiRequest<T> newApiRequest(ApiListener<T> listener) {
        return newApiRequest(Method.POST, BaseNetConfig.getNewServerURL(mContext), listener);
    }

    /**
     * 设置请求监听器
     *
     * @param listener 请求监听器
     */
    protected void setApiRequestListener(ApiRequestListener listener) {
        mApiRequestListener = listener;
    }

    /**
     * 设置请求监听器<br>
     * 新版
     *
     * @param listener 请求监听器
     */
    protected void setNewApiRequestListener(NewApiRequest.ApiRequestListener listener) {
        mNewApiRequestListener = listener;
    }

    /**
     * 设置请求监听器
     *
     * @param listener 请求监听器
     */
    protected void setNewApiRequestListener(ApiRequestListener listener) {
        mApiRequestListener = listener;
    }

    // 网络访问线程数
    private static final int THREAD_POOL_SIZE = 4;

    // 磁盘缓存目录名
    private static final String DEFAULT_CACHE_DIR = "volley";

    // 磁盘缓存大小(byte)
    private static final int DISK_CACHE_SIZE = 10 * 1024 * 1024;

    /**
     * 获取Volley框架Http请求执行队列
     *
     * @return
     */
    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mCacheDir = new File(mContext.getCacheDir(), DEFAULT_CACHE_DIR);
            HttpStack stack = new HurlStack();
            Network network = new BasicNetwork(stack);
            mRequestQueue = new RequestQueue(new DiskBasedCache(mCacheDir, DISK_CACHE_SIZE), network, THREAD_POOL_SIZE);
            mRequestQueue.start();
            // mRequestQueue = Volley.newRequestQueue(mContext, new HurlStack());
        } else if (isStop) {// 如果请求队列已停止,重新打开请求队列
            isStop = false;
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    /**
     * 关闭请求服务
     */
    public static void stopServer() {
        if (mRequestQueue != null) {
            isStop = true;
            mRequestQueue.stop();
        }
    }

    /**
     * 取消所有请求
     */
    final public void cancelAll() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(mTag);
        }
    }

    /**
     * 获取缓存大小<br>
     * 有可能包含不是Volley缓存的文件
     */
    public static long getCacheSize() {
        return FileUtils.getDirSize(mCacheDir, true);
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        if (mRequestQueue != null) {
            mRequestQueue.getCache().clear();
        }
    }

    /**
     * 获取request设置标志
     *
     * @see #mTag
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * 为request设置标志
     *
     * @see #mTag
     */
    public void setTag(Object tag) {
        this.mTag = tag;
    }
}

/**
 * Gson Integer类型反序列化类,用于处理Gson默认实现中类型转换错误的问题
 *
 * @author 李欣
 */
class IntegerDeserializer implements JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        LogUtil.d("IntegerDeserializer", "arg0:" + arg0 + " arg1:" + arg1 + " arg2:" + arg2);
        try {
            LogUtil.d("IntegerDeserializer", "int:" + arg0.getAsInt());
        } catch (NumberFormatException ex) {
            LogUtil.d("IntegerDeserializer", "error value:" + arg0.getAsString());
        }
        return 0;
    }

}