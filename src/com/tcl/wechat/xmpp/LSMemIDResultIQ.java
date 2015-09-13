
package com.tcl.wechat.xmpp;

import org.jivesoftware.smack.packet.IQ;

/**
 * 终端获取memID后的回包
 * @author junjian
 *
 */
public class LSMemIDResultIQ extends IQ {
	
    private final String xml;
    
	private String memberid;
	private String errorcode;
	private String status; 

	public LSMemIDResultIQ(final String xml) {
		this.xml = xml;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}


	/**
	* @return memberid
	*/
	
	public String getMemberid() {
		return "senge";
//		return memberid;
	}

	/**
	* @return memberid
	*/
	
	public void setMemberid(String memberid) {
		this.memberid = "senge";
//		this.memberid = memberid;
	}

	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
        buf.append("<addmaindevice xmlns=\"tcl:hc:login\">\n");
        if (errorcode != null){
        	buf.append("<errorcode>").append(errorcode).append("</errorcode>\n");
        }
        if (memberid != null){
        	buf.append("<memberId>").append(memberid).append("</memberId>\n");
        }
           
        buf.append("</addmaindevice>");
        return buf.toString();
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}