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
import com.tcl.wechat.controller.WeiXinMsgControl;
import com.tcl.wechat.model.WeiXinMessage;
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

	private WeiXinMsgControl mWeiXinMsgManager = WeiXinMsgControl.getInstance();
	
	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i(TAG, "Received push notification!");
		
		if (intent.getExtras() == null){
			Log.e(TAG, "intent.getExtras() is NULL!!");
			return;
		}
		
		WeiXinMessage weiXinMsg = (WeiXinMessage)intent.getExtras().getParcelable("weiXinMsg");
		//消息为空，则返回
		if (weiXinMsg == null){
			Log.e(TAG, "weiXinMsg is NULL!!");
			return;
		}

		//1、接收消息
		mWeiXinMsgManager.receiveWeiXinMsg(weiXinMsg);
		
		//2、收到消息给服务器发送反馈
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("openid",	weiXinMsg.getOpenid());
		hashMap.put("msgtype",	weiXinMsg.getMsgtype());
		hashMap.put("msgid", 	weiXinMsg.getMsgid());
		new WeiXmppCommand(EventType.TYPE_RESPONSE_SERVER, hashMap, null).execute();
	}
}
