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
 * 获取Token
 * @author rex.lei
 *
 */
public class LSAccessTokenIQ extends IQ {
	
	private final String xml;
	private String errorcode;
	private String accesstoken;

	public LSAccessTokenIQ(final String xml) {
		this.xml = xml;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}
	
	public String getAccesstoken() {
		return accesstoken;
	}

	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}

	@Override
	public String getChildElementXML() {
		
		StringBuilder buf = new StringBuilder();
        buf.append("<getaccesstoken xmlns=\"tcl:hc:portal\">\n");
        if (errorcode != null){
        	buf.append("<errorcode>").append(errorcode).append("</errorcode>\n");
        	buf.append("<accesstoken>").append(accesstoken).append("</accesstoken>\n");
        }
        buf.append("</getaccesstoken>");
        return buf.toString();
	}

}
