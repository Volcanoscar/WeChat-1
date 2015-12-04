package com.tcl.wechat.ui.activity;

import java.io.File;
import java.util.concurrent.Semaphore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.tcl.wechat.R;
import com.tcl.wechat.action.player.VideoPlayManager;
import com.tcl.wechat.action.player.listener.PlayStateListener;
import com.tcl.wechat.common.IConstant.DownloadState;
import com.tcl.wechat.utils.DataFileTools;

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
	private FrameLayout mProgressBarLayout;
	
	private String mFilePath;
	private boolean bPlayFlag = false;
	private boolean bDownload = false;
	
	/**
	 * 管理类
	 */
	private VideoPlayManager mPlayManager;
//	private DownloadManager mLoadManager;
	
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
		
		registerBroadcast();
		handleIntent();
		init();
	}
	
	/**
	 * 获取Intent数据，再此需要获取视频文件路径
	 */
	private void handleIntent() {
//		mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
//				File.separator + "video.mp4";
//		Log.i(TAG, "FilePath:" + mFilePath);
		mFilePath = getIntent().getExtras().getString("FilePath");
		Log.i(TAG, "FilePath:" + mFilePath);
	}

	/**
	 * 注册广播
	 */
	private void registerBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadState.DOWNLOAD_COMPLETED);
		registerReceiver(receiver, filter);
	}
	
	/**
	 * 初始化
	 */
	private void init() {
		
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
		
		//再次可以自定义视频窗口的位置和大小
		//坐标
		lp.x = 100;
		lp.y = 100;
		//大小
		lp.width = 800;
		lp.height = 450;
		getWindow().setAttributes(lp);
		
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
		
		mProgressBarLayout = (FrameLayout) findViewById(R.id.layout_progressbar);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
//		mLoadManager = DownloadManager.getInstace(); 
		
		// for test
		String fileName = mFilePath.substring(mFilePath.lastIndexOf("/") + 1);
		File savaFile = new File(DataFileTools.getInstance().getRecordVideoPath(), fileName);
		Log.i(TAG, "fileName:" + fileName);
		
		if (savaFile != null && savaFile.exists()){
			bDownload = true;
			mHandler.sendEmptyMessageDelayed(MSG_START_PLAY,2000);
		} else {
//			mLoadManager.startDownLload(mFilePath, null, DownlodaType.VIDEO);
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
				mProgressBarLayout.setVisibility(View.GONE);
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
	 * 广播接收器
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (DownloadState.DOWNLOAD_COMPLETED.equals(action)){
				if (isDestroyed()){
						return ;
				}
				String url = intent.getStringExtra("url");
				if (TextUtils.isEmpty(url) || !mFilePath.equals(url)){
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
