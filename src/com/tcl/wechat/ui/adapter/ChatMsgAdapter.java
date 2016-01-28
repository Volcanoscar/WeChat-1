package com.tcl.wechat.ui.adapter;

import java.io.File;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.ClipboardManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.action.audiorecorder.Recorder;
import com.tcl.wechat.action.audiorecorder.RecorderAudioManager;
import com.tcl.wechat.action.audiorecorder.RecorderPlayerManager;
import com.tcl.wechat.action.audiorecorder.listener.AudioPlayCompletedListener;
import com.tcl.wechat.action.imageloader.ImageLoader;
import com.tcl.wechat.common.IConstant.ChatMsgSource;
import com.tcl.wechat.common.IConstant.ChatMsgStatus;
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.common.IConstant.CommandAction;
import com.tcl.wechat.common.IConstant.EventReason;
import com.tcl.wechat.common.IConstant.EventType;
import com.tcl.wechat.controller.WeiXinMsgControl;
import com.tcl.wechat.controller.WeiXinMsgManager;
import com.tcl.wechat.controller.listener.UploadListener;
import com.tcl.wechat.database.WeiRecordDao;
import com.tcl.wechat.model.WeiXinMessage;
import com.tcl.wechat.model.WeixinMsgInfo;
import com.tcl.wechat.ui.activity.BaiduMapActivity;
import com.tcl.wechat.ui.activity.FilePreviewActivity;
import com.tcl.wechat.ui.activity.PlayVideoActivity;
import com.tcl.wechat.ui.activity.ShowImageActivity;
import com.tcl.wechat.ui.activity.ShowTextActivity;
import com.tcl.wechat.ui.activity.WebViewActivity;
import com.tcl.wechat.utils.DateUtils;
import com.tcl.wechat.utils.ExpressionUtil;
import com.tcl.wechat.utils.FileSizeUtil;
import com.tcl.wechat.utils.NetWorkUtil;
import com.tcl.wechat.view.ChatMsgImageView2;
import com.tcl.wechat.xmpp.ReplyResult;
import com.tcl.wechat.xmpp.XmppEvent;
import com.tcl.wechat.xmpp.XmppEventListener;

/**
 * 消息列表栏目适配器
 * @author rex.lei
 *
 */
@SuppressWarnings("deprecation")
@SuppressLint("InflateParams") 
public class ChatMsgAdapter extends BaseAdapter {
	
	private static final String TAG = ChatMsgAdapter.class.getSimpleName();
	
	/** item类型*/
	private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 2;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 3;
    private static final int MESSAGE_TYPE_RECV_VOICE = 4;
    private static final int MESSAGE_TYPE_SENT_VOICE = 5;
    private static final int MESSAGE_TYPE_RECV_VIDEO = 6;
    private static final int MESSAGE_TYPE_SENT_VIDEO = 7;
    private static final int MESSAGE_TYPE_RECV_WEB = 8;
    private static final int MESSAGE_TYPE_SENT_WEB = 9;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 10;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 11;
    private static final int MESSAGE_TYPE_RECV_FILE = 12;
    private static final int MESSAGE_TYPE_SEND_FILE = 13;
    private static final int MESSAGE_TYPE_RECV_MUSIC = 14;
    private static final int MESSAGE_TYPE_SEND_MUSIC = 15;
	
	private Context mContext;
	private LayoutInflater mInflater;
	
	private static final int TYPE_COUNT = 16;
	
	/** 音频显示最大宽度标尺*/
	private static final int CHAT_VIEW_WIDTH = 800;
	/** 最小宽度*/
	private int mMinItemWidth ;
	/** 最大宽度*/
	private int mMaxItemWidth;
	
	/** 音频播放动画View */
	private View mPlaySoundAnimView;
	
	/** 弹出的更多选择框 */
	private PopupWindow mPopupWindow;

	/** 复制，删除 */
	private TextView mCopyTv, mDeleteTv;
	
	/** 执行动画的时间   */
	protected long mAnimationTime = 150;
	
	public ChatMsgAdapter(Context context) {
		super();
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		
		mMinItemWidth = (int) (CHAT_VIEW_WIDTH * 0.1f);
		mMaxItemWidth = (int) (CHAT_VIEW_WIDTH * 0.8f);
		
		initPopWindow();
	}
	
	@Override
	public int getCount() {
		return WeiXinMsgManager.getInstance().getMessageCount();
	}


