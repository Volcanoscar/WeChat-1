package com.tcl.wechat.modle;

import java.io.Serializable;


/**
 * @author kuanghq
 *
 */
/**
 * @author kuanghq
 * 
 */
public class AppInfo implements Serializable {


	private String packageName;// APK包名
	private String appname;//应用名称






	public String getappname() {
		return appname;
	}

	public void setappname(String appname) {
		this.appname = appname;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	

}
