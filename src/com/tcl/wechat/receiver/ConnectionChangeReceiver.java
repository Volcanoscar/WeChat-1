/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.utils.NetUtil;
import com.tcl.wechat.xmpp.WeChatXmppManager;
import com.tcl.wechat.xmpp.WeChatXmppService;

/**
 * 监听网络状态变化
 * @author rex.lei
 *
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
	
	private static final String TAG = ConnectionChangeReceiver.class.getSimpleName();
	private static boolean firstConnect = true;
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		if (NetUtil.isNetworkAvailable(arg0)){
			if(firstConnect){
				firstConnect = false;
				Log.d(TAG, "网络连接OK");
				Intent serviceIntent = new Intent(arg0, WeChatXmppService.class);
		    	serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		    	serviceIntent.putExtra("startmode", IConstant.StartServiceMode.OWN);
		    	arg0.startService(serviceIntent); 
			}
		}
		else{
			Log.d(TAG, "网 络已断开");		
			WeChatXmppManager.getInstance().disconnection(); 
			firstConnect = true;
		}
	}

}
