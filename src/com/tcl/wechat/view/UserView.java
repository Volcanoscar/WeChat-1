package com.tcl.wechat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.tcl.wechat.R;

/**
 * 好友信息封装类
 * @author rex.lei
 *
 */
public class UserView extends LinearLayout {
	
	private UserInfoView mUserInfoView;
	
	public UserView(Context context) {
		this(context, null);
	}
	
	public UserView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public UserView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		View mView = inflate(context, R.layout.layout_friend_view, this);
		mUserInfoView = (UserInfoView) mView.findViewById(R.id.user_info);
	}

	public UserInfoView getUserInfoView(){
		return mUserInfoView;
	}
}
