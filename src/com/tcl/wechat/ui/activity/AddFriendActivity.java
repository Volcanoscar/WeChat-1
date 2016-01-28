package com.tcl.wechat.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.database.WeiQrDao;
import com.tcl.wechat.database.WeiUserDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.utils.ImageUtil;
import com.tcl.wechat.view.UserInfoView;

/**
 * 添加好友界面，提供二维码名片供其他用户使用
 * @author rex.lei
 *
 */
public class AddFriendActivity extends BaseActivity {
	
	private UserInfoView mUserInfoView;
	private ImageView mPersonalQrImg;
	private TextView mAddFriendHintTv;
	
	private Context mContext;
	
	private BindUser mSystemUser ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_qrcode);
		
		mContext = this;
		
		initData();
		initView();
	}

	private void initData() {
		// TODO Auto-generated method stub
		mSystemUser = WeiUserDao.getInstance().getSystemUser();
	}

	/**
	 * 界面初始化
	 */
	private void initView() {
		mUserInfoView = (UserInfoView) findViewById(R.id.uv_personal_icon);
		mPersonalQrImg = (ImageView) findViewById(R.id.img_personal_qrcode);
		mAddFriendHintTv = (TextView) findViewById(R.id.tv_add_friend_error);
		
		mUserInfoView.setUserNameVisible(View.GONE);
		
		if (mSystemUser != null){
			mUserInfoView.setUserIcon(mSystemUser.getHeadImageUrl());
			mUserInfoView.setUserNameVisible(View.VISIBLE);
			
			String remarkName = mSystemUser.getRemarkName();
			if (!TextUtils.isEmpty(remarkName)){
				mUserInfoView.setUserName(remarkName, false);
			} else {
				mUserInfoView.setUserName(mSystemUser.getNickName(), false);
				WeiUserDao.getInstance().updateRemarkName(mSystemUser.getOpenId(), 
						mSystemUser.getNickName());
			}
			
			String qrUrl = WeiQrDao.getInstance().getQrUrl();
			if (!TextUtils.isEmpty(qrUrl)){
				ImageUtil.getInstance().setImageBitmap(mPersonalQrImg, qrUrl);
			}
		}
		
		if (WeiUserDao.getInstance().getBindUserCnt() > 10){
			mAddFriendHintTv.setVisibility(View.VISIBLE);
		} else {
			mAddFriendHintTv.setVisibility(View.GONE);
		}
		
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		fixInputMethodManagerLeak(mContext);
		super.onDestroy();
	}
}
