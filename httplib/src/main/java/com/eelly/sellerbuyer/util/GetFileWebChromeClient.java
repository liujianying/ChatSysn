package com.eelly.sellerbuyer.util;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

import com.eelly.framework.app.NonProguard;

/**
 * Webview获取文件监听器
 * 
 * @author 苏腾
 */
public class GetFileWebChromeClient extends WebChromeClient implements NonProguard {

	private OnGetFileListener mListener;

	public GetFileWebChromeClient(OnGetFileListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		mListener = listener;
	}

	// Android < 3.0 调用这个方法
	public void openFileChooser(ValueCallback<Uri> uploadMsg) {
		mListener.onGetFile(uploadMsg);
	}

	// 3.0 + 调用这个方法
	public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
		mListener.onGetFile(uploadMsg);
	}

	// Android > 4.1.1 调用这个方法
	public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
		mListener.onGetFile(uploadMsg);
	}

	public interface OnGetFileListener {

		public void onGetFile(ValueCallback<Uri> callback);
	}

}
