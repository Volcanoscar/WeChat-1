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
import android.os.Bundle;
import android.util.Log;

import com.tcl.wechat.model.BindUser;

/**
 * 绑定用户监听器  ？？？？？？？？？？
 * @author rex.lei
 *
 */
public class WeiXinRemoteBindReceiver extends BroadcastReceiver{

	private static final String TAG = WeiXinRemoteBindReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i(TAG, "Received WeiXinRemoteBindUser");
		
		Bundle bundle = intent.getExtras();
		
		if (bundle != null){
			BindUser bindUser = bundle.getParcelable("bindUser");
			if (bindUser != null){
				
			} else {
				Log.w(TAG, "bindUser is NULL");
			}
		}
	}
}
