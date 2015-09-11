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

public class ReportTvstatus {
	private String tag = "liyulin";
	private  XMPPConnection connection = null;
	private String channelcode,video,app,image;

	public ReportTvstatus(XMPPConnection conn ,Map<String, String> values){
		this.connection = conn;
		channelcode = values.get("channelcode");
		video = values.get("video");
		app = values.get("app");
		image = values.get("image");
		
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
					.append("</image></control>");
					
			IQ userContentIQ = new UserContentIQ(content.toString());
					userContentIQ.setType(IQ.Type.SET);
					connection.sendPacket(userContentIQ);										
					//Log.d(tag, "发送tvstatus"+userContentIQ.toXML());
					Log.d(tag, "发送tvstatus成功");
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();		
	}
	
}
