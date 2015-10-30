package com.tcl.wechat.ui.adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.tcl.wechat.R;
import com.tcl.wechat.utils.ToastUtil;

/**
 * 图片选择适配器
 * @author rex.lei
 *
 */
public class PicSelectAdapter extends CommonAdapter<String>{

	private Context mContext;
	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public static LinkedList<String> mSelectedImage = new LinkedList<String>();
	
	/**
	 * 最多选择图片个数
	 */
	private static final int MAX_SELECT_COUNT = 9;
	
	/**
	 * 当前已选图片个数
	 */
	private int mSelectCount = 0;

	/**
	 * 文件夹路径
	 */
	private String mDirPath;
	
	private Button mIndicatorBtn ;

	public PicSelectAdapter(Context context, List<String> mDatas, int itemLayoutId,
			String dirPath){
		super(context, mDatas, itemLayoutId);
		this.mContext = context;
		this.mDirPath = dirPath;
		mSelectedImage.clear();
	}
	
	/**
	 * 显示已选图片View
	 */
	public void setSelectPicIndicatorView(Button view){
		mIndicatorBtn = view;
	}

	@Override
	public void convert(final ViewHolder helper, final String item){
		//设置no_pic
		helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
		//设置no_selected
		helper.setImageResource(R.id.id_item_select,
						R.drawable.picture_unselected);
		//设置图片
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
		
		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);
		
		mImageView.setColorFilter(null);
		//设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener(){
			//选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v){
				

				// 已经选择过该图片
				if (mSelectedImage.contains(mDirPath + "/" + item)){
					mSelectedImage.remove(mDirPath + "/" + item);
					mSelect.setImageResource(R.drawable.picture_unselected);
					mImageView.setColorFilter(null);
					mSelectCount --;
				} else { // 未选择该图片
					if (mSelectCount >= MAX_SELECT_COUNT){
						ToastUtil.showToastForced(String.format(mContext.getResources()
								.getString(R.string.select_pic_hint), MAX_SELECT_COUNT));
						return ;
					}
					mSelectedImage.add(mDirPath + "/" + item);
					mSelect.setImageResource(R.drawable.pictures_selected);
					mImageView.setColorFilter(Color.parseColor("#77000000"));
					mSelectCount++;
				}
				
				if (mSelectCount > 0){
					mIndicatorBtn.setText(String.format(mContext.getString(R.string.send_pic), 
							mSelectCount, MAX_SELECT_COUNT));
					mIndicatorBtn.setBackgroundResource(R.drawable.send_btn_enable);
				} else {
					mIndicatorBtn.setText(mContext.getString(R.string.send));
					mIndicatorBtn.setBackgroundResource(R.drawable.send_btn_disable);
				}
			}
		});
		
		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectedImage.contains(mDirPath + "/" + item)){
			mSelect.setImageResource(R.drawable.pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}
	}
	
	/**
	 * 获取已经选择的图片
	 */
	public String[] getSelectedImage(){
		if (mSelectedImage == null || mSelectedImage.isEmpty()){
			return null;
		}
		return mSelectedImage.toArray(new String[0]);
	}
}
