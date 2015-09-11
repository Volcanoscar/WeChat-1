package com.tcl.wechat.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tcl.wechat.R;
import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.receiver.WeiXinMsgReceiver;
import com.tcl.wechat.receiver.WeiXinNoticeReceiver;
import com.tcl.wechat.receiver.WeiXinLoginReceiver;
import com.tcl.wechat.utils.BaseUIHandler;
import com.tcl.wechat.xmpp.WeiXmppManager;
import com.tcl.wechat.xmpp.WeiXmppService;

public class LoginActivity extends Activity{
	
	private static final String TAG = "LoginActivity";

	String userName = "Tom";
	private boolean isFirstIn = false;
	
	private Handler timeHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		
		Log.i(TAG, "onCreate..");
		
		initHander();
		//开始登陆
        startLogin();     
	}
	
	private void initHander(){
		uiHandler.setActivity(this);
		uiNoticeHandler.setActivity(this);
		uiMsgHandler.setActivity(this);

		WeiXinLoginReceiver.setHandler(uiHandler);
		WeiXinNoticeReceiver.setHandler(uiNoticeHandler);
		WeiXinMsgReceiver.setHandler(uiMsgHandler);
	}
	
	/**
	 * 如果当前没有连接重新登陆
	 */
	public void startLogin() {
		
		Log.i(TAG, "start Login...");

		timeHandler.removeCallbacks(mUpdateTimeTask);
		timeHandler.postDelayed(mUpdateTimeTask, WeiConstant.LOG_IN_TIMEOUT);

		if (WeiXmppManager.getInstance().isConnected()) {
			Log.d(TAG, "长连接已经存在");
			uiHandler.sendEmptyMessageDelayed(CommandType.LONGCONNECTION_EXIST,WeiConstant.WAIT_LOGO_TIME);
			// uiHandler.sendEmptyMessage(CommandType.LONGCONNECTION_EXIST);
			return;
		}
		Intent serviceIntent = new Intent(this, WeiXmppService.class);
		serviceIntent.putExtra("startmode", WeiConstant.StartServiceMode.OWN);
		serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(serviceIntent);
	}

	
	/**
	 * 网络请求超时后跳转
	 */
	private Runnable mUpdateTimeTask = new Runnable() {

		public void run() {
			
			Toast.makeText(LoginActivity.this,"Network Timeout", Toast.LENGTH_LONG).show();
			if (isFirstIn) {
				goGuide(); //

			} else {
				goHomePage();  
			}

		}
	};
	
	
	private BaseUIHandler uiHandler = new BaseUIHandler<Object, Activity>() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Message m = msg;
			switch (m.what) {
			case CommandType.LOGIN_GETDATA_SUCCESS:
				Log.i(TAG, "LOGIN_GETDATA_SUCCESS");
				timeHandler.removeCallbacks(mUpdateTimeTask);
				if (isFirstIn) {// 登录成功，导航页面
					goGuide();
				} else {
					goHomePage(); 
				}
				break;
			case CommandType.LONGCONNECTION_EXIST:
				timeHandler.removeCallbacks(mUpdateTimeTask);
				if (isFirstIn) {// 登录成功，导航页面
					goGuide();
				} else {
					goHomePage();
				}
				
				break;
			default:
				break;
			}
		}
	};

	
	private BaseUIHandler uiNoticeHandler = new BaseUIHandler<Object, Activity>() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Message m = msg;
			Log.i(TAG, "   - --uiNoticeHandler--------------------"+m.what);
			switch (m.what) {
 
			default:
				break;
			}
		}
	};
	
	private BaseUIHandler uiMsgHandler = new BaseUIHandler<Object, Activity>() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Message m = msg;
			Log.i(TAG, "   - --uiMsgHandler--------------------"+m.what);
			switch (m.what) {
 
			default:
				break;
			}
		}
	};


	//for test 
	protected void goHomePage() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	protected void goGuide() {
		goHomePage();
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
 		WeiXinLoginReceiver.setHandler(null);
 		finish();
 	}
	
}
