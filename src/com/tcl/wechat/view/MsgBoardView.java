package com.tcl.wechat.view;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.action.audiorecorder.RecorderAudioManager;
import com.tcl.wechat.action.audiorecorder.RecorderPlayerManager;
import com.tcl.wechat.action.audiorecorder.listener.AudioPlayCompletedListener;
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.database.WeiRecordDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.WeiXinMessage;
import com.tcl.wechat.ui.activity.BaiduMapActivity;
import com.tcl.wechat.ui.activity.ChatActivity;
import com.tcl.wechat.ui.activity.FilePreviewActivity;
import com.tcl.wechat.ui.activity.PlayVideoActivity;
import com.tcl.wechat.ui.activity.ShowImageActivity;
import com.tcl.wechat.ui.activity.ShowTextActivity;
import com.tcl.wechat.ui.activity.WebViewActivity;
import com.tcl.wechat.utils.DateUtils;
import com.tcl.wechat.utils.ExpressionUtil;
import com.tcl.wechat.utils.FileSizeUtil;
import com.tcl.wechat.utils.SystemInfoUtil;

/**
 * 家庭留言板视图
 * @author rex.lei
 *
 */
public class MsgBoardView extends LinearLayout{

	private static final String TAG = MsgBoardView.class.getSimpleName();
	
	private static final int MSG_MSGCNT_REDUCE = 0x01;
	
	private static final int MSG_MSGCNT_EMPTY = 0x02;
	
	private Context mContext;
	
	private LayoutInflater mInflater;
	
	private View mView;
	
	/**
	 * 用户显示控件
	 */
	private UserInfoView mUserInfoViewUv;
	
	/**
	 * 消息时间
	 */
	private TextView mMsgReceiveTimeTv;
	
	/**
	 * 未读消息标识
	 */
	private TextView mUnReadMsgIndicatorTv;
	
	/**
	 * 回复按钮
	 */
	private Button mReplyBtn;
	
	private FrameLayout mMsgConentLayout;
	
	/**
	 * 用户openId 和消息openId必须匹配方可显示
	 */
	private BindUser mBindUser;
	private WeiXinMessage mRecorder;
	
	/**
	 * 音频播放动画View；
	 */
	private View mPlaySoundAnimView; 
	
	/**
	 * 未读消息
	 */
	private int mAllUnReadedMsgCnt = 0;
	private int mUnReadedMsgCnt = 0;;
	
	public MsgBoardView(Context context) {
		this(context, null);
	}
	
