package com.tcl.wechat.view;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.tcl.wechat.WeApplication;

/**
 * 图片消息显示控件
 * @author rex.lei
 *
 */
public class MsgImageView extends ImageView{
	
	private Context mContext;
	
	private static final int MAX_WIDTH = 200;
	private static final int MAX_HEIGHT = 200;
	
	private Bitmap mBitmap;
	private int mScreenWidth;
	private int mScreenHeight;
	private float mScaleWidth;  
    private float mScaleHeight; 
    
    private boolean bScale = false;
	
	public MsgImageView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public MsgImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context) {
		// TODO Auto-generated method stub
		mContext = context;
		DisplayMetrics dm = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScaleHeight = dm.heightPixels;
	}

	/**
	 * 设置显示图片
	 * @param requestUrl
	 */
	public void setImageBitmap(String requestUrl){
		WeApplication.getImageLoader().get(requestUrl, new ImageListener() {
			
			public void onErrorResponse(VolleyError arg0) {
				
			}
			
			public void onResponse(ImageContainer arg0, boolean arg1) {
				mBitmap = arg0.getBitmap();
				showImage(mBitmap);
			}
		}, 0, 0);
	}
	
	private int calculateInSampleSize(Bitmap bitmap, int reqWidth, int reqHeight){
		// 源图片的宽度
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int inSampleSize = 1;

		if (width > reqWidth && height > reqHeight){
			// 计算出实际宽度和目标宽度的比率
			int widthRatio = Math.round((float) width / (float) reqWidth);
			int heightRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = Math.max(widthRatio, heightRatio);
		}
		return inSampleSize;
	}
	
	private Bitmap decodeSampledBitmap(Bitmap bitmap){
//		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
//		final BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(pathName, options);
//		// 调用上面定义的方法计算inSampleSize值
//		options.inSampleSize = calculateInSampleSize(options, reqWidth,
//				reqHeight);
//		// 使用获取到的inSampleSize值再次解析图片
//		options.inJustDecodeBounds = false;
//		Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
		Bitmap outBitmap = null;
		// 源图片的宽度
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inSampleSize = calculateInSampleSize(bitmap, MAX_WIDTH, MAX_HEIGHT);
		options.inJustDecodeBounds = false;
//		Bitmap bitmap1 = BitmapFactory.decodeFile(, options);
				
		
		return outBitmap;
	}

	
	private void showImage(Bitmap bitmap){
		if (mBitmap != null){
			mScaleWidth = ((float)mScreenWidth) / mBitmap.getWidth();  
			mScaleHeight = ((float)mScreenHeight) / mBitmap.getHeight();
			setImageBitmap(decodeSampledBitmap(bitmap));
		}
	}
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch(event.getAction()){  
			case MotionEvent.ACTION_DOWN:{
				bScale = !bScale;
				if (bScale){
					 Matrix matrix=new Matrix();  
                     matrix.postScale(mScaleWidth, mScaleHeight);  
                       
                     Bitmap newBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), 
                    		 mBitmap.getHeight(), matrix, true);  
                     setImageBitmap(newBitmap);  
				} else {
					 Matrix matrix=new Matrix();  
                     matrix.postScale(1.0f,1.0f);  
                     Bitmap newBitmap=Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), 
                    		 mBitmap.getHeight(), matrix, true);  
                     setImageBitmap(newBitmap);  
				}
			}
		}
		return super.onTouchEvent(event);
	}
	

}
