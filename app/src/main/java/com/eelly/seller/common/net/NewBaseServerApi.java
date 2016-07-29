package com.eelly.seller.common.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.eelly.framework.util.DeviceUtil;
import com.eelly.framework.util.LogUtil;
import com.eelly.seller.common.util.PreferencesUtil;
import com.eelly.seller.constants.Constants;
import com.eelly.sellerbuyer.net.ApiError;
import com.eelly.sellerbuyer.net.ApiResponse;
import com.eelly.sellerbuyer.net.EellyServerApi;
import com.eelly.sellerbuyer.net.NewApiRequest;
import com.eelly.sellerbuyer.net.NewFileUploadAsyncTask;
import com.eelly.sellerbuyer.net.NewFileUploadAsyncTask.FileUploadListener;
import com.google.gson.JsonElement;

/**
 * 新版接口基类
 *
 * @author 林钊平
 */
public class NewBaseServerApi extends EellyServerApi {

    public static final String API_ENCRYPT_KEY = "jsJYCzaJZQJ3ZxZ@!4xKsR4b";

    public static final String APP_CLIENT = "Android";

    public static final String APP_TYPE = "seller";

    public static final String SIGN_TOKEN = "5h-NywaEfjG7ch4@PfQ23Hfz^nx#KdCC";

    public static final String ENCRYPT_KEY = "jsJYCzaJZQJ3ZxZ@!4xKsR4b";

    public static final String BBS_ENCRYPT_KEY = "+5nDkZ8!DPxZWnKp6CfSi*Jx";

    public static final String SERVER_VERSION = "v1";

    public static final String SERVER_CLIENT = "Android";

    public static final String TRANSMISSON_MODE = "Security";

    /**
     * 提示信息对象
     */
    private Toast mToast;

    /**
     * 自定义toast对象
     */
    public void setToast(Toast toast) {
        mToast = toast;
    }

    public NewBaseServerApi(final Context context) {
        super(context);
        //防止回调监听器，引用activity造成内存泄漏，所以在这里，就判断好
        final boolean isReturn = (context instanceof GetAccessActivity);
        setNewApiRequestListener(new NewApiRequest.ApiRequestListener() {

            @Override
            public void onPreRequest(NewApiRequest<?> newApiRequest) {
                String appVersion = String.valueOf(Constants.packageInfo_versionCode);
                String appTimestamp = String.valueOf(getServerTime(getContext()) / 1000L);
                newApiRequest.setClientName(APP_CLIENT);
                newApiRequest.setClientVerion(appVersion);
                newApiRequest.setClientUserType(APP_TYPE);
                newApiRequest.setTime(appTimestamp);
                newApiRequest.addHeader("Transmission-Mode", TRANSMISSON_MODE);
                if (isReturn) {
                    return;
                }
                String accessToken = getAccessToken(getContext());
                if (TextUtils.isEmpty(accessToken)) {
                    goGetAccessToken(getContext());
                    Log.v("mine", "我在这里获取AccssToken:onPreRequest(NewApiRequest<?> newApiRequest)");
                    return;
                }
                newApiRequest.addHeader("Transmission-From", accessToken);
                newApiRequest.setEncryptKey(ENCRYPT_KEY);

                // 在所有请求执行前默认加入用户token信息
                /*User user = AccountManager.getInstance().getUser();
                if (user != null) {
					String userToken = user.getTokenKey();
					newApiRequest.setUserToken(userToken);
					LogUtil.d("ApiRequestListener", "token:" + userToken);
				}*/
                //TODO
                newApiRequest.setUserToken("");
                //newApiRequest.setUserId("3");
            }

            @Override
            public void onError(NewApiRequest<?> newApiRequest, ApiResponse<?> apiResponse) {
                if (mToast == null) {
                    mToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
                }
                String msg = null;
                if (apiResponse.isNoConnectionError()) {
                    msg = "网络未连接,请检查网络";
                } else if (apiResponse.isTimeoutError()) {
                    msg = "网络不给力,请重试";
                } else if (apiResponse.isNetworkError() || apiResponse.isServerError() || apiResponse.isParseError()) {
                    msg = "请求失败,请重试";
                } else if (apiResponse.isApiError()) {
                    ApiError apiError = apiResponse.getError();
                    int errorCode = apiError.getStatusCode();
                    LogUtil.d("test", "msg:" + apiError.getMessage() + " code:" + apiError.getStatusCode());
                    if (errorCode == Constants.TOKEN_EXPIRE_CODE || errorCode == Constants.TOKEN_ERROR_CODE
                            || errorCode == Constants.TOKEN_EXPIRE_CODE1 || errorCode == Constants.TOKEN_EXPIRE_CODE2
                            || errorCode == Constants.TOKEN_EXPIRE_CODE3 || errorCode == Constants.TOKEN_EXPIRE_CODE4) {
                        //MainBroadcastReceiver.relogin(getContext());
                    } else if (errorCode == Constants.NO_PERMISSION_CODE) {
                        msg = apiResponse.getErrorMsg();
                        Context context = getContext();
                        /*if (context instanceof Activity && !(context instanceof MainActivity)) {
                            ((Activity) context).finish();
						}*/
                    } else if (errorCode == Constants.ACCESS_TOKEN_INVALID) {
                        goGetAccessToken(getContext());
                        Log.v("mine", "我在这里获取AccssToken:onPreRequest(NewApiRequest<?> newApiRequest)");
                    }
                }
                if (!newApiRequest.isSilent() && msg != null && msg.length() > 0) {
                    mToast.setText(msg);
                    mToast.show();
                }

                if (apiResponse.hasError()) {
                    LogUtil.w("ApiRequestError", apiResponse.getErrorMsg());
                    if (apiResponse.getError() instanceof VolleyError) {
                        VolleyError error = apiResponse.getError();
                        if (error.getMessage() != null)
                            LogUtil.w("ApiRequestError", error.getMessage());
                        if (error.networkResponse != null && error.networkResponse.data != null)
                            LogUtil.w("ApiRequestError", new String(error.networkResponse.data));
                    }
                }
            }

        });
    }

