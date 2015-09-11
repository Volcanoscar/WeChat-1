
package com.tcl.wechat.xmpp;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 * 终端登陆回包provider
 * @author junjian
 *
 */
public class LoginProvider implements IQProvider {
	
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		LoginResultIQ iq = new LoginResultIQ("");
		iq.setType(Type.ERROR);

		boolean done = false;
		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("errorcode")){
					parser.next();
					String err = parser.getText();
					iq.setErrorcode(err);
				}else if (parser.getName().equals("homenick")) {
					String homenick = parser.getText();
					iq.setHomenick(homenick);		
				}else if (parser.getName().equals("membernick")) {
					String membernick = parser.getText();
					iq.setMembernick(membernick);		
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("auth")) {
					done = true;
				}
			}
		}		
		return iq;
	}

}