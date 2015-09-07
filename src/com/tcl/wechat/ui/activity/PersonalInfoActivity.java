package com.tcl.wechat.ui.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tcl.wechat.R;
import com.tcl.wechat.view.UserInfoView;

/**
 * 用户详细信息界面
 * @author rex.lei
 *
 */
public class PersonalInfoActivity extends Activity{
	
	private UserInfoView mUserInfoView ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_personal_info);
        
        initView();
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		mUserInfoView = (UserInfoView) findViewById(R.id.uv_personal_icon);
		mUserInfoView.setUserNameVisible(View.GONE);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		
	}
	
	
	/**
	 * @MethodName: qrcodeStyleOnClick 
	 * @Description: TODO <二维码图片，点击更换样式>
	 * @param @param 
	 * @return void
	 * @throws
	 */
	public void qrcodeStyleOnClick(View v){
		
	}
	
	/**
	 * 返回按键
	 * @param view
	 */
	public void backOnClick(View view){
		finish();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
