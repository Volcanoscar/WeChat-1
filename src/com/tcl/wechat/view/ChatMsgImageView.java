package com.tcl.wechat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.model.WeiXinMsgRecorder;

/**
 * 聊天界面图片更新显示控件
 * @author rex.lei
 *
 */
public class ChatMsgImageView extends ImageView{
	
	private static final String TAG = ChatMsgImageView.class.getSimpleName();
	
	private int mProgress = 0; //进度
	
	private int mWidth, mHeight;

	private Paint mPaint;//画笔
	
	private boolean bUploadImageFlag = false; //是否正在上传图片  是：显示进度，否：不显示
	
	public ChatMsgImageView(Context context){
		this(context, null);
	}

	public ChatMsgImageView(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}

	public ChatMsgImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mPaint = new Paint();
	}
	

	@SuppressLint("DrawAllocation") 
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(!bUploadImageFlag){
			return ;
		}
        mPaint.setAntiAlias(true); 
        mPaint.setStyle(Paint.Style.FILL); 
        
        mWidth = getWidth();
        mHeight = getHeight();
        mPaint.setColor(Color.parseColor("#70000000"));
        canvas.drawRect(0, 0, mWidth, mHeight - mHeight * mProgress / 100, mPaint);
        
        mPaint.setColor(Color.parseColor("#00000000"));
        canvas.drawRect(0, mHeight - mHeight * mProgress / 100, mWidth,  mHeight, mPaint);
        
        mPaint.setTextSize(30);
        mPaint.setColor(Color.parseColor("#FFFFFF"));
		mPaint.setStrokeWidth(2);
		Rect rect = new Rect();
		mPaint.getTextBounds("100%", 0, "100%".length(), rect);
		canvas.drawText(mProgress + "%", mWidth / 2 - rect.width() / 2, mWidth / 2, mPaint);
	}
	
	/**
	 * 是否需要上传图片，是：则要显示上传进度
	 * @param flag
	 */
	public void setUploadImageFlag(boolean flag){
		bUploadImageFlag = flag;
	}
	
	public void setProgress(int progress){
		this.mProgress = progress;
		postInvalidate();
	}
	

	/**
	 * 加载图片
	 * @param recorder
	 */
	public void loadImage(WeiXinMsgRecorder recorder){
		
		if (recorder == null){
			return ;
		}
		
		bUploadImageFlag = false;
		String messageid = recorder.getMsgid();
		if (TextUtils.isEmpty(messageid)) {
			return ;
		}
		String url = recorder.getUrl();//WeiMsgRecordDao.getInstance().getRecorderUrl(recorder.getMsgid());
		if (TextUtils.isEmpty(url)){
			setErrorImage();
			return ;
		}
		Log.i(TAG, "loadImage url:" + url);
		WeApplication.getImageLoader().get(url, new ImageListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				setErrorImage();
			}
			
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				Bitmap bitmap = response.getBitmap();
				
				Log.i(TAG, "bitmap:" + bitmap);
				if (bitmap != null){
					setImageBitmap(bitmap);
				} else {
					setErrorImage();
				}
			}
		}, 0, 0);
	}
	
	/**
	 * 加载错误显示图片
	 */
	private void setErrorImage(){
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), 
				R.drawable.pictures_no);
		setImageBitmap(bitmap);
	}
 }  

