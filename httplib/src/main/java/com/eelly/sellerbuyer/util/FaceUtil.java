package com.eelly.sellerbuyer.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.eelly.framework.util.DeviceUtil;
import com.eelly.framework.util.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IM表情转换工具
 * 
 * @author 苏腾
 */
public class FaceUtil {

	public static final Pattern PATTERN = Pattern.compile("\\{(\\d{3}(?:\\.gif)?)\\}", Pattern.CASE_INSENSITIVE);

	/**
	 * 把源文本转换为表情图案形式
	 * 
	 * @param context
	 * @param text
	 * @return
	 */
	public static SpannableStringBuilder markup(Context context, CharSequence text) {
		return markup(context, text, DeviceUtil.dipToPx(context, 28));
	}

	/**
	 * 把源文本转换为表情图案形式
	 * 
	 * @param context
	 * @param text
	 * @param faceSize
	 * @return
	 */
	public static SpannableStringBuilder markup(Context context, CharSequence text, int faceSize) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		Matcher matcher = PATTERN.matcher(builder);
		while (matcher.find()) {
			String key = matcher.group(1).toLowerCase(Locale.getDefault());
			if (!key.endsWith(".gif")) {
				key += ".gif";
			}
			InputStream in = null;
			try {
				in = context.getAssets().open("expression/" + key, AssetManager.ACCESS_BUFFER);
				if (in != null) {
					BitmapDrawable drawable = new BitmapDrawable(context.getResources(), in);
					drawable.setBounds(0, 0, faceSize, faceSize);
					FaceSpan imageSpan = new FaceSpan(drawable);
					builder.setSpan(imageSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					in.close();
				}
			} catch (IOException e) {
			} finally {
				IOUtil.closeQuietly(in);
			}
		}
		return builder;
	}

	/**
	 * 把带表情的文字转换为源文本形式，markup的反操作
	 * 
	 * @param spannedText 图文混合形式
	 * @return 源文本形式
	 */
	public static CharSequence plain(CharSequence spannedText) {
		// return spannedText.toString(); // 这么做可以，只是暴力了点，会把其他Span(例如Html标记)也去掉
		SpannableStringBuilder builder = new SpannableStringBuilder(spannedText);
		FaceSpan[] spans = builder.getSpans(0, builder.length(), FaceSpan.class);
		for (int i = spans.length - 1; i >= 0; i--) {
			builder.removeSpan(spans[i]);
		}
		return builder;
	}

	/**
	 * 删除所有表情图标和标记
	 * 
	 * @param source
	 * @return
	 */
	public static String removeAll(CharSequence source) {
		return PATTERN.matcher(plain(source)).replaceAll("");
	}

	/**
	 * 删除当前选择的文字和表情，如果没选择，则删除光标的前一个元素
	 * 
	 * @param text
	 */
	public static void delete(Editable text) {
		int start = Selection.getSelectionStart(text), end = Selection.getSelectionEnd(text);
		if (start == end) {
			if (start > 0) {
				start--;
				FaceSpan[] spans = text.getSpans(start, end, FaceSpan.class);
				if (spans != null) {
					for (FaceSpan span : spans) {
						start = Math.min(text.getSpanStart(span), start);
					}
				}
				text.delete(start, end);
			}
		} else {
			text.replace(start, end, "");
		}
	}

	/**
	 * 这个类啥也不干，就是标识一下表情Span，以免删除表情时把其他ImageSpan也删掉
	 */
	private static class FaceSpan extends ImageSpan {

		private FaceSpan(Context context, Bitmap b) {
			super(context, b);
		}

		private FaceSpan(Drawable d) {
			super(d);
		}

	}

}
