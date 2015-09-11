/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.xmpp;

import org.jivesoftware.smack.packet.IQ;

import com.tcl.wechat.modle.WeiNotice;

/**
 * @ClassName: WeiNoticegResultIQ
 */

public class WeiNoticegResultIQ extends IQ {
	
	private final String xml;
	private String errorcode;
	private WeiNotice weiNotice;
  

	public WeiNoticegResultIQ(final String xml) {
		this.xml = xml;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	@Override
	public String getChildElementXML() {
		
		StringBuilder buf = new StringBuilder();
        buf.append("<notice xmlns=\"tcl:hc:wechat\">\n");
        if (errorcode != null){
        	buf.append("<errorcode>").append(errorcode).append("</errorcode>\n");
        }
        buf.append("</notice>");
        return buf.toString();
	}

	/**
	 * @return the weiNotice
	 */
	public WeiNotice getWeiNotice() {
		return weiNotice;
	}

	/**
	 * @param weiNotice the weiNotice to set
	 */
	public void setWeiNotice(WeiNotice weiNotice) {
		this.weiNotice = weiNotice;
	}

	
}