	public MsgBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}
	
	public void setupView(int screen){
		int layoutId = mContext.getResources().getIdentifier("layout_msgboard_" + (screen + 1), 
								"layout", mContext.getPackageName());
		mView = inflate(mContext, layoutId, this);
		
		mUserInfoViewUv = (UserInfoView) mView.findViewById(R.id.uv_familboard_userinfo);
		mUnReadMsgIndicatorTv = (TextView) mView.findViewById(R.id.tv_unread_msg_hint);
		mMsgReceiveTimeTv = (TextView) mView.findViewById(R.id.tv_msgreceive_time);
		mReplyBtn = (Button) mView.findViewById(R.id.btn_familyborad_replay);
		mMsgConentLayout = (FrameLayout) mView.findViewById(R.id.layout_msginfo); 
		
		mUserInfoViewUv.setUserNameBackGround(false);
		mUserInfoViewUv.setTextSize(22);
		mMsgReceiveTimeTv.setTypeface(WeApplication.getInstance().getTypeface1());
		mUnReadMsgIndicatorTv.setText(String.format(getResources().getString(R.string.page_Indicator), 0, 0));
		
		updateMsgStatue();
		mReplyBtn.setOnClickListener(replyClick);
	}
	
	/**
	 * 添加数据
	 * @param bindUser 用户信息
	 * @param recorders 消息记录
	 */
	public void addData(BindUser bindUser, WeiXinMessage recorder){
		mBindUser = bindUser;
		mRecorder = recorder;
		mAllUnReadedMsgCnt = WeiRecordDao.getInstance().getAllUnreadedMsgCnt(mBindUser.getOpenId());
		mUnReadedMsgCnt = mAllUnReadedMsgCnt;
		upadteView(mBindUser, mRecorder);
	}
	
	private OnClickListener replyClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessageDelayed(MSG_MSGCNT_EMPTY, 500);
			
			Intent intent = new Intent(mContext, ChatActivity.class);
			Bundle bundle = new Bundle();
			bundle.putParcelable("bindUser", mBindUser);
			intent.putExtras(bundle);
			mContext.startActivity(intent);
		}
	};
	
	
	/**
	 * 收到信消息
	 * @param recorder
	 */
	public void receiveNewMessage(BindUser bindUser ,WeiXinMessage recorder){
		if (bindUser == null || recorder == null){
			return ;
		}
		
		mBindUser = bindUser;
		mRecorder = recorder;
		mAllUnReadedMsgCnt = WeiRecordDao.getInstance().getAllUnreadedMsgCnt(mBindUser.getOpenId());
		if (mUnReadedMsgCnt < mAllUnReadedMsgCnt) {
			mUnReadedMsgCnt ++;
		}
		upadteView(mBindUser, mRecorder);
	}
	
	/**
	 * 更新用户信息
	 */
	public void updateBindUser(BindUser bindUser){
		Log.i(TAG, "updateBindUser-->>");
		if (bindUser == null) {
			return ;
		}
		
		mBindUser = bindUser;
		
		//更新用户信息
		String headImageUrl = bindUser.getHeadImageUrl();
		String userName = bindUser.getRemarkName();
		mUserInfoViewUv.setUserName(userName);
		if (headImageUrl != null && userName != null){
			mUserInfoViewUv.setUserIcon(headImageUrl, false);
		} else if ("-2".equals(bindUser.getSex())){
			mUserInfoViewUv.setUserIcon(R.drawable.default_device_icon, true);
		}
	}
	
	/**
	 * 更新界面
	 * @param bindUser
	 * @param recorders
	 */
	private void upadteView(BindUser bindUser, final WeiXinMessage recorder){
		if (bindUser == null || recorder == null){
			return ;
		}
		
		if (!bindUser.getOpenId().equals(recorder.getOpenid())){
			Log.w(TAG, "bindUser's Openid isn't equal Recorder's Opendid");
			return ;
		}
		
		updateBindUser(bindUser);
		mMsgConentLayout.removeAllViews();
		String time = recorder.getCreatetime();
		mMsgReceiveTimeTv.setText(DateUtils.getTimeShort(time));
		mMsgConentLayout.addView(getView(recorder));
		updateMsgStatue();
		
		//更新未读消息状态
		if (SystemInfoUtil.isActivityForeground(ChatActivity.class.getName())) {
			mHandler.sendEmptyMessageDelayed(MSG_MSGCNT_REDUCE, 1000);
		}
	}
	
	/**
	 * 更新未读消息提示状态
	 */
	private void updateMsgStatue(){
		
		Log.i(TAG, "22AllUnReadedMsgCnt:" + mAllUnReadedMsgCnt + ", UnReadedMsgCnt:" + mUnReadedMsgCnt);
		
		if (mUnReadedMsgCnt > 0 && mUnReadedMsgCnt <= mAllUnReadedMsgCnt){
			mUnReadMsgIndicatorTv.setVisibility(View.VISIBLE);
			mUnReadMsgIndicatorTv.setText(String.format(mContext.getString(R.string.page_Indicator), 
					mUnReadedMsgCnt, mAllUnReadedMsgCnt));
		} else {
			mUnReadMsgIndicatorTv.setVisibility(View.GONE);
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_MSGCNT_REDUCE:
				Log.i(TAG, "AllUnReadedMsgCnt:" + mAllUnReadedMsgCnt + ", UnReadedMsgCnt:" + mUnReadedMsgCnt);
				if (mUnReadedMsgCnt > 0 && "0".equals(mRecorder.getReaded())){
					mRecorder.setReaded("1");
					mUnReadedMsgCnt --;
					updateMsgStatue();
					WeiRecordDao.getInstance().updateMessageReadState(mRecorder.getMsgid(), "1");
				} 
				break;

			case MSG_MSGCNT_EMPTY:
				mAllUnReadedMsgCnt = 0;
				mUnReadedMsgCnt = 0;
				updateMsgStatue();
				WeiRecordDao.getInstance().updateAllMessageReadState(mBindUser.getOpenId());
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 获取图片
	 * @param msgType
	 * @return
	 */
	private View getView(final WeiXinMessage recorder) {
		View mView = null;
		
		//更新消息信息
		Log.i(TAG, "recorder:" + recorder.toString());
		String msgType = recorder.getMsgtype();
		Log.i(TAG, "Msgtype:" + msgType);
		if (ChatMsgType.TEXT.equals(msgType)){
			//加载文本显示控件
			mView = setupTextMsgView(recorder);
			
		} else if (ChatMsgType.VOICE.equals(msgType)){
			//加载音频显示控件
			mView = setupAudioMsgView(recorder);
			
		} else if (ChatMsgType.VIDEO.equals(msgType) 
				|| ChatMsgType.SHORTVIDEO.equals(msgType)){
			//加载视频显示控件
			mView = setupVideoMsgView(recorder);
			
		} else if (ChatMsgType.IMAGE.equals(msgType)){
			//加载图片显示控件
			mView = setupImageMsgView(recorder);
			
		} else if (ChatMsgType.MAP.equals(msgType)){ 
			//加载地理位置显示控件
			mView = setupLocationView(recorder);
			
		} else if (ChatMsgType.WEB.equals(msgType)) {
			//加载链接显示控件
			mView = setupLinkView(recorder);
		} else if (ChatMsgType.FILE.equals(msgType)) {
			//加载文件显示控件
			mView = setupFileView(recorder);
		} else if (ChatMsgType.MUSIC.equals(msgType)){
			//加载音乐文件显示控件
			mView = setupMusicFileView(recorder);
		}
		
		if (mView == null){
			return null;
		}
		
		mView.setClickable(true);
		mView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = createIntent(recorder);
				if (intent != null){
					//更新消息状态
					mHandler.sendEmptyMessageDelayed(MSG_MSGCNT_REDUCE, 1000);
					
					//启动Activity
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent);
				}
			}
		});
		return mView;
	}
	
	private Intent createIntent(WeiXinMessage recorder) {
		Intent mIntent = null;
		String msgType = recorder.getMsgtype();
		Log.i(TAG, "Msgtype:" + msgType);
		if (ChatMsgType.TEXT.equals(msgType)){
			mIntent = new Intent(mContext, ShowTextActivity.class);
			mIntent.putExtra("Content", recorder.getContent());
			
		} else if (ChatMsgType.VOICE.equals(msgType)){
			//加载音频显示控件
			playAudio(recorder.getFileName());
			
		} else if (ChatMsgType.VIDEO.equals(msgType) 
				|| ChatMsgType.SHORTVIDEO.equals(msgType)){
			// mIntent = new Intent(Intent.ACTION_VIEW);
			// String fileName =
			// DataFileTools.getInstance().getVideoFilePath(recorder.getMediaid());
			// mIntent.setDataAndType(Uri.parse(fileName), "video/mp4");
			if (!TextUtils.isEmpty(recorder.getFileName())){
				mIntent = new Intent(mContext, PlayVideoActivity.class);
				mIntent.putExtra("FilePath", recorder.getFileName());
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
		} else if (ChatMsgType.IMAGE.equals(msgType)){
			mIntent = new Intent(mContext, ShowImageActivity.class);
			mIntent.putExtra("WeiXinMsgRecorder", recorder);
			
		} else if (ChatMsgType.MAP.equals(msgType)){ 
			mIntent = new Intent(mContext, BaiduMapActivity.class);
			mIntent.putExtra("WeiXinMsgRecorder", recorder);
			
		} else if (ChatMsgType.WEB.equals(msgType)) {
			mIntent = new Intent(mContext, WebViewActivity.class); 
			mIntent.putExtra("LinkUrl", recorder.getUrl());
		} else if (ChatMsgType.FILE.equals(msgType)){
			mIntent = new Intent(mContext, FilePreviewActivity.class);
			mIntent.putExtra("WeiXinMsgRecorder", recorder);
		} else if (ChatMsgType.MUSIC.equals(msgType)){
			mIntent = new Intent(mContext, WebViewActivity.class);
			mIntent.putExtra("LinkUrl", recorder.getUrl());
		}
		return mIntent;
	}
	
	/**
	 * 创建文本消息视图
	 * @param recorder 消息内容
	 * @return
	 */
	private View setupTextMsgView(final WeiXinMessage recorder){
		View mMsgView = mInflater.inflate(R.layout.layout_msgboard_textview, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mMsgView.setLayoutParams(params);
		TextView textInfoTv = (TextView) mMsgView.findViewById(R.id.tv_msgboard_textinfo);
		CharSequence contentCharSeq = recorder.getContent();
		if (!TextUtils.isEmpty(contentCharSeq)){
			contentCharSeq = new ExpressionUtil().StringToSpannale(mContext, 
					new StringBuffer(contentCharSeq));
		} else {
			contentCharSeq = mContext.getString(R.string.no_message);
		}
		Log.i(TAG, "contentCharSeq:" + contentCharSeq);
		textInfoTv.setText(contentCharSeq);
		textInfoTv.setTypeface(WeApplication.getInstance().getTypeface2());
		return mMsgView;
	}
	
	/**
	 * 创建音频消息视图
	 * @param recorder 消息内容
	 * @return
	 */
	private View setupAudioMsgView(WeiXinMessage recorder){
		//创建音频显示视图
		View mVoiceView = mInflater.inflate(R.layout.layout_chat_voice_view, null);
		ImageButton imageBtn = (ImageButton) mVoiceView.findViewById(R.id.imgbtn_msgboard_voice);
		TextView mTimeLengthTv = (TextView) mVoiceView.findViewById(R.id.tv_voice_palytime);
		
		//TODO 获取音频文件播放的长度
		int duration  = 0;
		final String fileName = recorder.getFileName();
		Log.i(TAG, "FileName:" + fileName);
		if (!TextUtils.isEmpty(fileName)){
			File file = new File(fileName);
			if (file != null && file.exists()){
				duration = (int)(Math.round(RecorderAudioManager.getDuration(file) / 1000.0 ));
			}
		}
		Log.i(TAG, "duration:" + duration);
		mTimeLengthTv.setText(String.valueOf(duration) + "\"");
		imageBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				playAudio(fileName);
			}
		});
		return mVoiceView;
	}
	
	/**
	 * 创建图片消息视图
	 * @param recorder
	 * @return
	 */
	private View setupImageMsgView(final WeiXinMessage recorder){
		View mMsgView = null;
		mMsgView = mInflater.inflate(R.layout.layout_chat_imageview, null);
		ImageView imageView = (ImageView) mMsgView.findViewById(R.id.img_src_image);
		ProgressBar loadProgressBar = (ProgressBar) mMsgView.findViewById(R.id.loading);
		updateImage(recorder, imageView, loadProgressBar);
		return mMsgView;
	}
	
	/**
	 * 地理位置显示缩略图
	 * @param recorder
	 * @return
	 */
	private View setupLocationView(final WeiXinMessage recorder){
		View mMsgView = null;
		mMsgView = mInflater.inflate(R.layout.layout_msgboard_mapview, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(260, 200);
		mMsgView.setLayoutParams(params);
		TextView locationInfoTv = (TextView) mMsgView.findViewById(R.id.tv_location_info);
		if (TextUtils.isEmpty(recorder.getLabel())){
			recorder.setLabel(mContext.getResources().getString(R.string.unknown_location));
		}
		locationInfoTv.setText(recorder.getLabel());
		return mMsgView;
	}
	
	/**
	 * 链接显示控件
	 * @param recorder
	 * @return
	 */
	private View setupLinkView(final WeiXinMessage recorder){
		Log.i(TAG, "WeiXinMsgRecorder:" + recorder);
		View mMsgView = mInflater.inflate(R.layout.layout_msgboard_linkview, null);
		TextView mTitleTv = (TextView) mMsgView.findViewById(R.id.link_title);
		TextView mDetailTv = (TextView) mMsgView.findViewById(R.id.link_detail);
		
		String titleHtml = "<a href=" + mRecorder.getUrl() + ">" + mRecorder.getTitle()  + 
			       		   "</a>";
		String descHtml =  "<div>" + mRecorder.getDescription() + 
				           "</div>";
		mTitleTv.setText(Html.fromHtml(titleHtml));
		mDetailTv.setText(Html.fromHtml(descHtml));
		return mMsgView;
	}

	/**
	 * 文件显示控件
	 * @param recorder
	 * @return
	 */
	private View setupFileView(final WeiXinMessage recorder){
		Log.i(TAG, "WeiXinMsgRecorder:" + recorder);
		View mMsgView = mInflater.inflate(R.layout.layout_msgboard_fileview, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(260, 200);
		mMsgView.setLayoutParams(params);
		
		ImageView mFileIconImg = (ImageView) mMsgView.findViewById(R.id.img_file_icon);
		TextView mTitleTv = (TextView) mMsgView.findViewById(R.id.file_title);
		TextView mDetailTv = (TextView) mMsgView.findViewById(R.id.file_detail);
		try {
			mTitleTv.setText(recorder.getContent());
			String fileSize = FileSizeUtil.FormetFileSize(Long.parseLong(recorder.getFileSize()));
			mDetailTv.setText(fileSize);
			String fileType = recorder.getLabel();
			if ("mp3".equals(fileType)){
				mFileIconImg.setImageResource(R.drawable.file_mp3_icon);
			} else if("wma".equals(fileType)){
				mFileIconImg.setImageResource(R.drawable.file_wma_icon);
			} else if ("png".equals(fileType)){
				mFileIconImg.setImageResource(R.drawable.file_png_icon);
			} else if ("jpg".equals(fileType)){
				mFileIconImg.setImageResource(R.drawable.file_jpg_icon);
			} else if ("jepg".equals(fileType)){
				mFileIconImg.setImageResource(R.drawable.file_jpg_icon);
			} else if ("doc".equals(fileType) 
					|| "docx".equals(fileType)){
				mFileIconImg.setImageResource(R.drawable.file_doc_icon);
			} else if ("ppt".equals(fileType)
					|| "pptx".equals(fileType)){
				mFileIconImg.setImageResource(R.drawable.file_ppt_icon);
			} else if ("xls".equals(fileType)
					|| "xlsx".equals(fileType)){
				mFileIconImg.setImageResource(R.drawable.file_xls_icon);
			} else if ("pdf".equals(fileType)){
				mFileIconImg.setImageResource(R.drawable.file_pdf_icon);
			} else if ("txt".equals(fileType)){
				mFileIconImg.setImageResource(R.drawable.file_txt_icon);
			} else {
				mFileIconImg.setImageResource(R.drawable.file_def_icon);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mMsgView;
	}
	
	/**
	 * 音乐文件显示控件
	 * @param recorder
	 * @return
	 */
	private View setupMusicFileView(final WeiXinMessage recorder){
		Log.i(TAG, "WeiXinMsgRecorder:" + recorder);
		View mMsgView = mInflater.inflate(R.layout.layout_msgboard_musicview, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(280, 200);
		mMsgView.setLayoutParams(params);
		
		TextView titleTv = (TextView) mMsgView.findViewById(R.id.music_title);
		TextView artistTv = (TextView) mMsgView.findViewById(R.id.music_artist);
		titleTv.setText(recorder.getContent());
		artistTv.setText(recorder.getLabel());
		return mMsgView;
	}
	
	/**
	 * 设置视频图片视图
	 * @param recorder
	 * @return
	 */
	private View setupVideoMsgView(final WeiXinMessage recorder){
		View mVideoView = mInflater.inflate(R.layout.layout_msgboard_video_view, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(260, 200);
		mVideoView.setLayoutParams(params);
		ImageView thumbnailsImg = (ImageView) mVideoView.findViewById(R.id.img_video_thumbnails);
		updateImage(recorder, thumbnailsImg, null);
		return mVideoView;
	}
	
	/**
	 * 播放音频
	 * @param recorder
	 */
	private void playAudio(String fileName){
		
		if (RecorderPlayerManager.getInstance() == null){
			Log.e(TAG, "RecorderPlayerManager is not init!!");
			return ;
		}
		
		if (!RecorderPlayerManager.getInstance().isPlaying()){
			//更新消息状态
			WeiRecordDao.getInstance().updateMessagePlayState(mRecorder.getMsgid());
			mHandler.sendEmptyMessageDelayed(MSG_MSGCNT_REDUCE, 1000);
			
			//播放音频
			if (!TextUtils.isEmpty(fileName) ){
				RecorderPlayerManager.getInstance().play(fileName);
				RecorderPlayerManager.getInstance().setPlayCompletedListener(playCompletedListener);
				
				//音频点击，进行播放动画
				mPlaySoundAnimView = mView.findViewById(R.id.view_chat_anim_view);
				mPlaySoundAnimView.setBackgroundResource(R.drawable.play_sound_left_anim);
				AnimationDrawable anim = (AnimationDrawable) mPlaySoundAnimView.getBackground();
				anim.start();
			}
		} else { //如果正在播放，点击后，则需要暂停当前播放
			RecorderPlayerManager.getInstance().stop();
			resetPlayAnim();
		}
	}
	
	/**
	 * 更新图片消息
	 * @param url
	 * @param mImageView
	 * @param progressBar
	 */
	private void updateImage(final WeiXinMessage recorder, final ImageView mImageView, 
			final ProgressBar progressBar){
		
		//处理airkiss模式发送过来的图片
		if ("set".equals(recorder.getFormat())){
			String filePath = recorder.getFileName();
			Log.d(TAG, "Format:" + recorder.getFormat() + ",filePath:" + filePath);
			if (!TextUtils.isEmpty(filePath)){
				Bitmap bitmap = BitmapFactory.decodeFile(filePath);
				mImageView.setImageBitmap(bitmap);
				if (progressBar != null && progressBar.getVisibility() == VISIBLE){
					progressBar.setVisibility(GONE);
				}
			} else {
				mImageView.setImageResource(R.drawable.pictures_no);
			}
			return ;
		}
		
		String url = recorder.getUrl();
		if (TextUtils.isEmpty(url) || mImageView == null){
			return ;
		}
		Log.i(TAG, "url:" + url);
		WeApplication.getImageLoader().get(url, new ImageListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onError:" + error.getMessage());
				Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
						R.drawable.pictures_no);
				mImageView.setImageBitmap(bitmap);
			}
			
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				Log.i(TAG, "onResponse:" + response);
				Bitmap bitmap = response.getBitmap();
				if (bitmap != null){
					if (progressBar != null && progressBar.getVisibility() == VISIBLE){
						progressBar.setVisibility(GONE);
					}
					mImageView.setImageBitmap(bitmap);
				} else {
					bitmap = BitmapFactory.decodeResource(mContext.getResources(), 
							R.drawable.pictures_no);
					mImageView.setImageBitmap(bitmap);
					//加载进度
					if (progressBar != null){
						progressBar.setVisibility(VISIBLE);
					}
				}
			}
		});
	}
	
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		
		if (visibility != VISIBLE){
			if (RecorderPlayerManager.getInstance() != null 
					&& RecorderPlayerManager.getInstance().isPlaying()){
				resetPlayAnim();
				RecorderPlayerManager.getInstance().stop();
			}
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
	
	/**
	 * 重置播放动画
	 */
	private void resetPlayAnim(){
		if (mPlaySoundAnimView == null){
			return ;
		}
		mPlaySoundAnimView.setBackgroundResource(R.drawable.v_left_anim3);
	}
}
