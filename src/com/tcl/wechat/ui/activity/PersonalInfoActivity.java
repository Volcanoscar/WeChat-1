package com.tcl.wechat.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.common.IConstant.CommandAction;
import com.tcl.wechat.database.WeiUserDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.utils.DataFileTools;
import com.tcl.wechat.utils.ImageUtil;
import com.tcl.wechat.utils.WeixinToast;
import com.tcl.wechat.view.CustomImageView;
import com.tcl.wechat.view.UserInfoView;
import com.tcl.wechat.xmpp.WeiXmppManager;

/**
 * 个人信息界面
 * @author rex.lei
 *
 */
public class PersonalInfoActivity extends BaseActivity{
	
	private static final String TAG = "PersonalInfoActivity";
 	
	private static final int PHOTO_REQUEST_CAMERA =  1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 	 3;// 结果
	
	private static final String PHOTO_FILE_NAME = "system.jpg";
	
	private Context mContext;
	
	private Bitmap mBitmap;

	private File tempFile;
	
	/**
	 * 用户控件
	 */
	private UserInfoView mUserInfoView ;
	private EditText mEditUserNameEdt;
	private CustomImageView mEditUserIcon;
	
	/**
	 * 系统用户信息
	 */
	private BindUser mSystemUser ;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_personal_info);
        
        mContext = this;

        initData();
        initView();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}
	
	private void initData() {
		//用户是否已经注册
		if (!WeiXmppManager.getInstance().isRegister()){
			return ;
		}
		mSystemUser = WeiUserDao.getInstance().getSystemUser();
	}

	private void initView() {
		
		mUserInfoView = (UserInfoView) findViewById(R.id.uv_personal_icon);
		
		if (mSystemUser != null){
			mUserInfoView.setUserIcon(mSystemUser.getHeadImageUrl(), false);
			mUserInfoView.setUserName(mSystemUser.getRemarkName(), false);
		}
		
		mEditUserIcon = (CustomImageView) findViewById(R.id.img_edit_user_icon);
		mEditUserNameEdt = (EditText) findViewById(R.id.edt_user_name);
	}
	
	
	/**
	 * 提交上传头像
	 * @param view
	 */
	public void submitClick(View view){
		upload(view);
	}
	
	/**
	 * 从相册获取图片
	 * @param view
	 */
	@SuppressLint("InlinedApi") 
	public void selectIconClick(View view){
		showSelectDialog();
	}
	
	private void showSelectDialog() {
		final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.alertdialog);
        TextView cameraTv = (TextView) window.findViewById(R.id.tv_content1);
        cameraTv.setText(R.string.take_picture);
        cameraTv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startCamera();
            	dlg.cancel();
            }
        });
        
        TextView galleryTv = (TextView) window.findViewById(R.id.tv_content2);
        galleryTv.setText(R.string.gallery);
        galleryTv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startAlbum();
            	dlg.cancel();
            }
        });
	}
	
	public void startCamera() {
		try {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 判断存储卡是否可以用，可用进行存储
			if (hasSdcard()) {
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(DataFileTools.getRecordImagePath(), PHOTO_FILE_NAME)));
			}
			startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
		} catch (Exception e) {
			e.printStackTrace();
			//则提示用户打开相机失败！
			WeixinToast.makeText(mContext, getString(R.string.camera_open_failed)).show();
		}
	}

	/**
	 * 从图库获取
	 */
	public void startAlbum() {
		// 激活系统图库，选择一张图片
		try {
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WeixinToast.makeText(mContext, getString(R.string.garrly_open_failed)).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != -1){//返回失败
			return ;
		}
		if (requestCode == PHOTO_REQUEST_GALLERY) {
			if (data != null) {
				// 得到图片的全路径
				Uri uri = data.getData();
				crop(uri);
			}
		} else if (requestCode == PHOTO_REQUEST_CAMERA) {
			if (hasSdcard()) {
				tempFile = new File(DataFileTools.getRecordImagePath(),
						PHOTO_FILE_NAME);
				crop(Uri.fromFile(tempFile));
			} 
		} else if (requestCode == PHOTO_REQUEST_CUT) {
			try {
				mBitmap = data.getParcelableExtra("data");/*decodeUriAsBitmap(imageUri);*///decode bitmap/**/;
				if (mBitmap != null){
					mBitmap = ImageUtil.getInstance().zoomBitmap(mBitmap, 240, 240);
					mEditUserIcon.setImageBitmap(mBitmap);
					if (tempFile != null){
						boolean delete = tempFile.delete();
						Log.i(TAG, "delete = " + delete);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@SuppressWarnings("unused")
	private Bitmap decodeUriAsBitmap(Uri uri){
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	/**
	 * 剪切图片
	 */
	private void crop(Uri uri) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 400);
		intent.putExtra("outputY", 400);
		// 图片格式
		intent.putExtra("scale", true);
		intent.putExtra("outputFormat", "PNG");
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}
	
	/***
	 * 提交信息
	 * @param view
	 */
	public void upload(View view) {
		final String remarkName = mEditUserNameEdt.getText().toString();
		if (!TextUtils.isEmpty(remarkName)){
			if (WeiUserDao.getInstance().updateRemarkName(mSystemUser.getOpenId(), remarkName)){
				mUserInfoView.setUserName(remarkName);
				mEditUserNameEdt.setText("");
			} else {
				Log.e(TAG, "updateRemarkName ERROR!!");
			}
		}
		
		if (mBitmap != null){
			WeApplication.getImageLruCache().putBitmap(mSystemUser.getHeadImageUrl(), mBitmap);
			WeApplication.getImageLoader().put(mSystemUser.getHeadImageUrl(), mBitmap);
			mUserInfoView.setUserIcon(mBitmap, false);
		}
		
		//通知更新系统用户信息
		Intent intent = new Intent();
		intent.setAction(CommandAction.ACTION_UPDATE_SYSTEMUSER);
		sendBroadcast(intent);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		fixInputMethodManagerLeak(mContext);
		super.onDestroy();
	}
	
	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
}
