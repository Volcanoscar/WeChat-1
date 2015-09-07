package com.tcl.wechat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.tcl.wechat.R;
import com.tcl.wechat.view.page.TextPageView;

/**
 * 留言板视图类
 * @author rex.lei
 */
public class MessageBoardView extends LinearLayout{

	private View mView;
	//显示消息控件
	private TextPageView mMsgPageView;
	
	public MessageBoardView(Context context) {
		this(context, null);
	}
	
	public MessageBoardView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public MessageBoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context){
		mView = inflate(context, R.layout.main_familyboard_layout_1, this);
	}
}
