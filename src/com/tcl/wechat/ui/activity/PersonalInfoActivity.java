package com.tcl.wechat.ui.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.tcl.wechat.R;
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.data.DataFileTools;
import com.tcl.wechat.view.UserInfoView;

/**
 * 用户详细信息界面
 * @author rex.lei
 *
 */
public class PersonalInfoActivity extends Activity{
	
	private UserInfoView mUserInfoView ;
	private EditText mEditUserNameEdt;
	
	private BindUser mSystemUser ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_personal_info);
        
        
        initData();
        initView();
	}
	
	private void initData() {
		//现从传递过来的Extra 获取数据，如果获取失败，则从数据库读取
		if (getIntent() != null && getIntent().getExtras() != null){
			mSystemUser = getIntent().getExtras().getParcelable("BindUser");
		} else {
			mSystemUser = WeiUserDao.getInstance().getAllUsers().get(0);
		}
	}

	private void initView() {
		
		
		mUserInfoView = (UserInfoView) findViewById(R.id.uv_personal_icon);
		
		if (mSystemUser != null){
			Bitmap userIcon = DataFileTools.getInstance()
					.getBindUserIcon(mSystemUser.getHeadImageUrl());
			mUserInfoView.setUserIcon(userIcon, false);
			mUserInfoView.setUserName(mSystemUser.getRemarkName());
		}
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
