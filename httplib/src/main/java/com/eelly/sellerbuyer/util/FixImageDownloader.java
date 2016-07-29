package com.eelly.sellerbuyer.util;

import android.content.Context;
import android.os.Build;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * ImageLoader图片下载类,修正了在某些SDK版本中出现java.io.EOFException的问题
 * 
 * @author 李欣
 */
public class FixImageDownloader extends BaseImageDownloader {

	public FixImageDownloader(Context context) {
		super(context);
	}

	public FixImageDownloader(Context context, int connectTimeout, int readTimeout) {
		super(context, connectTimeout, readTimeout);
	}

	/**
	 * 复写获取网络连接方法
	 */
	@Override
	protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
		// https不验证服务器端证书
		SSLContext sc;
		try {
			sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[] { new MyTrustManager() }, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}

		HttpURLConnection conn = super.createConnection(url, extra);

		// 在指定的版本范围内设置网络访问参数,避免出现java.io.EOFException
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2 && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
			conn.setRequestProperty("Connection", "close");
		}
		return conn;
	}

	private class MyHostnameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	private class MyTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}