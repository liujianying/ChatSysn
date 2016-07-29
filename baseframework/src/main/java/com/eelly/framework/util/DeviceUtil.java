package com.eelly.framework.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NewApi")
public class DeviceUtil {

	/**
	 * 判断指定上下文所在进程是否应用主进程
	 * 
	 * @return true:应用主进程 false:和应用相关的独立进程
	 */
	public static boolean isAppMainProcess(Context context) {
		// 获取应用当前进程名
		String processName = getCurrentProcessName(context.getApplicationContext());
		String appProcessName = context.getApplicationInfo().processName;
		// 如果进程名和应用的主进程名(应用主进程名由AndroidManifext.xml中的<appliction
		// android:process="">属性定义,默认和PackageName包名相同)相同说明是应用主进程,不相同说明是一个独立的应用进程(在AndroidManifext.xml中设置了android:process属性的Activity、Service、receiver、provider)
		return appProcessName.equals(processName);
	}

	/**
	 * 获取当前上下文所在进程名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrentProcessName(Context context) {
		// 当前进程id
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

	/**
	 * 获取Activity信息
	 * 
	 * @param context
	 * @param className
	 * @return 获取失败返回null
	 */
	public static ActivityInfo getActivityInfo(Context context, String className) {
		ComponentName cn = new ComponentName(context, className);
		try {
			return context.getPackageManager().getActivityInfo(cn, PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	/**
	 * 获取Activity配置文件label名称
	 * 
	 * @param context 上下文
	 * @param className 要获取的Activity类完整名称
	 * @return 获取失败返回null
	 */
	public static String getActivityLabel(Context context, String className) {
		ActivityInfo aInfo = getActivityInfo(context, className);
		if (aInfo == null) {
			return null;
		} else {
			// return aInfo.loadLabel(context.getPackageManager()).toString();
			// 本来是上边一行代码搞定的，但是如果activity没设label他会返回application的label。下边的代码出自loadLabel，没有label时返回className
			if (aInfo.nonLocalizedLabel != null) {
				return aInfo.nonLocalizedLabel.toString();
			}
			if (aInfo.labelRes != 0) {
				CharSequence label = context.getPackageManager().getText(aInfo.packageName, aInfo.labelRes, aInfo.applicationInfo);
				if (label != null) {
					return label.toString().trim();
				}
			}
			return className;
		}
	}

	/**
	 * 获取手机IMEI
	 * 
	 * @param appContext
	 * @return
	 */
	public static String getIMEI(Context appContext) {

		return ((TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

	}

	/**
	 * 根据手机的分辨率从 dip 的单位 转成为px
	 * 
	 * @param appContext
	 * @param dpValue
	 * @return
	 */
	public static int dipToPx(Context appContext, float dpValue) {
		return (int) (dpValue * appContext.getResources().getDisplayMetrics().density + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px 的单位 转成为dip
	 * 
	 * @param appContext
	 * @param pxValue
	 * @return
	 */
	public static int px2Dip(Context appContext, float pxValue) {
		return (int) (pxValue / appContext.getResources().getDisplayMetrics().density + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param context （DisplayMetrics类中属性scaledDensity）
	 * @param pxValue
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param context （DisplayMetrics类中属性scaledDensity）
	 * @param spValue
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 获取显示屏像素大小
	 * 
	 * @param appContext
	 * @return point.x point.y
	 */
	@SuppressLint("NewApi")
	public static Point getDisplaySize(Context appContext) {
		WindowManager wm = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point point = new Point();
		if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 13) {
			point.set(display.getWidth(), display.getHeight());
		} else {
			display.getSize(point);
		}
		return point;
	}

	/**
	 * 隐藏输入法
	 * 
	 * @param context activity的context
	 */
	public static void hideKeyboard(Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			// 可能没有focus
			// imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
			imm.hideSoftInputFromWindow(((Activity) context).getWindow().getDecorView().getWindowToken(), 0);
		}
	}

	/**
	 * 显示输入法
	 * 
	 * @param view 接收输入的view
	 */
	public static void showKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}

	/**
	 * 自动延时弹出软键盘
	 * 
	 * @param lateTimer
	 */
	public static void showKeyboardLate(final View view, int lateTimer) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			public void run() {
				InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(view, 0);
			}

		}, lateTimer);
	}

	/**
	 * 获取应用信息
	 * 
	 * @param context
	 * @return
	 */
	public static PackageInfo getPackageInfo(Context context) {
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return packInfo;
	}

	/**
	 * 获取AndroidManifest.xml中设置的应用名
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppName(Context context) {
		PackageInfo packInfo = getPackageInfo(context);
		if (packInfo == null) {
			return "";
		}
		return context.getString(packInfo.applicationInfo.labelRes);
	}

	/**
	 * 获取当前SDK的系统版本
	 * 
	 * @return
	 */
	public static int getSystemVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * 获取状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		int statusHeight = 0;
		Rect localRect = new Rect();
		((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
				statusHeight = context.getResources().getDimensionPixelSize(i5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}

	/**
	 * ActionBar的高度
	 * 
	 * @param appContext
	 * @return
	 */
	public static float getActionBarHeight(Context appContext) {
		TypedArray actionbarSizeTypedArray = appContext.obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });
		float height = actionbarSizeTypedArray.getDimension(0, 0);
		actionbarSizeTypedArray.recycle();
		return height;
	}

	/**
	 * 拷贝文本到剪切板
	 * 
	 * @param context
	 * @param text
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static void copyText(Context context, String text) {
		if (android.os.Build.VERSION.SDK_INT < 11) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(text);
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("", text);
			clipboard.setPrimaryClip(clip);
		}
	}

	/**
	 * 判断应用是在前台还是在后台运行
	 * 
	 * @param context
	 * @return 后台true 前台false
	 */
	public static boolean isApplicationBroughtToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * TODO::暂时不要使用本方法,本方法从友盟复制而来,作为友盟测试和参考使用,日后需要测试改进 获取当前设备标示信息json
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceInfo(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			String device_id = tm.getDeviceId();

			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

			String mac = wifi.getConnectionInfo().getMacAddress();
			json.put("mac", mac);

			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}

			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
			}

			json.put("device_id", device_id);

			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 得到屏幕的高度
	 *
	 * @param context
	 */
	public static int getScreenHeight(Context context) {
		if (context == null) {
			return -1;
		}
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 得到屏幕的宽度
	 *
	 * @param context
	 */
	public static int getScreenWidth(Context context) {
		if (context == null) {
			return -1;
		}
		return context.getResources().getDisplayMetrics().widthPixels;
	}

}
