package com.tcl.wechat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.tcl.wechat.R;

/**
 * 第一个家庭留言板
 * @author rex.lei
 *
 */
public class MsgBoardSecondView extends LinearLayout{

	private static final String TAG = MsgBoardSecondView.class.getSimpleName();
	
	private Context mContext;
	
	private View mView;
	
	public MsgBoardSecondView(Context context) {
		this(context, null);
	}
	
	public MsgBoardSecondView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		Init(context);
	}

	private void Init(Context context) {
		// TODO Auto-generated method stub
		mContext = context;
		mView = inflate(context, R.layout.layout_msgboard_2, this);
	}

	
	
	
	

}
