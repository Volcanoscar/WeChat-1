package com.tcl.wechat.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.view.TouchImageView;

public class ShowImageDialog extends Dialog{

	private Context mContext;
	
	private Bitmap mBitmap;
	
	public ShowImageDialog(Context context) {
		this(context, 0);
	}
	
	public ShowImageDialog(Context context, int theme) {
		super(context, theme);
		setCancelable(true);
		
		mContext = context;
	}

	public void showImageView(Activity context, String fileName) {
		
		 WeApplication.getImageLoader().get(fileName, new ImageListener() {
				
			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pictures_no);
			}
			
			@Override
			public void onResponse(ImageContainer arg0, boolean arg1) {
				// TODO Auto-generated method stub
				mBitmap = arg0.getBitmap();
				TouchImageView view = new TouchImageView((Activity)mContext, mBitmap);
				setContentView(view);
			}
		}, 0, 0);
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		super.cancel();
	}
	
}
