package com.eelly.seller.common.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.eelly.framework.util.IOUtil;
import com.eelly.framework.util.ImageUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * 上传图片管理
 *
 * @author 林钊平
 */
public class UploadImageManager {

    /** 图片最大宽度 */
    public static final int PICTURE_MAX_WIDTH = 800;

    /** 图片压缩质量 */
    public static final int PICTURE_COMPRESS_QUALITY = 80;

    /**
     * 压缩图片，如果宽度过大，则压缩之；如果是Exif图片，则旋转之<br>
     * 耗时操作，不应放在主线程
     *
     * @param input 原文件
     * @param outputPath 压缩文件存储目录
     * @return 压缩过的文件，或者原文件
     */
    public static File getZoomedImage(File input, File outputPath) {
        String inputPath = input.getAbsolutePath();
        if (outputPath == null) {
            outputPath = input.getParentFile();
        }
        Bitmap bitmap;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(inputPath, options);
        int width = options.outWidth;
        int height = options.outHeight;
        int side = width; // 要限制的边长，默认宽度

        // Exif旋转角度
        int rotation = ImageUtils.getExifRotation(inputPath);
        if (rotation == 90 || rotation == 270) {
            side = height; // 因为要旋转，限制高度
        }

        // 不需缩小
        if (side <= PICTURE_MAX_WIDTH) {
            // 不需旋转
            if (rotation == 0) {
                return input;
            }
            // 旋转
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(inputPath, options);
            if (bitmap == null)
                return input;
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            // 保存
            File file = saveImage(bitmap, outputPath);
            return file == null ? input : file;
        }

        // 先用SampleSize来缩小至目标尺寸的2倍以内，防止图片太大造成oom
        int power = 1;
        int side2 = PICTURE_MAX_WIDTH << 1; // 目标尺寸的2倍
        while (side >= side2) { // 如果大于目标尺寸的2倍，就继续缩小
            power <<= 1;
            width >>= 1;
            height >>= 1;
            side >>= 1;
        }
        options.inSampleSize = power;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(inputPath, options);
        if (bitmap == null)
            return input;

        // 再用比例来缩小
        Matrix matrix = null;
        if (side > PICTURE_MAX_WIDTH) {
            matrix = new Matrix();
            float scale = 1F * PICTURE_MAX_WIDTH / side;
            matrix.postScale(scale, scale);
        }

        // 处理旋转
        if (rotation > 0) {
            if (matrix == null) {
                matrix = new Matrix();
            }
            matrix.postRotate(rotation);
        }

        if (matrix != null) {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }

        File file = saveImage(bitmap, outputPath);
        return file == null ? input : file;
    }

    /**
     * 压缩保存
     */
    public static File saveImage(Bitmap bitmap, File outputPath) {
        String fileName = UUID.randomUUID().toString() + ".jpg"; // 照片命名
        File output = new File(outputPath, fileName);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(output));
            bitmap.compress(Bitmap.CompressFormat.JPEG, PICTURE_COMPRESS_QUALITY, bos);
            bos.flush();
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtil.closeQuietly(bos);
            bitmap.recycle();
        }
    }

}
