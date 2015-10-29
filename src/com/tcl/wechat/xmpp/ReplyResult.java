package com.tcl.wechat.xmpp;

/**
 * 消息回应
 * @author rex.lei
 * @note 客户端将消息发送给服务器，服务返回的结果信息。
 *
 */
public class ReplyResult {

	private String msgid;
	private String errCode;
	private String result;
	
	public ReplyResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ReplyResult(String msgid, String errCode, String result) {
		super();
		this.msgid = msgid;
		this.errCode = errCode;
		this.result = result;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "ReplyResult [msgid=" + msgid + ", errCode=" + errCode
				+ ", result=" + result + "]";
	}
}
