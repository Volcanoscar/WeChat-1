package com.tcl.wechat.modle;

/**
 * Apk相关信息
 * @author rex.lei
 *
 */
public class AppInfo {

	private String packageName;
	private int versionCode;
	private String versionName;
	private String md5;//MD5校验码
	
	public String getPackageName() {
		return packageName;
	}
	
	public int getVersionCode() {
		return versionCode;
	}
	
	public String getVersionName() {
		return versionName;
	}
	
	public String getMd5() {
		return md5;
	}

	@Override
	public String toString() {
		return "AppInfo [packageName=" + packageName + ", versionCode="
				+ versionCode + ", versionName=" + versionName + ", md5=" + md5
				+ "]";
	}
}
