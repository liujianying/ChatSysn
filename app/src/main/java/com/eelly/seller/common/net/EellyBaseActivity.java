package com.eelly.seller.common.net;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.eelly.framework.annotation.AutoInitUtil;
import com.eelly.framework.app.ActivityListener;
import com.eelly.framework.app.ActivityListener.ActivityListenerManager;

import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class EellyBaseActivity extends AppCompatActivity implements ActivityListenerManager {

	/**
	 * Activity事件监听器列表
	 */
	private ArrayList<ActivityListener> mActivityListeners = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater layoutInflater = LayoutInflater.from(this);

		// 触发内容视图初始化前方法
		onPreContentInit();

		for (ActivityListener listener : mActivityListeners) {
			listener.onCreate(savedInstanceState);
		}
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		onContentViewInit();
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		onContentViewInit();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		onContentViewInit();
	}

	/**
	 * 内容视图初始化前触发方法,用于进行一些必须在内容视图初始化前进行的设置
	 */
	protected void onPreContentInit() {

	}

	/**
	 * 内容视图初始化完成触发方法
	 */
	protected void onContentViewInit() {
		// 进行自动注入
		AutoInitUtil.inject(this, this);
	}

	/**
	 * 解决其他有些手机有物理菜单键的情况下，action不能弹出菜单
	 */
	private void actionbarMenu() {
		try {
			ViewConfiguration mconfig = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(mconfig, false);
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
		}
	}



	/**
	 * 获取当前Activity根视图
	 * 
	 * @return
	 */
	final public ViewGroup getRootView() {
		return (ViewGroup) getWindow().findViewById(android.R.id.content);
	}

	@Override
	final public void addActivityListener(ActivityListener listener) {
		if (listener != null) {
			mActivityListeners.add(listener);
		}
	}

	@Override
	final public void removeActivityListener(ActivityListener listener) {
		mActivityListeners.remove(listener);
	}

	@Override
	protected void onStart() {
		super.onStart();
		for (ActivityListener listener : mActivityListeners) {
			listener.onStart();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		for (ActivityListener listener : mActivityListeners) {
			listener.onRestart();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		for (ActivityListener listener : mActivityListeners) {
			listener.onResume();
		}
	}

	@Override
	protected void onPause() {
		for (ActivityListener listener : mActivityListeners) {
			listener.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		for (ActivityListener listener : mActivityListeners) {
			listener.onStop();
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		for (ActivityListener listener : mActivityListeners) {
			listener.onDestroy();
		}
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		for (ActivityListener listener : mActivityListeners) {
			listener.onSaveInstanceState(outState);
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		for (ActivityListener listener : mActivityListeners) {
			listener.onRestoreInstanceState(savedInstanceState);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		for (ActivityListener listener : mActivityListeners) {
			listener.onNewIntent(intent);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		for (ActivityListener listener : mActivityListeners) {
			listener.onConfigurationChanged(newConfig);
		}
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
		for (ActivityListener listener : mActivityListeners) {
			listener.onAttachFragment(fragment);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		for (ActivityListener listener : mActivityListeners) {
			listener.onWindowFocusChanged(hasFocus);
		}
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		for (ActivityListener listener : mActivityListeners) {
			listener.onAttachedToWindow();
		}
	}

	@Override
	public void onDetachedFromWindow() {
		for (ActivityListener listener : mActivityListeners) {
			listener.onDetachedFromWindow();
		}
		super.onDetachedFromWindow();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		for (ActivityListener listener : mActivityListeners) {
			listener.onActivityResult(requestCode, resultCode, data);
		}
	}
}
