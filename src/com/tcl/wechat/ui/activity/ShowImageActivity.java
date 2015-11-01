package com.tcl.wechat.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.utils.DataFileTools;
import com.tcl.wechat.utils.ImageUtil;
import com.tcl.wechat.utils.MD5Util;
import com.tcl.wechat.utils.ToastUtil;

/**
 * 图片预览控件
 * @author rex.lei
 *
 */
public class ShowImageActivity extends Activity implements OnTouchListener{
	
	private static final String TAG = ShowImageActivity.class.getSimpleName();
	
	private RelativeLayout mLayout;
	private LinearLayout mLinearLayout;
	private ImageView mImageView;  
	private ProgressDialog mDownloadProgressDialog;
	private ProgressDialog mSaveProgressDialog;
	  
    private PointF point0 = new PointF();  
    private PointF pointM = new PointF();  
  
    private final float ZOOM_MIN_SPACE = 10f;  
  
    // 设定事件模式  
    private final int NONE = 0;  
    private final int DRAG = 1;  
    private final int ZOOM = 2;  
    private int mode = NONE;  
  
    private Matrix matrix = new Matrix();  
    private Matrix savedMatrix = new Matrix();  
  
    // 获取屏幕分辨率。
    private int displayWidth = 1920;  
    private int displayHeight = 1080;  
  
    private int mDegree = 90;
    private float minScale = 0.5f;  
    private float maxScale = 10f;  
    private float currentScale = 1f;  
    private float oldDist;  
  
    private int mImgWidth;  
    private int mImgHeight; 
    
    private String mFileName;
    private Bitmap mBitmap;
    
    
    @Override  
    protected void onCreate(Bundle arg0) {
    	super.onCreate(arg0);  
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_preview); 
        
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        displayWidth = dm.widthPixels;
        displayHeight = dm.heightPixels;
        
