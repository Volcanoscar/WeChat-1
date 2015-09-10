package com.tcl.wechat.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.action.recorder.Recorder;
import com.tcl.wechat.modle.ChatMessage;
import com.tcl.wechat.modle.ChatMsgSource;

/**
 * 消息列表栏目适配器
 * @author rex.lei
 *
 */
public class ChatMsgAdapter extends BaseAdapter{
	
	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<ChatMessage> mDatas;
	
	private static final int TYPE_COUNT = 2;
	private static final int CHAT_VIEW_WIDTH = 800;
	
	private int mMinItemWidth ;
	private int mMaxItemWidth;
	
	public ChatMsgAdapter(Context context, ArrayList<ChatMessage> data) {
		super();
		this.mContext = context;
		this.mDatas = data;
		this.mInflater = LayoutInflater.from(context);
		
		mMinItemWidth = (int) (CHAT_VIEW_WIDTH * 0.1f);
		mMaxItemWidth = (int) (CHAT_VIEW_WIDTH * 0.8f);
	}
	
	public void setData(ArrayList<ChatMessage> data) {
		this.mDatas = data;
	}

	@Override
	public int getCount() {
		return mDatas == null ? 0 : mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public int getItemViewType(int position) {
		ChatMessage chatMessage = mDatas.get(position);
		return chatMessage.getSource() == ChatMsgSource.SRC_RECEVIED ? 1 : 0;
	}
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return TYPE_COUNT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		ChatMessage chatMessage = mDatas.get(position);
		if (convertView == null){
			holder = new ViewHolder();
			convertView = inflateConvertView(chatMessage.getSource());
			holder.mMsgInfoLayout = (RelativeLayout) convertView.findViewById(R.id.layout_chat_msginfo);
			holder.mMsgSendTime = (TextView) convertView.findViewById(R.id.tv_chat_msgsendtime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mMsgSendTime.setText(chatMessage.getTime());
		holder.mMsgInfoLayout.removeAllViews();
		/**
		 * 根据消息的不同类型，显示消息 内容
		 */
		switch (chatMessage.getType()) {
		case TYPE_TEXT:
			TextView mMsgTv = new TextView(mContext);
			mMsgTv.setText((String)(chatMessage.getMessage()));
			mMsgTv.setTextColor(Color.BLACK);
			mMsgTv.setTextSize(24);
			holder.mMsgInfoLayout.addView(mMsgTv);
			break;
			
		case TYPE_IAMGE:
			ImageView mFaceImg = new ImageView(mContext);
			mFaceImg.setImageBitmap((Bitmap)(chatMessage.getMessage()));
			holder.mMsgInfoLayout.addView(mFaceImg);
			break;
			
		case TYPE_AUDIO:
			Recorder recoder = (Recorder) chatMessage.getMessage();
			View mRecordView = null;
			if (chatMessage.getSource() == ChatMsgSource.SRC_RECEVIED){
				mRecordView = mInflater.inflate(R.layout.layout_chat_voice_leftview, null);
			} else {
				mRecordView = mInflater.inflate(R.layout.layout_chat_voice_rightview, null);
			}
			FrameLayout layout = (FrameLayout) mRecordView.findViewById(R.id.layout_chat_voice);
			ViewGroup.LayoutParams lp = layout.getLayoutParams();
			lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * recoder.getSeconds()));
			if (lp.width > CHAT_VIEW_WIDTH){
				lp.width = CHAT_VIEW_WIDTH;
			}
			TextView mMsgTime = (TextView) mRecordView.findViewById(R.id.tv_chat_recorder_time);
			mMsgTime.setText((Math.round(recoder.getSeconds())) + "\"");
			holder.mMsgInfoLayout.addView(mRecordView);
			break;
			
		case TYPE_VIDEO:
			View mVideoView = null;
			int resid = (Integer) chatMessage.getMessage();
			mVideoView = mInflater.inflate(R.layout.layout_chat_video_view, null);
			ImageView mHumbmediaImg = (ImageView) mVideoView.findViewById(R.id.img_video_humbmedia);
			mHumbmediaImg.setBackgroundResource(resid);
			holder.mMsgInfoLayout.addView(mVideoView);
			break;
			
		case TYPE_ANIM :
			break;

		default:
			break;
		}
		return convertView;
	}
	
	/**
	 * 生成布局文件
	 * @param message
	 * @return
	 */
	private View inflateConvertView(ChatMsgSource  source){
		View convertView = null;
		if (source == ChatMsgSource.SRC_RECEVIED){
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
}
