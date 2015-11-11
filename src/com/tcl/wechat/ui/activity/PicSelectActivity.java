package com.tcl.wechat.ui.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tcl.wechat.R;
import com.tcl.wechat.action.imageloader.ImageFloder;
import com.tcl.wechat.ui.adapter.PicSelectAdapter;
import com.tcl.wechat.utils.ToastUtil;
import com.tcl.wechat.view.ListImageDirPopupWindow;
import com.tcl.wechat.view.ListImageDirPopupWindow.OnImageDirSelected;

/**
 * 图片选择界面
 * @author rex.lei
 *
 */
public class PicSelectActivity extends Activity implements OnImageDirSelected{

	private static final String TAG = PicSelectActivity.class.getSimpleName();
	
	private static final int MSG_SCAN_COMPLETED = 0x01;
	
	private static final int MSG_SELECT_COMPLETED = 0x02;
	
	private Context mContext;
	
	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String> mImgs;
	
	private Button mIndicatorBtn;
	private GridView mGirdView;
	private PicSelectAdapter mAdapter;
	
	private ProgressDialog mProgressDialog;
	
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

	private RelativeLayout mBottomLy;

	private TextView mChooseDir;
	private TextView mImageCount;
	
	private int mTotalCount = 0;

	private int mScreenHeight;

	private ListImageDirPopupWindow mListImageDirPopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_picture_select);

		mContext = this;

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;
		
		initData();
		initView();
		initEvent();

	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void initData(){
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "No external storage!!", Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, getString(R.string.loading));

		new Thread(new Runnable() {
			@Override
			public void run(){

				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = mContext.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=? or " 
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png", "image/jpg"},
						MediaStore.Images.Media.DATE_MODIFIED);

				Log.i(TAG, mCursor.getCount() + "");
				while (mCursor.moveToNext()){
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					Log.i(TAG, path);
					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)){
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					int picSize = parentFile.list(new FilenameFilter(){
						@Override
						public boolean accept(File dir, String filename){
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;
					mTotalCount += picSize;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize){
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				mCursor.close();

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(MSG_SCAN_COMPLETED);
			}
		}).start();
	}

	/**
	 * 初始化View
	 */
	private void initView(){
		mGirdView = (GridView) findViewById(R.id.gv_image_select);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) findViewById(R.id.id_total_count);
		mIndicatorBtn = (Button) findViewById(R.id.btn_indicator);
		
		mBottomLy = (RelativeLayout) findViewById(R.id.layout_bottom_view);
		
		//初始化
		mImageCount.setText(String.format(getString(R.string.pic_count), mTotalCount));
	}

	private void initEvent(){
		
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
	}
	
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what) {
			case MSG_SCAN_COMPLETED:
				mProgressDialog.dismiss();
				// 为View绑定数据
				data2View();
				// 初始化展示文件夹的popupWindw
				initListDirPopupWindw();
				break;
				
			case MSG_SELECT_COMPLETED:
				
				break;
				
			default:
				break;
			}
			
		}
	};

	/**
	 * 为View绑定数据
	 */
	private void data2View(){
		if (mImgDir == null){
			ToastUtil.showToastForced(R.string.no_picture);
			return;
		}

		mImgs = Arrays.asList(mImgDir.list());
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new PicSelectAdapter(mContext, mImgs,
				R.layout.pic_select_grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(String.format(getString(R.string.pic_count), mTotalCount));
		mAdapter.setSelectPicIndicatorView(mIndicatorBtn);
	};

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	@SuppressLint("InflateParams") 
	private void initListDirPopupWindw(){
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.pic_select_list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener(){

			@Override
			public void onDismiss(){
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	public void selected(ImageFloder floder){

		mImgDir = new File(floder.getDir());
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String filename)
			{
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new PicSelectAdapter(getApplicationContext(), mImgs,
				R.layout.pic_select_grid_item, mImgDir.getAbsolutePath());
		mAdapter.setSelectPicIndicatorView(mIndicatorBtn);
		mGirdView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		mImageCount.setText(String.format(getString(R.string.pic_count), floder.getCount()));
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();
	}
	
	/**
	 * 返回按钮
	 * @param view
	 */
	public void backOnClick(View view){
		
		finish();
	}
	
	/**
	 * 菜单按钮
	 * @param v
	 */
	public void onSendClick(View v){
		
		//在没有图片的情况下，Adapter并不会初始化
		if (mAdapter == null) {
			return ;
		}
		
		String[] selectPics = mAdapter.getSelectedImage();
		if (selectPics != null && selectPics.length > 0) {
			Intent intent = new Intent();
			intent.putExtra("selectPic", selectPics);
			setResult(0, intent);
			finish();
		}
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
