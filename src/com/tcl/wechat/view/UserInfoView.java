package com.tcl.wechat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.view.listener.UserIconClickListener;

/**
 * 用户头像显示View，包括头像、用户昵称
 * @author rex.lei
 *
 */
public class UserInfoView extends LinearLayout{

	private String mTextTitle;
	private float mTextSize;
	private int mTextColor;
	private Drawable mTextBg;
	private Bitmap mBitmap;
	
	private View mView;
	private ImageView mImageView;
	private Drawable mImgDrawable;
	private TextView mTextView;
	
	private UserIconClickListener mListener;
	
	public void setUserIconClickListener(UserIconClickListener listener) {
		this.mListener = listener;
	}
	
	@SuppressLint("NewApi") 
	public UserInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mView = LayoutInflater.from(context).inflate(R.layout.user_view, this, true);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.userImageView);
		mTextTitle = ta.getString(R.styleable.userImageView_textTitle);
		mTextSize = ta.getDimension(R.styleable.userImageView_textSize, 18);
		mTextColor = ta.getColor(R.styleable.userImageView_textColor, Color.BLACK);
		
		
		mTextBg = ta.getDrawable(R.styleable.userImageView_textBackground);
		mBitmap = BitmapFactory.decodeResource(getResources(), 
				ta.getResourceId(R.styleable.userImageView_imageSrc, 0));
		mImgDrawable = ta.getDrawable(R.styleable.userImageView_imageBackground);
		ta.recycle();
		
		mImageView = (ImageView) mView.findViewById(R.id.user_icon);
		mTextView = (TextView) mView.findViewById(R.id.user_name);
		
		mImageView.setImageBitmap(mBitmap);
		mImageView.setBackground(mImgDrawable);
		mImageView.setPadding(3, 3, 3, 3);
		mImageView.setScaleType(ScaleType.CENTER_INSIDE);
		
		mTextView.setText(mTextTitle);
		mTextView.setTextSize(mTextSize);
		mTextView.setTextColor(mTextColor);
		mTextView.setBackground(mTextBg);
		mTextView.setGravity(Gravity.CENTER);
		
		mImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mListener != null){
					mListener.onClick(v);
				}
			}
		});
	}
	
	/**
	 * 设置用户图像
	 * @param bitmap
	 */
	public void setUserIcon(Bitmap bitmap){
		mImageView.setImageBitmap(mBitmap);
	}
	
	/**
	 * 设置用户昵称
	 * @param userName
	 */
	public void setUserName(String userName){
		mTextView.setText(userName);
	}
	
	/**
	 * 设置用户名称是否可见
	 * @param visibility
	 */
	public void setUserNameVisible(int visibility){
		mTextView.setVisibility(visibility);
	}
	
}
