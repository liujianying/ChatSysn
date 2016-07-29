package com.eelly.sellerbuyer.net;

import android.text.TextUtils;

import com.eelly.framework.util.EncryptUtil;
import com.eelly.framework.util.IOUtil;
import com.eelly.framework.util.LogUtil;
import com.eelly.framework.util.StringUtil;
import com.eelly.sellerbuyer.util.ImageDeflateUtil;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * 新接口系统<br>
 * 文件上传任务
 * 
 * @author 林钊平
 */
public class NewFileUploadAsyncTask extends BaseFileAsyncTask<Void, Integer, String> {

	private final static String TAG = "NewFileUploadAsyncTask";

	/**
	 * POST提交数据分隔符
	 */
	public static final String BOUNDARY = "7cd4a6d158c";

	/**
	 * 数据分隔开始字符
	 */
	public static final String MP_BOUNDARY = "--" + BOUNDARY;

	/**
	 * 数据分隔结束字符
	 */
	public static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";

	/**
	 * 提交内容类型Http头信息
	 */
	public static final String CONTENT_TYPE = "multipart/form-data; boundary=" + BOUNDARY;

	/**
	 * 文件上传地址
	 */
	private String mUploadUrl;

	/**
	 * 上传文件数组(1个key可对应多个文件)
	 */
	private HashMap<String, Object> mUploadFiles = new HashMap<>();

	/**
	 * 上传监听器
	 */
	private FileUploadListener mListener;

	/**
	 * 上传进度监听器
	 */
	private FileUploadProgressListener mProgressListener;

	/**
	 * 服务端错误信息
	 */
	private String mErrorMsg = null;

	/**
	 * Http状态码
	 */
	private int mHttpCode = 0;

	/**
	 * 图片压缩工具
	 */
	private ImageDeflateUtil mImageDeflator;

	/**
	 * 新版接口系统的服务参数
	 */
	private HashMap<String, String> mServerParams;

	/**
	 * 3Des加密用的向量
	 */
	private String mEncryptIv = "";

	/**
	 * 3Des加密用的密钥
	 */
	private String mEncryptKey = "";

	private boolean isParseResult = false;

	/**
	 * @param uploadUrl 上传地址
	 * @param listener 上传监听器
	 */
	public NewFileUploadAsyncTask(String uploadUrl, FileUploadListener listener) {
		mUploadUrl = uploadUrl;
		mListener = listener;
		init();
	}

	/**
	 * @param uploadUrl 上传地址
	 * @param isParse 是否解析结果,true在FileUploadListener的onFinish方法只返回url，否则返回整个加密结果
	 * @param listener 上传监听器
	 */
	public NewFileUploadAsyncTask(String uploadUrl, boolean isParse, FileUploadListener listener) {
		mUploadUrl = uploadUrl;
		isParseResult = isParse;
		mListener = listener;
		init();
	}

	/**
	 * @param uploadUrl 上传地址
	 * @param fieldName 上传文件字段名
	 * @param uploadFile 上传文件
	 * @param listener 上传监听器
	 */
	public NewFileUploadAsyncTask(String uploadUrl, String fieldName, File uploadFile, FileUploadListener listener) {
		mUploadUrl = uploadUrl;
		mUploadFiles.put(fieldName, uploadFile);
		mListener = listener;
		init();
	}

	private void init() {
		mEncryptIv = StringUtil.getRandomString(8);
		mServerParams = new HashMap<>();
	}

	public void setProgressListener(FileUploadProgressListener listener) {
		this.mProgressListener = listener;
	}

	/**
	 * 添加上传文件
	 * 
	 * @param fieldName 文件对应的上传字段名
	 * @param uploadFile 要上传的文件
	 */
	public void addUploadFile(String fieldName, File uploadFile) {
		Object obj = mUploadFiles.get(fieldName);
		if (obj != null) {// 如果上传字段已存在
			if (obj instanceof File) {// 如果值为文件对象则改为数组
				ArrayList<File> files = new ArrayList<>();
				files.add((File) obj);
				files.add(uploadFile);
				mUploadFiles.put(fieldName, files);
			} else {// 否则加入上传数组
				ArrayList<File> files = (ArrayList<File>) obj;
				files.add(uploadFile);
			}
		} else {
			mUploadFiles.put(fieldName, uploadFile);
		}
	}

