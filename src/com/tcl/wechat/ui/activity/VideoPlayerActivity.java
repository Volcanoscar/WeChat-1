package com.tcl.wechat.ui.activity;

import java.io.File;
import java.util.concurrent.Semaphore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;

import com.tcl.wechat.R;
import com.tcl.wechat.action.player.DownloadManager;
import com.tcl.wechat.action.player.VideoPlayManager;
import com.tcl.wechat.action.player.listener.DownloadStateListener;
import com.tcl.wechat.action.player.listener.PlayStateListener;
import com.tcl.wechat.modle.data.DataFileTools;

/**
 * 视频播放控件
 * @author rex.lei
 *
 */
@SuppressLint("NewApi")
public class VideoPlayerActivity extends Activity implements SurfaceHolder.Callback{

	private static final String TAG = "VideoPlayerActivity";
	
	private static final int MSG_START_PLAY = 1;
	private static final int MSG_STOP_PLAY = 2;
	
	private SurfaceView mSurfaceView;
	private SurfaceHolder mHolder;
	private Button mPlayBtn;
	private ProgressBar mDownloadBar;
	
	private String mFilePath;
	private boolean bPlayFlag = false;
	private boolean bDownload = false;
	
	/**
	 * 管理类
	 */
	private VideoPlayManager mPlayManager;
	private DownloadManager mLoadManager;
	
	/**
	 * SrufaceView创建信号量
	 */
	private Semaphore mSurfaceCreateSemaphore = new Semaphore(1);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_video_player);
		
		
		bDownload = false;
		bPlayFlag = false;
		handleIntent();
		init();
	}
	
	/**
	 * 获取Intent数据，再此需要获取视频文件路径
	 */
	private void handleIntent() {
		mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
				File.separator + "video.mp4";
		Log.i(TAG, "FilePath:" + mFilePath);
	}

	/**
	 * 初始化
	 */
	private void init() {
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
		mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(this);
		mHolder.setKeepScreenOn(true);
		
		mPlayManager = VideoPlayManager.getInstance();
		mPlayManager.initMediaPlayer(mHolder);
		mPlayManager.setPlayStateListener(listener);
		
		mPlayBtn = (Button) findViewById(R.id.btn_player_pause);
		mPlayBtn.setBackgroundResource(R.drawable.media_play);
		mPlayBtn.setVisibility(View.GONE);
		
		mDownloadBar = (ProgressBar) findViewById(R.id.progress);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		mLoadManager = DownloadManager.getInstace(); 
		mLoadManager.setDownloadStateListener(downloadStateListener);
		
		// for test
		mFilePath = "http://dlsw.baidu.com/sw-search-sp/soft/05/12876/epp_V4.0.0.395_setup.1441769341.exe";
		String fileName = mFilePath.substring(mFilePath.lastIndexOf("/") + 1);
		File savaFile = new File(DataFileTools.getInstance().getRecordVideoPath(), fileName);
		Log.i(TAG, "fileName:" + fileName);
		
		if (savaFile != null && savaFile.exists()){
			bDownload = true;
			mHandler.sendEmptyMessageDelayed(MSG_START_PLAY,2000);
		} else {
			mLoadManager.startToDownload(mFilePath);
		}
		
	}
	
	/**
	 * 播放控制按钮
	 * @param view
	 */
	public void playOnClick(View view){
		if (!bDownload){
			return ;
		}
		bPlayFlag = !bPlayFlag; 
		if (bPlayFlag){
			mHandler.sendEmptyMessage(MSG_START_PLAY);
		} else {
			mHandler.sendEmptyMessage(MSG_STOP_PLAY);
		}
	}
	
	/**
	 * 开始播放
	 */
	private void play(){
		if (mPlayManager != null){
			if (mPlayManager.isPause()){
				mPlayManager.start();
			}else {
				mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
						File.separator + "video.mp4";
				mPlayManager.play(mFilePath);
			}
		}
	}
	
	/**
	 * 停止播放
	 */
	private void stop(){
		if (mPlayManager != null){
			mPlayManager.pause();
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_START_PLAY:
				mDownloadBar.setVisibility(View.GONE);
				mPlayBtn.setVisibility(View.VISIBLE);
				mPlayBtn.setBackgroundResource(R.drawable.media_pause);
				play();
				break;
				
			case MSG_STOP_PLAY:
				mPlayBtn.setBackgroundResource(R.drawable.media_play);
				stop();
				break;
				
			default:
				break;
			}
		};
	};
	
	/**
	 * 下载监听器
	 */
	private DownloadStateListener downloadStateListener = new DownloadStateListener() {
		
		@Override
		public void startDownLoad() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgressUpdate(int progress) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onProgressUpdate progress:" + progress);
		}
		
		@Override
		public void onDownLoadError(int errorCode) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onDownLoadError errorCode:" + errorCode);
		}
		
		@Override
		public void onDownLoadCompleted() {
			if (isDestroyed()){
				return ;
			}
			try {
				mSurfaceCreateSemaphore.acquire();
				bDownload = true;
				mHandler.sendEmptyMessage(MSG_START_PLAY);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * 播放状态监听
	 */
	private PlayStateListener listener = new PlayStateListener() {
		
		@Override
		public void startToPlay() {
			Log.i(TAG, "start to play successfully!!");
			mPlayBtn.setVisibility(View.VISIBLE);
			bPlayFlag = true;
		}
		
		@Override
		public void onPlayCompletion() {
			Log.i(TAG, " play Completed!!");
			bPlayFlag = false;
			mPlayBtn.setBackgroundResource(R.drawable.media_play);
		}
		
		@Override
		public void onBufferingUpdate() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(int errorCode) {
			// TODO Auto-generated method stub
			bPlayFlag = false;
		}
	};
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		Log.i(TAG, "surfaceCreated!");
		//释放信息号，此时可以进行播放
		mSurfaceCreateSemaphore.release();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.i(TAG, "surfaceDestroyed!");
		if (mPlayManager != null){
			mPlayManager.release();
		}
		bPlayFlag = false; 
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
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
	}
}
