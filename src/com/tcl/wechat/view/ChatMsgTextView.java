package com.tcl.wechat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcl.wechat.R;

/**
 * 聊天界面--文本显示(文本/表情)
 * @author rex.lei
 *
 */
@SuppressLint("InflateParams") 
public class ChatMsgTextView extends LinearLayout{

	private Context mContext;
	
	private LayoutInflater mInflater;
	
	private View mView;
	private TextView mMsgTextTv;
	private ImageView mMsgFaceImg;
	
	
	public ChatMsgTextView(Context context) {
		this(context, null);
	}
	
	public ChatMsgTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ChatMsgTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		
		mView = mInflater.inflate(R.layout.layout_chat_text_face, null);
		mMsgTextTv = (TextView) mView.findViewById(R.id.img_msg_face);
		mMsgFaceImg = (ImageView) mView.findViewById(R.id.tv_msg_text);
		
	}
	
	/**
	 * 设置显示内容
	 * @param content
	 */
	public final void setText(CharSequence content) {
		mMsgTextTv.setText(content);
	}
	
	public void setTextColor(int color){
		mMsgTextTv.setTextColor(color);
	}
	
	public void setTextSize(float size){
		mMsgTextTv.setTextSize(size);
	}

	public void setTypeface(Typeface tf){
		mMsgTextTv.setTypeface(tf);
	}

}
