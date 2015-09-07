package com.tcl.wechat.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 检测网络是否可用
 * @author rex.lei
 *
 */
public class NetUtil {
	
	/**
	 * 网络是否链接
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context) {
		boolean isNetConnected;
		// 获得网络连接服务
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			isNetConnected = true;
		} else {
			isNetConnected = false;
		}
		return isNetConnected;
	}
	
	/**
	 * 网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		return getAllNetworkStates(context);
	}
	
	public synchronized static boolean getAllNetworkStates(Context mContext) {
		ConnectivityManager connec = (ConnectivityManager) mContext.
				getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connec == null){
			return false;
		}
		NetworkInfo activeInfo = connec.getActiveNetworkInfo();			
		if (activeInfo != null && activeInfo.isConnected()
				&& activeInfo.isAvailable()) {							
			return true;
		} 
		return false;
	}
}
