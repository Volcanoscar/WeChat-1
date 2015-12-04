package com.tcl.wechat.controller;

import com.tcl.wechat.view.listener.UserInfoEditListener;

/**
 * ACtivity管理类，包括处理组件之间的通信
 * @author rex.lei
 *
 */
public class ActivityManager {
	
	private static ActivityManager mInstance;
	
	/**
	 * 用户信息 编辑监听器
	 */
	private UserInfoEditListener mUserInfoEditListener;
	

	private ActivityManager() {
		super();
	}

	public static ActivityManager getInstance(){
		if (mInstance == null){
			mInstance = new ActivityManager();
		}
		return mInstance;
	}
	
	public static void releaseInstance(){
		mInstance.setUserInfoEditListener(null);
		mInstance = null;
	}

	public UserInfoEditListener getUserInfoEditListener() {
		return mUserInfoEditListener;
	}

	public void setUserInfoEditListener(UserInfoEditListener listener) {
		this.mUserInfoEditListener = listener;
	}
}
