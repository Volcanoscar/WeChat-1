package com.tcl.wechat.view;

import java.io.File;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.action.recorder.RecorderAudioManager;
import com.tcl.wechat.action.recorder.RecorderPlayerManager;
import com.tcl.wechat.action.recorder.listener.AudioPlayCompletedListener;
import com.tcl.wechat.common.IConstant.ChatMsgSource;
import com.tcl.wechat.model.WeiXinMessage;

/**
 * 音频显示控件
 * @author Rex.lei
 *
 */
public class ChatMsgAudioView extends LinearLayout implements OnClickListener{

	private static final String TAG = ChatMsgAudioView.class.getSimpleName();
	
	private Context mContext;
	
	private WeiXinMessage mRecorder;
	
	private View mView;
	
	/** 音频显示最大宽度标尺*/
	private static final int CHAT_VIEW_WIDTH = 800;
	/** 最小宽度*/
	private int mMinItemWidth ;
	/** 最大宽度*/
	private int mMaxItemWidth;
	
	/** 音频播放动画View */
	private View mPlaySoundAnimView;
	/** 执行动画的时间   */
	protected long mAnimationTime = 150;
	/** 音频播放管理类  */
	private RecorderPlayerManager mAudioManager;
	
	public ChatMsgAudioView(Context context, WeiXinMessage recorder) {
		super(context);
		mContext = context;
		mRecorder = recorder;
		mAudioManager = RecorderPlayerManager.getInstance();
		setOnClickListener(this);
		
		mMinItemWidth = (int) (CHAT_VIEW_WIDTH * 0.1f);
		mMaxItemWidth = (int) (CHAT_VIEW_WIDTH * 0.8f);
		
		init(context);
		
	}

	private void init(Context context) {
		int duration  = 0;
		if (!TextUtils.isEmpty(mRecorder.getFileName())){
			File file = new File(mRecorder.getFileName());
			if (file != null && file.exists()){
				duration = (int)(Math.round(RecorderAudioManager.getDuration(file) / 1000.0 ));
			}
		}
		
		if (ChatMsgSource.RECEIVEED.equals(mRecorder.getReceived())){
			mView = inflate(context, R.layout.layout_chat_voice_leftview, this);
		} else {
			mView = inflate(context, R.layout.layout_chat_voice_rightview, this);
		}
		FrameLayout layout = (FrameLayout) mView.findViewById(R.id.layout_chat_voice);
		ViewGroup.LayoutParams lp = layout.getLayoutParams();
		lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * duration));
		if (lp.width > CHAT_VIEW_WIDTH){
			lp.width = CHAT_VIEW_WIDTH;
		}
		TextView mMsgTime = (TextView) mView.findViewById(R.id.tv_chat_recorder_time);
		
		//显示未读标识
		//if (!"2".equals(recorder.getReaded())) {
		//	Drawable unPlayFlag = mContext.getResources().getDrawable(R.drawable.unread_flag);
		//	unPlayFlag.setBounds(0, 0, 10, 10);
		//	mMsgTime.setCompoundDrawables(null, unPlayFlag, null, null);
		//}
		mMsgTime.setText(duration + "\"");
	}

	@Override
	public void onClick(View view) {
		//未读标识
		//WeiMsgRecordDao.getInstance().updatePlayState(mRecorder.getMsgid());
		//((TextView)view.findViewById(R.id.tv_chat_recorder_time)).setCompoundDrawables(null, null, null, null);

		if (mAudioManager.isPlaying()){
			mAudioManager.stop();
			resetPlayAnim(ChatMsgSource.RECEIVEED.equals(mRecorder.getReceived()));
		}
		
		//播放音频
		String filePath = mRecorder.getFileName();
		if (TextUtils.isEmpty(filePath)){
			Log.e(TAG, "filePath is NULL!!");
			return ;
		}
		
		mAudioManager.play(filePath);
		mAudioManager.setPlayCompletedListener(new AudioPlayCompletedListener() {
			
			@Override
			public void onError(int errorcode) {
				// TODO Auto-generated method stub
				resetPlayAnim(ChatMsgSource.RECEIVEED.equals(mRecorder.getReceived()));
			}
			
			@Override
			public void onCompleted() {
				// TODO Auto-generated method stub
				resetPlayAnim(ChatMsgSource.RECEIVEED.equals(mRecorder.getReceived()));
			}
		});
		
		//播放动画
		mPlaySoundAnimView = mView.findViewById(R.id.view_chat_recorder_info);
		if (ChatMsgSource.RECEIVEED.equals(mRecorder.getReceived())){
			mPlaySoundAnimView.setBackgroundResource(R.drawable.play_sound_left_anim);
		} else {
			mPlaySoundAnimView.setBackgroundResource(R.drawable.play_sound_right_anim);
		}
		AnimationDrawable anim = (AnimationDrawable) mPlaySoundAnimView.getBackground();
		anim.start();
	}
	
	/**
	 * 重置播放动画
	 */
	private void resetPlayAnim(boolean bReceive){
		if (mPlaySoundAnimView == null){
			return ;
		}
		if (bReceive){
			mPlaySoundAnimView.setBackgroundResource(R.drawable.v_left_anim3);
		} else {
			mPlaySoundAnimView.setBackgroundResource(R.drawable.v_right_anim3);
		}
	}

}
