package com.tcl.wechat.action.player;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import com.tcl.wechat.action.player.interf.MediaPlayerImpl;
import com.tcl.wechat.action.player.listener.PlayStateListener;

/**
 * 视频播放管理类
 * @author rex.lei
 *
 */
public class VideoPlayManager implements MediaPlayerImpl, 
		OnPreparedListener, OnCompletionListener, OnErrorListener{
	
	private static final String TAG = "VideoPlayManager";
	
	private static VideoPlayManager mInstance;
	
	private boolean bPause = false;
	private MediaPlayer mPlayer;
	private SurfaceHolder mHolder;
	
	private PlayStateListener mListener;
	public void setPlayStateListener(PlayStateListener listener){
		mListener = listener;
	}
	
	private VideoPlayManager() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static VideoPlayManager getInstance(){
		if (mInstance == null){
			synchronized (VideoPlayManager.class) {
				if (mInstance == null){
					mInstance = new VideoPlayManager();
				}
			}
		}
		return mInstance;
	}

	/**
	 * 初始化
	 */
	public void initMediaPlayer(SurfaceHolder holder){
		mHolder = holder;
	}
	
	public boolean isPlaying(){
		if (mPlayer != null && mPlayer.isPlaying()){
			return true;
		}
		return false;
	}
	
	public boolean isPause(){
		return bPause;
	}
	
	@Override
	public void play(String filePath) {
		if (mHolder == null){
			Log.e(TAG, "ERROR ! Please initialize MediaPlayer first!! ");
			return ;
		}
		if (TextUtils.isEmpty(filePath)){
			Log.e(TAG, "File path is NULL!!");
			return ;
		}
		if (mPlayer == null){
			mPlayer = new MediaPlayer();
		} else {
			mPlayer.reset();
		}
		try {
			mPlayer.setDisplay(mHolder);
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.setDataSource(filePath);
			mPlayer.setOnPreparedListener(this);
			mPlayer.setOnCompletionListener(this);
			mPlayer.setOnErrorListener(this);
			mPlayer.prepare();
			mPlayer.start();
			
			bPause = false;
			if (mListener != null){
				mListener.startToPlay();
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start(){
		if (mPlayer != null){
			bPause = false;
			mPlayer.start();
		}
	}
	
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		if (mPlayer != null && mPlayer.isPlaying()){
			bPause = true;
			mPlayer.pause();
		}
	}

	@Override
	public void stop() {
		if (mPlayer != null){
			mPlayer.stop();
		}
	}

	@Override
	public void release() {
		if(mPlayer != null){
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		if (mPlayer != null){
			mPlayer.stop();
			mPlayer.reset();
		}
		if (mListener != null){
			mListener.onError(-1);
		}
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		bPause = false;
		if (mListener != null){
			mListener.onPlayCompletion();
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		
	}

}
