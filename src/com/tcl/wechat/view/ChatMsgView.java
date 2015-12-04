package com.tcl.wechat.view;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.action.recorder.RecorderAudioManager;
import com.tcl.wechat.action.recorder.RecorderPlayerManager;
import com.tcl.wechat.action.recorder.listener.AudioPlayCompletedListener;
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.WeiXinMessage;
import com.tcl.wechat.ui.activity.BaiduMapActivity;
import com.tcl.wechat.ui.activity.ShowImageActivity;
import com.tcl.wechat.ui.activity.ShowTextActivity;
import com.tcl.wechat.ui.activity.WebViewActivity;
import com.tcl.wechat.utils.DataFileTools;
import com.tcl.wechat.utils.ExpressionUtil;

/**
 * 聊天信息显示界面
 * @author rex.lei
 *
 */
public class ChatMsgView extends RelativeLayout{
	
	private static final String TAG = ChatMsgView.class.getSimpleName();
	
	private Context mContext;

	private View mView;
	
	private FrameLayout mMsgConentLayout;
	
	private LayoutInflater mInflater;
	
	/**
	 * 音频播放播放管理类
	 */
	private RecorderPlayerManager mAudioManager;
	
	/**
	 * 音频播放动画View；
	 */
	private View mPlaySoundAnimView; 
	
	public ChatMsgView(Context context) {
		this(context, null);
	}
	
	public ChatMsgView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ChatMsgView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mAudioManager = RecorderPlayerManager.getInstance();
		
		initView(context);
	}

	private void initView(Context context) {
		// TODO Auto-generated method stub
		mView = inflate(mContext, R.layout.layout_chat_msgview, this); //mInflater.inflate(R.layout.layout_chat_msgview, null);
		mMsgConentLayout = (FrameLayout) mView.findViewById(R.id.layout_chatview);
	}
	
	public void addData(WeiXinMessage recorder){
		if (recorder != null ){
			Log.i(TAG, "receive Data:" + recorder.toString());
			mMsgConentLayout.addView(getView(recorder));
		} else {
			mMsgConentLayout.addView(setupTextMsgView(null));
		}
	}
	
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
		}
		
		mView.setClickable(true);
		mView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = createIntent(recorder);
				if (intent != null){
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
			mIntent = new Intent(Intent.ACTION_VIEW);
			String fileName = DataFileTools.getInstance().getVideoFilePath(recorder.getMediaid());
			mIntent.setDataAndType(Uri.parse(fileName), "video/mp4");
			
		} else if (ChatMsgType.IMAGE.equals(msgType)){
			mIntent = new Intent(mContext, ShowImageActivity.class);
			mIntent.putExtra("WeiXinMsgRecorder", recorder);
			
		} else if (ChatMsgType.MAP.equals(msgType)){ 
			mIntent = new Intent(mContext, BaiduMapActivity.class);
			mIntent.putExtra("WeiXinMsgRecorder", recorder);
			
		} else if (ChatMsgType.WEB.equals(msgType)) {
			mIntent = new Intent(mContext, WebViewActivity.class); 
			mIntent.putExtra("LinkUrl", recorder.getUrl());
		}
		return mIntent;
	}
	
	/**
	 * 设置字体
	 * @param fontpath
	 * @return
	 */
	private Typeface getFontTypeface(String fontpath) {
		try {
			return Typeface.createFromAsset(getContext().getAssets(), fontpath);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;	
	}
	
	/**
	 * 创建文本消息视图
	 * @param recorder 消息内容
	 * @return
	 */
	private View setupTextMsgView(WeiXinMessage recorder){
		View mMsgView = mInflater.inflate(R.layout.layout_msgboard_textview, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mMsgView.setLayoutParams(params);
		TextView textInfoTv = (TextView) mMsgView.findViewById(R.id.tv_msgboard_textinfo);
		
		CharSequence contentCharSeq = null;
		if (recorder != null ){
			contentCharSeq = ExpressionUtil.getInstance().StringToSpannale(mContext, 
					new StringBuffer(recorder.getContent()));
		} else {
			contentCharSeq = mContext.getString(R.string.no_message);
		}
		Log.i(TAG, "contentCharSeq:" + contentCharSeq);
		textInfoTv.setText(contentCharSeq);
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
		updateImage(recorder.getUrl(), imageView);
		return mMsgView;
	}
	
	/**
	 * 地理位置显示缩略图
	 * @param recorder
	 * @return
	 */
	private View setupLocationView(final WeiXinMessage recorder){
		View mMsgView = null;
		mMsgView = mInflater.inflate(R.layout.layout_chat_location_leftview, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(260, 200);
		mMsgView.setLayoutParams(params);
		TextView locationInfoTv = (TextView) mMsgView.findViewById(R.id.tv_location_info);
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
		
		String titleHtml = "<a href=" + recorder.getUrl() + ">" + recorder.getTitle()  + 
			       		   "</a>";
		String descHtml =  "<div>" + recorder.getDescription() + 
				           "</div>";
		mTitleTv.setText(Html.fromHtml(titleHtml));
		mDetailTv.setText(Html.fromHtml(descHtml));
		return mMsgView;
	}

	/**
	 * 设置视频图片视图
	 * @param recorder
	 * @return
	 */
	private View setupVideoMsgView(final WeiXinMessage recorder){
		View mVideoView = mInflater.inflate(R.layout.layout_chat_video_view, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(260, 200);
		mVideoView.setLayoutParams(params);
		ImageView thumbnailsImg = (ImageView) mVideoView.findViewById(R.id.img_video_thumbnails);
		updateImage(recorder.getUrl(), thumbnailsImg);
		return mVideoView;
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
				
				//音频点击，进行播放动画
				mPlaySoundAnimView = mView.findViewById(R.id.view_chat_anim_view);
				mPlaySoundAnimView.setBackgroundResource(R.drawable.play_sound_left_anim);
				AnimationDrawable anim = (AnimationDrawable) mPlaySoundAnimView.getBackground();
				anim.start();
			}
		} else { //如果正在播放，点击后，则需要暂停当前播放
			mAudioManager.stop();
			resetPlayAnim();
		}
	}
	
	/**
	 * 更新图片消息
	 * @param url
	 * @param mImageView
	 */
	private void updateImage(String url, final ImageView mImageView){
		
		if (TextUtils.isEmpty(url) || mImageView == null){
			return ;
		}
		Log.i(TAG, "url:" + url);
		WeApplication.getImageLoader().get(url, new ImageListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
						R.drawable.pictures_no);
				mImageView.setImageBitmap(bitmap);
			}
			
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				// TODO Auto-generated method stub
				Bitmap bitmap = response.getBitmap();
				if (bitmap != null){
					mImageView.setImageBitmap(bitmap);
				} else {
					bitmap = BitmapFactory.decodeResource(mContext.getResources(), 
							R.drawable.pictures_no);
					mImageView.setImageBitmap(bitmap);
				}
			}
		}, 400, 400);
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
