package com.tcl.wechat.xmpp;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 * 终端注册认证，获取memID的回包provider
 * @author junjian
 *
 */
public class LSMemIDProvider implements IQProvider {
	
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		LSMemIDResultIQ iq = new LSMemIDResultIQ("");
		iq.setType(Type.RESULT);
		
		boolean done = false;
		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("errorcode")){
					parser.next();
					String err = parser.getText();
					iq.setErrorcode(err);
				}else if (parser.getName().equals("memberId")) {
					parser.next();
					String memberid = parser.getText();
					iq.setMemberid(memberid);				
				}else if (parser.getName().equals("status")) {
					parser.next();
					String status = parser.getText();
					iq.setStatus(status);				
				}

			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("addmaindevice")) {
					done = true;
				}
			}
		}		
		return iq;
	}

}