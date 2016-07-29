package com.eelly.framework.app;

import android.content.Context;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashSet;
/**
 * 异常处理类
 * @author 李欣
 *
 */
public class ExceptionHandler implements UncaughtExceptionHandler {
	/**
	 * 异常处理对象单例
	 */
	private static ExceptionHandler mHandler;
	/**
	 * ApplactionContext
	 */
	private Context mContext;

	/**
	 * 系统默认未捕获异常处理对象
	 */
	private UncaughtExceptionHandler mDefaultUncaughtHandler;
	
	/**
	 * 异常监听器集合
	 */
	private HashSet<ExceptionHandlerListener> mListeners=new HashSet<ExceptionHandlerListener>();

	/**
	 * 根据ApplicationContext创建异常处理类对象
	 * @param context
	 */
	private ExceptionHandler(Context context){
		mContext = context;
		mDefaultUncaughtHandler = Thread.getDefaultUncaughtExceptionHandler();
		//将本对象注册为默认未捕获监听器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	/**
	 * 获取异常处理对象
	 * @param context
	 * @return
	 */
	public static ExceptionHandler getInstance(Context context){
		if(mHandler==null){
			mHandler = new ExceptionHandler(context.getApplicationContext());
		}
		return mHandler;
	}
	
	/**
	 * 未捕获异常处理函数
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if(mListeners.isEmpty()){//如果监听器为空则转发给系统默认处理类
			mDefaultUncaughtHandler.uncaughtException(thread, ex);
		}else{
			//转发未捕获异常到所有注册监听器
			for(ExceptionHandlerListener listener:mListeners){
				listener.onUncaughtException(mContext, thread, ex);
			}
		}
	}

	/**
	 * 已捕获异常处理函数
	 * @param ex
	 */
	public void handlerException(Exception ex){
		//转发捕获异常到所有注册监听器
		for(ExceptionHandlerListener listener:mListeners){
			listener.onException(ex);
		}
	}
	
	/**
	 * 注册异常监听器
	 * @param listener
	 */
	public void addListener(ExceptionHandlerListener listener) {
		mListeners.add(listener);
	}
	
	/**
	 * 移除异常监听器
	 * @param listener
	 */
	public void removeListener(ExceptionHandlerListener listener){
		mListeners.remove(listener);
	}
	
	/**
	 * 异常监听器
	 * @author 李欣
	 *
	 */
	public static abstract class ExceptionHandlerListener {
		
		/**
		 * 未捕获异常
		 * @param thread
		 * @param ex
		 * @return
		 */
		protected void onUncaughtException(Context context,Thread thread, Throwable ex){

		}
		
		/**
		 * 已捕获异常
		 * @param ex
		 */
		protected void onException(Exception ex){
			
		}
		
	}
	
}
