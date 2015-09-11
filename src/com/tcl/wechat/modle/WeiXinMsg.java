/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.modle;

import java.io.Serializable;

public class WeiXinMsg implements Serializable {
	
	private static final long serialVersionUID = -3570320519257495495L;
	
	/** 消息的类型，包括文本（text）、图片（image）、换台（channel）、音频（voice）、视频（video）和链接（link）*/
	private String msgtype;
	private String msgid;//防止丢失消息，回复id给服务器，服务器判断是否有消息丢失
	/** 当msgtype是text时，表示文本内容，当msgtype是channel时表示频道名称，当msgtype为视频或者图片时，为缩略图的存储路径*/
	private String content;
	/** 当msgtype是image时，表示图片下载地址，当msgtype是voice时表示音频下载地址，当msgtype是video时表示视频下载地址，当msgtype是link时表示链接地址*/
	private String url;
	/** 当msgtype是voice时，语音格式，如amr，speex等*/
	private String format;
	/** 消息创建时间*/
	private String createtime;
	/** 当msgtype是image、voice、video时，若没有下载地址url，则通过调用公共访问接口从微信服务器下载多媒体文件，acesstoken就是下载时用到的凭证*/
	private String accesstoken;
	/** accesstoken的过期时间*/
	private String mediaid;
	/** 多媒体文件标识*/
	private String thumbmediaid;
	/** 当msgtype是image、voice时，可以调用微信下载接口，下载缩略图*/
	private String expiretime;
	/** 判断当前是否已读*/
	private String read;
	/** 语音的文字信息*/
	private String recognition;
	/** 遥控命令*/
	private String command;
	/** 视频文件存储路径*/
	private String fileName;
	/** 视频文件大小*/
	private String fileSize;
	/** 视频文件时间长度*/
	private String fileTime;
	//爱奇艺影视参数
	private String playType;
	private String vrsAlbumId;
	private String vrsTvId;
	private String vrsChnId;
	private String albumId;
	private String history;
	private String customer;
	private String device;
	//芒果影视需要的参数
	private String player;
	private String action;
	private String cmdex;
	private String videoid;
	private String videotype;
	private String videouistyle;
	private String offlinemsg;
	//预约节目提醒
	private String channelname;
	//弹幕
	private String nickname;
	private String headurl;
	private String display;
	//欢网回看
	private String location;
	private String shifttime;
	private String shiftend;
	private String sp;
	//优酷
	private String showid;
	private String cats;
	private String vid;
	private String title;
	private String img;
	private String point;
	
	//截图
	private String imgsize;
	
	/**imgsize
	 * @return the imgsize
	 */
	public String getimgsize() {
		return imgsize;
	}
	/**
	 * @param imgsize the imgsize to set
	 */
	public void setimgsize(String imgsize) {
		this.imgsize = imgsize;
	}

