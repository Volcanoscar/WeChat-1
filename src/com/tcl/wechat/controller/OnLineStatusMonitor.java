package com.tcl.wechat.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tcl.wechat.WeApplication;
import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.model.OnLineStatus;

/**
 * 用户在线状态检测类
 * @author rex.lei
 *
 */
public class OnLineStatusMonitor {
	
	private static final String TAG = OnLineStatusMonitor.class.getSimpleName();
	
	public static final int INTERVAL = 1000 * 60 * 60 * 48;// 48h
	
	private Context mContext;

	private AlarmManager mAlarmManager;
	
	private HashMap<String, PendingIntent> mAlarmsHashMap = new HashMap<String, PendingIntent>();
	
	private static final class OnLineStatusMonitorInstance{
		private static final OnLineStatusMonitor mInstance = new OnLineStatusMonitor();
	}
	
	private OnLineStatusMonitor() {
		super();
		// TODO Auto-generated constructor stub
		mContext = WeApplication.getContext();
	}
	
	public static OnLineStatusMonitor getInstance(){
		return OnLineStatusMonitorInstance.mInstance;
	}
	
	/**
	 * 开始监测
	 * @param status
	 */
	@SuppressLint("SimpleDateFormat") 
	public void startMonitor(OnLineStatus status){
		Log.d(TAG, "startMonitor status:" + status.toString());
		mAlarmManager = (AlarmManager) mContext
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(IConstant.ACTION_ONLINE_ALARM);
		intent.putExtra("OnLineStatus", status);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, 
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		mAlarmsHashMap.put(status.getOpenid(), pendingIntent);
		
		try {
			long triggerAtMillis = Long.parseLong(status.getTriggerTime());
			mAlarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 结束监测
	 * @param status
	 */
	public void stopMonitor(String openid){
		Log.d(TAG, "stopMonitor openid:" + openid);
		if (mAlarmsHashMap == null || !mAlarmsHashMap.containsKey(openid)){
			return ;
		}
		if (mAlarmManager != null){
			mAlarmManager.cancel(mAlarmsHashMap.get(openid));
		}
	}
	
	/**
	 * 结束所有检测器
	 */
	public void stopAllMonitor(){
		if (mAlarmsHashMap == null || mAlarmsHashMap.size() <= 0 ){
			return ;
		}
		Iterator<Entry<String, PendingIntent>>  iterator = mAlarmsHashMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, PendingIntent> entry = iterator.next(); 
			String openid = entry.getKey();
			mAlarmManager.cancel(mAlarmsHashMap.get(openid));
		}
	}

}
