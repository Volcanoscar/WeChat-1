package com.tcl.wechat.model.file;

public class FileService{
	public WXMsgFile wxmsg_file;

	public FileService() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FileService(WXMsgFile wxmsg_file) {
		super();
		this.wxmsg_file = wxmsg_file;
	}

	public WXMsgFile getWxmsg_file() {
		return wxmsg_file;
	}

	public void setWxmsg_file(WXMsgFile wxmsg_file) {
		this.wxmsg_file = wxmsg_file;
	}

	@Override
	public String toString() {
		return "FileService [wxmsg_file=" + wxmsg_file + "]";
	}
}
