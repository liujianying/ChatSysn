package com.eelly.framework.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;

/**
 * 性能检测类
 * @author lx
 *
 */
public class TestUtil {

	/**
	 * 打开当前Activity UI线程性能监控
	 * -磁盘读写
	 * -网络访问
	 * -数据库慢操作
	 */
	@TargetApi(9)
	public static void openStrictModeThreadPolicy(){
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO){
			StrictMode.setThreadPolicy(new ThreadPolicy.Builder().detectAll().penaltyLog().build());
		}
	}

	/**
	 * 打开当前虚拟机进程性能监控
	 * -实现了java.io.Closeable接口但未关闭的资源对象
	 * -当前上下文已退出但未注销的BroadcastReceiver或ServiceConnection
	 * -没有及时关闭的android.database.sqlite.SQLiteCursor或其他SQLite对象 
	 */
	@TargetApi(9)
	public static void openStrictModeVmPolicy(){
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO){
			StrictMode.setVmPolicy(new VmPolicy.Builder().detectAll().build());
		}
	}
	
}