    /**
     * 获取新的上传任务，已经加入http头信息
     *
     * @param listener
     * @return
     */
    public static NewFileUploadAsyncTask getFileUploadAsyncTask(Context context, FileUploadListener listener) {
        String accessToken = getAccessToken(context);
        if (TextUtils.isEmpty(accessToken)) {
            goGetAccessToken(context);
        }
        NewFileUploadAsyncTask task = new NewFileUploadAsyncTask(NetConfig.getNewServerURL(context), listener);
        // 设置头信息
        // String appVersion = String.valueOf(Constants.packageInfo_versionCode);
        String appTimestamp = String.valueOf(getServerTime(context) / 1000L);
        task.addHeader("Transmission-Mode", TRANSMISSON_MODE);
        task.addHeader("Transmission-From", accessToken);
        task.setEncryptKey(ENCRYPT_KEY);
        task.setApp("Api");
        task.setServerName("UploadService");
        task.setMethod("upload");
        task.setTime(appTimestamp);
        task.setIMEI(DeviceUtil.getIMEI(context));

        // 加入用户token信息
        /*User user = AccountManager.getInstance().getUser();
        if (user != null) {
			String userToken = user.getTokenKey();
			LogUtil.d("FileUploadAsyncTask", "token:" + userToken);
			// 设置用户请求Token
		}*/

        // 设置超时时间
        task.setConnectTimeout(60000); // 1min
        task.setReadTimeout(600000); // 10min
        return task;
    }

    public static NewFileUploadAsyncTask getFileUploadAsyncTask(Context context, boolean isParse, FileUploadListener listener) {
        NewFileUploadAsyncTask task = getFileUploadAsyncTask(context, listener);
        task.setIsParseResult(isParse);
        return task;
    }

    /**
     * 从服务端返回结果里解析结果字符串，形如 {"statusCode":0,"data":{"result":"取消订单成功"}}<br>
     * 会依次尝试 result/message/msg/success 这些字段名
     *
     * @param je ResponseParser.parse 传进来的参数
     * @return 解析不了返回null
     */
    public static String getStringResult(JsonElement je) {
        String res = getStringResult(je, "result");
        if (res == null)
            res = getStringResult(je, "message");
        if (res == null)
            res = getStringResult(je, "msg");
        if (res == null)
            res = getStringResult(je, "success");
        return res;
    }

    /**
     * 从服务端返回结果里解析结果字符串，形如 {"statusCode":0,"data":{"result":"取消订单成功"}}
     *
     * @param je        ResponseParser.parse 传进来的参数
     * @param fieldName 字符串字段名
     * @return 解析不了返回null
     */
    public static String getStringResult(JsonElement je, String fieldName) {
        if (je == null || !je.isJsonObject())
            return null;
        try {
            JsonElement je2 = je.getAsJsonObject().get(fieldName);
            if (je2 != null)
                return je2.getAsString();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取AccessToken
     *
     * @return
     */
    protected static String getAccessToken(Context context) {
        if (TextUtils.isEmpty(Constants.accessToken)) {
            AccessToken token = PreferencesUtil.getAccessToken(context);
            if (token != null) {
                return token.getAccesstoken();
            }
            return "";
        }

        return Constants.accessToken;
    }

    /**
     * 跳到无网络页面获取AccessToken
     */
    private static void goGetAccessToken(Context context) {
        if (context instanceof GetAccessActivity) {
            return;
        }
        Intent intent = new Intent(context, GetAccessActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        Log.i("LogTag", "sss");

        context.startActivity(intent);
    }

    /**
     * 获取准确的时间
     *
     * @param context
     * @return
     */
    private static long getServerTime(Context context) {
        if (Constants.differTime != 0L && Math.abs(Constants.differTime) <= 5000L) {
            return System.currentTimeMillis();
        }
        long time = PreferencesUtil.getServerTime(context);
        return (System.currentTimeMillis() - time);
    }
}
