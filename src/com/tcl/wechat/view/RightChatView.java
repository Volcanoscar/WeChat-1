package com.tcl.wechat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 右侧聊天界面
 * @author rex.lei
 *
 */
public class RightChatView extends LinearLayout{
	
	public RightChatView(Context context) {
		this(context, null);
	}
	
	public RightChatView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public RightChatView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

}
