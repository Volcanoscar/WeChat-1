package com.tcl.wechat.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.controller.ActivityManager;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.data.DataFileTools;
import com.tcl.wechat.view.listener.UserInfoEditListener;

/**
 * 删除用户对话框
 * 		使用Activity弹框, 可以增加动画效果
 * @author rex.lei
 *
 */
public class DelUserActivity extends Activity{
	
	private static final String TAG = "DelUserActivity";
	
	private Context mContext;
	
	private BindUser mBindUser;
	
	private TextView mDelUserContentTv;
	
	private DataFileTools mDataFileTools;
	
	private ActivityManager mActivityManager;
	
	private UserInfoEditListener mEditListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_delete_friend);
		
		mContext = this;
		mDataFileTools = DataFileTools.getInstance();
		mActivityManager = ActivityManager.getInstance();
		mEditListener = mActivityManager.getUserInfoEditListener();
		
		handleIntent();
		initView();
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}
	
	private void handleIntent() {
		Intent intent = getIntent();
		mBindUser = intent.getExtras().getParcelable("bindUser");
	}
	
	private void initView() {
		mDelUserContentTv = (TextView) findViewById(R.id.tv_deluser_info);
		
		if (mBindUser == null || mBindUser.getNickName() == null){
			finish();
			return ;
		}
		Drawable userIcon = getResources().getDrawable(R.drawable.default_deluser_icon); 
		int width = userIcon.getMinimumWidth();
		int height = userIcon.getMinimumHeight();
		
		
		Log.i(TAG, "HeadImageUrl：" + mBindUser.getHeadImageUrl());
		
		if (!TextUtils.isEmpty(mBindUser.getHeadImageUrl())){
			Bitmap bitmap = mDataFileTools.getBindUserCircleIcon(mBindUser.getHeadImageUrl());
			
			userIcon = new BitmapDrawable(getResources(), bitmap);
			
			userIcon.setBounds(0, 0, width, height);
		} 
		
		StringBuffer userInfo = new StringBuffer(); 
		String remarkName = mBindUser.getRemarkName();
		String nickName = mBindUser.getNickName();
		if (remarkName != null && !remarkName.equals(nickName)){
			userInfo.append(remarkName).append("(").append(nickName).append(")");
		} else {
			userInfo.append(nickName);
		}
		
		mDelUserContentTv.setPadding(10, 0, 0, 0);
		mDelUserContentTv.setCompoundDrawables(userIcon, null, null, null);
		mDelUserContentTv.setText(String.format(getString(R.string.hint_delfriend), userInfo.toString()));
	}
	
	/**
	 * 确定删除用户
	 * @param view
	 */
	public void delUserOnClick(View view){
		if (mEditListener != null){
			mEditListener.onConfirmEditUser(mBindUser);
		}
		finish();
	}
	
	/**
	 * 取消删除用户
	 * @param view
	 */
	public void cancelOnclck(View view){
		if (mEditListener != null){
			mEditListener.onCancleEditUser();
		}
		finish();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
}
