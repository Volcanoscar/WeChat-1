package com.tcl.wechat.xmpp;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 * ReplyMediaProvider
 * @author rex.lei
 *
 */
public class ReplyProvider implements IQProvider {
	
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		ReplyResultIQ iq = new ReplyResultIQ("");
		iq.setType(Type.RESULT);
		boolean done = false;
		while (!done) {
			int eventType = parser.next();
			
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("errorcode")){
					parser.next();		
					String err = parser.getText();
					iq.setErrorcode(err);
				}else if (parser.getName().equals("result")){
					parser.next();		
					String result = parser.getText();
					iq.setResult(result);
				}else if (parser.getName().equals("msgid")){
					parser.next();		
					String msgid = parser.getText();
					iq.setMsgid(msgid);
				}
			} 
			else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("tvreplymsg")) {
					done = true;
				}
			}
		}		
		return iq;
	}
}