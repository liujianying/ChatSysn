/**
 * 
 */
package com.eelly.seller.common.util;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by 黄艳武 on 2015/10/19;最后一次修改时间10/19 20:20 Function:
 */
public class BaseLinearPoupWindows extends PopupWindow {

	private View views;

	private Context mContext;

	/**
	 * 布局文件
	 */
	private FrameLayout fram;

	private int[] screnLocation = new int[2];

	private Runnable runnable;

	public BaseLinearPoupWindows(Context context, int layoutid) {
		super();
		this.mContext = context;
		views = LayoutInflater.from(context).inflate(layoutid, null);
		setPoupNormalStyle(context);
	}

	public BaseLinearPoupWindows(Context context, View view) {
		super();
		this.mContext = context;
		views = view;
		setPoupNormalStyle(context);
	}

	private void setPoupNormalStyle(Context context) {

		fram = add2FramLayout(context);
		setPoupWindowsSize();
		setContentView(fram);
		setOutsideTouchable(true);
		setFocusable(true);
		BaseLinearPoupWindows.this.setBackgroundDrawable(new BitmapDrawable());

		update();
	}

	/**
	 * 将界面添加到另外一个layout中,这是为了让其有个属性可以控制其离右边距有多长
	 *
	 * @return
	 */
	@NonNull
	private FrameLayout add2FramLayout(Context context) {
		fram = new FrameLayout(context);
		fram.addView(views);
		return fram;
	}

	public BaseLinearPoupWindows(Context context) {
		if (onIniViews() == 0) {
			throw new IllegalStateException("请设置布局文件,复写onIniViews()或者调用getPoupInstance(Context context, int layoutid)");
		}
		views = LayoutInflater.from(context).inflate(onIniViews(), null);
		setPoupNormalStyle(context);
	}

	/**
	 * 设置布局文件的poupwindow的大小
	 *
	 * @return
	 */
	public void setPoupWindowsSize() {
		setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
		setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
	}

	/**
	 * 设置布局文件的poupwindow的大小
	 *
	 * @return
	 */
	public void setMatchPoupWindowsSize() {
		setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
		setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
	}

	/**
	 * 布局文件，R.layout.dialog_xml
	 *
	 * @return
	 */
	protected int onIniViews() {
		return 0;
	}

	/**
	 * 对控件进行初始化,并返回对应的控价类型
	 *
	 * @return
	 */
	public <E extends View> E findView(int viewid) {
		return (E) views.findViewById(viewid);
	}

	/**
	 * 设置离右边距有多长
	 *
	 * @return
	 */
	public BaseLinearPoupWindows setMarginRight(int marginRight) {
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) views.getLayoutParams();
		lp.rightMargin = marginRight;
		views.setLayoutParams(lp);
		return this;
	}

	/**
	 * 设置离边距有多长
	 *
	 * @return
	 */
	public BaseLinearPoupWindows setMargin(int margin) {
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) views.getLayoutParams();
		lp.setMargins(margin, margin, margin, margin);
		views.setLayoutParams(lp);
		return this;
	}

	/**
	 * 设置离边距有多长
	 *
	 * @return
	 */
	public BaseLinearPoupWindows setMargins(int left, int top, int right, int bottom) {
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) views.getLayoutParams();
		lp.setMargins(left, top, right, bottom);
		views.setLayoutParams(lp);
		return this;
	}

	/*public BaseLinearPoupWindows setBackGroundResoures(int resoures) {
	    fram.setCardBackgroundColor(resoures);
	    return this;
	}*/

	/**
	 * 设置控件的大小
	 *
	 * @return
	 */
	public BaseLinearPoupWindows setViewWidth(int width) {
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) views.getLayoutParams();
		lp.width = width;
		lp.height = width;
		views.setLayoutParams(lp);
		return this;
	}

	/**
	 * 通过控制布局的大小来控制PoupWindows的大小
	 *
	 * @return
	 */
	public BaseLinearPoupWindows setPoupWindowsSize(int width, int height) {
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) views.getLayoutParams();
		lp.width = width;
		lp.height = height;
		views.setLayoutParams(lp);
		return this;

	}

	public BaseLinearPoupWindows setpoupWindowWidth(int width) {
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) views.getLayoutParams();
		lp.width = width;
		views.setLayoutParams(lp);
		return this;
	}

	/**
	 * 设置背景颜色还有,如果是圆角矩形的话,设置半径的大小
	 */
	public BaseLinearPoupWindows setDrawableNRadius(int color, int radius) {
		GradientDrawable gradientDrawable = new GradientDrawable();
		gradientDrawable.setShape(GradientDrawable.RECTANGLE);
		gradientDrawable.setColor(mContext.getResources().getColor(color));
		gradientDrawable.setCornerRadius(radius);
		views.setBackgroundDrawable(gradientDrawable);
		return this;
	}

	/**
	 * 定时关闭poupwindows
	 *
	 * @return
	 */
	public BaseLinearPoupWindows postDelayDismiss(int delaytimes) {
		(new Handler()).postDelayed(new Runnable() {

			@Override
			public void run() {
				BaseLinearPoupWindows.this.dismiss();
			}
		}, delaytimes);
		return this;
	}

	/***
	 * 定时关闭poupwindows;使用已有handler,更加节省资源
	 *
	 * @return
	 */
	public BaseLinearPoupWindows postDelayDismiss(final Handler mHandler, int delaytimes) {
		if (runnable == null) {
			runnable = new Runnable() {

				@Override
				public void run() {
					BaseLinearPoupWindows.this.dismiss();
				}
			};
		}
		mHandler.postDelayed(runnable, delaytimes);
		return this;
	}

	public void showAtViewTop(View v) {
		v.getLocationOnScreen(screnLocation);
		this.showAtLocation(v, Gravity.NO_GRAVITY, (screnLocation[0] + v.getWidth() / 2) - this.getWidth() / 2, screnLocation[1] - this.getHeight());
	}

	/**
	 * 在屏幕中间显示
	 *
	 * @return
	 */
	public void showAtScreenCenter(View view) {
		showAtLocation((View) view.getParent(), Gravity.CENTER, 0, 0);
	}

	/**
	 * @param color
	 */
	public void setBackgroundColor(int color) {
		GradientDrawable gradientDrawable = new GradientDrawable();
		gradientDrawable.setColor(color);
		gradientDrawable.setDither(true);
		BaseLinearPoupWindows.this.setBackgroundDrawable(gradientDrawable);
		BaseLinearPoupWindows.this.update();

	}

	public Context getContext() {
		return mContext;
	}
}