        init();
    }  
  
	private void init() {  
		
		Bundle bundle = getIntent().getExtras();
		if (bundle == null){
			return ;
		}
		
		mFileName = bundle.getString("fileName");
		Log.d(TAG, "fileName:" + mFileName);
		if (TextUtils.isEmpty(mFileName)){
			return ;
		}
		
		mLayout = (RelativeLayout) findViewById(R.id.layout_imagepreview);
		mLinearLayout = (LinearLayout) findViewById(R.id.layout_imgfun);
        mImageView = (ImageView) findViewById(R.id.img_preview);  
        mImageView.setOnTouchListener(this);  
        
        // 显示进度条
        mDownloadProgressDialog = ProgressDialog.show(this, null, getString(R.string.loading));
  
     	//方法一：直接从本地读取
        /*mBitmap = DataFileTools.getInstance().getChatImageIcon(fileName);  
        if (mBitmap == null ){
        	mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pictures_no);
        }
        mImgWidth = mBitmap.getWidth();  
        mImgHeight = mBitmap.getHeight(); 
        mImageView.setImageBitmap(mBitmap);*/ 
        //方法二：加载本地图片（防止OOM）
        //ImageLoader.getInstance().loadImage(fileName, mImageView);
        //ImageSize size = ImageLoader.getInstance().getImageViewWidth(mImageView);
        //mImgWidth = size.getWidth();  
        //mImgHeight = size.getHeight();
        
        //方法三：加载网络图片
        WeApplication.getImageLoader().get(mFileName, new ImageListener() {
			
			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pictures_no);
		        mImageView.setImageBitmap(mBitmap);
			}
			
			@Override
			public void onResponse(ImageContainer arg0, boolean arg1) {
				// TODO Auto-generated method stub
				mBitmap = arg0.getBitmap();
				if (mBitmap == null){
					return ;
				}
				mDownloadProgressDialog.dismiss();
				mImgWidth = mBitmap.getWidth();  
		        mImgHeight = mBitmap.getHeight(); 
		        Log.i(TAG, "mImgWidth:" + mImgWidth + ", mImgHeight:" + mImgHeight);
		        mImageView.setImageBitmap(mBitmap);  
		        minScale = getMinScale();  
		        matrix.setScale(minScale, minScale);  
		        center();  
		        mImageView.setImageMatrix(matrix);  
			}
		}, 0, 0);
    }  
	
	/**
	 * 预览上一个图片
	 * @param view
	 */
	public void previousView(View view) {
		
		
	}
	
	/**
	 * 预览下一张图片
	 * @param View
	 */
	public void nextView(View View) {
		
	}
	
	/**
	 * 下载图片
	 * @param view
	 */
	public void downloadView(View view) {
		
		mSaveProgressDialog = ProgressDialog.show(this, null, getString(R.string.save_image));
		mSaveProgressDialog.setCancelable(false);
		
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				// TODO Auto-generated method stub
				String fileName = MD5Util.hashKeyForDisk(mFileName);
				String savePath = DataFileTools.getInstance().getTempPath();
				//如果已经保存，则不再保存
				//if (DataFileTools.fileExist(savePath, fileName)){
				//	return true;
				//}
				return ImageUtil.getInstance().saveBitmap(mBitmap, fileName, 
						savePath);
			}
			
			protected void onPostExecute(Boolean result) {
				mSaveProgressDialog.dismiss();
				if (result) {
					ToastUtil.showToastForced(String.format(getString(R.string.save_image_hint), 
							DataFileTools.getInstance().getTempPath()));
				} else {
					ToastUtil.showToastForced(String.format(getString(R.string.save_image_failed), 
							DataFileTools.getInstance().getTempPath()));
				}
			};
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}
	
	/**
	 * 图片旋转
	 * @param view
	 */
	public void rotationView(View view) {
		int bmpWidth = mBitmap.getWidth();
		int bmpHeight = mBitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.postRotate(mDegree);
		mDegree += 90;
		Bitmap resizeBmp = Bitmap.createBitmap(mBitmap, 0, 0, bmpWidth, bmpHeight,
				matrix, true);
		
		mLayout.removeAllViews();
		ImageView imageView = new ImageView(this);
		imageView.setImageBitmap(resizeBmp);
		imageView.setOnTouchListener(this);  
		RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		mLayout.addView(imageView, params);
		mLayout.addView(mLinearLayout);
		setContentView(mLayout);
	}
	
    @Override  
    public boolean onTouch(View v, MotionEvent event) {  
        ImageView mImageView = (ImageView) v;  
        switch (event.getAction() & MotionEvent.ACTION_MASK) {  
        case MotionEvent.ACTION_DOWN:  
            savedMatrix.set(matrix);  
            point0.set(event.getX(), event.getY());  
            mode = DRAG;  
            break;  
        case MotionEvent.ACTION_POINTER_DOWN:  
            oldDist = spacing(event);  
            if (oldDist > ZOOM_MIN_SPACE) {  
                savedMatrix.set(matrix);  
                setMidPoint(event);  
                mode = ZOOM;  
            }  
            break;  
        case MotionEvent.ACTION_UP:  
        case MotionEvent.ACTION_POINTER_UP:  
            mode = NONE;  
            PointF pointEnd = new PointF(event.getX(), event.getY());
            if (pointEnd != null && pointEnd.equals(point0)){
            	finish();
            }
            break;  
        case MotionEvent.ACTION_MOVE:  
            whenMove(event);  
            break;  
  
        }  
        mImageView.setImageMatrix(matrix);  
        checkView();  
        return true;  
    }  
    
    private void whenMove(MotionEvent event) {  
        switch (mode) {  
        case DRAG:  
            matrix.set(savedMatrix);  
            matrix.postTranslate(event.getX() - point0.x, event.getY()  
                    - point0.y);  
            break;  
        case ZOOM:  
            float newDist = spacing(event);  
            if (newDist > ZOOM_MIN_SPACE) {  
                matrix.set(savedMatrix);  
                float sxy = newDist / oldDist;  
                System.out.println(sxy + "<==放大缩小倍数");  
                matrix.postScale(sxy, sxy, pointM.x, pointM.y);  
            }  
            break;  
        }  
    }  
  
    // 两个触点的距离  
    private float spacing(MotionEvent event) {  
        float x = event.getX(0) - event.getX(1);  
        float y = event.getY(0) - event.getY(1);  
        return (float) Math.sqrt(x * x + y * y);  
    }  
  
    private void setMidPoint(MotionEvent event) {  
        float x = event.getX(0) + event.getY(1);  
        float y = event.getY(0) + event.getY(1);  
        pointM.set(x / 2, y / 2);  
    }  
  
    // 图片居中  
    private void center() {  
        RectF rect = new RectF(0, 0, mImgWidth, mImgHeight);  
        matrix.mapRect(rect);  
        float width = rect.width();  
        float height = rect.height();  
        float dx = 0;  
        float dy = 0;  
  
        if (width < displayWidth)  
            dx = displayWidth / 2 - width / 2 - rect.left;  
        else if (rect.left > 0)  
            dx = -rect.left;  
        else if (rect.right < displayWidth)  
            dx = displayWidth - rect.right;  
  
        if (height < displayHeight)  
            dy = displayHeight / 2 - height / 2 - rect.top;  
        else if (rect.top > 0)  
            dy = -rect.top;  
        else if (rect.bottom < displayHeight)  
            dy = displayHeight - rect.bottom;  
  
        matrix.postTranslate(dx, dy);  
    }  
  
    // 获取最小缩放比例  
    private float getMinScale() {  
        float sx = (float) displayWidth / mImgWidth;  
        float sy = (float) displayHeight / mImgHeight;  
        float scale = sx < sy ? sx : sy;  
        if (scale > 1) {  
            scale = 1f;  
        }  
        return scale;  
    }  
  
    // 检查约束条件，是否居中，空间显示是否合理  
    private void checkView() {  
        currentScale = getCurrentScale();  
        if (mode == ZOOM) {  
            if (currentScale < minScale) {  
                matrix.setScale(minScale, minScale);  
            }  
            if (currentScale > maxScale) {  
                matrix.set(savedMatrix);  
            }  
        }  
        center();  
    }  
  
    // 图片当前的缩放比例  
    private float getCurrentScale() {  
        float[] values = new float[9];  
        matrix.getValues(values);  
        return values[Matrix.MSCALE_X];  
    } 

    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	finish();
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
