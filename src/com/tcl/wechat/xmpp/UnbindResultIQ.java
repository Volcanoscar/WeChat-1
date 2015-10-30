/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.xmpp;

import org.jivesoftware.smack.packet.IQ;

/**
 * @ClassName: UnbindResultIQ
 */

public class UnbindResultIQ extends IQ {
	
	private final String xml;
	private String errorcode;
    private String openid;
    private String deviceid;

	public UnbindResultIQ(final String xml) {
		this.xml = xml;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}
	public String getopenid() {
		return openid;
	}

	public void setopenid(String openid) {
		this.openid = openid;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	@Override
	public String getChildElementXML() {
		
		StringBuilder buf = new StringBuilder();
        buf.append("<unbind xmlns=\"tcl:hc:wechat\">\n");
        if (errorcode != null){
        	buf.append("<errorcode>").append(errorcode).append("</errorcode>\n");
        	buf.append("<openid>").append(openid).append("</openid>\n");
        	buf.append("<deviceid>").append(deviceid).append("</deviceid>\n");
        }
        buf.append("</unbind>");
        return buf.toString();
	}

}
