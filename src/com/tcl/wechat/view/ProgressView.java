package com.tcl.wechat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * 进度显示控件
 * @author rex.lei
 *
 */
public class ProgressView extends View {
	
	private int mProgress = 0; //进度
	
	private Paint mPaint;//画笔
	
	private int mWidth;
	
	private int mHeight;
	
	public ProgressView(Context context) {
		this(context, null);
	}
	
	public ProgressView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mPaint = new Paint();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
        mPaint.setAntiAlias(true); 
        mPaint.setStyle(Paint.Style.FILL); 
        
//        mPaint.setColor(Color.parseColor("#70000000"));
//        canvas.drawRect(0, 0, getWidth(), getHeight()-getHeight()*mProgress/100, mPaint);
//        
//        mPaint.setColor(Color.parseColor("#00000000"));
//        canvas.drawRect(0, getHeight()-getHeight()*mProgress/100, getWidth(),  getHeight(), mPaint);
//        
//        mPaint.setTextSize(30);
//        mPaint.setColor(Color.parseColor("#FFFFFF"));
//		mPaint.setStrokeWidth(2);
//		Rect rect = new Rect();
//		mPaint.getTextBounds("100%", 0, "100%".length(), rect);
//		canvas.drawText(mProgress+"%", getWidth()/2-rect.width()/2,getHeight()/2, mPaint);
        mPaint.setColor(Color.parseColor("#70000000"));
        canvas.drawRect(0, 0, mWidth, mHeight - mHeight * mProgress / 100, mPaint);
        
        mPaint.setColor(Color.parseColor("#00000000"));
        canvas.drawRect(0, 
        		mHeight - mHeight * mProgress / 100, 
        		mWidth,  
        		mHeight, 
        		mPaint);
        
        mPaint.setTextSize(30);
        mPaint.setColor(Color.parseColor("#FFFFFF"));
		mPaint.setStrokeWidth(2);
		Rect rect = new Rect();
		mPaint.getTextBounds("100%", 0, "100%".length(), rect);
		canvas.drawText(mProgress+"%", 
				mWidth / 2 - rect.width() / 2,
				mHeight / 2, 
				mPaint);
	}
	
	public void setImageSize(int width, int height){
		mWidth = width;
		mHeight = height;
	}
	
	public void setProgress(int progress){
		this.mProgress = progress;
		postInvalidate();
	}
}
