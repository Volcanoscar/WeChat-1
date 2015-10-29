package com.tcl.wechat.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.tcl.wechat.WeApplication;

/**
 * 检测网络是否可用
 * @author rex.lei
 *
 */
public class NetWorkUtil {
	
	/**
	 * 网络是否链接
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected() {
		boolean isNetConnected;
		// 获得网络连接服务
		ConnectivityManager connManager = (ConnectivityManager) WeApplication.getContext()
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
	public static boolean isNetworkAvailable() {
		ConnectivityManager connec = (ConnectivityManager) WeApplication.getContext().
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
	
	public static boolean isWifiConnected(){
		ConnectivityManager manager = (ConnectivityManager)  WeApplication.getContext().
				getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()){
        	return true;
        }
		return false; 
	}
	
}
