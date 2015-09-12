package com.tcl.wechat.test;

import java.util.ArrayList;

import com.tcl.wechat.modle.IData.IData;

/**
 * 用户列表，用于解析Json数据
 * @author rex.lei
 *
 */
public class UserList implements IData{
	
	private String ret;
	private String retInfo;
	private String version;
	private ArrayList<User> userList;
	
	public UserList(String ret, String retInfo, String version,
			ArrayList<User> userList) {
		this.ret = ret;
		this.retInfo = retInfo;
		this.version = version;
		this.userList = userList;
	}

	public String getRet() {
		return ret;
	}

	public void setRet(String ret) {
		this.ret = ret;
	}

	public String getRetInfo() {
		return retInfo;
	}

	public void setRetInfo(String retInfo) {
		this.retInfo = retInfo;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ArrayList<User> getUserList() {
		return userList;
	}

	public void setUserList(ArrayList<User> userList) {
		this.userList = userList;
	}

	@Override
	public String toString() {
		return "UserList [ret=" + ret + ", retInfo=" + retInfo + ", version="
				+ version + ", userList=" + userList + "]";
	}
}
