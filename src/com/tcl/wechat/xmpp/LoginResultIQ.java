
package com.tcl.wechat.xmpp;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;

/**
 * 终端登陆成功后的回包
 * @author junjian
 *
 */
public class LoginResultIQ extends IQ {
	
    private final String xml;
    
	private String homenick;
	private String membernick;
	private String errorcode;
  

	public LoginResultIQ(final String xml) {
		this.xml = xml;
		//"<list xmlns='tcl.im.aslist'>\n" + xml + "\n</list>";
	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	/**
	* @return homenick
	*/
	
	public String getHomenick() {
		return homenick;
	}

	/**
	* @return homenick
	*/
	
	public void setHomenick(String homenick) {
		this.homenick = homenick;
	}

	/**
	* @return membernick
	*/
	
	public String getMembernick() {
		return membernick;
	}

	/**
	* @return membernick
	*/
	
	public void setMembernick(String membernick) {
		this.membernick = membernick;
	}

	
	@Override
	public String getChildElementXML() {

		StringBuilder buf = new StringBuilder();
        buf.append("<auth xmlns=\"tcl:hc:login\">\n");
        if (errorcode != null){
        	buf.append("<errorcode>").append(errorcode).append("</errorcode>\n");
        }
        if (homenick != null){
        	buf.append("<homenick>").append(homenick).append("</homenick>\n");
        }
        if (membernick != null){
        	buf.append("<membernick>").append(membernick).append("</membernick>\n");
        }        
        buf.append("</auth>");
        return buf.toString();
	}


}