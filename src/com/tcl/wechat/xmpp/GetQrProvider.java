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

import android.util.Log;

/**
 * @ClassName: GetQrProvider
 */

public class GetQrProvider implements IQProvider {
	
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		GetQrResultIQ iq = new GetQrResultIQ("");
		iq.setType(Type.RESULT);
		boolean done = false;
		while (!done) {
			
			int eventType = parser.next();
			
			if (eventType == XmlPullParser.START_TAG) {
				 Log.d("andy", "parser.getName()="+parser.getName());
				if (parser.getName().equals("errorcode")){
					parser.next();		
					String err = parser.getText();
					iq.setErrorcode(err);
					 Log.d("andy", "err="+err);
				}else if (parser.getName().equals("url")){
					parser.next();		
					String url = parser.getText();
					iq.setUrl(url);
					 Log.d("andy", "ur222="+url);
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("getqr")) {
					done = true;
				}
			}
		}		
		return iq;
	}

}