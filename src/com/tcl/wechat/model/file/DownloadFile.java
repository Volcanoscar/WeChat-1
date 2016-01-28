package com.tcl.wechat.model.file;

import java.io.File;

/**
 * 文件下载类型
 * @author rex.lei
 *
 */
public class DownloadFile {
	
	private String fileName;
	private String downloadId;
	private long sessionid;
	private long downLoadSize;
	private long totalSize;
	private int downLoadState;
	private String md5; //识别文件是否重复下载
	private File saveFile;
	
	public DownloadFile() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DownloadFile(String fileName, String downloadId, long sessionid,
			long downLoadSize, long totalSize, int downLoadState, String md5,
			File saveFileName) {
		super();
		this.fileName = fileName;
		this.downloadId = downloadId;
		this.sessionid = sessionid;
		this.downLoadSize = downLoadSize;
		this.totalSize = totalSize;
		this.downLoadState = downLoadState;
		this.md5 = md5;
		this.saveFile = saveFileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(String downloadId) {
		this.downloadId = downloadId;
	}

	public long getSessionid() {
		return sessionid;
	}

	public void setSessionid(long sessionid) {
		this.sessionid = sessionid;
	}

	public long getDownLoadSize() {
		return downLoadSize;
	}

	public void setDownLoadSize(long downLoadSize) {
		this.downLoadSize = downLoadSize;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public int getDownLoadState() {
		return downLoadState;
	}

	public void setDownLoadState(int downLoadState) {
		this.downLoadState = downLoadState;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public File getSaveFile() {
		return saveFile;
	}

	public void setSaveFile(File saveFile) {
		this.saveFile = saveFile;
	}

	@Override
	public String toString() {
		return "DownloadFile [fileName=" + fileName + ", downloadId="
				+ downloadId + ", sessionid=" + sessionid + ", downLoadSize="
				+ downLoadSize + ", totalSize=" + totalSize
				+ ", downLoadState=" + downLoadState + ", md5=" + md5
				+ ", saveFile=" + saveFile.getAbsolutePath() + "]";
	}
}
