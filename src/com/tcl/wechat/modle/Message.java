package com.tcl.wechat.modle;


/**
 * 消息实体类
 * @author rex.lei
 *
 */
public class Message {

	
	private int msgType;
	
	/**
	 * 消息状态
	 * 0：已读
	 * 1：未读
	 * -1：Error
	 */
	private int msgStatus;
	
	/**
	 * 发送消息内容,可为：文本、音频、视频、图像、动画
	 */
	private String msgContent;
	
	/**
	 * 消息未读数量
	 */
	private int msgUnReadCnt;
	
	public Message(int msgType, int msgStatus, String msgContent,
			int msgUnReadCnt) {
		super();
		this.msgType = msgType;
		this.msgStatus = msgStatus;
		this.msgContent = msgContent;
		this.msgUnReadCnt = msgUnReadCnt;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public int getMsgStatus() {
		return msgStatus;
	}

	public void setMsgStatus(int msgStatus) {
		this.msgStatus = msgStatus;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public int getMsgUnReadCnt() {
		return msgUnReadCnt;
	}

	public void setMsgUnReadCnt(int msgUnReadCnt) {
		this.msgUnReadCnt = msgUnReadCnt;
	}

	@Override
	public String toString() {
		return "Message [msgType=" + msgType + ", msgStatus=" + msgStatus
				+ ", msgContent=" + msgContent + ", msgUnReadCnt="
				+ msgUnReadCnt + "]";
	}

}
