package com.tcl.wechat.view.listener;

import com.tcl.wechat.modle.BindUser;


/**
 * 修改用户信息事件
 * @author rex.lei
 *
 */
public interface UserInfoEditListener {

	/**
	 * 删除用户事件
	 */
	public void onDeleteUserEvent(String eventTag);
	
	/**
	 * 修改用户名称事件(设置备注)
	 */
	public void onEditUserNameEvent();
	
	/**
	 * 取消编辑用户操作
	 */
	public void onCancleEditUser();
	
	/**
	 * 确定编辑用户操作
	 */
	public void onConfirmEditUser(BindUser bindUser);
}
