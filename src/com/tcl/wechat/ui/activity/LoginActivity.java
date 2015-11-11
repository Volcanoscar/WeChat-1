package com.tcl.wechat.ui.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.utils.SystemInfoUtil;
import com.tcl.wechat.xmpp.WeiXmppManager;
import com.tcl.wechat.xmpp.WeiXmppService;

/**
 * 登录主界面
 * @author rex.lei
 *
 */
public class LoginActivity extends Activity implements IConstant{
	
	private static final String TAG = LoginActivity.class.getSimpleName();
	
	/**
	 * 登陆超时定时器
	 */
	private Timer mTimer;
	
	private Thread initThread = null;
	
	private boolean bLoading = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        
        bLoading = true;
        WeApplication.bLoginSuccess = false;
    }
    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		startLogin();
		
		if( initThread != null){
 			initThread.interrupt();
 			initThread = null;
 		}
		initThread = new Thread(initRun);
		initThread.start();
	}
	
	private Runnable initRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (bLoading) {
				if (WeApplication.bLoginSuccess){
					startToEnter();
					return ;
				} 
				waitFor();
			}
		}
	};
	
	/**
	 * 开始登陆
	 */
	public void startLogin() {
		
		Log.d(TAG, "start Login...");
		if (WeiXmppManager.getInstance().isRegister()){//用户已经注册
			
			//网络不可用
			//if (!NetWorkUtil.isNetworkAvailable()){
			//	ToastUtil.showToastForced(R.string.notwork_not_available);
			//	//add by Rex:网络不可用
			//	if (WeiXmppManager.getInstance().isRegister()) {//已经注册，直接进入
			//		startToEnter();
			//	} else {  //未注册，退出
			//		ToastUtil.showToastForced(R.string.no_register);
			//	}
			//	return;
			//} 
			/**
			 * 增加超時处理 (可直接使用timer即可)
			 */
			//设置超时监听
			mTimer = new Timer();
			mTimer.schedule(mTimeoutTask, LOGIN_TIME_OUT);
			
			//长连接已存在，并且已经注册
			if (WeiXmppManager.getInstance().isConnected()) {
				WeApplication.bLoginSuccess = true;
			} else {
				WeiXmppManager.getInstance().login();
			}
			
		}  else {
			
			//未注册用户，则启动服务开始注册
			Intent serviceIntent = new Intent(LoginActivity.this, 
					WeiXmppService.class);
			serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);        	 			
			startService(serviceIntent);
		}
	}
	
	private TimerTask mTimeoutTask = new TimerTask() {
		
		@Override
		public void run() {
			startToEnter();
		}
	};
	
	/**
	 * 进入主应用
	 */
	private void startToEnter() {
		Log.i(TAG, "start to enter main view!");
		if (SystemInfoUtil.isTopActivity()) {
			Intent intent = new Intent(LoginActivity.this, 
					FamilyBoardMainActivity.class);
			startActivity(intent);
		}
		finish();
	}

	/**
	 * 等待
	 */
	private void waitFor(){
		try {
			Log.i(TAG, "waitFor....");
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
    @Override
    protected void onPause() {
    	super.onPause();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	bLoading = false;
    	WeApplication.bLoginSuccess = false;
    	
    	if (mTimer != null) {
    		mTimer.cancel();
    		mTimer = null;
    	}
    	
    	if( initThread != null){
 			initThread.interrupt();
 			initThread = null;
 		}
    }
}
