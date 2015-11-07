package com.tcl.wechat.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tcl.wechat.WeApplication;
import com.tcl.wechat.database.WeiMsgRecordDao;
import com.tcl.wechat.database.WeiUserDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.RecorderInfo;
import com.tcl.wechat.model.WeiXinMsgRecorder;
import com.tcl.wechat.utils.DensityUtil;
import com.tcl.wechat.view.GroupScrollView.ScrollViewListener;

/**
 * 留言板视图类
 * @author rex.lei
 */
public class MsgBoardGroupView extends LinearLayout{

	private static final String TAG = MsgBoardGroupView.class.getSimpleName();
	
	private Context mContext;
	
	/**
	 * 用户数据列表
	 */
	private LinkedList<RecorderInfo> mAllRecorderInfos;
	
	/**
	 * 用户视图排序
	 */
	private HashMap<String, MsgBoardView> mMsgBoardViewMap ;
	
	private final int USER_TAB_WIDTH = 400;

	private final int USER_TAB_HEIGHT = 400;
	
	/*
	 * 轨迹，根据移动的leftMargn来确定
	 */
	private int[] track = new int[]{20, 70, 100, 90};
	
	private GroupScrollView mHorizontalScrollView = null;
	
	public void setScrollView(GroupScrollView scrollView){
		mHorizontalScrollView = scrollView;
        mHorizontalScrollView.setScrollViewListener(mScrollListener);
    }
	
	public MsgBoardGroupView(Context context) {
		this(context, null);
	}
	