	/**
	 * 添加一组上传文件
	 * 
	 * @param fieldFormat 上传字段格式串，会调用 String.format(fieldFormat, i) 生成最终字段名
	 * @param uploadFiles 上传文件列表
	 */
	public void addUploadFiles(String fieldFormat, List<File> uploadFiles) {
		for (int i = 0; i < uploadFiles.size(); i++) {
			addUploadFile(String.format(fieldFormat, i), uploadFiles.get(i));
		}
	}

	/**
	 * 设置加密密钥
	 * 
	 * @param key
	 */
	public void setEncryptKey(String key) {
		mEncryptKey = key;
	}

	/**
	 * 获取加密密钥
	 * 
	 * @return
	 */
	public String getEncryptKey() {
		return mEncryptKey;
	}

	/**
	 * 获取加密向量
	 * 
	 * @return
	 */
	public String getEncryptIv() {
		return mEncryptIv;
	}

	/**
	 * 服务系统
	 * 
	 * @param app
	 */
	public void setApp(String app) {
		mServerParams.put("app", app);
	}

	/**
	 * 服务名
	 * 
	 * @param serverName
	 */
	public void setServerName(String serverName) {
		mServerParams.put("service_name", serverName);
	}

	/**
	 * 上图中的接口名
	 * 
	 * @param method
	 */
	public void setMethod(String method) {
		mServerParams.put("method", method);
	}

	/**
	 * 时间戳
	 * 
	 * @param time
	 */
	public void setTime(String time) {
		mServerParams.put("time", time);
	}

	/**
	 * 请求参数
	 * 
	 * @param args 请求的model对象转为json字符串
	 */
	public void setArgs(String args) {
		mServerParams.put("args", args);
	}

	public void setIMEI(String imei) {
		mServerParams.put("deviceNumber", imei);
	}

	/**
	 * 是否返回解析返回结果<br>
	 * 
	 * @param isParse true在FileUploadListener的onFinish方法只返回url，否则返回整个加密结果
	 */
	public void setIsParseResult(boolean isParse) {
		this.isParseResult = isParse;
	}

	/**
	 * 允许压缩上传的图片
	 * 
	 * @param options 压缩选项，null则取默认值
	 * @param tempPath 压缩图片存储目录，null则保存于图片同目录
	 */
	public void enableImageDeflate(ImageDeflateUtil.DeflateOptions options, File tempPath) {
		mImageDeflator = new ImageDeflateUtil(options, tempPath);
	}

	@Override
	protected void onPreExecute() {
		mListener.onStart();
	}

	private String getData() {
		HashMap<String, Object> map = getParams();
		try {
			Gson gson = new Gson();
			// String jsonStr = gson.toJson(map);
			// mServerParams.put("args", TextUtils.isEmpty(jsonStr) ? "{}" : jsonStr);
			String dataStr = gson.toJson(mServerParams);
			String encryptStr = EncryptUtil.des3EncodeCBC(mEncryptKey, mEncryptIv, dataStr);
			String data = ApiEncrypte.postParams(mEncryptIv, encryptStr);
			return data;
		} catch (Exception e) {
			LogUtil.d(TAG, "加密失败");
		}
		return "";
	}

