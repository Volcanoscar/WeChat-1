/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.tcl.wechat.common.WeiConstant;

/**
 * @author zhangjunjian
 *
 */
public class UIUtils {

	
	public static boolean isNetworkAvailable(Context context) {
		return getAllNetworkStates(context);
	}

	public synchronized static boolean getAllNetworkStates(Context mContext) {
		ConnectivityManager connec = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connec == null)
			return false;

		/*NetworkInfo[] allinfo = connec.getAllNetworkInfo();
		if (allinfo != null) {
			for (NetworkInfo info : allinfo) {

				if (info.isAvailable() && info.isConnected()) {
					return true;
				}
			}
		}*/
		NetworkInfo activeInfo = connec.getActiveNetworkInfo();			
		if (activeInfo != null && activeInfo.isConnected()
				&& activeInfo.isAvailable()) {							
			return true;
		} 
		return false;
	}
	
	public static String inflterNull(String str){
		if (str != null){
			return str;
		}
		return "";
	}
	
	public static String getTimeShort(int milliseconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date currentTime = new Date(milliseconds);
		String dateString = formatter.format(currentTime);
		if (milliseconds < 3600 * 1000) {
			return dateString.substring(3);
		}

		currentTime = null;
		formatter = null;

		return dateString;
	}
	
	public static void deleteFile(String path) {
		
		File file = new File(path);
		
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					String sPath = files[i].getAbsolutePath();
					deleteFile(sPath); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
			Log.d("andy", "文件不存在！");
		}
	}
	
	/**
	 * 判断SDCard是否存在且可读写
	 * 
	 * @return
	 */
	public static boolean ExistSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 判断当前文件是否存在
	 */
	public static boolean isExistFile(String path) {
		
		File file = new File(path);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}
	/*public static boolean isSpaceFull(Bitmap bitmap){
		if (!ExistSDCard()){
			return false;
		}
		// 文件
		if (getSDFreeSize() > 3*bitmap.getByteCount()){
			return true;
		}
		
		return false;
	}*/
	public static boolean isSpaceFull(int len){
		if (!ExistSDCard()){
			return false;
		}
		// 文件
		if (getSDFreeSize() > 3*len){
			return true;
		}
		
		return false;
	}
	/**
	 * SD卡剩余空间
	 * 
	 * @return
	 */
	public static long getSDFreeSize() {
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize();
		// 空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocks();
		// 返回SD卡空闲大小
		// return freeBlocks * blockSize; //单位Byte
		// return (freeBlocks * blockSize)/1024; //单位KB
		return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
	}
	
	/** 
     * 获取文件夹大小 
     * @param file File实例 
     * @return long 单位为M 
     * @throws Exception 
     */  
    public static long getVideoFolderSize()throws Exception{ 
    	File file = new File(WeiConstant.DOWN_LOAD_FLASH_PATH);
        long size = 0;  
        java.io.File[] fileList = file.listFiles();  
        for (int i = 0; i < fileList.length; i++)  
        {  
            if (fileList[i].isFile())  
            {  
            	 size = size + fileList[i].length();  
            }
        }  
        return size/1048576;  
    }  
    /** 
     * 在A71S video没有全屏，设置全屏，当前是storage需要scale
     * 
     */
    public static void scaleScreen(Context mContext){
//		final VideoWindowRect videoWindowType = new VideoWindowRect();
//		videoWindowType.x =100;
//		videoWindowType.y = 100;
//		videoWindowType.width = 500;
//		videoWindowType.height = 300;
		try
		{
//		TTvPictureManager.getInstance(mContext).scaleVideoWindow(EnTCLWindow.EN_TCL_MAIN, videoWindowType);
		}
		catch (Exception e)
		{
		e.printStackTrace();
		}
	}
	
	public static String getDownLoadPath(){
		
		if (ExistSDCard()){		
			return WeiConstant.DOWN_LOAD_SDCARD_PATH;			
		}
		
		return WeiConstant.DOWN_LOAD_FLASH_PATH;
	}
	public static void createPath(String path) {
	     File file = new File(path);
		 if (!file.exists()) {
		        file.mkdir();
	     }
	}
	
	
	private long timeOffset = 24 * 60 * 60 * 1000;// 一天的毫秒数
	public static String Milli2Date(String times,Context mContext) {
		long timeOffset = 24 * 60 * 60 * 1000;
		String[] splittime = times.split(" ");
		if (splittime.length > 0) {
			Log.v("zwh23", "splittime>>>" + splittime[0]);

			// SimpleDateFormat formatter1 = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat formatterHour = new SimpleDateFormat("HH:mm:ss");

			// formatter1.
			long time1 = System.currentTimeMillis();
			long time2 = time1 - timeOffset;
			long time3 = time2 - timeOffset;
			long time4 = time3 - timeOffset;

			String dateString = splittime[0];
			Date currentTime1 = new Date(time1);
			String dateString1 = formatter.format(currentTime1);
			Date currentTime2 = new Date(time2);
			String dateString2 = formatter.format(currentTime2);
			Date currentTime3 = new Date(time3);
			String dateString3 = formatter.format(currentTime3);
			splittime[1] = splittime[1].substring(0, 5);//不显示秒
//			if (dateString.equals(dateString1)) {
//				return mContext.getString(R.string.toDay)  + splittime[1];
//			} else if (dateString.equals(dateString2)) {
//				return mContext.getString(R.string.yesterDay)  + splittime[1];
//			} else if (dateString.equals(dateString3)) {
//				return mContext.getString(R.string.beforeYesterDay)
//						+ splittime[1];
//			} else {
//				return dateString + " " + splittime[1];
//			}
		} else {
			return times;
		}
		return times;
	}
}
