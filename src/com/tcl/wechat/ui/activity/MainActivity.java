package com.tcl.wechat.ui.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.data.DataFileTools;
import com.tcl.wechat.view.MyFriendGroupView;
import com.tcl.wechat.view.UserInfoView;
import com.tcl.wechat.view.listener.UserIconClickListener;

/**
 * 主界面Activity
 * @author rex.lei
 * 
 * 	//TODO  加入检测更新机制
 *
 */
public class MainActivity extends Activity implements WeiConstant{

	private static final String TAG = "MainActivity";
	
	private static final int MSG_UPDATE_SYSTEMUSER = 0x01;//更新系统用户头像
	private static final int MSG_UPDATE_FRIENDLIST = 0x02;//更新用户列表信息
	private static final int MSG_UNBINDER_USER     = 0x03;//用户解除绑定
	private static final int MSG_BINDER_USER       = 0x04;//用户绑定
	
	
	private Context mContext;
	
	private UserInfoView mSystemUserInfo;
	private UserInfoView mAddFriend;
	private TextView mMyFriendWord;
	private TextView mMyFamilyBoardWord;
	
	private MyFriendGroupView mFriendGroupView;
	private HorizontalScrollView mHorizontalScrollView;
	
	/**
	 * 系统用户
	 */
	private BindUser mSystemUser;
	/**
	 * 绑定好友用户列表
	 */
	private ArrayList<BindUser> mAllFirendUsers;
	
	
	/**
	 * 工具类
	 */
	private DataFileTools mDataFileTools;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		mContext = MainActivity.this;
		mDataFileTools = DataFileTools.getInstance();
		
		initData();
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}
	

	/**
	 * 初始化数据
	 */
	private void initData() {
		
		registerBroadcast();
		
		//加载信息
		loadBindUserData();
		
	}
	
	/**
	 * 注册广播
	 */
	private void registerBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(CommandAction.ACTION_UPDATE_BINDUSER);
		registerReceiver(receiver, filter);
	}
	
	/**
	 * 广播接收器
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			//用户解绑或者绑定
			if (CommandAction.ACTION_UPDATE_BINDUSER.equals(action)){
				//更新用户列表
				
				//如果解绑，同时更新留言板
			} 
			
		}
	};
	
	/**
	 * 更新用户信息
	 */
	private void updateUserInfo() {
		mHandler.sendEmptyMessage(MSG_UPDATE_SYSTEMUSER);
		mHandler.sendEmptyMessage(MSG_UPDATE_FRIENDLIST);
	}
	
	/**
	 * 更新用户头像
	 */
	private void updateUserIcon(){
		
		if (mSystemUser != null){
			Bitmap userIcon = mDataFileTools.getBindUserIcon(mSystemUser.getHeadImageUrl());
			mSystemUserInfo.setUserIcon(userIcon, true);
			
			if (mSystemUser.getRemarkName() != null){
				mSystemUserInfo.setUserName(mSystemUser.getRemarkName());
			} else {
				mSystemUserInfo.setUserName(mSystemUser.getNickName());
				WeiUserDao.getInstance().updateRemarkName(mSystemUser.getOpenId(), 
						mSystemUser.getNickName());
			}
		}
	}

	/**
	 * 加载绑定用户信息
	 */
	private void loadBindUserData() {
		ArrayList<BindUser> allUsers = WeiUserDao.getInstance().getAllUsers();

		if (allUsers == null || allUsers.isEmpty()){
			Log.d(TAG, "NO BindUser!!");
			return ;
		}
		
		mSystemUser = allUsers.get(0);
		mAllFirendUsers = new ArrayList<BindUser>();
		if (allUsers.size() > 0){
			for (int i = 1; i < allUsers.size(); i++) {
				mAllFirendUsers.add(allUsers.get(i));
			}
		}
	}
	
	/**
	 * 加载View
	 */
	private void initView() {
		mSystemUserInfo = (UserInfoView) findViewById(R.id.uv_system_user_info);
		mAddFriend = (UserInfoView) findViewById(R.id.uv_add_fiend);
		
		mSystemUserInfo.setUserIconClickListener(new UserIconClickListener() {
			
			@Override
			public void onClick(View view) {
				if (mSystemUser != null){
					Intent intent = new Intent(mContext, PersonalInfoActivity.class);
					intent.putExtra("BindUser", mSystemUser);
					startActivity(intent);
				}
			}
		});
		
		mAddFriend.setUserIconClickListener(new UserIconClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, AddFriendActivity.class);
				intent.putExtra("BindUser", mSystemUser);
				startActivity(intent);
			}
		});
		
		

		/**
		 * 好友列表布局
		 */
		mFriendGroupView = (MyFriendGroupView) findViewById(R.id.friendgroup);
		mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalSV);
		mFriendGroupView.setScrollView(mHorizontalScrollView);
		if (mAllFirendUsers != null && !mAllFirendUsers.isEmpty()){
			mFriendGroupView.setData(mAllFirendUsers);
		}
		//更新用户信息
		updateUserIcon();
		
		
		/**
		 * 留言板布局
		 */
		mMyFriendWord = (TextView)findViewById(R.id.myfriend_word);
		mMyFamilyBoardWord = (TextView) findViewById(R.id.my_messageborad_word);
		
		
		//字体
		setFont(mMyFriendWord, "fonts/oop.TTF");
		setFont(mMyFamilyBoardWord, "fonts/oop.TTF");
	}

	
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Log.i(TAG, "handleMessage:" + MSG_UPDATE_SYSTEMUSER);
			switch (msg.what) {
			case MSG_UPDATE_SYSTEMUSER:
				//重新加载一次用户信息，防止在网络不通的情况下，获取用户信息失败;或者用户信息已更新
				loadBindUserData();
				updateUserIcon();
				break;
				

			default:
				break;
			}
			
		};
	};
	
	
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
