package com.tcl.wechat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.tcl.wechat.R;

/**
 * 图片消息控件
 * @author rex.lei
 *
 */
public class MsgImageView extends View{

	private Paint mPaint ;
	
	private Bitmap mBitmap;
	
	/** 边框颜色  */
	private int mBoundColor;
	/** 边框宽度  */
	private int mBoundWidth = 3;//默认值
	/** 是否带边框 */
	private boolean hasBound = true;
	
	public MsgImageView(Context context) {
		this(context, null);
	}

	public MsgImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MsgImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.borderImageView);
		mBitmap = BitmapFactory.decodeResource(getResources(), 
				ta.getResourceId(R.styleable.borderImageView_imageSrc, 0));
		mBoundColor = ta.getColor(R.styleable.borderImageView_border_color, Color.WHITE);
		mBoundWidth = (int) ta.getDimensionPixelSize(R.styleable.borderImageView_border_width, 3);
		ta.recycle();
		
		//默认图片
		if (mBitmap == null){
			mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pictures_no);
		}
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG);
	}
	
	public void setImageBitmap(Bitmap bitmap){
		mBitmap = bitmap;
		postInvalidate();
	}
	
	public void setImageResource(int srcId){
		mBitmap = BitmapFactory.decodeResource(getResources(), srcId);
		postInvalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width, height;
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width = mBitmap.getWidth();
		}
		
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = mBitmap.getHeight();
		}
		setMeasuredDimension(width, height);
	}
	
	@SuppressLint("DrawAllocation") 
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		
		//绘制边框
		if (hasBound) {
			mPaint.setColor(mBoundColor);
			mPaint.setStyle(Style.STROKE);
			mPaint.setStrokeWidth(mBoundWidth);
			RectF bound = new RectF(new Rect(0, 0, width, height));
			canvas.drawRoundRect(bound, 10, 10, mPaint);
		}
		
		//设置图片的大小
		
		//绘制图片
		Bitmap output = Bitmap.createScaledBitmap(mBitmap, 
				width - getPaddingLeft() - getPaddingRight(), 
				height - getPaddingTop() - getPaddingBottom(), 
				true);
        canvas.drawBitmap(output, getPaddingLeft(), getPaddingTop(), mPaint);
	}
}
