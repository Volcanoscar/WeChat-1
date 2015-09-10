package com.tcl.wechat.view.listener;

/**
 * 修改用户信息事件
 * @author rex.lei
 *
 */
public interface UserInfoEditListener {

	/**
	 * 删除用户事件
	 */
	public void onDeleteUserEvent();
	
	/**
	 * 修改用户名称事件(设置备注)
	 */
	public void onEditUserNameEvent();
}
