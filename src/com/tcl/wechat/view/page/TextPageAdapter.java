package com.tcl.wechat.view.page;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class TextPageAdapter extends PagerAdapter {

	private ArrayList<ReadView> mPagedViews;

	public TextPageAdapter(ArrayList<ReadView> views) {
		super();
		this.mPagedViews = views;
	}

	@Override
	public int getCount() {
		return mPagedViews.isEmpty() ? 0 
				: mPagedViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ReadView readView = mPagedViews.get(position);
		container.addView(readView);
		return readView;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView(mPagedViews.get(position));
	}
	
}
