package com.eelly.seller.common.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.widget.EditText;

public class EditUtils {
	
	private static final int HINT_SIZE = 14;
	
	/**
	 * 可用于设置EditText的提示语
	 * @param str
	 * @param textSize
	 * @return
	 */
	public static SpannedString getSpannedStr(String str) {
		SpannableString spanStr = new SpannableString(str);
		AbsoluteSizeSpan ass = new AbsoluteSizeSpan(HINT_SIZE, true);
		spanStr.setSpan(ass, 0, spanStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return new SpannedString(spanStr);
	}
	
	public static String getEditContent(EditText edt) {
		return edt.getText().toString().trim();
	}
}
