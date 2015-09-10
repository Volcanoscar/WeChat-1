package com.tcl.wechat.action.player.listener;


/**
 * 资源下载状态监听器
 * @author rex.lei
 *
 */
public interface DownloadStateListener {
	
	/**
	 * 开始下载
	 */
	public void startDownLoad();
	
	/**
	 * 下载完成
	 */
	public void onDownLoadCompleted();
	
	/**
	 * 下载错误
	 * @param errorCode：错误码
	 * @see DownloadState
	 */
	public void onDownLoadError(int errorCode);
	
	/**
	 * 文件下载进度
	 * @param progress
	 */
	public void onProgressUpdate(int progress);
}
