package com.tcl.wechat.action.audiorecorder.listener;

import com.tcl.wechat.action.audiorecorder.Recorder;

/**
 * 录音结束回调方法
 * @author rex.lei
 *
 */
public interface AudioRecorderStateListener {
	
	/**
	 * 开始录音
	 */
	public void startToRecorder();
	
	/**
	 * 录音结束
	 * @param recorder 录音文件信息
	 */
	public void onCompleted(Recorder recorder);
}
