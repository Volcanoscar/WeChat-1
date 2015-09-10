package com.tcl.wechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
		
		if (ACTION_PLAY_VIDEO.equals(action)){//进入视频播放界面
			Intent playintent = new Intent(context, VideoPlayerActivity.class);
			playintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(playintent);
		} else if (ACTION_PLAY_SOUND.equals(action)){ //开始播放音频
			
		}
	}

}
