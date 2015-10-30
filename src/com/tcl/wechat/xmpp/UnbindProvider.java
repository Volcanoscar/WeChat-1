/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.xmpp;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 * @ClassName: UnbindProvider
 */

public class UnbindProvider implements IQProvider {
	
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		UnbindResultIQ iq = new UnbindResultIQ("");
		iq.setType(Type.RESULT);
		boolean done = false;
		while (!done) {
			
			int eventType = parser.next();
			
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("errorcode")){
					parser.next();		
					String err = parser.getText();
					iq.setErrorcode(err);
				}else if (parser.getName().equals("openid")){
					parser.next();		
					String openid = parser.getText();
					iq.setopenid(openid);
				} else if (parser.getName().equals("deviceid")){
					parser.next();		
					String deviceid = parser.getText();
					iq.setDeviceid(deviceid);
				}
			} 
			else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("unbind")) {
					done = true;
				}
			}
		}		
		return iq;
	}

}