package com.tcl.wechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.ui.activity.MainActivity;

/**
 * 进入应用广播消息处理
 * @author rex.lei
 */
public class StartAppReceiver extends BroadcastReceiver implements IConstant{

	private static final String TAG = "StartAppReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		Log.i(TAG, "action:" + action);
		if (ACTION_MAINVIEW.equals(action)) {//进入应用主界面
			
			Intent mainIntent = new Intent(context, MainActivity.class);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(mainIntent);
		} else if (ACTION_PLAYVIEW.equals(action)){//进入播放界面
			
			
		} else if (ACTION_CHATVIEW.equals(action)){//进入聊天界面
			
			
		} else if (ACTION_USERINFO.equals(action)) {//进入用户信息界面
			
			
		}
	}
	
	
}
