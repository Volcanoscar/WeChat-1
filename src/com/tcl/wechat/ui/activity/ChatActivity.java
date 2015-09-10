package com.tcl.wechat.ui.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.tcl.wechat.R;
import com.tcl.wechat.action.recorder.Recorder;
import com.tcl.wechat.action.recorder.RecorderPlayerManager;
import com.tcl.wechat.action.recorder.listener.AudioPlayCompletedListener;
import com.tcl.wechat.action.recorder.listener.AudioRecorderStateListener;
import com.tcl.wechat.modle.ChatMessage;
import com.tcl.wechat.modle.ChatMsgSource;
import com.tcl.wechat.modle.ChatMsgType;
import com.tcl.wechat.ui.adapter.ChatMsgAdapter;
import com.tcl.wechat.view.AudioRecorderButton;

/**
 * 聊天界面
 * @author rex.lei
 */
public class ChatActivity extends Activity {
	
	private Context mContext;
	
	/**
	 * 界面信息
	 */
	private EditText mMsgEdt;
	private ListView mChatListView;
	private ChatMsgAdapter mAdapter;
	private AudioRecorderButton mRecorderButton;
	private View mPlaySoundAnimView; //音频播放动画View；
	
	/**
	 * 数据信息
	 */
	private ArrayList<ChatMessage> mDatas;
	private int mCurrentPosition = -1;
	
	private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
	
	private RecorderPlayerManager mPlayerManager;
	
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
		mPlayerManager = RecorderPlayerManager.getInstance();
		
