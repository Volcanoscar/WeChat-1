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

import com.tcl.wechat.modle.WeiNotice;

/**
 * @ClassName: WeiNoticeMsgProvider
 */

public class WeiNoticeMsgProvider implements IQProvider {
	
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		WeiNoticegResultIQ iq = new WeiNoticegResultIQ("");
		WeiNotice weiNotice = new WeiNotice();
		iq.setType(Type.RESULT);
		boolean done = false;
		while (!done) {
			
			int eventType = parser.next();
			
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("errorcode")){
					parser.next();		
					String err = parser.getText();
					iq.setErrorcode(err);
				}else if (parser.getName().equals("event")){
					parser.next();		
					String event = parser.getText();
					weiNotice.setEvent(event);
				}else if (parser.getName().equals("openid")){
					parser.next();		
					String openid = parser.getText();
					weiNotice.setOpenid(openid);
				}else if (parser.getName().equals("nickname")){
					parser.next();		
					String nickname = parser.getText();
					weiNotice.setNickname(nickname);
				}else if (parser.getName().equals("sex")){
					parser.next();		
					String sex = parser.getText();
					weiNotice.setSex(sex);
					Log.d("WeiNoticeMsgProvider", "sex"+sex);
				}else if (parser.getName().equals("headimgurl")){
					parser.next();		
					String headimgurl = parser.getText();
					weiNotice.setHeadimgurl(headimgurl);
					Log.d("WeiNoticeMsgProvider", "headimgurl"+headimgurl);
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("notice")) {
					iq.setWeiNotice(weiNotice);
					done = true;
				}
			}
		}		
		return iq;
	}

}