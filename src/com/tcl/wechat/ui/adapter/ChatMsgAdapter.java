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
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.WeiXinMsgRecorder;
import com.tcl.wechat.modle.data.DataFileTools;
import com.tcl.wechat.utils.FontUtil;
import com.tcl.wechat.utils.ImageUtil;

/**
 * 消息列表栏目适配器
 * @author rex.lei
 *
 */
public class ChatMsgAdapter extends BaseAdapter{
	
	private Context mContext;
	private LayoutInflater mInflater;
	private BindUser mBindUser;
	private ArrayList<WeiXinMsgRecorder> mAllRecorders;
	
	private static final int TYPE_COUNT = 2;
	private static final int CHAT_VIEW_WIDTH = 800;
	
	private int mMinItemWidth ;
	private int mMaxItemWidth;
	
	private DataFileTools mDataFileTools;
	
	public ChatMsgAdapter(Context context, BindUser bindUser, ArrayList<WeiXinMsgRecorder> recorders) {
		super();
		this.mContext = context;
		this.mBindUser = bindUser;
		this.mAllRecorders = recorders;
		this.mInflater = LayoutInflater.from(context);
		this.mDataFileTools = DataFileTools.getInstance();
		
		mMinItemWidth = (int) (CHAT_VIEW_WIDTH * 0.1f);
		mMaxItemWidth = (int) (CHAT_VIEW_WIDTH * 0.8f);
	}
	
	public void setData(ArrayList<WeiXinMsgRecorder> recorders) {
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
		return recorder.isReceived() ? 1 : 0;
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
		String msgType = recorder.getMsgtype();
		
		if (ChatMsgType.TEXT.equals(msgType)){
			TextView mMsgTv = new TextView(mContext);
			mMsgTv.setText((String)(recorder.getContent()));
			mMsgTv.setTextColor(Color.BLACK);
			mMsgTv.setTextSize(32);
			mMsgTv.setTypeface(new FontUtil(mContext).getDefultFont());
			holder.mMsgInfoLayout.addView(mMsgTv);
		} else if (ChatMsgType.IMAGE.equals(msgType)){
			ImageView mMsgImg = new ImageView(mContext);
			//在此获取图片
			Bitmap bitmap = mDataFileTools.getChatImageIcon(recorder.getUrl());
			bitmap = ImageUtil.getInstance().zoomBitmap(bitmap, 200);
			mMsgImg.setImageBitmap(bitmap);
			holder.mMsgInfoLayout.addView(mMsgImg);
		} else if (ChatMsgType.VOICE.equals(msgType)) {
			
			//TODO 获取音频文件播放的长度
			int timeLength = 40;
			View mRecordView = null;
			if (recorder.isReceived()){
				mRecordView = mInflater.inflate(R.layout.layout_chat_voice_leftview, null);
			} else {
				mRecordView = mInflater.inflate(R.layout.layout_chat_voice_rightview, null);
			}
			FrameLayout layout = (FrameLayout) mRecordView.findViewById(R.id.layout_chat_voice);
			ViewGroup.LayoutParams lp = layout.getLayoutParams();
			lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * timeLength));
			if (lp.width > CHAT_VIEW_WIDTH){
				lp.width = CHAT_VIEW_WIDTH;
			}
			TextView mMsgTime = (TextView) mRecordView.findViewById(R.id.tv_chat_recorder_time);
			mMsgTime.setText((Math.round(timeLength)) + "\"");
			holder.mMsgInfoLayout.addView(mRecordView);
		
		
		} else if (ChatMsgType.VIDEO.equals(msgType)){
			View mVideoView = null;
			/*int resid = recorder.getThumbmediaid();
			mVideoView = mInflater.inflate(R.layout.layout_chat_video_view, null);
			ImageView mHumbmediaImg = (ImageView) mVideoView.findViewById(R.id.img_video_humbmedia);
			mHumbmediaImg.setBackgroundResource(resid);
			holder.mMsgInfoLayout.addView(mVideoView);*/
		} else {
			
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
		if (recorder.isReceived()){
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
