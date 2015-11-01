package com.tcl.wechat.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
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
	private int[] track = new int[]{0, 0, 0, 0, 0}; 
	
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
        	attachChildViewToParent(view, width, height, 65 , 0, position);
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
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,
                height);
		params.width = 0;
		params.height = LayoutParams.MATCH_PARENT;
		params.weight = 1;
		params.gravity = Gravity.CENTER_HORIZONTAL;
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
}
