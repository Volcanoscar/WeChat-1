package com.tcl.wechat.db.interf;

import java.util.ArrayList;

import com.tcl.wechat.modle.User;

public interface UserImpl {

	/**
	 * 获取所有用户
	 * @return 所用用户列表
	 */
	public ArrayList<User> getAllUsers();
	
	/**
	 * 添加用户
	 * @param user ：用户信息
	 * @return success true
	 * 		   failure false
	 */
	public boolean addUser(User user);
	
	/**
	 * 删除用户用户
	 * @param user ：用户信息
	 * @return success true
	 * 		   failure false
	 */
	public boolean deleteUser(User user);
	
	/**
	 * 更新用户用户
	 * @param user ：用户信息
	 * @return success true
	 * 		   failure false
	 */
	public boolean updateUser(User user);
	
	/**
	 * 修改备注名称
	 * @param userName 用户名称
	 * @param reMaekickName 备注名称
	 * @return success true
	 * 		   failure false
	 */
	public boolean updateRemakName(String userName, String reMaekickName);
}
