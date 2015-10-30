/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.xmpp;

import org.jivesoftware.smack.packet.IQ;

import com.tcl.wechat.model.WeiXinMsgRecorder;

/**
 * @ClassName: WeiMsgResultIQ
 */

public class WeiMsgResultIQ extends IQ {
	
	private final String xml;
	private String errorcode;
	private WeiXinMsgRecorder weiXinMsg;
  

	public WeiMsgResultIQ(final String xml) {
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
        buf.append("<msg xmlns=\"tcl:hc:wechat\">\n");
        if (errorcode != null){
        	buf.append("<errorcode>").append(errorcode).append("</errorcode>\n");
        }
        buf.append("</msg>");
        return buf.toString();
	}

	/**
	 * @return the weiXinMsg
	 */
	public WeiXinMsgRecorder getWeiXinMsg() {
		return weiXinMsg;
	}

	/**
	 * @param weiXinMsg the weiXinMsg to set
	 */
	public void setWeiXinMsg(WeiXinMsgRecorder weiXinMsg) {
		this.weiXinMsg = weiXinMsg;
	}

}