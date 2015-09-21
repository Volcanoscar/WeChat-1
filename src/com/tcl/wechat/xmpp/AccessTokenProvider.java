
package com.tcl.wechat.xmpp;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

/**
 * 
 * @author rex.lei
 *
 */
public class AccessTokenProvider implements IQProvider {
	
	private static final String TAG = "AccessTokenProvider";
	
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		LSAccessTokenIQ iq = new LSAccessTokenIQ("");
		iq.setType(Type.RESULT);

		Log.i(TAG, "parseIQ:" + parser.toString());
		
		/*
		  <iq type="result" id="oD0JF-7" to="#testzq1@wechat.big.tclclouds.com/tv-android-wechat">
		  	<getaccesstoken xmlns="tcl:hc:portal">
		  		<errorcode>0</errorcode>
		  		<accesstoken>s8U7cp21TtuAGlxklYEMs1P1eDu1RsB0ANL8PqbV1l9JQrtPUIllIytsYjbygw0JPLMiwBSxXspUZx8-NGQbrV8p1cIKgAHuezaBJPW1WL4</accesstoken>
		  	</getaccesstoken>
		  </iq>
		  
		 * */		
		boolean done = false;
		while (!done) {
			int eventType = parser.next();
			Log.i(TAG, "eventType:" + eventType);
			
			if (eventType == XmlPullParser.START_TAG) {
				
				if (parser.getName().equals("errorcode")){
					parser.next();
					String errorcode = parser.getText();
					
					iq.setErrorcode(errorcode);
					
				}else if (parser.getName().equals("accesstoken")) {
					parser.next();
					String accesstoken = parser.getText();
					iq.setAccesstoken(accesstoken);
				}
				
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("getaccesstoken")) {
					done = true;
				}
			}
		}		
		return iq;
	}

}