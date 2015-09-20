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
	
	
	
}
