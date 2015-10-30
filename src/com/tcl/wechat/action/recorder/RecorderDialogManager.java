package com.tcl.wechat.action.recorder;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcl.wechat.R;

/**
 * 录音提示框管理类
 * @author rex.lei
 *
 */
public class RecorderDialogManager {
	
	private Context mContext;
	private Dialog mRecorderDialog;
	
	private ImageView mRecorderIconImg;
	private ImageView mRecorderVoiceImg;
	private TextView mRecorderHintTv;
	
	public RecorderDialogManager(Context context) {
		super();
		this.mContext = context;
	}
	
	private void initView(){
		mRecorderDialog = new Dialog(mContext, R.style.dialogStyle);
		mRecorderDialog.setContentView(R.layout.dialog_recorder);
		mRecorderDialog.setCancelable(false);
		
		mRecorderIconImg = (ImageView) mRecorderDialog.findViewById(R.id.img_recorder_icon);
		mRecorderVoiceImg = (ImageView) mRecorderDialog.findViewById(R.id.img_recorder_voice);
		mRecorderHintTv = (TextView) mRecorderDialog.findViewById(R.id.tv_recorder_hint);
	}
	
	/**
	 * 显示Dialog
	 */
	public void show(){
		initView();
		mRecorderDialog.show();
	}
	
	/**
	 * 录音状态
	 */
	public void recording(){
		if (mRecorderDialog != null && mRecorderDialog.isShowing()){
			mRecorderIconImg.setVisibility(View.INVISIBLE);
			mRecorderVoiceImg.setVisibility(View.VISIBLE);
			mRecorderHintTv.setVisibility(View.VISIBLE);
			
			mRecorderVoiceImg.setImageResource(R.drawable.record_level9);
			mRecorderHintTv.setText(R.string.slide_cancel_send);
		}
	}
	
	/**
	 * 取消
	 */
	public void wanToCancel(){
		if (mRecorderDialog != null && mRecorderDialog.isShowing()){
			mRecorderIconImg.setVisibility(View.VISIBLE);
			mRecorderVoiceImg.setVisibility(View.INVISIBLE);
			mRecorderHintTv.setVisibility(View.VISIBLE);
			
			mRecorderIconImg.setImageResource(R.drawable.recorder_cancel);
			mRecorderHintTv.setText(R.string.release_cancel_send);
		}
	}
	
	/**
	 * 录音时间太短
	 */
	public void tooShort(){
		if (mRecorderDialog != null && mRecorderDialog.isShowing()){
			mRecorderIconImg.setVisibility(View.VISIBLE);
			mRecorderVoiceImg.setVisibility(View.INVISIBLE);
			mRecorderHintTv.setVisibility(View.VISIBLE);
			
			mRecorderIconImg.setImageResource(R.drawable.recorder_voice_too_short);
			mRecorderHintTv.setText(R.string.recorder_too_short);
		}
	}
	
	/**
	 * 隐藏dialog
	 */
	public void dismiss(){
		if (mRecorderDialog != null && mRecorderDialog.isShowing()){
			mRecorderDialog.dismiss();
			mRecorderDialog = null;
		}
	}
	
	/**
	 * 更新音量提示
	 * @param level 音量大小
	 */
	public void updateVoiceLevle(int level){
		if (mRecorderDialog != null && mRecorderDialog.isShowing()){
			int resId = mContext.getResources().getIdentifier("record_level" + level, 
					"drawable", mContext.getPackageName());
			mRecorderVoiceImg.setImageResource(resId);
		}
	}
}
