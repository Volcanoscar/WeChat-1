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
import com.tcl.wechat.action.audiorecorder.RecorderAudioManager;
import com.tcl.wechat.action.audiorecorder.RecorderPlayerManager;
import com.tcl.wechat.action.audiorecorder.listener.AudioPlayCompletedListener;
import com.tcl.wechat.common.Config;
import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.database.WeiRecordDao;
import com.tcl.wechat.database.WeiUserDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.WeiXinMessage;
import com.tcl.wechat.ui.activity.BaiduMapActivity;
import com.tcl.wechat.ui.activity.ChatActivity;
import com.tcl.wechat.ui.activity.FamilyBoardMainActivity;
import com.tcl.wechat.ui.activity.FilePreviewActivity;
import com.tcl.wechat.ui.activity.PlayVideoActivity;
import com.tcl.wechat.ui.activity.ShowImageActivity;
import com.tcl.wechat.ui.activity.ShowTextActivity;
import com.tcl.wechat.ui.activity.WebViewActivity;
import com.tcl.wechat.utils.DataFileTools;
import com.tcl.wechat.utils.DateUtils;
import com.tcl.wechat.utils.ExpressionUtil;
import com.tcl.wechat.utils.FileSizeUtil;
import com.tcl.wechat.utils.ImageUtil;
import com.tcl.wechat.utils.SystemTool;

/**
 * 微信留言板快捷方式
 * @author rex.lei
 * 
 */
public class WeChatWidget extends AppWidgetProvider implements IConstant{
	
	private static final String TAG = WeChatWidget.class.getSimpleName();
	
	private Context mContext = WeApplication.getContext();
	
	private RemoteViews mRemoteViews;
	
	private BindUser mBindUser;
	
	private WeiXinMessage mRecorder;
	
	private static int mUpdateCnt = 0;
	
	private static boolean bPlaying = false;
	
	private String mLastUserOpenid = null;
	
	private int[] mAllViewId = new int[]{
			R.id.tv_textmsg_detail, 
			R.id.img_imagemsg_detail, 
			R.id.layout_videoview,
			R.id.layout_audioview,
			R.id.layout_locationview,
			R.id.layout_linkview,
			R.id.layout_fileview,
			R.id.layout_musicview,
			R.id.img_default_info,
			R.id.default_user_info};
	
