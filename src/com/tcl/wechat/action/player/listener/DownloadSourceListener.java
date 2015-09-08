package com.tcl.wechat.action.player.listener;


/**
 * 资源下载监听器
 * @author rex.lei
 *
 */
public interface DownloadSourceListener {

	/**
	 * 开始下载
	 */
	public void startDownLoad();
	
	/**
	 * 下载完成
	 */
	public void OnLoadCompleted();
	
	/**
	 * 下载错误
	 * @param errorCode：错误码
	 */
	public void OnLoadError(int errorCode);
}
