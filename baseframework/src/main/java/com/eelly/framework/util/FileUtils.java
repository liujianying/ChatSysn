package com.eelly.framework.util;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件操作工具包
 * 
 * @author xubing
 * @date 2013年11月19日
 */
public class FileUtils {

	/**
	 * 获取文件扩展名
	 * 
	 * @param file
	 * @return 扩展名或null
	 */
	public static String getFileExt(String file) {
		if (file == null) {
			return null;
		}
		if (file.endsWith(".")) {
			return null;
		}
		int index = file.lastIndexOf(".");
		if (index == -1) {
			return null;
		}
		return file.substring(index + 1);
	}

	/**
	 * 向手机写图片
	 * 
	 * @param buffer
	 * @param folder
	 * @param fileName
	 * @return
	 */
	public static void writeFile(byte[] buffer, String filePath) {
		File file = new File(filePath);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 * 根据自定义文件夹名、文件名创建文件路径
	 * 
	 * @param folder 自定义文件夹
	 * @param fileName 自定义文件名
	 * @return
	 */
	public static String getFilePath(Activity mActivity, String folder, String fileName) {

		String folderPath = "";
		if (hasSDCard()) {
			folderPath = getExtPath() + File.separator + folder + File.separator;
		} else {
			folderPath = getPackagePath(mActivity) + File.separator + folder + File.separator;
		}

		File fileDir = new File(folderPath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		return folderPath + fileName;
	}

	/**
	 * 判断是否有sdcard
	 * 
	 * @return
	 */
	public static boolean hasSDCard() {
		boolean b = false;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			b = true;
		}
		return b;
	}

	/**
	 * 得到sdcard路径
	 * 
	 * @return
	 */
	public static String getExtPath() {
		if (checkSaveLocationExists()) {
			return Environment.getExternalStorageDirectory().getPath();
		}
		return "";
	}

	/**
	 * 得到/data/data/yanbin.imagedownload目录
	 * 
	 * @param mActivity
	 * @return
	 */
	public static String getPackagePath(Activity mActivity) {
		return mActivity.getFilesDir().toString();
	}

	/**
	 * 检查是否安装SD卡
	 * 
	 * @return
	 */
	public static boolean checkSaveLocationExists() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取文件MD5
	 * 
	 * @param filename
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String fileMD5(String filename) throws FileNotFoundException, IOException {
		FileInputStream fis = null;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		try {
			fis = new FileInputStream(filename);
			MessageDigest digest = MessageDigest.getInstance("MD5");
			while ((numRead = fis.read(buffer)) > 0) {
				digest.update(buffer, 0, numRead);
			}
			fis.close();
			return StringUtil.toHexString(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			fis.close();
		}
		return null;
	}

	/**
	 * 删除目录里的所有文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteDirectory(String path) {
		File file = new File(path);
		if (!file.exists() || !file.isDirectory()) {
			return false;
		}
		for (File temp : file.listFiles()) {
			if (temp.isFile()) {
				temp.delete();
			}
		}
		return true;
	}

	/**
	 * 返回带 file:// 前缀的路径
	 * 
	 * @param file
	 * @return
	 */
	public static String getSchemePath(File file) {
		// 可能有中文，所以要decode一次
		return Uri.decode(Uri.fromFile(file).toString());
	}

	/**
	 * 拷贝文件
	 * 
	 * @param source 源文件
	 * @param target 目标地址
	 * @return 是否成功
	 */
	public static boolean copyFile(File source, String target) {
		if (source == null || !source.exists() || !source.isFile() || target == null || target.length() == 0) {
			return false;
		}
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			File parent = new File(target).getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			inputStream = new FileInputStream(source);
			outputStream = new FileOutputStream(target);
			byte[] buffer = new byte[1024];
			int count;
			while ((count = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, count);
			}
			inputStream.close();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			IOUtil.closeQuietly(inputStream);
			IOUtil.closeQuietly(outputStream);
		}
		return true;
	}

	/**
	 * 计算文件夹总大小
	 * 
	 * @param dir
	 * @param containsSubDir 是否包含子文件夹
	 * @return 出错或不存在返回0
	 */
	public static long getDirSize(File dir, boolean containsSubDir) {
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			return 0;
		}
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			return 0;
		}
		long size = 0;
		for (File file : files) {
			if (file.isFile()) {
				size += file.length();
			} else if (file.isDirectory() && containsSubDir) {
				size += getDirSize(file, containsSubDir);
			}
		}
		return size;
	}
}