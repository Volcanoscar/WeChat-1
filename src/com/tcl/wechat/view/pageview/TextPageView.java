package com.tcl.wechat.view.pageview;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.model.WeiXinMessage;

/**
 * 聊天消息显示内容：消息内容  + 页码
 * @author rex.lei
 *
 */
public class TextPageView extends LinearLayout{

	private static final String TAG = TextPageView.class.getSimpleName();
	/**
	 * 每一页字符个数
	 */
	private static final int PAGE_CHAT_COUNT = 50;
	
	private View mView;
	private TextView mPageNumTv;
	private ViewPager mMsgPageView;
	private TextPageAdapter mAdapter;
	private ArrayList<ReadView> mReadViews ;
	
	private int mPageCount = 0;
	private int position = -1;
	private WeiXinMessage mMsgRecorder;
	private String mFontPath = "fonts/oop.TTF";
	
	public TextPageView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public TextPageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressLint("NewApi") 
	public TextPageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void init(Context context){
		mView = inflate(context, R.layout.layout_text_page, this);
		mMsgPageView = (ViewPager) mView.findViewById(R.id.page_msg_info);
		mPageNumTv = (TextView) mView.findViewById(R.id.tv_page_num);
		
		if (mMsgRecorder == null){
			return ;
		}
		
		if (ChatMsgType.TEXT.equals(mMsgRecorder.getMsgtype())){
			initTextPageView(context);
		} 
	}
	
	/**
	 * 文本信息初始化
	 */
	private void initTextPageView(Context context){
		if (mMsgRecorder == null){
			return ;
		}
		String msgContent = mMsgRecorder.getContent();
		Log.i(TAG, "TextMsg content:" + msgContent);
		
		mPageCount = getPageCount(msgContent); 
		Log.i(TAG, "mPageCount:" + mPageCount);
		
		if (mPageCount == 1){
			mPageNumTv.setVisibility(View.GONE);
		}
		
		mReadViews = new ArrayList<ReadView>();
		
		if (!TextUtils.isEmpty(msgContent)){
			for (int i = 0; i < mPageCount; i++) {
				ReadView readView = new ReadView(context);
				readView.setText(getCurrentPageMessage(msgContent, i));
				readView.setTextSize(28);
				readView.setTextColor(Color.BLACK);
				readView.setFont(mFontPath);
				readView.setGravity(Gravity.CENTER);
				readView.setFocusable(false);
				readView.setFocusableInTouchMode(false);
				mReadViews.add(readView);
			} 
		}
		mAdapter = new TextPageAdapter(mReadViews);
		mMsgPageView.setAdapter(mAdapter);
//		mMsgPageView.setOnPageChangeListener(pageChangeListener);
		mMsgPageView.addOnPageChangeListener(pageChangeListener);
	}
	
	private int getPageCount(String msgInfoStr){
		if (TextUtils.isEmpty(msgInfoStr)){
			return 0;
		}
		
		int count = (int)(msgInfoStr.length() /  PAGE_CHAT_COUNT);
		if ((msgInfoStr.length() %  PAGE_CHAT_COUNT) != 0){
			count ++;
		}
		return count;
	}
	/**
	 * 判断是否是最后一页
	 * @param index
	 * @return
	 */
	private boolean isLastPage(int index){
		return (index == mPageCount - 1 ) ? true : false;
	}
	
	/**
	 * 获取当前页面的文字信息
	 * @param curIndex
	 * @return
	 */
	private String getCurrentPageMessage(String mMsgInfoStr, int curIndex){
		position = curIndex * PAGE_CHAT_COUNT;
		if (isLastPage(curIndex)){
			return mMsgInfoStr.substring(position, mMsgInfoStr.length());
		} else {
			return mMsgInfoStr.substring(position, position + PAGE_CHAT_COUNT);
		}
	}
	
	/**
	 * 设置页面索引
	 * @param index
	 */
	private void setPageIndicator(int index){
		if (mPageNumTv != null && mPageNumTv.getVisibility() == ViewPager.VISIBLE){
			mPageNumTv.setText(String.format(getResources().getString(
					R.string.page_Indicator), index + 1, mPageCount));
		}
	}
	
	/**
	 * 添加数据,提供外部接口
	 * @param message
	 */
	public void setMessageInfo(Context context, WeiXinMessage recorder){
		mMsgRecorder = recorder;
		init(context);
	}
	
	/**
	 * 添加数据,提供外部接口,带字体
	 * @param message
	 * @param fontPath 字体路径
	 */
	public void setMessageInfo(Context context, WeiXinMessage recorder, String fontPath){
		mMsgRecorder = recorder;
		mFontPath = fontPath;
		init(context);
	}
	
	/**
	 * 设置字体样式
	 * @param fontPath
	 */
	public void setFont(String fontPath){
		mFontPath = fontPath;
	}
	
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onPageScrolled ");
			setPageIndicator(arg0);
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
	};
}