		initData();
		initView();
		
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		mPlayerManager.resume();
	}
	
	/**
	 * 数据初始化
	 */
	private void initData() {
		mDatas = new ArrayList<ChatMessage>();
		
		for (int i = 0; i < 5; i++) {
			ChatMessage imageMessage = new ChatMessage();
			imageMessage.setUserName("Rex");
			imageMessage.setMessage("[Android自定义控件] Android自定义控件");
			imageMessage.setTime(df.format(new Date()));
			if (i % 2 == 0){
				imageMessage.setSource(ChatMsgSource.SRC_SENDED);
			} else {
				imageMessage.setSource(ChatMsgSource.SRC_RECEVIED);
			}
			imageMessage.setType(ChatMsgType.TYPE_TEXT);
			mDatas.add(imageMessage);
		}
		
		for (int i = 0; i < 5; i++) {
			ChatMessage imageMessage = new ChatMessage();
			imageMessage.setUserName("Rex");
			
			imageMessage.setTime(df.format(new Date()));
			if (i % 2 == 0){
				imageMessage.setMessage(BitmapFactory.decodeResource(getResources(), R.drawable.biaoqing_012));
				imageMessage.setSource(ChatMsgSource.SRC_SENDED);
			} else {
				imageMessage.setMessage(BitmapFactory.decodeResource(getResources(), R.drawable.baioqing_013));
				imageMessage.setSource(ChatMsgSource.SRC_RECEVIED);
			}
			imageMessage.setType(ChatMsgType.TYPE_IAMGE);
			mDatas.add(imageMessage);
		}
		
		//TODO 需要添加用户名
		ChatMessage message1 = new ChatMessage();
		message1.setMessage(new Recorder("", 25));
		message1.setTime(df.format(new Date()));
		message1.setSource(ChatMsgSource.SRC_SENDED);
		message1.setType(ChatMsgType.TYPE_AUDIO);
		mDatas.add(message1);
		
		//TODO 需要添加用户名
		ChatMessage message2 = new ChatMessage();
		message2.setMessage(new Recorder("", 50));
		message2.setTime(df.format(new Date()));
		message2.setSource(ChatMsgSource.SRC_RECEVIED);
		message2.setType(ChatMsgType.TYPE_AUDIO);
		mDatas.add(message2);
		
		//视频
		ChatMessage videoMsg = new ChatMessage();
		videoMsg.setMessage(R.drawable.message_video_bg);
		videoMsg.setTime(df.format(new Date()));
		videoMsg.setSource(ChatMsgSource.SRC_RECEVIED);
		videoMsg.setType(ChatMsgType.TYPE_VIDEO);
		mDatas.add(videoMsg);
		
		ChatMessage imgMsg = new ChatMessage();
		imgMsg.setMessage(BitmapFactory.decodeResource(getResources(), R.drawable.chat_usericon));
		imgMsg.setTime(df.format(new Date()));
		imgMsg.setSource(ChatMsgSource.SRC_SENDED);
		imgMsg.setType(ChatMsgType.TYPE_IAMGE);
		mDatas.add(imgMsg);
		
	}

	/**
	 * 控件初始化
	 */
	private void initView() {
		mMsgEdt = (EditText) findViewById(R.id.edt_msg_input);
		mChatListView = (ListView) findViewById(R.id.lv_chat_info);
		mAdapter = new ChatMsgAdapter(mContext, mDatas);
		mChatListView.setAdapter(mAdapter);
		mChatListView.setSelection(mDatas.size() - 1);
		
		mRecorderButton = (AudioRecorderButton) findViewById(R.id.btn_sound_reply);
		
		//监听事件
		mRecorderButton.setRecorderCompletedListener(new AudioRecorderStateListener() {
			
			@Override
			public void startToRecorder() {
				resetPlayAnim();
			}
			
			@Override
			public void onCompleted(Recorder recoder) {
				ChatMessage message = new ChatMessage();
				
				//TODO 需要添加用户名
				message.setMessage(recoder);
				message.setTime(df.format(new Date()));
				message.setSource(ChatMsgSource.SRC_SENDED);
				message.setType(ChatMsgType.TYPE_AUDIO);
				mDatas.add(message);
				update();
				
			}
		});
		mChatListView.setOnItemClickListener(listener);
	}
	
	private OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			if (mCurrentPosition != position){
				resetPlayAnim();
				mCurrentPosition = position;
			}
			
			ChatMessage message = mDatas.get(position);
			
			if (ChatMsgType.TYPE_AUDIO == message.getType()){
				//播放动画
				mPlaySoundAnimView = view.findViewById(R.id.view_chat_recorder_info);
				if (ChatMsgSource.SRC_RECEVIED == message.getSource()){
					mPlaySoundAnimView.setBackgroundResource(R.drawable.play_sound_left_anim);
				} else {
					mPlaySoundAnimView.setBackgroundResource(R.drawable.play_sound_right_anim);
				}
				AnimationDrawable anim = (AnimationDrawable) mPlaySoundAnimView.getBackground();
				anim.start();
				//播放音频
				mPlayerManager.play(((Recorder)mDatas.get(mCurrentPosition).getMessage()).getFileName());
				mPlayerManager.setPlayCompletedListener(playCompletedListener);
			} else if (ChatMsgType.TYPE_VIDEO == message.getType()) {
				Intent intent = new Intent(mContext, VideoPlayerActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				startActivity(intent);
			}
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
		if (mDatas == null || mDatas.get(mCurrentPosition) == null){
			return ;
		}
		if (ChatMsgSource.SRC_RECEVIED == mDatas.get(mCurrentPosition).getSource()){
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
		mChatListView.setSelection(mDatas.size() -1);
	}
	
	/**
	 * 消息回复按键回调
	 * @param v
	 */
	public void msgReplyClick(View v){
		if ("".equals(mMsgEdt.getText().toString())){
			return ;
		}
		
		ChatMessage message = new ChatMessage();
		message.setUserName("Rex");
		message.setMessage(mMsgEdt.getText().toString());
		message.setTime(df.format(new Date()));
		message.setSource(ChatMsgSource.SRC_SENDED);
		message.setType(ChatMsgType.TYPE_TEXT);
		mDatas.add(message);
		mAdapter.notifyDataSetChanged();
		mChatListView.setSelection(mDatas.size() - 1);
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
		
		ChatMessage message = new ChatMessage();
		message.setUserName("Rex");
		message.setMessage(mMsgEdt.getText().toString());
		message.setTime(df.format(new Date()));
		message.setSource(ChatMsgSource.SRC_SENDED);
		message.setType(ChatMsgType.TYPE_IAMGE);
		mDatas.add(message);
		mAdapter.notifyDataSetChanged();
		mChatListView.setSelection(mDatas.size() - 1);
		
		mMsgEdt.setText("");
	}
	
	public void imgReplyClick(View view){
		
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
		mPlayerManager.pause();
		
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mPlayerManager.stop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPlayerManager.release();
		mCurrentPosition = 0;
	}

}
