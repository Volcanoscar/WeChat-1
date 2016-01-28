package com.tcl.wechat.model.file;

import com.tcl.wechat.model.IData.IData;

public class MusicFileInfo implements IData{

	private String device_id;
	private String device_type;
	private String user;
	private int msg_id;
	private String msg_type;
	private MusicService services;
	
	public MusicFileInfo() {
		super();
	}

	public MusicFileInfo(String device_id, String device_type, String user,
			int msg_id, String msg_type, MusicService services) {
		super();
		this.device_id = device_id;
		this.device_type = device_type;
		this.user = user;
		this.msg_id = msg_id;
		this.msg_type = msg_type;
		this.services = services;
	}

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getDevice_type() {
		return device_type;
	}

	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getMsg_id() {
		return msg_id;
	}

	public void setMsg_id(int msg_id) {
		this.msg_id = msg_id;
	}

	public String getMsg_type() {
		return msg_type;
	}

	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}

	public MusicService getServices() {
		return services;
	}

	public void setServices(MusicService services) {
		this.services = services;
	}

	@Override
	public String toString() {
		return "MusicFileInfo [device_id=" + device_id + ", device_type="
				+ device_type + ", user=" + user + ", msg_id=" + msg_id
				+ ", msg_type=" + msg_type + ", services=" + services + "]";
	}
}
