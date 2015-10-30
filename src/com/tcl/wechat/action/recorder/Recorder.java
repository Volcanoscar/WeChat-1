package com.tcl.wechat.action.recorder;

import java.io.Serializable;

/**
 * 录音文件信息类
 * @author rex.lei
 *
 */
public class Recorder implements Serializable{
	
	private static final long serialVersionUID = -3180669186863934531L;
	
	/**
	 * 录音文件名称
	 */
	private String fileName;
	/**
	 * 录音时间长度
	 */
	private float seconds;
	
	/**
	 * 录音文件长度
	 */
	private float fileSize;
	
	public Recorder() {
		super();
	}

	public Recorder(String fileName, float seconds, float fileSize) {
		super();
		this.fileName = fileName;
		this.seconds = seconds;
		this.fileSize = fileSize;
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

	public float getFileSize() {
		return fileSize;
	}

	public void setFileSize(float fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public String toString() {
		return "Recorder [fileName=" + fileName + ", seconds=" + seconds
				+ ", fileSize=" + fileSize + "]";
	}
}
