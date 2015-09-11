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

public class ReportVideo {
	
	private  XMPPConnection connection = null;
	private String videoId,showid,duration,currentPostion;

	public ReportVideo(XMPPConnection conn ,Map<String, String> values){
		this.connection = conn;
		videoId = values.get("videoId");
		showid = values.get("showid");
		duration = values.get("duration");
		currentPostion = values.get("currentPostion");
	}
	
	public void sentPacket(){
//result是应答用的,set是主动发用的，都为set
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String content = "";				
					content = "<reportvideo  xmlns=\"tcl:hc:wechat\">" 
							+ "<videoid>" + videoId+ "</videoid>"
							+ "<showid>" + showid+ "</showid>" 	
							+ "<duration>" + duration+ "</duration>" 	
							+ "<currentpostion>" + currentPostion+ "</currentpostion>" 	
							+ "</reportvideo>";
				
					IQ userContentIQ = new UserContentIQ(content);
					userContentIQ.setType(IQ.Type.SET);
					connection.sendPacket(userContentIQ);										
					Log.i("YoukuReceiver","ReportVideo:"+ userContentIQ.toString());
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();		
	}
	
}
