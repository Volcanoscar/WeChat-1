package com.tcl.wechat.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tcl.wechat.WeApplication;
import com.tcl.wechat.common.IConstant.EventType;
import com.tcl.wechat.controller.ActivityManager;
import com.tcl.wechat.controller.WeiXinMsgManager;
import com.tcl.wechat.database.DeviceDao;
import com.tcl.wechat.database.Property;
import com.tcl.wechat.database.WeiMsgRecordDao;
import com.tcl.wechat.database.WeiUserDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.OnLineStatus;
import com.tcl.wechat.model.WeiNotice;
import com.tcl.wechat.ui.dialog.DelUserDialog;
import com.tcl.wechat.utils.DensityUtil;
import com.tcl.wechat.view.GroupScrollView.ScrollViewListener;
import com.tcl.wechat.view.listener.UserInfoEditListener;
import com.tcl.wechat.xmpp.WeiXmppCommand;
import com.tcl.wechat.xmpp.XmppEvent;
import com.tcl.wechat.xmpp.XmppEventListener;

/**
 * 我的好友视图类
 * @author rex.lei
 *
 */
@SuppressLint("InflateParams") 
public class MyFriendViewGroup extends LinearLayout{

	private static final String TAG = MyFriendViewGroup.class.getSimpleName();
	
	private Context mContext;
	
	private LayoutInflater mInflater;
	
	private LinkedList<BindUser> mAllBindUsers;
	
	private ArrayList<UserView> mAllUserViews;
	
	/**
	 * 好友视图Map类
	 */
	private HashMap<String, UserView> mAllUserViewMap;
	
	private final int USER_TAB_WIDTH = 200;

	private final int USER_TAB_HEIGHT = 200;
	
	/**
	 * 轨迹，要根据移动的topMargn来确定
	 */
	private int[] track = new int[]{110, 135, 160, 110, 75};
	
	private ActivityManager mActivityManager;
	
	private GroupScrollView mHorizontalScrollView = null;
	
	public void setScrollView(GroupScrollView scrollView){
        mHorizontalScrollView = scrollView;
        mHorizontalScrollView.setScrollViewListener(mScrollListener);
    }
	
	public MyFriendViewGroup(Context context) {
		this(context, null);
	}

