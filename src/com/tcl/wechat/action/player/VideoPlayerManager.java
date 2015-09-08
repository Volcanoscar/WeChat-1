package com.tcl.wechat.action.player;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import com.tcl.wechat.action.player.interf.MediaPlayerImpl;
import com.tcl.wechat.action.player.listener.MediaPlayListener;

public class VideoPlayerManager implements MediaPlayerImpl, OnCompletionListener, 
	OnErrorListener, OnBufferingUpdateListener{

	private static final String TAG = "VideoPlayerManager";
	
	private static VideoPlayerManager mInstance;
	
	private MediaPlayer mMediaPlayer;
	private SurfaceHolder mHolder;
	
	private MediaPlayListener mListener;
	public void setOnCompletionListener(MediaPlayListener listener){
		mListener = listener;
	}
	
	private VideoPlayerManager() {
		super();
	}

	public static VideoPlayerManager getInstance(){
		if (mInstance == null){
			synchronized (VideoPlayerManager.class) {
				mInstance = new VideoPlayerManager();
			}
		}
		return mInstance;
	}
	
	public void init(SurfaceHolder holder) {
		mHolder = holder;
	}
	
	@Override
	public void play(String filePath) {
		if (TextUtils.isEmpty(filePath)){
			return ;
		}
		if (mHolder == null){
			Log.e(TAG, "Play failed, because surfaceHolder is NULL!!");
			return ;
		}
		if (mMediaPlayer == null){
			mMediaPlayer = new MediaPlayer();
		} else {
			mMediaPlayer.reset();
		}
		try {
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setDisplay(mHolder);
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnErrorListener(this);
			mMediaPlayer.setOnBufferingUpdateListener(this);
			mMediaPlayer.setDataSource(filePath);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
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


	@Override
	public void pause() {
		if (mMediaPlayer != null){
			mMediaPlayer.pause();
		}
		
	}

	@Override
	public void stop() {
		if (mMediaPlayer != null){
			mMediaPlayer.stop();
		}
	}

	@Override
	public void release() {
		if (mMediaPlayer != null){
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (mMediaPlayer != null){
			mMediaPlayer.reset();
		}
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if (mListener != null){
			mListener.onPlayCompletion();
		}
	}
}
