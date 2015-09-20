package com.tcl.wechat.ui.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.action.recorder.Recorder;
import com.tcl.wechat.action.recorder.RecorderPlayerManager;
import com.tcl.wechat.action.recorder.listener.AudioPlayCompletedListener;
import com.tcl.wechat.action.recorder.listener.AudioRecorderStateListener;
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.controller.WeiXinMsgManager;
import com.tcl.wechat.controller.listener.NewMessageListener;
import com.tcl.wechat.db.WeiMsgRecordDao;
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.WeiXinMsgRecorder;
import com.tcl.wechat.modle.data.DataFileTools;
import com.tcl.wechat.ui.adapter.ChatMsgAdapter;
import com.tcl.wechat.view.AudioRecorderButton;
import com.tcl.wechat.view.UserInfoView;

/**
 * 聊天界面
 * @author rex.lei
 */
public class ChatActivity extends Activity {
	
	private static final String TAG = ChatActivity.class.getSimpleName();
	
	private Context mContext;
	
	/**
	 * 界面信息
	 */
	private UserInfoView mUserIconImg;
	private TextView mUserNameTv;
	private EditText mMsgEdt;
	private ListView mChatListView;
	private ChatMsgAdapter mAdapter;
	private AudioRecorderButton mRecorderButton;
	private View mPlaySoundAnimView; //音频播放动画View；
	
	private BindUser mSysBindUser;
	
	/**
	 * 用户信息
	 */
	private BindUser mBindUser;
	
	/**
	 * 聊天记录
	 */
	private ArrayList<WeiXinMsgRecorder> mALlUserRecorders;
	
	/**
	 * 数据信息
	 */
	private int mCurrentPosition = -1;
	
	/**
	 * 音频播放管理类
	 */
	private RecorderPlayerManager mAudioManager;
	
	/**
	 * 消息记录工具类
	 */
	private WeiMsgRecordDao mRecordDao;
	
	/**
	 * 数据工具类
	 */
	private DataFileTools mDataFileTools;
	
	/**
	 * 日期格式
	 */
	private SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
	
