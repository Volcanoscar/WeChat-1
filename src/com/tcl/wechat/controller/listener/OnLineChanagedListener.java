package com.tcl.wechat.controller.listener;


/**
 * 用户在线状态监听器
 * @author rex.lei
 *
 */
public interface OnLineChanagedListener {

	/**
	 * 用户在线状态发生变化
	 * @param openId
	 * @param bOnLine
	 */
	public void onStatusChanged(String openId, boolean bOnLine);
}
