package com.eelly.seller.common.net;

import android.os.AsyncTask;
import android.util.Log;

import com.eelly.framework.util.IOUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 简单的字符类型请求
 *
 * @author 林钊平
 */
public class StringRequestTask extends AsyncTask<Void, String, String> {

    private int mHttpCode = 0;

    private String mErrorMsg = "";

    private ResponseListener mListener;

    private String mUrl;

    public StringRequestTask(String url, ResponseListener listener) {
        mUrl = url;
        mListener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = null;
        InputStream input = null;
        HttpURLConnection conn = null;
        StringBuilder temp = null;
        try {
            URL fileUrl = new URL(mUrl);
            conn = (HttpURLConnection) fileUrl.openConnection();
            conn.setConnectTimeout(3 * 1000);
            mHttpCode = conn.getResponseCode();
            if (mHttpCode != HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                temp = new StringBuilder(10);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    temp.append(line);
                }
                reader.close();
                mErrorMsg = temp.toString();
            } else {
                input = conn.getInputStream();
                result = IOUtil.toString(input);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(input);
            conn.disconnect();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.v("mine", "有回应了");
        if (result == null) {
            Log.v("mine", "返回结果错误");
            mListener.onError(mHttpCode, mErrorMsg);
        } else {
            mListener.reponse(result);
            Log.v("mine", "成功返回结果");
        }
    }

    public static interface ResponseListener {

        void reponse(String result);

        void onError(int httpCode, String errorMsg);
    }

}
