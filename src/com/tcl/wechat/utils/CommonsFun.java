package com.tcl.wechat.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.util.EncodingUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;

import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.modle.AppInfo;
import com.tcl.wechat.xml.ParseConfigXml;
//import android.os.SystemProperties;
//import com.tcl.device.authentication.SqlCommon;


public class CommonsFun {
	private static String tag = "CommonsFun";
	/**
	 * 获取deviceid
	 */
	public static String getDeviceId(Context mContext){		 		
//		 SqlCommon sqlcommon = new SqlCommon();	
//		 ContentResolver resolver = mContext.getContentResolver();
//		 String deviceid = sqlcommon.getDeviceid(resolver);
//		 //deviceid= "6c99e7943d15d7cb6325c79d0837b3bf65344d77";
		String deviceid = "6c99e7943d15d7cb6325c79d0837b3bf65344d80";
		Log.i(tag, "deviceID=" + deviceid);
		return deviceid;
	}
	/**
	 * 获取clinttype
	 */
	public static String getClienttype(Context mContext){
		
		 String clienttype =   "TCL_BIGPAD";
		Log.i(tag, "clienttype=" + clienttype);
		 
		 return clienttype;
	}
	public static String getDnum(Context mContext){
//		SqlCommon sqlcommon = new SqlCommon();	
//		 ContentResolver resolver = mContext.getContentResolver();
//		 String dnum =  sqlcommon.getDum(resolver);
//		 Log.i(tag,"dnum="+dnum);
//		 return dnum;
		return null;
	}
	/**
	 * 获取mac
	 */
	public static String getMAC(){
		if (WeiConstant.MAC == null) {
//			//有些平台add On 获取不到Mac
//			try {
//				WeiConstant.MAC=  TDeviceInfo.getInstance().getMACAddress().replace(":", "").toLowerCase();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}		
//		 Log.i(tag,"mac="+WeiConstant.MAC);
//		 return WeiConstant.MAC;
		
		WeiConstant.MAC = "00:08:22:80:0b:fc";
		}
		Log.i(tag, "mac=" + WeiConstant.MAC);
		return WeiConstant.MAC;
		}
	
	/**
	 * 获取softVersion
	 */
	public static String getSoftVersion(){
		String softVersion="000";
		
//		try {
//			softVersion=  TDeviceInfo.getInstance().getSoftwareVersion();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
				
		 Log.i(tag,"softVersion="+softVersion);
		 return softVersion;
	}
	/**
	 * 获取projectID
	 */
	public static int getProjectID() {
		int projectID = 0;

		// try {
		// projectID= TDeviceInfo.getInstance().getProjectID();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		Log.i(tag, "projectID=" + projectID);
		return projectID;
	}

