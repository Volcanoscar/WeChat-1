/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.modle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 微信消息类型
 * @author rex.lei
 */
public class WeiXinMsgRecorder implements Parcelable {
	
	private String openid;
	
	/** 消息的类型:文本（text）、图片（image）、音频（voice）、视频（video）和链接（link）*/
	private String msgtype;
	
	/** 防止丢失消息，回复id给服务器，服务器判断是否有消息丢失*/
	private String msgid;
	
	/** 
	 * msgtype = text时，表示文本内容；
	 * msgtype = voice时，表示语音内容；
	 * msgtype = image/video, 标识缩略图的存储路径
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
	 * msgtype = voice时，语音格式，如amr，speex等*/
	private String format;
	
	/** msgtype 为image、voice、video时，若没有下载地址url，则通过调用公共访问接口从微信服务器下载多媒体文件，acesstoken就是下载时用到的凭证*/
	private String accesstoken;
	
	/** 消息创建时间*/
	private String createtime;
	
	/** accesstoken的过期时间*/
	private String expiretime;
	
	/** 多媒体文件标识*/
	private String mediaid;
	
	/** 当msgtype是image、voice时，可以调用微信下载接口，下载缩略图*/
	private String thumbmediaid;
	
	/** 判断当前是否已读*/
	private String read;
	
	/** 视频文件存储路径*/
	private String fileName;
	
	/** 视频文件大小*/
	private String fileSize;
	
	/** 视频文件时间长度*/
	private String fileTime;
	
	/** 离线消息*/
	private String offlinemsg;

	public WeiXinMsgRecorder() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public WeiXinMsgRecorder(Parcel source) {
		super();
		this.openid = source.readString();
		this.msgtype = source.readString();
		this.msgid = source.readString();
		this.content = source.readString();
		this.url = source.readString();
		this.format = source.readString();
		this.accesstoken = source.readString();
		this.createtime = source.readString();
		this.expiretime = source.readString();
		this.mediaid = source.readString();
		this.thumbmediaid = source.readString();
		this.read = source.readString();
		this.fileName = source.readString();
		this.fileSize = source.readString();
		this.fileTime = source.readString();
		this.offlinemsg = source.readString();
	}
	
	public WeiXinMsgRecorder(String openid, String accesstoken,
			String msgtype, String msgid, String content, String url,
			String format, String createtime, String expiretime,
			String mediaid, String thumbmediaid, String read,
			String fileName, String fileSize, String fileTime, 
			String offlinemsg) {
		super();
		this.openid = openid;
		this.accesstoken = accesstoken;
		this.msgtype = msgtype;
		this.msgid = msgid;
		this.content = content;
		this.url = url;
		this.format = format;
		this.createtime = createtime;
		this.expiretime = expiretime;
		this.mediaid = mediaid;
		this.thumbmediaid = thumbmediaid;
		this.read = read;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.fileTime = fileTime;
		this.offlinemsg = offlinemsg;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
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

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getAccesstoken() {
		return accesstoken;
	}

	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getExpiretime() {
		return expiretime;
	}

	public void setExpiretime(String expiretime) {
		this.expiretime = expiretime;
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
	
	@Override
	public String toString() {
		return "WeiXinMsg [openid=" + openid + ", msgtype=" + msgtype
				+ ", msgid=" + msgid + ", content=" + content + ", url=" + url
				+ ", format=" + format + ", accesstoken=" + accesstoken
				+ ", createtime=" + createtime + ", expiretime=" + expiretime
				+ ", mediaid=" + mediaid + ", thumbmediaid=" + thumbmediaid
				+ ", read=" + read + ", fileName=" + fileName + ", fileSize=" + fileSize
				+ ", fileTime=" + fileTime + ", offlinemsg=" + offlinemsg + "]";
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(openid);
		dest.writeString(msgtype);
		dest.writeString(msgid);
		dest.writeString(content);
		dest.writeString(url);
		dest.writeString(format);
		dest.writeString(accesstoken);
		dest.writeString(createtime);
		dest.writeString(expiretime);
		dest.writeString(mediaid);
		dest.writeString(thumbmediaid);
		dest.writeString(read);
		dest.writeString(fileName);
		dest.writeString(fileSize);
		dest.writeString(fileTime);
		dest.writeString(offlinemsg);
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
