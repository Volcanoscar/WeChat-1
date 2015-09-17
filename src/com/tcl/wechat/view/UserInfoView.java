package com.tcl.wechat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.tcl.wechat.R;
import com.tcl.wechat.utils.ImageUtil;
import com.tcl.wechat.view.listener.UserIconClickListener;
import com.tcl.wechat.view.listener.UserInfoEditListener;

/**
 * 用户头像显示View，包括头像、用户昵称、在线状态、删除状态
 * @author rex.lei
 *
 */
public class UserInfoView extends LinearLayout{

	private static final String TAG = "UserInfoView";
	
	private String mTextTitle;
	private float mTextSize;
	private int mTextColor;
	private Drawable mTextBg;
	private Bitmap mBitmap;
//	private Drawable mImgDrawable;
	
	private View mView;
	private ImageView mUserIconImg;
	private ImageView mOnlineFlagImg;
	private ImageView mDeleteFlagImg;
	private EditText mUserNameEdt;
	
	private boolean bEditable = false;
	
	/**
	 * 点击监听事件
	 */
	private UserIconClickListener mListener;
	public void setUserIconClickListener(UserIconClickListener listener) {
		this.mListener = listener;
	}
	
	private UserInfoEditListener mEditListener;
	public void setUserInfoEditListener(UserInfoEditListener listener){
		mEditListener = listener;
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
//		mImgDrawable = ta.getDrawable(R.styleable.userImageView_imageBackground);
		ta.recycle();
		
		mUserIconImg = (ImageView) mView.findViewById(R.id.user_icon);
		mOnlineFlagImg = (ImageView) mView.findViewById(R.id.img_online_flag);
		mDeleteFlagImg = (ImageView) mView.findViewById(R.id.img_delete_flag);
		mUserNameEdt = (EditText) mView.findViewById(R.id.user_name);
		LinearLayout layout = (LinearLayout) mView.findViewById(R.id.user_icon_layout);
		LayoutParams params = (LayoutParams) layout.getLayoutParams();
		params.width = mBitmap.getWidth();
		params.height = mBitmap.getHeight();
		
		mUserIconImg.setImageBitmap(mBitmap);
		mUserIconImg.setScaleType(ScaleType.CENTER_INSIDE);
		mUserIconImg.setBackgroundResource(R.drawable.user_icon_bg);
		
		mUserNameEdt.setText(mTextTitle);
		mUserNameEdt.setTextSize(mTextSize);
		mUserNameEdt.setTextColor(mTextColor);
		mUserNameEdt.setBackground(mTextBg);
		mUserNameEdt.setGravity(Gravity.CENTER);
		
		mUserIconImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (mListener != null){
					mListener.onClick(view);
				}
			}
		});
		
		mUserIconImg.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if (bEditable){
					mDeleteFlagImg.setVisibility(VISIBLE);
					if (mEditListener != null){
						mEditListener.onDeleteUserEvent();
						return true;
					}
				}
				return false;
			}
		});
		
		mUserNameEdt.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if (bEditable) {
					mUserNameEdt.setFocusable(true);
					mUserNameEdt.setFocusableInTouchMode(true);
				}
				return false;
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(TAG, "action:" + event.getAction());
		return super.onTouchEvent(event);
	}
	
	/**
	 * 设置头像是否可编辑
	 * @param editable
	 */
	public void setUserIconEditable(boolean editable){
		this.bEditable = editable;
	}
	
	/**
	 * 设置用户图像
	 * @param bitmap
	 */
	public void setUserIcon(Bitmap bitmap){
		setUserIcon(bitmap, false);
	}
	
	/**
	 * 设置用户图像
	 * @param bitmap
	 * @param hasBg 是否设置背景
	 */
	public void setUserIcon(Bitmap bitmap, boolean hasBg){
		Bitmap userIcon = ImageUtil.getInstance().createCircleImage(bitmap);
		mUserIconImg.setImageBitmap(userIcon);
		if (hasBg){
			mUserIconImg.setBackgroundResource(R.drawable.user_icon_bg);
		} else {
			mUserIconImg.setBackgroundColor(Color.TRANSPARENT);
		}
	}
	
	/**
	 * 设置用户图像
	 * @param resId
	 */
	public void setUserIcon(int resId){
		Bitmap icon = BitmapFactory.decodeResource(getResources(), resId);
		setUserIcon(icon, false);
	}
	
	/**
	 * 设置用户图像
	 * @param resId
	 */
	public void setUserIcon(int resId, boolean hasBg){
		Bitmap icon = BitmapFactory.decodeResource(getResources(), resId);
		setUserIcon(icon, hasBg);
	}
	
	/**
	 * 设置用户昵称
	 * @param userName
	 */
	public void setUserName(String userName){
		mUserNameEdt.setText(userName);
	}
	
	/**
	 * 设置用户名称是否可见
	 * @param visibility
	 */
	public void setUserNameVisible(int visibility){
		mUserNameEdt.setVisibility(visibility);
	}
	
	/**
	 * 设置用户在线状态
	 * @param isOnLine 是否在线， 默认离线
	 */
	public void setOnLineStatue(boolean isOnLine){
		if (isOnLine){
			mOnlineFlagImg.setVisibility(VISIBLE);
		} else {
			mOnlineFlagImg.setVisibility(GONE);
		}
	}
	
	/**
	 * 设置删除标志
	 * @param isDelete 是否删除， 默认不删除
	 */
	public void setDeleteStatue(boolean isDelete){
		if (isDelete){
			mDeleteFlagImg.setVisibility(VISIBLE);
		} else {
			mDeleteFlagImg.setVisibility(GONE);
		}
	}
	
}
