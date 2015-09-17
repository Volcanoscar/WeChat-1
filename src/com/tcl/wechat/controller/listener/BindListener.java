package com.tcl.wechat.controller.listener;

public interface BindListener {
	
	public void onBind(String openId, int errorCode);
}
