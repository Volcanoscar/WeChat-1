package com.tcl.wechat.action.recorder;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.WeApplication;
import com.tcl.wechat.action.recorder.interf.MediaPlayerImpl;
import com.tcl.wechat.action.recorder.listener.AudioPlayCompletedListener;

public class RecorderPlayerManager implements MediaPlayerImpl, 
		OnCompletionListener, OnErrorListener{
	
	private static final String TAG = "RecorderPlayerManager";
	
	private String mFilePath;
	
	private AudioManager mAudioManager;
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
		mAudioManager = (AudioManager) WeApplication.getContext()
				.getSystemService(Context.AUDIO_SERVICE);
	}

	public static RecorderPlayerManager getInstance(){
		return RecorderPlayerManagerInstance.mInstance;
	}
	
	private void initPlayer(){
		
		if (mMediaPlayer == null){
			mMediaPlayer = new MediaPlayer();
		} else {
			mMediaPlayer.reset();
		}
		
		try {
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnErrorListener(this);
			mMediaPlayer.setDataSource(mFilePath);
			mMediaPlayer.prepare();
			
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
	public void play(String filePath) {
		Log.i(TAG, "start to Play sound, filePath:" + filePath);
		if (TextUtils.isEmpty(filePath)){
			return ;
		}
		
		mFilePath = filePath;
		initPlayer();
		if (requestAudioFocus()){
			mMediaPlayer.start();
		}
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
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.pause();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
				if (mMediaPlayer == null) {
					initPlayer();
				} else if (!mMediaPlayer.isPlaying()) {
					mMediaPlayer.start();
				}
			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
			} else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
				abandonAudioFocus();
			} else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
			}
		}
	};
	
	public boolean isPlaying(){
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
			return true;
		}
		return false;
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
			abandonAudioFocus();
		}
		
	}
	
	@Override
	public void stop(){
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
			mMediaPlayer.stop();
			abandonAudioFocus();
		}
	}
	
	
	@Override
	public void release() {
		if (mMediaPlayer != null){
			mMediaPlayer.release();
			mMediaPlayer = null;
			abandonAudioFocus();
		}
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		if (mListener != null){
			abandonAudioFocus();
			mListener.onCompleted();
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		abandonAudioFocus();
		if (mMediaPlayer != null){
			mMediaPlayer.reset();
		}
		if (mListener != null){
			mListener.onError(what);
		}
		return false;
	}
}
