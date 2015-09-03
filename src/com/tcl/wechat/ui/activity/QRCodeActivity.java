package com.tcl.wechat.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tcl.wechat.R;
import com.tcl.wechat.encoder.QRCodeCreator;
import com.tcl.wechat.utils.ImageUtil;
import com.tcl.wechat.view.UserInfoView;

/**
 * 添加好友界面，提供二维码名片供其他用户使用
 * @author rex.lei
 *
 */
public class QRCodeActivity extends Activity {
	
	private Context mContext;
	private UserInfoView mUserInfoView;
	private ImageView mPersonalQrImg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_qrcode);
		
		mContext = QRCodeActivity.this;
		
		initView();
	}

	/**
	 * 界面初始化
	 */
	private void initView() {
		mUserInfoView = (UserInfoView) findViewById(R.id.uv_personal_icon);
		mPersonalQrImg = (ImageView) findViewById(R.id.img_personal_qrcode);
		
		mUserInfoView.setUserNameVisible(View.GONE);
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		
		createQRCode("WeChat");
	}

	
	/**
	 * 生成二维码名片
	 * @param content
	 */
	private void createQRCode(String content){
		//产生二维码名片
		Bitmap qRCodeBitmap = QRCodeCreator.create(content, 450);
		//获取个人图像
		Bitmap personalIcon = BitmapFactory.decodeResource(
				getResources(), 
				R.drawable.big_head);
		
		//for test
		personalIcon = ImageUtil.getInstance().zoomBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.head_user_icon),
				50, 50);
				
		
		//合并二维码与个人图像
		Bitmap bitmap = Bitmap.createBitmap(
				qRCodeBitmap.getWidth(),
				qRCodeBitmap.getHeight(), 
				qRCodeBitmap.getConfig());
		
		Canvas canvas = new Canvas(bitmap);
		//二维码
 		canvas.drawBitmap(qRCodeBitmap, 0, 0, null);
 		//personIcon绘制在二维码中央
		canvas.drawBitmap(personalIcon, 
				(qRCodeBitmap.getWidth() - personalIcon.getWidth()) / 2, 
				(qRCodeBitmap.getHeight() - personalIcon.getHeight()) / 2, 
				null);
		
		mPersonalQrImg.setImageBitmap(bitmap);
	}
	
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
	
	private void onsop() {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
