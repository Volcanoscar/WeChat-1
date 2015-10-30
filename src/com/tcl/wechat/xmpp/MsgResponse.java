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

import android.os.AsyncTask;
import android.util.Log;

import com.tcl.wechat.WeApplication;

/**
 * 响应服务器
 * @author rex.lei
 *
 */
public class MsgResponse {
	
	private static final String TAG = MsgResponse.class.getSimpleName();
	
	private  XMPPConnection connection = null;
	private String openid,msgid,msgtype;

	public MsgResponse(XMPPConnection conn ,Map<String, String> values){
		this.connection = conn;
		openid = values.get("openid");
		msgid = values.get("msgid");
		msgtype = values.get("msgtype");
		
	}
	

	public void sentPacket(){
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				String content = "";				
				content = "<replymsg xmlns=\"tcl:hc:wechat\">" 
						+ "<openid>" + openid + "</openid>" 
						+ "<msgid>" + msgid + "</msgid>"
						+ "<msgtype>" + msgtype + "</msgtype>"
						+ "</replymsg>";
			
				IQ userContentIQ = new UserContentIQ(content);
				userContentIQ.setType(IQ.Type.SET);
				connection.sendPacket(userContentIQ);										
				Log.d(TAG, "Send Response:" + userContentIQ.toXML());
				return null;
			}
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}
}