	@Override
	public WeiXinMessage getItem(int position) {
		// TODO Auto-generated method stub
		return WeiXinMsgManager.getInstance().getMessage(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public int getItemViewType(int position) {
		WeiXinMessage message = WeiXinMsgManager.getInstance().getMessage(position);
		String msgType = message.getMsgtype();
		if (ChatMsgType.IMAGE.equals(msgType)) {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;
		} else if (ChatMsgType.VOICE.equals(msgType)) {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
		} else if (ChatMsgType.VIDEO.equals(msgType) ||
				ChatMsgType.SHORTVIDEO.equals(msgType)) {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
		} else if (ChatMsgType.MAP.equals(msgType)) {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
		} else if (ChatMsgType.WEB.equals(msgType)) {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					MESSAGE_TYPE_RECV_WEB : MESSAGE_TYPE_SENT_WEB;
		} else if (ChatMsgType.FILE.equals(msgType)) {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SEND_FILE;
		} else if (ChatMsgType.MUSIC.equals(msgType)) {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					MESSAGE_TYPE_RECV_MUSIC : MESSAGE_TYPE_SEND_MUSIC;
		} else {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
		} 
	}
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return TYPE_COUNT;
	}
	
	/**
	 * 创建convertView
	 * @param message
	 * @param position
	 * @return
	 */
	public View createViewByMessage(WeiXinMessage message, int position){
		String msgType = message.getMsgtype();
		if (ChatMsgType.IMAGE.equals(msgType)) {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					mInflater.inflate(R.layout.item_received_image, null) :
					mInflater.inflate(R.layout.item_send_image, null);
		} else if (ChatMsgType.VOICE.equals(msgType)) {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					mInflater.inflate(R.layout.item_received_voicemsg, null) :
					mInflater.inflate(R.layout.item_send_voicemsg, null);
		} else if (ChatMsgType.VIDEO.equals(msgType) ||
				ChatMsgType.SHORTVIDEO.equals(msgType)) {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					mInflater.inflate(R.layout.item_received_videomsg, null) :
					mInflater.inflate(R.layout.item_send_videomsg, null);
		} else if (ChatMsgType.MAP.equals(msgType)) {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					mInflater.inflate(R.layout.item_received_locationmsg, null) :
					mInflater.inflate(R.layout.item_send_locationmsg, null);
		} else if (ChatMsgType.WEB.equals(msgType)) {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					mInflater.inflate(R.layout.item_received_webmsg, null) :
					mInflater.inflate(R.layout.item_send_webmsg, null);
		} else if (ChatMsgType.FILE.equals(msgType)) {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					mInflater.inflate(R.layout.item_received_file, null) :
					mInflater.inflate(R.layout.item_received_file, null);
		} else if (ChatMsgType.MUSIC.equals(msgType)) {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					mInflater.inflate(R.layout.item_received_music, null) :
					mInflater.inflate(R.layout.item_received_music, null);
		} else {
			return ChatMsgSource.RECEIVEED.equals(message.getReceived()) ?
					mInflater.inflate(R.layout.item_received_text, null) :
					mInflater.inflate(R.layout.item_send_text, null);
		}
	}
	
	/**
	 * ViewHolder初始化
	 * @param message
	 * @param convertView
	 * @param holder
	 * @param position
	 */
	private void createItemViewHolder(WeiXinMessage message, View convertView, 
			ViewHolder holder, int position){
		String msgType = message.getMsgtype();
		if (ChatMsgType.TEXT.equals(msgType)) {
			try {
				holder.mMessageTimeTv = (TextView) convertView.findViewById(R.id.tv_mseeage_time);
				holder.mMessageTextTv = (TextView) convertView.findViewById(R.id.tv_chat_text);
				holder.mMessageStatusImg = (ImageView) convertView.findViewById(R.id.img_msg_status);
				holder.mMessageProgressBar = (ProgressBar) convertView.findViewById(R.id.pbar_sending);
				holder.mMessageProgressTv = (TextView) convertView.findViewById(R.id.tv_progress);
			} catch (Exception e) {
			}
		} else if (ChatMsgType.IMAGE.equals(msgType)) {
			try {
				holder.mMessageTimeTv = (TextView) convertView.findViewById(R.id.tv_mseeage_time);
				holder.mChatImageView = (ChatMsgImageView2) convertView.findViewById(R.id.img_chat_image);
				holder.mMessageStatusImg = (ImageView) convertView.findViewById(R.id.img_msg_status);
				holder.mMessageProgressBar = (ProgressBar) convertView.findViewById(R.id.pbar_sending);
				holder.mMessageProgressTv = (TextView) convertView.findViewById(R.id.tv_progress);
			} catch (Exception e) {
			}
		} else if (ChatMsgType.VOICE.equals(msgType)) {
			try {
				holder.mMessageTimeTv = (TextView) convertView.findViewById(R.id.tv_mseeage_time);
				holder.mMessageLayout = (RelativeLayout) convertView.findViewById(R.id.layout_msg_info);
				holder.mVoiceLayout = (FrameLayout) convertView.findViewById(R.id.layout_voice);
				holder.mMessageTextTv = (TextView) convertView.findViewById(R.id.tv_chat_recorder_time);
				holder.mMessageStatusImg = (ImageView) convertView.findViewById(R.id.img_msg_status);
				holder.mMessageProgressBar = (ProgressBar) convertView.findViewById(R.id.pbar_sending);
				holder.mMessageProgressTv = (TextView) convertView.findViewById(R.id.tv_progress);
			} catch (Exception e) {
			}
		} else if (ChatMsgType.VIDEO.equals(msgType) ||
				ChatMsgType.SHORTVIDEO.equals(msgType)) {
			try {
				holder.mMessageTimeTv = (TextView) convertView.findViewById(R.id.tv_mseeage_time);
				holder.mMessageLayout = (RelativeLayout) convertView.findViewById(R.id.layout_msg_info);
				holder.mChatImageView = (ChatMsgImageView2) convertView.findViewById(R.id.img_video_thumbnails);
				holder.mMessageStatusImg = (ImageView) convertView.findViewById(R.id.img_msg_status);
				holder.mMessageProgressBar = (ProgressBar) convertView.findViewById(R.id.pbar_sending);
				holder.mMessageProgressTv = (TextView) convertView.findViewById(R.id.tv_progress);
			} catch (Exception e) {
			}
		} else if (ChatMsgType.MAP.equals(msgType)) {
			try {
				holder.mMessageTimeTv = (TextView) convertView.findViewById(R.id.tv_mseeage_time);
				holder.mMessageLayout = (RelativeLayout) convertView.findViewById(R.id.layout_msg_info);
				holder.mMessageTextTv = (TextView) convertView.findViewById(R.id.tv_location_info);
				holder.mMessageStatusImg = (ImageView) convertView.findViewById(R.id.img_msg_status);
				holder.mMessageProgressBar = (ProgressBar) convertView.findViewById(R.id.pbar_sending);
				holder.mMessageProgressTv = (TextView) convertView.findViewById(R.id.tv_progress);
			} catch (Exception e) {
			}
		} else if (ChatMsgType.WEB.equals(msgType)) {
			try {
				holder.mMessageTimeTv = (TextView) convertView.findViewById(R.id.tv_mseeage_time);
				holder.mMessageLayout = (RelativeLayout) convertView.findViewById(R.id.layout_msg_info);
				holder.mMessageTextTv = (TextView) convertView.findViewById(R.id.tv_web_title);
				holder.mMessageDetatilTv = (TextView) convertView.findViewById(R.id.tv_web_detail);
				holder.mMessageStatusImg = (ImageView) convertView.findViewById(R.id.img_msg_status);
				holder.mMessageProgressBar = (ProgressBar) convertView.findViewById(R.id.pbar_sending);
				holder.mMessageProgressTv = (TextView) convertView.findViewById(R.id.tv_progress);
			} catch (Exception e) {
			}
		} else if (ChatMsgType.FILE.equals(msgType)) {
			try {
				holder.mMessageStatusImg = (ImageView) convertView.findViewById(R.id.img_file_icon);
				holder.mMessageTimeTv = (TextView) convertView.findViewById(R.id.tv_mseeage_time);
				holder.mMessageLayout = (RelativeLayout) convertView.findViewById(R.id.layout_msg_info);
				holder.mMessageTextTv = (TextView) convertView.findViewById(R.id.tv_file_title);
				holder.mMessageDetatilTv = (TextView) convertView.findViewById(R.id.tv_file_detail);
			} catch (Exception e) {
			}
		}else if (ChatMsgType.MUSIC.equals(msgType)) {
			try {
				holder.mMessageTimeTv = (TextView) convertView.findViewById(R.id.tv_mseeage_time);
				holder.mMessageLayout = (RelativeLayout) convertView.findViewById(R.id.layout_msg_info);
				holder.mMessageTextTv = (TextView) convertView.findViewById(R.id.tv_music_title);
				holder.mMessageDetatilTv = (TextView) convertView.findViewById(R.id.tv_music_detail);
			} catch (Exception e) {
			}
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		WeiXinMessage message = WeiXinMsgManager.getInstance().getMessage(position);
		if (message == null){
			return null;
		}
		
		if (convertView == null){
			holder = new ViewHolder();
			convertView = createViewByMessage(message, position);
			createItemViewHolder(message, convertView, holder, position);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		handleMessage(position, holder, message, convertView);
		return convertView;
	}
	
	private class ViewHolder{
		/**时间 */
		private TextView mMessageTimeTv;
		private RelativeLayout mMessageLayout;
		
		/** 消息显示内容*/
		private TextView mMessageTextTv;
		private TextView mMessageDetatilTv;
		private FrameLayout mVoiceLayout;
		private ChatMsgImageView2 mChatImageView;
		
		/** 消息状态*/
		private ImageView mMessageStatusImg; 	//消息发送状态：success or failed
		private ProgressBar mMessageProgressBar;//消息发送进度条
		private TextView mMessageProgressTv;	//消息发送百分比显示
	}
	
	/**
	 * 消息处理
	 * @param position
	 * @param holder
	 * @param message
	 */
	private void handleMessage(int position, ViewHolder holder , 
			WeiXinMessage message, View convertView) {
		//显示时间处理
		try {
			Long timestamp = Long.parseLong(message.getCreatetime());
			if (position == 0) {
				holder.mMessageTimeTv.setText(DateUtils.getTimestampString(new Date(timestamp)));
				holder.mMessageTimeTv.setVisibility(View.VISIBLE);
			} else {
			    // 两条消息时间离得如果稍长，显示时间
			    if (DateUtils.isCloseEnough(timestamp, 
			    		Long.parseLong(WeiXinMsgManager.getInstance().getMessage(position - 1).getCreatetime()))) {
			    	holder.mMessageTimeTv.setVisibility(View.GONE);
			    } else {
			    	holder.mMessageTimeTv.setText(DateUtils.getTimestampString(new Date(timestamp)));
			    	holder.mMessageTimeTv.setVisibility(View.VISIBLE);
			    }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//消息内容处理
		String msgType = message.getMsgtype();
		if (ChatMsgType.TEXT.equals(msgType)) {
			handleTextMessage(convertView, position, holder, message);
			
		} else if (ChatMsgType.IMAGE.equals(msgType)) {
			handleImageMessage(convertView, position, holder, message);
			
		} else if (ChatMsgType.VOICE.equals(msgType)) {
			handleAudioMessage(convertView, position, holder, message);
			
		} else if (ChatMsgType.VIDEO.equals(msgType) ||
				ChatMsgType.SHORTVIDEO.equals(msgType)) {
			handleVideoMessage(convertView, position, holder, message);
			
		} else if (ChatMsgType.MAP.equals(msgType)) {
			handleLocationMessage(convertView, position, holder, message);
			
		} else if (ChatMsgType.WEB.equals(msgType)) {
			handleWebMessage(convertView, position, holder, message);
			
		} else if (ChatMsgType.FILE.equals(msgType)) {
			handleFileMessage(convertView, position, holder, message);
			
		} else if (ChatMsgType.MUSIC.equals(msgType)) {
			handleMusicMessage(convertView, position, holder, message);
		}
	}


	/**
	 * 文本消息处理
	 * @param position
	 * @param holder
	 * @param message
	 */
	private void handleTextMessage(View convertView, int position, ViewHolder holder, 
			WeiXinMessage message) {
		String content = message.getContent();
		if (!TextUtils.isEmpty(content)) {
			content = content.replace("<![CDATA[", "").replace("]]>", "");
		}
		final CharSequence contentCharSeq = new ExpressionUtil().StringToSpannale(mContext, 
				new StringBuffer(content));
		
		holder.mMessageTextTv.setTypeface(WeApplication.getInstance().getTypeface2());
		holder.mMessageTextTv.setText(contentCharSeq);
		
		if (ChatMsgSource.SENDED.equals(message.getReceived())){
			if (ChatMsgStatus.FAILED.equals(message.getStatus())){
				holder.mMessageStatusImg.setVisibility(View.VISIBLE);
			} else {
				holder.mMessageStatusImg.setVisibility(View.GONE);
			}
			
			holder.mMessageStatusImg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				}
			});
		}
		
		//监听事件
		holder.mMessageTextTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, ShowTextActivity.class);
				intent.putExtra("Content", contentCharSeq);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			}
		});
		
		if (ChatMsgSource.RECEIVEED.equals(message.getReceived())){
			holder.mMessageTextTv.setOnLongClickListener(new popAction(convertView, position, 0, true));
		} else {
			holder.mMessageTextTv.setOnLongClickListener(new popAction(convertView, position, 1, true));
		}
	}
	
	/**
	 * 图片消息处理
	 * @param position
	 * @param holder
	 * @param message
	 */
	private void handleImageMessage(View convertView, int position, final ViewHolder holder,
			final WeiXinMessage message) {
		
		if ("set".equals(message.getFormat())){//处理airkiss模式发送过来的图片文件
			String filePath = message.getFileName();
			if (!TextUtils.isEmpty(filePath)){
				Bitmap bitmap = BitmapFactory.decodeFile(filePath);
				holder.mChatImageView.setImageBitmap(bitmap);
			} else {
				holder.mChatImageView.setImageResource(R.drawable.pictures_no);
			}
		} else if (ChatMsgSource.RECEIVEED.equals(message.getReceived())) {
			holder.mChatImageView.setImageBitmap(message);
		} else {
			try {
				//1、设置图片信息
				Bitmap bitmap = ImageLoader.getInstance().decodeSampledBitmapFromResource(
						message.getFileName(), 400, 400);
				if (bitmap != null){
					holder.mChatImageView.setImageBitmap(bitmap);
				} else if (!TextUtils.isEmpty(message.getUrl())){
					holder.mChatImageView.setImageBitmap(message);
				} else {
					holder.mChatImageView.setImageResource(R.drawable.pictures_no);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//2、上传图片
			if (NetWorkUtil.isNetworkAvailable()){
				sendImage(message, holder);
			} else {
				//更新内存数据
				WeiXinMsgManager.getInstance().setMessageStatus(message.getMsgid(), 
						ChatMsgStatus.FAILED);
				//更新数据库
				WeiRecordDao.getInstance().updateMessageState(message.getMsgid(),
						ChatMsgStatus.FAILED);
				holder.mMessageStatusImg.setVisibility(View.VISIBLE);
			}
			
			//3、更新状态
			if (ChatMsgStatus.FAILED.equals(message.getStatus())){
				holder.mMessageStatusImg.setVisibility(View.VISIBLE);
			} else {
				holder.mMessageStatusImg.setVisibility(View.GONE);
			}
		}
		
		
		//4、点击事件
		holder.mChatImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ShowImageActivity.class);
				intent.putExtra("WeiXinMsgRecorder", message);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			}
		});
		
		//5、长按事件
		if (ChatMsgSource.RECEIVEED.equals(message.getReceived())){
			holder.mChatImageView.setOnLongClickListener(new popAction(convertView, position, 0, false));
		} else {
			holder.mChatImageView.setOnLongClickListener(new popAction(convertView, position, 1, false));
		}
	}
	
	/**
	 * 音频消息处理
	 * @param position
	 * @param holder
	 * @param message
	 */
	private void handleAudioMessage(View convertView, int position, final ViewHolder holder,
			final WeiXinMessage message) {
		
		int duration  = 0;
		if (!TextUtils.isEmpty(message.getFileName())){
			File file = new File(message.getFileName());
			if (file != null && file.exists()){
				duration = (int)(Math.round(RecorderAudioManager.getDuration(file) / 1000.0 ));
			}
		}
		
		ViewGroup.LayoutParams lp = holder.mVoiceLayout.getLayoutParams();
		lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * duration));
		if (lp.width > CHAT_VIEW_WIDTH){
			lp.width = CHAT_VIEW_WIDTH;
		}
		holder.mMessageTextTv.setText(duration + "\"");
		
		//消息状态
		if (ChatMsgSource.SENDED.equals(message.getReceived())){
			if (ChatMsgStatus.FAILED.equals(message.getStatus())){
				holder.mMessageStatusImg.setVisibility(View.VISIBLE);
			} else {
				holder.mMessageStatusImg.setVisibility(View.GONE);
			}
		}
		
		final boolean bPalyable = duration > 0 ? true : false;
		
		holder.mVoiceLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (bPalyable){
					playAudio(message, holder);
				}
			}
		});
		
		if (ChatMsgSource.RECEIVEED.equals(message.getReceived())){
			holder.mVoiceLayout.setOnLongClickListener(new popAction(convertView, position, 0, false));
		} else {
			holder.mVoiceLayout.setOnLongClickListener(new popAction(convertView, position, 1, false));
		}
	}
	
	/**
	 * 视频消息处理
	 * @param position
	 * @param holder
	 * @param message
	 */
	private void handleVideoMessage(View convertView, int position, ViewHolder holder,
			final WeiXinMessage message) {
		holder.mChatImageView.setImageBitmap(message);
		/*
		Bitmap thumbnailsBitmap = BitmapFactory.decodeFile(
				DataFileTools.getImageFilePath(message.getUrl())); 
		if (thumbnailsBitmap != null){
			holder.mChatImageView.setImageBitmap(thumbnailsBitmap);
		} else {
			holder.mMessageImageImg.setImageBitmap(BitmapFactory.decodeResource(
					mContext.getResources(), R.drawable.message_video_bg));
		}*/
		holder.mMessageLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PlayVideoActivity.class);
				intent.putExtra("FilePath", message.getFileName());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			}
		});
		if (ChatMsgSource.RECEIVEED.equals(message.getReceived())){
			holder.mMessageLayout.setOnLongClickListener(new popAction(convertView, position, 0, false));
		} else {
			holder.mMessageLayout.setOnLongClickListener(new popAction(convertView, position, 1, false));
		}
	}
	
	/**
	 * 地理位置消息处理
	 * @param position
	 * @param holder
	 * @param message
	 */
	private void handleLocationMessage(View convertView, int position, ViewHolder holder,
			final WeiXinMessage message) {
		if (TextUtils.isEmpty(message.getLabel())){
			message.setLabel(mContext.getResources().getString(R.string.unknown_location));
		}
		holder.mMessageTextTv.setText(message.getLabel());
		holder.mMessageLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, BaiduMapActivity.class);
				intent.putExtra("WeiXinMsgRecorder", message);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			}
		});
		if (ChatMsgSource.RECEIVEED.equals(message.getReceived())){
			holder.mMessageLayout.setOnLongClickListener(new popAction(convertView, position, 0, false));
		} else {
			holder.mMessageLayout.setOnLongClickListener(new popAction(convertView, position, 1, false));
		}
	}
	
	/**
	 * 网页链接消息处理
	 * @param position
	 * @param holder
	 * @param message
	 */
	private void handleWebMessage(View convertView, int position, ViewHolder holder,
			final WeiXinMessage message) {
		
		String titleHtml = "<a href=" + message.getUrl() + ">" + message.getTitle()  + 
	       		   "</a>";
		String descHtml  = "<div>" + message.getDescription() + 
				           "</div>";
		holder.mMessageTextTv.setText(Html.fromHtml(titleHtml));
		holder.mMessageDetatilTv.setText(Html.fromHtml(descHtml));
		holder.mMessageLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, WebViewActivity.class); 
				intent.putExtra("LinkUrl", message.getUrl());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			}
		});
		if (ChatMsgSource.RECEIVEED.equals(message.getReceived())){
			holder.mMessageLayout.setOnLongClickListener(new popAction(convertView, position, 0, false));
		} else {
			holder.mMessageLayout.setOnLongClickListener(new popAction(convertView, position, 1, false));
		}
	}
	
	/**
	 * 文件消息处理（通过airkiss接收过来的文件）
	 * @param convertView
	 * @param position
	 * @param holder
	 * @param message
	 */
	private void handleFileMessage(View convertView, int position, ViewHolder holder,
			final WeiXinMessage message){
		try {
			holder.mMessageTextTv.setText(message.getContent());
			String fileSize = FileSizeUtil.FormetFileSize(Long.parseLong(message.getFileSize()));
			holder.mMessageDetatilTv.setText(fileSize);
			String fileType = message.getLabel();
			if ("mp3".equals(fileType)){
				holder.mMessageStatusImg.setImageResource(R.drawable.file_mp3_icon);
			} else if ("wma".equals(fileType)){
				holder.mMessageStatusImg.setImageResource(R.drawable.file_wma_icon);
			} else if ("png".equals(fileType)){
				holder.mMessageStatusImg.setImageResource(R.drawable.file_png_icon);
			} else if ("jpg".equals(fileType)){
				holder.mMessageStatusImg.setImageResource(R.drawable.file_jpg_icon);
			} else if ("jepg".equals(fileType)){
				holder.mMessageStatusImg.setImageResource(R.drawable.file_jpg_icon);
			} else if ("doc".equals(fileType) 
					|| "docx".equals(fileType)){
				holder.mMessageStatusImg.setImageResource(R.drawable.file_doc_icon);
			} else if ("ppt".equals(fileType)
					|| "pptx".equals(fileType)){
				holder.mMessageStatusImg.setImageResource(R.drawable.file_ppt_icon);
			} else if ("xls".equals(fileType)
					|| "xlsx".equals(fileType)){
				holder.mMessageStatusImg.setImageResource(R.drawable.file_xls_icon);
			} else if ("pdf".equals(fileType)){
				holder.mMessageStatusImg.setImageResource(R.drawable.file_pdf_icon);
			} else if ("txt".equals(fileType)){
				holder.mMessageStatusImg.setImageResource(R.drawable.file_txt_icon);
			} else {
				holder.mMessageStatusImg.setImageResource(R.drawable.file_def_icon);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		holder.mMessageLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, FilePreviewActivity.class);
				intent.putExtra("WeiXinMsgRecorder", message);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			}
		});
		
		if (ChatMsgSource.RECEIVEED.equals(message.getReceived())){
			holder.mMessageLayout.setOnLongClickListener(new popAction(convertView, position, 0, true));
		} else {
			holder.mMessageLayout.setOnLongClickListener(new popAction(convertView, position, 1, true));
		}
	}
	
	/**
	 * 音乐文件消息处理
	 * @param convertView
	 * @param position
	 * @param holder
	 * @param message
	 */
	private void handleMusicMessage(View convertView, int position,
			ViewHolder holder, final WeiXinMessage message) {
		holder.mMessageTextTv.setText(message.getContent());
		holder.mMessageDetatilTv.setText(message.getLabel());
		
		holder.mMessageLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, WebViewActivity.class);
				intent.putExtra("LinkUrl", message.getUrl());
				mContext.startActivity(intent);
			}
		});
		
		if (ChatMsgSource.RECEIVEED.equals(message.getReceived())){
			holder.mMessageLayout.setOnLongClickListener(new popAction(convertView, position, 0, true));
		} else {
			holder.mMessageLayout.setOnLongClickListener(new popAction(convertView, position, 1, true));
		}
	}
	
	/**************************************************************************
	 *                               音频播放模块
	 *************************************************************************/
	/**
	 * 音频播放
	 * @param message
	 * @param holder
	 */
	private void playAudio(final WeiXinMessage message, ViewHolder holder){
		if (RecorderPlayerManager.getInstance().isPlaying()){
			RecorderPlayerManager.getInstance().stop();
			resetPlayAnim();
			return;
		}
		
		//播放音频
		String filePath = message.getFileName();
		if (TextUtils.isEmpty(filePath)){
			Log.e(TAG, "filePath is NULL!!");
			return ;
		}
		
		RecorderPlayerManager.getInstance().play(filePath);
		RecorderPlayerManager.getInstance().setPlayCompletedListener(new AudioPlayCompletedListener() {
			
			@Override
			public void onError(int errorcode) {
				// TODO Auto-generated method stub
				resetPlayAnim();
			}
			
			@Override
			public void onCompleted() {
				// TODO Auto-generated method stub
				resetPlayAnim();
			}
		});
		
		//播放动画
		mPlaySoundAnimView = holder.mVoiceLayout.findViewById(R.id.view_chat_recorder_info);
		if (ChatMsgSource.RECEIVEED.equals(message.getReceived())){
			mPlaySoundAnimView.setBackgroundResource(R.drawable.play_sound_left_anim);
			mPlaySoundAnimView.setTag("true");
		} else {
			mPlaySoundAnimView.setBackgroundResource(R.drawable.play_sound_right_anim);
			mPlaySoundAnimView.setTag("false");
		}
		AnimationDrawable anim = (AnimationDrawable) mPlaySoundAnimView.getBackground();
		anim.start();
	}
	
	/**
	 * 重置播放动画
	 */
	private void resetPlayAnim(){
		if (mPlaySoundAnimView == null){
			return ;
		}
		if ("true".equals(mPlaySoundAnimView.getTag())){
			mPlaySoundAnimView.setBackgroundResource(R.drawable.v_left_anim3);
		} else {
			mPlaySoundAnimView.setBackgroundResource(R.drawable.v_right_anim3);
		}
		// if (bReceive){
		// mPlaySoundAnimView.setBackgroundResource(R.drawable.v_left_anim3);
		// } else {
		// mPlaySoundAnimView.setBackgroundResource(R.drawable.v_right_anim3);
		// }
	}
	
	/**
	 * 退出聊天界面，如果音频正在播放，则需要暂停
	 */
	public void stopPlayAudio(){
		if (RecorderPlayerManager.getInstance() != null && RecorderPlayerManager.getInstance().isPlaying()){
			RecorderPlayerManager.getInstance().stop();
			resetPlayAnim();
		}
	}
	
	/**************************************************************************
	 *                               音频播放完成
	 *************************************************************************/
	
	
	
	/**************************************************************************
	 *                               图片处理模块
	 *************************************************************************/
	
	/**
	 * 上传图片至服务器
	 * @param message
	 * @param holder
	 */
	private void sendImage(final WeiXinMessage message, final ViewHolder holder){
		
		String state = message.getStatus();
		
		if (ChatMsgStatus.SEND.equals(state)){
			
			//需要上传
			holder.mMessageProgressBar.setVisibility(View.VISIBLE);
			holder.mMessageProgressTv.setVisibility(View.VISIBLE);
			holder.mMessageProgressTv.setText(String.format(
					mContext.getString(R.string.progress), 0));
			
			//切换状态
			WeiXinMsgManager.getInstance().setMessageStatus(message.getMsgid(), 
					ChatMsgStatus.SENDING);
			WeiRecordDao.getInstance().updateMessageState(message.getMsgid(), 
					ChatMsgStatus.FAILED);
			//上传图片
			holder.mChatImageView.setUploadImage(message, new UploadListener() {
				
				@Override
				public void onResult(String result) {
						
					Log.i(TAG, "UploadImage Result:" + result);
					holder.mMessageProgressBar.setVisibility(View.GONE);
					holder.mMessageProgressTv.setVisibility(View.GONE);
					if (!TextUtils.isEmpty(result)){
						sendImageMessage(result, message);
					} else {
						holder.mMessageStatusImg.setVisibility(View.VISIBLE);
						
						WeiXinMsgManager.getInstance().setMessageStatus(message.getMsgid(), 
								ChatMsgStatus.FAILED);
						WeiRecordDao.getInstance().updateMessageState(message.getMsgid(),
								ChatMsgStatus.FAILED);
					}
				}
				
				@Override
				public void onProgressUpdate(int progress) {
					Log.i(TAG, "progress:" + progress);
					holder.mMessageProgressTv.setText(String.format(
							mContext.getString(R.string.progress), progress));
					if (progress == 100){
						WeiRecordDao.getInstance().updateMessageReadState(
								message.getMsgid(), ChatMsgStatus.SUCCESS);
					}
				}
				
				@Override
				public void onError(int errorCode) {
					
				}
			});
		}
	}
	
	/**
	 * 发送消息
	 * @param result 图片上传返回结果
	 * @param message 消息类
	 */
	private void sendImageMessage(String result, WeiXinMessage message){
		try {
			JSONObject object = new JSONObject(result);
			String mediaid = (String) object.get("media_id");
			
			// 生成对应的消息类型
			Recorder recorder = new Recorder();
			recorder.setFileName(recorder.getFileName());

			WeixinMsgInfo weixinMsgInfo = new WeixinMsgInfo();
			weixinMsgInfo.setFromusername(message.getOpenid());
			weixinMsgInfo.setTousername(message.getToOpenid());
			weixinMsgInfo.setMessageid(message.getMsgid());
			weixinMsgInfo.setMsgtype(ChatMsgType.IMAGE);
			weixinMsgInfo.setRecorder(recorder);
			weixinMsgInfo.setMediaid(mediaid);
			WeiXinMsgControl.getInstance().replyMessage(mXmppEventListener, weixinMsgInfo);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private XmppEventListener mXmppEventListener = new XmppEventListener() {
		
		@Override
		public void onEvent(XmppEvent event) {
			// TODO Auto-generated method stub
			
			switch (event.getType()) {
			case EventType.TYPE_SEND_WEIXINMSG:
				
				ReplyResult result = (ReplyResult) event.getEventData();
				String msgid = result.getMsgid();
				String url = result.getResult();
				
				int reason = event.getReason();
				if (reason == EventReason.REASON_COMMON_SUCCESS) {
					if (!TextUtils.isEmpty(url)) {
						Log.i(TAG, "Result url:" + url);
						WeiXinMsgManager.getInstance().setMessageUrl(msgid, url);
						WeiXinMsgManager.getInstance().setMessageStatus(msgid, 
								ChatMsgStatus.SUCCESS);
						
						//更新URL
						WeiRecordDao.getInstance().updateRecorderUrl(
								msgid, result.getResult());
						
						//状态修改为发送成功
						WeiRecordDao.getInstance().updateMessageState(
								msgid, ChatMsgStatus.SUCCESS);
					} else {
						//状态修改为发送失败
						WeiRecordDao.getInstance().updateMessageReadState(
								msgid, ChatMsgStatus.FAILED);
					}
				} else {
					//状态修改为发送失败
					WeiRecordDao.getInstance().updateMessageReadState(
							msgid, ChatMsgStatus.FAILED);
				}
				break;

			default:
				break;
			}
		}
	};
	
	/**************************************************************************
	 *                               图片上传结束
	 *************************************************************************/
	
	
	
	
	/**************************************************************************
	 * Item长按事件
	 *************************************************************************/
	/**
	 * 初始化弹出的pop
	 */
	private void initPopWindow() {
		View popView = mInflater.inflate(R.layout.chat_item_copy_delete_menu,
				null);
		mCopyTv = (TextView) popView.findViewById(R.id.chat_copy_menu);
		mDeleteTv = (TextView) popView.findViewById(R.id.chat_delete_menu);
		mPopupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
	}
	
	/**
	 * 显示popWindow
	 * */
	public void showPop(View parent, int x, int y, final View view,
			final int position, final int fromOrTo) {
		mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mCopyTv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				}
				try {
					ClipboardManager clipboardManager = (ClipboardManager) mContext
							.getSystemService(Context.CLIPBOARD_SERVICE);
					//clipboardManager.setText(mAllRecorders.get(position).getContent());
					String content = WeiXinMsgManager.getInstance().getMessage(position).getContent()
							.replace("<![CDATA[", "").replace("]]>", "");
					clipboardManager.setText(new ExpressionUtil().StringToSpannale(mContext, 
							new StringBuffer(content)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mDeleteTv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				}
				
				//TODO 在此要通知进行清除数据库，更新数据
				WeiXinMessage message = WeiXinMsgManager.getInstance().getMessage(position);
				
				if (message == null){
					return ;
				}
				
				//删除数据库中数据
				if (WeiRecordDao.getInstance().deleteRecorder(message.getMsgid())){
					//动画
					if (fromOrTo == 0) {
						leftRemoveAnimation(view, position);
					} else if (fromOrTo == 1) {
						rightRemoveAnimation(view, position);
					}
					
					Intent intent = new Intent();
					intent.setAction(CommandAction.ACTION_MSG_UPDATE);
					WeApplication.getContext().sendBroadcast(intent);
				}
			}
		});
		mPopupWindow.update();
	}

	/**
	 * 每个ITEM中more按钮对应的点击动作
	 * */
	public class popAction implements OnLongClickListener {
		int position;
		View view;
		int fromOrTo;
		boolean bText;

		public popAction(View view, int position, int fromOrTo, boolean bText) {
			this.position = position;
			this.view = view;
			this.fromOrTo = fromOrTo;
			this.bText = bText;
		}

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			int[] arrayOfInt = new int[2];
			// 获取点击按钮的坐标
			v.getLocationOnScreen(arrayOfInt);
			int x = arrayOfInt[0];
			int y = arrayOfInt[1];
			if (!bText) {
				mCopyTv.setVisibility(View.GONE);
			} else {
				mCopyTv.setVisibility(View.VISIBLE);
			}
			showPop(v, x, y, view, position, fromOrTo);
			return true;
		}
	}

	/**
	 * item删除动画
	 * */
	private void rightRemoveAnimation(final View view, final int position) {
		final Animation animation = (Animation) AnimationUtils.loadAnimation(
				mContext, R.anim.chatto_remove_anim);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				view.setAlpha(0);
				performDismiss(view, position);
				animation.cancel();
			}
		});

		view.startAnimation(animation);
	}

	/**
	 * item删除动画
	 * */
	private void leftRemoveAnimation(final View view, final int position) {
		final Animation animation = (Animation) AnimationUtils.loadAnimation(
				mContext, R.anim.chatfrom_remove_anim);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				view.setAlpha(0);
				performDismiss(view, position);
				animation.cancel();
			}
		});
		view.startAnimation(animation);
	}

	/**
	 * 在此方法中执行item删除之后，其他的item向上或者向下滚动的动画，并且将position回调到方法onDismiss()中
	 * 
	 * @param dismissView
	 * @param dismissPosition
	 */
	private void performDismiss(final View dismissView,
			final int dismissPosition) {
		final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();// 获取item的布局参数
		final int originalHeight = dismissView.getHeight();// item的高度

		ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0)
				.setDuration(mAnimationTime);
		animator.start();

		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				WeiXinMsgManager.getInstance().deleteMessage(dismissPosition);
				notifyDataSetChanged();
				ViewHelper.setAlpha(dismissView, 1f);
				ViewHelper.setTranslationX(dismissView, 0);
				ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
				lp.height = originalHeight;
				dismissView.setLayoutParams(lp);
			}
		});
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				lp.height = (Integer) valueAnimator.getAnimatedValue();
				dismissView.setLayoutParams(lp);
			}
		});
	}
	
	
	
}
