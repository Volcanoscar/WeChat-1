package com.tcl.wechat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.tcl.wechat.R;
import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.controller.OnLineStatusMonitor;
import com.tcl.wechat.controller.WeiXinMsgControl;
import com.tcl.wechat.controller.WeiXinNotifier;
import com.tcl.wechat.controller.listener.BindListener;
import com.tcl.wechat.controller.listener.NewMessageListener;
import com.tcl.wechat.controller.listener.OnLineChanagedListener;
import com.tcl.wechat.database.WeiQrDao;
import com.tcl.wechat.database.WeiRecordDao;
import com.tcl.wechat.database.WeiUserDao;
import com.tcl.wechat.logcat.DLog;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.OnLineStatus;
import com.tcl.wechat.model.WeiXinMessage;
import com.tcl.wechat.utils.NetWorkUtil;
import com.tcl.wechat.utils.WeixinToast;
import com.tcl.wechat.view.GroupScrollView;
import com.tcl.wechat.view.MsgBoardViewGroup;
import com.tcl.wechat.view.MyFriendViewGroup;
import com.tcl.wechat.view.UserInfoView;
import com.tcl.wechat.view.listener.UserIconClickListener;
import com.tcl.wechat.xmpp.WeiXmppManager;
import com.tcl.wechat.xmpp.WeiXmppService;

/**
 * 留言板主界面
 * 
 * @author rex.lei
 * 
 */
public class FamilyBoardMainActivity extends BaseActivity implements IConstant {

	private static final String TAG = "FamilyBoardMainActivity";

	private static final int MSG_UPDATE_SYSUSER = 0x01;
	private static final int MSG_UPDATE_MSGBORAD = 0x02;
	private static final int MSG_UPDATE_APPWIDGET = 0x03; 

	private Context mContext;

	private RelativeLayout mMainView;
	private ProgressBar mLoadingBar;
	private UserInfoView mSystemUserInfo;
	private UserInfoView mAddFriend;
	private ImageView mWifiStateImg;

	private GroupScrollView mFriendHorizontalSV;
	private GroupScrollView mMsgBoardHorizontalSV;
	private MyFriendViewGroup mFriendViewGroup;
	private MsgBoardViewGroup mMsgBoardGroupView;
	
	private Thread initThread = null;
	
	/**
	 * 系统用户
	 */
	private BindUser mSystemUser;

