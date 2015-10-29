package com.tcl.wechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.controller.OnLineStatusMonitor;
import com.tcl.wechat.controller.WeiXinMsgManager;
import com.tcl.wechat.model.OnLineStatus;

/**
 * 用户状态检测器
 * @author rex.lei
 *
 */
public class MotitorReceiver extends BroadcastReceiver implements IConstant{

	private static final String TAG = MotitorReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		Log.d(TAG, "action:" + action);
		if (ACTION_ONLINE_ALARM.equals(action)){
			Bundle bundle = intent.getExtras();
			if (bundle != null){
				OnLineStatus status = (OnLineStatus) bundle.getSerializable("OnLineStatus");
				if (status == null){
					Log.w(TAG, "MotitorReceiver ERROR,getParcelableExtra is NULL! ");
					return ;
				}
				String openid = status.getOpenid();
				OnLineStatusMonitor.getInstance().stopMonitor(openid);
				WeiXinMsgManager.getInstance().notifyUserStatusChaned(status);
			}
		}
	}
}
