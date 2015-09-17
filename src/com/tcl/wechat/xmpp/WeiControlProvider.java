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
 * @ClassName: WeiControlProvider
 * @Description: TODO
 */

public class WeiControlProvider implements IQProvider {
	private String tag = "WeiControlProvider";
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		WeiControlResultIQ iq = new WeiControlResultIQ("");
		WeiXinMsgRecorder weiXinMsg = new WeiXinMsgRecorder();
		
		iq.setType(Type.RESULT);
		boolean done = false;
		while (!done) {
			
			int eventType = parser.next();
			/*“playType” : "history",
			“vrsAlbumId”："123456789",
			“vrsTvId”："123456789",
			“vrsChnId”: “12”;
			“albumId” : “12345”;
			“history”: “0”,
			“customer”: "hisense",
			“device”: "H01-23"*/
			if (eventType == XmlPullParser.START_TAG) {
				Log.i(tag,"parser.getName()="+parser.getName());
				if (parser.getName().equals("errorcode")){
					parser.next();		
					String err = parser.getText();
					iq.setErrorcode(err);
				}else if (parser.getName().equals("openid")){
					parser.next();		
					String openid = parser.getText();
					weiXinMsg.setOpenid(openid);
					Log.i(tag,"parser.getopenid()="+openid);
				}else if (parser.getName().equals("type")){
					parser.next();		
					String msgtype = parser.getText();
					weiXinMsg.setMsgtype(msgtype);
					Log.i(tag,"type="+msgtype);
				}
//				else if (parser.getName().equals("command")){//command表示推荐的视频的海报url
//					parser.next();		
//					String command = parser.getText();
//					weiXinMsg.setCommand(command);
//					Log.i(tag,"command="+command);
//				}//上面是两种机型共有的节点
//				else if (parser.getName().equals("playType")){
//					parser.next();		
//					String playType = parser.getText();
//					weiXinMsg.setplayType(playType);
//				}else if (parser.getName().equals("albumId")){
//					parser.next();		
//					String albumId = parser.getText();
//					weiXinMsg.setalbumId(albumId);
//				}else if (parser.getName().equals("vrsAlbumId")){
//					parser.next();		
//					String vrsAlbumId = parser.getText();
//					weiXinMsg.setvrsAlbumId(vrsAlbumId);
//				}else if (parser.getName().equals("vrsTvId")){
//					parser.next();		
//					String vrsTvId = parser.getText();
//					weiXinMsg.setvrsTvId(vrsTvId);
//				}else if (parser.getName().equals("vrsChnId")){
//					parser.next();		
//					String vrsChnId = parser.getText();
//					weiXinMsg.setvrsChnId(vrsChnId);
//				}else if (parser.getName().equals("history")){
//					parser.next();		
//					String history = parser.getText();
//					weiXinMsg.sethistory(history);
//				}else if (parser.getName().equals("customer")){
//					parser.next();		
//					String customer = parser.getText();
//					weiXinMsg.setcustomer(customer);
//				}else if (parser.getName().equals("device")){
//					parser.next();		
//					String device = parser.getText();
//					weiXinMsg.setdevice(device);
//				}//下面是芒果影视需要的节点
//				else if (parser.getName().equals("player")){
//					parser.next();		
//					String player = parser.getText();
//					weiXinMsg.setplayer(player);
//					Log.i(tag,"player="+player);
//				}else if (parser.getName().equals("action")){
//					parser.next();		
//					String action = parser.getText();
//					weiXinMsg.setaction(action);
//					Log.i(tag,"action="+action);
//				}else if (parser.getName().equals("cmdex")){
//					parser.next();		
//					String cmdex = parser.getText();
//					weiXinMsg.setcmdex(cmdex);
//					Log.i(tag,"cmdex="+cmdex);
//				}else if (parser.getName().equals("videoid")){
//					parser.next();		
//					String videoid = parser.getText();
//					weiXinMsg.setvideoid(videoid);
//					Log.i(tag,"videoid="+videoid);
//				}else if (parser.getName().equals("videotype")){
//					parser.next();		
//					String videotype = parser.getText();
//					weiXinMsg.setvideotype(videotype);
//					Log.i(tag,"videotype="+videotype);
//				}else if (parser.getName().equals("videouistyle")){
//					parser.next();		
//					String videouistyle = parser.getText();
//					weiXinMsg.setvideouistyle(videouistyle);
//					Log.i(tag,"videouistyle="+videouistyle);
//				}else if (parser.getName().equals("location")){
//					parser.next();		
//					String location = parser.getText();
//					weiXinMsg.setlocation(location);
//					Log.i(tag,"location="+location);
//				}else if (parser.getName().equals("shifttime")){
//					parser.next();		
//					String shifttime = parser.getText();
//					weiXinMsg.setshifttime(shifttime);
//					Log.i(tag,"shifttime="+shifttime);
//				}else if (parser.getName().equals("shiftend")){
//					parser.next();		
//					String shiftend = parser.getText();
//					weiXinMsg.setshiftend(shiftend);
//					Log.i(tag,"shiftend="+shiftend);
//				}else if (parser.getName().equals("sp")){
//					parser.next();		
//					String sp = parser.getText();
//					weiXinMsg.setsp(sp);
//					Log.i(tag,"sp="+sp);
//				}else if (parser.getName().equals("showid")){
//					parser.next();		
//					String showid = parser.getText();
//					weiXinMsg.setshowid(showid);
//					Log.i(tag,"showid="+showid);
//				}else if (parser.getName().equals("cats")){
//					parser.next();		
//					String cats = parser.getText();
//					weiXinMsg.setcats(cats);
//					Log.i(tag,"cats="+cats);
//				}else if (parser.getName().equals("vid")){
//					parser.next();		
//					String vid = parser.getText();
//					weiXinMsg.setvid(vid);
//					Log.i(tag,"vid="+vid);
//				}else if (parser.getName().equals("title")){
//					parser.next();		
//					String title = parser.getText();
//					weiXinMsg.settitle(title);
//					Log.i(tag,"title="+title);
//				}else if (parser.getName().equals("img")){
//					parser.next();		
//					String img = parser.getText();
//					weiXinMsg.setimg(img);
//					Log.i(tag,"img="+img);
//				}else if (parser.getName().equals("point")){
//					parser.next();		
//					String point = parser.getText();
//					weiXinMsg.setpoint(point);
//					Log.i(tag,"point="+point);
//				}else if(parser.getName().equals("channelname")){
//					parser.next();		
//					String channelname = parser.getText();
//					weiXinMsg.setchannelname(channelname);
//					Log.i(tag, "chanelname="+channelname);
//				}			
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("control")) {
					iq.setWeiXinMsg(weiXinMsg);
					done = true;
				}
			}
		}		
		return iq;
	}

}
