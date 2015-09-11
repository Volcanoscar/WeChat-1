/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.receiver;

import java.util.ArrayList;

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
	private String tag = "OthersAppExitQRPageReceiver";
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d(tag, "收到其他应用退出二维码页面的广播");	
		WeiConstant.StartServiceMode.CURRENTMODE = WeiConstant.StartServiceMode.OWN;
		//如果当前没有用户，关闭长连接，后台服务退出
		ArrayList<String> list =new ArrayList<String>();
		WeiUserDao dao = new WeiUserDao(context);
		Log.d(tag, "11111111111111");	
		if(dao.find().size()<=0){
			Log.d(tag, "222222222222");	
			Intent serviceIntent = new Intent(context, WeiXmppService.class);      	 			
			context.stopService(serviceIntent); 
		}
	}
}		
	