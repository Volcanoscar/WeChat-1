package com.tcl.wechat.utils;

import android.content.Context;
import android.util.Log;


public class DensityUtil {
	
	/*
	 * 根据分辨率dp转px
	 */
	public static int dip2px(Context context, float dpValue)
	{
		float scale = context.getResources().getDisplayMetrics().density;
		Log.i("DensityUtil", "scale="+scale);
		return (int)(dpValue * scale + 0.5f);
	}
	
	/*
	 * 根据分辨率px转dp
	 */
	public static int px2dip(Context context, float pxValue)
	{
		float scale = context.getResources().getDisplayMetrics().density;  
	    return (int) (pxValue / scale + 0.5f);  
	}

}

