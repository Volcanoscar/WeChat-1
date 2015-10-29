package com.tcl.wechat.xmpp;

import org.jivesoftware.smack.packet.IQ;

/**
 * 回复文件消息响应结果
 * @author rex.lei
 *
 */
public class ReplyResultIQ extends IQ {
	
	private final String xml;
	private String msgid;
	private String result;
	private String errorcode;
	
	public ReplyResultIQ(final String xml) {
		this.xml = xml;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
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
        buf.append("<tvreplymsg xmlns=\"tcl:hc:portal\">\n");
        if (errorcode != null){
        	buf.append("<errorcode>").append(errorcode).append("</errorcode>\n")
        		.append("<result>").append(result).append("</result>\n")
        		.append("<msgid>").append(msgid).append("</msgid>\n");
        }
        buf.append("</tvreplymsg>");
        return buf.toString();
	}

}