	/**
	 * 获取开机次数
	 */
	public static int getBootTimes(Context mContext){
		int bootTimes=0;
		
//		try {
//			bootTimes=  SqlCommon.getBootTimes(mContext.getContentResolver());
//		} catch (java.lang.NoSuchMethodError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
				
		 Log.i(tag,"bootTimes="+bootTimes);
		 return bootTimes;
	}
	/**
	 * 获取sn
	 */
	public static String getSn(){
		String sn="000";
//		try {
//			sn=  TDeviceInfo.getInstance().getSn();
//		} catch (java.lang.NoSuchMethodError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
				
		 Log.i(tag,"sn="+sn);
		 return sn;
	}
	/**
	 * 获取UbootVersion
	 */
	public static String getUbootVersion(){
		String ubootVersion="000";
		
		/*try {
			ubootVersion=  TDeviceInfo.getInstance().getUbootVersion();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
				
		 Log.i(tag,"ubootVersion="+ubootVersion);
		 return ubootVersion;
	}
	
	public static String getMD5(String val) throws NoSuchAlgorithmException{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		return byteToString(md5.digest(val.getBytes()));

	/*	md5.update(val.getBytes());
		byte[] m = md5.digest();//加密
		return getString(m);*/
	}
	// 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }
    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }
    // 全局数组
    private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	private static String getString(byte[] b){
		StringBuffer sb = new StringBuffer();
		 for(int i = 0; i < b.length; i ++){
		  sb.append(b[i]);
		 }
		 return sb.toString();
	}

	/*
	 * 获取机器内存大小
	 */
	public static String getConfigure(){
//		try {
//			WeiConstant.WechatConfigure.CurConfigure = SystemProperties.get("persist.sys.getWechatConfigure");
//			Log.v(tag, "persist.sys.getWechatConfigure = "+ WeiConstant.WechatConfigure.CurConfigure);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
		//WeiConstant.WechatConfigure.CurConfigure =WeiConstant.WechatConfigure.SimpleVer;
		return WeiConstant.WechatConfigure.CurConfigure;
	}
	
	/**
	 * 获取huanid
	 */
	public static String getHuanid(Context mContext){
//		 SqlCommon sqlcommon = new SqlCommon();	
//		 ContentResolver resolver = mContext.getContentResolver();
//		 String huanid =  sqlcommon.getHuanid(resolver);
//		 Log.i(tag,"huanid="+huanid);
//		 return huanid;
		return null;
	}
	
	public static String getLocalIpAddress() {
    	try {
    		String ipv4;
    		List <NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
    		for (NetworkInterface ni: nilist) 
    		{
    			List<InetAddress>  ialist = Collections.list(ni.getInetAddresses());
    			for (InetAddress address: ialist){
    				if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4=address.getHostAddress())) 
    				{ 
    					return ipv4;
    				}
    			}
 
    		}
 
    	} catch (SocketException ex) {
    		//Log.e(LOG_TAG, ex.toString());
    	}
    	return null;
    }      
	//获取消息盒子ID
	public static String getMessage_Box_id(Context mContext){
		String[] str_user = { "userid", "create_time" };
	    String uriString = "content://com.android.tcl.messagebox/message_user";
	    String message_box_id = "";
	    Uri uri = Uri.parse(uriString);
	    try {
			Cursor c = mContext.getContentResolver().query(uri, str_user, null,
			     null, null);
			if (c != null && c.getCount() > 0) {
			   c.moveToFirst();
			   message_box_id = c.getString(c.getColumnIndex("userid")) == null ? ""
			        : c.getString(c.getColumnIndex("userid"));
			   Log.d(tag, "get Userid from db===========>is :" + message_box_id);
			} else {
				message_box_id = "";
			}

			if (c != null && !c.isClosed()) {
			   c.close();
			}
		} catch (SQLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
	    return message_box_id;
	}
	public static void delSrcFile(final String filename){
		Log.i("liyulin","000start to del file-filename="+filename);
		if(filename!=null&& !filename.equals("null")){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.i("liyulin","start to del file-filename="+filename);
					UIUtils.deleteFile(WeiConstant.DOWN_LOAD_FLASH_PATH+"/"+filename);
				}
			}).start();
			
		}
	}
	public static boolean isTopActivity(Context context){
        String topActivityname = "com.tcl.webchat.image.ImagePlayerActivity";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo>  tasksInfo = activityManager.getRunningTasks(1);  
        if(tasksInfo.size() > 0){  
            Log.i("liyulin", "---------------包名-----------"+tasksInfo.get(0).topActivity.getClassName());
            //应用程序位于堆栈的顶层  
            if(topActivityname.equals(tasksInfo.get(0).topActivity.getClassName())){  
                return true;  
            }  
        }  
        return false;
    }
	public static void chmodfile(String path){
		try {
	        String command = "chmod 777 " + path;
	        Log.i("chmod", "command = " + command);
	        Runtime runtime = Runtime.getRuntime(); 
	
	        Process proc = runtime.exec(command);
	       } catch (IOException e) {
	        Log.i("zyl","chmod fail!!!!");
	        e.printStackTrace();
	       }
	}
	
	
	public static   ArrayList<AppInfo> getSystemApp(Context mContext)
	{
		ArrayList<AppInfo> appInfoList = new ArrayList<AppInfo>();
		if (appInfoList.size() > 0)
		{
			return appInfoList;
		}
		File file;
		String clientTypeModel = CommonsFun.getClienttype(mContext);
		/** MTK和RTK平台预装文件放置系统目录下 **/
		if (clientTypeModel.contains("MT")
				|| clientTypeModel.contains("TCL-CN-RT")
				|| clientTypeModel.contains("ROWA-CN-RT")
				|| clientTypeModel.contains("TCL-CN-AM6C-A71C"))
		{
			// MTK读取系统配置文件为system/etc
			file = new File("/system/etc/appinfo", "appconfig.xml");
		}
		else if ("TCL-CN-MS901K-H9500A-UDM".equals(clientTypeModel))
		{
			file = new File("/config/appinfo", "appconfig_h9500.xml");
		}
		else
		{
			// MSTAR读取系统配置config
			file = new File("/config/appinfo", "appconfig.xml");
		}
		Log.i(tag,"zongss 读取系统配置文件 : " + file.getAbsolutePath() + "---exist:"
				+ file.exists());
		String res = "";
		if (file.exists())
		{
			try
			{
				FileInputStream fin = new FileInputStream(file);
				int length = fin.available();
				byte[] buffer = new byte[length];
				fin.read(buffer);
				res = EncodingUtils.getString(buffer, "UTF-8");
				fin.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			ParseConfigXml paser = new ParseConfigXml(appInfoList);
			paser.getSysXmlAppList(res);
			return appInfoList;
		}
		return null;
	}
	public static  String getTopActivityName(Context context){
	       
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo>  tasksInfo = activityManager.getRunningTasks(1);  
        if(tasksInfo.size() > 0){  
            System.out.println("---------------包名-----------"+tasksInfo.get(0).topActivity.getPackageName());
            //应用程序位于堆栈的顶层  
            return tasksInfo.get(0).topActivity.getPackageName();  
        }  
        return null;
    }
	
	public static String get_apkver(Context context) 
	{ 	     
	    PackageManager packageManager = context.getPackageManager();
	    String versionName = "";
	    PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			versionName = packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		Log.i(tag,"apk_version="+versionName);
	    return versionName; 
	}
}
