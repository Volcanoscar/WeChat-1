package com.tcl.wechat.xmpp;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;

/**
 * 终端以账号和加密密码登录认证，登录服务器认证后的回包
 * @author junjian
 *
 */
public class LSLoginResultIQ extends IQ {
	
    private final String xml;
    
    private List<String> ipAndport;
	private String token;
	private String errorcode;
    
    public List<String> getIpAndport() {
		return ipAndport;
	}

	public void setIpAndport(List<String> ipAndport) {
		this.ipAndport = ipAndport;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LSLoginResultIQ(final String xml) {
		this.xml = xml;
		//"<list xmlns='tcl.im.aslist'>\n" + xml + "\n</list>";
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
        buf.append("<auth xmlns=\"tcl:hc:portal\">\n");
        if (errorcode != null){
        	buf.append("<errorcode>").append(errorcode).append("</errorcode>\n");
        }
        buf.append("<aslist>");
        if (ipAndport != null) {
        	for(String str : ipAndport){
				if(str == null){
					continue;
				}
				if(str.split(",").length < 2){
					continue;
				}
				buf.append("<as");
				buf.append(" ip=\"" + str.split(",")[0] + "\"");					
				buf.append(" port=\"" + str.split(",")[1] + "\">");
				buf.append("</as>\n");					
        	}
        }
        buf.append("</aslist>");
        if (token != null) {
            if (token.equals("")) {
                buf.append("<token xmlns=\"tcl:hc:token\"></token>\n");
            }
            else {
            	buf.append("<token xmlns=\"tcl:hc:token\">").append(token).append("</token>\n");
            }
        }	        
        buf.append("</auth>");
        return buf.toString();
	}


}
