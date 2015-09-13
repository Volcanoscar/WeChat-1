/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.receiver;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.controller.WeiXinMsgManager;
import com.tcl.wechat.controller.WeiXinNotifier;
import com.tcl.wechat.modle.WeiXinMsg;
import com.tcl.wechat.xmpp.WeiXmppCommand;

/**
 * 微信消息接收器
 * @author rex.lei
 * @see
 * 	1）注册消息
 * 	2）接收消息-->发送通知--发送给消息监听者
 * 	3）反馈服务器
 *
 */
public class WeiXinMsgReceiver extends BroadcastReceiver implements IConstant{
	
	private static final String TAG = WeiXinMsgReceiver.class.getSimpleName();

	private WeiXinMsgManager mWeiXinMsgManager = WeiXinMsgManager.getInstance();
	
	private WeiXinNotifier mWeiXinNotifier = WeiXinNotifier.getInstance();
	
	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i(TAG, "Received push notification!");
		
		if (intent.getExtras() == null){
			Log.e(TAG, "intent.getExtras() is NULL!!");
			return;
		}
		
		WeiXinMsg weiXinMsg = (WeiXinMsg)intent.getExtras().getSerializable("weiXinMsg");
		//消息为空，则返回
		if (weiXinMsg == null){
			Log.e(TAG, "weiXinMsg is NULL!!");
			return;
		}
		
		/**
		 * 1、通知消息
		 */
		mWeiXinNotifier.notify(weiXinMsg);
		
		/**
		 * 2、接收微信消息
		 */
		String type = weiXinMsg.getMsgtype();
		Log.i(TAG, "msgtype is:" + type);
		
		if (ChatMsgType.TEXT.equals(type)){    
			
			//接收文本消息
			mWeiXinMsgManager.receiveTextMsg(weiXinMsg);
		} else if (ChatMsgType.IMAGE.equals(type)){ 
			
			//接收图片消息
			mWeiXinMsgManager.receiveImageMsg(weiXinMsg);
		} else if (ChatMsgType.VOICE.equals(type)){
			
			//接收音频消息
			mWeiXinMsgManager.receiveVoiceMsg(weiXinMsg);
		} else if (ChatMsgType.VIDEO.equals(type)){
			
			//接收视频消息
			mWeiXinMsgManager.receiveVideoMsg(weiXinMsg);
		} else if (ChatMsgType.BARRAGE.equals(type)){
			
			//接收弹幕消息
			mWeiXinMsgManager.receiveBarrageMsg(weiXinMsg);
		} else if (ChatMsgType.NOTICE.equals(type)){
			
			//接收预约节目提醒
			mWeiXinMsgManager.receiveNoticeMsg(weiXinMsg);
		} else {
			Log.w(TAG, "Could not recognize message type:" + type);
		}
		
		/**
		 *3、收到消息给服务器发送反馈		
		 */
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("openid",	weiXinMsg.getOpenid());
		hashMap.put("msgtype",	weiXinMsg.getMsgtype());
		hashMap.put("msgid", 	weiXinMsg.getmsgid());
		new WeiXmppCommand(null, CommandType.COMMAND_MSGRESPONSE, hashMap).execute();
	}
}
