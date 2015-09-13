package com.tcl.wechat.controller;

/**
 * 登录状态监听器
 * @author rex.lei
 *
 */
public interface LoginStateListener {

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
