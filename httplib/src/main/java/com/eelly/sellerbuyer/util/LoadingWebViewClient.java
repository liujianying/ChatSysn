package com.eelly.sellerbuyer.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Timer;
import java.util.TimerTask;

/**
 * WebView加载动作监听器
 * 
 * @author 苏腾
 */
public class LoadingWebViewClient extends WebViewClient {

	/** 页面加载超时时间 */
	public static final int TIMEOUT = 30000;

	protected boolean isError = false;

	protected long lastErrorTime = 0L;

	protected WebView webView;

	protected LoadingListener listener;

	protected String currentUrl;

	protected Timer timer;

	public LoadingWebViewClient(WebView webView, LoadingListener listener) {
		this.webView = webView;
		this.listener = listener;
	}

	@SuppressLint("HandlerLeak")
	protected Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// 超时后,首先判断页面加载进度,超时并且进度小于100,就执行超时后的动作
			if (webView.getProgress() < 100) {
				webView.stopLoading();
				onReceivedError(webView, ERROR_TIMEOUT, "", currentUrl);
			}
		};
	};

	/**
	 * 覆盖这个方法以设定超时时间
	 * 
	 * @return
	 */
	protected int getTimeout() {
		return TIMEOUT;
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		super.onPageStarted(view, url, favicon);

		// 出错之后可能会再走一次onPageStarted，在此忽略
		if (System.currentTimeMillis() - lastErrorTime < 100) {
			return;
		}

		isError = false;
		currentUrl = url;
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				Message.obtain(handler).sendToTarget();
				cancelTimer();
			}
		}, getTimeout());
		if (listener != null) {
			listener.onPageStarted(view, url);
		}
	}

	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);
		isError = true;
		lastErrorTime = System.currentTimeMillis();
		cancelTimer();
		if (listener != null) {
			String res = "";
			switch (errorCode) {
				case WebViewClient.ERROR_HOST_LOOKUP:
					res = "加载失败";
					break;
				case WebViewClient.ERROR_CONNECT:
					res = "无法连接至服务器";
					break;
				case WebViewClient.ERROR_TIMEOUT:
					res = "连接超时";
					break;
				case WebViewClient.ERROR_REDIRECT_LOOP:
					res = "过多重定向";
					break;
				case WebViewClient.ERROR_BAD_URL:
					res = "网址错误";
					break;
				default:
					res = "连接失败(" + errorCode + ")";
					break;
			}
			listener.onReceivedError(view, errorCode, description, failingUrl, res + "，请点击重试");
		}
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		cancelTimer();
		if (!isError && listener != null) {
			listener.onPageFinished(view, url);
		}
	}

	/**
	 * 返回当前地址
	 * 
	 * @return
	 */
	public String getCurrentUrl() {
		return currentUrl;
	}

	/**
	 * 取消定时器
	 */
	public synchronized void cancelTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

	/**
	 * 代理WebViewClient的若干方法
	 */
	public interface LoadingListener {

		public void onPageStarted(WebView view, String url);

		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl, String detailMessage);

		public void onPageFinished(WebView view, String url);
	}

}
