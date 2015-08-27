package com.tcl.wechat.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * 圆形图片显示
 * @author rex.lei
 *
 */
public class CircleImageView extends ImageView {

	private static final String TAG = "CircleImageView";
	
	
	public CircleImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 显示图片
	 * @param fileName 文件名称
	 */
	public void setCircleImageBitmap(Bitmap bitmap){
		if (bitmap == null){
			Log.w(TAG, "bitmap is NULL, so show default image!!");
			setDefauleImageView();
			return ;
		}
		
		//for test
		//Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
        int r = 0;  
        if(width > height) {  
            r = height;  
        } else {  
            r = width;  
        }  
        //构建一个bitmap  
        Bitmap backgroundBmp = Bitmap.createBitmap(width,  
                 height, Config.ARGB_8888);  
        Canvas canvas = new Canvas(backgroundBmp);  
        Paint paint = new Paint();  
        paint.setAntiAlias(true);  

        RectF rect = new RectF(0, 0, r, r);  
        canvas.drawRoundRect(rect, r/2, r/2, paint);  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, null, rect, paint); 
        
        this.setImageBitmap(backgroundBmp);
	}
	
	/**
	 * 设置默认图片
	 * 1、url为空或者不存在
	 * 2、图片加载失败
	 * 在以上情况下，会使用默认图片
	 */
	private void setDefauleImageView(){
//		this.setImageResource(R.drawable.ic_launcher);
	}
}
