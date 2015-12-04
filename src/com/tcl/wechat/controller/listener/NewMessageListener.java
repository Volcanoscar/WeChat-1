package com.tcl.wechat.controller.listener;

import com.tcl.wechat.model.WeiXinMessage;

/**
 * 新消息接收监听器
 * @author rex.lei
 *
 */
public interface NewMessageListener {
	
	/**
	 * 收到一条新消息
	 * @param recorder
	 */
	public void onNewMessage(WeiXinMessage recorder);
}
