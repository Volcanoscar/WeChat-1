package com.tcl.wechat.modle;

/**
 * 用户角色类
 * @author rex.lei
 *
 */
public class User {

	private int userId;			//用户编号
	private String userName;	//用户账号
	private String nickName;	//用户昵称
	private String reMarkName;	//备注名称
	private String userIconUrl; //用户图像对应的URL
	
	/**
	 * 类型参数：
	 * 0：男 
	 * 1：女
	 */
	private boolean userSex;	 //用户性别
	private String signatureInfo;//签名信息
	
	public User(int userId, String userName, String nickName,
			String reMarkName, String userIconUrl, boolean userSex,
			String signatureInfo) {
		this.userId = userId;
		this.userName = userName;
		this.nickName = nickName;
		this.reMarkName = reMarkName;
		this.userIconUrl = userIconUrl;
		this.userSex = userSex;
		this.signatureInfo = signatureInfo;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getReMarkName() {
		return reMarkName;
	}

	public void setReMarkName(String reMarkName) {
		this.reMarkName = reMarkName;
	}

	public String getUserIconUrl() {
		return userIconUrl;
	}

	public void setUserIconUrl(String userIconUrl) {
		this.userIconUrl = userIconUrl;
	}

	public boolean isUserSex() {
		return userSex;
	}

	public void setUserSex(boolean userSex) {
		this.userSex = userSex;
	}

	public String getSignatureInfo() {
		return signatureInfo;
	}

	public void setSignatureInfo(String signatureInfo) {
		this.signatureInfo = signatureInfo;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userName=" + userName
				+ ", nickName=" + nickName + ", reMarkName=" + reMarkName
				+ ", userIconUrl=" + userIconUrl + ", userSex=" + userSex
				+ ", signatureInfo=" + signatureInfo + "]";
	}
}
