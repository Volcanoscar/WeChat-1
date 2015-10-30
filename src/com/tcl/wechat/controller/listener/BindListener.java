package com.tcl.wechat.controller.listener;

/**
 * 用户绑定 解绑监听器
 * @author rex.lei
 *
 */
public interface BindListener {
	
	/**
	 * 绑定新用户
	 * @param openId
	 * @param errorCode
	 */
	public void onBind(String openId, int errorCode);
	
	/**
	 * 解绑用户
	 * @param openId
	 */
	public void onUnbind(String openId);
}
