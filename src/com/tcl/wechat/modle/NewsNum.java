/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.modle;

/**
 * 消息信息
 * @author rex.lei
 *
 */
public class NewsNum {
	
	private String openid;
	private String newsnum;
	
	public NewsNum() {
		super();
	}

	public NewsNum(String openid, String newsnum) {
		super();
		this.openid = openid;
		this.newsnum = newsnum;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNewsnum() {
		return newsnum;
	}

	public void setNewsnum(String newsnum) {
		this.newsnum = newsnum;
	}

	@Override
	public String toString() {
		return "NewsNum [openid=" + openid + ", newsnum=" + newsnum + "]";
	}
}