	public MyFriendViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context){
		mContext = context;
        mInflater = LayoutInflater.from(context);
        mAllUserViews = new ArrayList<UserView>();
        mAllBindUsers = new LinkedList<BindUser>();
        mAllUserViewMap = new HashMap<String, UserView>();
        mActivityManager = ActivityManager.getInstance();
        
        mActivityManager.setUserInfoEditListener(mEditListener);
        
        //加载数据
        loadBindUserData();
	}
	
	private void loadBindUserData() {
		// TODO Auto-generated method stub
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				mAllBindUsers = WeiUserDao.getInstance().getBindUsers();
				return null;
			}
			
			protected void onPostExecute(Void result) {
				int size = mAllBindUsers.size();
				Log.i(TAG, "userCount:" + size);
				for (int i = 0; i < size; i++) {
					BindUser bindUser = mAllBindUsers.get(i);
					if (bindUser != null){
						addUserColumn(bindUser, i);
					}
				}
			};
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}
	
	/**
	 * 添加用户
	 * @param bindUser
	 */
	public void addUser(BindUser bindUser){
		
		removeAllView();
		mAllUserViews.clear();
		mAllUserViewMap.clear();
		mAllBindUsers.addFirst(bindUser);
		
		int size = mAllBindUsers.size();
		Log.i(TAG, "addUserSize:" + size);
		for (int i = 0; i < size; i++) {
			BindUser user = mAllBindUsers.get(i);
			if (user != null){
				addUserColumn(user, i);
			}
		}
	}
	
	/**
	 * 移除好友
	 * @param bindUser
	 */
	public void removeUser(BindUser bindUser){
		if (bindUser == null){
			return ;
		}
		
		removeAllView();
		mAllUserViews.clear();
		mAllUserViewMap.clear();
		Iterator<BindUser> iterator = mAllBindUsers.iterator();  
		while (iterator.hasNext()) {
			BindUser user = iterator.next();
			if (user != null && user.getOpenId().equals(bindUser.getOpenId())){
				iterator.remove();
				break;
			}
		}
		
		int size = mAllBindUsers.size();
		Log.i(TAG, "delUserSize:" + size);
		for (int i = 0; i < size; i++) {
			BindUser user = mAllBindUsers.get(i);
			if (user != null){
				addUserColumn(user, i);
			}
		}
	}
	
	/**
	 * 移除所有view
	 */
	private void removeAllView(){
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView =  getChildAt(i);
			if (childView != null){
				ViewGroup parent = (ViewGroup)childView.getParent();
				parent.removeView(childView);
			}
		}
		removeAllViewsInLayout();
	}
	
	/**
	 * 添加好友视图
	 * @param bindUser
	 * @param position
	 */
	private void addUserColumn(BindUser bindUser, int position){
		//非法用户（系统用户）
		if (bindUser == null || "-1".equals(bindUser.getSex())){
			return;
		}
		
		int width = DensityUtil.dip2px(mContext,USER_TAB_WIDTH);
        int height = DensityUtil.dip2px(mContext, USER_TAB_HEIGHT);
        
        View view = setUpView(bindUser, position);
        attachChildViewToParent(view, position, width, height, 100 , getTopMargin(position));
	}

	private View setUpView(BindUser bindUser, int position) {
		if (bindUser == null){
			return null;
		}
		
//		View childView = mInflater.inflate(R.layout.layout_friend_view, null);
//		UserInfoView userInfoView = (UserInfoView) childView.findViewById(R.id.user_info);
//		
//		UserView userView = new UserView(mContext);
//		
//		userInfoView.setUserIconEditable(true);
//		userInfoView.setTag(bindUser.getOpenId());
//		
//		//添加好友编辑监听
//		userInfoView.setUserInfoEditListener(mEditListener);
//		
//		//添加视图列表
//		mAllUserViews.add(userInfoView);
//		mAllUserViewMap.put(bindUser.getOpenId(), userInfoView);
//		attachUserToView(bindUser);
		
		UserView userView = new UserView(mContext);
		
		userView.getUserInfoView().setUserIconEditable(true);
		userView.getUserInfoView().setTag(bindUser.getOpenId());
		
		//添加好友编辑监听
		userView.getUserInfoView().setUserInfoEditListener(mEditListener);
		
		//添加视图列表
		mAllUserViews.add(userView);
		mAllUserViewMap.put(bindUser.getOpenId(), userView);
		attachUserToView(bindUser);
		
		
		return userView;
	}
	
	/**
	 * 初始化用户信息
	 * @param userInfoView
	 * @param bindUser
	 */
	public void attachUserToView(BindUser bindUser){
		if (bindUser == null){
			return ;
		}
		String openid = bindUser.getOpenId();
		UserInfoView userInfoView = mAllUserViewMap.get(openid).getUserInfoView();
		
		String headImageUrl = bindUser.getHeadImageUrl();
		if (!TextUtils.isEmpty(headImageUrl)){
			userInfoView.setUserIcon(headImageUrl, true);
		}
		
		if (bindUser.getRemarkName() != null){
			userInfoView.setUserName(bindUser.getRemarkName());
		} else {
			userInfoView.setUserName(bindUser.getNickName());
			WeiUserDao.getInstance().updateRemarkName(bindUser.getOpenId(), 
					bindUser.getNickName());
		}
		Log.d(TAG, "user online status:" + bindUser.getStatus());
		if ("true".equals(bindUser.getStatus())){ //在线
			userInfoView.setOnLineStatue(true);
			//用户在线状态监听器
			startMonitorUserStatus(openid);
		} else {
			//离线用户不用检测，在收到新消息时，开始检测
			userInfoView.setOnLineStatue(false);
		}
	}
	
	private void attachChildViewToParent(View view, int position, int width, int height, 
			int leftMargin, int topMargin) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,
                height);
		params.leftMargin = leftMargin;
		params.topMargin = topMargin;
        
		//添加视图
		addView(view, position, params);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	/**
	 * 获取轨迹
	 * @param leftMargin
	 * @return
	 */
	private int getTopMargin(int index){
		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		int size = track[index % 5];
		return (int) (size * dm.density);
	}
	
	
	/**
	 * 根据OpenId获取User
	 * @param openid
	 */
	private BindUser getBindUser(String openid){
		if (TextUtils.isEmpty(openid)){
			return null;
		}
		for (int i = 0; i < mAllBindUsers.size(); i++) {
			BindUser bindUser = mAllBindUsers.get(i);
			if (openid.equals(bindUser.getOpenId())){
				return bindUser;
			}
		}
		return null;
	}
	
	/**
	 * 更新用户
	 */
	public void updateAllUser(){
		Log.i(TAG, "updateAllUser");
		//更新界面
		int size = mAllUserViews.size();
		for (int i = 0; i < size; i++) {
			UserView uv = mAllUserViews.get(i);
			uv.invalidate();
		}
	}
	
	/**
	 * 用户在线状态发生改变
	 * @param bindUser
	 * @param bOnLine
	 */
	public void onUserOlineStatusChanged(BindUser bindUser, boolean bOnLine) {
		Log.d(TAG, "onUserOlineStatusChanged OnLineStatus:" + bOnLine);
		UserInfoView userView = mAllUserViewMap.get(bindUser.getOpenId()).getUserInfoView();
		if (userView == null){
			return ;
		}
		userView.setOnLineStatue(bOnLine);
		if (bOnLine){
			WeiUserDao.getInstance().updateStatus(bindUser.getOpenId(), "true");
		} else {
			WeiUserDao.getInstance().updateStatus(bindUser.getOpenId(), "false");
		}
	}
	

	/**
	 * 用户编辑监听器
	 */
	private UserInfoEditListener mEditListener = new UserInfoEditListener() {
		
		@Override
		public void onEditUserNameEvent() {
			
		}

		@Override
		public void onDeleteUserEvent(String eventTag) {
			// TODO Auto-generated method stub
			String openid = eventTag;
			BindUser bindUser = getBindUser(openid);
			UserInfoView userInfoView = mAllUserViewMap.get(openid).getUserInfoView();
			userInfoView.setDeleteStatue(true);
//			Intent intent = new Intent(mContext, DelUserActivity.class);
//			intent.putExtra("bindUser", bindUser);
//			mContext.startActivity(intent);
			DelUserDialog dialog = new DelUserDialog(mContext);
			dialog.showDailog(bindUser);
		}

		@Override
		public void onCancleEditUser() {
			//更新UI状态
			updateAllUser();
		}

		@Override
		public void onConfirmEditUser(BindUser user) {
			// TODO Auto-generated method stub
			String deviceId = DeviceDao.getInstance().getMemberId();
			String openid = user.getOpenId();
			
			Map<String, String> values = new HashMap<String, String>();
			values.put(Property.COLUMN_OPENID, openid);
			values.put(Property.COLUMN_DEVICEID, deviceId);
			new WeiXmppCommand(EventType.TYPE_UNBIND_EVENT, values, mListener).execute();
		}

	};
	
	/**
	 * 解绑用户
	 * @param openid
	 */
	private void unBindUser(String openid){
		Log.i(TAG, "send NoticeMsg to unbind User!!");
		BindUser bindUser = WeiUserDao.getInstance().getUser(openid);
		if (bindUser == null){
			return ;
		}
		/**
		 * 发送消息，通知清除用户信息
		 */
		WeiNotice weiNotice = new WeiNotice();
		weiNotice.setEvent("unbind");
		weiNotice.setOpenId(bindUser.getOpenId());
		weiNotice.setHeadImageUrl(bindUser.getHeadImageUrl());
		weiNotice.setNickName(bindUser.getNickName());
		weiNotice.setSex(bindUser.getSex());
		WeiXinMsgManager.getInstance().receiveNoticeMsg(weiNotice );
	}
	
	private XmppEventListener mListener = new XmppEventListener() {
		
		@Override
		public void onEvent(XmppEvent event) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onEvent type:" + event.getType());
			switch (event.getType()) {
			case EventType.TYPE_UNBIND_EVENT:
				final String openid = (String) event.getEventData();
				((Activity) mContext).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						unBindUser(openid);
					}
				});
				break;
				
			default:
				break;
			}
		}
	};
	
	/**
	 * 启动检测用户在线状态
	 * @param openId
	 */
	private void startMonitorUserStatus(String openId){
		OnLineStatus status = new OnLineStatus();
		status.setOpenid(openId);
		status.setTriggerTime(WeiMsgRecordDao.getInstance().getLatestRecorderTime(openId));
		WeiXinMsgManager.getInstance().notifyUserStatusChaned(status);
				
	}
	
	
	/**
	 * 以下为滑动过程中，改变view的位置
	 * 
	 *	0）0——230    135
		1）230——530  135——110
		2）530——830  110——135
		3）830——1130 135——160
		4）1130——1430 160——110
		5）1430——1730 110——75
		6）1730——1920 75——65	
	 * 
	 */
	int[] topMargin = new int[]{135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,135,
			135,135,135,135,135,135,135,135,135,135,135,134,134,134,134,134,134,134,134,134,134,134,134,133,133,133,133,133,133,133,133,133,133,133,133,132,132,132,132,132,132,132,132,132,132,132,132,131,131,131,131,131,131,131,131,131,131,131,131,130,130,130,130,130,130,130,130,130,130,130,130,129,129,129,129,129,129,129,129,129,129,129,129,128,128,128,128,128,128,128,128,128,128,128,128,127,127,127,127,127,127,127,127,127,127,127,127,126,126,126,126,126,126,126,126,126,126,126,126,125,125,125,125,125,125,125,125,125,125,125,125,124,124,124,124,124,124,124,124,124,124,124,124,123,123,123,123,123,123,123,123,123,123,123,123,122,122,122,122,122,122,122,122,122,122,122,122,121,121,121,121,121,121,121,121,121,121,121,121,120,120,120,120,120,120,120,120,120,120,120,120,119,119,119,119,119,119,119,119,119,119,119,119,118,118,118,118,118,118,118,118,118,118,118,118,117,117,117,117,117,117,117,117,117,117,117,117,116,116,116,116,116,116,116,116,116,116,116,116,115,115,115,115,115,115,115,115,115,115,115,115,114,114,114,114,114,114,114,114,114,114,114,114,113,113,113,113,113,113,113,113,113,113,113,113,112,112,112,112,112,112,112,112,112,112,112,112,111,111,111,111,111,111,111,111,111,111,111,111,110,
			110,110,110,110,110,110,110,110,110,110,110,111,111,111,111,111,111,111,111,111,111,111,111,112,112,112,112,112,112,112,112,112,112,112,112,113,113,113,113,113,113,113,113,113,113,113,113,114,114,114,114,114,114,114,114,114,114,114,114,115,115,115,115,115,115,115,115,115,115,115,115,116,116,116,116,116,116,116,116,116,116,116,116,117,117,117,117,117,117,117,117,117,117,117,117,118,118,118,118,118,118,118,118,118,118,118,118,119,119,119,119,119,119,119,119,119,119,119,119,120,120,120,120,120,120,120,120,120,120,120,120,121,121,121,121,121,121,121,121,121,121,121,121,122,122,122,122,122,122,122,122,122,122,122,122,123,123,123,123,123,123,123,123,123,123,123,123,124,124,124,124,124,124,124,124,124,124,124,124,125,125,125,125,125,125,125,125,125,125,125,125,126,126,126,126,126,126,126,126,126,126,126,126,127,127,127,127,127,127,127,127,127,127,127,127,128,128,128,128,128,128,128,128,128,128,128,128,129,129,129,129,129,129,129,129,129,129,129,129,130,130,130,130,130,130,130,130,130,130,130,130,131,131,131,131,131,131,131,131,131,131,131,131,132,132,132,132,132,132,132,132,132,132,132,132,133,133,133,133,133,133,133,133,133,133,133,133,134,134,134,134,134,134,134,134,134,134,134,134,135,
			135,135,135,135,135,135,135,135,135,135,135,136,136,136,136,136,136,136,136,136,136,136,136,137,137,137,137,137,137,137,137,137,137,137,137,138,138,138,138,138,138,138,138,138,138,138,138,139,139,139,139,139,139,139,139,139,139,139,139,140,140,140,140,140,140,140,140,140,140,140,140,141,141,141,141,141,141,141,141,141,141,141,141,142,142,142,142,142,142,142,142,142,142,142,142,143,143,143,143,143,143,143,143,143,143,143,143,144,144,144,144,144,144,144,144,144,144,144,144,145,145,145,145,145,145,145,145,145,145,145,145,146,146,146,146,146,146,146,146,146,146,146,146,147,147,147,147,147,147,147,147,147,147,147,147,148,148,148,148,148,148,148,148,148,148,148,148,149,149,149,149,149,149,149,149,149,149,149,149,150,150,150,150,150,150,150,150,150,150,150,150,151,151,151,151,151,151,151,151,151,151,151,151,152,152,152,152,152,152,152,152,152,152,152,152,153,153,153,153,153,153,153,153,153,153,153,153,154,154,154,154,154,154,154,154,154,154,154,154,155,155,155,155,155,155,155,155,155,155,155,155,156,156,156,156,156,156,156,156,156,156,156,156,157,157,157,157,157,157,157,157,157,157,157,157,158,158,158,158,158,158,158,158,158,158,158,158,159,159,159,159,159,159,159,159,159,159,159,159,160,
			160,160,160,160,160,159,159,159,159,159,159,158,158,158,158,158,158,157,157,157,157,157,157,156,156,156,156,156,156,155,155,155,155,155,155,154,154,154,154,154,154,153,153,153,153,153,153,152,152,152,152,152,152,151,151,151,151,151,151,150,150,150,150,150,150,149,149,149,149,149,149,148,148,148,148,148,148,147,147,147,147,147,147,146,146,146,146,146,146,145,145,145,145,145,145,144,144,144,144,144,144,143,143,143,143,143,143,142,142,142,142,142,142,141,141,141,141,141,141,140,140,140,140,140,140,139,139,139,139,139,139,138,138,138,138,138,138,137,137,137,137,137,137,136,136,136,136,136,136,135,135,135,135,135,135,134,134,134,134,134,134,133,133,133,133,133,133,132,132,132,132,132,132,131,131,131,131,131,131,130,130,130,130,130,130,129,129,129,129,129,129,128,128,128,128,128,128,127,127,127,127,127,127,126,126,126,126,126,126,125,125,125,125,125,125,124,124,124,124,124,124,123,123,123,123,123,123,122,122,122,122,122,122,121,121,121,121,121,121,120,120,120,120,120,120,119,119,119,119,119,119,118,118,118,118,118,118,117,117,117,117,117,117,116,116,116,116,116,116,115,115,115,115,115,115,114,114,114,114,114,114,113,113,113,113,113,113,112,112,112,112,112,112,111,111,111,111,111,111,110,
			110,110,110,110,110,110,110,110,110,110,110,109,109,109,109,109,109,109,109,109,109,109,109,108,108,108,108,108,108,108,108,108,108,108,108,107,107,107,107,107,107,107,107,107,107,107,107,106,106,106,106,106,106,106,106,106,106,106,106,105,105,105,105,105,105,105,105,105,105,105,105,104,104,104,104,104,104,104,104,104,104,104,104,103,103,103,103,103,103,103,103,103,103,103,103,102,102,102,102,102,102,102,102,102,102,102,102,101,101,101,101,101,101,101,101,101,101,101,101,100,100,100,100,100,100,100,100,100,100,100,100,99,99,99,99,99,99,99,99,99,99,99,99,98,98,98,98,98,98,98,98,98,98,98,98,97,97,97,97,97,97,97,97,97,97,97,97,96,96,96,96,96,96,96,96,96,96,96,96,95,95,95,95,95,95,95,95,95,95,95,95,94,94,94,94,94,94,94,94,94,94,94,94,93,93,93,93,93,93,93,93,93,93,93,93,92,92,92,92,92,92,92,92,92,92,92,92,91,91,91,91,91,91,91,91,91,91,91,91,90,90,90,90,90,90,90,90,90,90,90,90,89,89,89,89,89,89,89,89,89,89,89,89,88,88,88,88,88,88,88,88,88,88,88,88,87,87,87,87,87,87,87,87,87,87,87,87,86,86,86,86,86,86,86,86,86,86,86,86,85,
			75,75,75,75,75,75,75,75,75,75,75,75,75,75,75,75,75,75,74,74,74,74,74,74,74,74,74,74,74,74,74,74,74,74,74,74,74,73,73,73,73,73,73,73,73,73,73,73,73,73,73,73,73,73,73,73,72,72,72,72,72,72,72,72,72,72,72,72,72,72,72,72,72,72,72,71,71,71,71,71,71,71,71,71,71,71,71,71,71,71,71,71,71,71,70,70,70,70,70,70,70,70,70,70,70,70,70,70,70,70,70,70,70,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,69,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,68,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,67,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,66,65};
	
	private ScrollViewListener mScrollListener = new ScrollViewListener() {
		
		@Override
		public void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
			// TODO Auto-generated method stub
			Log.i(TAG, "Layout:[" + left + "," + top + "," + oldLeft + "," + oldTop + "]");
			
			/**
			 * 设计思路：根据滑动的left值，设置topMargin。然后重新layout
			 */
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				
				/**
				 * 当前控件的位置
				 */
				int[] location = new int[2];
				child.getLocationOnScreen(location);
	            int posX = location[0];
	            //int posY = location[1];
				//Log.i(TAG, "ChildIndex:" + i + "-->location:[" + posX + "," +  posY + "]");
				
				int childLeft = child.getLeft();
				int childTop = child.getTop();
				int childRight = child.getRight();
				//int childBottom = child.getBottom();

				//int width = child.getMeasuredWidth();
				int height = child.getMeasuredHeight();
			
				//int posX = (int) child.getX();
				//int posY = (int) child.getY();
				
				int topMatgin = childTop;
				if (posX > 0 && posX < 1920) {
					topMatgin = topMargin[(posX) % 1920]/*(int) (Math.sin(childLeft / 175 * 3.1415) * 225 )*/;
				} 
				
				//Log.i(TAG, "Layout:[" + childLeft + "," + childTop + "," + childRight + "," + childBottom + "]" + 
				//		"Size:[" + width + "," + height + "," + posX + "," + posY + "]");
				child.layout(childLeft, topMatgin, childRight, topMatgin + height);
			}
		}
	};
}

