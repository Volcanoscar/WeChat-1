/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.modle;

/**
 * @ClassName: BinderUser
 */

public class BinderUser {
	
	private String openid;
	private String nickname;
	private String sex;
	private String headimgurl;
	private String newsnum;
	private String status;//success wait 分别为绑定成功，收到请求绑定，等待服务器返回。默认是success
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
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}
	/**
	 * @param sex the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}
	/**
	 * @return the headimgurl
	 */
	public String getHeadimgurl() {
		return headimgurl;
	}
	/**
	 * @param headimgurl the headimgurl to set
	 */
	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}
	
	/**
	 * @return the newsnum
	 */
	public String getNewsnum() {
		return newsnum;
	}
	/**
	 * @param headimgurl the headimgurl to set
	 */
	public void setNewsnum(String newsnum) {
		this.newsnum = newsnum;
	}
	/**
	 * @return the status
	 */
	public String getstatus() {
		return status;
	}
	/**
	 * @param  the status to set
	 */
	public void setstatus(String status) {
		this.status = status;
	}
	public void init (){
		openid="";
		nickname="";
		sex="";
		headimgurl="";
		newsnum="0";
		status = "success";
	}
	@Override
	public String toString() {
		return "BinderUser [openid=" + openid + ", nickname=" + nickname
				+ ", sex=" + sex + ", headimgurl=" + headimgurl + ", newsnum="
				+ newsnum + ", status=" + status + "]";
	}
}
