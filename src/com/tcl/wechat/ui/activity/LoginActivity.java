package com.tcl.wechat.ui.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.utils.NetWorkUtil;
import com.tcl.wechat.utils.SystemInfoUtil;
import com.tcl.wechat.utils.SystemShare.SharedEditer;
import com.tcl.wechat.utils.ToastUtil;
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
	
	private SharedEditer mEditer ;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        
        mEditer = new SharedEditer();
    }
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	
		registerBoradcast();
		startLogin(); 
    }
    
    /**
	 * 注册广播，用于监听登陆是否成功
	 */
	private void registerBoradcast() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter();
		filter.addAction(CommandAction.ACTION_LOGIN_SUCCESS);
		registerReceiver(receiver, filter);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (CommandAction.ACTION_LOGIN_SUCCESS.equals(intent.getAction())){
				startToEnter();
			}
		}
	};
	

	/**
	 * 开始登陆
	 */
	public void startLogin() {
		
		Log.d(TAG, "start Login...");
		if (WeiXmppManager.getInstance().isRegister()){//用户已经注册
			
			//设置超时监听
			mTimer = new Timer();
			mTimer.schedule(mTimeoutTask, LOGIN_TIME_OUT);
			
			//网络不可用
			if (!NetWorkUtil.isNetworkAvailable()){
				ToastUtil.showToastForced(R.string.notwork_not_available);
				//add by Rex:网络不可用
				if (WeiXmppManager.getInstance().isRegister()) {//已经注册，直接进入
					startToEnter();
				} else {  //未注册，退出
					ToastUtil.showToastForced(R.string.no_register);
				}
				return;
			} 
			
			//长连接已存在，并且已经注册
			if (WeiXmppManager.getInstance().isConnected()) {
				startToEnter();
			} else {
				WeiXmppManager.getInstance().login();
			}
			return;
		}
		
		//未注册用户，则启动服务开始注册
		Intent serviceIntent = new Intent(LoginActivity.this, WeiXmppService.class);
		serviceIntent.putExtra("startmode", StartServiceMode.OWN);
    	serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);        	 			
    	startService(serviceIntent);
		
	}
	
	/**
	 * 进入主界面（此处已经注册成功）
	 */
	private void startToEnter(){
		
		new AsyncTask<Void, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(Void... params) {
				while (true) {
					if (mEditer.getBoolean(SystemShared.KEY_REGISTENER_SUCCESS, false)){
						return true;
					} 
					waitFor();
					
				}
			}
			protected void onPostExecute(Boolean result) {
				if (result) {
					if (SystemInfoUtil.isTopActivity() &&  
							WeiXmppManager.getInstance().isRegister()){
						Intent intent = new Intent(LoginActivity.this, 
								FamilyBoardMainActivity.class);
						startActivity(intent);
					} 
					if (mTimer != null){
						mTimer.cancel();
					}
					finish();
				}
			};
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}
	
	private TimerTask mTimeoutTask = new TimerTask() {
		
		@Override
		public void run() {
			startToEnter();
		}
	};
	
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
    	if (mTimer != null){
			mTimer.cancel();
		}
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
		unregisterReceiver(receiver);
    }
}
