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

import com.tcl.wechat.WeApplication;

import android.os.AsyncTask;
import android.util.Log;

/**
 * 上报设备信息
 * @author rex.lei
 *
 */
public class ReportDeviceInfo {
	
	private static final String TAG = ReportDeviceInfo.class.getSimpleName();
	
	private  XMPPConnection connection = null;
	private String deviceid,dnum,huanid,lanip,messageboxid,mac,version;

	public ReportDeviceInfo(XMPPConnection conn ,Map<String, String> values){
		this.connection = conn;
		deviceid = values.get("deviceid");
		lanip = values.get("lanip");
		dnum = values.get("dnum");
		huanid = values.get("huanid");
		messageboxid = values.get("messageboxid");
		mac = values.get("mac");
		version = values.get("version");
	}
	

	public void sentPacket(){
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				String content = "<report xmlns=\"tcl:hc:wechat\">" 
						+ "<deviceid>" + deviceid+ "</deviceid>" 
						+ "<dnum>" + dnum+ "</dnum>" 
						+ "<huanid>" + huanid+ "</huanid>" 
						+ "<lanip>"+lanip+"</lanip>"
						+ "<messageboxid>"+messageboxid+"</messageboxid>"
						+ "<mac>"+mac+"</mac>"
						+ "<version>"+version+"</version>"
						+ "</report>";
				
				IQ userContentIQ = new UserContentIQ(content);
				userContentIQ.setType(IQ.Type.SET);
				connection.sendPacket(userContentIQ);										
				Log.d(TAG, "Send ReportDeviceInfo:"+userContentIQ.toXML());
				return null;
			}
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}
	
}
