package com.eelly.sellerbuyer.net;

import android.net.Uri;

import com.eelly.framework.util.IOUtil;
import com.eelly.framework.util.LogUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * 文件下载任务
 * @author 李欣
 *
 */
public class FileDownloadAsyncTask extends BaseFileAsyncTask<Void, Integer, Boolean> {

	private final static String TAG = "FileDownloadAsyncTask";

	/**
	 * 要下载的文件地址
	 */
	private String mFileUrl;

	/**
	 * 下载文件保存到的本地文件
	 */
	private File mSaveFile;

	/**
	 * 下载监听器
	 */
	private FileDownloadListener mListener;

	/**
	 * @param url 文件下载地址
	 * @param saveFile 文件保存路径
	 * @param listener 下载监听器
	 */
	public FileDownloadAsyncTask(String url, File saveFile, FileDownloadListener listener) {
		mFileUrl = url;
		mListener = listener;
		mSaveFile = saveFile;
	}
	
	private String getRequestUrl() {
		HashMap<String, Object> params = getParams();
		if (params != null) {
			// 检查并自动补全地址参数开始符号
			if (mFileUrl.indexOf("?") == -1) {
				mFileUrl += "?";
			}
			StringBuilder encodedUrl = new StringBuilder(mFileUrl);
			try {
				for (Entry<String, Object> entry : params.entrySet()) {
					String param = entry.getKey();
					Object obj = entry.getValue();
					
					if(obj instanceof String){
						appendUrlParam(param, String.valueOf(obj), encodedUrl);
					}else{
						ArrayList<String> values = (ArrayList<String>)obj;
						for(String value:values){
							appendUrlParam(param, value, encodedUrl);
						}
					}
				}
				return encodedUrl.toString();
			} catch (UnsupportedEncodingException ex) {
				throw new RuntimeException("Encoding not supported ", ex);
			}
		}
		return mFileUrl;
	}

	/**
	 * 将参数加入Url中
	 * @throws UnsupportedEncodingException 
	 */
	private void appendUrlParam(String param, String value, StringBuilder sb) throws UnsupportedEncodingException{
		sb.append('&');
		sb.append(param);
		sb.append('=');
		if (getMethod() == METHOD.GET) { //如果是GET请求方式则对参数进行编码,防止中文乱码
			value = URLEncoder.encode(value, ENCODE);
		}
		sb.append(value);
	}
	
	@Override
	protected void onPreExecute() {
		mListener.onStart();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		InputStream input = null;
		BufferedWriter buffOutput = null;
		FileOutputStream output = null;
		try {
			String requestUrl = getRequestUrl();
			URL fileUrl = new URL(requestUrl);
			HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
			conn.setConnectTimeout(getConnectTimeout());
			conn.setReadTimeout(getReadTimeout());
			conn.setDoInput(true);
			
			// 设置自定义Http头信息
			HashMap<String, String> headers = getHeaders();
			if (headers != null) {
				for (Entry<String, String> header : headers.entrySet()) {
					conn.setRequestProperty(header.getKey(), header.getValue());
				}
			}
			
			// 如果请求方法为POST
			if(getMethod() == METHOD.POST){
//				Log.d(TAG, "METHOD POST:" + fileUrl.getQuery());
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				buffOutput = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), ENCODE));
				String query = fileUrl.getQuery();
				if(query!=null) buffOutput.write(query);
				buffOutput.flush();
			}
			
			// 开始Http连接
			conn.connect();
			// 获取Http输入流
			input = conn.getInputStream();

			// 获取文件字节大小
			float fileLength = conn.getContentLength();
			// Log.d(TAG, "fileLength:" + fileLength);
			if (mSaveFile.isDirectory()) {// 如果是目录则自动根据Url获取文件名
				// 根据Url获取文件名
				String fileName = Uri.parse(mFileUrl).getLastPathSegment();
				// 将文件名与目录组合
				mSaveFile = new File(mSaveFile, fileName);
			}

//			LogUtil.d(TAG, "down file save:" + mSaveFile.getAbsolutePath() + " len:" + mSaveFile.length());

			// 文件输出流
			output = new FileOutputStream(mSaveFile);

			byte[] data = new byte[4096];
			// 已下载的字节数
			int count = 0;
			// 单次下载的字节数
			int inputLen = 0;

			while ((inputLen = input.read(data)) != -1) {
				// 如果已取消则退出
				if (isCancelled())
					return false;
				output.write(data, 0, inputLen);
				count += inputLen;
				// 将已下载的文件长度发送至onProgressUpdate方法用以更新进度条UI
				publishProgress((int) ((count / fileLength) * 100));
			}
			return true;
		} catch (Exception e) {
			LogUtil.d(TAG, e.toString());
		} finally {
			IOUtil.closeQuietly(input);
			IOUtil.closeQuietly(output);
			IOUtil.closeQuietly(buffOutput);
		}
		return false;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		if (values[0] % 10 == 0)
			mListener.onProgress(values[0]);
	}

	@Override
	protected void onPostExecute(Boolean success) {
		if (success) {
			mListener.onFinish();
		} else {
			mListener.onFail();
		}
	}

	@Override
	protected void onCancelled() {
		mListener.onCancelled();
	}

	public static interface FileDownloadListener {

		/**
		 * 开始下载
		 */
		public void onStart();

		/**
		 * 下载进度
		 * 
		 * @param progress
		 */
		public void onProgress(int progress);

		/**
		 * 下载完成
		 */
		public void onFinish();

		/**
		 * 下载失败
		 */
		public void onFail();

		/**
		 * 下载取消
		 */
		public void onCancelled();
	}
}
