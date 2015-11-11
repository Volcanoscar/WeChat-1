package com.tcl.wechat.ui.adapter;

import java.io.File;
import java.net.URLDecoder;
import java.util.LinkedList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.tcl.wechat.R;
import com.tcl.wechat.action.recorder.RecorderAudioManager;
import com.tcl.wechat.action.recorder.RecorderPlayerManager;
import com.tcl.wechat.action.recorder.listener.AudioPlayCompletedListener;
import com.tcl.wechat.common.IConstant.ChatMsgRource;
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.controller.WeiXinMsgManager;
import com.tcl.wechat.database.WeiMsgRecordDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.WeiXinMsgRecorder;
import com.tcl.wechat.ui.activity.BaiduMapActivity;
import com.tcl.wechat.ui.activity.ShowImageActivity;
import com.tcl.wechat.ui.activity.ShowTextActivity;
import com.tcl.wechat.ui.activity.WebViewActivity;
import com.tcl.wechat.utils.DataFileTools;
import com.tcl.wechat.utils.ExpressionUtil;
import com.tcl.wechat.utils.FontUtil;
import com.tcl.wechat.view.ChatMsgImageView;
import com.tcl.wechat.view.ChatMsgImageView.UploadCompleteListener;

/**
 * 消息列表栏目适配器
 * @author rex.lei
 *
 */
@SuppressLint("InflateParams") 
public class ChatMsgAdapter extends BaseAdapter implements OnScrollListener{
	
	private static final String TAG = ChatMsgAdapter.class.getSimpleName();
	
	private Context mContext;
	private LayoutInflater mInflater;
	private BindUser mBindUser;
	private LinkedList<WeiXinMsgRecorder> mAllRecorders;
	
	/** 弹出的更多选择框 */
	private PopupWindow mPopupWindow;

	/** 复制，删除 */
	private TextView mCopyTv, mDeleteTv;
	
	/** 音频播放动画View */
	private View mPlaySoundAnimView;
	
	/** 执行动画的时间   */
	protected long mAnimationTime = 150;
	
	/**
	 * 音频播放管理类
	 */
	private RecorderPlayerManager mAudioManager;
	
	private static final int TYPE_COUNT = 5;
	private static final int CHAT_VIEW_WIDTH = 800;
	
	private int mMinItemWidth ;
	private int mMaxItemWidth;
	
	public ChatMsgAdapter(Context context, BindUser bindUser, LinkedList<WeiXinMsgRecorder> recorders) {
		super();
		this.mContext = context;
		this.mBindUser = bindUser;
		this.mAllRecorders = recorders;
		this.mInflater = LayoutInflater.from(context);
		this.mAudioManager = RecorderPlayerManager.getInstance();
		
		mMinItemWidth = (int) (CHAT_VIEW_WIDTH * 0.1f);
		mMaxItemWidth = (int) (CHAT_VIEW_WIDTH * 0.8f);
		
		initPopWindow();
	}
	
	public void setData(LinkedList<WeiXinMsgRecorder> recorders) {
		this.mAllRecorders = recorders;
	}
	
	@Override
	public int getCount() {
		if (mBindUser == null || mAllRecorders == null ||
				mAllRecorders.isEmpty()){
			return 0;
		}
		return mAllRecorders.size();
	}

