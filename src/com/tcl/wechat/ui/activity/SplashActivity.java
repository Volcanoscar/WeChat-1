package com.tcl.wechat.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.tcl.wechat.R;
import com.tcl.wechat.common.IConstant;

public class SplashActivity extends BaseActivity implements IConstant{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				long start = System.currentTimeMillis();
				
				
				long costTime = System.currentTimeMillis() - start;
				
				if (LOGIN_TIME_OUT - costTime > 0) {
					try {
						Thread.sleep(LOGIN_TIME_OUT - costTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//进入主页面
				startActivity(new Intent(SplashActivity.this, 
						FamilyBoardMainActivity.class));
				finish();
			}
		}).start();
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
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
}
