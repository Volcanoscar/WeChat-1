package com.tcl.wechat.modle;

import java.util.Arrays;

/**
 * 消息实体类
 * @author rex.lei
 *
 */
public class Message {

	/**
	 * 消息类型:
	 * 0：文本，默认
	 * 1：音频
	 * 2：视频
     * 3：图片
     * 4：动画
     * -1：错误类型
	 */
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
	private byte[] msgContent;
	
	/**
	 * 消息未读数量
	 */
	private int msgUnReadCnt;
	
	public Message(int msgType, int msgStatus, byte[] msgContent,
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

	public byte[] getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(byte[] msgContent) {
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
				+ ", msgContent=" + Arrays.toString(msgContent)
				+ ", msgUnReadCnt=" + msgUnReadCnt + "]";
	}
}
