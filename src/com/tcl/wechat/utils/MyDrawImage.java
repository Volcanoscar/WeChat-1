package com.tcl.wechat.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyDrawImage {

	private List<Bitmap> bitmapList;
	private int[] drawableID;
	private int[] imageViewID;
	private int count = 0;
	private Activity a;
	private int layoutId;

	// private BitmapFactory.Options opts;
	public MyDrawImage(Activity a, int[] drawableID, int[] imageViewID) {

		this.drawableID = drawableID;
		this.count = drawableID.length;
		this.a = a;
		this.layoutId = layoutId;
		this.imageViewID = imageViewID;
		this.bitmapList = new ArrayList<Bitmap>();
	}

	public void creatSrcImageView() {

		for (int i = 0; i < count; i++) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 2;
			Bitmap bitmap;
			bitmap = BitmapFactory.decodeResource(a.getResources(),
					drawableID[i], opts);
			bitmapList.add(bitmap);
			View v = a.findViewById(imageViewID[i]);
			if (v instanceof ImageView) {
				((ImageView) v).setImageBitmap(bitmap);
			} else if (v instanceof RelativeLayout || 
					v instanceof Button ||
					v instanceof TextView ||
					v instanceof EditText ||
					v instanceof LinearLayout ||
					v instanceof FrameLayout) {

				BitmapDrawable bd = new BitmapDrawable(bitmap);

				v.setBackgroundDrawable(bd);
			}
			else if (a.findViewById(imageViewID[i]) instanceof LinearLayout) {
				BitmapDrawable bd = new BitmapDrawable(bitmap);

				v.setBackgroundDrawable(bd);
			} else {
				Log.v("lyr", "no fount View!");
			}
			Log.v("lyr", "creatImageView");
		}
	}

	public void recycleImageView() {
		
		for (int i = 0; i < count; i++) {
			if ((bitmapList.get(i) != null)
					&& (bitmapList.get(i).isRecycled() != true)) {
				bitmapList.get(i).recycle();
			}
		}
	}
}