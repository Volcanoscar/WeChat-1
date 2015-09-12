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
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.xmpp.WeiXmppService;
/**
 * @ClassName: OthersAppExitQRPageReceiver 
 * @Description: 收到其他应用退出二维码页面的广播
 */

public class OthersAppExitQRPageReceiver extends BroadcastReceiver{
	private static final String TAG = OthersAppExitQRPageReceiver.class.getSimpleName();
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "收到其他应用退出二维码页面的广播");	
		WeiConstant.StartServiceMode.CURRENTMODE = WeiConstant.StartServiceMode.OWN;
		
		//如果当前没有用户，关闭长连接，后台服务退出
		if(WeiUserDao.getInstance().getBindUserNum() <= 0){
			Log.i(TAG, "NO bindUser, stopService!!");	
			Intent serviceIntent = new Intent(context, WeiXmppService.class);      	 			
			context.stopService(serviceIntent); 
		}
	}
}		
	