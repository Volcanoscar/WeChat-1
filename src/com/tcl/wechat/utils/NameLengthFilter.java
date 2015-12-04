package com.tcl.wechat.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * InputFilter
 * @author rex.lei
 *
 */
public class NameLengthFilter implements InputFilter {

	// 最大字符数
	private int max = 8;

	/**
	 * filter
	 * @param source 输入的文字 
	 * @param start 开始位置
	 * @param end 结束位置
	 * @param dest 当前显示的内容
	 * @param dstart 当前开始位置 
	 * @param dend /当前结束位置
	 */
	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		int source_count = countChinese(source.toString());// 刚输入的字符中所含中文字符数
		int dest_count = countChinese(dest.toString());// 已存在字符中所含中文字符数
		int keep = max - dest_count - (dest.length() - (dend - dstart));// 还能输入的字符数

		if (keep <= 0) {// 字符数满
			return "";
		} else if (keep - source_count >= end - start) {// 加入新字符后字符数未满
			return null; // keep original
		} else {// 加入新字符后字符数超
			char[] ch = source.toString().toCharArray();
			int k = keep;
			keep = 0;
			for (int i = 0; i < ch.length; i++) {
				if (isChinese(ch[i])) {
					k = k - 2;
				} else {
					k--;
				}
				keep++;
				if (k <= 0) {
					break;
				}
			}
			return source.subSequence(start, start + keep);
		}
	}

	/**
	 * 判断是否为中文
	 * 
	 * @param c
	 * @return
	 */
	private static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * 计算字符串中中文字符数
	 * 
	 * @param strName
	 * @return
	 */
	int countChinese(String strName) {
		int count = 0;
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				count++;
			}
		}
		return count;
	}

}