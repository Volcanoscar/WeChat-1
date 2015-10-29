package com.tcl.wechat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.utils.ImageUtil;
import com.tcl.wechat.view.listener.UserIconClickListener;
import com.tcl.wechat.view.listener.UserInfoEditListener;

/**
 * 用户头像显示View，包括头像、用户昵称、在线状态、删除状态
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
	private ImageView mUserIconImg;
	private ImageView mOnlineFlagImg;
	private ImageView mDeleteFlagImg;
	private EditText mUserNameEdt;
	
	private boolean bEditable = false;
	private boolean bHasBg = false;
	
	private String mTag = "";
	
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
					if (mEditListener != null){
						mEditListener.onDeleteUserEvent(getTag());
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

	/**
	 * 设置头像是否可编辑
	 * @param editable
	 */
	public void setUserIconEditable(boolean editable){
		this.bEditable = editable;
	}
	
	public boolean isEditable() {
		return bEditable;
	}

	public ImageView getUserView(){
		return mUserIconImg;
	}
	
	public String getEditUserName(){
		Editable editable = mUserNameEdt.getText();
		if (editable == null){
			return "";
		}
		return editable.toString();
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
		if (userIcon != null){
			mUserIconImg.setImageBitmap(userIcon);
		}
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
	 * 设置用户头像
	 * @param headimageUrl 图像url地址
	 * @note 该方法实现过程：图像下载、头像缓存、头像显示，建议使用该方法
	 */
	public void setUserIcon(String headimageUrl){
		bHasBg = false;
		WeApplication.getImageLoader().get(headimageUrl, mImageListener );
	}
	
	/**
	 * 设置用户头像
	 * @param headimageUrl 图像url地址
	 * @note 该方法实现过程：图像下载、头像缓存、头像显示，建议使用该方法
	 */
	public void setUserIcon(String headimageUrl, boolean hasBg){
		bHasBg = hasBg;
		try {
			WeApplication.getImageLoader().get(headimageUrl, mImageListener );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 设置用户昵称
	 * @param userName
	 */
	public void setUserName(String userName){
		
		if (userName != null && userName.length() >= 8){
			userName = userName.substring(0, 7) + "...";
		}
		setFont("fonts/oop.TTF");
		mUserNameEdt.setText(userName);
	}
	
	/**
	 * 设置用户昵称
	 * @param userName
	 * @param hasBg
	 */
	public void setUserName(String userName, boolean hasBg){
		
		if (userName != null && userName.length() >= 8){
			userName = userName.substring(0, 7) + "...";
		}
		mUserNameEdt.setText(userName);
		setFont("fonts/oop.TTF");
		if (!hasBg){
			mUserNameEdt.setBackgroundColor(Color.TRANSPARENT);
		}
	}
	
	/**
	 * 设置用户名称是否可见
	 * @param visibility
	 */
	public void setUserNameVisible(int visibility){
		mUserNameEdt.setVisibility(visibility);
	}
	
	/**
	 * 设置用户名称是否有背景
	 * @param hasBg
	 */
	public void setUserNameBackGround(boolean hasBg){
		if (!hasBg){
			mUserNameEdt.setBackgroundColor(Color.TRANSPARENT);
		}
	}
	
	/**
	 * 设置昵称字体大小
	 */
	public void setTextSize(float size){
		mUserNameEdt.setTextSize(size);
	}
	
	/**
	 * 设置昵称字体颜色
	 * @param color
	 */
	public void setTextColor(int color){
		mUserNameEdt.setTextColor(color);
	}
	
	/**
	 * 设置昵称字体样式
	 * @param tv
	 * @param fontpath
	 */
	public void setFont(String fontpath) {
		try {
			Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(), fontpath);
			mUserNameEdt.setTypeface(typeFace);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		mUserNameEdt.setFocusable(false);
		mUserNameEdt.setFocusableInTouchMode(false);
		mDeleteFlagImg.setVisibility(GONE);
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

	/**
	 * 设置Tag标签
	 * @return
	 */
	public String getTag() {
		return mTag;
	}

	/**
	 * 获取Tag标签
	 * @param tag
	 */
	public void setTag(String tag) {
		this.mTag = tag;
	}
	
	private ImageListener mImageListener = new ImageListener() {
		
		@Override
		public void onErrorResponse(VolleyError error) {
			// TODO Auto-generated method stub
			Bitmap defuserIcon = BitmapFactory.decodeResource(getResources(), 
					R.drawable.default_user_icon);
			if (defuserIcon != null){
				mUserIconImg.setImageBitmap(defuserIcon);
			}
		}
		
		@Override
		public void onResponse(ImageContainer response, boolean isImmediate) {
			// TODO Auto-generated method stub
			Bitmap userIcon = ImageUtil.getInstance().createCircleImage(response.getBitmap());
			if (userIcon != null){
				mUserIconImg.setImageBitmap(userIcon);
				if (bHasBg){
					mUserIconImg.setBackgroundResource(R.drawable.user_icon_bg);
				} else {
					mUserIconImg.setBackgroundColor(Color.TRANSPARENT);
				}
			} else {
				
			}
		}
	};
}
