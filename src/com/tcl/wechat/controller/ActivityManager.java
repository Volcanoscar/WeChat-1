package com.tcl.wechat.controller;

import com.tcl.wechat.view.listener.UserInfoEditListener;

/**
 * ACtivity管理类，包括处理组件之间的通信
 * @author rex.lei
 *
 */
public class ActivityManager {
	
	/**
	 * 用户信息 编辑监听器
	 */
	private UserInfoEditListener mUserInfoEditListener;
	
	private static class ActivityManagerInstance{
		private static final ActivityManager mInstance = new ActivityManager();
	}

	private ActivityManager() {
		super();
	}

	public static ActivityManager getInstance(){
		return ActivityManagerInstance.mInstance;
	}

	public UserInfoEditListener getUserInfoEditListener() {
		return mUserInfoEditListener;
	}

	public void setUserInfoEditListener(UserInfoEditListener listener) {
		this.mUserInfoEditListener = listener;
	}
}
