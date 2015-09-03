package com.tcl.wechat.action.recorder;

/**
 * 录音文件信息类
 * @author rex.lei
 *
 */
public class Recorder {
	
	/**
	 * 录音文件名称
	 */
	private String fileName;
	/**
	 * 录音时间长度
	 */
	private float seconds;
	
	public Recorder() {
		super();
	}

	public Recorder(String fileName, float seconds) {
		super();
		this.fileName = fileName;
		this.seconds = seconds;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public float getSeconds() {
		return seconds;
	}

	public void setSeconds(float seconds) {
		this.seconds = seconds;
	}

	@Override
	public String toString() {
		return "Recoder [fileName=" + fileName + ", seconds=" + seconds + "]";
	}
}
