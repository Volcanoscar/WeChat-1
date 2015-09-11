/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.xmpp;

import org.jivesoftware.smack.packet.IQ;

import com.tcl.wechat.modle.WeiRemoteBind;

/**
 * @ClassName: WeiNoticegResultIQ
 */

public class WeiRemoteBindResultIQ extends IQ {
	
	private final String xml;
	private String errorcode;
	private WeiRemoteBind weiRemoteBind;
  

	public WeiRemoteBindResultIQ(final String xml) {
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
        buf.append("<remotebind xmlns=\"tcl:hc:wechat\">\n");
        if (errorcode != null){
        	buf.append("<errorcode>").append(errorcode).append("</errorcode>\n");
        }
        buf.append("</remotebind>");
        return buf.toString();
	}

	/**
	 * @return the WeiRemoteBind
	 */
	public WeiRemoteBind getWeiRemoteBind() {
		return weiRemoteBind;
	}

	/**
	 * @param weiNotice the WeiRemoteBind to set
	 */
	public void setWeiRemoteBind(WeiRemoteBind weiRemoteBind) {
		this.weiRemoteBind = weiRemoteBind;
	}

	
}