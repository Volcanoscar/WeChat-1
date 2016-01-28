package com.tcl.wechat.model;

import com.tcl.wechat.action.audiorecorder.Recorder;




/**
 * 上传服务器文件信息
 * @author rex.lei
 *
 */
public class WeixinMsgInfo {
	
	private String messageid;
	private String tousername;
	private String fromusername;
	private String msgtype;
	private String mediaid;
	private Recorder recorder;

	public WeixinMsgInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WeixinMsgInfo(String messageid, String tousername,
			String fromusername, String msgtype, String mediaid,
			Recorder recorder) {
		super();
		this.messageid = messageid;
		this.tousername = tousername;
		this.fromusername = fromusername;
		this.msgtype = msgtype;
		this.mediaid = mediaid;
		this.recorder = recorder;
	}

	public String getMessageid() {
		return messageid;
	}

	public void setMessageid(String messageid) {
		this.messageid = messageid;
	}

	public String getTousername() {
		return tousername;
	}

	public void setTousername(String tousername) {
		this.tousername = tousername;
	}

	public String getFromusername() {
		return fromusername;
	}

	public void setFromusername(String fromusername) {
		this.fromusername = fromusername;
	}

	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	public String getMediaid() {
		return mediaid;
	}

	public void setMediaid(String mediaid) {
		this.mediaid = mediaid;
	}

	public Recorder getRecorder() {
		return recorder;
	}

	public void setRecorder(Recorder recorder) {
		this.recorder = recorder;
	}

	@Override
	public String toString() {
		return "WeixinMsgInfo [messageid=" + messageid + ", tousername="
				+ tousername + ", fromusername=" + fromusername + ", msgtype="
				+ msgtype + ", mediaid=" + mediaid + ", recorder=" + recorder
				+ "]";
	}
}
