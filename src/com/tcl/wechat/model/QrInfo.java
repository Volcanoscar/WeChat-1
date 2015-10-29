package com.tcl.wechat.model;

/**
 * 二维码信息
 * @author rex.lei
 *
 */
public class QrInfo {

	private String url;
	private String uuid;
	
	public QrInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QrInfo(String url, String uuid) {
		super();
		this.url = url;
		this.uuid = uuid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return "QrInfo [url=" + url + ", uuid=" + uuid + "]";
	}
}
