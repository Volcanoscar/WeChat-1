/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.xmpp;

import java.util.Map;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import android.util.Log;

/**
 * @ClassName: QueryBinder
 */

public class MsgResponse {
	private String tag = "MsgResponse";
	private  XMPPConnection connection = null;
	private String openid,msgid,msgtype;

	public MsgResponse(XMPPConnection conn ,Map<String, String> values){
		this.connection = conn;
		openid = values.get("openid");
		msgid = values.get("msgid");
		msgtype = values.get("msgtype");
		
	}
	

	public void sentPacket(){

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String content = "";				
					content = "<replymsg xmlns=\"tcl:hc:wechat\">" 
							+ "<openid>" + openid+ "</openid>" 
							+"<msgid>"+msgid+"</msgid>"
							+"<msgtype>"+msgtype+"</msgtype>"
							+ "</replymsg>";
				
					IQ userContentIQ = new UserContentIQ(content);
					userContentIQ.setType(IQ.Type.SET);
					connection.sendPacket(userContentIQ);										
					Log.d(tag, "发送MsgResponse"+userContentIQ.toXML());
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();		
	}
	
}


