package com.eelly.seller.common.net;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.eelly.seller.common.util.PreferencesUtil;
import com.eelly.seller.constants.Constants;
import com.eelly.sellerbuyer.net.ApiListener;
import com.eelly.sellerbuyer.net.ApiResponse;

/**
 * 获取AccessToken<br>
 * 当服务端返回AccessToken无效时，跳到此页面进行获取服务器时间，然后再获取AccessToken
 *
 * @author 林钊平
 */
public class GetAccessActivity extends BaseActivity {

    public static final String GET_TIME_URL = "/api/time";

    private AppStartApi mStartApi;

    private TextView mReLoadTv;

    private StringRequestTask mGetTimeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new View(this));
        mStartApi = new AppStartApi(this);
        getTime();
    }

    /**
     * 获取AccessToken
     */
    private void getAccessToken() {
        mStartApi.getAccessToken(new ApiListener<AccessToken>() {

            @Override
            public void onResponse(ApiResponse<AccessToken> response) {
                if (response.hasError()) {
                    if (response.isApiError()) {
                        showToast(response.getErrorMsg());
                        mReLoadTv.setVisibility(View.VISIBLE);
                        return;
                    }
                } else {
                    AccessToken token = response.get();
                    if (token != null) {
                        Constants.accessToken = token.getAccesstoken();
                        PreferencesUtil.saveAccessToken(GetAccessActivity.this, token);
                    }
                    finish();
                }
            }
        });
    }

    /**
     * 获取系统时间
     */
    private void getTime() {
        mGetTimeTask = new StringRequestTask(NetConfig.getNewApiBaseURL(this) + GET_TIME_URL, new StringRequestTask.ResponseListener() {

            @Override
            public void reponse(String result) {
                PreferencesUtil.saveDifferTime(GetAccessActivity.this, result);
                getAccessToken();
            }

            @Override
            public void onError(int httpCode, String errorMsg) {
                String defaultTime = String.valueOf(System.currentTimeMillis() / 1000L);
                PreferencesUtil.saveDifferTime(GetAccessActivity.this, defaultTime);
                getAccessToken();
            }
        });
        mGetTimeTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGetTimeTask.cancel(true);
        mStartApi.cancelAll();
    }

}
