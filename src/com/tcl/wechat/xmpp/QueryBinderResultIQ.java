/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.xmpp;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;

import com.tcl.wechat.modle.BinderUser;


/**
 * @ClassName: QueryBinderResultIQ
 */

public class QueryBinderResultIQ extends IQ {
	
	private final String xml;
	private String errorcode;
	private ArrayList<BinderUser> files = new ArrayList<BinderUser>();	

	public QueryBinderResultIQ(final String xml) {
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
        buf.append("<querybinder xmlns=\"tcl:hc:wechat\">\n");
        if (errorcode != null){
        	buf.append("<errorcode>").append(errorcode).append("</errorcode>\n");
        }
        buf.append("</querybinder>");
        return buf.toString();
	}

	/**
	 * @return the files
	 */
	public ArrayList<BinderUser> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(ArrayList<BinderUser> files) {
		this.files = files;
	}

}
