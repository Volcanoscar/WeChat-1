package com.tcl.wechat.action.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageHolder {

	private Bitmap mBitmap;
	private ImageView mImageView;
	private String mPath;
	
	public ImageHolder() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ImageHolder(Bitmap bitmap, ImageView imageView, String path) {
		super();
		this.mBitmap = bitmap;
		this.mImageView = imageView;
		this.mPath = path;
	}

	public Bitmap getmBitmap() {
		return mBitmap;
	}

	public void setmBitmap(Bitmap mBitmap) {
		this.mBitmap = mBitmap;
	}

	public ImageView getmImageView() {
		return mImageView;
	}

	public void setmImageView(ImageView mImageView) {
		this.mImageView = mImageView;
	}

	public String getmPath() {
		return mPath;
	}

	public void setmPath(String mPath) {
		this.mPath = mPath;
	}
	
}
