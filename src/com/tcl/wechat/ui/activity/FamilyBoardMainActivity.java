
package com.tcl.wechat.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.controller.OnLineStatusMonitor;
import com.tcl.wechat.controller.WeiXinMsgManager;
import com.tcl.wechat.controller.listener.BindListener;
import com.tcl.wechat.controller.listener.NewMessageListener;
import com.tcl.wechat.controller.listener.OnLineChanagedListener;
import com.tcl.wechat.database.WeiQrDao;
import com.tcl.wechat.database.WeiUserDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.OnLineStatus;
import com.tcl.wechat.model.WeiXinMsgRecorder;
import com.tcl.wechat.utils.ToastUtil;
import com.tcl.wechat.view.MsgBoardGroupView;
import com.tcl.wechat.view.MyFriendGroupView;
import com.tcl.wechat.view.UserInfoView;
import com.tcl.wechat.view.listener.UserIconClickListener;
import com.tcl.wechat.xmpp.WeiXmppManager;

/**
 * 留言板主界面
 * @author rex.lei
 *
 */
public class FamilyBoardMainActivity extends Activity implements IConstant, OnGestureListener{
	
	private static final String TAG = "FamilyBoardMainActivity";
	
	private static final int MSG_UPDATE_SYSUSER = 0x01;
	private static final int MSG_UPDATE_MSGBORAD = 0x02;
	
	
	private Context mContext;
	
	private UserInfoView mSystemUserInfo;
	private UserInfoView mAddFriend;
	private TextView mMyFriendWord;
	private TextView mMyFamilyBoardWord;
	
	private HorizontalScrollView mFriendHorizontalSV;
	private HorizontalScrollView mMsgBoardHorizontalSV;
	private MyFriendGroupView mFriendGroupView;
	private MsgBoardGroupView mMsgBoardGroupView;
	
	/**
	 * 系统用户
	 */
	private BindUser mSystemUser;
	
	private WeiXinMsgManager mWeiXinMsgManager;
	
	private boolean bConnected = false;
	
