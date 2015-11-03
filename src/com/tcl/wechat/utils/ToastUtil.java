package com.tcl.wechat.utils;

import com.tcl.wechat.WeApplication;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast帮助类
 * @author rex.lei
 *
 * TODO 需要重新定义，防止多次弹出Toast
 */
public class ToastUtil {
	
	private static Context mContext = WeApplication.getContext();
	
	/**
	 * 开关标志
	 */
	private static boolean bSwitchFlag = true;
	
	/**
	 * 非强制显示Toast
	 * @param message
	 */
	public static void showToast(String message){
		
		if (bSwitchFlag) {
			Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * 非强制显示Toast
	 * @param resId
	 */
	public static void showToast(int resId){
		if (bSwitchFlag) {
			Toast.makeText(mContext, mContext.getResources().getString(resId), 
					Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * 强制显示Toast
	 * @param message
	 */
	public static void showToastForced(String message){
		Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 强制显示Toast
	 * @param resId
	 */
	public static void showToastForced(int resId){
		Toast.makeText(mContext, mContext.getResources().getString(resId), 
				Toast.LENGTH_LONG).show();
	}

}
