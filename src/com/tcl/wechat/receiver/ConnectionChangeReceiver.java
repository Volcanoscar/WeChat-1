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
import android.net.ConnectivityManager;
import android.util.Log;

import com.tcl.wechat.common.IConstant.StartServiceMode;
import com.tcl.wechat.utils.NetWorkUtil;
import com.tcl.wechat.xmpp.WeiXmppManager;
import com.tcl.wechat.xmpp.WeiXmppService;

/**
 * @ClassName: ConnectionChangeReceiver
 * @Description: 网络状态广播
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
	
	private static final String TAG = ConnectionChangeReceiver.class.getSimpleName();
	
	private static boolean firstConnect = true;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		String action = intent.getAction();
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
			
			if (NetWorkUtil.isWifiConnected()){
				if (firstConnect){
					firstConnect = false;
					Log.d(TAG, "network connected!!");
					Intent serviceIntent = new Intent(context, WeiXmppService.class);
			    	serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			    	serviceIntent.putExtra("startmode", StartServiceMode.OWN);
			    	context.startService(serviceIntent); 
				}
				
			} else {
				WeiXmppManager.getInstance().disconnection();
				firstConnect = true;
				Log.d(TAG, "network disConnected!!");
			}
		}
	}
}
