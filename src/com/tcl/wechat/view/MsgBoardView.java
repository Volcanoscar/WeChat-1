package com.tcl.wechat.view;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
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
import com.tcl.wechat.model.WeiXinMsgRecorder;
import com.tcl.wechat.ui.activity.BaiduMapActivity;
import com.tcl.wechat.ui.activity.ChatActivity;
import com.tcl.wechat.ui.activity.ShowImageActivity;
import com.tcl.wechat.ui.activity.ShowTextActivity;
import com.tcl.wechat.ui.activity.WebViewActivity;
import com.tcl.wechat.utils.DataFileTools;
import com.tcl.wechat.utils.DateTimeUtil;
import com.tcl.wechat.utils.ExpressionUtil;
import com.tcl.wechat.utils.FontUtil;
import com.tcl.wechat.view.pageview.TextPageView;

/**
 * 家庭留言板视图
 * @author rex.lei
 *
 */
public class MsgBoardView extends LinearLayout{

	private static final String TAG = MsgBoardView.class.getSimpleName();
	
	private Context mContext;
	
	private LayoutInflater mInflater;
	
	private View mView;
	
	private int mScreen = 0;
	
	/**
	 * 消息显示控件
	 */
	private TextPageView mMsgPageView;
	
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
	private WeiXinMsgRecorder mRecorder;
	
	/**
	 * 音频播放播放管理类
	 */
	private RecorderPlayerManager mAudioManager;
	
	/**
	 * 音频播放动画View；
	 */
	private View mPlaySoundAnimView; 
	
	
	public MsgBoardView(Context context) {
		this(context, null);
	}
	
