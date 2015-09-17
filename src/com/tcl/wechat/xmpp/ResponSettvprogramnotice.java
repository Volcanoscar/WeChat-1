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

public class ResponSettvprogramnotice {
	private String tag = "ResponSettvprogramnotice";
	private  XMPPConnection connection = null;
	private String channelcode;

	public ResponSettvprogramnotice(XMPPConnection conn ,Map<String, String> values){
		this.connection = conn;
		channelcode = values.get("channelcode");
	}
	

	public void sentPacket(){
		//result是应答用的,set是主动发用的,暂时共用，都为set
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String content = "";				
					content = "<replymsg  xmlns=\"tcl:hc:wechat\">" 
							+ "<msgtype>tvprogramnotice</msgtype>"
							+ "<channelcode>" + channelcode+ "</channelcode>" 						
							+ "</replymsg>";
				
					IQ userContentIQ = new UserContentIQ(content);
					userContentIQ.setType(IQ.Type.RESULT);
					connection.sendPacket(userContentIQ);										
					Log.d(tag, "回复tvprogramnotice"+userContentIQ.toXML());
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();		
	}
	
}
