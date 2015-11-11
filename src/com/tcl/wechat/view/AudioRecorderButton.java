package com.tcl.wechat.view;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.tcl.wechat.R;
import com.tcl.wechat.action.recorder.Recorder;
import com.tcl.wechat.action.recorder.RecorderAudioManager;
import com.tcl.wechat.action.recorder.RecorderDialogManager;
import com.tcl.wechat.action.recorder.RecorderPlayerManager;
import com.tcl.wechat.action.recorder.listener.AudioPrepareCompletedListener;
import com.tcl.wechat.action.recorder.listener.AudioRecorderStateListener;
import com.tcl.wechat.utils.DataFileTools;

/**
 * 录音按钮
 * @author rex.lei
 *
 */

public class AudioRecorderButton extends Button implements AudioPrepareCompletedListener{
	

	private static final int DISTANCE_Y_CANCEL = 50;
	/**
	 * 按钮录音状态
	 */
	private static final int STATE_NORMAL = 1;			// 默认正常状态
    private static final int STATE_RECORDING = 2;		// 正在录音
    private static final int STATE_WANT_TO_CANCEL = 3;	// 希望取消
    
    /**
     * 消息类型
     */
    private static final int MSG_AUDIO_PREPARED = 0x100;//Audio准备完成
    private static final int MSG_VOICE_CHANGED = 0x101; //音量改变
    private static final int MSG_DIALOG_DIMISS = 0x102; //隐藏Dialog
    
    /**
     * 状态属性值
     */
    private int mCurrentState = STATE_NORMAL; 	// 当前的状态
    private boolean bRecording = false;			// 已经开始录音
    private boolean bReady;						// 是否触发longClick
    private float mRecorderTime; 				//录音时间
    
    private RecorderDialogManager mDialogManager;
    private RecorderAudioManager mAudioManager;
    
    private AudioRecorderStateListener mListener;
    public void setRecorderCompletedListener(AudioRecorderStateListener listener){
    	mListener = listener;
    }
	
	public AudioRecorderButton(Context context) {
		super(context, null);
	}
	
	public AudioRecorderButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mDialogManager = new RecorderDialogManager(getContext());
		mAudioManager = RecorderAudioManager.getInstance();
		
		mAudioManager.setFilePath(DataFileTools.getInstance().getRecordAudioPath());
		mAudioManager.setAudioStateListener(this);
		
		setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				bReady = true;
				mAudioManager.prepare();
				if (mListener != null){
					mListener.startToRecorder();
				}
				return false;
			}
		});
	}
	
	@Override
	public void prepareCompleted() {
		mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
	}
	
	
	private Runnable updateVoiceRunnable = new Runnable() {
		
		@Override
		public void run() {
			while (bRecording){
				try {
					Thread.sleep(100);
					mRecorderTime += 0.1f;
					/*int voiceLevel = mAudioManager.getLevel();
					Message msg = new Message();
					msg.what = MSG_VOICE_CHANGED;
					msg.arg1 = voiceLevel ;
					mHandler.sendMessage(msg);*/
					mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	/**
	 * 更新音量大小
	 */
	private void updateVoiceLevel(){
		new Thread(updateVoiceRunnable).start();
	}
	
	/**
	 * 消息处理
	 */
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_AUDIO_PREPARED:
				bRecording = true;
				mDialogManager.show();
				updateVoiceLevel();
				break;
				
			case MSG_VOICE_CHANGED:
				mDialogManager.updateVoiceLevle(mAudioManager.getLevel());
				break;
				
			case MSG_DIALOG_DIMISS:
				mDialogManager.dismiss();
				break;
	
			default:
				break;
			}
		};
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int action = event.getAction();
		int x = (int) event.getX();// 获得x轴坐标
        int y = (int) event.getY();// 获得y轴坐标
        
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			changeState(STATE_RECORDING);
			break;
			
		case MotionEvent.ACTION_MOVE:
			if (bRecording){
				// 如果想要取消，根据x,y的坐标看是否需要取消
                if (wantToCancle(x, y)) {
                    changeState(STATE_WANT_TO_CANCEL);
                } else {
                    changeState(STATE_RECORDING);
                }
			}
			break;
			
		case MotionEvent.ACTION_UP:
			if (!bReady){
				reset();
				return super.onTouchEvent(event);
			}
			if (!bRecording || mRecorderTime < 0.6){
				
				mDialogManager.tooShort();
				mAudioManager.cancel();
				mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
				
			}else if (mCurrentState == STATE_RECORDING){
				mDialogManager.dismiss();
				mAudioManager.release();
				
				if (mListener != null){
					File file = new File(mAudioManager.getCurrentPath());
					int duration = (int)(Math.ceil(RecorderAudioManager.getDuration(file) / 1000.0 ));
					mListener.onCompleted(new Recorder(mAudioManager.getCurrentPath(), duration, file.length()));
				}
			} else if (mCurrentState == STATE_WANT_TO_CANCEL){
				
				mDialogManager.dismiss();
				mAudioManager.cancel();
			}
			reset();
			break;

		default:
			break;
		}
		
		return super.onTouchEvent(event);
	}

	/**
	 * 恢复状态及标志位
	 */
	private void reset() {
		bReady = false;
		bRecording = false;
		mRecorderTime = 0;
		mHandler.removeMessages(MSG_VOICE_CHANGED);
		changeState(STATE_NORMAL);
	}

	/**
	 * 是否取消录音
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean wantToCancle(int x, int y) {
		if (x < 0 || x > getWidth()){
			return true;
		}
		if (y < - DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL){
			return true;
		}
		return false;
	}

	/**
	 * 改变Button录音状态
	 * @param state
	 */
	private void changeState(int state) {
		if (mCurrentState != state){
			mCurrentState = state;
			switch (state) {
			case STATE_NORMAL:
				setBackgroundResource(R.drawable.msg_sound_reply);
				break;
				
			case STATE_RECORDING:
				setBackgroundResource(R.drawable.msg_sound_reply_pressed);
				if (bRecording){
					mDialogManager.recording();
					if (RecorderPlayerManager.getInstance().isPlaying()){
						//暂停正在播放的音频文件
						RecorderPlayerManager.getInstance().stop();
					}
				}
				break;
				
			case STATE_WANT_TO_CANCEL:
				setBackgroundResource(R.drawable.msg_sound_reply_pressed);
				mDialogManager.wanToCancel();
				break;

			default:
				break;
			}
		}
	}

}