	@Override
	protected String doInBackground(Void... args) {
		InputStream input = null;
		OutputStream output = null;
		FileInputStream fileInput = null;
		try {

			// 压缩图片
			if (mImageDeflator != null) {
				mImageDeflator.deflate(mUploadFiles);
			}

			URL fileUrl = new URL(mUploadUrl);
			HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
			conn.setConnectTimeout(getConnectTimeout());
			conn.setReadTimeout(getReadTimeout());
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			// conn.setUseCaches(false);
			// conn.setChunkedStreamingMode(0);
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Charsert", ENCODE);
			conn.setRequestProperty("Content-Type", CONTENT_TYPE);

			// 设置自定义Http头信息
			HashMap<String, String> headers = getHeaders();
			if (headers != null) {
				for (Entry<String, String> header : headers.entrySet()) {
					conn.setRequestProperty(header.getKey(), header.getValue());
				}
			}
			// 进行Http连接
			conn.connect();

			// Http输出流
			output = new BufferedOutputStream(conn.getOutputStream());

			// // 写入要上传的文本信息
			// HashMap<String, Object> params = getParams();
			// if (params != null) {
			// for (Entry<String, Object> one : params.entrySet()) {
			// String param = one.getKey();
			// Object obj = one.getValue();
			// if (obj instanceof String) {
			// writeParam(param, String.valueOf(obj), output);
			// } else {
			// ArrayList<String> values = (ArrayList<String>) obj;
			// for (String value : values) {
			// writeParam(param, value, output);
			// }
			// }
			// /*
			// * StringBuilder temp = new StringBuilder(10); temp.append(MP_BOUNDARY).append("\r\n");
			// * temp.append("content-disposition: form-data; name=\"").append(one.getKey()).append("\"\r\n\r\n");
			// * temp.append(one.getValue()).append("\r\n"); output.write(temp.toString().getBytes());
			// */
			// }
			// }
			writeParam("data", getData(), output);

			int uploaded = 0;

			// 判断是否有需要上传的文件
			if (mUploadFiles.size() > 0) {
				// 开始上传文件
				byte[] bufferOut = new byte[100000];
				LogUtil.d(TAG, "upload start");
				for (Entry<String, Object> entry : mUploadFiles.entrySet()) {
					// 如果已取消则退出
					if (isCancelled())
						return null;
					String fieldName = entry.getKey();
					Object obj = entry.getValue();
					if (obj instanceof File) {
						LogUtil.d(TAG, "uploading file fieldName:" + fieldName);
						File uploadFile = (File) obj;
						fileInput = new FileInputStream(uploadFile);
						// 如果已取消则退出
						if (!writeFile(fieldName, uploadFile.getName(), uploadFile.length(), fileInput, output, bufferOut)) {
							return null;
						}
						IOUtil.closeQuietly(fileInput);
						uploaded++;
						mListener.onProgress(uploadFile, uploaded, mUploadFiles.size());
					} else {
						ArrayList<File> uploadFiles = (ArrayList<File>) obj;
						for (File uploadFile : uploadFiles) {
							LogUtil.d(TAG, "uploading array fieldName:" + fieldName);
							// 如果已取消则退出
							if (isCancelled())
								return null;
							fileInput = new FileInputStream(uploadFile);
							// 如果已取消则退出
							if (!writeFile(fieldName, uploadFile.getName(), uploadFile.length(), fileInput, output, bufferOut)) {
								return null;
							}
							IOUtil.closeQuietly(fileInput);
							uploaded++;
							mListener.onProgress(uploadFile, uploaded, uploadFiles.size());
						}
					}

				}
			}
			// 文件数据块结束分隔符
			StringBuilder temp = new StringBuilder(10);
			temp.append(END_MP_BOUNDARY + "\r\n");
			output.write(temp.toString().getBytes());
			output.flush();
			output.close();

			input = conn.getInputStream();
			mHttpCode = conn.getResponseCode();
			if (mHttpCode != HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				temp = new StringBuilder(10);
				String line = null;
				while ((line = reader.readLine()) != null) {
					temp.append(line);
				}
				reader.close();
				mErrorMsg = temp.toString();
				return null;
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				temp = new StringBuilder(10);
				String line = null;
				while ((line = reader.readLine()) != null) {
					temp.append(line);
				}
				reader.close();
			}
			LogUtil.d(TAG, "upload end");

			// 清理压缩过的图片。因为FileUploadAsyncTask没有失败重试的机制，重试都会再new一个FileUploadAsyncTask重新设置上传文件，所以这里删掉文件也无妨
			if (mImageDeflator != null) {
				mImageDeflator.clean();
			}

			return temp.toString();
		} catch (Exception e) {
			mErrorMsg = e.toString();
			e.printStackTrace();
			LogUtil.d(TAG, e.toString());
		} finally {
			IOUtil.closeQuietly(output);
			IOUtil.closeQuietly(fileInput);
			IOUtil.closeQuietly(input);
		}
		return null;
	}

