package com.tcl.wechat.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.ui.activity.WebViewActivity;

/**
 * 链接显示文本
 * @author rex.lei
 *
 */
public class ChatMsgLinkView extends View implements OnClickListener{
	
	private Context mContext;
	
	private LinearLayout mLinearLayout;
	private TextView mTitleTv;
	private TextView mDetailTv;
	
	//链接地址
	private String mLinkUrl;
	
	public ChatMsgLinkView(Context context) {
		this(context, null);
	}

	public ChatMsgLinkView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	
	public ChatMsgLinkView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		initView(context);
	}

	private void initView(Context context) {
		View view = inflate(context, R.layout.layout_chat_linkview, null);
		mLinearLayout = (LinearLayout) view.findViewById(R.id.layout_link);
		mTitleTv = (TextView) mLinearLayout.findViewById(R.id.link_title);
		mDetailTv = (TextView) mLinearLayout.findViewById(R.id.link_detail);
	}

	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title){
		mTitleTv.setText(title);
	}
	
	/**
	 * 设置描述信息
	 * @param detail
	 */
	public void setDetail(String detail){
		mDetailTv.setText(detail);
	}
	
	/**
	 * 设置链接地址
	 * @param linkUrl
	 */
	public void setUrl(String linkUrl){
		this.mLinkUrl = linkUrl;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try {
			Intent intent = new Intent(mContext, WebViewActivity.class); 
			intent.putExtra("LinkUrl", mLinkUrl);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
}
