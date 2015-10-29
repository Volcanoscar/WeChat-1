package com.tcl.wechat.xmpp;

/**
 * 事件回调接口
 * @author rex.lei
 *
 */
public interface XmppEventListener {
	
	/**消息处理函数*/
	public void onEvent(XmppEvent event);

}
