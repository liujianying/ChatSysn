package com.eelly.framework.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import java.lang.reflect.Field;

public class ViewUtil {

	/**
	 * 获取Activity根视图
	 * 
	 * @return Activity根视图
	 */
	public static ViewGroup getActivityView(Activity activity) {
		return (ViewGroup) activity.getWindow().findViewById(android.R.id.content);
	}

	/**
	 * 设置溢出滚动模式,主要用于屏蔽魅族等自定义的阻尼下拉,避免冲突
	 * 
	 * @param view
	 * @param mode
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void disableOverScrollMode(View view) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			view.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
	}

	/**
	 * 使用软件加速,在打开硬件加速的情况下,由于某些View并不支持硬件加速,因此需要设置为软件加速
	 * 
	 * @param view
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void useSoftware(View view, Paint paint) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {// Android3.0开始有此方法
			view.setLayerType(View.LAYER_TYPE_SOFTWARE, paint);
		}
	}

	/**
	 * 打开当前Window的硬件加速
	 * 
	 * @param window
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static void openHardWare(Window window) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {// Android3.0开始有此方法,但加速效果太差,所以只在4.0以上版本开启
			window.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		}
	}

	/**
	 * 视图是否支持硬件加速
	 * 
	 * @param view
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static boolean isHardwareAccelerated(View view) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {// Android3.0开始有此方法
			return view.isHardwareAccelerated();
		}
		return false;
	}

	/**
	 * Canvas是否支持硬件加速
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static boolean isHardwareAccelerated(Canvas canvas) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {// Android3.0开始有此方法
			return canvas.isHardwareAccelerated();
		}
		return false;
	}

	/**
	 * 设置View背景
	 * 
	 * @param view
	 * @param drawable
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void setBackground(View view, Drawable drawable) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			view.setBackground(drawable);
		} else {
			view.setBackgroundDrawable(drawable);
		}
	}

	/**
	 * 全局布局监听器 通过GlobalLayoutListener.onLayout()返回flase将保持监听,返回true将自动移除监听器
	 */
	@SuppressLint("WrongCall")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static abstract class GlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

		private View view;

		@Override
		final public void onGlobalLayout() {
			if (view != null && view.getViewTreeObserver() != null && onLayout(view)) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				} else {
					view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
				view = null;
			}
		}

		abstract public boolean onLayout(View view);

	}

	/**
	 * 添加全局布局监听器,主要用于需要在布局完成后获取视图高宽等信息
	 * 
	 * @param listener
	 */
	public static void addGlobalLayoutListener(View view, GlobalLayoutListener listener) {
		listener.view = view;
		view.getViewTreeObserver().addOnGlobalLayoutListener(listener);
	}

	/**
	 * 用于在视图未确定高宽前"估算"视图的width以及height,注意此方法必须是使用LayoutInflater.inflate(resid, container, false),并且container!=null的情况才有效
	 * 
	 * @param child
	 */
	public static void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * 修复PopupWindow在Android3.0之前因为绑定到ViewTreeObserver.OnScrollChangedListener后没有判断null导致的错误
	 */
	public static void popupWindowFix(final PopupWindow popupWindow) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			try {
				final Field fAnchor = PopupWindow.class.getDeclaredField("mAnchor");
				fAnchor.setAccessible(true);
				Field listener = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
				listener.setAccessible(true);
				final ViewTreeObserver.OnScrollChangedListener originalListener = (ViewTreeObserver.OnScrollChangedListener) listener
						.get(popupWindow);
				ViewTreeObserver.OnScrollChangedListener newListener = new ViewTreeObserver.OnScrollChangedListener() {

					public void onScrollChanged() {
						try {
							View mAnchor = (View) fAnchor.get(popupWindow);
							if (mAnchor == null) {
								return;
							} else {
								originalListener.onScrollChanged();
							}
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				};
				listener.set(popupWindow, newListener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static int getBottomBarHeight(Activity activity) {
		Rect rectangle = new Rect();
		Window window = activity.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectangle);

		DisplayMetrics dm = activity.getResources().getDisplayMetrics();
		return dm.heightPixels - rectangle.bottom;
	}

	public static int getTopBarHeight(Activity activity) {
		Rect rect = new Rect();
		Window win = activity.getWindow();
		win.getDecorView().getWindowVisibleDisplayFrame(rect);
		return rect.top;
	}

	/**
	 * 获得view的后一个兄弟view
	 * 
	 * @param view
	 * @return
	 */
	public static View getNextSibling(View view) {
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent == null)
			return null;
		int i = parent.indexOfChild(view) + 1;
		return i < parent.getChildCount() ? parent.getChildAt(i) : null;
	}

	/**
	 * 获得view的前一个兄弟view
	 * 
	 * @param view
	 * @return
	 */
	public static View getPreviousSibling(View view) {
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent == null)
			return null;
		int i = parent.indexOfChild(view) - 1;
		return i >= 0 ? parent.getChildAt(i) : null;
	}

}