	public MsgBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mAudioManager = RecorderPlayerManager.getInstance();
	}
	
	public void setupView(int screen){
		mScreen = screen;
		int layoutId = mContext.getResources().getIdentifier("layout_msgboard_" + (screen + 1), 
								"layout", mContext.getPackageName());
		mView = inflate(mContext, layoutId, this);
		
		mMsgPageView = (TextPageView) mView.findViewById(R.id.tv_familboard_msg);
		mUserInfoViewUv = (UserInfoView) mView.findViewById(R.id.uv_familboard_userinfo);
		mUnReadMsgIndicatorTv = (TextView) mView.findViewById(R.id.tv_unread_msg_hint);
		mMsgReceiveTimeTv = (TextView) mView.findViewById(R.id.tv_msgreceive_time);
		mReplyBtn = (Button) mView.findViewById(R.id.btn_familyborad_replay);
		mMsgConentLayout = (FrameLayout) mView.findViewById(R.id.layout_msginfo); 
		
		mUserInfoViewUv.setUserNameBackGround(false);
		mUserInfoViewUv.setTextSize(22);
		mUserInfoViewUv.setFont("fonts/oop.TTF");
		mMsgReceiveTimeTv.setTypeface(getFontTypeface("fonts/oop.TTF"));
		mUnReadMsgIndicatorTv.setText(String.format(getResources().getString(R.string.page_Indicator), 0, 0));
		
		mReplyBtn.setOnClickListener(replyClick);
	}
	
	/**
	 * 添加数据
	 * @param bindUser 用户信息
	 * @param recorders 消息记录
	 */
	public void addData(BindUser bindUser, WeiXinMsgRecorder recorder){
		mBindUser = bindUser;
		mRecorder = recorder;
				
		upadteView(mBindUser, mRecorder);
	}
	
	private OnClickListener replyClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
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
	public void receiveNewMessage(BindUser bindUser ,WeiXinMsgRecorder recorder){
		if (bindUser == null || recorder == null){
			return ;
		}
		addData(bindUser, recorder);
	}
	
	/**
	 * 更新界面
	 * @param bindUser
	 * @param recorders
	 */
	private void upadteView(BindUser bindUser, final WeiXinMsgRecorder recorder){
		if (bindUser == null || recorder == null){
			return ;
		}
		
		if (!bindUser.getOpenId().equals(recorder.getOpenid())){
			Log.w(TAG, "bindUser's Openid isn't equal Recorder's Opendid");
			return ;
		}
		
		//更新用户信息
		String headImageUrl = bindUser.getHeadImageUrl();
		String userName = bindUser.getRemarkName();
		
		if (headImageUrl != null && userName != null){
			mUserInfoViewUv.setUserIcon(headImageUrl, false);
			mUserInfoViewUv.setUserName(userName);
		}
		
		//更新消息信息
		Log.i(TAG, "recorder:" + recorder.toString());
		String msgType = recorder.getMsgtype();
		Log.i(TAG, "Msgtype:" + msgType);
		
		mMsgConentLayout.removeAllViews();
		String time = recorder.getCreatetime();
		mMsgReceiveTimeTv.setText(DateTimeUtil.getShortTime(time));
		
		if (ChatMsgType.TEXT.equals(msgType)){
			mMsgConentLayout.addView(setupTextMsgView(recorder));
		} else if (ChatMsgType.VOICE.equals(msgType)){
			mMsgConentLayout.addView(setupVoiceMsgView(recorder));
		} else if (ChatMsgType.VIDEO.equals(msgType) 
				|| ChatMsgType.SHORTVIDEO.equals(msgType)){
			mMsgConentLayout.addView(setupVideoMsgView(recorder));
		} else if (ChatMsgType.IMAGE.equals(msgType)){
			mMsgConentLayout.addView(setupImageMsgView(recorder));
		} else if (ChatMsgType.LOCATION.equals(msgType)){ //位置信息
			mMsgConentLayout.addView(setupLocationView(recorder));
		} else if (ChatMsgType.LINK.equals(msgType)) {
			mMsgConentLayout.addView(setupLinkView(recorder));
		}
	}
	
	private Typeface getFontTypeface(String fontpath) {
			return Typeface.createFromAsset(getContext().getAssets(), fontpath);
	}
	
	/**
	 * 创建文本消息视图
	 * @param recorder 消息内容
	 * @return
	 */
	private View setupTextMsgView(final WeiXinMsgRecorder recorder){
		View mMsgView = mInflater.inflate(R.layout.layout_msgboard_textview, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mMsgView.setLayoutParams(params);
		TextView textInfoTv = (TextView) mMsgView.findViewById(R.id.tv_msgboard_textinfo);
		CharSequence contentCharSeq = recorder.getContent();
		if (!TextUtils.isEmpty(contentCharSeq)){
			contentCharSeq = ExpressionUtil.getInstance().StringToSpannale(mContext, 
					new StringBuffer(contentCharSeq));
		} else {
			contentCharSeq = mContext.getString(R.string.no_message);
		}
		textInfoTv.setText(contentCharSeq);
		textInfoTv.setTypeface(new FontUtil(mContext).getFont("fonts/regular.TTF"));
		
		final CharSequence content = contentCharSeq;
		
		mMsgView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showTextInfo(content);
			}
		});
		return mMsgView;
	}
	
	/**
	 * 创建音频消息视图
	 * @param recorder 消息内容
	 * @return
	 */
	private View setupVoiceMsgView(WeiXinMsgRecorder recorder){
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
				duration = (int)(Math.ceil(RecorderAudioManager.getDuration(file) / 1000.0 ));
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
	private View setupImageMsgView(final WeiXinMsgRecorder recorder){
		ImageView imageView = new ImageView(mContext);
		imageView.setScaleType(ScaleType.CENTER_INSIDE);
		updateImage(recorder.getUrl(), imageView);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPicture(recorder.getUrl());
			}
		});
		return imageView;
	}
	
	/**
	 * 地理位置显示缩略图
	 * @param recorder
	 * @return
	 */
	private View setupLocationView(final WeiXinMsgRecorder recorder){
		View mMsgView = null;
		mMsgView = mInflater.inflate(R.layout.layout_chat_locationview, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(260, 200);
		mMsgView.setLayoutParams(params);
		TextView locationInfoTv = (TextView) mMsgView.findViewById(R.id.tv_location_info);
		locationInfoTv.setText(recorder.getLabel());
		mMsgView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, BaiduMapActivity.class);
				intent.putExtra("WeiXinMsgRecorder", recorder);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			}
		});
		
		return mMsgView;
	}
	
	/**
	 * 链接显示控件
	 * @param recorder
	 * @return
	 */
	private View setupLinkView(final WeiXinMsgRecorder recorder){
		Log.i(TAG, "WeiXinMsgRecorder:" + recorder);
		View mMsgView = mInflater.inflate(R.layout.layout_msgboard_linkview, null);
		TextView mTitleTv = (TextView) mMsgView.findViewById(R.id.link_title);
		TextView mDetailTv = (TextView) mMsgView.findViewById(R.id.link_detail);
		
		String titleHtml = "<a href=" + mRecorder.getUrl() + ">" + mRecorder.getTitle()  + 
			       		   "</a>";
		String descHtml =  "<div>" + mRecorder.getDescription() + 
				           "</div>";
		Log.i(TAG, "titleHtml:" + titleHtml);
		Log.i(TAG, "descHtml:" + descHtml);
		
		mTitleTv.setText(Html.fromHtml(titleHtml));
		mDetailTv.setText(Html.fromHtml(descHtml));
		
		mMsgView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Intent intent = new Intent(mContext, WebViewActivity.class); 
					intent.putExtra("LinkUrl", recorder.getUrl());
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			}
		});
		return mMsgView;
	}

	/**
	 * 设置视频图片视图
	 * @param recorder
	 * @return
	 */
	private View setupVideoMsgView(final WeiXinMsgRecorder recorder){
		View mVideoView = mInflater.inflate(R.layout.layout_chat_video_view, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(260, 200);
		mVideoView.setLayoutParams(params);
		ImageView thumbnailsImg = (ImageView) mVideoView.findViewById(R.id.img_video_thumbnails);
		updateImage(recorder.getUrl(), thumbnailsImg);
		mVideoView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_VIEW);
				String fileName = DataFileTools.getInstance().getVideoFilePath(recorder.getMediaid());
                intent.setDataAndType(Uri.parse(fileName), "video/mp4");
                mContext.startActivity(intent);
			}
		});
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
	 * 文本预览大图
	 * @param url 文本内容
	 */
	private void showTextInfo(CharSequence content) {
		Intent intent = new Intent(mContext, ShowTextActivity.class);
		intent.putExtra("Content", content);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}
	
	/**
	 * 图片大图预览
	 * @param url 图片url地址
	 */
	private void showPicture(String url) {
		Intent intent = new Intent(mContext, ShowImageActivity.class);
		intent.putExtra("fileName", url);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
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
		}, 0, 0);
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
		mPlaySoundAnimView.setBackgroundResource(R.drawable.voice_left);
	}
}
