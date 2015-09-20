package com.tcl.wechat.widget;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.tcl.wechat.R;
import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.common.WeiConstant.CommandAction;
import com.tcl.wechat.db.WeiMsgRecordDao;
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.WeiXinMsgRecorder;
import com.tcl.wechat.modle.data.DataFileTools;
import com.tcl.wechat.utils.DateUtil;

public class WeChatWidget extends AppWidgetProvider implements IConstant{
	
	private static final String TAG = "WeChatWidget";
	
	private BindUser mBindUser;
	
	private WeiXinMsgRecorder mRecorder;
	
	private ArrayList<WeiXinMsgRecorder> mAllRecorders;
	
	
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		Log.i(TAG, "onUpdate-->>");
		
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
		
		if (CommandAction.ACTION_MSG_UPDATE.equals(action)){
			Bundle bundle = intent.getExtras();
			if (bundle != null){
				WeiXinMsgRecorder recorder = bundle.getParcelable("WeiXinMsgRecorder");
				
				if (recorder == null){
					return ;
				}
				
				Log.i(TAG, "Recorder:" + recorder.toString());
				
				mRecorder = recorder;
				
				if (mBindUser == null || !recorder.getOpenid().equals(mBindUser.getOpenId())){
					
					mBindUser = WeiUserDao.getInstance().getUser(recorder.getOpenid());
				}
			} 
		}else {
			mAllRecorders = WeiMsgRecordDao.getInstance().getAllUserRecorder();
			if (mAllRecorders != null && !mAllRecorders.isEmpty()){
				mRecorder = mAllRecorders.get(0);
			}
			
			mBindUser = WeiUserDao.getInstance().getUser(mRecorder.getOpenid());
		}
		AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManger.getAppWidgetIds(new ComponentName(context, WeChatWidget.class));
        
        for (int i = 0; i < appWidgetIds.length; i++) {
			updateAppWidget(context, appWidgetManger, appWidgetIds[i]);
		} 
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
		 * 1、 更新消息
		 */
		updateWeiXinMsg(remoteViews);
		
		
		/**
		 * 2、跳转进主界面
		 */
		//进入主界面
		Intent mainIntent = new Intent();
		mainIntent.setAction(IConstant.ACTION_MAINVIEW);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, mainIntent, 0);
		remoteViews.setOnClickPendingIntent(R.id.img_msgbd, pendingIntent);
		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		
		//进入聊天界面
		Intent chatIntent = new Intent();
		chatIntent.setAction(IConstant.ACTION_CHATVIEW);
		PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, chatIntent, 0);
		remoteViews.setOnClickPendingIntent(R.id.btn_reply, pendingIntent2);
		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		
		if (mRecorder != null && ChatMsgType.VIDEO.equals(mRecorder.getMsgtype())){
			//进入视频播放
			Intent playIntent = new Intent();
			playIntent.setAction(IConstant.ACTION_PLAY_VIDEO);
			PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, 0, playIntent, 0);
			remoteViews.setOnClickPendingIntent(R.id.chat_msg_layout, pendingIntent3);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
		
		if (mRecorder != null && ChatMsgType.VOICE.equals(mRecorder.getMsgtype())){
			//播放音频
		}
	}

	/**
	 * 更新消息
	 * @param remoteViews
	 */
	@SuppressLint("NewApi") 
	private void updateWeiXinMsg(RemoteViews remoteViews) {
		// TODO Auto-generated method stub
		if (mBindUser != null && mRecorder != null){
			//更新用户信息
			Bitmap userIcon = DataFileTools.getInstance().getBindUserCircleIcon(mBindUser.getHeadImageUrl());
			remoteViews.setImageViewBitmap(R.id.img_user_icon, userIcon);
			
			remoteViews.setTextViewText(R.id.tv_user_name, mBindUser.getNickName());
			remoteViews.setTextViewText(R.id.tv_msg_receiver_time, 
					DateUtil.getTime(mRecorder.getCreatetime()));
			
			
			//更新消息记录
			String msgType = mRecorder.getMsgtype();
			if (ChatMsgType.TEXT.equals(msgType)){
				remoteViews.setViewVisibility(R.id.tv_textmsg_detail, View.VISIBLE);
				remoteViews.setViewVisibility(R.id.img_voicemsg_detail, View.GONE);
				remoteViews.setViewVisibility(R.id.img_videomsg_detail, View.GONE);
				remoteViews.setViewVisibility(R.id.img_imagemsg_detail, View.GONE);
				remoteViews.setViewVisibility(R.id.msg_play_time, View.GONE);
				remoteViews.setTextViewText(R.id.tv_textmsg_detail, mRecorder.getContent());
			} else if (ChatMsgType.VOICE.equals(msgType)){
				remoteViews.setViewVisibility(R.id.img_voicemsg_detail, View.VISIBLE);
				remoteViews.setViewVisibility(R.id.tv_textmsg_detail, View.GONE);
				remoteViews.setViewVisibility(R.id.img_videomsg_detail, View.GONE);
				remoteViews.setViewVisibility(R.id.img_imagemsg_detail, View.GONE);
				remoteViews.setTextViewText(R.id.msg_play_time, "3\"");
				
			} else if (ChatMsgType.VIDEO.equals(msgType)){
				remoteViews.setViewVisibility(R.id.img_videomsg_detail, View.VISIBLE);
				remoteViews.setViewVisibility(R.id.img_voicemsg_detail, View.GONE);
				remoteViews.setViewVisibility(R.id.tv_textmsg_detail, View.GONE);
				remoteViews.setViewVisibility(R.id.img_imagemsg_detail, View.GONE);
				
			} else if (ChatMsgType.IMAGE.equals(msgType)) {
				remoteViews.setViewVisibility(R.id.img_imagemsg_detail, View.VISIBLE);
				remoteViews.setViewVisibility(R.id.img_videomsg_detail, View.GONE);
				remoteViews.setViewVisibility(R.id.img_voicemsg_detail, View.GONE);
				remoteViews.setViewVisibility(R.id.tv_textmsg_detail, View.GONE);
				remoteViews.setViewVisibility(R.id.msg_play_time, View.GONE);
				Bitmap bitmap = DataFileTools.getInstance().getChatImageIcon(mRecorder.getUrl());
				remoteViews.setImageViewBitmap(R.id.img_imagemsg_detail, bitmap);
			}
			
 		}
	}
}
