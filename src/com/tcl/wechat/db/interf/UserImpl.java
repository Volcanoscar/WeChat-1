package com.tcl.wechat.db.interf;

import java.util.ArrayList;

import com.tcl.wechat.modle.User;

public interface UserImpl {

	/**
	 * 获取用户数量
	 * @return 用户数
	 */
	public int getUserCount();
	
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
	 * @param userName ：用户名称
	 * @return success true
	 * 		   failure false
	 */
	public boolean deleteUser(String userName);
	
	/**
	 * 修改昵称
	 * @param userName 用户名称
	 * @param nickkName 昵称
	 * @return success true
	 * 		   failure false
	 */
	public boolean updateNickName(String userName, String nickkName);
	
	/**
	 * 修改备注名称
	 * @param userName 用户名称
	 * @param reMarkickName 备注名称
	 * @return success true
	 * 		   failure false
	 */
	public boolean updateRemakName(String userName, String reMarkickName);
	
	/**
	 * 更新用户签名信息
	 * @param userName 用户名称
	 * @param signatureInfo 签名信息
	 * @return success true
	 * 		   failure false
	 */
	public boolean updateSignatureInfo(String userName, String signatureInfo);
	
	/**
	 * 更新用户头像
	 * @param userName 用户名称
	 * @param headimageurl 用户头像地址
	 * @return success true
	 * 		   failure false
	 */
	public boolean updateHeadimage(String userName, String headimageurl);
}
