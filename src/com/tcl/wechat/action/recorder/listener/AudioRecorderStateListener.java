package com.tcl.wechat.action.recorder.listener;

import com.tcl.wechat.action.recorder.Recorder;

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
	 * @param recoder 录音文件信息
	 */
	public void onCompleted(Recorder recoder);
}