	private WeiXinMsgManager mWeiXinMsgManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_chat);
		
		mContext = ChatActivity.this;
		mRecordDao = WeiMsgRecordDao.getInstance();
		mAudioManager = RecorderPlayerManager.getInstance();
		mDataFileTools = DataFileTools.getInstance();
		mWeiXinMsgManager = WeiXinMsgManager.getInstance();
		
		initData();
		initView();
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		mAudioManager.resume();
	}
	
	/**
	 * 数据初始化
	 */
	private void initData() {
		
		/**
		 * 获取数据信息
		 */
		Bundle bundle = getIntent().getExtras();
		if (bundle == null){
			return ;
		}
		mBindUser = (BindUser) bundle.get("bindUser");
		mSysBindUser = WeiUserDao.getInstance().getSystemUser();
		
		if (mBindUser == null || mSysBindUser == null){
			Log.e(TAG, "BindUser Or SysBindUser is NULL !!");
			return ;
		}
		Log.i(TAG, "BindUser:" + mBindUser.toString());
	
		mALlUserRecorders = WeiMsgRecordDao.getInstance().getUserRecorder(mBindUser.getOpenId());
		
		if (mALlUserRecorders != null){
			for (WeiXinMsgRecorder recorder : mALlUserRecorders) {
				recorder.setReceived(true);
			}
			Log.i(TAG, "ALlUserRecorders:" + mALlUserRecorders.toString());
		}

		/**
		 * 增加新消息监听器
		 */
		mWeiXinMsgManager.addNewMessageListener(mNewMessageListener);
	}

	/**
	 * 控件初始化
	 */
	private void initView() {
		
		if (mALlUserRecorders == null || mALlUserRecorders.isEmpty()){
			return ;
		}
		
		/**
		 * 初始化界面
		 */
		mUserIconImg = (UserInfoView) findViewById(R.id.img_chat_usericon);
		mUserNameTv = (TextView) findViewById(R.id.tv_chat_username);
		mMsgEdt = (EditText) findViewById(R.id.edt_msg_input);
		mChatListView = (ListView) findViewById(R.id.lv_chat_info);
		mAdapter = new ChatMsgAdapter(mContext, mBindUser, mALlUserRecorders);
		mChatListView.setAdapter(mAdapter);
		mChatListView.setSelection(mALlUserRecorders.size() - 1);
		
		mRecorderButton = (AudioRecorderButton) findViewById(R.id.btn_sound_reply);

		/**
		 * 数据填充
		 */
		Bitmap userIcon = mDataFileTools.getBindUserCircleIcon(mBindUser.getHeadImageUrl());
		mUserIconImg.setUserIcon(userIcon, true);
		if (TextUtils.isEmpty(mBindUser.getRemarkName())){
			WeiUserDao.getInstance().updateRemarkName(mBindUser.getOpenId(), mBindUser.getNickName());
			mBindUser.setRemarkName(mBindUser.getNickName());
		}
		mUserNameTv.setText(mBindUser.getRemarkName());
		
		
		//监听事件
		mRecorderButton.setRecorderCompletedListener(new AudioRecorderStateListener() {
			
			@Override
			public void startToRecorder() {
				resetPlayAnim();
			}
			
			@Override
			public void onCompleted(Recorder recoder) {
				
				/**
				 * 录音完成
				 */
				
				//1、上传音频文件
				
				
				//2、更新数据库
				WeiXinMsgRecorder msgRecorder = new WeiXinMsgRecorder();
				msgRecorder.setOpenid(mBindUser.getOpenId());
				msgRecorder.setMsgtype("voice");
				msgRecorder.setContent(recoder.getFileName());
				mALlUserRecorders.add(msgRecorder);
				
				//3、更新列表
				update();
				
			}
		});
		mChatListView.setOnItemClickListener(listener);
		
		//强制隐藏Android输入法窗口
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(mMsgEdt.getWindowToken(),0);
	}
	
	private OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			if (mCurrentPosition != position){
				resetPlayAnim();
				mCurrentPosition = position;
			}
			
			WeiXinMsgRecorder recorder = mALlUserRecorders.get(position);
			String msgType = recorder.getMsgtype();
			
			if (ChatMsgType.VOICE.equals(msgType)){
				//播放动画
				mPlaySoundAnimView = view.findViewById(R.id.view_chat_recorder_info);
				if (0 == getWeiXinMsgResource(recorder)){
					mPlaySoundAnimView.setBackgroundResource(R.drawable.play_sound_left_anim);
				} else {
					mPlaySoundAnimView.setBackgroundResource(R.drawable.play_sound_right_anim);
				}
				AnimationDrawable anim = (AnimationDrawable) mPlaySoundAnimView.getBackground();
				anim.start();
				//播放音频
				//播放音频
				String filePath = mDataFileTools.getAudioFilePath(recorder.getUrl());
				mAudioManager.play(filePath);
				mAudioManager.setPlayCompletedListener(playCompletedListener);
			
			} else if (ChatMsgType.VIDEO.equals(msgType)){
				Intent intent = new Intent(mContext, VideoPlayerActivity.class);
				intent.putExtra("recoder", "recorder");
				intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				startActivity(intent);
			}
		}
	};
	
	private int getWeiXinMsgResource(WeiXinMsgRecorder recorder){
		if (recorder.getOpenid().equals(mSysBindUser.getOpenId())){//send
			return 0;
		} else { //received
			return 1;
		}
	}
	
	/**
	 * 收到新消息监听器
	 */
	private NewMessageListener mNewMessageListener = new NewMessageListener(){

		@Override
		public void onNewMessage(WeiXinMsgRecorder recorder) {
			// TODO Auto-generated method stub
			if (recorder == null){
				return ;
			}
			mALlUserRecorders.add(recorder);
			update();
		}
	};
	
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
		if (mALlUserRecorders == null || mALlUserRecorders.get(mCurrentPosition) == null){
			return ;
		}
		WeiXinMsgRecorder recorder = mALlUserRecorders.get(mCurrentPosition);
		if (recorder.isReceived()){
			mPlaySoundAnimView.setBackgroundResource(R.drawable.voice_left);
		} else {
			mPlaySoundAnimView.setBackgroundResource(R.drawable.voice_right);
		}
	}
	/**
	 * 更新聊天列表
	 */
	private void update(){
		mAdapter.notifyDataSetChanged();
		mChatListView.setSelection(mALlUserRecorders.size() -1);
	}
	
	/**
	 * 消息回复按键回调
	 * @param v
	 */
	public void msgReplyClick(View v){
		if ("".equals(mMsgEdt.getText().toString())){
			return ;
		}
		
		WeiXinMsgRecorder recorder = new WeiXinMsgRecorder();
		recorder.setOpenid(mBindUser.getOpenId());
		recorder.setMsgtype("text");
		recorder.setContent(mMsgEdt.getText().toString());
		recorder.setCreatetime(df.format(new Date()));
		recorder.setReceived(false);
		mRecordDao.addRecorder(recorder);
		
		mALlUserRecorders.add(recorder);
		mAdapter.notifyDataSetChanged();
		mChatListView.setSelection(mALlUserRecorders.size() - 1);
		mMsgEdt.setText("");
		
	}
	
	/**
	 * 表情回复按键回调
	 * @param v
	 */
	public void faceReplyClick(View v){
		if ("".equals(mMsgEdt.getText().toString())){
			return ;
		}
		
		
	}
	
	/**
	 * 图片选择按钮
	 * @param view
	 */
	public void imgReplyClick(View view){
		Intent intent = new Intent(this, PicSelectActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 左边消息回复按键回调
	 * @param v
	 */
	public void msgReplyLeftClick(View v){
		
	}
	
	/**
	 * 右边消息回复按键回调
	 * @param v
	 */
	public void msgReplyRightClick(View v){
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mAudioManager.pause();
		
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mAudioManager.stop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAudioManager.release();
		mCurrentPosition = 0;
	}

}
