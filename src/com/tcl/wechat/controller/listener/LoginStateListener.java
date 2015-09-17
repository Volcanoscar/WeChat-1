package com.tcl.wechat.controller.listener;

/**
 * 登录状态监听器
 * @author rex.lei
 *
 */
public interface LoginStateListener {

	public static class LoginState{
		public static final int GET_BINDUSER_SUCCESS = 0x01;
		public static final int GET_BINDUSER_TIMEOUT = 0x02;
		public static final int NO_BINDUSER 		 = 0x03;
	}
	
	/**
	 * 登录成功
	 */
	public void onLoginSuccess();
	
	/**
	 * 登录失败
	 * @param errorCode 错误码
	 */
	public void onLoginFailed(int errorCode);
	
}
