package com.eelly.sellerbuyer.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;

import com.eelly.framework.util.IOUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * 图片缩减工具<br>
 * 用于上传图片时，缩小图片、降低图片质量、减小文件大小
 * 
 * @author 苏腾
 */
public class ImageDeflateUtil {

	private static final String[] IMAGE_SUFFIXES = new String[] { "bmp", "jpg", "jpeg", "png" }; // 忽略gif

	/** 检查、压缩选项 */
	private DeflateOptions mOptions;

	/** 已压缩图片列表 */
	private ArrayList<File> mDeflatedImages;

	/**
	 * 用默认选项、压缩图片存储于原图目录
	 * 
	 * @see #ImageDeflateUtil(DeflateOptions, File)
	 */

	/**
	 * 是否是上传店招
	 */
	private boolean isUpLoadShopBoard=false;

	public ImageDeflateUtil() {
		this(null, null);
	}

	/**
	 * @param options 检查、压缩选项，null则取默认值
	 * @param tempPath 压缩图片存储目录，null则保存于图片同目录
	 */
	public ImageDeflateUtil(DeflateOptions options, File tempPath) {
		mOptions = options == null ? new DeflateOptions() : options;
		if (tempPath != null) {
			if (!tempPath.isDirectory())
				throw new IllegalArgumentException("tempPath should be a directory");
			if (!tempPath.exists())
				tempPath.mkdirs();
		}
		mOptions.outPath = tempPath;
		mDeflatedImages = new ArrayList<>();
	}


	/**
	 * @param options 检查、压缩选项，null则取默认值
	 * @param tempPath 压缩图片存储目录，null则保存于图片同目录
	 */
	public ImageDeflateUtil(DeflateOptions options, File tempPath,boolean isUploadShopBoard) {
		this.isUpLoadShopBoard= isUploadShopBoard;
		mOptions = options == null ? new DeflateOptions() : options;
		if (tempPath != null) {
			if (!tempPath.isDirectory())
				throw new IllegalArgumentException("tempPath should be a directory");
			if (!tempPath.exists())
				tempPath.mkdirs();
		}
		mOptions.outPath = tempPath;
		mDeflatedImages = new ArrayList<>();
	}


	/**
	 * 检查、压缩选项<br>
	 * 各项检查界限，在相应值达到设定值时认为是大图片；各项之间是或的关系。
	 */
	public static class DeflateOptions {

		/** 检查界限：文件大小，大于0才检查 */
		public int inFileSize = 512000;

		/** 检查界限：像素数，大于0才检查 */
		public int inPixel = 512000;

		/** 检查界限：边长(长或宽)，大于0才检查 */
		public int inSide = 800;

		/** 压缩后的最大边长，大于0才生效 */
		public int outSide = 800;

		/** 压缩后的位图模式，如果要更高压缩比可用Config.RGB_565 */
		public Config outConfig = Config.ARGB_8888;

		/** 压缩后的图片格式 */
		public CompressFormat outFormat = CompressFormat.JPEG;

		/** 压缩后的图片质量，0-100 */
		public int outQuality = 75;

		/** 压缩图片存储路径 */
		private File outPath;

	}

	/**
	 * 返回压缩过的图片
	 */
	public ArrayList<File> getDeflatedImages() {
		return mDeflatedImages;
	}

	/**
	 * 删除压缩过的图片
	 */
	public void clean() {
		if (!mDeflatedImages.isEmpty()) {
			for (File file : mDeflatedImages) {
				file.delete();
			}
			mDeflatedImages.clear();
		}
	}

	/**
	 * 是否图片文件<br>
	 * 这里没用ContentResolver，因为它只认识content://开头的uri；也没用文件头信息判断，简单效率点就用后缀了
	 */
	public boolean isImage(File file) {
		if (file == null || !file.exists() || !file.isFile())
			return false;
		String name = file.getName();
		int dotIndex = name.lastIndexOf(".");
		if (dotIndex < 0 || dotIndex == name.length() - 1) {
			return false;
		}
		String suffix = name.substring(dotIndex + 1, name.length()).toLowerCase(Locale.getDefault());
		for (int i = 0; i < IMAGE_SUFFIXES.length; i++) {
			if (suffix.equals(IMAGE_SUFFIXES[i]))
				return true;
		}
		return false;
	}

	/**
	 * 检查是否大图片<br>
	 */
	public boolean isLargeImage(File file) {
		if (!isImage(file))
			return false;
		if (mOptions.inFileSize > 0 && mOptions.inFileSize < file.length())
			return true;
		if (mOptions.inPixel <= 0 && mOptions.inSide <= 0)
			return false;
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		int w = options.outWidth;
		int h = options.outHeight;
		if (w <= 0 || h <= 0)
			return false;
		if (mOptions.inPixel > 0 && mOptions.inPixel < w * h)
			return true;
		if (mOptions.inSide > 0 && (mOptions.inSide < w || mOptions.inSide < h))
			return true;
		return false;
	}