	/**
	 * 输入法管理类
	 */
	private InputMethodManager mInputMethodManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_familyboard);
		
		mContext = this;
		mWeiXinMsgManager = WeiXinMsgManager.getInstance();
		mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		initData();
		initView();
		initEvent();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "onResume-->>");
		
		bConnected = true;
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (bConnected) {
					Log.d(TAG, "isConnected:" + (WeiXmppManager.getInstance().getConnection() == null ? "NullPoninter" : 
						WeiXmppManager.getInstance().getConnection().isConnected()));
					try {
						Thread.sleep(10 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		
		mSystemUser = WeiUserDao.getInstance().getSystemUser();
		
		//通知更新AppWidget
		Intent intent = new Intent();
		intent.setAction(CommandAction.ACTION_MSG_UPDATE);
		WeApplication.getContext().sendBroadcast(intent);
		
		//注册广播事件
		registerBroadcast();
	}
	
	/**
	 * 初始化监听事件
	 */
	private void initEvent() {
		// TODO Auto-generated method stub
		mWeiXinMsgManager.initAllListener();
		
		mWeiXinMsgManager.addNewMessageListener(mNewMsgListener);
		mWeiXinMsgManager.addBindListener(mBindListener);
		mWeiXinMsgManager.addOnLineStatusListener(mOnLineChanagedListener);
		
	}
	
	/**
	 * 加载View
	 */
	private void initView() {
		mSystemUserInfo = (UserInfoView) findViewById(R.id.uv_system_user_info);
		mAddFriend = (UserInfoView) findViewById(R.id.uv_add_fiend);
		mAddFriend.setUserName("");
		
		mSystemUserInfo.setUserIconClickListener(new UserIconClickListener() {
			
			@Override
			public void onClick(View view) {
				if (mSystemUser != null){
					Intent intent = new Intent(mContext, PersonalInfoActivity.class);
					startActivity(intent);
				}
			}
		});
		
		mAddFriend.setUserIconClickListener(new UserIconClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(WeiQrDao.getInstance().getQrUrl())){
					return;
				}
				Intent intent = new Intent(mContext, AddFriendActivity.class);
				startActivity(intent);
			}
		});
		
		/**
		 * 好友列表布局
		 */
		mFriendGroupView = (MyFriendGroupView) findViewById(R.id.friendgroup);
		
		/**
		 * 留言板布局
		 */
		mMyFriendWord = (TextView)findViewById(R.id.myfriend_word);
		mMyFamilyBoardWord = (TextView) findViewById(R.id.my_messageborad_word);
		mMsgBoardGroupView = (MsgBoardGroupView) findViewById(R.id.msgboard_group);
		
		/**
		 * 滑动监听
		 */
		mFriendHorizontalSV = (HorizontalScrollView) findViewById(R.id.horizontalSV);
		mFriendGroupView.setScrollView(mFriendHorizontalSV);
		mMsgBoardHorizontalSV = (HorizontalScrollView) findViewById(R.id.msgBoardhorizontalSV);
		mMsgBoardGroupView.setScrollView(mMsgBoardHorizontalSV);
		
		//通知更新用户信息和留言板
		mHandler.sendEmptyMessage(MSG_UPDATE_SYSUSER);
		mHandler.sendEmptyMessage(MSG_UPDATE_MSGBORAD);
		
		//字体
		setFont(mMyFriendWord, "fonts/oop.TTF");
		setFont(mMyFamilyBoardWord, "fonts/oop.TTF");
	}
	
	
	/**
	 * 注册广播
	 */
	private void registerBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(CommandAction.ACTION_UPDATE_SYSTEMUSER);
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
			if (CommandAction.ACTION_UPDATE_SYSTEMUSER.equals(action)){
				updateSystemUser();
			}  
		}
	};
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_SYSUSER:
				updateSystemUser();
				break;
				
			case MSG_UPDATE_MSGBORAD:
				if (mMsgBoardGroupView != null) {
					mMsgBoardGroupView.loadMsgBoardData();
				}
				break;

			default:
				break;
			}
		};
	};
	
	
	private OnLineChanagedListener mOnLineChanagedListener = new OnLineChanagedListener() {
		
		@Override
		public void onStatusChanged(String openId, boolean bOnLine) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onStatusChanged opendId:" + openId + ", bOnLine:" + bOnLine);
			BindUser bindUser = WeiUserDao.getInstance().getUser(openId);
			if (bindUser != null){
				mFriendGroupView.onUserOlineStatusChanged(bindUser, bOnLine);
			}
		}
	};
	
	/**
	 * 新消息监听器
	 */
	private NewMessageListener mNewMsgListener = new NewMessageListener(){

		@Override
		public void onNewMessage(WeiXinMsgRecorder recorder) {
			// TODO Auto-generated method stub
			if (mFriendGroupView == null || recorder == null){
				return ;
			}
			
			BindUser bindUser = WeiUserDao.getInstance().getUser(recorder.getOpenid());
			if (bindUser == null){
				return ;
			}
			
			//收到删除消息后，如果数据库中没有改用户的消息记录，则messageid 为 "-1"
			if ("-1".equals(recorder.getMsgid())){
				mMsgBoardGroupView.removeRecorder(bindUser);
				return ;
			}
			
			//通知接收消息
			mMsgBoardGroupView.receiveNewMessage(recorder);
			Log.i(TAG, "bindUser status:" + bindUser.getStatus());
			if (!"true".equals(bindUser.getStatus())){// false or null
				//如果当前用户状态为不在线，则要开始监听(在线的用户，在进入主应用时，已经开始监听所有在线用户了)
				OnLineStatus status = new OnLineStatus(recorder.getOpenid(), recorder.getCreatetime());
				OnLineStatusMonitor.getInstance().startMonitor(status);
				mFriendGroupView.onUserOlineStatusChanged(bindUser, true);
			} 
		}
	};
	
	/**
	 * 绑定解绑监听器
	 */
	private BindListener mBindListener = new BindListener() {
		
		@Override
		public void onUnbind(final String openId) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onUnbind openid:" + openId );
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					BindUser unBindUser = WeiUserDao.getInstance().getUser(openId);
					if (unBindUser != null){
						mFriendGroupView.removeUser(unBindUser);
						mMsgBoardGroupView.removeRecorder(unBindUser);
						ToastUtil.showToastForced(String.format(getResources().getString(R.string.user_unbind), 
								unBindUser.getRemarkName()));
					}
				}
			});
		}
		
		@Override
		public void onBind(final String openId, int errorCode) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onBind openid:" + openId );
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					BindUser newUser = WeiUserDao.getInstance().getUser(openId);
					if (newUser != null){
						mFriendGroupView.addUser(newUser);
						ToastUtil.showToastForced(String.format(getResources().getString(R.string.user_bind), 
								newUser.getNickName()));
					}
				}
			});
		}
	};
	
	/**
	 * 更新系统用户
	 */
	private void updateSystemUser(){
		
		Log.d(TAG, "update user info");
		if (mSystemUser != null){
			String remarkName = WeiUserDao.getInstance().getSystemUser().getRemarkName();
			if (!TextUtils.isEmpty(remarkName)){
				mSystemUserInfo.setUserName(remarkName);
			} else {
				mSystemUserInfo.setUserName(mSystemUser.getNickName());
				WeiUserDao.getInstance().updateRemarkName(mSystemUser.getOpenId(), 
						mSystemUser.getNickName());
			}
			
			String headImageUrl = mSystemUser.getHeadImageUrl();
			if (!TextUtils.isEmpty(headImageUrl)){
				mSystemUserInfo.setUserIcon(headImageUrl, true);
			} 
		}
	}
	
	@Override  
	public boolean onTouchEvent(MotionEvent event) {  
	  // TODO Auto-generated method stub  
		if(event.getAction() == MotionEvent.ACTION_DOWN){  
			if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){  
				mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 
						InputMethodManager.HIDE_NOT_ALWAYS);  
			}  
		} 
		
		
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			
			break;
			
		case MotionEvent.ACTION_MOVE:
			float x = event.getX();
			float y = event.getY();
			Log.i(TAG, "[x:" + x + ",y:" + y + "]");
			break;
			
		case MotionEvent.ACTION_UP:
			break;
			

		default:
			break;
		}
		return super.onTouchEvent(event);  
	}  
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(TAG, "onPause-->>");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i(TAG, "onStop-->>");
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		bConnected = false;
		unregisterReceiver(receiver);
		if (mWeiXinMsgManager != null){
			mWeiXinMsgManager.removeNewMessageListener(mNewMsgListener);
		}
		OnLineStatusMonitor.getInstance().stopAllMonitor();
		WeiXinMsgManager.getInstance().removeBindListener(mBindListener);
		WeiXinMsgManager.getInstance().removeNewMessageListener(mNewMsgListener);
	}
	
	public void setFont(TextView tv, String fontpath) {
		try {
			Typeface typeFace = Typeface.createFromAsset(getAssets(), fontpath);
			tv.setTypeface(typeFace);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		Log.i(TAG, "distanceX:" + distanceX + ", distanceY" + distanceY);
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
