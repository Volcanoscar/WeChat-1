package com.tcl.wechat.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tcl.wechat.R;
import com.tcl.wechat.action.player.DownloadManager;
import com.tcl.wechat.action.player.listener.DownloadStateListener;
import com.tcl.wechat.db.DeviceDao;
import com.tcl.wechat.db.WeiQrDao;
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.QrInfo;
import com.tcl.wechat.modle.data.DataFileTools;
import com.tcl.wechat.utils.ImageUtil;
import com.tcl.wechat.utils.QrUtil;
import com.tcl.wechat.view.UserInfoView;

/**
 * 添加好友界面，提供二维码名片供其他用户使用
 * @author rex.lei
 *
 */
public class AddFriendActivity extends Activity {
	
	private Context mContext;
	private UserInfoView mUserInfoView;
	private ImageView mPersonalQrImg;
	
	private QrInfo mQrInfo;
	
	private BindUser mSystemUser ;
	
	private QrUtil mQrUtil ;
	
	private DataFileTools mDataFileTools;
	
	private DownloadManager mDownloadManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_qrcode);
		
		mContext = AddFriendActivity.this;
		mQrUtil = new QrUtil(mContext);
		mDataFileTools = DataFileTools.getInstance();
		mDownloadManager = DownloadManager.getInstace();
		
		initData();
		initView();
	}

	private void initData() {
		// TODO Auto-generated method stub
		if (getIntent() != null && getIntent().getExtras() != null){
			mSystemUser = getIntent().getExtras().getParcelable("BindUser");
		} else {
			mSystemUser = WeiUserDao.getInstance().getAllUsers().get(0);
		}
		
		mQrInfo = WeiQrDao.getInstance().getQr();
	}

	/**
	 * 界面初始化
	 */
	private void initView() {
		mUserInfoView = (UserInfoView) findViewById(R.id.uv_personal_icon);
		mPersonalQrImg = (ImageView) findViewById(R.id.img_personal_qrcode);
		
		mUserInfoView.setUserNameVisible(View.GONE);
		
		if (mSystemUser != null){
			Bitmap userIcon = DataFileTools.getInstance()
					.getBindUserIcon(mSystemUser.getHeadImageUrl());
			mUserInfoView.setUserIcon(userIcon, false);
			mUserInfoView.setUserNameVisible(View.VISIBLE);
			mUserInfoView.setUserName(mSystemUser.getNickName());
			
			/*Bitmap qrBitmap = mQrUtil.createQRCode(mSystemUser.getNickName(), 
					ImageUtil.getInstance().createCircleImage(userIcon));*/
//			String qrUrl = WeiQrDao.getInstance().getQrUrl();
			
			Bitmap qrBitmap = mDataFileTools.getQrBitmap(mQrInfo.getUrl());
			if (qrBitmap != null){
				Bitmap mixtrixQr = mQrUtil.mixtrixBitmap(qrBitmap, 
						ImageUtil.getInstance().createCircleImage(userIcon));
				mPersonalQrImg.setImageBitmap(mixtrixQr);
			} else {
				mDownloadManager.setDownloadStateListener(mDownLoadListener);
				mDownloadManager.startToDownload(mQrInfo.getUrl(), "image");
			}
			
			if (qrBitmap == null){
				String deviceid = DeviceDao.getInstance().getDeviceId();
				qrBitmap = mQrUtil.createQRCode(deviceid, 
						ImageUtil.getInstance().createCircleImage(userIcon));
			}
		}
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	DownloadStateListener mDownLoadListener = new DownloadStateListener() {
		
		@Override
		public void startDownLoad() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgressUpdate(int progress) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDownLoadError(int errorCode) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDownLoadCompleted() {
			// TODO Auto-generated method stub
			if (mPersonalQrImg != null){
				Bitmap qrBitmap = mDataFileTools.getQrBitmap(mQrInfo.getUrl());
				Bitmap userIcon = DataFileTools.getInstance()
						.getBindUserIcon(mSystemUser.getHeadImageUrl());
				Bitmap mixtrixQr = mQrUtil.mixtrixBitmap(qrBitmap, 
						ImageUtil.getInstance().createCircleImage(userIcon));
				mPersonalQrImg.setImageBitmap(mixtrixQr);
				mPersonalQrImg.setImageBitmap(qrBitmap);
			}
		}
	};
	
	/**
	 * 返回按键
	 * @param view
	 */
	public void backOnClick(View view){
		finish();
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
		super.onDestroy();
	}
}