	/**
	 * 生成请求参数字节数组
	 * 
	 * @param param
	 * @param value
	 * @return
	 * @throws IOException
	 */
	private void writeParam(String param, String value, OutputStream output) throws IOException {
		StringBuilder temp = new StringBuilder(10);
		temp.append(MP_BOUNDARY).append("\r\n");
		temp.append("content-disposition: form-data; name=\"").append(param).append("\"\r\n\r\n");
		temp.append(value).append("\r\n");
		output.write(temp.toString().getBytes());
	}

	/**
	 * @param fieldName 上传字段名
	 * @param fileName 上传文件名
	 * @param fileSize 文件大小
	 * @param fileInput 文件输入流
	 * @param output 网络输出流
	 * @param bufferOut 流缓存字节数组
	 * @return true:任务继续 false:任务被终止
	 * @throws IOException
	 */
	private boolean writeFile(String fieldName, String fileName, float fileSize, FileInputStream fileInput, OutputStream output, byte[] bufferOut) throws IOException {
		LogUtil.d(TAG, "fieldName:" + fieldName + " fileName:" + fileName + " fileSize:" + fileSize);
		StringBuilder temp = new StringBuilder(10);
		// 文件数据块开始分隔符
		temp.append(MP_BOUNDARY).append("\r\n");
		temp.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"").append(fileName).append("\"\r\n");
		temp.append("Content-Type: ").append("application/octet-stream").append("\r\n\r\n");
		output.write(temp.toString().getBytes());

		// 写入上传文件
		int len = 0;
		// 已上传字节数
		long uploadedSize = 0;
		while ((len = fileInput.read(bufferOut)) != -1) {
			// 如果已取消则退出
			if (isCancelled())
				return false;
			output.write(bufferOut, 0, len);
			uploadedSize += len;
			LogUtil.d(TAG, "fieldName:" + fieldName + " fileName:" + fileName + " uploadedSize:" + uploadedSize + " uploaded:"
					+ ((int) ((uploadedSize / fileSize) * 100)) + "%");
			publishProgress(((int) ((uploadedSize / fileSize) * 100)));
		}
		output.write("\r\n".getBytes());
		output.flush();
		return true;
	}

	/*
	 * @Override protected void onProgressUpdate(Integer... values) { mListener.onProgress(values[0]); }
	 */

	@Override
	protected void onPostExecute(String result) {
		if (result == null) {
			mListener.onFail(mHttpCode, mErrorMsg);
		} else {
			if (!TextUtils.isEmpty(result) && isParseResult) {
				ApiReponseModel model = parseData(result);
				String url = model.getData();
				mListener.onFinish(url);
				return;
			}
			mListener.onFinish(result);
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		if(mProgressListener != null && values != null) {
			mProgressListener.onProgress(values[0]);
		}

	}

	@Override
	protected void onCancelled() {
		mListener.onCancelled();
	}

	public static interface FileUploadProgressListener {

		public void onProgress(int progress);

	}

	public static interface FileUploadListener {

		/**
		 * 上传开始
		 */
		public void onStart();

		/**
		 * 上传进度(在非主线程回调)
		 * 
		 * @param file 刚上传的文件
		 * @param uploaded 已上传了文件个数
		 * @param fileCount 总文件个数
		 */
		public void onProgress(File file, int uploaded, int fileCount);

		/**
		 * 上传完成
		 * 
		 * @param result 服务端返回结果
		 */
		public void onFinish(String result);

		/**
		 * 上传失败
		 * 
		 * @param httpCode Http状态码
		 * @param errorMsg 错误信息
		 */
		public void onFail(int httpCode, String errorMsg);

		/**
		 * 上传取消
		 */
		public void onCancelled();
	}

	/**
	 * 解析返回的数据
	 * 
	 * @param result 服务端返回的结果
	 * @return
	 */
	public ApiReponseModel parseData(String result) {
		ApiReponseModel responseModel = null;
		try {
			responseModel = new Gson().fromJson(result, ApiReponseModel.class);
			String dataStr = responseModel.getData();
			String data = EncryptUtil.des3DecodeCBC(mEncryptKey, mEncryptIv, dataStr);
			responseModel.setData(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseModel;
	}

}
