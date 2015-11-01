package com.tcl.wechat.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import com.tcl.wechat.WeApplication;
import com.tcl.wechat.model.AppInfo;

/**
 * 系统信息工具类
 * @author rex.lei
 */
public class SystemInfoUtil {
	
	private static final String TAG = SystemInfoUtil.class.getSimpleName();
	
	private static Context mContext = WeApplication.getContext();
	 
	
	/**
	 * 获取clinttype
	 */
	public static String getClienttype(Context mContext){
		
		 String clienttype = "TCL_BIGPAD";
		 
		 Log.i(TAG, "clienttype=" + clienttype);
		 return clienttype;
	}
	
	/**
	 * 获取deviceid
	 */
	public static String getDeviceId(){		 		
		String deviceid = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID); 
		Log.i(TAG, "deviceID=" + deviceid);
		return deviceid;
	}
	
	/**
	 * 获取Wifi mac地址
	 */
	public static String getWifiMacAddr(){
		
		WifiManager wifi = (WifiManager) WeApplication.getContext().getSystemService(Context.WIFI_SERVICE);  
	    WifiInfo info = wifi.getConnectionInfo();
	    if (info == null){
	    	Log.e(TAG, "get MAC Address Failed!!");
	    	return null;
	    }
	    return info.getMacAddress();  
	}
	
	/**
	 * 获取本机mac地址
	 * @return
	 */
	public static String getLocalMacAddress() {  
		String macAddr = null;  
		FileInputStream wlanFis = null;
		FileInputStream ethFis = null;
		try{
			String path = "sys/class/net/wlan0/address";  
			if((new File(path)).exists()){
				wlanFis = new FileInputStream(path);  
				byte[] buffer = new byte[8192];  
				int byteCount = wlanFis.read(buffer);  
				if(byteCount>0){  
					macAddr = new String(buffer, 0, byteCount, "utf-8");  
				}  
			}  
			if(macAddr == null || macAddr.length() == 0)  {  
				path = "sys/class/net/eth0/address";  
				ethFis = new FileInputStream(path);  
				byte[] buffer_name = new byte[8192];  
				int byteCount_name = ethFis.read(buffer_name);  
				if(byteCount_name>0){  
					macAddr = new String(buffer_name, 0, byteCount_name, "utf-8");  
				}  
			}  
		}catch(Exception e){  
			e.printStackTrace();
		} finally {
			try {
				if (ethFis != null){
					ethFis.close();
				}
				if (wlanFis != null){
					wlanFis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return macAddr.trim();  
	}
	
	
	/**
	 * 获取本地IP地址
	 * @return
	 */
	public static String getLocalIpAddress() {
    	try {
    		String ipv4;
    		List <NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
    		for (NetworkInterface ni: nilist) {
    			List<InetAddress>  ialist = Collections.list(ni.getInetAddresses());
    			for (InetAddress address: ialist){
    				if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4=address.getHostAddress())){ 
    					return ipv4;
    				}
    			}
    		}
    	} catch (SocketException ex) {
    		ex.printStackTrace();
    	}
    	return null;
    }     
	
	public static String getPackageName(){
		return mContext.getPackageName();
	}
	
	/**
	 * 获取版本名称
	 * @return
	 */
	public static String getVersionName(){
		PackageInfo pi = getPackageInfo();
		if (pi != null){
			return pi.versionName;
		}
		return null;
	}
	
	/**
	 * 获取版本号
	 * @return
	 */
	public static String getVersionCode(){
		PackageInfo pi = getPackageInfo();
		if (pi != null){
			return String.valueOf(pi.versionCode);
		}
		return "";
	}
	
	public static String getAppName(){
		ApplicationInfo appInfo = getApplicationInfo();
		if (appInfo != null){
			return (String) appInfo.loadLabel(getPackageManager());
		}
		return null;
	}
	
	/**
	 * 获取应用信息
	 * @return
	 */
	public static ApplicationInfo getApplicationInfo(){
		PackageInfo pi = getPackageInfo();
		if (pi != null){
			return pi.applicationInfo;
		}
		return null;
	}
	
	/**
	 * 获取PackageManager信息
	 * @return
	 */
	public static PackageManager getPackageManager(){
		return mContext.getPackageManager();
	}
	
	/**
	 * 获取包信息
	 * @return
	 */
	public static PackageInfo getPackageInfo(){
		PackageInfo pi = null;
		try {
			PackageManager pm = getPackageManager();
			if (pm != null){
				pi = pm.getPackageInfo(getPackageName(), 
						PackageManager.GET_CONFIGURATIONS);
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pi;
	}
	
	/**
	 * 获取应用信息
	 * @return
	 */
	public static AppInfo getAppInfo(){
		AppInfo appInfo = new AppInfo();
		appInfo.setAppName(getAppName());
		appInfo.setPackageName(getPackageName());
		appInfo.setVersionCode(getVersionCode());
		appInfo.setVersionName(getVersionName());
		
		//MD5加密，用于校验
		String key = getPackageName() + getVersionCode() + getVersionName();
		appInfo.setMd5(MD5Util.hashKeyForDisk(key));
		return appInfo;
	}
	
	public static  String getTopActivityName(){
	       
        ActivityManager activityManager = (ActivityManager) mContext.
        		getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo>  tasksInfo = activityManager.getRunningTasks(1);  
        if(tasksInfo.size() > 0){  
            //应用程序位于堆栈的顶层  
            return tasksInfo.get(0).topActivity.getPackageName();  
        }  
        return null;
    }
	
	/**
	 * 判断当前应用是否为前台应用
	 * @return
	 */
	public static boolean isTopActivity(){
		if (getTopActivityName() == null){
			return true;
		}
		if (getPackageName().equals(getTopActivityName())){
			return true;
		}
		return false;
	}
	
}
