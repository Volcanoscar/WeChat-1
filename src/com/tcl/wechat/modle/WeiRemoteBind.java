/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.modle;

import java.io.Serializable;

/**
 * @ClassName: WeiNotice
 */

public class WeiRemoteBind implements Serializable{
	
	private static final long serialVersionUID = 7845860354229518993L;
	
	/**设备注册时生成的设备号*/
	private String memberid;
	/**微信用户标识*/
	private String openid;
	/**用户昵称*/
	private String nickname;
	/**用户的性别，值为1时是男性，值为2时是女性，值为0时是未知*/
	private String sex;
	/**用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空*/
	private String headimgurl;
	
	private String reply;
	/**
	 * @return the memberid
	 */
	public String getmemberid() {
		return memberid;
	}
	/**
	 * @param event the memberid to set
	 */
	public void setmemberid(String memberid) {
		this.memberid = memberid;
	}
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
	 * @return the reply
	 */
	public String getreply() {
		return reply;
	}
	/**
	 * @param reply the reply to set
	 */
	public void setreply(String reply) {
		this.reply = reply;
	}
}
