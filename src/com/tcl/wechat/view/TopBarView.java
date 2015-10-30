package com.tcl.wechat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcl.wechat.R;

/**
 * 自定义TopBarView控件
 * @author rex.lei
 *
 */
public class TopBarView extends RelativeLayout {

	private View mView ;
	private String mTitle;
	private TextView mTitleTv;
	
	public TopBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBatView);
		mTitle = ta.getString(R.styleable.TopBatView_title);
		ta.recycle();
		
		mView = LayoutInflater.from(context).inflate(R.layout.topbar_layout, this, true);
		mTitleTv = (TextView) mView.findViewById(R.id.tv_topbar_title);
		mTitleTv.setTextSize(24);
		mTitleTv.setText(mTitle);
	}
}
