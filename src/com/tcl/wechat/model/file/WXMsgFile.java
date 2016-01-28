package com.tcl.wechat.model.file;

public class WXMsgFile{
	
	private String type;
	private String name;
	private long size;
	private String md5;
	
	public WXMsgFile() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WXMsgFile(String type, String name, long size, String md5) {
		super();
		this.type = type;
		this.name = name;
		this.size = size;
		this.md5 = md5;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	@Override
	public String toString() {
		return "WXMsgFile [type=" + type + ", name=" + name + ", size=" + size
				+ ", md5=" + md5 + "]";
	}
}