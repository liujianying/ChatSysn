package com.eelly.framework.annotation;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 视图自动注入类
 * 
 * @author 李欣,苏腾
 */
public class AutoInitUtil {

	/**
	 * 进行注入
	 * 
	 * @param target 要注入视图和资源的目标对象
	 * @param view 要注入视图所在父视图，&监听器提供者
	 */
	public static void inject(Object target, View view) {
		inject(target, new ViewInjectHelperImp1(view), view);
	}

	/**
	 * 进行注入
	 * 
	 * @param target 要注入视图和资源的目标对象
	 * @param view 要注入视图所在父视图
	 * @param provider 监听器提供者
	 */
	public static void inject(Object target, View view, Object provider) {
		inject(target, new ViewInjectHelperImp1(view), provider);
	}

	/**
	 * 进行注入
	 * 
	 * @param target 要注入视图和资源的目标对象
	 * @param activity 要注入视图所在Activity，&监听器提供者
	 */
	public static void inject(Object target, Activity activity) {
		inject(target, new ViewInjectHelperImp2(activity), activity);
	}

	/**
	 * 进行注入
	 * 
	 * @param target 要注入视图和资源的目标对象
	 * @param activity 要注入视图所在Activity
	 * @param provider 监听器提供者
	 */
	public static void inject(Object target, Activity activity, Object provider) {
		inject(target, new ViewInjectHelperImp2(activity), provider);
	}

	/**
	 * 进行注入
	 * 
	 * @param target 要注入的目标对象
	 * @param injectHelper 视图注入帮助
	 * @param provider 监听器提供者
	 */
	public static void inject(Object target, ViewInjectHelper injectHelper, Object provider) {
		ArrayList<Field> allFields = new ArrayList<>();
		Class<?> cls = target.getClass();
		while (cls != Object.class) {
			Field[] fields = cls.getDeclaredFields();
			if (fields != null) {
				allFields.addAll(Arrays.asList(fields));
			}
			cls = cls.getSuperclass();
		}
		Resources resources = injectHelper.getContext().getResources();

		for (Field field : allFields) {
			// 检查属性是否有视图注解
			AutoInitView aiv = field.getAnnotation(AutoInitView.class);
			if (aiv != null && (View.class == field.getType() || View.class.isAssignableFrom(field.getType()))) {
				if (!field.isAccessible()) {// 如果属性不能访问则修改访问权限
					field.setAccessible(true);
				}
				View view = injectHelper.findViewById(aiv.id());
				try {
					field.set(target, view);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (view != null) {
					if (aiv.click())
						view.setOnClickListener(getListener(View.OnClickListener.class, provider));
					if (aiv.longclick())
						view.setOnLongClickListener(getListener(View.OnLongClickListener.class, provider));
					if (aiv.touch())
						view.setOnTouchListener(getListener(View.OnTouchListener.class, provider));
					if (aiv.key())
						view.setOnKeyListener(getListener(View.OnKeyListener.class, provider));
					if (aiv.focuschange())
						view.setOnFocusChangeListener(getListener(View.OnFocusChangeListener.class, provider));
					if (aiv.contextmenu())
						view.setOnCreateContextMenuListener(getListener(View.OnCreateContextMenuListener.class, provider));
				}
				continue;
			}

			// 检查属性是否有资源注解
			AutoInitResource air = field.getAnnotation(AutoInitResource.class);
			if (air != null) {
				// Log.d(TAG, "field:" + field.getName() + "aRes:" + aRes + " resId:" + aRes.id());
				if (!field.isAccessible()) {// 如果属性不能访问则修改访问权限
					field.setAccessible(true);
				}
				try {
					field.set(target, getResource(resources, air, field.getName()));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static Object getResource(Resources resources, AutoInitResource air, String fieldName) {
		int resid = air.id();
		switch (air.type()) {
			case String: {
				return resources.getString(resid);
			}
			case StringArray: {
				return resources.getStringArray(resid);
			}
			case Integer: {
				return resources.getInteger(resid);
			}
			case IntArray: {
				return resources.getIntArray(resid);
			}
			default:
				break;
		}
		// 默认抛出异常
		throw new IllegalArgumentException("资源属性:" + fieldName + " 自动注入失败,检查资源id是否正确");
	}

	private static <T> T getListener(Class<T> cls, Object provider) {
		return cls.isAssignableFrom(provider.getClass()) ? cls.cast(provider) : null;
	}
}

/**
 * 视图注入帮助实现1
 * 
 * @author 李欣
 */
class ViewInjectHelperImp1 implements ViewInjectHelper {

	private View mView;

	public ViewInjectHelperImp1(View view) {
		mView = view;
	}

	@Override
	public View findViewById(int resId) {
		return mView.findViewById(resId);
	}

	@Override
	public Context getContext() {
		return mView.getContext();
	}

}

/**
 * 视图注入帮助实现2
 * 
 * @author 李欣
 */
class ViewInjectHelperImp2 implements ViewInjectHelper {

	private Activity mActivity;

	public ViewInjectHelperImp2(Activity activity) {
		mActivity = activity;
	}

	@Override
	public View findViewById(int resId) {
		return mActivity.findViewById(resId);
	}

	@Override
	public Context getContext() {
		return mActivity;
	}

}

/**
 * 视图注入帮助接口
 * 
 * @author 李欣
 */
interface ViewInjectHelper {

	/**
	 * 根据资源id返回视图
	 * 
	 * @param resId
	 * @return
	 */
	public View findViewById(int resId);

	/**
	 * 获取所在上下文
	 * 
	 * @return
	 */
	public Context getContext();
}