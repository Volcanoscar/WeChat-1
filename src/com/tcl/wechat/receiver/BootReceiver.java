package com.tcl.wechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//开机启动完成广播消息
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
			
		}
	}

}
