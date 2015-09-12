package com.tcl.wechat.modle;

import java.io.Serializable;


/**
 * App应用信息
 * @author rex.lei
 *
 */
public class AppInfo implements Serializable {


	private static final long serialVersionUID = -2455950643865620100L;
	
	private String packageName;	// APK包名
	private String appName;		//应用名称
	
	public AppInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AppInfo(String packageName, String appName) {
		super();
		this.packageName = packageName;
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	@Override
	public String toString() {
		return "AppInfo [packageName=" + packageName + ", appName=" + appName
				+ "]";
	}
}
