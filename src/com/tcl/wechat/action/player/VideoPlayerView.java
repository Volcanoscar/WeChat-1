package com.tcl.wechat.action.player;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

/**
 * 视频播放控件
 * @author rex.lei
 *
 */
public class VideoPlayerView extends SurfaceView implements Callback{

	private static final String TAG = VideoPlayerView.class.getSimpleName();
	
	private SurfaceHolder mHolder;
	
	private VideoPlayerManager mPlayerManager;
	
	public VideoPlayerView(Context context) {
		this(context, null);
	}
	
	public VideoPlayerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public VideoPlayerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init(context);
	}

	private void init(Context context) {
		mHolder = this.getHolder();
		mHolder.addCallback(this);
		mPlayerManager = VideoPlayerManager.getInstance();
		mPlayerManager.init(mHolder);
	}
	
	/**
	 * 播放视频文件
	 * @param path 文件路径
	 */
	public void play(String path){
		mPlayerManager.play(path);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "surfaceCreated!");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i(TAG, "surfaceChanged!");
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "surfaceDestroyed!");
	}

	

	

	
	
}
