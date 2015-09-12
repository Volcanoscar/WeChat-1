package com.tcl.wechat.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.modle.UserRecord;
import com.tcl.wechat.test.RecordDao;
import com.tcl.wechat.test.User;
import com.tcl.wechat.test.UserDao;
import com.tcl.wechat.view.UserInfoView;
import com.tcl.wechat.view.listener.UserIconClickListener;
import com.tcl.wechat.xmpp.WeiXmppManager;
import com.tcl.wechat.xmpp.WeiXmppService;

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
	private TextView mMyFriendWord;
	private TextView mMyFamilyBoardWord;
	
	/**
	 * 好友模块
	 */
	private ArrayList<User> mAllUsers;
	
	/**
	 * 留言板模块
	 */
	private HashMap<String, ArrayList<UserRecord>> mAllUserRecords;
	
	
	
	private Handler timeHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.activity_main);
		
		mContext = MainActivity.this;
		
//		initData();
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}
	
	private void initData() {
		Log.i(TAG, "initData-->>");
		test();
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
				Intent intent = new Intent(mContext, AddFriendActivity.class);
				startActivity(intent);
			}
		});
		
		mMyFriendWord = (TextView)findViewById(R.id.myfriend_word);
		mMyFamilyBoardWord = (TextView) findViewById(R.id.my_messageborad_word);
		
		setFont(mMyFriendWord, "fonts/oop.TTF");
		setFont(mMyFamilyBoardWord, "fonts/oop.TTF");
	}

	/**
	 * 请求用户数据
	 */
	private void test() {

		// TODO Auto-generated method stub
		UserDao userDao = UserDao.getInstance(mContext);
//		for (int i = 0; i < 5; i++) {
//			User user = new User("" + i, "adjhk" + i, null, null, "男", null, null, null, null);
//			userDao.addUser(user);
//		}
		mAllUsers = userDao.getAllUsers();
		
		
		RecordDao recordDao = RecordDao.getInstance(mContext);
//		for (int i = 0; i < 5; i++) {
//			UserRecord record = new UserRecord("" + i, null, "0", getResources().getString(R.string.test),
//					null, null, null, null, 0 ,0, null, null, 0, 0);
//			recordDao.addRecord(record);
//		}
		mAllUserRecords = new HashMap<String, ArrayList<UserRecord>>();
		for (int i = 0; i < mAllUsers.size(); i++) {
			mAllUserRecords.put(mAllUsers.get(i).getOpenId(), recordDao.getAllUserRecord(mAllUsers.get(i).getOpenId()));
		}
		
		Log.i(TAG, "users:" + mAllUsers);
		for (int i = 0; i < mAllUserRecords.size(); i++) {
			Log.i(TAG, "UserRecord:" + mAllUserRecords.get("" + i));
		}
	}
	
	/**
	 * 检测事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(TAG, "action:" + event.getAction());
		return super.onTouchEvent(event);
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
	
	public void setFont(TextView tv, String fontpath) {
		try {
			Typeface typeFace = Typeface.createFromAsset(getAssets(), fontpath);
			tv.setTypeface(typeFace);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
