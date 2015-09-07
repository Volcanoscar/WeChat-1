package com.tcl.wechat.utils;

import android.content.Context;
import android.content.SharedPreferences.Editor;

/**
 * 本地数据存储帮助类
 * @author rex.lei
 *
 */
public class SystemShare {
	
	private Context mContext;
	private static SystemShare mInstance;
	private Editor mEditor;
	
	private SystemShare(Context context) {
		super();
		mContext = context; 
	}

	public SystemShare getInstance(Context context){
		if (mInstance == null){
			synchronized (mInstance) {
				mInstance = new SystemShare(context.getApplicationContext());
			}
		}
		return mInstance;
	}
	
	
}
