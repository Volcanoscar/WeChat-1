package com.tcl.wechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.tcl.wechat.R;
import com.tcl.wechat.utils.NetWorkUtil;
import com.tcl.wechat.utils.WeixinToast;
import com.tcl.wechat.xmpp.WeiXmppManager;
import com.tcl.wechat.xmpp.WeiXmppService;

/**
 * 网络状态广播接收器
 * @author Rex.lei
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
	
	private static final String TAG = NetworkChangeReceiver.class.getSimpleName();
	
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
					startService(context); 
				}
				
			} else {
				WeiXmppManager.getInstance().disconnection();
				firstConnect = true;
				Log.d(TAG, "network disConnected!!");
				WeixinToast.makeText(context, R.string.network_not_available).show();
			}
		}
	}
	
	/**
	 * 启动服务
	 * @param context
	 */
	private void startService(Context context) {
		Intent serviceIntent = new Intent(context, WeiXmppService.class);
		context.startService(serviceIntent); 
	}
}
