package com.tcl.wechat.model.file;

/**
 * 文件下载类型
 * @author rex.lei
 *
 */
public class FileLoader {
	
	private String fileName;
	private int downloadId;
	private long downLoadSize;
	private long totalSize;
	private int downLoadState;
	
	public FileLoader() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FileLoader(String fileName, int downloadId, long downLoadSize,
			long totalSize, int downLoadState) {
		super();
		this.fileName = fileName;
		this.downloadId = downloadId;
		this.downLoadSize = downLoadSize;
		this.totalSize = totalSize;
		this.downLoadState = downLoadState;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(int downloadId) {
		this.downloadId = downloadId;
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

	@Override
	public String toString() {
		return "DownloadFile [fileName=" + fileName + ", downloadId="
				+ downloadId + ", downLoadSize=" + downLoadSize
				+ ", totalSize=" + totalSize + ", downLoadState="
				+ downLoadState + "]";
	}
}
