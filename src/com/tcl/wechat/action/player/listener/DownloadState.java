package com.tcl.wechat.action.player.listener;

public class DownloadState {
	
	/**
	 * 错误码
	 */
	public static final int ERROR_MEDIA_MOUNTED = 101; 		//內存卡掛載失敗
	public static final int ERROR_LOWER_MEMORY = 102;  		//内存不足
	public static final int ERROR_FILE_NOTFOUND = 103; 		//下载文件不存在
	public static final int ERROR_NET_NOTAVAILABLE = 103; 	//网络不可用
	
	/**
	 * 下载状态
	 */
	public static final int STATE_START_DOWNLOAD = 201; 		//开始下载
	public static final int STATE_DOWNLOAD_COMPLETED = 202; 	//下载完成
	public static final int STATE_DOWNLOAD_FAILED = 203; 		//下载失败
	public static final int STATE_UPDATE_PROGRESS = 204; 		//更新下载进度
}
