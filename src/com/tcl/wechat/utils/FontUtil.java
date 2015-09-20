package com.tcl.wechat.utils;

import android.content.Context;
import android.graphics.Typeface;

public class FontUtil {
	
	private Context mContext;
	
	private FontUtil() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FontUtil(Context mContext) {
		super();
		this.mContext = mContext;
	}

	public Typeface getDefultFont(){
		return getFont("fonts/oop.TTF");
	}
	
	public Typeface getFont(String fontpath) {
		try {
			return Typeface.createFromAsset(mContext.getAssets(), fontpath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
