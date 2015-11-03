package com.tcl.wechat.widget;

import java.io.File;
import java.math.BigDecimal;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.action.recorder.RecorderAudioManager;
import com.tcl.wechat.action.recorder.RecorderPlayerManager;
import com.tcl.wechat.action.recorder.listener.AudioPlayCompletedListener;
import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.database.WeiMsgRecordDao;
import com.tcl.wechat.database.WeiUserDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.WeiXinMsgRecorder;
import com.tcl.wechat.ui.activity.BaiduMapActivity;
import com.tcl.wechat.ui.activity.ChatActivity;
import com.tcl.wechat.ui.activity.LoginActivity;
import com.tcl.wechat.ui.activity.ShowImageActivity;
import com.tcl.wechat.ui.activity.ShowTextActivity;
import com.tcl.wechat.ui.activity.WebViewActivity;
import com.tcl.wechat.utils.DataFileTools;
import com.tcl.wechat.utils.DateTimeUtil;
import com.tcl.wechat.utils.ExpressionUtil;
import com.tcl.wechat.utils.ImageUtil;
import com.tcl.wechat.utils.SystemTool;

/**
 * 微信留言板快捷方式
 * @author rex.lei
 * 
 */
public class WeChatWidget extends AppWidgetProvider implements IConstant{
	
	private static final String TAG = "WeChatWidget";
	
	private static Context mContext = WeApplication.getContext();
	
	private static RemoteViews mRemoteViews;
	
	private static BindUser mBindUser;
	
	private static WeiXinMsgRecorder mRecorder;
	
	private static int mUpdateCnt = 0;
	
	private static boolean bPlaying = false;
	
	private static RecorderPlayerManager mAudioManager = RecorderPlayerManager.getInstance();
	
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		Log.i(TAG, "onUpdate-->>");
		
		initData();
		initRemoteViews();
		initEvent(context, appWidgetManager, appWidgetIds);
		updateAppWidget(context, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		
		String action = intent.getAction();
		Log.i(TAG, "action:" + action);
		
		if (CommandAction.ACTION_MSG_UPDATE.equals(action)){
			//新消息事件
			Bundle bundle = intent.getExtras();
			if (bundle != null){
				WeiXinMsgRecorder recorder = bundle.getParcelable("WeiXinMsgRecorder");
				Log.i(TAG, "Receive msg:" + recorder);
				if (recorder == null){
					return ;
				}
				mRecorder = recorder;
				mBindUser = WeiUserDao.getInstance().getUser(recorder.getOpenid());
				if (mBindUser == null){
					Log.e(TAG, "BindUser is NULL!!");
					return ;
				}
			} else {
				initData();
			}
			AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(context);
	        int[] appWidgetIds = appWidgetManger.getAppWidgetIds(new ComponentName(context, WeChatWidget.class));
	        updateAppWidget(context, appWidgetManger, appWidgetIds);
		}  else if (ACTION_UNBIND_ENENT.equals(action)){
			//用户绑定解绑事件
			mBindUser = WeiUserDao.getInstance().getLastBindUser();
			if (mBindUser != null){
				Log.d(TAG, "mRecorder:" + mRecorder);
				mRecorder = WeiMsgRecordDao.getInstance().getLatestRecorder(mBindUser.getOpenId());
				Log.d(TAG, "mRecorder:" + mRecorder);
			} else {
				mRecorder = null;
			}
			
			AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(context);
	        int[] appWidgetIds = appWidgetManger.getAppWidgetIds(new ComponentName(context, WeChatWidget.class));
	        updateAppWidget(context, appWidgetManger, appWidgetIds);
		} else if (ACTION_MAINVIEW.equals(action)){
			//进入主界面
			resetPlayAnim();
			Intent mainintent = new Intent(context, LoginActivity.class);
			mainintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(mainintent);
		} else if (ACTION_UPDATE_AUDIO_ANMI.equals(action)){
			//更新播放动画
			int layoutId = mContext.getResources().getIdentifier("v_left_anim" + (mUpdateCnt++ % 3 + 1), 
					"drawable", mContext.getPackageName());
			if (mRemoteViews == null){
				initRemoteViews();
			}
			mRemoteViews.setViewVisibility(R.id.img_voicemsg_detail, View.VISIBLE);
			mRemoteViews.setImageViewResource(R.id.img_voicemsg_detail, layoutId);
			
			AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(mContext);
	        int[] appWidgetIds = appWidgetManger.getAppWidgetIds(new ComponentName(mContext, WeChatWidget.class));
	        appWidgetManger.updateAppWidget(appWidgetIds, mRemoteViews);
		} else {
			//启动Activity
			Intent actionIntent = createIntent(context, action);
			if (actionIntent != null){
				context.startActivity(actionIntent);
			}
		}
	}
	
