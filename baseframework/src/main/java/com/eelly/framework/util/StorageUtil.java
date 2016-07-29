package com.eelly.framework.util;

import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;

import java.io.File;

public class StorageUtil {

	/**
	 * 获取用户目录
	 * @return
	 */
	static public File getUsesrDir(){
		return Environment.getDataDirectory();
	}	
	
	/**
	 * 检查外部存储设备是否可写
	 * @return
	 */
	static public boolean externalStorageWriteable(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * 检查外部存储设备指定路径是否可写
	 * @param path 检查路径
	 * @return
	 */
	static public boolean externalStorageWriteable(File path){
		return EnvironmentCompat.getStorageState(path).equals(Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * 返回外部存储设备状态
	 * @return one of MEDIA_UNKNOWN, MEDIA_REMOVED, MEDIA_UNMOUNTED, MEDIA_CHECKING, MEDIA_NOFS, MEDIA_MOUNTED, MEDIA_MOUNTED_READ_ONLY, MEDIA_SHARED, MEDIA_BAD_REMOVAL, or MEDIA_UNMOUNTABLE
	 */
	static public String getExternalStorageState(){
		return Environment.getExternalStorageState();
	}
	
	/**
	 * 返回外部存储设备指定路径状态
	 * @return one of MEDIA_UNKNOWN, MEDIA_REMOVED, MEDIA_UNMOUNTED, MEDIA_CHECKING, MEDIA_NOFS, MEDIA_MOUNTED, MEDIA_MOUNTED_READ_ONLY, MEDIA_SHARED, MEDIA_BAD_REMOVAL, or MEDIA_UNMOUNTABLE
	 */
	static public String getExternalStorageState(File path){
		return EnvironmentCompat.getStorageState(path);
	}
	
	/**
	 * 获取下载缓冲目录
	 * @return {@link File}
	 */
	static public File getDownloadCacheDir(){
		return Environment.getDownloadCacheDirectory();
	}
	
	/**
	 * 获取外置存储设备目录,不能被其他应用访问,不会随着应用卸载而删除 (多个用户设备每个用户独立)
	 * @return
	 */
	static public File getExternalDir(){
		return Environment.getExternalStorageDirectory();
	}
	
	/**
	 * 获取外置存储设备目录,不能被其他应用访问,不会随着应用卸载而删除 (多个用户设备每个用户独立)
	 * @return
	 */
	static public File getExternalDir(String path){
		return new File(Environment.getExternalStorageDirectory().getPath(),path);
	}
	
	/**
	 * 获取外置存储设置公开目录,可被其他应用访问，不会随着应用卸载而删除 (多个用户设备每个用户独立)
	 * @param type
	 * @return
	 */
	static public File getExternalPublicDir(String type){
		return Environment.getExternalStoragePublicDirectory(type);
	}
	
	/**
	 * 系统提醒铃声存放的标准目录
	 * @return
	 */
	static public File getDirALARMS(){
		return getExternalPublicDir(Environment.DIRECTORY_ALARMS);
	}
	
	/**
	 * 相机拍摄照片和视频的标准目录
	 * @return
	 */
	static public File getDirDCIM(){
		return getExternalPublicDir(Environment.DIRECTORY_DCIM);
	}
	
	/**
	 * 音乐存放的标准目录
	 * @return
	 */
	static public File getDirMUSIC(){
		return getExternalPublicDir(Environment.DIRECTORY_MUSIC);
	}
	
	/**
	 * 下载的标准目录
	 * @return
	 */
	static public File getDirDOWNLOADS(){
		return getExternalPublicDir(Environment.DIRECTORY_DOWNLOADS);
	}
	
	/**
	 * 图片存放的标准目录
	 * @return
	 */
	static public File getDirPICTURES(){
		return getExternalPublicDir(Environment.DIRECTORY_PICTURES);
	}
	
	/**
	 * 电影存放的标准目录
	 * @return
	 */
	static public File getDirMOVIES(){
		return getExternalPublicDir(Environment.DIRECTORY_MOVIES);
	}
	
	/**
	 * 系统通知铃声存放的标准目录
	 * @return
	 */
	static public File getDirNOTIFICATIONS(){
		return getExternalPublicDir(Environment.DIRECTORY_NOTIFICATIONS);
	}
	
	/**
	 * 系统广播存放的标准目录
	 * @return
	 */
	static public File getDirPODCASTS(){
		return getExternalPublicDir(Environment.DIRECTORY_PODCASTS);
	}
	
	/**
	 * 系统铃声存放的标准目录
	 * @return
	 */
	static public File getDirRINGTONES(){
		return getExternalPublicDir(Environment.DIRECTORY_RINGTONES);
	}
	
}
