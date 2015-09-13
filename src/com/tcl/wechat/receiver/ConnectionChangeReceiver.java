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

import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.utils.UIUtils;
import com.tcl.wechat.xmpp.WeiXmppManager;
import com.tcl.wechat.xmpp.WeiXmppService;

/**
 * @ClassName: ConnectionChangeReceiver
 * @Description: 网络状态广播
 */

public class ConnectionChangeReceiver extends BroadcastReceiver {
	 private static boolean firstConnect = true;
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Log.d("ConnectionChangeReceiver", "网络状态改变，weixin开启服务登陆");
		if (UIUtils.isNetworkAvailable()){
			if(firstConnect){
				firstConnect = false;
				Log.d("ConnectionChangeReceiver", "网络连接OK");
				Intent serviceIntent = new Intent(arg0, WeiXmppService.class);
		    	serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		    	serviceIntent.putExtra("startmode", WeiConstant.StartServiceMode.OWN);
		    	arg0.startService(serviceIntent); 
			}
		}
		else{
			Log.d("ConnectionChangeReceiver", "网 络已断开");		
			WeiXmppManager.getInstance().disconnection(); 
			firstConnect = true;
		}
		
	}

}
