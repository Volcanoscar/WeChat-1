package com.tcl.wechat.widget;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.tcl.wechat.action.audiorecorder.Recorder;
import com.tcl.wechat.action.audiorecorder.RecorderPlayerManager;

public class WidgetService extends Service {
	
	private static final String TAG = WidgetService.class.getSimpleName();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onStartCommand");
		Recorder recorder = (Recorder) intent.getSerializableExtra("Recorder");
		Log.d(TAG, "Recorder:" + recorder);
		if (recorder != null){
			if (RecorderPlayerManager.getInstance().isPlaying()){
				RecorderPlayerManager.getInstance().stop();
				RecorderPlayerManager.getInstance().release();
			} else {
				String filePath = recorder.getFileName();
				RecorderPlayerManager.getInstance().play(filePath);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

}
