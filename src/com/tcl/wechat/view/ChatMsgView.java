package com.tcl.wechat.view;

import com.tcl.wechat.modle.ChatMsgType;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


/**
 * 根据消息的不同类型，绘制不同的控件
 * 	主要适用于Launcher 和 家庭留言板首页消息提示框使用
 * @author rex.lei
 *
 */
public class ChatMsgView extends View{

	private ChatMsgType mChatMsgType = ChatMsgType.TYPE_TEXT;
	
	public ChatMsgView(Context context) {
		super(context);
	}
	
	public ChatMsgView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ChatMsgView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
}
