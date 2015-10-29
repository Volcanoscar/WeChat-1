package com.tcl.wechat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 绑定用户
 * @author rex.lei
 *
 */
public class BindUser implements Parcelable{
	
	private String openId;		//微信用户标识
	private String nickName;	//用户昵称
	private String remarkName;	//备注名称
	/**
	 * 性别，1 :男
	 * 		2 :女
	 *      0 :未知,标识公众号
	 */
	private String sex ;		
	
	/**
	 * 用户头像
	 * 		最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
	 * 		比如：http://wx.qlogo.cn/mmopen/Vw948POqJQ8qP7qIhH4/0
	 */
	private String headImageUrl;//用户头像存储地址
	private String newsNum;		//消息个数
	/**
	 * success 绑定成功，收到请求绑定
	 * wait    等待服务器返回。
	 * 		         默认是success
	 */
	private String status;
	
	/**
	 * 服务器回复
	 * 		disallow：不允许用户加入
	 * 		allow：
	 */
	private String reply;
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(openId);
		dest.writeString(nickName);
		dest.writeString(remarkName);
		dest.writeString(sex);
		dest.writeString(headImageUrl);
		dest.writeString(newsNum);
		dest.writeString(status);
		dest.writeString(reply);
	}
	
	public static Parcelable.Creator<BindUser> CREATOR = new Creator<BindUser>() {

		@Override
		public BindUser createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new BindUser(source);
		}

		@Override
		public BindUser[] newArray(int size) {
			// TODO Auto-generated method stub
			return new BindUser[size];
		}
	};
	
	
	public BindUser() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public BindUser(Parcel source) {
		super();
		this.openId = source.readString();
		this.nickName = source.readString();
		this.remarkName = source.readString();
		this.sex = source.readString();
		this.headImageUrl = source.readString();
		this.newsNum = source.readString();
		this.status = source.readString();
		this.reply = source.readString();
	}

	public BindUser(String openId, String nickName, String remarkName,
			String sex, String headImageUrl, String newsNum, String status,
			String reply) {
		super();
		this.openId = openId;
		this.nickName = nickName;
		this.remarkName = remarkName;
		this.sex = sex;
		this.headImageUrl = headImageUrl;
		this.newsNum = newsNum;
		this.status = status;
		this.reply = reply;
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

	public String getRemarkName() {
		return remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
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

	public String getNewsNum() {
		return newsNum;
	}

	public void setNewsNum(String newsNum) {
		this.newsNum = newsNum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	@Override
	public String toString() {
		return "BindUser [openId=" + openId + ", nickName=" + nickName
				+ ", remarkName=" + remarkName + ", sex=" + sex
				+ ", headImageUrl=" + headImageUrl + ", newsNum=" + newsNum
				+ ", status=" + status + ", reply=" + reply + "]";
	}
}