	@Override
	public Object getItem(int position) {
		return mAllRecorders.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public int getItemViewType(int position) {
		WeiXinMsgRecorder recorder = mAllRecorders.get(position);
		if (ChatMsgRource.RECEIVEED.equals(recorder.getReceived())) {
			return 0; //接收
		} 
		return 1; //发送
	}
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return TYPE_COUNT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		WeiXinMsgRecorder recorder = mAllRecorders.get(position);
		if (recorder == null){
			return null;
		}
		if (convertView == null){
			holder = new ViewHolder();
			convertView = inflateConvertView(recorder);
			holder.mMsgInfoLayout = (RelativeLayout) convertView.findViewById(R.id.layout_chat_msginfo);
			holder.mMsgSendTime = (TextView) convertView.findViewById(R.id.tv_chat_msgsendtime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mMsgSendTime.setText(recorder.getCreatetime());
		holder.mMsgInfoLayout.removeAllViews();
		/**
		 * 根据消息的不同类型，显示消息 内容
		 */
		View itemView = null;
		String msgType = recorder.getMsgtype();
		if (ChatMsgType.TEXT.equals(msgType)){
			//文本消息显示控件
			itemView = setupTextMsgView(recorder);
			
		} else if (ChatMsgType.IMAGE.equals(msgType)){
			//图片消息显示控件
			itemView = setupImageMsgView(recorder);
			
		} else if (ChatMsgType.VOICE.equals(msgType)) {
			//音频消息显示控件
			itemView = setupAudioMsgView(recorder);
			
		} else if (ChatMsgType.VIDEO.equals(msgType) ||
				ChatMsgType.SHORTVIDEO.equals(msgType)){
			//视频消息显示控件
			itemView = setupVideoMsgView(recorder);
			
		} else if (ChatMsgType.LOCATION.equals(msgType)){
			//地理位置消息显示控件
			itemView = setupLocationMsgView(recorder);
			
		} else if (ChatMsgType.LINK.equals(msgType)){
			//链接消息显示控件
			itemView = setupLinkMsgView(recorder);
		}
		
		if (itemView != null){
			holder.mMsgInfoLayout.addView(itemView);
			
			/**
			 * 点击事件
			 *  Image 放大查看
			 *  voice 播放
			 *  text: 可放大查看
			 */
			itemView.setOnClickListener(new ClickAction(convertView, position));
			
			/**
			 * 长按事件 复制 + 删除
			 */
			if (ChatMsgRource.RECEIVEED.equals(recorder.getReceived())){
				// 0 是收到的消息，1是发送的消息
				itemView.setOnLongClickListener(new popAction(convertView, position, 0));
			} else {
				itemView.setOnLongClickListener(new popAction(convertView, position, 1));
			}
		}
		return convertView;
	}
	
	/**
	 * 生成布局文件
	 * @param message
	 * @return
	 */
	private View inflateConvertView(WeiXinMsgRecorder recorder){
		View convertView = null;
		if (ChatMsgRource.RECEIVEED.equals(recorder.getReceived())){
			convertView = mInflater.inflate(R.layout.chat_msg_receive_item, null);
		} else {
			convertView = mInflater.inflate(R.layout.chat_msg_send_item, null);
		}
		return convertView;
	}
	
	private class ViewHolder{
		private RelativeLayout mMsgInfoLayout;
		private TextView mMsgSendTime;
	}
	
	/**
	 * 文本消息显示控件
	 * @param recorder
	 * @return
	 */
	private View setupTextMsgView(WeiXinMsgRecorder recorder){
		View mMsgView = null;
		if (ChatMsgRource.RECEIVEED.equals(recorder.getReceived())){
			mMsgView = mInflater.inflate(R.layout.layout_chat_text_leftview, null);
		} else {
			mMsgView = mInflater.inflate(R.layout.layout_chat_text_rightview, null);
		}
		TextView textInfoTv = (TextView) mMsgView.findViewById(R.id.tv_text_view);
		
		try {
			String content = URLDecoder.decode(recorder.getContent(), "utf-8");
			Log.i(TAG, "content11:" + content);
			if (!TextUtils.isEmpty(content)) {
				content = content.replace("<![CDATA[", "").replace("]]>", "");
				Log.i(TAG, "content22:" + content);
			}
			CharSequence contentCharSeq = ExpressionUtil.getInstance().StringToSpannale(mContext, 
						new StringBuffer(content));
			textInfoTv.setText(contentCharSeq);
			textInfoTv.setTypeface(new FontUtil(mContext).getFont("fonts/regular.TTF"));
			mMsgView.setTag(contentCharSeq);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mMsgView;
	}
	
	/**
	 * 图片显示视图
	 * @return
	 */
	private View setupImageMsgView(WeiXinMsgRecorder recorder){
		View mMsgView = null;
		if (ChatMsgRource.RECEIVEED.equals(recorder.getReceived())){
			mMsgView = mInflater.inflate(R.layout.layout_chat_image_leftview, null);
		} else {
			mMsgView = mInflater.inflate(R.layout.layout_chat_image_rightview, null);
		}
		ChatMsgImageView mChatMsgView = (ChatMsgImageView) mMsgView.findViewById(R.id.img_msg_imageview);
		recorder.setUrl(WeiMsgRecordDao.getInstance().getRecorderUrl(recorder.getMsgid()));
		mChatMsgView.setBitmapImage(recorder);
		mChatMsgView.setUploadCompleteListener(new UploadCompleteListener() {
			
			@Override
			public void onComplete(WeiXinMsgRecorder recorder) {
				// TODO Auto-generated method stub
				mAllRecorders.removeLast();
				mAllRecorders.addLast(recorder);
				notifyDataSetChanged();
			}
		});
		return mMsgView;
	}
	
	/**
	 * 音频显示视图
	 * @param recorder
	 * @return
	 */
	private View setupAudioMsgView(WeiXinMsgRecorder recorder){
		//TODO 获取音频文件播放的长度
		int duration  = 0;
		Log.i(TAG, "FileName:" + recorder.getFileName());
		if (!TextUtils.isEmpty(recorder.getFileName())){
			File file = new File(recorder.getFileName());
			if (file != null && file.exists()){
				duration = (int)(Math.round(RecorderAudioManager.getDuration(file) / 1000.0 ));
			}
		}
		Log.i(TAG, "duration:" + duration);
		View mRecordView = null;
		if (ChatMsgRource.RECEIVEED.equals(recorder.getReceived())){
			mRecordView = mInflater.inflate(R.layout.layout_chat_voice_leftview, null);
		} else {
			mRecordView = mInflater.inflate(R.layout.layout_chat_voice_rightview, null);
		}
		FrameLayout layout = (FrameLayout) mRecordView.findViewById(R.id.layout_chat_voice);
		ViewGroup.LayoutParams lp = layout.getLayoutParams();
		lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * duration));
		if (lp.width > CHAT_VIEW_WIDTH){
			lp.width = CHAT_VIEW_WIDTH;
		}
		TextView mMsgTime = (TextView) mRecordView.findViewById(R.id.tv_chat_recorder_time);
		
		//显示未读标识
		//if (!"2".equals(recorder.getReaded())) {
		//	Drawable unPlayFlag = mContext.getResources().getDrawable(R.drawable.unread_flag);
		//	unPlayFlag.setBounds(0, 0, 10, 10);
		//	mMsgTime.setCompoundDrawables(null, unPlayFlag, null, null);
		//}
		mMsgTime.setText(duration + "\"");
		return mRecordView;
	}
	
	/**
	 * 视频显示视图
	 * @param recorder
	 * @return
	 */
	private View setupVideoMsgView(WeiXinMsgRecorder recorder){
		View mVideoView = null;
		mVideoView = mInflater.inflate(R.layout.layout_chat_video_view, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(320, 240);
		mVideoView.setLayoutParams(params);
		ImageView thumbnailsImg = (ImageView) mVideoView.findViewById(R.id.img_video_thumbnails);
		Bitmap thumbnailsBitmap = BitmapFactory.decodeFile(
				DataFileTools.getInstance().getImageFilePath(recorder.getUrl())); 
		if (thumbnailsBitmap != null){
			thumbnailsImg.setImageBitmap(thumbnailsBitmap);
		} else {
			thumbnailsImg.setImageBitmap(BitmapFactory.decodeResource(
					mContext.getResources(), R.drawable.message_video_bg));
		}
		return mVideoView;
	}
	
	/**
	 * 位置信息显示视图
	 * @param recorder
	 * @return
	 */
	private View setupLocationMsgView(final WeiXinMsgRecorder recorder){
		View mMsgView = null;
		mMsgView = mInflater.inflate(R.layout.layout_chat_locationview, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(320, 240);
		mMsgView.setLayoutParams(params);
		
		TextView locationInfoTv = (TextView) mMsgView.findViewById(R.id.tv_location_info);
		locationInfoTv.setText(recorder.getLabel());
		return mMsgView;
	}
	
	/**
	 * 链接信息显示视图
	 * @param recorder
	 * @return
	 */
	private View setupLinkMsgView(final WeiXinMsgRecorder recorder){
		View mMsgView = mInflater.inflate(R.layout.layout_chat_linkview, null);
		TextView mTitleTv = (TextView) mMsgView.findViewById(R.id.link_title);
		TextView mDetailTv = (TextView) mMsgView.findViewById(R.id.link_detail);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500, LayoutParams.WRAP_CONTENT);
		mMsgView.setLayoutParams(params);
		
		String titleHtml = "<a href=" + recorder.getUrl() + ">" + recorder.getTitle()  + 
			       		   "</a>";
		String descHtml  = "<div>" + recorder.getDescription() + 
				           "</div>";
		Log.i(TAG, "titleHtml:" + titleHtml);
		Log.i(TAG, "descHtml:" + descHtml);
		
		mTitleTv.setText(Html.fromHtml(titleHtml));
		mDetailTv.setText(Html.fromHtml(descHtml));
		return mMsgView;
	}
	
	
	/**********************************************************
	 * Item点击事件
	 **********************************************************/
	private class ClickAction implements OnClickListener{
		
		private View mView;
		
		private int position;
		
		public ClickAction(View mView, int position) {
			super();
			this.mView = mView;
			this.position = position;
		}
		
		@Override
		public void onClick(View view) {
			
			final WeiXinMsgRecorder recorder = mAllRecorders.get(position);
			
			if (recorder == null){
				return ;
			}
			
			
			String msgType = recorder.getMsgtype();
			if (ChatMsgType.VOICE.equals(msgType)){
				
				//更新状态
				WeiMsgRecordDao.getInstance().updatePlayState(recorder.getMsgid());
				((TextView)mView.findViewById(R.id.tv_chat_recorder_time)).setCompoundDrawables(null, null, null, null);

				if (mAudioManager.isPlaying()){
					resetPlayAnim(!ChatMsgRource.RECEIVEED.equals(recorder.getReceived()));
					mAudioManager.stop();
				}
				
				//播放音频
				String filePath = recorder.getFileName();
				if (TextUtils.isEmpty(filePath)){
					Log.e(TAG, "filePath is NULL!!");
					return ;
				}
				
				mAudioManager.play(filePath);
				mAudioManager.setPlayCompletedListener(new AudioPlayCompletedListener() {
					
					@Override
					public void onError(int errorcode) {
						// TODO Auto-generated method stub
						resetPlayAnim(ChatMsgRource.RECEIVEED.equals(recorder.getReceived()));
					}
					
					@Override
					public void onCompleted() {
						// TODO Auto-generated method stub
						resetPlayAnim(ChatMsgRource.RECEIVEED.equals(recorder.getReceived()));
					}
				});
				
				//播放动画
				mPlaySoundAnimView = mView.findViewById(R.id.view_chat_recorder_info);
				if (ChatMsgRource.RECEIVEED.equals(recorder.getReceived())){
					mPlaySoundAnimView.setBackgroundResource(R.drawable.play_sound_left_anim);
				} else {
					mPlaySoundAnimView.setBackgroundResource(R.drawable.play_sound_right_anim);
				}
				AnimationDrawable anim = (AnimationDrawable) mPlaySoundAnimView.getBackground();
				anim.start();
			} else if (ChatMsgType.VIDEO.equals(msgType) 
					|| ChatMsgType.SHORTVIDEO.equals(msgType)){
				WeiMsgRecordDao.getInstance().updatePlayState(recorder.getMsgid());
				Intent intent = new Intent(Intent.ACTION_VIEW);
				String fileName = DataFileTools.getInstance().getVideoFilePath(recorder.getMediaid());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.parse(fileName), "video/mp4");
                mContext.startActivity(intent);
			} else if (ChatMsgType.IMAGE.equals(msgType)){
				recorder.setUrl(WeiMsgRecordDao.getInstance().getRecorderUrl(recorder.getMsgid()));
				Intent intent = new Intent(mContext, ShowImageActivity.class);
				intent.putExtra("WeiXinMsgRecorder", recorder);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			} else if (ChatMsgType.LOCATION.equals(msgType)){
				Intent intent = new Intent(mContext, BaiduMapActivity.class);
				intent.putExtra("WeiXinMsgRecorder", recorder);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			} else if (ChatMsgType.LINK.equals(msgType)){
				try {
					Intent intent = new Intent(mContext, WebViewActivity.class); 
					intent.putExtra("LinkUrl", recorder.getUrl());
					mContext.startActivity(intent);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			} else if (ChatMsgType.TEXT.equals(msgType)){
				CharSequence contentCharSeq = (CharSequence) view.getTag();
				Intent intent = new Intent();
				intent.setClass(mContext, ShowTextActivity.class);
				intent.putExtra("Content", contentCharSeq);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			}
		}
	}
	
	public Intent createIntent(){
		Intent mIntent = null;
		
		return mIntent;
	}
	
	/**
	 * 重置播放动画
	 */
	private void resetPlayAnim(boolean bReceive){
		if (mPlaySoundAnimView == null){
			return ;
		}
		if (bReceive){
			mPlaySoundAnimView.setBackgroundResource(R.drawable.voice_left);
		} else {
			mPlaySoundAnimView.setBackgroundResource(R.drawable.voice_right);
		}
	}

	/**********************************************************
	 * Item长按事件
	 **********************************************************/
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
		mPopupWindow.showAtLocation(parent, 0, x, y);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mCopyTv.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				}
				ClipboardManager clipboardManager = (ClipboardManager) mContext
						.getSystemService(Context.CLIPBOARD_SERVICE);
				clipboardManager.setText(mAllRecorders.get(position).getContent());
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
				WeiXinMsgRecorder recorder = mAllRecorders.get(position);
				if (recorder == null){
					return ;
				}
				//删除数据库中数据
				if (WeiMsgRecordDao.getInstance().deleteRecorder(recorder.getMsgid())){
					//动画
					if (fromOrTo == 0) {
						leftRemoveAnimation(view, position);
						
						//通知更新主界面及AppWidget
						if (position == getCount() - 1){
							WeiXinMsgManager.getInstance().notifyMsgUpdate(recorder);
						}
					} else if (fromOrTo == 1) {
						rightRemoveAnimation(view, position);
					}
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

		public popAction(View view, int position, int fromOrTo) {
			this.position = position;
			this.view = view;
			this.fromOrTo = fromOrTo;
		}

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			int[] arrayOfInt = new int[2];
			// 获取点击按钮的坐标
			v.getLocationOnScreen(arrayOfInt);
			int x = arrayOfInt[0];
			int y = arrayOfInt[1];
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
				mAllRecorders.remove(dismissPosition);
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

	/**
	 * 以下实现在滑动的过程中，不进行图片加载
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}
	
}
