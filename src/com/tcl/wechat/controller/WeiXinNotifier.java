package com.tcl.wechat.controller;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.database.WeiUserDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.WeiXinMessage;
import com.tcl.wechat.ui.activity.ChatActivity;
import com.tcl.wechat.utils.DataFileTools;
import com.tcl.wechat.utils.ExpressionUtil;
import com.tcl.wechat.utils.SystemInfoUtil;

/**
 * 微信消息通知实体类
 * @author rex.lei
 *
 */
public class WeiXinNotifier {
	
	private static final String TAG = "WeiXinNotifier";
	
	private static final String ACTION_NOTIFY_CANCEL = "com.wechat.NOTIFY_CANCEL";
	
	public static int mNewNum = 0;// 通知栏新消息条目，使用全局变量
	
	private Context mContext;
	
	private BindUser mBindUser;
	
	private static NotificationManager mNotificationManager;
	
	private static class WeiXinNotifierInstance{
		private static final WeiXinNotifier mInstance = new WeiXinNotifier();
	}

	private WeiXinNotifier() {
		super();
		mContext = WeApplication.getContext();
		mNotificationManager = (NotificationManager)mContext.
					getSystemService(Context.NOTIFICATION_SERVICE);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_NOTIFY_CANCEL);
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
	public void notify(WeiXinMessage weiXinMsg){
		Log.i(TAG, "notify!!");
		
		if (weiXinMsg == null){
			return ;
		}
		mNewNum++;
		//获取绑定用户
		mBindUser =  WeiUserDao.getInstance().getUser(weiXinMsg.getOpenid());
		//点击进入事件
		Intent contentIntent = new Intent();
		contentIntent.setClass(mContext, ChatActivity.class);
		contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				Intent.FLAG_ACTIVITY_CLEAR_TASK);
		contentIntent.putExtra("bindUser", mBindUser);
		PendingIntent pendingContentIntent = PendingIntent.getActivity(mContext, 0, contentIntent, 0);
		
		//点击取消事件
		Intent deleteIntent = new Intent();
		deleteIntent.setAction(ACTION_NOTIFY_CANCEL);
		PendingIntent pendingDeleteIntent = PendingIntent.getBroadcast(mContext, 0, deleteIntent, 0);
		
		StringBuffer contentBuffer = new StringBuffer();
		if (mNewNum > 1){
			contentBuffer.append("[").append(mNewNum).append("]");
		}
		Bitmap userIcon = null;
		String userName = null;
		if (mBindUser != null){
			userIcon = DataFileTools.getInstance().getBindUserCircleIcon(mBindUser.getHeadImageUrl());
			userName = WeiUserDao.getInstance().getUser(weiXinMsg.getOpenid()).getNickName();
		}
		
		contentBuffer.append(userName).append("：");
		String contentText = "" ;
		if (ChatMsgType.TEXT.equals(weiXinMsg.getMsgtype())){
			contentText = weiXinMsg.getContent();
			if (!TextUtils.isEmpty(contentText)){
				contentText = ExpressionUtil.getInstance().StringToCharacters(mContext, 
						new StringBuffer(contentText), false).toString();
			} 
		} else if (ChatMsgType.VIDEO.equals(weiXinMsg.getMsgtype())){
			contentText = "[小视频]";
		} else if (ChatMsgType.VOICE.equals(weiXinMsg.getMsgtype())){
			contentText = "[语音]";
		} else if (ChatMsgType.IMAGE.equals(weiXinMsg.getMsgtype())){
			contentText = "[图片]";
		}
		contentBuffer.append(contentText);
		
		
		
//		Notification notification = new Notification();
//		notification.defaults |= Notification.DEFAULT_SOUND;
		
		/**
		 * 根据不同的消息类型，显示
		 */
		Notification.Builder builder = new Notification.Builder(mContext);
		builder.setDefaults(Notification.DEFAULT_SOUND);
		
		if (!SystemInfoUtil.isTopActivity()){
//			RemoteViews contentView = new RemoteViews(SystemInfoUtil.getPackageName(), 
//					R.layout.layout_notify);
//			contentView.setImageViewBitmap(R.id.img_user_icon, userIcon);
//			contentView.setTextViewText(R.id.tv_user_name, userName);
//			contentView.setTextViewText(R.id.tv_msg_info, contentText);
//			contentView.setTextViewText(R.id.tv_time, contentText);
//			
//			notification.contentView = contentView;
//			notification.icon = R.drawable.notify;  
//			notification.tickerText = contentText;  
//			notification.when = System.currentTimeMillis(); // 立即发生此通知 
//			notification.number = mNewNum;
			
			builder.setSmallIcon(R.drawable.notify)//设置状态栏里面的图标（小图标） 　　　　　　　　　　　　　　　　　　　　
				.setLargeIcon(userIcon)		//下拉下拉列表里面的图标（大图标） 　　　　　　　
				.setTicker(contentText) 	//设置状态栏的显示的信息  
				.setWhen(System.currentTimeMillis())//设置时间发生时间  
				.setAutoCancel(true)		//设置可以清除  
				.setContentTitle(userName)	//设置下拉列表里的标题  
				.setContentText(contentBuffer.toString())
				.setContentIntent(pendingContentIntent)
				.setDeleteIntent(pendingDeleteIntent);
		}
		
		Notification notification = builder.build();
		mNotificationManager.notify(0, notification);
	}

	private BroadcastReceiver deleteReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			mNewNum = 0;
		}
	};
	
	//删除通知    
    public void clearNotification(){
        // 启动后删除之前我们定义的通知   
        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);   
        notificationManager.cancel(0); 
        mBindUser = null;
        mNewNum = 0;
    }
	
}
