package com.tcl.wechat.ui.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.tcl.wechat.R;
import com.tcl.wechat.action.UserHelper;
import com.tcl.wechat.modle.User;
import com.tcl.wechat.view.UserInfoView;
import com.tcl.wechat.view.listener.UserIconClickListener;

/**
 * 主界面Activity
 * @author rex.lei
 *
 */
public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	
	private Context mContext;
	
	private UserInfoView mUserInfo;
	private UserInfoView mAddFriend;
	
	/**
	 * 好友模块
	 */
	private ArrayList<User> mAllUsers;
	
	/**
	 * 留言板模块
	 */
	
	/**
	 * 工具类
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.activity_main);
		
		mContext = MainActivity.this;
		
		initData();
		initView();
	}
	
	private void initData() {
		Log.i(TAG, "initData-->>");
		
		mAllUsers = UserHelper.getAllUsers(mContext);
		
		Log.i(TAG, "mAllUsers:" + mAllUsers);
	}

	private void initView() {
		mUserInfo = (UserInfoView) findViewById(R.id.user_info);
		mAddFriend = (UserInfoView) findViewById(R.id.add_fiend);
		
		mUserInfo.setUserIconClickListener(new UserIconClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(mContext, PersonalInfoActivity.class);
				startActivity(intent);
			}
		});
		mUserInfo.setUserIcon(BitmapFactory.decodeResource(getResources(), 
				R.drawable.big_head));
		mUserInfo.setUserName("Rex");
		
		mAddFriend.setUserIconClickListener(new UserIconClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onClick To AddFriend-->>");
				Intent intent = new Intent(mContext, QRCodeActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}
	
	/**
	 * 点击进入用户信息界面，可进行相关信息编辑
	 */
	public void enToUserIDetaiClick(View v){
		Intent intent = new Intent(mContext, PersonalInfoActivity.class);
		startActivity(intent);
	}
	
	public void onBtnReplyClick(View v){
		Intent intent = new Intent(mContext, ChatActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
