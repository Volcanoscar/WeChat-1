package com.tcl.wechat.ui.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.tcl.wechat.R;
import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.controller.WeiXinMsgManager;
import com.tcl.wechat.controller.listener.LoginStateListener;
import com.tcl.wechat.utils.SystemShare.SharedEditer;
import com.tcl.wechat.utils.UIUtils;
import com.tcl.wechat.xmpp.WeiXmppManager;
import com.tcl.wechat.xmpp.WeiXmppService;

public class LoginActivity extends Activity implements WeiConstant{
	
	private static final String TAG = "LoginActivity";
	
	private SharedEditer mEditer;
	
	private Timer mTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_loading);
		
		mEditer = new SharedEditer();
		
        startLogin();     
	}
	
	/**
	 * 判断用户是否注册成功，包含用户信息已经保存
	 * @return
	 */
	private boolean isRegistener(){
		
		return mEditer.getBoolean(SystemShared.KEY_REGISTENER_SUCCESS, false);
	}
	
	/**
	 * 开始登陆
	 */
	public void startLogin() {
		
		Log.i(TAG, "start Login...");
		mEditer.putBoolean(SystemShared.KEY_FLAG_ENTER, false);
		
		//长连接已存在
		if (WeiXmppManager.getInstance().isConnected()) {
			startToEnter();
			return;
		}
		
		//网络不可用，已经注册完成
		if (!UIUtils.isNetworkAvailable() && isRegistener()){
			startToEnter();
			return;
		}
		
		mEditer.putBoolean(SystemShared.KEY_FLAG_ENTER, false);
		
		addLoginStateListenter();
		WeiXinMsgManager.getInstance().setLoginStateListener(mLoginStateListener);
		
		if (WeiXmppManager.getInstance().isRegister()){
			WeiXmppManager.getInstance().login();
		} else {
			Intent serviceIntent = new Intent(this, WeiXmppService.class);
			serviceIntent.putExtra("startmode", WeiConstant.StartServiceMode.OWN);
			serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startService(serviceIntent);
		}
	}

	/**
	 * 监听登陆状态
	 */
	private void addLoginStateListenter(){
		
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if (isRegistener()){
					startToEnter();
				} /*else { // 进程已经起来，会自动注册
					//没有注册成功，则继续登录
					Intent serviceIntent = new Intent(LoginActivity.this, WeiXmppService.class);
					serviceIntent.putExtra("startmode", WeiConstant.StartServiceMode.OWN);
					serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startService(serviceIntent);
				}*/
			}
		}, LOG_IN_TIMEOUT);
	}
	
	/**
	 * 进入主界面（此处已经注册成功）
	 */
	private void startToEnter(){
		
		if (mTimer != null){
			mTimer.cancel();
		}
		
		mEditer.putBoolean(SystemShared.KEY_FLAG_ENTER, true);
		
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	/**
	 * 登录状态监听器
	 */
	private LoginStateListener mLoginStateListener = new LoginStateListener() {
		
		@Override
		public void onLoginSuccess() {
			// TODO Auto-generated method stub
			startToEnter();
		}
		
		@Override
		public void onLoginFailed(int errorCode) {
			
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (mTimer != null){
			mTimer.cancel();
			mTimer = null;
		}
 	}
	
}