	//背景
	private int[] mStyleBg = new int[]{
			R.drawable.appwidget_style1_bg,
			R.drawable.appwidget_style2_bg };
	
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		Log.i(TAG, "onEnabled-->>");
	}

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
				WeiXinMessage recorder = bundle.getParcelable("WeiXinMsgRecorder");
				Log.i(TAG, "Receive msg:" + recorder);
				if (recorder == null){
					return ;
				}
				
				//消息信息
				mRecorder = recorder;
				if (!mRecorder.getOpenid().equals(mLastUserOpenid)){
					mBindUser = WeiUserDao.getInstance().getUser(recorder.getOpenid());
				}
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
				mRecorder = WeiRecordDao.getInstance().getLatestRecorder(mBindUser.getOpenId());
				Log.d(TAG, "mRecorder:" + mRecorder);
			} else {
				mRecorder = null;
			}
			
			AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(context);
	        int[] appWidgetIds = appWidgetManger.getAppWidgetIds(new ComponentName(context, WeChatWidget.class));
	        updateAppWidget(context, appWidgetManger, appWidgetIds);
		} else if (ACTION_DATA_CLEARED.equals(action)){
			//清除数据
			mBindUser = null;
			mRecorder = null;
			AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(context);
	        int[] appWidgetIds = appWidgetManger.getAppWidgetIds(new ComponentName(context, WeChatWidget.class));
	        updateAppWidget(context, appWidgetManger, appWidgetIds);
		} else if (ACTION_MAINVIEW.equals(action)){
			//进入主界面
			resetPlayAnim();
			Intent mainintent = new Intent(context, FamilyBoardMainActivity.class);
			mainintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(mainintent);
		} else if (ACTION_STYLE_CHANGE.equals(action)){
			//切换风格
			if (mRemoteViews == null){
				initRemoteViews();
			}
			mRemoteViews.setImageViewResource(R.id.img_appwidget_bg, mStyleBg[Config.mWidgetStyleIndex]);
			if (Config.mWidgetStyleIndex == 1){
				Config.mWidgetStyleIndex = 0;
			}else {
				Config.mWidgetStyleIndex = 1;
			}
			AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(mContext);
			int[] appWidgetIds = appWidgetManger.getAppWidgetIds(new ComponentName(mContext, WeChatWidget.class));
	        appWidgetManger.updateAppWidget(appWidgetIds, mRemoteViews);
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
			Intent actionIntent = createIntent(context, intent);
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
	public Intent createIntent(Context context, Intent intent){
		Intent mIntent = null;
		
		if (mRecorder == null){
			mRecorder = WeiRecordDao.getInstance().getLatestRecorder();
		}
		
		if (mRecorder == null){
			Log.i(TAG, "No Message!!");
			return null;
		}
		
		if (mBindUser == null ){
			mBindUser = WeiUserDao.getInstance().getUser(mRecorder.getOpenid());
		}
		String action = intent.getAction();
		if (ACTION_CHATVIEW.equals(action)) {
			//进入聊天界面, 输入框需要获取焦点
			resetPlayAnim();
			mRecorder = WeiRecordDao.getInstance().getLatestRecorder(mBindUser.getOpenId());
			if (mBindUser != null && mRecorder != null){
				mIntent = new Intent(context, ChatActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("bindUser", mBindUser);
				bundle.putBoolean("NeedReply", true);
				mIntent.putExtras(bundle);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
						Intent.FLAG_ACTIVITY_CLEAR_TASK);
			}
		} else if (ACTION_CHATVIEW2.equals(action)) {
			//进入聊天界面
			resetPlayAnim();
			mRecorder = WeiRecordDao.getInstance().getLatestRecorder(mBindUser.getOpenId());
			if (mBindUser != null && mRecorder != null){
				mIntent = new Intent(context, ChatActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("bindUser", mBindUser);
				bundle.putBoolean("NeedReply", false);
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
					contentCharSeq = new ExpressionUtil().StringToSpannale(context, 
							new StringBuffer(contentCharSeq));
					
					mIntent = new Intent(context, ShowTextActivity.class);
					mIntent.putExtra("Content", contentCharSeq);
					mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
							Intent.FLAG_ACTIVITY_CLEAR_TASK);
				}
			} 
		} else if (ACTION_SHOW_IMAGE.equals(action)) {
			//显示图像
			mIntent = new Intent(context, ShowImageActivity.class);
			mIntent.putExtra("WeiXinMsgRecorder", mRecorder);
			mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
					Intent.FLAG_ACTIVITY_CLEAR_TASK);
		} else if (ACTION_PLAY_AUDIO.equals(action)) {
			//播放音频
			String fileName = mRecorder.getFileName();
			if (!TextUtils.isEmpty(fileName)){

				playAudio(fileName);
				//更新播放状态
			}
		} else if (ACTION_PLAY_VIDEO.equals(action)) {
			//播放视频
			if (!TextUtils.isEmpty(mRecorder.getFileName())){
				mIntent = new Intent(mContext, PlayVideoActivity.class);
				mIntent.putExtra("FilePath", mRecorder.getFileName());
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
						Intent.FLAG_ACTIVITY_CLEAR_TASK);
			}
		} else if (ACTION_SHOW_LOCATION.equals(action)) {
			//位置信息
			if (mRecorder != null){
				mIntent = new Intent(context, BaiduMapActivity.class);
				mIntent.putExtra("WeiXinMsgRecorder", mRecorder);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
						Intent.FLAG_ACTIVITY_CLEAR_TASK);
			}
		} else if (ACTION_SHOW_LINK.equals(action)) {
			//链接信息
			String url = mRecorder.getUrl();
			Log.i(TAG, "LinkUrl:" + url);
			if (!TextUtils.isEmpty(url)){
				mIntent = new Intent(context, WebViewActivity.class); 
				mIntent.putExtra("LinkUrl", url);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
						Intent.FLAG_ACTIVITY_CLEAR_TASK);
			}  
		} else if (ACTION_SHOW_FILE.equals(action)){
			//文件信息
			if (mRecorder != null){
				mIntent = new Intent(context, FilePreviewActivity.class);
				mIntent.putExtra("WeiXinMsgRecorder", mRecorder);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
						Intent.FLAG_ACTIVITY_CLEAR_TASK);
			}
		} else if (ACTION_SHOW_MUSIC.equals(action)){
			
			//音乐信息
			String url = mRecorder.getUrl();
			if (!TextUtils.isEmpty(url)){
				mIntent = new Intent(mContext, WebViewActivity.class);
				mIntent.putExtra("LinkUrl", mRecorder.getUrl());
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
						Intent.FLAG_ACTIVITY_CLEAR_TASK);
			}
		}
		return mIntent;
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
	 * 播放音频
	 * @param recorder
	 */
	private void playAudio(String fileName){
		
		if (!RecorderPlayerManager.getInstance().isPlaying() && !bPlaying){
			//播放音频
			if (!TextUtils.isEmpty(fileName) ){
				RecorderPlayerManager.getInstance().play(fileName);
				RecorderPlayerManager.getInstance().setPlayCompletedListener(playCompletedListener);
			}
			
			mUpdateCnt = 0;
			bPlaying = true;
			new Thread(mPlayIngRunnable).start();
		} else { //如果正在播放，点击后，则需要暂停当前播放
			resetPlayAnim();
		}
	}
	
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
		if (RecorderPlayerManager.getInstance() != null) {
			RecorderPlayerManager.getInstance().stop();
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
		mRemoteViews.setOnClickPendingIntent(R.id.img_appwidget, mainPendingIntent);
		
		//切换风格
		Intent styleIntent = new Intent(ACTION_STYLE_CHANGE);
		PendingIntent stylePendingIntent = PendingIntent.getBroadcast(context, 0, styleIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.imgbtn_appwidget, stylePendingIntent);
		
		//点击默认图标，进入主界面
		Intent mainIntent2 = new Intent(ACTION_MAINVIEW);
		PendingIntent mainPendingIntent2 = PendingIntent.getBroadcast(context, 0, mainIntent2, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.img_default_info, mainPendingIntent2);
		
		//进入聊天界面
		Intent chatIntent = new Intent(ACTION_CHATVIEW);
		PendingIntent chatPendingIntent = PendingIntent.getBroadcast(context, 0, chatIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_reply, chatPendingIntent);
		
		//用户头像，进入聊天页面
		Intent chatIntent2 = new Intent(ACTION_CHATVIEW2);
		PendingIntent chatPendingIntent2 = PendingIntent.getBroadcast(context, 0, chatIntent2, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.img_user_icon, chatPendingIntent2);
		
		//文本消息
		Intent textIntent = new Intent(ACTION_SHOW_TEXT);
		PendingIntent textPendingIntent = PendingIntent.getBroadcast(context, 0, textIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.tv_textmsg_detail, textPendingIntent);
		
		Intent imageIntent = new Intent(ACTION_SHOW_IMAGE);
		PendingIntent imagePendingIntent = PendingIntent.getBroadcast(context, 0, imageIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.img_imagemsg_detail, imagePendingIntent);
		
		//音频消息
		Intent audioIntent = new Intent(ACTION_PLAY_AUDIO);
		PendingIntent audioPendingIntent = PendingIntent.getBroadcast(context, 0, audioIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.layout_audioview, audioPendingIntent);
		
		//视频消息
		Intent videoIntent = new Intent(ACTION_PLAY_VIDEO);
		PendingIntent videoPendingIntent = PendingIntent.getBroadcast(context, 0, videoIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.layout_videoview, videoPendingIntent);
		
		//位置信息
		Intent loacationIntent = new Intent(ACTION_SHOW_LOCATION);
		PendingIntent locationPendingIntent = PendingIntent.getBroadcast(context, 0, loacationIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.layout_locationview, locationPendingIntent);
		
		//链接信息
		Intent linkIntent = new Intent(ACTION_SHOW_LINK);
		PendingIntent linkPendingIntent = PendingIntent.getBroadcast(context, 0, linkIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.layout_linkview, linkPendingIntent);
		
		//文件信息
		Intent filentent = new Intent(ACTION_SHOW_FILE);
		PendingIntent filePendingIntent = PendingIntent.getBroadcast(context, 0, filentent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.layout_fileview, filePendingIntent);
		
		//音乐信息
		Intent musicIntent = new Intent(ACTION_SHOW_MUSIC);
		PendingIntent musicPendingIntent = PendingIntent.getBroadcast(context, 0, musicIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.layout_musicview, musicPendingIntent);
		
		appWidgetManager.updateAppWidget(appWidgetIds, mRemoteViews);
	}
	
	/**
	 * 数据初始化
	 */
	private void initData(){
		mRecorder = WeiRecordDao.getInstance().getLatestRecorder();
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
			
			//更新时间
			mRemoteViews.setTextViewText(R.id.tv_msg_receiver_time, 
					DateUtils.getTimeShort(mRecorder.getCreatetime()));
	
			//更新消息记录
			String msgType = mRecorder.getMsgtype();
			Log.i(TAG, "msgType : " + msgType);
			if (ChatMsgType.TEXT.equals(msgType)){
				//文字显示控件
				resetViews(R.id.tv_textmsg_detail);
				setupTextView(context);
				
			} else if (ChatMsgType.IMAGE.equals(msgType)) {
				//图像显示控件
				resetViews(R.id.img_imagemsg_detail);
				setupImageView();
				
			} else if (ChatMsgType.VOICE.equals(msgType)) {
				//音频显示控件
				resetViews(R.id.layout_audioview);
				setupAudioView();
				
			} else if (ChatMsgType.VIDEO.equals(msgType) ||
					ChatMsgType.SHORTVIDEO.equals(msgType)) {
				//视频显示控件
				resetViews(R.id.layout_videoview);
				setupVideoView();
				
			} else if (ChatMsgType.MAP.equals(msgType)) {
				//位置显示控件
				resetViews(R.id.layout_locationview);
				setupLocationView();
				
			} else if (ChatMsgType.WEB.equals(msgType)) {
				//链接显示控件
				resetViews(R.id.layout_linkview);
				setupLinkView();
				
			} else if (ChatMsgType.FILE.equals(msgType)){
				//文件显示控件
				resetViews(R.id.layout_fileview);
				setupFileView();
				
			} else if (ChatMsgType.MUSIC.equals(msgType)){
				//音乐显示控件
				resetViews(R.id.layout_musicview);
				setupMusicView();
			}
			
			//更新用户头像
			setupUserView();
		} else {
			
			//控件复位
			resetViews(0);
			
			//设置默认显示内容
			setupDefaultView();
		}
		appWidgetManager.updateAppWidget(appWidgetIds, mRemoteViews);
	}
	
	private void resetViews(int viewId){
		for (int i = 0; i < mAllViewId.length; i++) {
			if (viewId != mAllViewId[i]){
				mRemoteViews.setViewVisibility(mAllViewId[i], View.GONE);
			} else {
				mRemoteViews.setViewVisibility(mAllViewId[i], View.VISIBLE);
			}
		}
	}

	/**
	 * 显示用户信息
	 */
	public void setupUserView(){
		
		if (mLastUserOpenid == null || !mLastUserOpenid.equals(mBindUser.getOpenId())){
			
			mRemoteViews.setViewVisibility(R.id.btn_reply, View.VISIBLE);
			mRemoteViews.setViewVisibility(R.id.tv_user_name, View.VISIBLE);
			mRemoteViews.setViewVisibility(R.id.tv_msg_receiver_time, View.VISIBLE);
			
			if ("-2".equals(mBindUser.getSex())){
				mRemoteViews.setImageViewResource(R.id.img_user_icon, R.drawable.default_device_icon);
			} else { 
				updateImageView(R.id.img_user_icon, 80, 80, true);	
			}
			String userName = mBindUser.getRemarkName();
			if (TextUtils.isEmpty(userName)){
				userName = mBindUser.getNickName();
			}
			mRemoteViews.setTextViewText(R.id.tv_user_name, userName);
		}
	}
	
	/**
	 * 默认显示内容
	 */
	public void setupDefaultView(){
		
		mRemoteViews.setViewVisibility(R.id.btn_reply, View.GONE);
		mRemoteViews.setViewVisibility(R.id.img_user_icon, View.GONE);
		mRemoteViews.setViewVisibility(R.id.tv_user_name, View.GONE);
		mRemoteViews.setViewVisibility(R.id.tv_msg_receiver_time, View.GONE);
		
		mRemoteViews.setViewVisibility(R.id.img_user_icon, View.VISIBLE);
		mRemoteViews.setViewVisibility(R.id.default_user_info, View.VISIBLE);
		mRemoteViews.setViewVisibility(R.id.img_default_info, View.VISIBLE);
		//默认头像
		mRemoteViews.setImageViewResource(R.id.img_user_icon, R.drawable.head_default);
	}
	
	/**
	 * 显示文本信息
	 */
	public void setupTextView(Context context){
		CharSequence contentCharSeq = mRecorder.getContent();
		if (!TextUtils.isEmpty(contentCharSeq)){
			contentCharSeq = new ExpressionUtil().StringToCharacters(context, 
					new StringBuffer(contentCharSeq), true);
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
		if ("set".equals(mRecorder.getFormat())){
			if (!TextUtils.isEmpty(mRecorder.getFileName())){
				Bitmap bitmap = BitmapFactory.decodeFile(mRecorder.getFileName());
				if (mRemoteViews == null){
					initRemoteViews();
				}
				
				RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.appwidget_view);
				remoteViews.setImageViewBitmap(R.id.img_imagemsg_detail, bitmap);
				
				AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(mContext);
		        int[] appWidgetIds = appWidgetManger.getAppWidgetIds(new ComponentName(mContext, WeChatWidget.class));
		        appWidgetManger.updateAppWidget(appWidgetIds, remoteViews);
			}
		} else {
			updateImageView(R.id.img_imagemsg_detail, 400, 400, false);
		}
	}
	
	/**
	 * 显示视频信息
	 */
	public void setupVideoView(){
		Bitmap thumbnailsBitmap = BitmapFactory.decodeFile(
				DataFileTools.getImageFilePath(mRecorder.getUrl())); 
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
		if (TextUtils.isEmpty(mRecorder.getLabel())){
			mRecorder.setLabel(mContext.getResources().getString(R.string.unknown_location));
		}
		mRemoteViews.setTextViewText(R.id.tv_location_detail, mRecorder.getLabel());
	}
	
	/**
	 * 显示连接信息
	 */
	public void setupLinkView(){
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
	 * 文件显示控件
	 */
	public void setupFileView(){
		
		mRemoteViews.setTextViewText(R.id.file_title, mRecorder.getContent());
		String fileSize = FileSizeUtil.FormetFileSize(Long.parseLong(mRecorder.getFileSize()));
		mRemoteViews.setTextViewText(R.id.file_detail, fileSize);
		String fileType = mRecorder.getLabel();
		if ("mp3".equals(fileType)){
			mRemoteViews.setImageViewResource(R.id.img_file_icon, R.drawable.file_mp3_icon);
		} else if("wma".equals(fileType)){
			mRemoteViews.setImageViewResource(R.id.img_file_icon, R.drawable.file_wma_icon);
		} else if ("png".equals(fileType)){
			mRemoteViews.setImageViewResource(R.id.img_file_icon, R.drawable.file_png_icon);
		} else if ("jpg".equals(fileType) 
				|| "jepg".equals(fileType)){
			mRemoteViews.setImageViewResource(R.id.img_file_icon, R.drawable.file_jpg_icon);
		} else if ("doc".equals(fileType) 
				|| "docx".equals(fileType)){
			mRemoteViews.setImageViewResource(R.id.img_file_icon, R.drawable.file_doc_icon);
		} else if ("ppt".equals(fileType)
				|| "pptx".equals(fileType)){
			mRemoteViews.setImageViewResource(R.id.img_file_icon, R.drawable.file_ppt_icon);
		} else if ("xls".equals(fileType)
				|| "xlsx".equals(fileType)){
			mRemoteViews.setImageViewResource(R.id.img_file_icon, R.drawable.file_xls_icon);
		} else if ("pdf".equals(fileType)){
			mRemoteViews.setImageViewResource(R.id.img_file_icon, R.drawable.file_pdf_icon);
		} else if ("txt".equals(fileType)){
			mRemoteViews.setImageViewResource(R.id.img_file_icon, R.drawable.file_txt_icon);
		} else {
			mRemoteViews.setImageViewResource(R.id.img_file_icon, R.drawable.file_def_icon);
		}
	}
	
	/**
	 * 音乐显示控件
	 */
	public void setupMusicView(){
		mRemoteViews.setTextViewText(R.id.music_title, mRecorder.getContent());
		mRemoteViews.setTextViewText(R.id.music_artist, mRecorder.getLabel());
		mRemoteViews.setImageViewResource(R.id.img_music_icon, R.drawable.file_music_icon);
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
			defaultUserIcon();
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
				
				RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.appwidget_view);
				remoteViews.setImageViewBitmap(viewId, bitmap);
				
				AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(mContext);
		        int[] appWidgetIds = appWidgetManger.getAppWidgetIds(new ComponentName(mContext, WeChatWidget.class));
		        appWidgetManger.updateAppWidget(appWidgetIds, remoteViews);
			}
		}, maxWidth, maxHeight);
	}
	
	/**
	 * 用户默认头像
	 */
	private void defaultUserIcon(){
	
		if (mRemoteViews == null){
			initRemoteViews();
		}
		mRemoteViews.setImageViewResource(R.id.img_user_icon, R.drawable.head_default);
		AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(mContext);
        appWidgetManger.updateAppWidget(new int[]{R.id.img_user_icon}, mRemoteViews);
	}
}
