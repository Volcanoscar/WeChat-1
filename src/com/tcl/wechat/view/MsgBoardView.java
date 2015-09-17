package com.tcl.wechat.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.WeiXinMsgRecorder;
import com.tcl.wechat.modle.data.DataFileTools;
import com.tcl.wechat.view.page.TextPageView;

/**
 * 家庭留言板
 * @author rex.lei
 *
 */
public class MsgBoardView extends LinearLayout{

	private static final String TAG = MsgBoardView.class.getSimpleName();
	
	private Context mContext;
	
	private View mView;
	
	private int mScreen = 0;
	
	private TextPageView mMsgPageViewPg;
	
	private UserInfoView mUserInfoViewUv;
	
	private TextView mMsgReceiveTimeTv;
	
	private DataFileTools mDataFileTools;
	
	private ArrayList<WeiXinMsgRecorder> mUserRecorders;
	
	public MsgBoardView(Context context) {
		this(context, null);
	}
	
	public MsgBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mDataFileTools = DataFileTools.getInstance();
	}
	
	public void setupView(int screen){
		mScreen = screen;
		int layoutId = mContext.getResources().getIdentifier("layout_msgboard_" + (screen + 1), 
								"layout", mContext.getPackageName());
		mView = inflate(mContext, layoutId, this);
		
		mMsgPageViewPg = (TextPageView) mView.findViewById(R.id.tv_familboard_msg);
		mUserInfoViewUv = (UserInfoView) mView.findViewById(R.id.uv_familboard_userinfo);
		mMsgReceiveTimeTv = (TextView) mView.findViewById(R.id.tv_msgreceive_time);
	}
	
	/**
	 * 添加数据
	 * @param bindUser 用户信息
	 * @param recorders 消息记录
	 */
	public void addData(BindUser bindUser, ArrayList<WeiXinMsgRecorder> recorders){
		if (bindUser == null){
			return ;
		}
		mUserRecorders = recorders;
		
		int size = recorders.size();
		WeiXinMsgRecorder laststRecorder = recorders.get(size -1);
		
		upadteView(bindUser, laststRecorder);
	}
	
	/**
	 * 收到信消息
	 * @param recorder
	 */
	public void receiveNewMessage(BindUser bindUser ,WeiXinMsgRecorder recorder){
		if (bindUser == null || recorder == null){
			return ;
		}
		
		Log.d(TAG, "receiveNewMessage:" + bindUser.toString());
		Log.d(TAG, "receiveNewMessage:" + recorder.toString());
		
		mUserRecorders.add(recorder);
		upadteView(bindUser, recorder);
	}
	
	/**
	 * 更新界面
	 * @param bindUser
	 * @param recorders
	 */
	private void upadteView(BindUser bindUser, WeiXinMsgRecorder recorder){
		//更新用户信息
		Bitmap userIcon = mDataFileTools.getBindUserCircleIcon(bindUser.getHeadImageUrl());
		String userName = bindUser.getRemarkName();
		mUserInfoViewUv.setUserIcon(userIcon);
		mUserInfoViewUv.setUserName(userName);
		
		//更新消息信息
		if (recorder == null ){
			return ;
		}
		
		String msgType = recorder.getMsgtype();
		
		if (ChatMsgType.TEXT.equals(msgType)){
			
			mMsgPageViewPg.setMessageInfo(mContext, recorder.getContent());
			mMsgReceiveTimeTv.setText(recorder.getCreatetime());
		} else if (ChatMsgType.VOICE.equals(msgType)){
			 
		} else if (ChatMsgType.VIDEO.equals(msgType)){
			
		} else if (ChatMsgType.IMAGE.equals(msgType)){
			
		}
	}

}
