package com.tcl.wechat.controller;

import com.tcl.wechat.modle.WeiXinMsg;

/**
 * 新消息接收监听器
 * @author rex.lei
 *
 */
public interface NewMessageListener {
	
	/**
	 * 收到一条新消息
	 * @param weiXinMsg
	 */
	public void onNewMessage(WeiXinMsg weiXinMsg);
}
