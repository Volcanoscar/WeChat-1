/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 微信绑定解绑事件信息
 * @author rex.lei
 *
 */
public class WeiNotice implements Parcelable{
	
	
	/**事件类型包括绑定（bind）、解除绑定（unbind）*/
	private String event;
	
	/**微信用户标识*/
	private String openId;
	
	/**用户昵称*/
	private String nickName;
	
	/**用户的性别，1:男；2:女；-1:系统用户； 0：未知*/
	private String sex;
	
	/**用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空*/
	private String headImageUrl;
	
	public WeiNotice() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public WeiNotice(String event, String openId, String nickName, String sex,
			String headImageUrl) {
		super();
		this.event = event;
		this.openId = openId;
		this.nickName = nickName;
		this.sex = sex;
		this.headImageUrl = headImageUrl;
	}


	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getHeadImageUrl() {
		return headImageUrl;
	}

	public void setHeadImageUrl(String headImageUrl) {
		this.headImageUrl = headImageUrl;
	}

	@Override
	public String toString() {
		return "WeiNotice [event=" + event + ", openId=" + openId
				+ ", nickName=" + nickName + ", sex=" + sex + ", headImageUrl="
				+ headImageUrl + "]";
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public WeiNotice(Parcel source){
		this.event = source.readString();
		this.openId = source.readString();
		this.nickName = source.readString();
		this.sex = source.readString();
		this.headImageUrl = source.readString();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(event);
		dest.writeString(openId);
		dest.writeString(nickName);
		dest.writeString(sex);
		dest.writeString(headImageUrl);
	}
	
	public static Parcelable.Creator<WeiNotice> CREATOR = new Creator<WeiNotice>() {

		@Override
		public WeiNotice createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new WeiNotice(source);
		}

		@Override
		public WeiNotice[] newArray(int size) {
			// TODO Auto-generated method stub
			return new WeiNotice[size];
		}
	};
}
