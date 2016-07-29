package com.eelly.sellerbuyer.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.eelly.framework.util.FileUtils;
import com.eelly.framework.util.StringUtil;
import com.eelly.sellerbuyer.net.FileDownloadAsyncTask;
import com.eelly.sellerbuyer.net.FileDownloadAsyncTask.FileDownloadListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map.Entry;

/**
 * 下载文件工具类<br>
 * <br>
 * 下载过的文件缓存于程序私有空间。可以添加多个下载文件，但只会一个个下载。<br>
 * 下载文件(加进队列){@link #loadFile(int, String, FileLoadingListener)}<br>
 * 取消下载某个文件{@link #cancel(int)}<br>
 * 取消所有文件{@link #cancelAll()} 退出当前activity时请调用<br>
 * 返回队列长度{@link #getQueueLength()}<br>
 * 返回是否在下载中{@link #isDownloading()}
 * 
 * @author 苏腾
 */
public class FileLoaderUtil {

	private Context context;

	private ArrayList<Item> queue;

	private Item curItem;

	private FileDownloadAsyncTask downTask;

	private static File path;

	private static int serialSeed = 0;

	private final static String XML_FILENAME = FileLoaderUtil.class.getName();

	public FileLoaderUtil(Context context) {
		this.context = context;
		queue = new ArrayList<>();
		initPath(context);
	}

	/**
	 * 加载url指定的文件
	 * 
	 * @param url
	 * @param listener
	 * @return 任务序列号serialID，成功添加进队列>0，url非法/已有重复<0
	 */
	public int loadFile(String url, FileLoadingListener listener) {
		if (url == null || url.length() == 0)
			return -1;
		for (Item item : queue) {
			if (item.url.equalsIgnoreCase(url))
				return -1;
		}
		int serialID;
		synchronized (FileLoaderUtil.class) {
			serialID = ++serialSeed;
		}
		queue.add(new Item(serialID, url, listener));
		try {
			next();
		}catch (Exception e){

		}
		return serialID;
	}

	/**
	 * 将本地文件拷贝到缓存目录，相当于已下载
	 * 
	 * @param context
	 * @param file 本地文件
	 * @param url 文件的"下载url"
	 * @return
	 */
	public static void copyFileToCache(Context context, File file, String url) {
		initPath(context);
		Item item = new Item(0, url, null);

		SharedPreferences sharedata = context.getSharedPreferences(XML_FILENAME, 0);
		String savedPath = sharedata.getString(item.md5, null);
		if (savedPath != null) {
			return;
		}

		InputStream fis = null;
		OutputStream fos = null;
		try {
			item.createTempFile();
			fis = new BufferedInputStream(new FileInputStream(file));
			fos = new BufferedOutputStream(new FileOutputStream(item.file));
			byte[] buf = new byte[1024];
			int i;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
			sharedata.edit().putString(item.md5, item.file.getAbsolutePath()).commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * 取消某个文件下载
	 * 
	 * @param serialID
	 */
	public void cancel(int serialID) {
		if (curItem != null && curItem.serialID == serialID && downTask != null) {
			downTask.cancel(true);
			downTask = null;
		}
		for (int i = 0; i < queue.size(); i++) {
			if (queue.get(i).serialID == serialID) {
				queue.remove(i);
				return;
			}
		}
	}

	/**
	 * 取消所有文件下载
	 */
	public void cancelAll() {
		if (downTask != null) {
			downTask.cancel(true);
			downTask = null;
		}
		queue.clear();
	}

	public int getQueueLength() {
		return queue.size();
	}

	public boolean isDownloading() {
		return queue.size() > 0 || downTask != null;
	}

	private void next() {
		if (queue.size() == 0 || downTask != null)
			return;
		curItem = queue.remove(0);

		SharedPreferences sharedata = context.getSharedPreferences(XML_FILENAME, 0);
		String savedPath = sharedata.getString(curItem.md5, null);
		if (savedPath != null) {
			curItem.file = new File(savedPath);
			if (curItem.file.exists()) {
				if (curItem.listener != null)
					curItem.listener.onComplete(curItem.serialID, curItem.file);
				next();
				return;
			}
		}
		try {
			curItem.createTempFile();
		} catch (IOException e) {
			throw new RuntimeException("Couldn't create file on SD card", e);
		}
		downTask = new FileDownloadAsyncTask(curItem.url, curItem.file, listener);
		downTask.execute();
	}

	private FileDownloadListener listener = new FileDownloadListener() {

		@Override
		public void onStart() {
			if (curItem.listener != null)
				curItem.listener.onStart(curItem.serialID);
		}

		@Override
		public void onProgress(int progress) {

		}

		@Override
		public void onFinish() {
			SharedPreferences.Editor sharedata = context.getSharedPreferences(XML_FILENAME, 0).edit();
			sharedata.putString(curItem.md5, curItem.file.getAbsolutePath());
			sharedata.commit();
			if (curItem.listener != null)
				curItem.listener.onComplete(curItem.serialID, curItem.file);
			downTask = null;
			next();
		}

		@Override
		public void onFail() {
			if (curItem.listener != null)
				curItem.listener.onFail(curItem.serialID);
			downTask = null;
		}

		@Override
		public void onCancelled() {
			if (curItem.listener != null)
				curItem.listener.onCancel(curItem.serialID);
			downTask = null;
		}
	};

	/**
	 * 下载文件类
	 * 
	 * @author 苏腾
	 */
	public static class Item {

		int serialID;

		String url;

		FileLoadingListener listener;

		String md5;

		File file;

		public Item(int serialID, String url, FileLoadingListener listener) {
			this.serialID = serialID;
			this.url = url;
			this.listener = listener;
			md5 = StringUtil.md5(url.toLowerCase(Locale.getDefault()));
		}

		public void createTempFile() throws IOException {
			String suffix = null;
			int p = url.lastIndexOf(".");
			if (p > 0)
				suffix = url.substring(p);
			file = File.createTempFile(md5, suffix, path);
		}

	}

	/**
	 * 下载状态监听器
	 * 
	 * @author 苏腾
	 */
	public static interface FileLoadingListener {

		void onStart(int serialID);

		void onFail(int serialID);

		void onComplete(int serialID, File file);

		void onCancel(int serialID);

	}

	private static void initPath(Context context) {
		if (path == null) {
			path = new File(context.getCacheDir() + "/filecache/");
			path.mkdirs();
		}
	}

	/**
	 * 获取缓存大小<br>
	 * 有可能包含不是FileLoaderUtil缓存的文件
	 */
	public static long getCacheSize() {
		return FileUtils.getDirSize(path, false);
	}

	/**
	 * 清除缓存
	 * 
	 * @param context
	 */
	public static void clearCache(Context context) {
		SharedPreferences sharedata = context.getSharedPreferences(XML_FILENAME, 0);
		for (Entry<String, ?> entry : sharedata.getAll().entrySet()) {
			File file = new File(entry.getValue().toString());
			if (file.exists())
				file.delete();
		}
		sharedata.edit().clear().commit();
	}
}
