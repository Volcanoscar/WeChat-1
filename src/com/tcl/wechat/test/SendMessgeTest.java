package com.tcl.wechat.test;

import java.util.HashMap;
import java.util.Map;

import android.test.AndroidTestCase;

import com.tcl.wechat.xmpp.WeiXmppManager;

public class SendMessgeTest extends AndroidTestCase {
	
	/**
	 * 发送消息
	 * @param view
	 */
	public void sendMessage(){
		
		/**
		 * 
		 */
		Map<String, String> values = new HashMap<String, String>();
		values.put("openid", "");
		values.put("msgtype", "");
		values.put("msgid", "");
		
		ResponseTextMsg mMsgResponse = new ResponseTextMsg(WeiXmppManager.getInstance().getConnection(), values);
		mMsgResponse.sentPacket();
	}

}