	/**
	 * 获取Intent
	 * @param context
	 * @param action
	 * @return
	 */
	public Intent createIntent(Context context, String action){
		Intent mIntent = null;
		
		if (mRecorder == null){
			mRecorder = WeiMsgRecordDao.getInstance().getLatestRecorder();
		}
		
		if (mRecorder == null){
			Log.i(TAG, "No Message!!");
			return null;
		}
		
		if (mBindUser == null ){
			mBindUser = WeiUserDao.getInstance().getUser(mRecorder.getOpenid());
		}
		
		if (ACTION_CHATVIEW.equals(action)) {
			//进入聊天界面
			resetPlayAnim();
			mRecorder = WeiMsgRecordDao.getInstance().getLatestRecorder(mBindUser.getOpenId());
			if (mBindUser != null && mRecorder != null){
				mIntent = new Intent(context, ChatActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("bindUser", mBindUser);
				mIntent.putExtras(bundle);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
						Intent.FLAG_ACTIVITY_CLEAR_TASK);
			}
		} else if (ACTION_SHOW_TEXT.equals(action)) {
			//显示文本内容
			CharSequence contentCharSeq = mRecorder.getContent();
			if (!TextUtils.isEmpty(contentCharSeq)){
				if (SystemTool.isEmail(mRecorder.getContent())){
					//识别为邮箱
					
				} else {
					contentCharSeq = ExpressionUtil.getInstance().StringToSpannale(context, 
							new StringBuffer(contentCharSeq));
					
					mIntent = new Intent(context, ShowTextActivity.class);
					mIntent.putExtra("Content", contentCharSeq);
					mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				}
			} 
		} else if (ACTION_SHOW_IMAGE.equals(action)) {
			//显示图像
			String url = mRecorder.getUrl();
			if (!TextUtils.isEmpty(url)){
				mIntent = new Intent(context, ShowImageActivity.class);
				mIntent.putExtra("WeiXinMsgRecorder", mRecorder);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
		} else if (ACTION_PLAY_AUDIO.equals(action)) {
			//播放音频
			String fileName = mRecorder.getFileName();
			if (!TextUtils.isEmpty(fileName)){
				
				playAudio(fileName);
				//更新播放状态
//				WeiMsgRecordDao.getInstance().updatePlayState(mRecorder.getMsgid());
//				//启动播放
//				mIntent = new Intent(android.content.Intent.ACTION_VIEW);
//				mIntent.setDataAndType(Uri.parse(fileName), "audio/amr");
//				mIntent.setComponent(new ComponentName("com.android.music", 
//						"com.android.music.MediaPlaybackActivity"));
//				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
		} else if (ACTION_PLAY_VIDEO.equals(action)) {
			//播放视频
			String fileName = DataFileTools.getInstance().getVideoFilePath(mRecorder.getMediaid());
			if (!TextUtils.isEmpty(fileName)){
				mIntent = new Intent(android.content.Intent.ACTION_VIEW);
				mIntent.setDataAndType(Uri.parse(fileName), "video/mp4");
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
		} else if (ACTION_SHOW_LOCATION.equals(action)) {
			//位置信息
			if (mRecorder != null){
				mIntent = new Intent(context, BaiduMapActivity.class);
				mIntent.putExtra("WeiXinMsgRecorder", mRecorder);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
		} else if (ACTION_SHOW_LINK.equals(action)) {
			//链接信息
			String url = mRecorder.getUrl();
			Log.i(TAG, "LinkUrl:" + url);
			if (!TextUtils.isEmpty(url)){
				mIntent = new Intent(context, WebViewActivity.class); 
				mIntent.putExtra("LinkUrl", url);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}  
		}
		return mIntent;
	}
	
	/**
	 * 播放音频
	 * @param recorder
	 */
	private void playAudio(String fileName){
		
		
		if (mAudioManager == null){
			Log.e(TAG, "RecorderPlayerManager is not init!!");
			return ;
		}
		
		if (!mAudioManager.isPlaying()){
			
			//播放音频
			if (!TextUtils.isEmpty(fileName) ){
				mAudioManager.play(fileName);
				mAudioManager.setPlayCompletedListener(playCompletedListener);
			}
			bPlaying = true;
			mUpdateCnt = 0;
			new Thread(mPlayIngRunnable).start();
		} else { //如果正在播放，点击后，则需要暂停当前播放
			mAudioManager.stop();
			resetPlayAnim();
		}
	}
	
	private Runnable mPlayIngRunnable = new Runnable() {
		
		@Override
		public void run() {
			while (bPlaying) {
				Intent intent = new Intent(ACTION_UPDATE_AUDIO_ANMI);
				mContext.sendBroadcast(intent);
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
	/**
	 * 音频播放完成监听器
	 */
	private AudioPlayCompletedListener playCompletedListener = new AudioPlayCompletedListener() {
		
		@Override
		public void onCompleted() {
			resetPlayAnim();
		}

		@Override
		public void onError(int errorcode) {
			// TODO Auto-generated method stub
			resetPlayAnim();
		}
	};
	
	private void resetPlayAnim(){
		mUpdateCnt = 0;
		bPlaying = false;
		if (mAudioManager != null && mAudioManager.isPlaying()) {
			mAudioManager.stop();
		}
		if (mRemoteViews == null){
			initRemoteViews();
		}		
		mRemoteViews.setImageViewResource(R.id.img_voicemsg_detail, R.drawable.v_left_anim3);
		
		AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(mContext);
        int[] appWidgetIds = appWidgetManger.getAppWidgetIds(new ComponentName(mContext, WeChatWidget.class));
        appWidgetManger.updateAppWidget(appWidgetIds, mRemoteViews);
	}
	
	
	/**
	 * 点击事件监听器
	 * @param context
	 * @param appWidgetManager
	 * @param appWidgetIds
	 */
	private void initEvent(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		//进入主界面
		Intent mainIntent = new Intent(ACTION_MAINVIEW);
		PendingIntent mainPendingIntent = PendingIntent.getBroadcast(context, 0, mainIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.img_msgbd, mainPendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, mRemoteViews);
		
		//进入聊天界面
		Intent chatIntent = new Intent(ACTION_CHATVIEW);
		PendingIntent chatPendingIntent = PendingIntent.getBroadcast(context, 0, chatIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_reply, chatPendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, mRemoteViews);
		
		//文本消息
		Intent textIntent = new Intent(ACTION_SHOW_TEXT);
		PendingIntent textPendingIntent = PendingIntent.getBroadcast(context, 0, textIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.tv_textmsg_detail, textPendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, mRemoteViews);
		
		Intent imageIntent = new Intent(ACTION_SHOW_IMAGE);
		PendingIntent imagePendingIntent = PendingIntent.getBroadcast(context, 0, imageIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.img_imagemsg_detail, imagePendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, mRemoteViews);
		
		//音频消息
		Intent audioIntent = new Intent(ACTION_PLAY_AUDIO);
		PendingIntent audioPendingIntent = PendingIntent.getBroadcast(context, 0, audioIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.layout_audioview, audioPendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, mRemoteViews);
		
		//视频消息
		Intent videoIntent = new Intent(ACTION_PLAY_VIDEO);
		PendingIntent videoPendingIntent = PendingIntent.getBroadcast(context, 0, videoIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.layout_videoview, videoPendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, mRemoteViews);
		
		//位置信息
		Intent loacationIntent = new Intent(ACTION_SHOW_LOCATION);
		PendingIntent locationPendingIntent = PendingIntent.getBroadcast(context, 0, loacationIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.layout_locationview, locationPendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, mRemoteViews);
		
		//链接信息
		Intent linkIntent = new Intent(ACTION_SHOW_LINK);
		PendingIntent linkPendingIntent = PendingIntent.getBroadcast(context, 0, linkIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.layout_linkview, linkPendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, mRemoteViews);
	}
	
	/**
	 * 数据初始化
	 */
	private void initData(){
		mRecorder = WeiMsgRecordDao.getInstance().getLatestRecorder();
		if (mRecorder != null){
			Log.d(TAG, "mRecorder:" + mRecorder);
			mBindUser = WeiUserDao.getInstance().getUser(mRecorder.getOpenid());
			Log.d(TAG, "mBindUser:" + mBindUser);
		}
	}
	
	/**
	 * RemoteViews初始化
	 */
	private void initRemoteViews(){
		String packageName = mContext.getPackageName();
		mRemoteViews = new RemoteViews(packageName, R.layout.appwidget_view);
	}
	
	/**
	 * 更新控件信息
	 * 
	 */
	private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		if (mRemoteViews == null){
			initRemoteViews();
		}
		
		if (mBindUser != null && mRecorder != null){
			
			//更新用户信息
			setupUserView();
			
			//控件复位
			resetViews();
			
			//更新消息记录
			String msgType = mRecorder.getMsgtype();
			Log.i(TAG, "msgType : " + msgType);
			
			if (ChatMsgType.TEXT.equals(msgType)){
				//文字显示控件
				setupTextView(context);
				
			} else if (ChatMsgType.IMAGE.equals(msgType)) {
				//图像显示控件
				setupImageView();
				
			} else if (ChatMsgType.VOICE.equals(msgType)) {
				//音频显示控件
				setupAudioView();
				
			} else if (ChatMsgType.VIDEO.equals(msgType) ||
					ChatMsgType.SHORTVIDEO.equals(msgType)) {
				//视频显示控件
				setupVideoView();
				
			} else if (ChatMsgType.LOCATION.equals(msgType)) {
				//位置显示控件
				setupLocationView();
				
			} else if (ChatMsgType.LINK.equals(msgType)) {
				//链接显示控件
				setupLinkView();
			} 
		} else {
			
			//控件复位
			resetViews();
			
			//设置默认显示内容
			setupDefaultView();
		}
		appWidgetManager.updateAppWidget(appWidgetIds, mRemoteViews);
	}
	
	/**
	 * 控件状态复位
	 */
	public void resetViews(){
		mRemoteViews.setViewVisibility(R.id.tv_textmsg_detail, View.GONE);
		mRemoteViews.setViewVisibility(R.id.img_imagemsg_detail, View.GONE);
		mRemoteViews.setViewVisibility(R.id.layout_videoview, View.GONE);
		mRemoteViews.setViewVisibility(R.id.layout_audioview, View.GONE);
		mRemoteViews.setViewVisibility(R.id.layout_locationview, View.GONE);
		mRemoteViews.setViewVisibility(R.id.layout_linkview, View.GONE);
	}

	/**
	 * 显示用户信息
	 */
	public void setupUserView(){
		updateImageView(R.id.img_user_icon, 80, 80, true);
		mRemoteViews.setTextViewText(R.id.tv_user_name, mBindUser.getNickName());
		mRemoteViews.setTextViewText(R.id.tv_msg_receiver_time, 
				DateTimeUtil.getShortTime(mRecorder.getCreatetime()));
		mRemoteViews.setTextColor(R.id.btn_reply, Color.WHITE);
	}
	
	/**
	 * 默认显示内容
	 */
	public void setupDefaultView(){
		Bitmap userIcon = BitmapFactory.decodeResource(mContext.getResources(), 
				R.drawable.head);
		
		mRemoteViews.setImageViewBitmap(R.id.img_user_icon, userIcon);
		mRemoteViews.setTextViewText(R.id.tv_user_name, "悠生活");
		mRemoteViews.setTextViewText(R.id.tv_msg_receiver_time, "6.8");
		
		mRemoteViews.setViewVisibility(R.id.layout_videoview, View.VISIBLE);
		Bitmap imgBitmap = BitmapFactory.decodeResource(mContext.getResources(), 
				R.drawable.message_video_bg);
		mRemoteViews.setImageViewBitmap(R.id.img_videomsg_detail, imgBitmap);
		
		mRemoteViews.setTextColor(R.id.btn_reply, Color.GRAY);
	}
	
	/**
	 * 显示文本信息
	 */
	public void setupTextView(Context context){
		mRemoteViews.setViewVisibility(R.id.tv_textmsg_detail, View.VISIBLE);
		CharSequence contentCharSeq = mRecorder.getContent();
		if (!TextUtils.isEmpty(contentCharSeq)){
			contentCharSeq = ExpressionUtil.getInstance().StringToCharacters(context, 
					new StringBuffer(contentCharSeq));
		} else {
			contentCharSeq = context.getString(R.string.no_message);
		}
		Log.i(TAG, "contentCharSeq:" + contentCharSeq);
		mRemoteViews.setTextViewText(R.id.tv_textmsg_detail, Html.fromHtml(contentCharSeq.toString()));
	}
	
	/**
	 * 显示图像信息
	 */
	public void setupImageView(){
		mRemoteViews.setViewVisibility(R.id.img_imagemsg_detail, View.VISIBLE);
		updateImageView(R.id.img_imagemsg_detail, 400, 400, false);
	}
	
	/**
	 * 显示视频信息
	 */
	public void setupVideoView(){
		mRemoteViews.setViewVisibility(R.id.layout_videoview, View.VISIBLE);
		Bitmap thumbnailsBitmap = BitmapFactory.decodeFile(
				DataFileTools.getInstance().getImageFilePath(mRecorder.getUrl())); 
		if (thumbnailsBitmap == null){
			thumbnailsBitmap = BitmapFactory.decodeResource(
					mContext.getResources(), R.drawable.message_video_bg);
		} 
		mRemoteViews.setImageViewBitmap(R.id.img_videomsg_detail, thumbnailsBitmap);
	}
	
	
	/**
	 * 显示音频信息
	 */
	public void setupAudioView(){
		mRemoteViews.setViewVisibility(R.id.layout_audioview, View.VISIBLE);
		mRemoteViews.setViewVisibility(R.id.msg_play_time, View.VISIBLE);
		int duration  = 0;
		if (!TextUtils.isEmpty(mRecorder.getFileName())){
			File file = new File(mRecorder.getFileName());
			if (file != null && file.exists()){
				new BigDecimal("2").setScale(0, BigDecimal.ROUND_HALF_UP);
				duration = (int)(Math.round(RecorderAudioManager.getDuration(file) / 1000.0 ));
			}
		}
		mRemoteViews.setTextViewText(R.id.msg_play_time, duration + "\"");
	}
	
	/**
	 * 显示位置信息
	 */
	public void setupLocationView(){
		mRemoteViews.setViewVisibility(R.id.layout_locationview, View.VISIBLE);
		mRemoteViews.setTextViewText(R.id.tv_location_detail, mRecorder.getLabel());
	}
	
	/**
	 * 显示连接信息
	 */
	public void setupLinkView(){
		mRemoteViews.setViewVisibility(R.id.layout_linkview, View.VISIBLE);
		String titleHtml = "<a href=" + mRecorder.getUrl() + ">" + mRecorder.getTitle()  + 
					       "</a>";
		String descHtml = "<div>" + mRecorder.getDescription() + 
						  "</div>";
		Log.i(TAG, "titleHtml:" + titleHtml);
		Log.i(TAG, "descHtml:" + descHtml);
		mRemoteViews.setTextViewText(R.id.tv_link_title, Html.fromHtml(titleHtml));
		mRemoteViews.setTextViewText(R.id.tv_link_desprition, Html.fromHtml(descHtml));
	}
	
	/**
	 * 更新ImageView
	 * @param viewId 控件ID
	 * @param url 请求地址
	 * @param maxWidth 图片请求最大宽度
	 * @param maxHeight 图片请求最大高度
	 * @param isUserIcon 是否是用户头像
	 */
	private void updateImageView(final int viewId, int maxWidth, int maxHeight, 
			final boolean isUserIcon){
		String url = null;
		if (isUserIcon) {
			url = mBindUser.getHeadImageUrl();
		} else {
			url = mRecorder.getUrl();
		}
		if (TextUtils.isEmpty(url)){
			return ;
		}
		Log.i(TAG, "ImageUrl:" + url);
		
		WeApplication.getImageLoader().get(url, new ImageListener() {
			
			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				// TODO Auto-generated method stub
				Bitmap bitmap = response.getBitmap();
				Log.i(TAG, "Bitmap:" + bitmap);
				if (bitmap == null){
					return ;
				}
				
				if (isUserIcon){
					bitmap = ImageUtil.getInstance().createCircleImage(bitmap);
				} 
				
				if (mRemoteViews == null){
					initRemoteViews();
				}
				mRemoteViews.setImageViewBitmap(viewId, bitmap);
				
				AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(mContext);
		        int[] appWidgetIds = appWidgetManger.getAppWidgetIds(new ComponentName(mContext, WeChatWidget.class));
		        appWidgetManger.updateAppWidget(appWidgetIds, mRemoteViews);
			}
		}, maxWidth, maxHeight);
	}
}