	public MsgBoardGroupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context){
		//初始化
		mContext = context;
		
		mMsgBoardViewMap = new HashMap<String, MsgBoardView>();
		mAllRecorderInfos = new LinkedList<RecorderInfo>();
		
		//加载数据
		//loadMsgBoardData();
	}
	
	/**
	 * 加载用户聊天数据
	 */
	public void loadMsgBoardData(){
		/**
		 * 获取所有聊天用户的   最后一条  聊天信息
		 */
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				
				ArrayList<WeiXinMsgRecorder> lastRecorders = WeiMsgRecordDao.getInstance().getLastRecorder();
				
				Log.i(TAG, "lastRecorders:" + lastRecorders);
				if (lastRecorders == null){
					return null;
				}
				
				int size = lastRecorders.size();
				Log.i(TAG, "lastRecorderSize:" + size);
				for (int i = 0; i < size; i++) {
					WeiXinMsgRecorder recorder = lastRecorders.get(i);
					BindUser bindUser = WeiUserDao.getInstance().getUser(recorder.getOpenid());
					mAllRecorderInfos.addFirst(new RecorderInfo(bindUser, recorder));
				}
				
				RecorderInfo recorder = mAllRecorderInfos.get(0);
				if (recorder != null) {
					for (int i = 0; i < 5; i++) {
						mAllRecorderInfos.add(recorder);
					}
				}
				
				return null;
			};
			protected void onPostExecute(Void result) {
				if (mAllRecorderInfos == null || mAllRecorderInfos.isEmpty()){
					return ;
				}
				int size = mAllRecorderInfos.size();
				for (int i = 0; i < size; i++) {
					RecorderInfo recorder = mAllRecorderInfos.get(i);
					addRecorderColumn(recorder.getBindUser(), recorder.getRecorder(), i);
				}
			};
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}
	
	/**
	 * 增加消息记录
	 * @param bindUser
	 * @param recorder
	 */
	public void addRecorder(BindUser bindUser, WeiXinMsgRecorder recorder){
		if (bindUser == null || recorder == null){
			return ;
		}
		
		if (mAllRecorderInfos == null){
			mAllRecorderInfos = new LinkedList<RecorderInfo>();
		}
		
		/**
		 * 1、判断该视图是否已经创建
		 * 	是：更新消息
		 *  否：增加是否
		 */
		String openid = bindUser.getOpenId();
		RecorderInfo firsetRecorderInfo = null;
		if (!mAllRecorderInfos.isEmpty()){
			firsetRecorderInfo = mAllRecorderInfos.getFirst();
		}
		
		if (firsetRecorderInfo != null && bindUser.getOpenId().equals(
				firsetRecorderInfo.getBindUser().getOpenId())) {
			//再此只要跟新view即可
			MsgBoardView msgBoardView = mMsgBoardViewMap.get(openid);
			if (msgBoardView != null){
				msgBoardView.receiveNewMessage(bindUser, recorder);
			}
		} else { //不在第一位 或者 不存在
			removeAllView();
			RecorderInfo info = new RecorderInfo(bindUser, recorder);
			updateRecorderInfo(openid);
			mAllRecorderInfos.addFirst(info);
			mMsgBoardViewMap.clear();
			
			int size = mAllRecorderInfos.size();
			Log.i(TAG, "addRecorderSize:" + size);
			for (int i = 0; i < size; i++) {
				RecorderInfo recorderInfo = mAllRecorderInfos.get(i);
				addRecorderColumn(recorderInfo.getBindUser(), recorderInfo.getRecorder(), i);
			}
		 }
	}
	
	/**
	 * 移除用户视图
	 * @param bindUser
	 */
	public void removeRecorder(BindUser bindUser){
		if (bindUser == null){
			return ;
		}
		
		removeAllView();
		updateRecorderInfo(bindUser.getOpenId());
		mMsgBoardViewMap.clear();
		
		int size = mAllRecorderInfos.size();
		for (int i = 0; i < size; i++) {
			RecorderInfo recorderInfo = mAllRecorderInfos.get(i);
			addRecorderColumn(recorderInfo.getBindUser(), recorderInfo.getRecorder(), i);
		}
	}
	
	/**
	 * 更新数据列表
	 * @param openid
	 */
	private void updateRecorderInfo(String openid){
		
		if (TextUtils.isEmpty(openid)){
			return ;
		}
		/**
		 * 如果已经包含有该视图，则直接更新消息内容
		 * 否则，添加新视图
		 */
		if (mMsgBoardViewMap.containsKey(openid)){
			Iterator<RecorderInfo> iterator = mAllRecorderInfos.iterator();
			while (iterator.hasNext()) {
				RecorderInfo recorderInfo = iterator.next();
				if (recorderInfo != null && openid.equals(recorderInfo.getBindUser().getOpenId())){
					iterator.remove();
					break;
				}
			}
		}
	}
	
	/**
	 * 移除所有view
	 */
	private void removeAllView(){
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView =  getChildAt(i);
			if (childView != null){
				ViewGroup parent = (ViewGroup)childView.getParent();
				parent.removeView(childView);
			}
		}
		removeAllViewsInLayout();
	}
	
	/**
	 * 添加用户视图
	 * @param bindUser
	 * @param recorder
	 * @param position
	 */
	private void addRecorderColumn(BindUser bindUser, WeiXinMsgRecorder recorder, int position){
		if (bindUser == null || recorder == null){
			return;
		}
		
		int width = DensityUtil.dip2px(mContext, USER_TAB_WIDTH);
        int height = DensityUtil.dip2px(mContext, USER_TAB_HEIGHT);
        
        if (position < mAllRecorderInfos.size()){
        	View view = setUpView(bindUser, recorder, position);
        	attachChildViewToParent(view, width, height, 65 , getTopMargin(position), position);
        }
	}
	
	/**
	 * 生成控件
	 * @param bindUser
	 * @param recorder
	 * @param position
	 * @return
	 */
	private View setUpView(BindUser bindUser, WeiXinMsgRecorder recorder, int position) {
		if (bindUser == null){
			return null;
		}
		MsgBoardView childView = new MsgBoardView(mContext);
		childView.setupView(position % 4);
		childView.addData(bindUser, recorder);
		childView.setTag(bindUser.getOpenId());
		
		mMsgBoardViewMap.put(bindUser.getOpenId(), childView);
		return childView;
	}
	
	private void attachChildViewToParent(View view, int width, int height, 
			int leftMargin, int topMargin, int position) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		params.leftMargin = leftMargin;
		params.topMargin = topMargin;
        
		addView(view, position, params);
	}
	
	/**
	 * 接收新消息
	 * @param recorder
	 */
	public void receiveNewMessage(WeiXinMsgRecorder recorder){
		Log.i(TAG, "receive new message!");
		
		if (recorder == null){
			return ;
		}
		
		String openid = recorder.getOpenid();
		BindUser bindUser = WeiUserDao.getInstance().getUser(openid);
		
		if (bindUser != null){
			addRecorder(bindUser, recorder);
		}
	}
	
	/**
	 * 获取轨迹
	 * @param leftMargin
	 * @return
	 */
	private int getTopMargin(int index){
		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		int size = track[index % 4];
		return (int) (size * dm.density);
	}
	
	/**
	 * 以下实现滑动效果
	 */
	int[] topMargin = new int[]{1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,3,3,3,3,4,4,4,4,4,4,4,4,4,4,4,4,5,5,5,5,5,5,5,5,5,5,5,5,6,6,6,6,6,6,6,6,6,6,6,6,7,7,7,7,7,7,7,7,7,7,7,7,7,8,8,8,8,8,8,8,8,8,8,8,8,9,9,9,9,9,9,9,9,9,9,9,9,10,10,10,10,10,10,10,10,10,10,10,10,11,11,11,11,11,11,11,11,11,11,11,11,12,12,12,12,12,12,12,12,12,12,12,12,13,13,13,13,13,13,13,13,13,13,13,13,13,14,14,14,14,14,14,14,14,14,14,14,14,15,15,15,15,15,15,15,15,15,15,15,15,16,16,16,16,16,16,16,16,16,16,16,16,17,17,17,17,17,17,17,17,17,17,17,17,18,18,18,18,18,18,18,18,18,18,18,18,19,19,19,19,19,19,19,19,19,19,19,19,20,
			20,20,20,20,20,20,21,21,21,21,21,21,21,22,22,22,22,22,22,23,23,23,23,23,23,23,24,24,24,24,24,24,24,25,25,25,25,25,25,26,26,26,26,26,26,26,27,27,27,27,27,27,27,28,28,28,28,28,28,29,29,29,29,29,29,29,30,30,30,30,30,30,30,31,31,31,31,31,31,32,32,32,32,32,32,32,33,33,33,33,33,33,34,34,34,34,34,34,34,35,35,35,35,35,35,35,36,36,36,36,36,36,37,37,37,37,37,37,37,38,38,38,38,38,38,38,39,39,39,39,39,39,40,40,40,40,40,40,40,41,41,41,41,41,41,41,42,42,42,42,42,42,43,43,43,43,43,43,43,44,44,44,44,44,44,45,45,45,45,45,45,45,46,46,46,46,46,46,46,47,47,47,47,47,47,48,48,48,48,48,48,48,49,49,49,49,49,49,49,50,50,50,50,50,50,51,51,51,51,51,51,51,52,52,52,52,52,52,52,53,53,53,53,53,53,54,54,54,54,54,54,54,55,55,55,55,55,55,55,56,56,56,56,56,56,57,57,57,57,57,57,57,58,58,58,58,58,58,59,59,59,59,59,59,59,60,60,60,60,60,60,60,61,61,61,61,61,61,62,62,62,62,62,62,62,63,63,63,63,63,63,63,64,64,64,64,64,64,65,65,65,65,65,65,65,66,66,66,66,66,66,66,67,67,67,67,67,67,68,68,68,68,68,68,68,69,69,69,69,69,69,70,
			70,70,70,70,70,70,70,70,70,70,70,71,71,71,71,71,71,71,71,71,71,71,71,72,72,72,72,72,72,72,72,72,72,72,72,73,73,73,73,73,73,73,73,73,73,73,73,74,74,74,74,74,74,74,74,74,74,74,74,75,75,75,75,75,75,75,75,75,75,75,75,76,76,76,76,76,76,76,76,76,76,76,76,77,77,77,77,77,77,77,77,77,77,77,77,78,78,78,78,78,78,78,78,78,78,78,78,79,79,79,79,79,79,79,79,79,79,79,79,80,80,80,80,80,80,80,80,80,80,80,80,81,81,81,81,81,81,81,81,81,81,81,81,82,82,82,82,82,82,82,82,82,82,82,82,83,83,83,83,83,83,83,83,83,83,83,83,84,84,84,84,84,84,84,84,84,84,84,84,85,85,85,85,85,85,85,85,85,85,85,85,86,86,86,86,86,86,86,86,86,86,86,86,87,87,87,87,87,87,87,87,87,87,87,87,88,88,88,88,88,88,88,88,88,88,88,88,89,89,89,89,89,89,89,89,89,89,89,89,90,90,90,90,90,90,90,90,90,90,90,90,91,91,91,91,91,91,91,91,91,91,91,91,92,92,92,92,92,92,92,92,92,92,92,92,93,93,93,93,93,93,93,93,93,93,93,93,94,94,94,94,94,94,94,94,94,94,94,94,95,95,95,95,95,95,95,95,95,95,95,95,96,96,96,96,96,96,96,96,96,96,96,96,97,97,97,97,97,97,97,97,97,97,97,97,98,98,98,98,98,98,98,98,98,98,98,98,99,99,99,99,99,99,99,99,99,99,99,99,100,100,100,100,100,100,100,100,100,100,100,100,101,101,101,101,101,101,101,101,101,101,101,101,102,102,102,102,102,102,102,102,102,102,102,102,103,103,103,103,103,103,103,103,103,103,103,103,104,104,104,104,104,104,104,104,104,104,104,104,105,105,105,105,105,105,105,105,105,105,105,105,106,106,106,106,106,106,106,106,106,106,106,106,107,107,107,107,107,107,107,107,107,107,107,107,108,108,108,108,108,108,108,108,108,108,108,108,109,109,109,109,109,109,109,109,109,109,109,109,110,
			110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,108,108,108,108,108,108,108,108,108,108,108,108,108,108,108,108,108,108,108,108,108,108,108,108,107,107,107,107,107,107,107,107,107,107,107,107,107,107,107,107,107,107,107,107,107,107,107,107,106,106,106,106,106,106,106,106,106,106,106,106,106,106,106,106,106,106,106,106,106,106,106,106,105,105,105,105,105,105,105,105,105,105,105,105,105,105,105,105,105,105,105,105,105,105,105,105,104,104,104,104,104,104,104,104,104,104,104,104,104,104,104,104,104,104,104,104,104,104,104,104,103,103,103,103,103,103,103,103,103,103,103,103,103,103,103,103,103,103,103,103,103,103,103,103,102,102,102,102,102,102,102,102,102,102,102,102,102,102,102,102,102,102,102,102,102,102,102,102,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,98,98,98,98,98,98,98,98,98,98,98,98,98,98,98,98,98,98,98,98,98,98,98,98,97,97,97,97,97,97,97,97,97,97,97,97,97,97,97,97,97,97,97,97,97,97,97,97,96,96,96,96,96,96,96,96,96,96,96,96,96,96,96,96,96,96,96,96,96,96,96,96,95,95,95,95,95,95,95,95,95,95,95,95,95,95,95,95,95,95,95,95,95,95,95,95,94,94,94,94,94,94,94,94,94,94,94,94,94,94,94,94,94,94,94,94,94,94,94,94,93,93,93,93,93,93,93,93,93,93,93,93,93,93,93,93,93,93,93,93,93,93,93,93,92,92,92,92,92,92,92,92,92,92,92,92,92,92,92,92,92,92,92,92,92,92,92,92,91,91,91,91,91,91,91,91,91,91,91,91,91,91,91,91,91,91,91,91,91,91,91,91,90,
			110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110,110};
	
	private ScrollViewListener mScrollListener = new ScrollViewListener() {
		
		@Override
		public void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
			// TODO Auto-generated method stub
			Log.i(TAG, "Layout:[" + left + "," + top + "," + oldLeft + "," + oldTop + "]");
			
			/**
			 * 设计思路：根据滑动的left值，设置topMargin。然后重新layout
			 */
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				
				/**
				 * 当前控件的位置
				 */
				int[] location = new int[2];
				child.getLocationOnScreen(location);
	            int posX = location[0];
	            int posY = location[1];
				
				int childLeft = child.getLeft();
				int childTop = child.getTop();
				int childRight = child.getRight();
				
				//int width = child.getMeasuredWidth();
				int height = child.getMeasuredHeight();
				
				int topMatgin = childTop;
				if (posX > 0 && posX < 1920) {
					topMatgin = topMargin[(posX) % 1920];
				} 
				Log.i(TAG, "ChildIndex:" + i + "-->location:[" + (posX) + "," +  posY + "]" +
						"-->margin:[" + childLeft + "," + topMatgin + "," + childRight + "," + (topMatgin + height) + "]");
				child.layout(childLeft, topMatgin, childRight, topMatgin + height);
			}
		}
	};
}
