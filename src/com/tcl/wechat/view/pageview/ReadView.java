package com.tcl.wechat.view.pageview;

import com.tcl.wechat.WeApplication;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class ReadView extends TextView{

	public ReadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ReadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ReadView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 设置字体
	 * @param fontpath
	 */
	public void setFont(String fontpath) {
		try {
			this.setTypeface(WeApplication.getInstance().getTypeface1());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
