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

public class ResponseTvstatus {
	private String tag = "liyulin";
	private  XMPPConnection connection = null;
	private String openid, channelcode,video,app,image,thumbnail;

	public ResponseTvstatus(XMPPConnection conn ,Map<String, String> values){
		this.connection = conn;
		openid = values.get("openid");
		channelcode = values.get("channelcode");
		video = values.get("video");
		app = values.get("app");
		image = values.get("image");
		thumbnail = values.get("thumbnail");
		
	}
	

	public void sentPacket(){
//result是应答用的,set是主动发用的,暂时共用，都为set

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					StringBuffer content = new StringBuffer();
					 content.append("<control xmlns=\"tcl:hc:wechat\"><type>gettvstatus</type>")
					 .append("<openid>")
					 .append(openid)
					 .append("</openid>")
					 .append("<channelcode>")
					 .append(channelcode)
					 .append("</channelcode>")
					 .append("<video>")
					 .append(video)
					 .append("</video>")
					 .append("<app>")
					 .append(app)
					 .append("</app>")
					 .append("<image>")
					 .append(image)
					 .append("</image>")
					 .append("<thumbnail>")
					 .append(thumbnail)
					 .append("</thumbnail></control>");
				
					IQ userContentIQ = new UserContentIQ(content.toString());
					userContentIQ.setType(IQ.Type.RESULT);
					connection.sendPacket(userContentIQ);										
					//Log.d(tag, "回复tvstatus"+userContentIQ.toXML());
					Log.d(tag, "回复tvstatus成功");
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();		
	}
	
}
