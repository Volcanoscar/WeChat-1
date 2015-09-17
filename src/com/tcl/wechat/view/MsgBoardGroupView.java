package com.tcl.wechat.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.tcl.wechat.R;
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
	
	private View mMainView;
	
	/**
	 * 聊天用户列表
	 */
	private ArrayList<BindUser> mAllBindUsers;
	
	/**
	 * 所有消息记录
	 */
	private ArrayList<WeiXinMsgRecorder> mAllMsgRecords;
	
	/**
	 * 消息分类
	 */
	private HashMap<String, ArrayList<WeiXinMsgRecorder>> mMsgRecorderMap;
	
	private final int USER_TAB_WIDTH = 200;

	private final int USER_TAB_HEIGHT = 200;
	
	private WeiXinMsgManager mWeiXinMsgManager;
	
	private HashMap<String, BindUser> mBindUserMap;
	
	private HashMap<String, MsgBoardView> mMsgBoardViewMap;
	
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
		
		mAllBindUsers = new ArrayList<BindUser>();
		mMsgRecorderMap = new HashMap<String, ArrayList<WeiXinMsgRecorder>>();
		mBindUserMap = new HashMap<String, BindUser>();
		mMsgBoardViewMap = new HashMap<String, MsgBoardView>();
		mAllMsgRecords = mWeiMsgRecordDao.getAllUserRecorder();
		
		//加载视图
		mMainView = inflate(context, R.layout.layout_main_friend_group, this);
		loadMsgBoardData();
		
		//设置监听器
		mWeiXinMsgManager.addNewMessageListener(mNewMsgListener);
	}
	
	/**
	 * 加载用户聊天数据
	 */
	private void loadMsgBoardData(){
		
		if (mAllMsgRecords == null && mAllMsgRecords.isEmpty()){
			return ;
		}
		
		for (WeiXinMsgRecorder recorder : mAllMsgRecords) {
			if (!mAllBindUsers.contains(recorder)){
				BindUser bindUser = mWeiUserDao.getUser(recorder.getOpenid());
				mAllBindUsers.add(bindUser);
				mBindUserMap.put(recorder.getOpenid(), bindUser);
			}
		}
		
		if (mAllBindUsers == null && mAllBindUsers.isEmpty()){
			return ;
		}
		
		//反转，最新用户放在第一位
		Collections.reverse(mAllBindUsers);
		int userCount = mAllBindUsers.size();
		for (int i = 0; i < userCount; i++) {
			BindUser bindUser = mAllBindUsers.get(i);
			String openid = bindUser.getOpenId();
			mMsgRecorderMap.put(openid, mWeiMsgRecordDao.getUserRecorder(openid));

			addUserColumn(bindUser, i);
		}
	}
	
	private void addUserColumn(BindUser bindUser, int position){
		if (bindUser == null){
			return;
		}
		int width = DensityUtil.dip2px(mContext, USER_TAB_WIDTH);
        int height = DensityUtil.dip2px(mContext,USER_TAB_HEIGHT);
        
        View view = setUpView(bindUser, position + 1);
        attachChildViewToParent(view, position, width, height, 100 , 100);
        
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
		childView.addData(bindUser, mMsgRecorderMap.get(bindUser.getOpenId()));
		
		childView.setupView(position % 4);
		
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
		params.leftMargin = leftMargin;
		params.topMargin = topMargin;
        
		addView(view, position, params);
	}
	
	private NewMessageListener mNewMsgListener = new NewMessageListener() {
		
		@Override
		public void onNewMessage(WeiXinMsgRecorder recorder) {
			Log.i(TAG, "receive new message!");
			String msgType = recorder.getMsgtype();
			
			Log.i(TAG, "WeiXinMsgRecorder：" + recorder.toString());
			
			MsgBoardView msgBoardView = mMsgBoardViewMap.get(recorder.getOpenid());
			BindUser bindUser = mBindUserMap.get(recorder.getOpenid());
			msgBoardView.receiveNewMessage(bindUser, recorder);
		}
	};
}
