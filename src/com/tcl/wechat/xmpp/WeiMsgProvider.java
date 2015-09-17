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

import com.tcl.wechat.modle.WeiXinMsgRecorder;

/**
 * WeiMsgProvider
 * @author rex.lei
 *
 */
public class WeiMsgProvider implements IQProvider {
	
	
	private static final String TAG = WeiMsgProvider.class.getSimpleName();

	@Override
	public IQ parseIQ(XmlPullParser parser) throws Exception {

		WeiMsgResultIQ iq = new WeiMsgResultIQ("");
		
		WeiXinMsgRecorder weiXinMsg = new WeiXinMsgRecorder();
		
		iq.setType(Type.RESULT);
		
		boolean done = false;
		
		while (!done) {
			
			int eventType = parser.next();
			
			if (eventType == XmlPullParser.START_TAG) {
				
				Log.i(TAG,"parser.getName()="+parser.getName());
				
				if (parser.getName().equals("errorcode")){
					parser.next();		
					String err = parser.getText();
					iq.setErrorcode(err);
				} else if (parser.getName().equals("msgtype")){
					parser.next();		
					String msgtype = parser.getText();
					weiXinMsg.setMsgtype(msgtype);
				} else if (parser.getName().equals("content")){
					parser.next();		
					String content = parser.getText();
					weiXinMsg.setContent(content);
				} else if (parser.getName().equals("url")){					
					parser.next();		
					String mediaurl = parser.getText();
					weiXinMsg.setUrl(mediaurl);
					Log.i("andy","mediaurl="+mediaurl);
				} else if (parser.getName().equals("format")){
					parser.next();		
					String format = parser.getText();
					weiXinMsg.setFormat(format);
				} else if (parser.getName().equals("createtime")){
					parser.next();		
					String createtime = parser.getText();
					weiXinMsg.setCreatetime(createtime);
				} else if (parser.getName().equals("accesstoken")){
					parser.next();		
					String accesstoken = parser.getText();
					weiXinMsg.setAccesstoken(accesstoken);
				} else if (parser.getName().equals("mediaid")){
					parser.next();		
					String mediaid = parser.getText();
					Log.i("andy","xmpp mediaid="+mediaid);
					weiXinMsg.setMediaid(mediaid);
				} else if (parser.getName().equals("thumbmediaurl")){
					parser.next();		
					String thumbmediaid = parser.getText();
					Log.i("andy","thumbmediaid="+thumbmediaid);
					weiXinMsg.setThumbmediaid(thumbmediaid);
				} else if (parser.getName().equals("expiretime")){
					parser.next();		
					String expiretime = parser.getText();
					weiXinMsg.setExpiretime(expiretime);
				} else if (parser.getName().equals("openid")){
					parser.next();		
					String openid = parser.getText();
					weiXinMsg.setOpenid(openid);
				} else if (parser.getName().equals("recognition")){
					parser.next();		
					String recognition = parser.getText();
					weiXinMsg.setContent(recognition); //语音，发送过来的是文字信息
				} else if (parser.getName().equals("offlinemsg")){
					parser.next();		
					String offlinemsg = parser.getText();
					weiXinMsg.setOfflinemsg(offlinemsg);
				} else if (parser.getName().equals("msgid")){
					parser.next();		
					String msgid = parser.getText();
					weiXinMsg.setMsgid(msgid);
				} else {
					Log.w(TAG, "Unrecognized node:" + parser.getName());
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				Log.i(TAG, "Parser end! parser.getName = " + parser.getName());
				if (parser.getName().equals("msg")) {
					iq.setWeiXinMsg(weiXinMsg);
					done = true;
				}
			}
		}		
		return iq;
	}
}