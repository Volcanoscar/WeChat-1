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

import com.tcl.wechat.model.BindUser;

/**
 * @ClassName: WeiNoticeMsgProvider
 */

public class WeiRemoteBindProvider implements IQProvider {
	
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		WeiRemoteBindResultIQ iq = new WeiRemoteBindResultIQ("");
		BindUser bindUser = new BindUser();
		iq.setType(Type.RESULT);
		boolean done = false;
		while (!done) {
			
			int eventType = parser.next();
			
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("errorcode")){
					parser.next();		
					String err = parser.getText();
					iq.setErrorcode(err);
				}else if (parser.getName().equals("memberid")){
					parser.next();		
					String memberid = parser.getText();
//					TODO bindUser.setMemberId(memberid);
				}else if (parser.getName().equals("openid")){
					parser.next();		
					String openid = parser.getText();
					bindUser.setOpenId(openid);
				}else if (parser.getName().equals("nickname")){
					parser.next();		
					String nickname = parser.getText();
					bindUser.setNickName(nickname);
				}else if (parser.getName().equals("sex")){
					parser.next();		
					String sex = parser.getText();
					bindUser.setSex(sex);
					Log.d("WeiRemoteBindProvider", "sex"+sex);
				}else if (parser.getName().equals("headimgurl")){
					parser.next();		
					String headimgurl = parser.getText();
					bindUser.setHeadImageUrl(headimgurl);  
					Log.d("WeiRemoteBindProvider", "headimgurl"+headimgurl);
				}else if (parser.getName().equals("reply")){
					parser.next();		
					String reply = parser.getText();
					bindUser.setReply(reply);
					Log.d("WeiRemoteBindProvider", "reply"+reply);
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("remotebind")) {
					iq.setWeiRemoteBind(bindUser);
					done = true;
				}
			}
		}		
		return iq;
	}

}