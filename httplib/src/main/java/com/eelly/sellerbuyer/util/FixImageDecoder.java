package com.eelly.sellerbuyer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecodingInfo;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.nostra13.universalimageloader.utils.L;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片缓存解码类,修正了在某些手机中使用BitmapFactory.decodeStream对图片解码时返回null的问题
 * 
 * @author 李欣
 */
public class FixImageDecoder extends BaseImageDecoder {

	public FixImageDecoder(boolean loggingEnabled) {
		super(loggingEnabled);
	}

	@Override
	public Bitmap decode(ImageDecodingInfo decodingInfo) throws IOException {
		Bitmap decodedBitmap;
		ImageFileInfo imageInfo;

		InputStream imageStream = getImageStream(decodingInfo);
		try {
			imageInfo = defineImageSizeAndRotation(imageStream, decodingInfo);
			imageStream = resetStream(imageStream, decodingInfo);
			Options decodingOptions = prepareDecodingOptions(imageInfo.imageSize, decodingInfo);
			// 使用输入流对图片进行解码,在某些手机(红米手机)可能会出现返回null的情况(TODO::可能是机器底层实现的InputStream有问题或者是ImageLoader实现的ContentLengthInputStream有问题,有待测试)
			decodedBitmap = BitmapFactory.decodeStream(imageStream, null, decodingOptions);
			if (decodedBitmap == null) {// 如果使用输入流解码图片失败则尝试使用文件方式解码
				File bitmapFile = new File(Scheme.FILE.crop(decodingInfo.getImageUri()));
				decodedBitmap = BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), decodingOptions);
			}
		} finally {
			IoUtils.closeSilently(imageStream);
		}

		if (decodedBitmap == null) {
			L.e(ERROR_CANT_DECODE_IMAGE, decodingInfo.getImageKey());
		} else {
			decodedBitmap = considerExactScaleAndOrientatiton(decodedBitmap, decodingInfo, imageInfo.exif.rotation, imageInfo.exif.flipHorizontal);
		}
		return decodedBitmap;
	}

}
