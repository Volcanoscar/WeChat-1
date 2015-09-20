package com.tcl.wechat.view;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.action.recorder.RecorderPlayerManager;
import com.tcl.wechat.action.recorder.listener.AudioPlayCompletedListener;
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.WeiXinMsgRecorder;
import com.tcl.wechat.modle.data.DataFileTools;
import com.tcl.wechat.ui.activity.ChatActivity;
import com.tcl.wechat.utils.DateUtil;
import com.tcl.wechat.view.page.TextPageView;

/**
 * 家庭留言板视图
 * @author rex.lei
 *
 */
public class MsgBoardView extends LinearLayout implements OnClickListener{

	private static final String TAG = MsgBoardView.class.getSimpleName();
	
	private static final String DATA_FORMAT = "mm:ss";
	
	private Context mContext;
	
	private LayoutInflater mInflater;
	
	private View mView;
	
	private int mScreen = 0;
	
	/**
	 * 消息显示控件
	 */
	private TextPageView mMsgPageView;
	
	/**
	 * 用户显示控件
	 */
	private UserInfoView mUserInfoViewUv;
	
	/**
	 * 消息时间
	 */
	private TextView mMsgReceiveTimeTv;
	
	/**
	 * 回复按钮
	 */
	private Button mReplyBtn;
	
	private FrameLayout mMsgConentLayout;
	
	/**
	 * 工具类
	 */
	private DataFileTools mDataFileTools;
	
//	/**
//	 * 用户消息记录
//	 */
//	private ArrayList<WeiXinMsgRecorder> mUserRecorders;
	
	/**
	 * 用户openId 和消息openId必须匹配方可显示
	 */
	private BindUser mBindUser;
	private WeiXinMsgRecorder mRecorder;
	
	/**
	 * 日期帮助类
	 */
	private DateUtil mDateUtil;
	
	/**
	 * 音频播放播放管理类
	 */
	private RecorderPlayerManager mAudioManager;
	
	/**
	 * 视频视图类
	 */
	private View mVideoView ;
	
	/**
	 * 音频视图类
	 */
	private View mVoiceView;
	/**
	 * 音频播放动画View；
	 */
	private View mPlaySoundAnimView; 
	
	
	public MsgBoardView(Context context) {
		this(context, null);
	}
	
	public MsgBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mDataFileTools = DataFileTools.getInstance();
//		mUserRecorders = new ArrayList<WeiXinMsgRecorder>();
		mDateUtil = new DateUtil(DATA_FORMAT);
		mAudioManager = RecorderPlayerManager.getInstance();
	}
	
	public void setupView(int screen){
		mScreen = screen;
		int layoutId = mContext.getResources().getIdentifier("layout_msgboard_" + (screen + 1), 
								"layout", mContext.getPackageName());
		mView = inflate(mContext, layoutId, this);
		
		mMsgPageView = (TextPageView) mView.findViewById(R.id.tv_familboard_msg);
		mUserInfoViewUv = (UserInfoView) mView.findViewById(R.id.uv_familboard_userinfo);
		mMsgReceiveTimeTv = (TextView) mView.findViewById(R.id.tv_msgreceive_time);
		mReplyBtn = (Button) mView.findViewById(R.id.btn_familyborad_replay);
		mMsgConentLayout = (FrameLayout) mView.findViewById(R.id.layout_familboard_msg); 
		
		mUserInfoViewUv.setUserNameBackGround(false);
		mUserInfoViewUv.setTextSize(22);
		mUserInfoViewUv.setFont("fonts/oop.TTF");
		mMsgReceiveTimeTv.setTypeface(getFontTypeface("fonts/oop.TTF"));
		
		mReplyBtn.setOnClickListener(replyClick);
	}
	
	/**
	 * 添加数据
	 * @param bindUser 用户信息
	 * @param recorders 消息记录
	 */
	public void addData(BindUser bindUser, WeiXinMsgRecorder recorder){
		if (bindUser == null){
			return ;
		}
		mBindUser = bindUser;
		mRecorder = recorder;
				
		upadteView(bindUser, mRecorder);
	}
	
	private OnClickListener replyClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(mContext, ChatActivity.class);
			intent.putExtra("bindUser", mBindUser);
			mContext.startActivity(intent);
		}
	};
	
	
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
		
