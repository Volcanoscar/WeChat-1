package com.tcl.wechat.action.player.listener;

/**
 * 视频播放监听器
 * @author rex.lei
 *
 */
public interface MediaPlayListener {

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
	 * 播放完成
	 */
	public void onPlayCompletion();
}
