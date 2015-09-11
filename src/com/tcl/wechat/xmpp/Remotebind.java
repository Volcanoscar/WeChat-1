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

public class Remotebind {
	private String tag = "Remotebind";
	private  XMPPConnection connection = null;
	private String openid,reply;

	public Remotebind(XMPPConnection conn ,Map<String, String> values){
		this.connection = conn;
		openid = values.get("openid");
		reply = values.get("reply");
		
	}
	

	public void sentPacket(){

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String content = "";				
					content = "<remotebind xmlns=\"tcl:hc:wechat\">" 
							+ "<openid>" + openid+ "</openid>" 
							+"<reply>"+reply+"</reply>"
							+ "</remotebind>";
				
					IQ userContentIQ = new UserContentIQ(content);
					userContentIQ.setType(IQ.Type.SET);
					connection.sendPacket(userContentIQ);										
					Log.d(tag, "发送Remotebind请求"+userContentIQ.toXML());
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();		
	}
	
}


