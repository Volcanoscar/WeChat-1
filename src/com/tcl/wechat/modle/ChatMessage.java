package com.tcl.wechat.modle;

/**
 * 聊天消息类型
 * @author rex.lei
 *
 */
public class ChatMessage {
	
	private String userName;
	/**
	 * 消息内容，类型主要根据消息类型来决定
	 * 储存类型： 
	 * 		文字消息： String 文字内容
	 * 		表情：Bitmap  表情Bitmap
	 * 		音频：String  音频文件path
	 * 		视频：String  视频文件path
	 * 		动画：byte[]  动画数据
	 */
	private Object message;
	private String time;
	private ChatMsgSource source;
	private ChatMsgType type;
	
	public ChatMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ChatMessage(String userName, Object message, String time,
			ChatMsgSource source, ChatMsgType type) {
		super();
		this.userName = userName;
		this.message = message;
		this.time = time;
		this.source = source;
		this.type = type;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public ChatMsgSource getSource() {
		return source;
	}

	public void setSource(ChatMsgSource source) {
		this.source = source;
	}

	public ChatMsgType getType() {
		return type;
	}

	public void setType(ChatMsgType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ChatMessage [userName=" + userName + ", message=" + message
				+ ", time=" + time + ", source=" + source + ", type=" + type
				+ "]";
	}
	
}
