package com.eelly.framework.util;

import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 输入工具类
 * 
 * @author 苏腾
 */
public class InputUtil {

	/**
	 * 给TextView添加一个InputFilter
	 * 
	 * @param view
	 * @param filter
	 */
	public static void addFilter(TextView view, InputFilter filter) {
		InputFilter[] f0 = view.getFilters();
		ArrayList<InputFilter> f1 = new ArrayList<InputFilter>();
		if (f0 != null && f0.length > 0) {
			f1.addAll(Arrays.asList(f0));
		}
		f1.add(filter);
		InputFilter[] f2 = new InputFilter[f1.size()];
		f1.toArray(f2);
		view.setFilters(f2);
	}

	/**
	 * 字符过滤器，用于限制TextView可输入的字符<br>
	 * android:digits 属性对物理键盘有效，但各种输入法的软键盘不一定会实现<br>
	 * 
	 * @author 苏腾
	 */
	public static class DigitsFilter implements InputFilter {

		protected String digits;

		public DigitsFilter(String digits) {
			if (digits == null || digits.length() == 0)
				throw new RuntimeException("Digits should not be empty!");
			this.digits = digits;
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			char[] accept = digits.toCharArray();
			int i;
			for (i = start; i < end; i++) {
				if (!check(accept, source.charAt(i))) {
					break;
				}
			}

			if (i == end) {
				// It was all OK.
				return null;
			}

			if (end - start == 1) {
				// It was not OK, and there is only one char, so nothing remains.
				return "";
			}

			SpannableStringBuilder filtered = new SpannableStringBuilder(source, start, end);
			i -= start;
			end -= start;

			// Only count down to i because the chars before that were all OK.
			for (int j = end - 1; j >= i; j--) {
				if (!check(accept, source.charAt(j))) {
					filtered.delete(j, j + 1);
				}
			}

			return filtered;
		}

		protected boolean check(char[] accept, char c) {
			for (int i = accept.length - 1; i >= 0; i--) {
				if (accept[i] == c) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * 字符过滤器，限制TextView不能输入某些字符
	 * 
	 * @see {@link DigitsFilter}
	 * @author 苏腾
	 */
	public static class NonDigitsFilter extends DigitsFilter {

		public NonDigitsFilter(String digits) {
			super(digits);
		}

		@Override
		protected boolean check(char[] accept, char c) {
			return !super.check(accept, c);
		}

	}

	/**
	 * 正则过滤器，用正则表达式限制可输入的内容<br>
	 * 要注意的是，输入是逐个字符输入的，每输入一个字符都会触发一次检测，因此类似 \d{3} 这样的正则是不能输入的，应该写成 \d{0,3}<br>
	 * 当粘贴时，粘贴内容只要不符合要求，会全部丢弃
	 * 
	 * @author 苏腾
	 */
	public static class PattenFilter implements InputFilter {

		protected Pattern pattern;

		public PattenFilter(Pattern pattern) {
			this.pattern = pattern;
		}

		public PattenFilter(String pattern) {
			this.pattern = Pattern.compile(pattern);
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			// Log.wtf("filter","source:" + source + ",start:" + start + ",end:" + end + ",dest:" + dest + ",dstart:" + dstart + ",dend:" + dend);
			// 构造替换后的内容
			char[] s0 = dest.toString().toCharArray(); // 原始串
			char[] s1 = source.toString().toCharArray(); // 替换串
			char[] s2 = new char[s0.length - dend + dstart + end - start]; // 替换后
			int i, c = 0;
			for (i = 0; i < dstart; i++)
				s2[c++] = s0[i]; // 头
			for (i = start; i < end; i++)
				s2[c++] = s1[i]; // 替换部分
			for (i = dend; i < s0.length; i++)
				s2[c++] = s0[i]; // 尾
			return pattern.matcher(String.valueOf(s2)).matches() ? null : "";
		}

	}

	/**
	 * 正则过滤器的特例，所提供的正则应该是一个集合检测，输入和粘贴时会逐个字符判断，只留下符合要求的
	 * 
	 * @author 苏腾
	 */
	public static class SetPattenFilter extends PattenFilter {

		public SetPattenFilter(Pattern pattern) {
			super(pattern);
		}

		public SetPattenFilter(String pattern) {
			super(pattern);
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			if (pattern.matcher(source.subSequence(start, end)).matches())
				return null;

			if (end - start == 1) {
				return "";
			}

			SpannableStringBuilder filtered = new SpannableStringBuilder(source, start, end);
			for (int i = filtered.length(); i > 0; i--) {
				if (!pattern.matcher(filtered.subSequence(i - 1, i)).matches()) {
					filtered.delete(i - 1, i);
				}
			}
			return filtered;
		}

		/** 半角符号，全角符号，汉字 */
		public static final String GENERAL_TEXT = "[\\x20-\\x7f\\u2000-\\u206f\\u3000-\\u303f\\u4e00-\\u9fa5\\uff00-\\uffef]*";

		/** 支付密码-数字字母组合 */
		public static final String PAY_PASSWORD_PATTERN = "([a-z]|[A-Z]|[0-9])*";

	}

}
