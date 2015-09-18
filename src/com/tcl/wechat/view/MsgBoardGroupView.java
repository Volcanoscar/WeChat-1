package com.tcl.wechat.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.tcl.wechat.controller.WeiXinMsgManager;
import com.tcl.wechat.controller.listener.NewMessageListener;
import com.tcl.wechat.db.WeiMsgRecordDao;
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.WeiXinMsgRecorder;
import com.tcl.wechat.utils.DensityUtil;

/**
 * 留言板视图类
 * @author rex.lei
 */
public class MsgBoardGroupView extends LinearLayout{

	private static final String TAG = MsgBoardGroupView.class.getSimpleName();
	
	private Context mContext;
	private LayoutInflater mInflater;
	
	private static int mOnLineChatUserCnt = 0;
	/**
	 * 所有消息记录
	 */
	private ArrayList<WeiXinMsgRecorder> mAllMsgRecords;
	
	/**
	 * 保存每个用户的最新消息
	 */
	private HashMap<String, WeiXinMsgRecorder> mLastestMsgRecorderMap;
	
	/**
	 * 用户列表
	 */
	private HashMap<String, BindUser> mAllBindUserMap;
	
	/**
	 * 用户视图排序
	 */
	private HashMap<String, MsgBoardView> mMsgBoardViewMap ;
	
	
	private final int USER_TAB_WIDTH = 400;

	private final int USER_TAB_HEIGHT = 400;
	
	private WeiXinMsgManager mWeiXinMsgManager;
	
	/*
	 * 轨迹，根据移动的leftMargn来确定
	 */
	private int[] track = new int[]{0, 0, 0, 0, 0}; 
	
	private WeiUserDao mWeiUserDao = WeiUserDao.getInstance();
	private WeiMsgRecordDao mWeiMsgRecordDao = WeiMsgRecordDao.getInstance();
	
	
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
		mInflater = LayoutInflater.from(context);
		mWeiXinMsgManager = WeiXinMsgManager.getInstance();
		
		mAllMsgRecords = new ArrayList<WeiXinMsgRecorder>();
		mLastestMsgRecorderMap = new HashMap<String, WeiXinMsgRecorder>();
		mAllBindUserMap = new HashMap<String, BindUser>();
		mMsgBoardViewMap = new HashMap<String, MsgBoardView>();
		
		//加载数据
		loadMsgBoardData();
		
		//设置监听器
		mWeiXinMsgManager.addNewMessageListener(mNewMsgListener);
	}
	
	/**
	 * 加载用户聊天数据
	 */
	private void loadMsgBoardData(){
		
		mAllMsgRecords = mWeiMsgRecordDao.getAllUserRecorder();
		
		if (mAllMsgRecords == null && mAllMsgRecords.isEmpty()){
			return ;
		}
		//反转
		Collections.reverse(mAllMsgRecords);
		
		for (WeiXinMsgRecorder recorder : mAllMsgRecords) {
			String openid = recorder.getOpenid();
			if (!mLastestMsgRecorderMap.containsKey(openid)){
				mLastestMsgRecorderMap.put(openid, recorder);
				
				BindUser bindUser = mWeiUserDao.getUser(openid);
				if (bindUser != null){
					mAllBindUserMap.put(openid, bindUser);
				}
				
				addUserColumn(bindUser, mOnLineChatUserCnt);
				mOnLineChatUserCnt ++;
			}
		}
	}
	
	private void addUserColumn(BindUser bindUser, int position){
		if (bindUser == null){
			return;
		}
		int width = DensityUtil.dip2px(mContext, USER_TAB_WIDTH);
        int height = DensityUtil.dip2px(mContext,USER_TAB_HEIGHT);
        
        View view = setUpView(bindUser, position);
        attachChildViewToParent(view, position, width, height, 65 , 0);
        
	}
	
	private View setUpView(BindUser bindUser, int position) {
		if (bindUser == null){
			return null;
		}
		//方法一
		/*int layoutId = mContext.getResources().getIdentifier("layout_msgboard_" + (position + 1), 
				"layout", mContext.getPackageName());
		View childView = mInflater.inflate(layoutId, null);*/
		
		//方法二
		MsgBoardView childView = new MsgBoardView(mContext);
		childView.setupView(position % 4);
		childView.addData(bindUser, mLastestMsgRecorderMap.get(bindUser.getOpenId()));
		
		mMsgBoardViewMap.put(bindUser.getOpenId(), childView);
		
		//方法三
		/*View childView = null;
		switch (position % 4) {
		case 0:
			childView = new MsgBoardFirstView(mContext);
			break;
		case 1:
			childView = new MsgBoardSecondView(mContext);
		case 2:
			childView = new MsgBoardThirdView(mContext);
		case 3:
			childView = new MsgBoardFourthView(mContext);
		default:
			break;
		}*/
		return childView;
	}
	
	private void attachChildViewToParent(View view, int position, int width, int height, 
			int leftMargin, int topMargin) {
		Log.i(TAG, "leftMargin:" + leftMargin + ", topMargin:" + topMargin);
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
	
	private NewMessageListener mNewMsgListener = new NewMessageListener() {
		
		@Override
		public void onNewMessage(WeiXinMsgRecorder recorder) {
			Log.i(TAG, "receive new message!");
			
			if (recorder == null){
				return ;
			}
			
			/**
			 * 收到新消息
			 * 1、用户视图重新排序
			 */
			
			String openId = recorder.getOpenid();
			
			if (mLastestMsgRecorderMap.containsKey(openId)){
				mLastestMsgRecorderMap.remove(openId);
			}
			mLastestMsgRecorderMap.put(openId, recorder);
			
			/**
			 * 2、如果是新用户，要添加视图
			 */
			BindUser bindUser = mAllBindUserMap.get(openId);
			if (bindUser == null){//新聊天用户
				bindUser = mWeiUserDao.getUser(openId);
				addUserColumn(bindUser, mOnLineChatUserCnt);
		        mOnLineChatUserCnt ++;
			} 
			//3、 排序
			
			
			//4、消息通知
			mMsgBoardViewMap.get(openId).receiveNewMessage(bindUser, recorder);
			
		}
	};
}