	/**offlinemsg
	 * @return the msgtype
	 */
	public String getMsgtype() {
		return msgtype;
	}
	/**
	 * @param msgtype the msgtype to set
	 */
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	/**
	 * @return the offlinemsg
	 */
	public String getofflinemsg() {
		return offlinemsg;
	}
	/**
	 * @param msgtype the offlinemsg to set
	 */
	public void setofflinemsg(String offlinemsg) {
		this.offlinemsg = offlinemsg;
	}
	/**
	 * @return the msgid
	 */
	public String getmsgid() {
		return msgid;
	}
	/**
	 * @param msgtype the offlinemsg to set
	 */
	public void setmsgid(String msgid) {
		this.msgid = msgid;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}
	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	/**
	 * @return the createtime
	 */
	public String getCreatetime() {
		return createtime;
	}
	/**
	 * @param createtime the createtime to set
	 */
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	/**
	 * @return the accesstoken
	 */
	public String getAccesstoken() {
		return accesstoken;
	}
	/**
	 * @param accesstoken the accesstoken to set
	 */
	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}
	/**
	 * @return the mediaid
	 */
	public String getMediaid() {
		return mediaid;
	}
	/**
	 * @param mediaid the mediaid to set
	 */
	public void setMediaid(String mediaid) {
		this.mediaid = mediaid;
	}
	/**
	 * @return the thumbmediaid
	 */
	public String getThumbmediaid() {
		return thumbmediaid;
	}
	/**
	 * @param thumbmediaid the thumbmediaid to set
	 */
	public void setThumbmediaid(String thumbmediaid) {
		this.thumbmediaid = thumbmediaid;
	}
	/**
	 * @return the expiretime
	 */
	public String getExpiretime() {
		return expiretime;
	}
	/**
	 * @param expiretime the expiretime to set
	 */
	public void setExpiretime(String expiretime) {
		this.expiretime = expiretime;
	}
	private String openid;
	/**
	 * @return the openid
	 */
	public String getOpenid() {
		return openid;
	}
	/**
	 * @param openid the openid to set
	 */
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	/**
	 * @return the read
	 */
	public String getRead() {
		return read;
	}
	/**
	 * @param read the read to set
	 */
	public void setRead(String read) {
		this.read = read;
	}
	/**
	 * @return the recognition
	 */
	public String getRecognition() {
		return recognition;
	}
	/**
	 * @param recognition the recognition to set
	 */
	public void setRecognition(String recognition) {
		this.recognition = recognition;
	}
	/**
	 * @return the recognition
	 */
	public String getCommand() {
		return command;
	}
	/**
	 * @param recognition the recognition to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the fileSize
	 */
	public String getFileSize() {
		return fileSize;
	}
	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	/**
	 * @return the fileTime
	 */
	public String getFileTime() {
		return fileTime;
	}
	/**
	 * @param fileTime the fileTime to set
	 */
	public void setFileTime(String fileTime) {
		this.fileTime = fileTime;
	}
	/**
	 * @return the playType
	 */
	public String getplayType() {
		return playType;
	}
	/**
	 * @param msgtype the msgtype to set
	 */
	public void setplayType(String playType) {
		this.playType = playType;
	}
	/**
	 * @return the vrsAlbumId
	 */
	public String getvrsAlbumId() {
		return vrsAlbumId;
	}
	/**
	 * @param msgtype the msgtype to set
	 */
	public void setvrsAlbumId(String vrsAlbumId) {
		this.vrsAlbumId = vrsAlbumId;
	}
	/**
	 * @return the vrsTvId
	 */
	public String getvrsTvId() {
		return vrsTvId;
	}
	/**
	 * @param msgtype the msgtype to set
	 */
	public void setvrsTvId(String vrsTvId) {
		this.vrsTvId = vrsTvId;
	}
	/**
	 * @return the vrsChnId
	 */
	public String getvrsChnId() {
		return vrsChnId;
	}
	/**
	 * @param msgtype the vrsChnId to set
	 */
	public void setvrsChnId(String vrsChnId) {
		this.vrsChnId = vrsChnId;
	}
	/**
	 * @return the albumId
	 */
	public String getalbumId() {
		return albumId;
	}
	/**
	 * @param msgtype the albumId to set
	 */
	public void setalbumId(String albumId) {
		this.albumId = albumId;
	}
	/**
	 * @return the history
	 */
	public String gethistory() {
		return history;
	}
	/**
	 * @param msgtype the history to set
	 */
	public void sethistory(String history) {
		this.history = history;
	}
	/**
	 * @return the customer
	 */
	public String getcustomer() {
		return customer;
	}
	/**
	 * @param msgtype the customer to set
	 */
	public void setcustomer(String customer) {
		this.customer = customer;
	}
	/**
	 * @return the device
	 */
	public String getdevice() {
		return device;
	}
	/**
	 * @param msgtype the device to set
	 */
	public void setdevice(String device) {
		this.device = device;
	}
	
	
	/**
	 * @return the player
	 */
	public String getplayer() {
		return player;
	}
	/**
	 * @param msgtype the player to set
	 */
	public void setplayer(String player) {
		this.player =player ;
	}
	/**
	 * @return the action
	 */
	public String getaction() {
		return action;
	}
	/**
	 * @param msgtype the action to set
	 */
	public void setaction(String action) {
		this.action =action ;
	}
	/**
	 * @return the cmdex
	 */
	public String getcmdex() {
		return cmdex;
	}
	/**
	 * @param msgtype the cmdex to set
	 */
	public void setcmdex(String cmdex) {
		this.cmdex =cmdex ;
	}
	/**
	 * @return the videoid
	 */
	public String getvideoid() {
		return videoid;
	}
	/**
	 * @param msgtype the videoid to set
	 */
	public void setvideoid(String videoid) {
		this.videoid =videoid ;
	}
	/**
	 * @return the videotype
	 */
	public String getvideotype() {
		return videotype;
	}
	/**
	 * @param msgtype the videotype to set
	 */
	public void setvideotype(String videotype) {
		this.videotype =videotype ;
	}
	/**
	 * @return the videouistyle
	 */
	public String getvideouistyle() {
		return videouistyle;
	}
	/**
	 * @param msgtype the videouistyle to set
	 */
	public void setvideouistyle(String videouistyle) {
		this.videouistyle =videouistyle ;
	}
	/**offlinemsg
	 * @return the channelname
	 */
	public String getchannelname() {
		return channelname;
	}
	/**
	 * @param channelcode the channelname to set
	 */
	public void setchannelname(String channelname) {
		this.channelname = channelname;
	}
	/**
	 * @return the nickname
	 */
	public String getnickname() {
		return nickname;
	}
	/**
	 * @param nickname the nickname to set
	 */
	public void setnickname(String nickname) {
		this.nickname = nickname;
	}
	/**
	 * @return the headurl
	 */
	public String getheadurl() {
		return headurl;
	}
	/**
	 * @param headurl the headurl to set
	 */
	public void setheadurl(String headurl) {
		this.headurl = headurl;
	}
	/**
	 * @return the display
	 */
	public String getdisplay() {
		return headurl;
	}
	/**
	 * @param display the display to set
	 */
	public void setdisplay(String display) {
		this.display = display;
	}
	/**
	 * @return the location
	 */
	public String getlocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setlocation(String location) {
		this.location = location;
	}
	/**
	 * @return the shifttime
	 */
	public String getshifttime() {
		return shifttime;
	}
	/**
	 * @param shifttime the shifttime to set
	 */
	public void setshifttime(String shifttime) {
		this.shifttime = shifttime;
	}
	/**
	 * @return the shiftend
	 */
	public String getshiftend() {
		return shiftend;
	}
	/**
	 * @param shiftend the shiftend to set
	 */
	public void setshiftend(String shiftend) {
		this.shiftend = shiftend;
	}
	/**
	 * @return the sp
	 */
	public String getsp() {
		return sp;
	}
	/**
	 * @param sp the sp to set
	 */
	public void setsp(String sp) {
		this.sp = sp;
	}
	/**
	 * @return the showid
	 */
	public String getshowid() {
		return showid;
	}
	/**
	 * @param showid the showid to set
	 */
	public void setshowid(String showid) {
		this.showid = showid;
	}
	/**
	 * @return the cats
	 */
	public String getcats() {
		return cats;
	}
	/**
	 * @param cats the cats to set
	 */
	public void setcats(String cats) {
		this.cats = cats;
	}
	/**
	 * @return the vid
	 */
	public String getvid() {
		return vid;
	}
	/**
	 * @param vid the vid to set
	 */
	public void setvid(String vid) {
		this.vid = vid;
	}
	/**
	 * @return the title
	 */
	public String gettitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void settitle(String title) {
		this.title = title;
	}
	/**
	 * @return the img
	 */
	public String getimg() {
		return img;
	}
	/**
	 * @param img the img to set
	 */
	public void setimg(String img) {
		this.img = img;
	}
	/**
	 * @return the point
	 */
	public String getpoint() {
		return point;
	}
	/**
	 * @param point the point to set
	 */
	public void setpoint(String point) {
		this.point = point;
	}
}
