package com.eelly.framework.util;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 * 
 * @author xubing
 * @date 2013年11月19日
 */
public class StringUtil {

	public static final String RAMDOM_BASE = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/** md5编码 */
	public static final String ALGORITHM_MD5 = "MD5";

	/** sha1编码 */
	public static final String ALGORITHM_SHA1 = "SHA1";

	/**
	 * 字符串是否为空(空字符串或null)
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	/***
	 * 将字符串进行Base64编码。编码结果超过76字符会插入换行符，末尾有换行符
	 * 
	 * @param str
	 * @return
	 */
	public static String base64Encode(String str) {
		return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
	}

	/***
	 * 将字符串进行Base64编码，去掉其中所有的换行符
	 * 
	 * @param str
	 * @return
	 */
	public static String base64EncodeNoCR(String str) {
		return base64Encode(str).replaceAll("\\n", "");
	}

	/***
	 * 将字符串进行Base64解码
	 * 
	 * @param str
	 * @return
	 */
	public static String base64Decode(String str) {
		return new String(Base64.decode(str.getBytes(), Base64.DEFAULT));
	}

	/**
	 * 十进制转十六进制对应表
	 */
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * 字节转十六进制
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			sb.append(HEX_DIGITS[(bytes[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[bytes[i] & 0x0f]);
		}
		return sb.toString();
	}

	/**
	 * MD5编码，大写，使用默认charset(UTF-8)
	 */
	public static String md5(String str) {
		return encode(str.getBytes(), ALGORITHM_MD5);
	}

	/**
	 * MD5编码，小写，使用默认charset(UTF-8)
	 */
	public static String md5Lcase(String str) {
		String output = encode(str.getBytes(), ALGORITHM_MD5);
		return output == null ? null : output.toLowerCase(Locale.getDefault());
	}

	/**
	 * MD5编码，大写，使用特定charset
	 * 
	 * @param str
	 * @param charsetName
	 * @return
	 */
	public static String md5(String str, String charsetName) {
		try {
			return encode(str.getBytes(charsetName), ALGORITHM_MD5);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * MD5编码，小写，使用特定charset
	 * 
	 * @param str
	 * @param charsetName
	 * @return
	 */
	public static String md5Lcase(String str, String charsetName) {
		String output = md5(str, charsetName);
		return output == null ? null : output.toLowerCase(Locale.getDefault());
	}

	/**
	 * MD5编码，小写
	 * 
	 * @param bytes
	 * @return
	 */
	public static String md5Lcase(byte[] bytes) {
		String output = encode(bytes, ALGORITHM_MD5);
		return output == null ? null : output.toLowerCase(Locale.getDefault());
	}

	/**
	 * SHA1编码，大写，使用默认charset(UTF-8)
	 */
	public static String sha1(String str) {
		return encode(str.getBytes(), ALGORITHM_SHA1);
	}

	/**
	 * SHA1编码，小写，使用默认charset(UTF-8)
	 */
	public static String sha1Lcase(String str) {
		String output = encode(str.getBytes(), ALGORITHM_SHA1);
		return output == null ? null : output.toLowerCase(Locale.getDefault());
	}

	/**
	 * SHA1编码，大写，使用特定charset
	 * 
	 * @param str
	 * @param charsetName
	 * @return
	 */
	public static String sha1(String str, String charsetName) {
		try {
			return encode(str.getBytes(charsetName), ALGORITHM_SHA1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * SHA1编码，小写，使用特定charset
	 * 
	 * @param str
	 * @param charsetName
	 * @return
	 */
	public static String sha1Lcase(String str, String charsetName) {
		String output = sha1(str, charsetName);
		return output == null ? null : output.toLowerCase(Locale.getDefault());
	}

	/**
	 * SHA1编码，小写
	 * 
	 * @param bytes
	 * @return
	 */
	public static String sha1Lcase(byte[] bytes) {
		String output = encode(bytes, ALGORITHM_SHA1);
		return output == null ? null : output.toLowerCase(Locale.getDefault());
	}

	/**
	 * SHA1和MD5编码，大写
	 * 
	 * @param bytes 输入
	 * @param algorithm 编码算法 {@link#ALGORITHM_MD5}, {@link#ALGORITHM_SHA1}
	 * @return 输出
	 */
	public static String encode(byte[] bytes, String algorithm) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			digest.update(bytes);
			return toHexString(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 把颜色数值转为#RRGGBB形式
	 * 
	 * @param color
	 * @return
	 */
	public static String getRGBColor(int color) {
		return "#" + Integer.toHexString(color | 0xff000000).substring(2);
	}

	/**
	 * 把颜色数值转为#AARRGGBB形式
	 * 
	 * @param color
	 * @return
	 */
	public static String getARGBColor(int color) {
		return "#" + Long.toHexString(color | 0xffffffff00000000L).substring(8);
	}

	/**
	 * 设置同一个textview里边文字的不同颜色
	 * 
	 * @param format 以中括号标明要上色的文字，例如 "[12人]给了[8种]印象"
	 * @param colors 例如 "#dd465e"。如果数量少于format里的括号数，不足的会取最后一个
	 * @return 返回的Spanned可以直接用于TextView.setText()
	 */
	public static Spanned formatColor(String format, String... colors) {
		if (colors == null || colors.length == 0)
			return Html.fromHtml(format);
		Pattern pattern = Pattern.compile("(\\[[^\\[\\]]*\\])");
		StringBuffer buffer = new StringBuffer(format.length());
		Matcher matcher = pattern.matcher(format);
		int i = 0;
		while (matcher.find()) {
			int c = i < colors.length ? i : colors.length - 1;
			String group = matcher.group();
			group = "<font color=\"" + colors[c] + "\">" + group.substring(1, group.length() - 1) + "</font>";
			matcher.appendReplacement(buffer, group);
			i++;
		}
		matcher.appendTail(buffer);
		return Html.fromHtml(buffer.toString());
	}

	/**
	 * 设置同一个textview里边文字的不同颜色
	 * 
	 * @param format 以中括号标明要上色的文字，例如 "[12人]给了[8种]印象"
	 * @param colors 颜色值，例如 Color.RED, 0xffdd465e，忽略透明度。如果数量少于format里的括号数，不足的会取最后一个
	 * @return 返回的Spanned可以直接用于TextView.setText()
	 */
	public static Spanned formatColor(String format, int... colors) {
		if (colors == null || colors.length == 0)
			return Html.fromHtml(format);
		String[] colors2 = new String[colors.length];
		for (int i = 0; i < colors.length; i++) {
			colors2[i] = getRGBColor(colors[i]);
		}
		return formatColor(format, colors2);
	}

	/**
	 * 设置同一个textview里边文字的不同颜色
	 * 
	 * @param format 以中括号标明要上色的文字，例如 "[12人]给了[8种]印象"
	 * @param context 上下文
	 * @param colorIds 颜色资源id，例如 R.color.eelly_red ，忽略透明度。如果数量少于format里的括号数，不足的会取最后一个
	 * @return 返回的Spanned可以直接用于TextView.setText()
	 */
	public static Spanned formatColorResource(String format, Context context, int... colorIds) {
		if (colorIds == null || colorIds.length == 0)
			return Html.fromHtml(format);
		String[] colors2 = new String[colorIds.length];
		for (int i = 0; i < colorIds.length; i++) {
			colors2[i] = getRGBColor(context.getResources().getColor(colorIds[i]));
		}
		return formatColor(format, colors2);
	}

	/**
	 * 获得带颜色的文字
	 * 
	 * @param text
	 * @param color
	 * @return
	 */
	public static SpannableStringBuilder getColoredText(String text, int color) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		builder.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return builder;
	}

	/**
	 * @see #getColoredText(String, int)
	 */
	public static SpannableStringBuilder getColoredText(Context context, String text, int colorId) {
		return getColoredText(text, context.getResources().getColor(colorId));
	}

	/**
	 * @see #getColoredText(String, int)
	 */
	public static SpannableStringBuilder getColoredText(Context context, int textId, int colorId) {
		return getColoredText(context.getString(textId), context.getResources().getColor(colorId));
	}

	/**
	 * 在字符串中查找第一个匹配的字符
	 * 
	 * @param source 源字符串
	 * @param target 要查找的目标字符串
	 * @param ignoreCase 是否忽略大小写
	 * @return 字符串查找结果,没找到时返回null
	 */
	public static StringFind find(String source, String target, boolean ignoreCase) {
		Pattern pattern = ignoreCase ? Pattern.compile(target, Pattern.CASE_INSENSITIVE | Pattern.LITERAL) : Pattern.compile(target, Pattern.LITERAL);
		Matcher matcher = pattern.matcher(source);
		if (matcher.find()) {
			return new StringFind(matcher.start(), matcher.end(), matcher.group());
		}
		return null;
	}

	/**
	 * 字符串查找结果对象
	 * 
	 * @author 李欣
	 */
	public static class StringFind {

		/**
		 * 查找字符串的开始位置
		 */
		public int start;

		/**
		 * 查找字符串的结束位置+1
		 */
		public int end;

		/**
		 * 查找字符串在源字符串中的原始值(主要用于字母大小写的情况获取原始值)
		 */
		public String original;

		StringFind(int start, int end, String original) {
			this.start = start;
			this.end = end;
			this.original = original;
		}
	}

	/**
	 * 计算字符串的字节长度(半角符号计1，全角符号计2)
	 * 
	 * @param string
	 * @return
	 */
	public static int getByteLength(String string) {
		int count = 0;
		for (int i = 0; i < string.length(); i++) {
			count += Integer.toHexString(string.charAt(i)).length() == 4 ? 2 : 1;
		}
		return count;
	}

	/**
	 * 按指定长度，截断字符串，超长会添加指定后缀<br>
	 * 半角符号长度为1，全角符号长度为2
	 * 
	 * @param string 字符串
	 * @param length 保留字符串长度
	 * @param suffix 超长时添加的后缀
	 * @return 截断后的字符串
	 */
	public static String trimString(String string, int length, String suffix) {
		if (getByteLength(string) <= length)
			return string;
		StringBuffer sb = new StringBuffer();
		int count = 0;
		if (suffix == null)
			suffix = "";
		int slength = getByteLength(suffix);
		for (int i = 0; i < string.length(); i++) {
			char temp = string.charAt(i);
			count += Integer.toHexString(temp).length() == 4 ? 2 : 1;
			if (count + slength <= length) {
				sb.append(temp);
			}
			if (count + slength >= length) {
				break;
			}
		}
		sb.append(suffix);
		return sb.toString();
	}

	/**
	 * 按指定长度，截断字符串，超长会添加…<br>
	 * 半角符号长度为1，全角符号长度为2
	 * 
	 * @param string 字符串
	 * @param length 保留字符串长度
	 * @return 截断后的字符串
	 */
	public static String trimString(String string, int length) {
		return trimString(string, length, "…");
	}

	/**
	 * 根据像素宽度截取字符串<br>
	 * 这个是为了某些地方不需要省略号结尾，而TextView的宽度不够，这样最后的字会
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static String trimPixelLength(String str, int pixel, float textSize) {
		if (TextUtils.isEmpty(str)) {
			return "";
		}
		float width = getTextWidth(str, textSize);
		if (Math.ceil(width) <= pixel) {
			return str.toString();
		} else {
			for (int i = str.length() - 1; i > 0; i--) {
				String temp = str.substring(0, i);
				float w = getTextWidth(temp, textSize);
				if (Math.ceil(w) <= pixel) {
					return temp;
				}
			}
		}
		return "";
	}

	private static float getTextWidth(String text, float textSize) {
		TextPaint paint = new TextPaint();
		paint.setTextSize(textSize);
		return paint.measureText(text);
	}

	/**
	 * 过滤Html内容,将Html格式的内容转为普通文本
	 * 
	 * @param source
	 * @return
	 */
	public static String htmlToString(String source) {
		return Html.fromHtml(source).toString();
	}

	/**
	 * 金钱单位四舍五入
	 * 
	 * @param money 金钱
	 * @return 四舍五入之后的金钱
	 */
	public static String moneyRound(String money) {
		if (money.length() == 0) {
			money = "0";
		}
		BigDecimal decimal = new BigDecimal(Double.valueOf(money));
		return decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	/**
	 * javascript escape
	 * 
	 * @param s
	 * @return
	 */
	public static String escape(String s) {
		StringBuilder sb = new StringBuilder();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			int ch = s.charAt(i);
			if (isEscapePersistDigit(ch)) {
				sb.append((char) ch);
			} else if (ch <= 0x007F) {
				sb.append('%');
				sb.append(HEX_DIGITS[(ch & 0xf0) >>> 4]);
				sb.append(HEX_DIGITS[ch & 0x0f]);
			} else {
				sb.append('%');
				sb.append('u');
				sb.append(HEX_DIGITS[(ch & 0xf000) >>> 12]);
				sb.append(HEX_DIGITS[(ch & 0x0f00) >>> 8]);
				sb.append(HEX_DIGITS[(ch & 0x00f0) >>> 4]);
				sb.append(HEX_DIGITS[ch & 0x000f]);
			}
		}
		return sb.toString();
	}

	/**
	 * javascript unescape
	 * 
	 * @param s
	 * @return
	 */
	public static String unescape(String s) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		int len = s.length();
		while (i < len) {
			int ch = s.charAt(i);
			if (isEscapePersistDigit(ch)) {
				sb.append((char) ch);
			} else if (ch == '%') {
				boolean flag = false;
				int cint = 0;
				if ('u' == s.charAt(i + 1) || 'U' == s.charAt(i + 1)) {
					if (i + 5 < len) {
						char[] chs = new char[4];
						s.getChars(i + 2, i + 6, chs, 0);
						if (isHexDigits(chs)) {
							cint = Integer.parseInt(new String(chs), 16);
							i += 5;
							flag = true;
						}
					}
				} else {
					if (i + 2 < len) {
						char[] chs = new char[2];
						s.getChars(i + 1, i + 3, chs, 0);
						if (isHexDigits(chs)) {
							cint = Integer.parseInt(new String(chs), 16);
							i += 2;
							flag = true;
						}
					}
				}
				if (flag)
					sb.append((char) cint);
				else
					sb.append((char) ch);
			} else {
				sb.append((char) ch);
			}
			i++;
		}
		return sb.toString();
	}

	private static boolean isHexDigit(int ch) {
		return ('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z') || ('0' <= ch && ch <= '9');
	}

	private static boolean isHexDigits(char[] chs) {
		for (int i = 0; i < chs.length; i++) {
			if (!isHexDigit(chs[i]))
				return false;
		}
		return true;
	}

	private static boolean isEscapePersistDigit(int ch) {
		return isHexDigit(ch) || ch == '*' || ch == '@' || ch == '-' || ch == '_' || ch == '+' || ch == '.' || ch == '/';
	}

	/**
	 * 判断字符是否为数字，包括小数
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isNumeric(String input) {
		if(TextUtils.isEmpty(input)) {
			return false;
		}
		Pattern pattern = Pattern.compile("-?[0-9]*.?[0-9]*");
		Matcher isNum = pattern.matcher(input);
		return !TextUtils.isEmpty(input) && isNum.matches();
	}

	/**
	 * 获取随机字符串，字符由数字和大小写字符组成
	 * 
	 * @param length 获取的字符串长度
	 * @return 相应长度的随机字符串
	 */
	public static String getRandomString(int length) {
		return getRandomString(length, null);
	}

	/**
	 * 获取随机字符串，字符由数字和大小写字符组成
	 * 
	 * @param length 获取的字符串长度
	 * @param base 组成随机字符串的字符，传null时，默认使用{@value #RAMDOM_BASE}
	 * @return 相应长度的随机字符串
	 */
	public static String getRandomString(int length, String base) {
		if (TextUtils.isEmpty(base)) {
			base = RAMDOM_BASE;
		}
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 字符串翻转
	 * 
	 * @param str 要翻转的字符串
	 * @return
	 */
	public static String reverseString(String str) {
		if (TextUtils.isEmpty(str)) {
			return "";
		}
		StringBuffer sb = new StringBuffer(str);
		return sb.reverse().toString();
	}

}
