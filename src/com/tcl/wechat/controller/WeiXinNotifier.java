package com.tcl.wechat.controller;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.tcl.wechat.R;
import com.tcl.wechat.WeChatApplication;
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.modle.WeiXinMsg;
import com.tcl.wechat.ui.activity.ChatActivity;

/**
 * 微信消息通知实体类
 * @author rex.lei
 *
 */
public class WeiXinNotifier {
	
	private static final String TAG = "WeiXinNotifier";
	
	private static final String ACTION = "com.wechat.NOTIFY_CANCEL";
	
	public static final int NOTIFY_ID = 0x000;
	
	public static int mNewNum = 0;// 通知栏新消息条目，使用全局变量
	
	private Context mContext;
	private static NotificationManager mNotificationManager;
	
	private static class WeiXinNotifierInstance{
		private static final WeiXinNotifier mInstance = new WeiXinNotifier();
	}

	private WeiXinNotifier() {
		super();
		mContext = WeChatApplication.gContext;
		mNotificationManager = (NotificationManager)mContext.
					getSystemService(Context.NOTIFICATION_SERVICE);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION);
		mContext.registerReceiver(deleteReceiver, filter);
	}
	
	public static WeiXinNotifier getInstance(){
		return WeiXinNotifierInstance.mInstance;
	}

	/**
	 * 进行通知
	 * @param weiXinMsg 通知消息实体类
	 */
	@SuppressLint("NewApi") 
	public void notify(WeiXinMsg weiXinMsg){
		Log.i(TAG, "notify!!");
		
		if (weiXinMsg == null){
			return ;
		}
		mNewNum++;

		/**
		 * 通知样式
		 * 		用户图像    用户名                                                                              微信消息图像
		 * 				[条数]用户名: 文本消息
		 * 							[语音]
		 * 							[小视频]
		 * 							[图片]
		 */
		
		/**
		 *  更新通知栏
		 */
		//点击进入事件
		Intent contentIntent = new Intent(WeChatApplication.gContext, ChatActivity.class);
		contentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingContentIntent = PendingIntent.getActivity(mContext, 0, contentIntent, 0);
		//点击取消时间
		Intent deleteIntent = new Intent();
		deleteIntent.setAction(ACTION);
		deleteIntent.putExtra("NOTIFY_ID", NOTIFY_ID);
		PendingIntent pendingDeleteIntent = PendingIntent.getBroadcast(mContext, 0, deleteIntent, 0);
		
		StringBuffer contentBuffer = new StringBuffer();
		if (mNewNum > 1){
			contentBuffer.append("[").append(mNewNum).append("]");
		}
		contentBuffer.append(weiXinMsg.getnickname()).append(":");
		String contentText = "" ;
		if (ChatMsgType.TEXT.equals(weiXinMsg.getMsgtype())){
			contentText = weiXinMsg.getContent();
		} else if (ChatMsgType.VIDEO.equals(weiXinMsg.getMsgtype())){
			contentText = "[小视频]";
		} else if (ChatMsgType.VOICE.equals(weiXinMsg.getMsgtype())){
			contentText = "[语音]";
		} else if (ChatMsgType.IMAGE.equals(weiXinMsg.getMsgtype())){
			contentText = "[图片]";
		}
		contentBuffer.append(contentText);
		
		/**
		 * 根据不同的消息类型，显示
		 */
		Notification.Builder builder = new Notification.Builder(mContext);
		builder.setSmallIcon(R.drawable.ic_launcher)//设置状态栏里面的图标（小图标） 　　　　　　　　　　　　　　　　　　　　
				.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.head_user_icon))//下拉下拉列表里面的图标（大图标） 　　　　　　　
				.setTicker(contentText) //设置状态栏的显示的信息  
				.setWhen(System.currentTimeMillis())//设置时间发生时间  
				.setAutoCancel(true)//设置可以清除  
				.setContentTitle(weiXinMsg.getnickname())//设置下拉列表里的标题  
				.setContentText(contentBuffer.toString())
				.setDefaults(Notification.DEFAULT_SOUND)
				.setContentIntent(pendingContentIntent)
				.setDeleteIntent(pendingDeleteIntent);
		
		Notification notification = builder.build();
		mNotificationManager.notify(NOTIFY_ID, notification);
	}

	private BroadcastReceiver deleteReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			int notifyId = intent.getExtras().getInt("NOTIFY_ID");
			if (NOTIFY_ID == notifyId){
				mNewNum = 0;
			}
		}
	};
	
}
