package com.eelly.seller.common.net;

import android.content.Context;

import com.eelly.sellerbuyer.net.ApiListener;
import com.eelly.sellerbuyer.net.NewApiRequest;
import com.eelly.sellerbuyer.net.ResponseParser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 应用启动，获取AccessToken
 *
 * @author 林钊平
 */
public class AppStartApi extends NewBaseServerApi {

    /**
     * 获取AccessToken加密密钥
     */
    public final static String ENCRYPT_KEY = "%HdoQqwI3sQ3bBnaLReX^hMp";

    public static final String APP_ID = "XA&N%e-6&nkTZz2jNs";

    public static final String APP_SECRET = "dj4*^#)>48s";

    public static final String GET_TIME_URL = "http://mall.eelly.test/api/time";

    private Gson mGson;

    public AppStartApi(Context context) {
        super(context);
        mGson = new Gson();
    }

    /**
     * 获取AccessToken
     *
     * @param listener
     * @return
     */
    public NewApiRequest<?> getAccessToken(ApiListener<AccessToken> listener) {
        NewApiRequest<AccessToken> apiRequest = newApiRequest(listener);
        apiRequest.setApp("Api");
        apiRequest.setServerName("CredentialService");
        apiRequest.setMethod("token");
        apiRequest.setArgs(mGson.toJson(new GetAccessToken(APP_ID, APP_SECRET)));
        apiRequest.setEncryptKey(ENCRYPT_KEY);
        apiRequest.addHeader("Transmission-Token", "true");
        return apiRequest.request(new ResponseParser<AccessToken>() {

            @Override
            public AccessToken parse(JsonElement je) {
                return mGson.fromJson(je, AccessToken.class);
            }
        });
    }

}
