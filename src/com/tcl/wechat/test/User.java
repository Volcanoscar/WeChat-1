package com.tcl.wechat.test;

import com.tcl.wechat.modle.IData.IData;

/**
 * 用户角色类
 * @author rex.lei
 *
 */
public class User implements IData{

	private String openId;			//用户编号
	private String userName;	//用户账号
	private String nickName;	//用户昵称
	private String remarkName;	//备注名称
	private String sex ;		//用户性别
	private String headimageurl;//用户头像存储地址
	private String signature;	//用户签名信息
	private String newsNum;		//消息个数
	private String status;      //用户在线状态
	
	public User() {
		super();
	}

	public User(String openId, String userName, String nickName,
			String remarkName, String sex, String headimageurl,
			String signature, String newsNum, String status) {
		super();
		this.openId = openId;
		this.userName = userName;
		this.nickName = nickName;
		this.remarkName = remarkName;
		this.sex = sex;
		this.headimageurl = headimageurl;
		this.signature = signature;
		this.newsNum = newsNum;
		this.status = status;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
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

	public String getRemarkName() {
		return remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getHeadimageurl() {
		return headimageurl;
	}

	public void setHeadimageurl(String headimageurl) {
		this.headimageurl = headimageurl;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getNewsNum() {
		return newsNum;
	}

	public void setNewsNum(String newsNum) {
		this.newsNum = newsNum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "User [openId=" + openId + ", userName=" + userName
				+ ", nickName=" + nickName + ", remarkName=" + remarkName
				+ ", sex=" + sex + ", headimageurl=" + headimageurl
				+ ", signature=" + signature + ", newsNum=" + newsNum
				+ ", status=" + status + "]";
	}
}