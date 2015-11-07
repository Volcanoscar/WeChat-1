package com.tcl.wechat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.HorizontalScrollView;

/**
 * 列表滑动栏目
 * @author rex.lei
 *
 */
public class GroupScrollView extends HorizontalScrollView{

	private static final String TAG = GroupScrollView.class.getSimpleName();
	
	private ScrollViewListener mListener;
	
	public void setScrollViewListener(ScrollViewListener listener) {
		mListener = listener;
	}
	
	public GroupScrollView(Context context) {
		this(context, null);
	}
	
	public GroupScrollView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}
	
	public GroupScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
		// TODO Auto-generated method stub
		super.onScrollChanged(left, top, oldLeft, oldTop);
		
		Log.i(TAG, "left:" + left + ", top:" + top + ",oldLeft:" + oldLeft + ",oldTop:" + oldTop);
		
		if (mListener != null) {
			mListener.onScrollChanged(left, top, oldLeft, oldTop);
		}
	}
	
	/**
	 * 滑动监听接口
	 * @author rex.lei
	 *
	 */
	public interface ScrollViewListener { 
		
	    public void onScrollChanged(int left, int top, int oldLeft, int oldTop);  
	}  
	
}
