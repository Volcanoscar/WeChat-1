package com.tcl.wechat.modle;

import java.util.Date;

/**
 * 消息模型
 * @author rex.lei
 *
 */
public class MessageInfo {

	private int messageId; 		  //消息编号
	private String sender; 		  //消息发送者
	private String receiver;	  //消息接收者
	private Date msgSendTime; 	  //消息发送时间
	private Message massage;	  //消息实体
	
	public MessageInfo(int messageId, String sender, String receiver,
			Date msgSendTime, Message massage) {
		super();
		this.messageId = messageId;
		this.sender = sender;
		this.receiver = receiver;
		this.msgSendTime = msgSendTime;
		this.massage = massage;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Date getMsgSendTime() {
		return msgSendTime;
	}

	public void setMsgSendTime(Date msgSendTime) {
		this.msgSendTime = msgSendTime;
	}

	public Message getMassage() {
		return massage;
	}

	public void setMassage(Message massage) {
		this.massage = massage;
	}

	@Override
	public String toString() {
		return "MessageInfo [messageId=" + messageId + ", sender=" + sender
				+ ", receiver=" + receiver + ", msgSendTime=" + msgSendTime
				+ ", massage=" + massage + "]";
	}
}
