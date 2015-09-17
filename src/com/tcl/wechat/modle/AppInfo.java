package com.tcl.wechat.modle;

import java.io.Serializable;


/**
 * App应用信息
 * @author rex.lei
 *
 */
public class AppInfo implements Serializable {


	private static final long serialVersionUID = -2455950643865620100L;
	
	private String appName;		//应用名称
	private String packageName;	// APK包名
	private String versionCode; //版本号
	private String versionName; //版本名称
	private String md5;
	
	public AppInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AppInfo(String appName, String packageName, String versionCode,
			String versionName) {
		super();
		this.appName = appName;
		this.packageName = packageName;
		this.versionCode = versionCode;
		this.versionName = versionName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "AppInfo [appName=" + appName + ", packageName=" + packageName
				+ ", versionCode=" + versionCode + ", versionName="
				+ versionName + ", md5=" + md5 + "]";
	}
}
