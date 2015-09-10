package com.tcl.wechat.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.tcl.wechat.R;
import com.tcl.wechat.common.IConstant;

public class WeChatWidget extends AppWidgetProvider implements IConstant{
	
	private static final String TAG = "WeChatWidget";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		for (int i = 0; i < appWidgetIds.length; i++) {
			updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
		}
	}

	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		String action = intent.getAction();
		Log.i(TAG, "action:" + action);
		
	}
	
	/**
	 * 初始化控件
	 * @param context
	 * @param appWidgetManager
	 * @param appWidgetIds
	 */
	private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
			int appWidgetId) {

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_view);
		
		/**
		 * 跳转进主界面
		 */
//		Intent mainIntent = new Intent(context, MainActivity.class);
//		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
//		remoteViews.setOnClickPendingIntent(R.id.btn_reply, pendingIntent);
//		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		//进入主界面
		Intent mainIntent = new Intent();
		mainIntent.setAction(IConstant.ACTION_MAINVIEW);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, mainIntent, 0);
		remoteViews.setOnClickPendingIntent(R.id.img_msgbd, pendingIntent);
		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		
		//for test 
		Intent chatIntent = new Intent();
		chatIntent.setAction(IConstant.ACTION_CHATVIEW);
		PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, chatIntent, 0);
		remoteViews.setOnClickPendingIntent(R.id.btn_reply, pendingIntent2);
		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		
		
		Intent playIntent = new Intent();
		playIntent.setAction(IConstant.ACTION_PLAY_VIDEO);
		PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, 0, playIntent, 0);
		remoteViews.setOnClickPendingIntent(R.id.chat_msg_layout, pendingIntent3);
		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}
	
	
}
