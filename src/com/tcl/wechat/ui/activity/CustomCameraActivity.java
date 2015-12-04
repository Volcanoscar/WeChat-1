package com.tcl.wechat.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.tcl.wechat.R;

/**
 * 自定义相机界面界面
 * @author Rex.lei
 *
 */
public class CustomCameraActivity extends Activity implements SurfaceHolder.Callback{
	
	private Camera mCamera;
	
	private SurfaceView msSurfaceView;
	private SurfaceHolder mHolder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_camera);
		
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mCamera == null){
			mCamera = getCamera();
			if (mHolder != null){
				setStartPreview(mCamera, mHolder);
			}
		}
	}
	
	private void initView() {
		msSurfaceView = (SurfaceView) findViewById(R.id.sv_preview);
		mHolder = msSurfaceView.getHolder();
		mHolder.addCallback(this);
	}
	
	/**
	 * 拍照
	 * @param view
	 */
	public void capture(View view){
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPictureFormat(ImageFormat.JPEG);
		parameters.setPictureSize(800, 400);
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		mCamera.autoFocus(new AutoFocusCallback() {
			
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				if (success) {
					mCamera.takePicture(null, null, mPictureCallback);
				}
			}
		});
	}
	
	private PictureCallback mPictureCallback = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			//直接返回byte数据
			/*Intent intent = new Intent();
    		intent.putExtra("data", data);
    		setResult(4, intent);*/
			
			//返回保存文件路径
			saveBitmap(data);
		}
	};
	
	private void saveBitmap(byte[] data){
		// for test
		File tempFile = new File(Environment.getDownloadCacheDirectory() + "/temp.png");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(tempFile);
			fos.write(data);
			fos.flush();
			
			Intent intent = new Intent();
			intent.putExtra("path", tempFile.getAbsolutePath());
    		setResult(4, intent);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (fos != null){
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取Camera对象
	 * @return
	 */
	private Camera getCamera(){
		Camera camera = null;
		try {
			camera = Camera.open();
		} catch (Exception e) {
			e.printStackTrace();
			camera = null;
		}
		return camera;
	}
	
	/**
	 * 开始预览相机内容
	 * @return
	 */
	private void setStartPreview(Camera camera, SurfaceHolder holder){
		try {
			camera.setPreviewDisplay(holder);
			//转化横屏为竖屏
			//camera.setDisplayOrientation(90);
			camera.startPreview();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 释放Camera资源
	 */
	private void releaseCamera(){
		if (mCamera != null){
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setStartPreview(mCamera, mHolder);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mCamera.stopPreview();
		setStartPreview(mCamera, mHolder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCamera();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
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
