package com.tcl.wechat.receiver;

/**
 * 消息接收代理类
 * @author rex.lei
 *
 */
public class WeiXinMsgProxy {

	private static final String TAG = "MsgReceiverProxy";
	
	private static class MsgReceiverProxyInstance{
		private static final WeiXinMsgProxy mInstance = new WeiXinMsgProxy();
	}
	
	private WeiXinMsgProxy() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static WeiXinMsgProxy getInstance(){
		return MsgReceiverProxyInstance.mInstance;
	}
	
	
	

}
