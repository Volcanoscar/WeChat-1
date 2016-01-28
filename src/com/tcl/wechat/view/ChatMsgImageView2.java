package com.tcl.wechat.view;

import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.action.imageloader.ImageLoader;
import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.common.IConstant.ChatMsgStatus;
import com.tcl.wechat.controller.listener.UploadListener;
import com.tcl.wechat.database.WeiRecordDao;
import com.tcl.wechat.model.WeiXinMessage;
import com.tcl.wechat.utils.AccessTokenRequest;
import com.tcl.wechat.utils.AccessTokenRequest.TokenRequestListener;
import com.tcl.wechat.utils.PictureUtil;
import com.tcl.wechat.utils.http.HttpMultipartPost;

/**
 * 聊天界面图片显示控件
 * 加载网络图片、上传本地图片、更新进度等功能
 * @author rex.lei
 *
 */
public class ChatMsgImageView2 extends ImageView implements IConstant{
	
	private static final String TAG = ChatMsgImageView2.class.getSimpleName();
	
	private UploadListener mListener;
	
	public ChatMsgImageView2(Context context) {
		this(context, null);
	}
	
	public ChatMsgImageView2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ChatMsgImageView2(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setMinimumWidth(100);
		setMinimumHeight(100);
		setScaleType(ScaleType.CENTER_CROP);
	}
	
	public void setImageBitmap(WeiXinMessage message){
		
		if (TextUtils.isEmpty(message.getUrl())){
			return ;
		}
		loadImageFromNetWork(message.getUrl());
	}
	
	public void setUploadImage(WeiXinMessage message, UploadListener listener){
		
		mListener = listener;
		
		uploadImage(message);
	}
	
	/**
	 * 加载本次图片
	 * @MethodName: loadImage 
	 * @Description: TODO .
	 * @param @param fileName
	 * @return void
	 * @throws
	 */
	public void loadImage(String fileName){
		ImageLoader.getInstance().loadImage(fileName, this);
	}

	/**
	 * 加载图片
	 * @param
	 */
	private void loadImageFromNetWork(String url) {

		WeApplication.getImageLoader().get(url, new ImageListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				setErrorImage();
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				Bitmap bitmap = response.getBitmap();

				if (bitmap != null) {
					setImageBitmap(bitmap);
				} else {
					setErrorImage();
				}
			}
		}, 0, 0);
	}
	
	/**
	 * 上传图片
	 * @param fileName
	 */
	private void uploadImage(final WeiXinMessage message) {
		
		// 获取请求列表
		AccessTokenRequest.getInstance().get(new TokenRequestListener() {

			@Override
			public void onSuccess(String response, Map<String, String> arg1,
					String arg2, int arg3) {
				sendPost(response, message);
			}

			@Override
			public void onRequest() {

			}

			@Override
			public void onError(String arg0, String arg1, int arg2) {
				setErrorImage();
				if (mListener != null){
					mListener.onResult(null);
				}
			}
		});
	}
	
	/**
	 * 上传图片
	 * @param accesstoken
	 * @param fileName
	 */
	private void sendPost(String accesstoken, final WeiXinMessage message){
		
		String fileName = message.getFileName();
		if (TextUtils.isEmpty(fileName)){
			return ;
		}
		
		String thumbFileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
		String savePath = PictureUtil.compressImage(getContext(), fileName, thumbFileName, 80);
		
		if (TextUtils.isEmpty(savePath)){
			setErrorImage();
			if (mListener != null){
				mListener.onResult(null);
			}
			return ;
		}
		
		Log.i(TAG, "thumbFileName:" + thumbFileName + ",savePath:" + savePath);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(URL_TICKET).append("access_token=").append(accesstoken)
				.append("&type=").append(ChatMsgType.IMAGE);
		
		Log.i(TAG, "PostUrl:" + buffer.toString());
		
		new HttpMultipartPost(savePath, new UploadListener() {

			@Override
			public void onResult(String result) {
				// TODO Auto-generated method stub
				if (mListener != null){
					mListener.onResult(result);
				}
			}

			@Override
			public void onProgressUpdate(int progress) {
				if (mListener != null){
					mListener.onProgressUpdate(progress);
				}
				if (progress == 100){
					WeiRecordDao.getInstance().updateMessageState(message.getMsgid(), ChatMsgStatus.SUCCESS);
				}
			}

			@Override
			public void onError(int errorCode) {
				// TODO Auto-generated method stub
				setErrorImage();
				if (mListener != null){
					mListener.onResult(null);
				}
			}
		}).executeOnExecutor(WeApplication.getExecutorPool(), buffer.toString());
	}
	
	/**
	 * 加载错误显示图片
	 */
	private void setErrorImage() {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.pictures_no);
		setImageBitmap(bitmap);
	}
}
