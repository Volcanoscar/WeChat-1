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
import android.os.Bundle;
import android.util.Log;

import com.tcl.wechat.common.IConstant.EventType;
import com.tcl.wechat.controller.WeiXinMsgManager;
import com.tcl.wechat.model.WeiNotice;
import com.tcl.wechat.xmpp.WeiXmppCommand;

/**
 * 微信用户绑定/解绑监听器
 * @author rex.lei
 *
 */
public class WeiXinNoticeReceiver extends BroadcastReceiver{

	private static final String TAG = WeiXinNoticeReceiver.class.getSimpleName();
 	
	private WeiXinMsgManager mWeiXinMsgManager = WeiXinMsgManager.getInstance();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();
		if (bundle != null){
			WeiNotice weiNotice = bundle.getParcelable("weiNotice");
			if (weiNotice == null){
				Log.w(TAG, "receive weiNotice is NULL");
				return ;
			}
		}
		
		WeiNotice weiNotice = (WeiNotice)intent.getExtras().getParcelable("weiNotice");
		if (weiNotice == null){
			Log.w(TAG, "receive weiNotice is NULL");
			return ;
		}
		Log.d(TAG, "receive bind event:" + weiNotice.getEvent());
		Log.i(TAG, "weiNotice:" + weiNotice.toString());
		
		//1、处理Notice消息
		mWeiXinMsgManager.receiveNoticeMsg(weiNotice);
		
		//2、收到消息给服务器发送反馈
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("openid",	weiNotice.getOpenId());
		hashMap.put("msgtype",	"unsubscribeEvent");
		new WeiXmppCommand(EventType.TYPE_RESPONSE_SERVER, hashMap, null).execute();
	}
}
