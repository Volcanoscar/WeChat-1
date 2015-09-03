package com.tcl.wechat.action.recorder;

import java.io.IOException;

import com.tcl.wechat.action.recorder.interf.MediaPlayerImpl;
import com.tcl.wechat.action.recorder.listener.AudioPlayCompletedListener;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;

public class RecorderPlayerManager implements MediaPlayerImpl, 
		OnCompletionListener, OnErrorListener{
	
	private static final String TAG = "RecorderPlayerManager";
	
	private MediaPlayer mMediaPlayer;
	private boolean bPause;
	
	/**
	 * 播放完成监听回调
	 */
	private AudioPlayCompletedListener mListener;
	public void setPlayCompletedListener(AudioPlayCompletedListener listener){
		mListener = listener;
	}
	
	/**
	 * 单例对象
	 * @author rex.lei
	 *
	 */
	private static class RecorderPlayerManagerInstance{
		private static final RecorderPlayerManager mInstance = 
				new RecorderPlayerManager();
	}
	
	private RecorderPlayerManager() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static RecorderPlayerManager getInstance(){
		return RecorderPlayerManagerInstance.mInstance;
	}
	
	@Override
	public void play(String filePath) {
		Log.i(TAG, "start to Play sound, filePath:" + filePath);
		if (mMediaPlayer == null){
			mMediaPlayer = new MediaPlayer();
		} else {
			mMediaPlayer.reset();
		}
		
		try {
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnErrorListener(this);
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
	public void resume() {
		if (mMediaPlayer != null && bPause){
			bPause = false;
			mMediaPlayer.start();
		}
	}
	
	@Override
	public void pause() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
			bPause = true;
			mMediaPlayer.pause();
		}
		
	}
	
	@Override
	public void stop(){
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
			mMediaPlayer.stop();
		}
	}
	
	
	@Override
	public void release() {
		if (mMediaPlayer != null){
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		if (mListener != null){
			mListener.onCompleted();
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (mMediaPlayer != null){
			mMediaPlayer.reset();
		}
		return false;
	}

	
	

}
