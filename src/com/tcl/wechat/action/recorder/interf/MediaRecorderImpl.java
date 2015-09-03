package com.tcl.wechat.action.recorder.interf;

/**
 * 录音接口类
 * @author rex.lei
 *
 */
public interface MediaRecorderImpl {

	/**
	 * 准备MediaRecorder
	 */
	public void prepare();
	
	/**
	 * 取消
	 */
	public void cancel();
	
	/**
	 * 释放资源
	 */
	public void release();
	
	/**
	 * 获取音量等级
	 * @return
	 */
	public int getLevel();
}