	/**
	 * 检查列表里有没有大图片
	 * 
	 * @see #isLargeImage(File)
	 */
	public boolean hasLargeImage(List<File> files) {
		if (files != null && !files.isEmpty())
			for (File file : files)
				if (isLargeImage(file))
					return true;
		return false;
	}

	/**
	 * 检查数组里有没有大图片
	 * 
	 * @see #isLargeImage(File)
	 */
	public boolean hasLargeImage(File... files) {
		if (files != null && files.length > 0)
			for (File file : files)
				if (isLargeImage(file))
					return true;
		return false;
	}

	/**
	 * FileUploadAsyncTask专用，检查上传图片有没有大图片
	 * 
	 * @see #isLargeImage(File)
	 */
	@SuppressWarnings("unchecked")
	public boolean hasLargeImage(HashMap<String, Object> uploadFileMap) {
		if (uploadFileMap == null || uploadFileMap.isEmpty())
			return false;
		for (Entry<String, Object> entry : uploadFileMap.entrySet()) {
			Object obj = entry.getValue();
			if (obj instanceof File) {
				if (isLargeImage((File) obj)) {
					return true;
				}
			} else {
				ArrayList<File> uploadFiles = (ArrayList<File>) obj;
				for (File file : uploadFiles) {
					if (isLargeImage(file)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 压缩图片。此操作不应放在主线程<br>
	 * 
	 * @return 返回压缩后的图片，不需压缩的话返回原图，失败的话也返回原图
	 */
	public File deflate(File input) {
		if (!isLargeImage(input))
			return input;
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(input.getAbsolutePath(), options);
		int w = options.outWidth;
		int h = options.outHeight;
		int side = Math.max(w, h); // 最大边长

		if (isUpLoadShopBoard){
			side=h;
		}

		options.inJustDecodeBounds = false;
		options.inPreferredConfig = mOptions.outConfig;

		// 限制大小
		boolean zoom = mOptions.outSide > 0 && mOptions.outSide < side; // 需缩小
		if (zoom) { // 先用SampleSize来缩小至目标尺寸的2倍以内，防止图片太大造成oom
			int power = 1;
			int side2 = mOptions.outSide << 1; // 目标尺寸的2倍
			while (side >= side2) { // 如果大于目标尺寸的2倍，就继续缩小
				power <<= 1;
				side >>= 1;
				w >>= 1;
				h >>= 1;
			}
			options.inSampleSize = power;
		}
		Bitmap bitmap = BitmapFactory.decodeFile(input.getAbsolutePath(), options);
		if (bitmap == null)
			return input;
		if (zoom && side > mOptions.outSide) { // 用比例来缩小
			float scale = 1F * mOptions.outSide / side;
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		}

		// 压缩保存
		String fileName = UUID.randomUUID().toString() + ".jpg"; // 照片命名
		File path = mOptions.outPath;
		if (path == null)
			path = input.getParentFile();
		File output = new File(path, fileName);
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(output));
			bitmap.compress(mOptions.outFormat, mOptions.outQuality, bos);
			bos.flush();
			mDeflatedImages.add(output);
			return output;
		} catch (Exception e) {
			e.printStackTrace();
			return input;
		} finally {
			IOUtil.closeQuietly(bos);
			bitmap.recycle();
		}
	}

	/**
	 * 压缩图片。此操作不应放在主线程<br>
	 * 
	 * @return 参数files本身。如果有压缩图片，在files里直接改了
	 * @see #deflate(File)
	 */
	public List<File> deflate(List<File> files) {
		if (files != null && !files.isEmpty())
			for (int i = 0; i < files.size(); i++)
				files.set(i, deflate(files.get(i)));
		return files;
	}

	/**
	 * 压缩图片。此操作不应放在主线程<br>
	 * 
	 * @return files构成的数组。如果有压缩图片，在数组里就是压缩过后的图片
	 * @see #deflate(File)
	 */
	public File[] deflate(File... files) {
		if (files != null && files.length > 0)
			for (int i = 0; i < files.length; i++)
				files[i] = deflate(files[i]);
		return files;
	}

	/**
	 * FileUploadAsyncTask专用，压缩上传文件里的大图片。此操作不应放在主线程
	 * 
	 * @return 参数本身
	 * @see #deflate(File)
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> deflate(HashMap<String, Object> uploadFileMap) {
		if (uploadFileMap == null || uploadFileMap.isEmpty())
			return uploadFileMap;
		for (Entry<String, Object> entry : uploadFileMap.entrySet()) {
			Object obj = entry.getValue();
			if (obj instanceof File) {
				entry.setValue(deflate((File) obj));
			} else {
				ArrayList<File> uploadFiles = (ArrayList<File>) obj;
				for (int i = 0; i < uploadFiles.size(); i++) {
					File file = uploadFiles.get(i);
					uploadFiles.set(i, deflate(file));
				}
			}
		}
		return uploadFileMap;
	}

}
