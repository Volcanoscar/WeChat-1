package com.tcl.wechat.model;

/**
 * 留言板数据封装类
 * @author rex.lei
 *
 */
public class RecorderInfo {

	private BindUser bindUser;
	private WeiXinMessage recorder;
	
	public RecorderInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public RecorderInfo(BindUser bindUser, WeiXinMessage recorder) {
		super();
		this.bindUser = bindUser;
		this.recorder = recorder;
	}

	public BindUser getBindUser() {
		return bindUser;
	}

	public void setBindUser(BindUser bindUser) {
		this.bindUser = bindUser;
	}

	public WeiXinMessage getRecorder() {
		return recorder;
	}

	public void setRecorder(WeiXinMessage recorder) {
		this.recorder = recorder;
	}

	@Override
	public String toString() {
		return "RecorderInfo [bindUser=" + bindUser + ", recorder=" + recorder
				+ "]";
	}
}
