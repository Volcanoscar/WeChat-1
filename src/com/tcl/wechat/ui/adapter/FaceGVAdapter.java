package com.tcl.wechat.ui.adapter;

import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcl.wechat.R;

public class FaceGVAdapter extends BaseAdapter {
	
	private Context mContext;
	
	private LayoutInflater mInflater;
	
	private ArrayList<String> mFaceList;

	public FaceGVAdapter(Context context, ArrayList<String> faceList) {
		super();
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.mFaceList = faceList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFaceList == null ? 0 : mFaceList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mFaceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("InflateParams") 
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHodler hodler;
		if (convertView == null) {
			hodler = new ViewHodler();
			convertView = mInflater.inflate(R.layout.layout_face_image, null);
			hodler.mFaceImg = (ImageView) convertView.findViewById(R.id.img_face);
			hodler.mFaceTv = (TextView) convertView.findViewById(R.id.tv_text);
			convertView.setTag(hodler);
		} else {
			hodler = (ViewHodler) convertView.getTag();
		}
		try {
			Bitmap mBitmap = BitmapFactory.decodeStream(mContext.getAssets()
					.open("face/png/" + mFaceList.get(position)));
			hodler.mFaceImg.setImageBitmap(mBitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		hodler.mFaceTv.setText("face/png/" + mFaceList.get(position));
		return convertView;
	}
	class ViewHodler {
		ImageView mFaceImg;
		TextView mFaceTv;
	}
}
