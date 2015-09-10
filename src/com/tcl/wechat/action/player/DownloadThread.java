package com.tcl.wechat.action.player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import com.tcl.wechat.action.player.listener.DownloadState;
import com.tcl.wechat.action.player.listener.DownloadStateListener;
import com.tcl.wechat.modle.data.DataFileTools;

public class DownloadThread extends Thread { 
   
	private String TAG = "DownloadThread";
	
	private static final int TIME_OUT = 8000;
	private static final int BUFFER_SIZE = 1024;
	
	private String mFileName; 
    private String mDownloadUrl; 
    private DownloadStateListener mDownloadListener;
    
    private DataFileTools mFileTools = DataFileTools.getInstance();
    
   
	public DownloadThread(String downloadUrl, DownloadStateListener listener) { 
	    mDownloadUrl = downloadUrl; 
	    mDownloadListener = listener;
	    mFileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
	} 
	
	/**
	 * 获取保存文件路径
	 * @return
	 */
	private File getSaveFilePath(){
		File file = null;
		try {
			if (mFileTools.getRecordVideoPath() != null){
				file = new File(mFileTools.getRecordVideoPath());
				if (!file.exists()){
					file.mkdirs();
				}
				File savaFile = new File(file, mFileName);
				return savaFile;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 下载完成
	 */
	private void onComplete(){
		if(mDownloadListener != null){
			mDownloadListener.onDownLoadCompleted();
		}
	}
	
	/**
	 * 更新下载进度
	 * @param progress
	 */
	private void onUpdateProgress(int progress){
		if(mDownloadListener != null){
			mDownloadListener.onProgressUpdate(progress);
		}
	}
	
	/**
	 * 错误事件
	 * @param errorCode 错误码
	 * @see DownloadState
	 */
	private void onError(int errorCode){
		if (mDownloadListener != null){
			mDownloadListener.onDownLoadError(errorCode);
		}
	}
	
	@Override
	public void run(){
		int downFileSize = 0;
		int progress = 0;
		if (DataFileTools.getInstance().isSdCardExist()){
		   File saveFilePath = getSaveFilePath();
		   //文件路径不存在
		   if (saveFilePath == null ){
			   onError(DownloadState.ERROR_FILE_NOTFOUND);
//			   return ;
		   }
		   Log.i(TAG, "saveFilePath:" + saveFilePath);
		   
		   URL url;
		   InputStream is = null;
		   FileOutputStream fos = null;
		   try {
			   url = new URL(mDownloadUrl);
			   HttpURLConnection httpConn = (HttpURLConnection)url.openConnection(); 
			   int fileSize = httpConn.getContentLength();
			   Log.i(TAG, "fileSize:" + fileSize / 8 + ", freeMemory:" 
					   + Runtime.getRuntime().freeMemory() );
			   //内存不足
			   if (Runtime.getRuntime().freeMemory() <= fileSize /8){
				   onError(DownloadState.ERROR_LOWER_MEMORY);
//				   return ;
			   }
			   //读取超时时间 毫秒级 
			   httpConn.setConnectTimeout(TIME_OUT);
			   httpConn.setRequestMethod("GET");
			   if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				   	is = httpConn.getInputStream(); 
	              	fos = new FileOutputStream(saveFilePath);
	              	int len = 0;
	              	int tempProgress = -1; 
	              	byte[] buffer = new byte[BUFFER_SIZE];
	              	while ((len = is.read(buffer)) != -1) {
	              		downFileSize = downFileSize + len;
	              		// 下载进度 
	              		progress = (int) (downFileSize * 100.0 / fileSize); 
	              		fos.write(buffer, 0, len);
	            	  
	              		synchronized (this) { 
	              			if (downFileSize == fileSize) { 
	              				// 下载完成 
	              				onComplete();
	              			} else if (tempProgress != progress) {
	              				//下载中，更新进度
	                           	tempProgress = progress; 
	                           	Log.i(TAG, "progress：" + progress);
	                           	onUpdateProgress(progress);
	                       } 
	              		} 
	              	}
	              	fos.flush();
	           	}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	   } else {
		   onError(DownloadState.ERROR_MEDIA_MOUNTED);
	   }
	}
}