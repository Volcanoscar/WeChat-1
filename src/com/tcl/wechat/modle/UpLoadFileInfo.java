package com.tcl.wechat.modle;


/**
 * 上传服务器文件信息
 * @author rex.lei
 *
 */
public class UpLoadFileInfo {
	
	/**
	 * 接入令牌
	 */
	private String accesstoken;
	
	/**
	 * 文件类型
	 */
	private String type;
	
	/**
	 * 文件绝路径
	 */
	private String filePath;

	public UpLoadFileInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UpLoadFileInfo(String accesstoken, String type, String filePath) {
		super();
		this.accesstoken = accesstoken;
		this.type = type;
		this.filePath = filePath;
	}

	public String getAccesstoken() {
		return accesstoken;
	}

	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String toString() {
		return "UpLoadFileInfo [accesstoken=" + accesstoken + ", type=" + type
				+ ", filePath=" + filePath + "]";
	}
}
