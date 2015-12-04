package com.tcl.wechat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.tcl.wechat.R;

/**
 * 聊天列表界面
 * @author rex.lei
 *
 */
public class ChatListView extends ListView implements OnScrollListener{
	
	private FrameLayout mHeaderLayout;
	private LinearLayout mHeadView;
	private ProgressBar mProgressBar;
	
	private int mHeaderHeight ; //顶部布局文件的高度
	private int mFirstVisibleItem; //当前第一个可见Item位置
	private int mStartY;
	private int mState = DONE;
	
	/**
	 * 实际的padding的距离与界面上偏移距离的比例
	 */
	private final static int RATIO = 3;
	
	/**
	 * 用于保证startY的值在一个完整的touch事件中只被记录一次
	 */
	private boolean bRecored;
	
	/**
	 * 是否返回
	 */
	private boolean bBack;
	
	private boolean bRefreash ;
	
	/**
	 * 滑动事件
	 */
	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;
    
	private OnRefreshListener mRefreshListener;
	
	public interface OnRefreshListener {
		public void onRefresh();
	}
	
    public void setOnRefreshListener(OnRefreshListener listener){
    	mRefreshListener = listener;
    	bRefreash = true;
    }
	
	public ChatListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public ChatListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public ChatListView(Context context) {
		super(context);
		initView(context);
	}
	
	@SuppressLint("InflateParams") 
	private void initView(Context context){
		mHeaderLayout = (FrameLayout) LayoutInflater.from(context)
				.inflate(R.layout.layout_chatlist_header, null);
		mHeadView = (LinearLayout) mHeaderLayout.findViewById(R.id.drop_down_head);
		mProgressBar = (ProgressBar) mHeaderLayout.findViewById(R.id.loading);
		
		measureView(mHeadView);
		mHeaderHeight = mHeadView.getMeasuredHeight();
		topPadding(-mHeaderHeight);
		addHeaderView(mHeaderLayout, null, false);
		
		setTranscriptMode(TRANSCRIPT_MODE_ALWAYS_SCROLL);
	}
	
	/**
	 * 设置Header布局的上边距
	 * @param topPadding
	 */
	private void topPadding(int topPadding){
		mHeadView.setPadding(0, topPadding, 0, 0);
		mHeadView.invalidate();
		setOnScrollListener(this);
	}
	
	/**
	 * 计算Header的大小
	 * @param view
	 */
	private void measureView(View view) {
		ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) view.getLayoutParams();
		if (params == null){
			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, params.width);  
        int lpHeight = params.height;  
        int childHeightSpec;  
        if (lpHeight > 0) {  
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,  
                    MeasureSpec.EXACTLY);  
        } else {  
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,  
                    MeasureSpec.UNSPECIFIED);  
        }  
        view.measure(childWidthSpec, childHeightSpec); 
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		if (mFirstVisibleItem == 0){
			//TODO 是否实现自动刷新
		}
	}
	
	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!bRefreash) {
			return super.onTouchEvent(event);
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mFirstVisibleItem == 0 && !bRecored){
				bRecored = true;
				mStartY = (int) event.getY();
			}
			break;
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (mState != REFRESHING && mState != LOADING) {
				if (mState == PULL_To_REFRESH) {
					mState = DONE;
					changeHeaderViewByState();
				}
				if (mState == RELEASE_To_REFRESH) {
					mState = REFRESHING;
					changeHeaderViewByState();
					onRefresh();
				}
			}
			bRecored = false;
			bBack = false;
			break;
			
		case MotionEvent.ACTION_MOVE:
			int tempY = (int) event.getY();
			if (!bRecored && mFirstVisibleItem == 0) {
				bRecored = true;
				mStartY = tempY;
			}

			if (mState != REFRESHING && bRecored && mState != LOADING) {
				if (mState == RELEASE_To_REFRESH) {
					setSelection(0);
					if (((tempY - mStartY) / RATIO < mHeaderHeight) && (tempY - mStartY) > 0) {
						mState = PULL_To_REFRESH;
						changeHeaderViewByState();
					} else if (tempY - mStartY <= 0) {
						mState = DONE;
						changeHeaderViewByState();
					}
				}
				if (mState == PULL_To_REFRESH) {
					setSelection(0);
					if ((tempY - mStartY) / RATIO >= mHeaderHeight) {
						mState = RELEASE_To_REFRESH;
						bBack = true;
						changeHeaderViewByState();
					} else if (tempY - mStartY <= 0) {
						mState = DONE;
						changeHeaderViewByState();
					}
				}
				if (mState == DONE) {
					if (tempY - mStartY > 0) {
						mState = PULL_To_REFRESH;
						changeHeaderViewByState();
					}
				}

				if (mState == PULL_To_REFRESH) {
					mHeadView.setPadding(0, -1 * mHeaderHeight + (tempY - mStartY) / RATIO, 0, 0);
				}
				if (mState == RELEASE_To_REFRESH) {
					mHeadView.setPadding(0, (tempY - mStartY) / RATIO - mHeaderHeight, 0, 0);
				}
			}
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	
	/**
	 * 更新界面
	 */
	private void changeHeaderViewByState() {
		switch (mState) {
		case RELEASE_To_REFRESH:
			mProgressBar.setVisibility(View.VISIBLE);
			break;
			
		case PULL_To_REFRESH:
			mProgressBar.setVisibility(View.VISIBLE);
			if (bBack) {
				bBack = false;
			}
			break;

		case REFRESHING:
			mHeadView.setPadding(0, 0, 0, 0);
			mProgressBar.setVisibility(View.VISIBLE);
			break;
			
		case DONE:
			mHeadView.setPadding(0, -1 * mHeaderHeight, 0, 0);
			mProgressBar.setVisibility(View.GONE);
			break;
		}
	}

	private void onRefresh() {
		if (mRefreshListener != null) {
			mRefreshListener.onRefresh();
		}
	}
	
	public void onRefreshComplete() {
		mState = DONE;
		changeHeaderViewByState();
	}
}
