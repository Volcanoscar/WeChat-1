/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 微信消息类型
 * @author rex.lei
 */
public class WeiXinMsgRecorder implements Parcelable {
	
	private String openid;
	
	/**
	 * 消息发送者openid
	 */
	private String toOpenid;
	
	/** 消息的类型:文本（text）、图片（image）、音频（voice）、视频（video）、链接（link）、地理位置(location) 和 小视频 (shortvideo)*/
	private String msgtype;
	
	/** 防止丢失消息，回复id给服务器，服务器判断是否有消息丢失*/
	private String msgid;
	
	/** 
	 * msgtype = text时，表示文本内容；
	 * msgtype = voice时，表示语音内容；
	 * msgtype = image/video, 标识缩略图的存储路径
	 * msgtype = link时，标识链接标题
	 */
	private String content;
	
	/**
	 * msgtype = image时，表示图片下载地址；
	 * msgtype = voice时，表示音频下载地址；
	 * msgtype = video时，表示视频下载地址；
	 * msgtype = link 时，表示链接地址。
	 */
	private String url;
	
	/**
	 * 地理位置x坐标
	 */
	private String location_x;
	
	/**
	 * 地理位置y坐标
	 */
	private String location_y;
	
	/**
	 * 地理位置描述信息
	 */
	private String label;
	
	/**
	 * 链接标题内容
	 */
	private String title;
	
	/**
	 * 链接内容描述
	 */
	private String description;
	
	/** 
	 * msgtype = voice时，语音格式，如amr，speex等*/
	private String format;
	
	/** 消息创建时间*/
	private String createtime;
	
	/** 多媒体文件标识,如果为空，表示失败*/
	private String mediaid;
	
	/** 当msgtype是image、voice时，可以调用微信下载接口，下载缩略图*/
	private String thumbmediaid;
	
	/** 判断当前是否已读*/
	private String read;
	
	/** 视频、音频、图片文件存储路径*/
	private String fileName;
	
	/** 视频、音频、图片文件大小*/
	private String fileSize;
	
	/** 视频、音频文件时间长度*/
	private String fileTime;
	
	/** 离线消息*/
	private String offlinemsg;
	
	/**
	 * 消息来源
	 * 	true：接收到的消息
	 * 	false:发送出去的消息
	 */
	private boolean isReceived;

	public WeiXinMsgRecorder() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public WeiXinMsgRecorder(Parcel source) {
		super();
		this.openid = source.readString();
		this.toOpenid = source.readString();
		this.msgtype = source.readString();
		this.msgid = source.readString();
		this.content = source.readString();
		this.url = source.readString();
		this.location_x = source.readString();
		this.location_y =  source.readString();
		this.label = source.readString();
		this.title = source.readString();
		this.description =  source.readString();
		this.format = source.readString();
		this.createtime = source.readString();
		this.mediaid = source.readString();
		this.thumbmediaid = source.readString();
		this.read = source.readString();
		this.fileName = source.readString();
		this.fileSize = source.readString();
		this.fileTime = source.readString();
		this.offlinemsg = source.readString();
		this.isReceived = (source.readByte() == 1) ? true : false;
	}
	
	public WeiXinMsgRecorder(String openid, String toOpenid, String msgtype,
			String msgid, String content, String url, String location_x,
			String location_y, String label, String title, String description,
			String format, String createtime, String mediaid,
			String thumbmediaid, String read, String fileName, String fileSize,
			String fileTime, String offlinemsg, boolean isReceived) {
		super();
		this.openid = openid;
		this.toOpenid = toOpenid;
		this.msgtype = msgtype;
		this.msgid = msgid;
		this.content = content;
		this.url = url;
		this.location_x = location_x;
		this.location_y = location_y;
		this.label = label;
		this.title = title;
		this.description = description;
		this.format = format;
		this.createtime = createtime;
		this.mediaid = mediaid;
		this.thumbmediaid = thumbmediaid;
		this.read = read;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.fileTime = fileTime;
		this.offlinemsg = offlinemsg;
		this.isReceived = isReceived;
	}
	
	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	public String getToOpenid() {
		return toOpenid;
	}

	public void setToOpenid(String toOpenid) {
		this.toOpenid = toOpenid;
	}

	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLocation_x() {
		return location_x;
	}

	public void setLocation_x(String location_x) {
		this.location_x = location_x;
	}

	public String getLocation_y() {
		return location_y;
	}

	public void setLocation_y(String location_y) {
		this.location_y = location_y;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getMediaid() {
		return mediaid;
	}

	public void setMediaid(String mediaid) {
		this.mediaid = mediaid;
	}

	public String getThumbmediaid() {
		return thumbmediaid;
	}

	public void setThumbmediaid(String thumbmediaid) {
		this.thumbmediaid = thumbmediaid;
	}

	public String getRead() {
		return read;
	}

	public void setRead(String read) {
		this.read = read;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileTime() {
		return fileTime;
	}

	public void setFileTime(String fileTime) {
		this.fileTime = fileTime;
	}
	
	public String getOfflinemsg() {
		return offlinemsg;
	}

	public void setOfflinemsg(String offlinemsg) {
		this.offlinemsg = offlinemsg;
	}
	
	
	public boolean isReceived() {
		return isReceived;
	}

	public void setReceived(boolean isReceived) {
		this.isReceived = isReceived;
	}

	@Override
	public String toString() {
		return "WeiXinMsgRecorder [openid=" + openid + ", toOpenid=" + toOpenid
				+ ", msgtype=" + msgtype + ", msgid=" + msgid + ", content="
				+ content + ", url=" + url + ", location_x=" + location_x
				+ ", location_y=" + location_y + ", label=" + label
				+ ", title=" + title + ", description=" + description
				+ ", format=" + format + ", createtime=" + createtime
				+ ", mediaid=" + mediaid + ", thumbmediaid=" + thumbmediaid
				+ ", read=" + read + ", fileName=" + fileName + ", fileSize="
				+ fileSize + ", fileTime=" + fileTime + ", offlinemsg="
				+ offlinemsg + ", isReceived=" + isReceived + "]";
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(openid);
		dest.writeString(toOpenid);
		dest.writeString(msgtype);
		dest.writeString(msgid);
		dest.writeString(content);
		dest.writeString(url);
		dest.writeString(location_x);
		dest.writeString(location_y);
		dest.writeString(label);
		dest.writeString(title);
		dest.writeString(description);
		dest.writeString(format);
		dest.writeString(createtime);
		dest.writeString(mediaid);
		dest.writeString(thumbmediaid);
		dest.writeString(read);
		dest.writeString(fileName);
		dest.writeString(fileSize);
		dest.writeString(fileTime);
		dest.writeString(offlinemsg);
		dest.writeByte((byte)(isReceived ? 1 : 0));
	}
	
	public static Parcelable.Creator<WeiXinMsgRecorder> CREATOR = new Creator<WeiXinMsgRecorder>(){

		@Override
		public WeiXinMsgRecorder createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new WeiXinMsgRecorder(source);
		}

		@Override
		public WeiXinMsgRecorder[] newArray(int size) {
			// TODO Auto-generated method stub
			return new WeiXinMsgRecorder[size];
		}
	};
}
