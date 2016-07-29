package com.eelly.framework.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * Activity事件监听器<br>
 * 本应设计为interface的，但方法过多，为了便于选择性实现，就改为class和空方法
 * 
 * @author 苏腾
 */
public class ActivityListener {

	public void onCreate(Bundle savedInstanceState) {
	}

	public void onStart() {
	}

	public void onResume() {
	}

	public void onPause() {
	}

	public void onStop() {
	}

	public void onRestart() {
	}

	public void onDestroy() {
	}

	public void onSaveInstanceState(Bundle outState) {
	}

	public void onRestoreInstanceState(Bundle savedInstanceState) {
	}

	public void onNewIntent(Intent intent) {
	}

	public void onConfigurationChanged(Configuration newConfig) {
	}

	public void onAttachFragment(android.app.Fragment fragment) {
	}

	public void onAttachFragment(android.support.v4.app.Fragment fragment) {
	}

	public void onWindowFocusChanged(boolean hasFocus) {
	}

	public void onAttachedToWindow() {
	}

	public void onDetachedFromWindow() {
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	/**
	 * {@link ActivityListener}管理接口<br>
	 * 实现该接口的类，有义务在各个生命周期事件调用所注册的监听器
	 * 
	 * @author 苏腾
	 */
	public static interface ActivityListenerManager {

		/**
		 * 增加一个监听器
		 */
		public void addActivityListener(ActivityListener listener);

		/**
		 * 移除一个监听器
		 */
		public void removeActivityListener(ActivityListener listener);

	}
}
