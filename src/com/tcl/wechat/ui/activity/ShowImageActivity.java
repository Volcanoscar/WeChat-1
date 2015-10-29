package com.tcl.wechat.ui.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
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

import com.tcl.wechat.R;
import com.tcl.wechat.utils.DataFileTools;

/**
 * 图片预览控件
 * @author rex.lei
 *
 */
public class ShowImageActivity extends Activity implements OnTouchListener{
	
	private static final String TAG = ShowImageActivity.class.getSimpleName();
	
	private ImageView imgv;  
	  
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
  
    // 获取屏幕分辨率。以1920*1080为例  
    private int displayWidth = 1920;  
    private int displayHeight = 1080;  
  
    private float minScale = 1f;  
    private float maxScale = 10f;  
    private float currentScale = 1f;  
    private float oldDist;  
  
    private Bitmap mBitmap;  
    private int mImgWidth;  
    private int mImgHeight; 
    
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
		
		String fileName = bundle.getString("fileName");
		Log.d(TAG, "fileName:" + fileName);
		if (TextUtils.isEmpty(fileName)){
			return ;
		}
		
        imgv = (ImageView) findViewById(R.id.img_preview);  
        imgv.setOnTouchListener(this);  
  
        mBitmap = DataFileTools.getInstance().getChatImageIcon(fileName);  
        if (mBitmap == null ){
        	mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pictures_no);
        }
        mImgWidth = mBitmap.getWidth();  
        mImgHeight = mBitmap.getHeight(); 
        imgv.setImageBitmap(mBitmap);  
        minScale = getMinScale();  
        matrix.setScale(minScale, minScale);  
        center();  
        imgv.setImageMatrix(matrix);  
    }  
  
    @Override  
    public boolean onTouch(View v, MotionEvent event) {  
        ImageView imgv = (ImageView) v;  
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
        imgv.setImageMatrix(matrix);  
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
