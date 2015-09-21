package com.tcl.wechat.ui.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.tcl.wechat.R;
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.http.AsyncHttpClient;
import com.tcl.wechat.http.AsyncHttpResponseHandler;
import com.tcl.wechat.http.RequestParams;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.data.DataFileTools;
import com.tcl.wechat.utils.ImageUtil;
import com.tcl.wechat.utils.ToastUtil;
import com.tcl.wechat.view.UserInfoView;
import com.tcl.wechat.xmpp.WeiXmppManager;

/**
 * 个人信息界面
 * @author rex.lei
 *
 */
public class PersonalInfoActivity extends Activity{
	
	private static final String TAG = "PersonalInfoActivity";
 	
	private Context mContext;
	
	private static final int PHOTO_REQUEST_CAMERA =  1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 	 3;// 结果
	
	private static final String PHOTO_FILE_NAME = "system.jpg";
	
	private Bitmap bitmap;

	private File tempFile;
	
	/**
	 * 用户控件
	 */
	private UserInfoView mUserInfoView ;
	private EditText mEditUserNameEdt;
	private ImageView mEditUserIcon;
	
	/**
	 * 系统用户信息
	 */
	private BindUser mSystemUser ;
	
	/**
	 * 工具类
	 */
	private DataFileTools mDataFileTools;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_personal_info);
        
        mContext = this;
        mDataFileTools = DataFileTools.getInstance();
        
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
		
		if (getIntent() != null && getIntent().getExtras() != null){
			mSystemUser = getIntent().getExtras().getParcelable("BindUser");
		} else {
			mSystemUser = WeiUserDao.getInstance().getAllUsers().get(0);
		}
	}

	private void initView() {
		
		mUserInfoView = (UserInfoView) findViewById(R.id.uv_personal_icon);
		
		if (mSystemUser != null){
			Bitmap userIcon = DataFileTools.getInstance()
					.getBindUserIcon(mSystemUser.getHeadImageUrl());
			mUserInfoView.setUserIcon(userIcon, false);
			mUserInfoView.setUserName(mSystemUser.getRemarkName());
		}
		
		mEditUserIcon = (ImageView) findViewById(R.id.img_edit_user_icon);
		mEditUserNameEdt = (EditText) findViewById(R.id.edt_user_name);
	}
	
	
	/**
	 * 提交上传头像
	 * @param view
	 */
	public void submitClick(View view){
		
	}
	
	/**
	 * 从相册获取图片
	 * @param view
	 */
	public void selectIconClick(View view){
		// 激活系统图库，选择一张图片
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}
	
	/*
	 * 从相机获取
	 */
	public void camera(View view) {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// 判断存储卡是否可以用，可用进行存储
		if (mDataFileTools.isSdCardExist()) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
		}
		startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTO_REQUEST_GALLERY) {
			if (data != null) {
				// 得到图片的全路径
				Uri uri = data.getData();
				crop(uri);
			}
		} else if (requestCode == PHOTO_REQUEST_CAMERA) {
			if (mDataFileTools.isSdCardExist()) {
				tempFile = new File(Environment.getExternalStorageDirectory(),
						PHOTO_FILE_NAME);
				crop(Uri.fromFile(tempFile));
			} 
		} else if (requestCode == PHOTO_REQUEST_CUT) {
			try {
				bitmap = data.getParcelableExtra("data");
				Bitmap userIcon = ImageUtil.getInstance().createCircleImage(bitmap);
				this.mEditUserIcon.setImageBitmap(userIcon);
				boolean delete = tempFile.delete();
				System.out.println("delete = " + delete);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/*
	 * 上传图片
	 */
	public void upload(View view) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			byte[] buffer = out.toByteArray();

			byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
			String photo = new String(encode);

			RequestParams params = new RequestParams();
			params.put("photo", photo);
			String url = "http://110.65.99.66:8080/jerry/UploadImgServlet";

			AsyncHttpClient client = new AsyncHttpClient();
			client.post(url, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					try {
						if (statusCode == 200) {
							ToastUtil.showToastForced(R.string.update_success);
						} else {
							ToastUtil.showToastForced(String.format(
									getString(R.string.intnet_err), statusCode));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					ToastUtil.showToastForced(String.format(
							getString(R.string.intnet_err), statusCode));
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 剪切图片
	 * 
	 * @function:
	 * @author:Jerry
	 * @date:2013-12-30
	 * @param uri
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
		intent.putExtra("outputX", 250);
		intent.putExtra("outputY", 250);
		// 图片格式
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
