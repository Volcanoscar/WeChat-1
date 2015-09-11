package com.tcl.wechat.xmpp;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 * 终端以账号和加密密码登录认证，登录服务器认证后的回包provider
 * @author junjian
 *
 */
public class LSLoginProvider implements IQProvider {
	
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		LSLoginResultIQ iq = new LSLoginResultIQ("");
		iq.setType(Type.RESULT);
		
		List<String> ipAndport = new ArrayList<String>();		   
//		<iq type="result" id="v1s29-1" to="#admin\40163.com@tcl.com/pc-win-im">
//		  <list xmlns="tcl.im.aslist">
//		    <as ip="192.168.0.1" port="5222"></as>
//		    <as ip="192.168.0.2" port="8563"></as>
//			<as ip="192.168.0.3" port="7452"></as>
//			<tid>T聊账�?/tid>
//			<token xmlns="tcl.im.token">ADFGHJBNMKIRSDsdgfgahGGFDD…�?.dDFGH</token>
//		  </list>
//		</iq>		
		
/*		<iq type="result" id="v1s29-1" to="#memberid@tcl.com/tv-android-homecloud">
		  <auth xmlns="tcl.hc.login">
		    <errorcode>0</errorcode>
		    <aslist>
		      <as ip="192.168.0.1" port="5222"></as>
		      <as ip="192.168.0.2" port="8563"></as>
			  <as ip="192.168.0.3" port="7452"></as>
			</aslist>
			<token xmlns="tcl.hc.token">ADFGHJBNMKIRSDsdgfgahGGFD</token>
		  </auth>
		</iq>
*/
		boolean done = false;
		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("errorcode")){
					String err = parser.getText();
					iq.setErrorcode(err);
				}else if (parser.getName().equals("as")) {
					String ip = parser.getAttributeValue("", "ip");
					String port = parser.getAttributeValue("", "port");						
					ipAndport.add(ip +"," + port);					
				}else if (parser.getName().equals("token")){
					String token = parser.nextText();
					iq.setToken(token);
				}

			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("auth")) {
					done = true;
				}
			}
		}		
		iq.setIpAndport(ipAndport);		
		return iq;
	}

}