	private WeiXinMsgControl mWeiXinMsgManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_familyboard);

		mContext = this;
		mWeiXinMsgManager = WeiXinMsgControl.getInstance();
		
		initData();
		initView();
		initEvent();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private Runnable initRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			startLogin();
		}
	};
	
	/**
	 * 开始登陆
	 */
	public void startLogin() {
		
		Log.d(TAG, "start Login...");
		if (WeiXmppManager.getInstance().isRegister()){//用户已经注册
			//长连接已存在，并且已经注册
			if (!WeiXmppManager.getInstance().isConnected()) {
				WeiXmppManager.getInstance().login();
			} 
		}  else {
			//未注册用户，则启动服务开始注册
			Intent serviceIntent = new Intent(FamilyBoardMainActivity.this, 
					WeiXmppService.class);
			startService(serviceIntent);
		}
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		
		if (!NetWorkUtil.isNetworkAvailable()) {
			WeixinToast.makeText(R.string.network_not_available).show();
		}
		
		if( initThread != null){
 			initThread.interrupt();
 			initThread = null;
 		}
		initThread = new Thread(initRun);
		initThread.start();

		// 注册广播事件
		registerBroadcast();
	}

	/**
	 * 初始化监听事件
	 */
	private void initEvent() {
		mWeiXinMsgManager.initAllListener();

		mWeiXinMsgManager.addNewMessageListener(mNewMsgListener);
		mWeiXinMsgManager.addBindListener(mBindListener);
		mWeiXinMsgManager.addOnLineStatusListener(mOnLineChanagedListener);
	}

	/**
	 * 加载View
	 */
	private void initView() {
		mLoadingBar = (ProgressBar) findViewById(R.id.progress_loading);
		mMainView = (RelativeLayout) findViewById(R.id.layout_main_contentview);
		
		mSystemUserInfo = (UserInfoView) findViewById(R.id.uv_system_user_info);
		mAddFriend = (UserInfoView) findViewById(R.id.uv_add_fiend);
		mAddFriend.setUserName("");
		
		mSystemUserInfo.setFocusable(false);
		mSystemUserInfo.setFocusableInTouchMode(false);
		mSystemUserInfo.setUserIconClickListener(new UserIconClickListener() {

			@Override
			public void onClick(View view) {
				if (mSystemUser != null) {
					Intent intent = new Intent(mContext,
							PersonalInfoActivity.class);
					startActivity(intent);
				}
			}
		});

		mAddFriend.setUserIconClickListener(new UserIconClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(WeiQrDao.getInstance().getQrUrl())) {
					return;
				}
				Intent intent = new Intent(mContext, AddFriendActivity.class);
				startActivity(intent);
			}
		});
		
		/**
		 * 好友列表布局
		 */
		mFriendViewGroup = (MyFriendViewGroup) findViewById(R.id.friendgroup);

		/**
		 * 留言板布局
		 */
		mMsgBoardGroupView = (MsgBoardViewGroup) findViewById(R.id.msgboard_group);

		/**
		 * 滑动监听
		 */
		mFriendHorizontalSV = (GroupScrollView) findViewById(R.id.horizontalSV);
		mFriendViewGroup.setScrollView(mFriendHorizontalSV);
		mMsgBoardHorizontalSV = (GroupScrollView) findViewById(R.id.msgBoardhorizontalSV);
		mMsgBoardGroupView.setScrollView(mMsgBoardHorizontalSV);

		mWifiStateImg = (ImageView) findViewById(R.id.img_wifi_signal);
		
		mLoadingBar.setVisibility(View.VISIBLE);
		mMainView.setVisibility(View.GONE);
		// 通知更新用户信息和留言板
		//mHandler.sendEmptyMessage(MSG_UPDATE_SYSUSER);
		
		//加载主界面数据
		mHandler.sendEmptyMessage(MSG_UPDATE_MSGBORAD);

		// 通知更新AppWidget
		mHandler.sendEmptyMessage(MSG_UPDATE_APPWIDGET);
	}
	
	/**
	 * 注册广播
	 */
	private void registerBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(CommandAction.ACTION_UPDATE_SYSTEMUSER);
		filter.addAction(CommandAction.ACTION_UPDATE_BINDUSER);
		filter.addAction(CommandAction.ACTION_MSG_USER);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(CommandAction.ACTION_MSG_UPDATE);
		registerReceiver(receiver, filter);
	}

	/**
	 * 广播接收器
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i(TAG, "receive event, action:" + action);
			// 用户解绑或者绑定
			if (CommandAction.ACTION_UPDATE_SYSTEMUSER.equals(action)) {
				updateSystemUser();
			} else if (CommandAction.ACTION_UPDATE_BINDUSER.equals(action)) {
				updateBindUser();
			} else if (CommandAction.ACTION_MSG_USER.equals(action)) {
				updateSystemUser();
				updateBindUser();
			} else if (CommandAction.ACTION_MSG_UPDATE.equals(action)) {
				if (mNewMsgListener != null) {
					mNewMsgListener.onNewMessage(WeiRecordDao.getInstance().getLatestRecorder());
				}
			} else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
				int wifiState = intent.getIntExtra("wifi_state", 0);
				int wifiLevel = Math
						.abs(((WifiManager) getSystemService(WIFI_SERVICE))
								.getConnectionInfo().getRssi());
				DLog.d(TAG, "wifiState:" + wifiState + ",wifiLevel:"
						+ wifiLevel);
				if (wifiLevel > 100) {
					wifiLevel = 100;
				}
				updateWifiState(wifiState, wifiLevel);

			}
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_SYSUSER:
				updateSystemUser();
				break;

			case MSG_UPDATE_MSGBORAD:
				updateSystemUser();
				if (mFriendViewGroup != null){
					mFriendViewGroup.loadBindUserData();
				}
				if (mMsgBoardGroupView != null){
					mMsgBoardGroupView.loadMsgBoardData();
				}
				mMainView.setVisibility(View.VISIBLE);
				mLoadingBar.setVisibility(View.GONE);
				break;
			case MSG_UPDATE_APPWIDGET:
				//通知更新AppWidget
				Intent intent = new Intent();
				intent.setAction(CommandAction.ACTION_MSG_UPDATE);
				sendBroadcast(intent);
				
				// 通知清除通知栏
				WeiXinNotifier.getInstance().clearNotification();
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
			Log.i(TAG, "onStatusChanged opendId:" + openId + ", bOnLine:"
					+ bOnLine);
			BindUser bindUser = WeiUserDao.getInstance().getUser(openId);
			if (bindUser != null) {
				mFriendViewGroup.onUserOlineStatusChanged(bindUser, bOnLine);
			}
		}
	};

	/**
	 * 新消息监听器
	 */
	private NewMessageListener mNewMsgListener = new NewMessageListener() {

		@Override
		public void onNewMessage(WeiXinMessage recorder) {
			// TODO Auto-generated method stub
			if (mFriendViewGroup == null || recorder == null) {
				return;
			}

			BindUser bindUser = WeiUserDao.getInstance().getUser(
					recorder.getOpenid());
			if (bindUser == null) {
				return;
			}

			// 收到删除消息后，如果数据库中没有改用户的消息记录，则messageid 为 "-1"
			if ("-1".equals(recorder.getMsgid())) {
				mMsgBoardGroupView.removeRecorder(bindUser);
				return;
			}

			// 通知接收消息
			mMsgBoardGroupView.receiveNewMessage(recorder);
			Log.i(TAG, "bindUser status:" + bindUser.getStatus());
			if (!"true".equals(bindUser.getStatus())) {// false or null
				// 如果当前用户状态为不在线，则要开始监听(在线的用户，在进入主应用时，已经开始监听所有在线用户了)
				OnLineStatus status = new OnLineStatus(recorder.getOpenid(),
						recorder.getCreatetime());
				OnLineStatusMonitor.getInstance().startMonitor(status);
				mFriendViewGroup.onUserOlineStatusChanged(bindUser, true);
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
			Log.i(TAG, "onUnbind openid:" + openId);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					BindUser unBindUser = WeiUserDao.getInstance().getUser(
							openId);
					if (unBindUser != null) {
						mFriendViewGroup.removeUser(unBindUser);
						mMsgBoardGroupView.removeRecorder(unBindUser);
						WeixinToast.makeText(String.format(getResources()
								.getString(R.string.user_unbind), unBindUser
								.getRemarkName())).show();
					}
				}
			});
		}

		@Override
		public void onBind(final String openId, int errorCode) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onBind openid:" + openId);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					BindUser newUser = WeiUserDao.getInstance().getUser(openId);
					if (newUser != null) {
						mFriendViewGroup.addUser(newUser);
						WeixinToast.makeText(String.format(getResources()
								.getString(R.string.user_bind), newUser
								.getNickName())).show();
					}
				}
			});
		}
	};

	/**
	 * 更新系统用户
	 */
	private void updateSystemUser() {

		Log.d(TAG, "update user info");
		mSystemUser = WeiUserDao.getInstance().getSystemUser();
		if (mSystemUser != null && !TextUtils.isEmpty(mSystemUser.getOpenId())) {
			String remarkName = WeiUserDao.getInstance().getSystemUser()
					.getRemarkName();
			if (!TextUtils.isEmpty(remarkName)) {
				mSystemUserInfo.setUserName(remarkName);
			} else {
				mSystemUserInfo.setUserName(mSystemUser.getNickName());
				WeiUserDao.getInstance().updateRemarkName(
						mSystemUser.getOpenId(), mSystemUser.getNickName());
			}

			String headImageUrl = mSystemUser.getHeadImageUrl();
			if (!TextUtils.isEmpty(headImageUrl)) {
				mSystemUserInfo.setUserIcon(headImageUrl, true);
			}
		} 
	}

	/**
	 * 更新绑定用户列表
	 */
	private void updateBindUser() {

		if (mFriendViewGroup != null) {
			mFriendViewGroup.updateBindUser();
		}

		if (mMsgBoardGroupView != null) {
			mMsgBoardGroupView.updateBindUser();
		}
		mHandler.sendEmptyMessage(MSG_UPDATE_APPWIDGET);
	}

	/**
	 * 更新wifi信号强度
	 * 
	 * @param state
	 *            wifi状态
	 * @param level
	 *            wifi信号强度
	 */
	private void updateWifiState(int state, int level) {

		switch (state) {
		case WifiManager.WIFI_STATE_DISABLING:
			mWifiStateImg.setImageResource(R.drawable.wifi_set);
			mWifiStateImg.setImageLevel(level);
			break;
		case WifiManager.WIFI_STATE_DISABLED:
			mWifiStateImg.setImageResource(R.drawable.wifi_set);
			mWifiStateImg.setImageLevel(0);
			WeixinToast.makeText(R.string.network_not_available).show();
			break;
		case WifiManager.WIFI_STATE_ENABLING:
			mWifiStateImg.setImageResource(R.drawable.wifi_set);
			mWifiStateImg.setImageLevel(level);
			if (!WeiXmppManager.getInstance().isRegister()){
				//如果没有注册，则开始注册
				WeixinToast.makeText(R.string.register).show();
				WeiXmppManager.getInstance().register();
			} else {
				WeixinToast.makeText(R.string.network_connectting, 1000).show();
			}
			break;
		case WifiManager.WIFI_STATE_ENABLED:
			mWifiStateImg.setImageResource(R.drawable.wifi_set);
			mWifiStateImg.setImageLevel(level);
			break;
		case WifiManager.WIFI_STATE_UNKNOWN:
			mWifiStateImg.setImageResource(R.drawable.wifi_set);
			mWifiStateImg.setImageLevel(level);
			break;
		default:
			mWifiStateImg.setImageResource(R.drawable.wifi_set);
			mWifiStateImg.setImageLevel(0);
		}
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
		Log.i(TAG, "onDestroy-->>");
		Intent intent = new Intent(ACTION_START_SERVICE);
		sendBroadcast(intent);
		unregisterReceiver(receiver);
		if (mWeiXinMsgManager != null) {
			mWeiXinMsgManager.removeNewMessageListener(mNewMsgListener);
			mWeiXinMsgManager.removeNewMessageListener(mNewMsgListener);
			mWeiXinMsgManager.removeOnLineStatusListener();
			mWeiXinMsgManager = null;
		}
		OnLineStatusMonitor.getInstance().stopAllMonitor();
		OnLineStatusMonitor.releaseInstance();
		
		super.onDestroy();
	}
}
