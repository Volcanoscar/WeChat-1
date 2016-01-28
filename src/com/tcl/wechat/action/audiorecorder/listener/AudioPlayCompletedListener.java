package com.tcl.wechat.action.audiorecorder.listener;

/**
 * 播放完成监听器
 * @author rex.lei
 *
 */
public interface AudioPlayCompletedListener {

	/**
	 * 播放完成
	 */
	public void onCompleted();
	
	
	/**
	 * 播放错误
	 * @param errorcode 错误码
	 */
	public void onError(int errorcode);
}
