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

import com.tcl.wechat.modle.WeiXinMsg;

/**
 * @ClassName: WeiMsgProvider
 */

public class WeiMsgProvider implements IQProvider {
	
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		WeiMsgResultIQ iq = new WeiMsgResultIQ("");
		WeiXinMsg weiXinMsg = new WeiXinMsg();
		iq.setType(Type.RESULT);
		boolean done = false;
		while (!done) {
			
			int eventType = parser.next();
			
			if (eventType == XmlPullParser.START_TAG) {
				Log.i("andy","parser.getName()="+parser.getName());
				if (parser.getName().equals("errorcode")){
					parser.next();		
					String err = parser.getText();
					iq.setErrorcode(err);
				}else if (parser.getName().equals("msgtype")){
					parser.next();		
					String msgtype = parser.getText();
					weiXinMsg.setMsgtype(msgtype);
				}else if (parser.getName().equals("content")){
					parser.next();		
					String content = parser.getText();
					weiXinMsg.setContent(content);
				}else if (parser.getName().equals("url")){					
					parser.next();		
					String mediaurl = parser.getText();
					weiXinMsg.setUrl(mediaurl);
					Log.i("andy","mediaurl="+mediaurl);
				}else if (parser.getName().equals("format")){
					parser.next();		
					String format = parser.getText();
					weiXinMsg.setFormat(format);
				}else if (parser.getName().equals("createtime")){
					parser.next();		
					String createtime = parser.getText();
					weiXinMsg.setCreatetime(createtime);
				}else if (parser.getName().equals("accesstoken")){
					parser.next();		
					String accesstoken = parser.getText();
					weiXinMsg.setAccesstoken(accesstoken);
				}else if (parser.getName().equals("mediaid")){
					parser.next();		
					String mediaid = parser.getText();
					Log.i("andy","xmpp mediaid="+mediaid);
					weiXinMsg.setMediaid(mediaid);
				}else if (parser.getName().equals("thumbmediaurl")){
					parser.next();		
					String thumbmediaid = parser.getText();
					Log.i("andy","thumbmediaid="+thumbmediaid);
					weiXinMsg.setThumbmediaid(thumbmediaid);
				}else if (parser.getName().equals("expiretime")){
					parser.next();		
					String expiretime = parser.getText();
					weiXinMsg.setExpiretime(expiretime);
				}
				else if (parser.getName().equals("openid")){
					parser.next();		
					String openid = parser.getText();
					weiXinMsg.setOpenid(openid);
				}else if (parser.getName().equals("nickname")){
					parser.next();		
					String nickname = parser.getText();
					weiXinMsg.setnickname(nickname);
				}else if (parser.getName().equals("headurl")){
					parser.next();		
					String headurl = parser.getText();
					weiXinMsg.setheadurl(headurl);
				}else if (parser.getName().equals("display")){
					parser.next();		
					String display = parser.getText();
					weiXinMsg.setdisplay(display);
				}else if (parser.getName().equals("recognition")){
					parser.next();		
					String recognition = parser.getText();
					weiXinMsg.setRecognition(recognition);
				}else if (parser.getName().equals("command")){
					parser.next();		
					String command = parser.getText();
					weiXinMsg.setCommand(command);
				}else if (parser.getName().equals("offlinemsg")){
					parser.next();		
					String offlinemsg = parser.getText();
					weiXinMsg.setofflinemsg(offlinemsg);
				}else if (parser.getName().equals("msgid")){
					parser.next();		
					String msgid = parser.getText();
					weiXinMsg.setmsgid(msgid);
				}else if(parser.getName().equals("channelcode")){
					parser.next();		
					String channelname = parser.getText();
					weiXinMsg.setchannelname(channelname);
				}
				
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("msg")) {
					iq.setWeiXinMsg(weiXinMsg);
					done = true;
				}
			}
		}		
		return iq;
	}

}