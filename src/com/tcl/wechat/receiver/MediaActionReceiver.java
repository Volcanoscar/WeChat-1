package com.tcl.wechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tcl.wechat.action.recorder.Recorder;
import com.tcl.wechat.action.recorder.RecorderPlayerManager;
import com.tcl.wechat.action.recorder.listener.AudioPlayCompletedListener;
import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.ui.activity.VideoPlayerActivity;

/**
 * 媒体处理接收器
 * @author rex.lei
 *
 */
public class MediaActionReceiver extends BroadcastReceiver implements IConstant{
	
	private static final String TAG = "MediaActionReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i(TAG, "action:" + action);
		
		if (CommandAction.ACTION_PLAY_VIDEO.equals(action)){//进入视频播放界面
			Intent playintent = new Intent(context, VideoPlayerActivity.class);
			playintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(playintent);
		} else if (CommandAction.ACTION_PLAY_SOUND.equals(action)){ //开始播放音频
			Recorder recorder = (Recorder) intent.getSerializableExtra("Recorder");
			if (recorder != null){
				String fileName = recorder.getFileName();
				Log.i(TAG, "filePath:" + fileName);
				RecorderPlayerManager.getInstance().play(fileName);
				RecorderPlayerManager.getInstance().setPlayCompletedListener(playCompletedListener);
			}
		}
	}
	
	/**
	 * 音频播放完成监听器
	 */
	private AudioPlayCompletedListener playCompletedListener = new AudioPlayCompletedListener() {
		
		@Override
		public void onCompleted() {
		}

		@Override
		public void onError(int errorcode) {
			Log.e(TAG, "play failed, errorcode:" + errorcode);
		}
	};

}
