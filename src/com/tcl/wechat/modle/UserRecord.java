package com.tcl.wechat.modle;

import java.util.Date;

/**
 * 消息记录
 * @author rex.lei
 *
 */
public class UserRecord {
	
	private String openId;
	private String accessToken;
	private String msgType;
	private String content;
	private String imageurl;
	private String format;
	private String mediaId;
	private String humbmediaId;
	private Date createTime;
	private Date expireTime;
	private boolean readed;
	private String fileName;
	private int fileSize;
	private Date fileTime;
	
	private UserRecord() {
		super();
	}

	private UserRecord(String openId, String accessToken, String msgType,
			String content, String imageurl, String format, String mediaId,
			String humbmediaId, Date createTime, Date expireTime,
			boolean readed, String fileName, int fileSize, Date fileTime) {
		super();
		this.openId = openId;
		this.accessToken = accessToken;
		this.msgType = msgType;
		this.content = content;
		this.imageurl = imageurl;
		this.format = format;
		this.mediaId = mediaId;
		this.humbmediaId = humbmediaId;
		this.createTime = createTime;
		this.expireTime = expireTime;
		this.readed = readed;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.fileTime = fileTime;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getHumbmediaId() {
		return humbmediaId;
	}

	public void setHumbmediaId(String humbmediaId) {
		this.humbmediaId = humbmediaId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public boolean isReaded() {
		return readed;
	}

	public void setReaded(boolean readed) {
		this.readed = readed;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public Date getFileTime() {
		return fileTime;
	}

	public void setFileTime(Date fileTime) {
		this.fileTime = fileTime;
	}

	@Override
	public String toString() {
		return "UserRecord [openId=" + openId + ", accessToken=" + accessToken
				+ ", msgType=" + msgType + ", content=" + content
				+ ", imageurl=" + imageurl + ", format=" + format
				+ ", mediaId=" + mediaId + ", humbmediaId=" + humbmediaId
				+ ", createTime=" + createTime + ", expireTime=" + expireTime
				+ ", readed=" + readed + ", fileName=" + fileName
				+ ", fileSize=" + fileSize + ", fileTime=" + fileTime + "]";
	}
}
