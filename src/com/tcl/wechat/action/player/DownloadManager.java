package com.tcl.wechat.action.player;

import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.action.player.listener.DownloadStateListener;

/**
 * 视频下载管理类
 * @author rex.lei
 *
 */
public class DownloadManager {
	
	private static final String TAG = "DownloadManager";
	
	private static DownloadManager mInstance;
	
	
	/**
	 * 下载状态监听器
	 */
	private DownloadStateListener mListener;
	public void setDownloadStateListener(DownloadStateListener listener){
		mListener = listener;
	}
	
	private DownloadManager(){
		
	}
	
	public static DownloadManager getInstace(){
		if (mInstance == null){
			synchronized (DownloadManager.class) {
				if (mInstance == null){
					mInstance = new DownloadManager();
				}
			}
		}
		return mInstance;
	}
	
	public void startToDownload(String urlStr){
		Log.i(TAG, "start to download file :" + urlStr);
		if (TextUtils.isEmpty(urlStr)){
			return ;
		}
		download(urlStr, null, mListener);
	}
	
	/**
	 * 开始下载文件
	 * @param urlStr 文件Url
	 * @param type 文件类型
	 */
	public void startToDownload(String urlStr, String type){
		Log.i(TAG, "start to download file :" + urlStr);
		if (TextUtils.isEmpty(urlStr)){
			return ;
		}
		download(urlStr, type, mListener);
	}
	
	/**
	 * 下载文件
	 * @param url 文件url地址
	 */
	private void download(String url, String type, DownloadStateListener listener){
		new DownloadThread(url, type, listener).start();
	}
	
}
