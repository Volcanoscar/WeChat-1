package com.tcl.wechat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.tcl.wechat.R;

public class MyFriendsGroupView extends LinearLayout {

	
	public MyFriendsGroupView(Context context) {
		super(context);
		init(context);
	}

	public MyFriendsGroupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context){
        inflate(context, R.layout.main_friend_group, this);
        this.setVisibility(View.GONE);
	}
	
	 @Override
    public void addView(View child, int index,
            android.view.ViewGroup.LayoutParams params){
        super.addView(child, index, params);
    }
	
}
