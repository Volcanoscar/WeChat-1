package com.tcl.wechat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.tcl.wechat.R;

/**
 * 第三个家庭留言板
 * @author rex.lei
 *
 */
public class MsgBoardThirdView extends LinearLayout{

	private static final String TAG = MsgBoardThirdView.class.getSimpleName();
	
	private Context mContext;
	
	private View mView;
	
	public MsgBoardThirdView(Context context) {
		this(context, null);
	}
	
	public MsgBoardThirdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		Init(context);
	}

	private void Init(Context context) {
		// TODO Auto-generated method stub
		mContext = context;
		mView = inflate(context, R.layout.layout_msgboard_3, this);
	}

	
	
	
	

}
