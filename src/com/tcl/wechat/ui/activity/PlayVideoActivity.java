package com.tcl.wechat.ui.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.utils.WeixinToast;

/**
 * 视频播放器
 * @author rex.lei
 *
 */
public class PlayVideoActivity extends Activity implements OnGestureListener,
		OnClickListener, OnBufferingUpdateListener, OnCompletionListener, 
		MediaPlayer.OnPreparedListener, SurfaceHolder.Callback {
	
	private static final String TAG = PlayVideoActivity.class.getSimpleName();
	
	private static final int MSG_SHOW_CONTROLVIEW = 0x01;
	private static final int MSG_UPDATE_PROGRESS = 0x02;
	
	private static final int POP_SHOW_TIME = 3 * 1000;
	
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	
	private ImageButton mPlayImgBtn;
	private ImageButton mFastPlayImgBtn;
	private ImageButton mFastBackImgBtn;
	private View mPopuView;
	private PopupWindow mPopupWindow;
	private TextView mPlayTimeTv;
	private TextView mTotalTimeTv;
	private SeekBar mSeekBar;
	
	private int mVideoWidth;
	private int mVideoHeight;
	private int mScreenWidth;
	private int mScreenHeight;
	
	private String mFilePath;
	/** 拖动进度条 **/
	private int mVideoLength;
	/** 播放位置*/
	private int mPlayPosition ; 
	/** 视频宽高比*/
	private float mScreenRatio ;
	
	boolean mFlag = true;
	private boolean bPopVisiableFlag;
	
	private MediaPlayer mMediaPlayer;
	private AudioManager mAudioManager;
	private RefreashTimeThread mRefreashThread; 
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_player);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		mScreenRatio = mScreenWidth / (float)mScreenHeight;
		
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		initData();
		initView();
	}
	
	private void initData() {
		try {
			mFilePath = getIntent().getExtras().getString("FilePath");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initView() {
		
		mPopuView = getLayoutInflater().inflate(R.layout.popuwindow, null);
		mSeekBar = (SeekBar) mPopuView.findViewById(R.id.seekbar);
		mPlayTimeTv = (TextView) mPopuView.findViewById(R.id.tv_playtime);
		mTotalTimeTv = (TextView) mPopuView.findViewById(R.id.tv_totaltime);
		mPlayImgBtn = (ImageButton) mPopuView.findViewById(R.id.imgbtn_play);
		mFastBackImgBtn = (ImageButton) mPopuView.findViewById(R.id.imgbtn_fastback);
		mFastPlayImgBtn = (ImageButton) mPopuView.findViewById(R.id.imgbtn_fastplay);
		mPopupWindow = new PopupWindow(mPopuView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		mPlayImgBtn.setOnClickListener(this);
		mFastBackImgBtn.setOnClickListener(this);
		mFastPlayImgBtn.setOnClickListener(this);
		
		mSurfaceView.setOnTouchListener(onTouchListener);
		mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private OnTouchListener onTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				if (!bPopVisiableFlag) {
					mPopupWindow.showAtLocation(mPopuView, Gravity.BOTTOM, 0, 0);
					bPopVisiableFlag = true;
					mHandler.removeMessages(MSG_SHOW_CONTROLVIEW);
					mHandler.sendEmptyMessageDelayed(MSG_SHOW_CONTROLVIEW, POP_SHOW_TIME);
				} else {
					mHandler.removeMessages(MSG_SHOW_CONTROLVIEW);
					hidePopWindow();
				}
			}
			return true;
		}
	};
	
	private OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			mVideoLength = seekBar.getProgress();
			mMediaPlayer.seekTo(mVideoLength);
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SHOW_CONTROLVIEW:
				hidePopWindow();
				break;
				
			case MSG_UPDATE_PROGRESS:
				if (mMediaPlayer != null) {
					mPlayPosition = mMediaPlayer.getCurrentPosition();
				}
				mSeekBar.setProgress(mPlayPosition);
				mPlayPosition = mPlayPosition / 1000;
				int minute = mPlayPosition / 60;
				//int hour = minute / 60;
				int second = mPlayPosition % 60;
				minute = minute % 60;
				mPlayTimeTv.setText(String.format(getString(R.string.play_time), /*hour, */minute, second));
				break;
				
			default:
				break;
			}
		};
	};
	
	/**
	 * 初始化播放器
	 */
	private void initPlayer(){
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDisplay(mSurfaceHolder);
			mMediaPlayer.setDataSource(mFilePath);
			mMediaPlayer.setOnBufferingUpdateListener(this);
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.prepare();
			
			mSeekBar.setMax(mMediaPlayer.getDuration());
			mRefreashThread = new RefreashTimeThread();
			mRefreashThread.start();
			int duration = mMediaPlayer.getDuration();// 获得持续时间
			mSeekBar.setMax(duration);
			duration = duration / 1000;
			int m = duration / 60;
			//int h = m / 60;
			int s = duration % 60;
			m = m % 60;
			mTotalTimeTv.setText(String.format(getString(R.string.total_time),/* h, */m, s));

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void hidePopWindow(){
		if (mPopupWindow.isShowing() && mFlag) {
			mPopupWindow.dismiss();
			bPopVisiableFlag = false;
		}
	}
	
	private class RefreashTimeThread extends Thread {

		@Override
		public void run() {
			mHandler.sendEmptyMessageDelayed(MSG_SHOW_CONTROLVIEW, POP_SHOW_TIME);
			while (mFlag) {
				try {
					sleep(1000);
					mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		initPlayer();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 请求音频焦点
	 */
	private boolean requestAudioFocus(){
		int result = mAudioManager.requestAudioFocus(afChangeListener,
				AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			return true;
		}
		return false;
	}
	
	/**
	 * 释放音频焦点
	 */
	private void abandonAudioFocus(){
		if (mAudioManager!= null){
			mAudioManager.abandonAudioFocus(afChangeListener);
		}
	}
	
	private OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
				if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
					mMediaPlayer.pause();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
				if (mMediaPlayer == null) {
					initPlayer();
				} else if (!mMediaPlayer.isPlaying()) {
					mMediaPlayer.start();
				}
			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
				if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
				abandonAudioFocus();
			} else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
			} else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
				if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
			}
		}
	};

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mVideoWidth = mMediaPlayer.getVideoWidth();
		mVideoHeight = mMediaPlayer.getVideoHeight();
		
		//视频宽高比
		float ratio = mVideoWidth / (float)mVideoHeight;
		if (ratio > mScreenRatio) {
			mVideoWidth = mScreenWidth;
			mVideoHeight = (int) (mScreenWidth * ratio);
		} else {
			mVideoHeight = mScreenHeight;
			mVideoWidth = (int) (mScreenHeight * ratio);
		}
		Log.i(TAG, "VideoSize:[" + mVideoWidth + "," + mVideoHeight + 
				"],ScreenSize:[" + mScreenWidth + "," + mScreenHeight + 
				"],LayoutSize:[" + mVideoWidth + "," + mVideoHeight + "]");
		LayoutParams params = new LayoutParams(mVideoWidth, mVideoHeight);
		params.gravity = Gravity.CENTER;
		mSurfaceView.setLayoutParams(params);
		
		//mMediaPlayer.start();
		//请求音频焦点
		if (requestAudioFocus()) {
			mMediaPlayer.start();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		WeixinToast.makeText(R.string.play_complete).show();
		finish();
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.imgbtn_fastback:
			int backPosition = mMediaPlayer.getCurrentPosition() - 3000;
			mMediaPlayer.seekTo(backPosition);
			mSeekBar.setProgress(backPosition);
			mHandler.removeMessages(MSG_SHOW_CONTROLVIEW);
			mHandler.sendEmptyMessageDelayed(MSG_SHOW_CONTROLVIEW, POP_SHOW_TIME);
			break;
			
		case R.id.imgbtn_fastplay:
			int position = mMediaPlayer.getCurrentPosition() + 3000;
			mMediaPlayer.seekTo(position);
			mSeekBar.setProgress(position);
			mHandler.removeMessages(MSG_SHOW_CONTROLVIEW);
			mHandler.sendEmptyMessageDelayed(MSG_SHOW_CONTROLVIEW, POP_SHOW_TIME);
			break;
			
		case R.id.imgbtn_play:
			if (mMediaPlayer.isPlaying()) {
				mPlayImgBtn.setImageResource(R.drawable.pause);
				mMediaPlayer.pause();
				abandonAudioFocus();
			} else {
				mPlayImgBtn.setImageResource(R.drawable.play);
				if (requestAudioFocus()){
					mMediaPlayer.start();
				}
			}
			mHandler.removeMessages(MSG_SHOW_CONTROLVIEW);
			mHandler.sendEmptyMessageDelayed(MSG_SHOW_CONTROLVIEW, POP_SHOW_TIME);
			break;
			
		default:
			break;
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mMediaPlayer.isPlaying()) {
			mPlayImgBtn.setImageResource(R.drawable.pause);
			mMediaPlayer.pause();
			abandonAudioFocus();
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
			mPopupWindow.dismiss();
			mFlag = false;
			mHandler.removeMessages(MSG_UPDATE_PROGRESS);
		}
		if (mRefreashThread != null){
			mRefreashThread.interrupt();
			mRefreashThread = null;
		}
		abandonAudioFocus();
	}
}