//		mUserRecorders.add(recorder);
		upadteView(bindUser, recorder);
	}
	
	/**
	 * 更新界面
	 * @param bindUser
	 * @param recorders
	 */
	private void upadteView(BindUser bindUser, WeiXinMsgRecorder recorder){
		if (bindUser == null || recorder == null){
			return ;
		}
		
		if (!bindUser.getOpenId().equals(recorder.getOpenid())){
			Log.w(TAG, "bindUser's Openid isn't equal Recorder's Opendid");
			return ;
		}
		
		//更新用户信息
		Bitmap userIcon = mDataFileTools.getBindUserCircleIcon(bindUser.getHeadImageUrl());
		String userName = bindUser.getRemarkName();
		
		if (userIcon != null && userName != null){
			mUserInfoViewUv.setUserIcon(userIcon, true);
			mUserInfoViewUv.setUserName(userName);
		}
		
		//更新消息信息
		Log.i(TAG, "recorder:" + recorder.toString());
		String msgType = recorder.getMsgtype();
		Log.i(TAG, "Msgtype:" + msgType);
		
		if (ChatMsgType.TEXT.equals(msgType)){
			mMsgConentLayout.removeAllViews();
			mMsgPageView.setMessageInfo(mContext, recorder);
			mMsgPageView.setVisibility(VISIBLE);
			String date = recorder.getCreatetime();
			mMsgReceiveTimeTv.setText(mDateUtil.getTime(date));
			mMsgConentLayout.addView(mMsgPageView);
			
		} else if (ChatMsgType.VOICE.equals(msgType)){
			//移除其他视图
			mMsgConentLayout.removeAllViews();
			//创建音频显示视图
			mVoiceView = mInflater.inflate(R.layout.layout_chat_voice_view, null);
			ImageButton imageBtn = (ImageButton) mVoiceView.findViewById(R.id.imgbtn_msgboard_voice);
			TextView mTimeLengthTv = (TextView) mVoiceView.findViewById(R.id.tv_voice_palytime);
			mTimeLengthTv.setText(recorder.getFileTime());
			imageBtn.setOnClickListener(this);
			//将音频视图添加进容器
			mMsgConentLayout.addView(mVoiceView);
		} else if (ChatMsgType.VIDEO.equals(msgType)){
			View mVideoView = mInflater.inflate(R.layout.layout_chat_video_view, null);
			ImageView mHumbmediaImg = (ImageView) mVideoView.findViewById(R.id.img_video_humbmedia);
			
			//设置缩略图
			
			mMsgConentLayout.addView(mVideoView);
		} else if (ChatMsgType.IMAGE.equals(msgType)){
			
		}
	}
	
	public Typeface getFontTypeface(String fontpath) {
			return Typeface.createFromAsset(getContext().getAssets(), fontpath);
	}

	@Override
	public void onClick(View v) {
		if (mRecorder == null){
			return ;
		}
		String  msgType = mRecorder.getMsgtype();
		
		if (ChatMsgType.TEXT.equals(msgType)){
			//文件点击，进行放大消息
			
		} else if (ChatMsgType.VOICE.equals(msgType)){
			
			//播放音频
			String filePath = mDataFileTools.getAudioFilePath(mRecorder.getUrl());
			Log.i(TAG, "filePath:" + filePath);
			if (!TextUtils.isEmpty(filePath) ){
				mAudioManager.play(filePath);
				mAudioManager.setPlayCompletedListener(playCompletedListener);
				
				//音频点击，进行播放动画
				mPlaySoundAnimView = mView.findViewById(R.id.view_chat_anim_view);
				mPlaySoundAnimView.setBackgroundResource(R.drawable.play_sound_left_anim);
				AnimationDrawable anim = (AnimationDrawable) mPlaySoundAnimView.getBackground();
				anim.start();
			}
		} else if (ChatMsgType.VIDEO.equals(msgType)){
			//视频点击，进行播放
			
		} else if (ChatMsgType.IMAGE.equals(msgType)){
			//图像点击，进行放大
			
		}
	}
	
	/**
	 * 音频播放完成监听器
	 */
	private AudioPlayCompletedListener playCompletedListener = new AudioPlayCompletedListener() {
		
		@Override
		public void onCompleted() {
			resetPlayAnim();
		}
	};
	
	/**
	 * 重置播放动画
	 */
	private void resetPlayAnim(){
		if (mPlaySoundAnimView == null){
			return ;
		}
		mPlaySoundAnimView.setBackgroundResource(R.drawable.voice_left);
	}
}
