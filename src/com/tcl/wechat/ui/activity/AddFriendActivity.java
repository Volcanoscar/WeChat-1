package com.tcl.wechat.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tcl.wechat.R;
import com.tcl.wechat.utils.QrUtil;
import com.tcl.wechat.view.UserInfoView;

/**
 * 添加好友界面，提供二维码名片供其他用户使用
 * @author rex.lei
 *
 */
public class AddFriendActivity extends Activity {
	
	private Context mContext;
	private UserInfoView mUserInfoView;
	private ImageView mPersonalQrImg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_qrcode);
		
		mContext = AddFriendActivity.this;
		
		initView();
	}

	/**
	 * 界面初始化
	 */
	private void initView() {
		mUserInfoView = (UserInfoView) findViewById(R.id.uv_personal_icon);
		mPersonalQrImg = (ImageView) findViewById(R.id.img_personal_qrcode);
		
		mUserInfoView.setUserNameVisible(View.GONE);
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		
		new QrUtil(mContext).createQRCode(mPersonalQrImg, "WeChat", R.drawable.head_user_icon);
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
	
	private void onsop() {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
