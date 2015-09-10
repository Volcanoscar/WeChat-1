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
	 * 下次次数，如如果3次未下载成功，则不去下载
	 */
	private int mDownLoadNum = 3;
	
	private String mDownloadUrl;
	
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
		mDownloadUrl = urlStr;
		download(mDownloadUrl, mListener);
	}
	
	/**
	 * 下载文件
	 * @param url 文件url地址
	 */
	private void download(String url, DownloadStateListener listener){
		new DownloadThread(url, listener).start();
	}
}
