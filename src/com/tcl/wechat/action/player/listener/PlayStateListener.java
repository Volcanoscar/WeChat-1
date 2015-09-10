package com.tcl.wechat.action.player.listener;

/**
 * 视频播放状态监听器
 * @author rex.lei
 *
 */
public interface PlayStateListener {

	/**
	 * 开始播放
	 */
	public void startToPlay();
	
	/**
	 * 视频缓冲监听器
	 * 	适用于播放网络视频
	 */
	public void onBufferingUpdate();
	
	/**
	 * 播放失败
	 * @param errorCode 错误码
	 */
	public void onError(int errorCode);
	
	/**
	 * 播放完成
	 */
	public void onPlayCompletion();
}
