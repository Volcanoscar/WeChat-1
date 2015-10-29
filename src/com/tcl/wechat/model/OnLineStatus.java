package com.tcl.wechat.model;

import java.io.Serializable;

/**
 * 在线状态
 * @author rex.lei
 *
 */
public class OnLineStatus implements Serializable{
	
	private static final long serialVersionUID = 5777352427167598835L;
	
	private String openid;
	private String triggerTime;
	
	public OnLineStatus() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public OnLineStatus(String openid, String triggerTime) {
		super();
		this.openid = openid;
		this.triggerTime = triggerTime;
	}
	
	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getTriggerTime() {
		return triggerTime;
	}

	public void setTriggerTime(String triggerTime) {
		this.triggerTime = triggerTime;
	}

	@Override
	public String toString() {
		return "OnLineStatus [openid=" + openid + ", triggerTime="
				+ triggerTime + "]";
	}
}
