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

public class NewsNum {
	
	private String openid;
	private String newsnum;
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
	
	public void init (){
		openid="";
		newsnum="";
	}
}
