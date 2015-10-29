package com.tcl.wechat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 消息回复内容
 * @author rex.lei
 *
 */
public class WeiReplyMsg implements Parcelable{
	
	private String toUserName;
	private String fromUserName;
	private String msgType;
	/** 文本消息，只回复消息内容即可*/
	private String content;
	/** 媒体类文件(音频、视频、图片)，需要meidia */
	private String mediaId;
	private String createTime;
	
	public WeiReplyMsg() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public WeiReplyMsg(String toUserName, String fromUserName, String msgType,
			String content, String mediaId, String createTime) {
		super();
		this.toUserName = toUserName;
		this.fromUserName = fromUserName;
		this.msgType = msgType;
		this.content = content;
		this.mediaId = mediaId;
		this.createTime = createTime;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "WeiReplyMsg [toUserName=" + toUserName + ", fromUserName="
				+ fromUserName + ", msgType=" + msgType + ", content="
				+ content + ", mediaId=" + mediaId + ", createTime="
				+ createTime + "]";
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public WeiReplyMsg(Parcel source) {
		super();
		this.toUserName = source.readString();
		this.fromUserName = source.readString();
		this.msgType = source.readString();
		this.content = source.readString();
		this.mediaId = source.readString();
		this.createTime = source.readString();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(toUserName);
		dest.writeString(fromUserName);
		dest.writeString(msgType);
		dest.writeString(content);
		dest.writeString(mediaId);
		dest.writeString(createTime);
	}

	public static Parcelable.Creator<WeiReplyMsg> CREATOR = new Creator<WeiReplyMsg>() {

		@Override
		public WeiReplyMsg createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new WeiReplyMsg(source);
		}

		@Override
		public WeiReplyMsg[] newArray(int size) {
			// TODO Auto-generated method stub
			return new WeiReplyMsg[size];
		}
	}; 
}
