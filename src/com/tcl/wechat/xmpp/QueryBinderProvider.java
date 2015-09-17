/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.xmpp;

/**
 * @ClassName: QueryBinderProvider
 */


import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

import com.tcl.wechat.modle.BindUser;

/**
 * @ClassName: GetQrProvider
 */

public class QueryBinderProvider implements IQProvider {
	
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		QueryBinderResultIQ iq = new QueryBinderResultIQ("");
		ArrayList<BindUser> files = new ArrayList<BindUser>();
		BindUser binderUser = null;
		iq.setType(Type.RESULT);
		boolean done = false;
		while (!done) {
			
			int eventType = parser.next();
			
			if (eventType == XmlPullParser.START_TAG) {
				Log.d("QueryBinderProvider", "name="+parser.getName());
				if (parser.getName().equals("errorcode")){
					parser.next();		
					String err = parser.getText();
					iq.setErrorcode(err);
				}else if (parser.getName().equals("item")){
					Log.d("QueryBinderProvider", "new item=");
					binderUser = new BindUser();
					Log.i("andy","item00000000000");
				}else if (parser.getName().equals("openid")){
					parser.next();		
					String openid = parser.getText();
					binderUser.setOpenId(openid);
				}else if (parser.getName().equals("nickname")){
					parser.next();		
					String nickname = parser.getText();
					Log.d("QueryBinderProvider", "nickname="+nickname);
					binderUser.setNickName(nickname);
				}else if (parser.getName().equals("sex")){
					parser.next();		
					String sex = parser.getText();
					Log.d("QueryBinderProvider", "nickname="+sex);
					binderUser.setSex(sex);
				}else if (parser.getName().equals("headimgurl")){
					parser.next();		
					String headimgurl = parser.getText();
					Log.d("QueryBinderProvider", "headimgurl="+headimgurl);
					binderUser.setHeadImageUrl(headimgurl);
				}
				Log.i("andy","parser.getName()"+parser.getName()+"parser.getText()="+parser.getText());
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("querybinder")) {
					done = true;
					Log.d("QueryBinderProvider", "files size="+files.size());
					iq.setFiles(files);
					Log.i("andy","files.size="+files.size());
				}else if (parser.getName().equals("item")) {
					files.add(binderUser);
					Log.d("QueryBinderProvider", "end item");
				}
			}
		}		
		return iq;
	}

}