package com.tcl.wechat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.tcl.wechat.R;

public class UserView extends LinearLayout {
	
	
	public UserView(Context context) {
		this(context, null);
	}
	
	public UserView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public UserView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		inflate(context, R.layout.layout_friend_view, null);
	}

}
