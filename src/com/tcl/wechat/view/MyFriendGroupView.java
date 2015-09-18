package com.tcl.wechat.view;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.tcl.wechat.R;
import com.tcl.wechat.controller.ActivityManager;
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.data.DataFileTools;
import com.tcl.wechat.ui.activity.DelUserActivity;
import com.tcl.wechat.utils.DensityUtil;
import com.tcl.wechat.view.listener.UserInfoEditListener;
import com.tcl.wechat.xmpp.WeiXmppManager;

/**
 * 我的好友视图类
 * @author rex.lei
 *
 */
@SuppressLint("InflateParams") 
public class MyFriendGroupView extends LinearLayout {

	private static final String TAG = MyFriendGroupView.class.getSimpleName();
	
	private Context mContext;
	
	private LayoutInflater mInflater;
	
	private ArrayList<BindUser> mAllBindUsers;
	
	private ArrayList<UserInfoView> mAllUserViews;
	
	private DataFileTools mDataFileTools;
	
	private final int USER_TAB_WIDTH = 200;

	private final int USER_TAB_HEIGHT = 200;
	
	/**
	 * 轨迹，要根据移动的leftMargn来确定
	 */
	private int[] track = new int[]{95, 110, 140, 90, 60};
	
	private HorizontalScrollView mHorizontalScrollView = null;
	
	private ActivityManager mActivityManager;
	
	
	public MyFriendGroupView(Context context) {
		this(context, null);
	}

	public MyFriendGroupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context){
		mContext = context;
		mDataFileTools = DataFileTools.getInstance();
        mInflater = LayoutInflater.from(context);
        mAllUserViews = new ArrayList<UserInfoView>();
        mAllBindUsers = new ArrayList<BindUser>();
        
        mActivityManager = ActivityManager.getInstance();
        
        mActivityManager.setUserInfoEditListener(mEditListener);
	}
	
	public void setData(ArrayList<BindUser> users) {
		if (users == null || users.isEmpty()){
			return ;
		}
		
		mAllBindUsers = users;
		Log.i(TAG, "mAllBindUsers:" + mAllBindUsers.toString());
		
		for (int i = 0; i < mAllBindUsers.size(); i++) {
			BindUser bindUser = mAllBindUsers.get(i);
			addUserColumn(bindUser, i);
		}
		
		if (mHorizontalScrollView != null){
			mHorizontalScrollView.invalidate();
		}
	}

	private void updateUser(UserInfoView userInfoView, BindUser bindUser){
		if (bindUser == null){
			return ;
		}
		
		if (bindUser.getHeadImageUrl() != null){
			if (bindUser.getOpenId().equals(userInfoView.getTag())){
				Bitmap userIcon = mDataFileTools.getBindUserIcon(bindUser.getHeadImageUrl());
				userInfoView.setUserIcon(userIcon, true);
			}
		}
		if (bindUser.getRemarkName() != null){
			userInfoView.setUserName(bindUser.getRemarkName());
		} else {
			userInfoView.setUserName(bindUser.getNickName());
			WeiUserDao.getInstance().updateRemarkName(bindUser.getOpenId(), 
					bindUser.getNickName());
		}
	}
	
	private void updateAllUser(){
		Log.i(TAG, "updateAllUser");
		//更新界面
		int size = mAllUserViews.size();
		for (int i = 0; i < size; i++) {
			UserInfoView uv = mAllUserViews.get(i);
			uv.invalidate();
		}
	}
	
	
	private void addUserColumn(BindUser bindUser, int position){
		if (bindUser == null){
			return;
		}
		int width = DensityUtil.dip2px(mContext,USER_TAB_WIDTH);
        int height = DensityUtil.dip2px(mContext, USER_TAB_HEIGHT);
        
        
        View view = setUpView(bindUser, position + 1);
        attachChildViewToParent(view, position, width, height, 100 , getTopMargin(position));
	}
	
	private View setUpView(BindUser bindUser, int index) {
		if (bindUser == null){
			return null;
		}
		View childView = mInflater.inflate(R.layout.layout_friend_view, null);
		UserInfoView userInfoView = (UserInfoView) childView.findViewById(R.id.user_info);
		userInfoView.setUserIconEditable(true);
		userInfoView.setTag(bindUser.getOpenId());
		
		//添加好友编辑监听
		userInfoView.setUserInfoEditListener(mEditListener);
		
		//添加视图列表
		mAllUserViews.add(userInfoView);
		
		updateUser(userInfoView, bindUser);
		
		return childView;
	}
	
	private void attachChildViewToParent(View view, int position, int width, int height, 
			int leftMargin, int topMargin) {
		Log.i(TAG, "leftMargin:" + leftMargin + ", topMargin:" + topMargin);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,
                height);
		params.leftMargin = leftMargin;
		params.topMargin = topMargin;
        
		addView(view, position, params);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		Log.i(TAG, "width:" + widthMeasureSpec + ", height:" + heightMeasureSpec);
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
		
		return track[index % 5];
	}
	
	public void setScrollView(HorizontalScrollView scrollView){
        mHorizontalScrollView = scrollView;
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
			Intent intent = new Intent(mContext, DelUserActivity.class);
			intent.putExtra("bindUser", bindUser);
			mContext.startActivity(intent);
		}

		@Override
		public void onCancleEditUser() {
			//更新UI状态
			updateAllUser();
		}

		@Override
		public void onConfirmEditUser(BindUser user) {
			// TODO Auto-generated method stub
			//1、发送删除用户请求
			WeiXmppManager manager = WeiXmppManager.getInstance();
			manager.unbind(user);
			
			//2、等待时间，完成后。
			
			
			//3、更新UI状态. TODO 不用再次更新，删除用户完成后，更新。
//			updateAllUser();
		}
		
	};
	
}

