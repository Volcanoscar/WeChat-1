package com.tcl.wechat.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.utils.ExpressionUtil;
import com.tcl.wechat.utils.FontUtil;

/**
 * 文本消息显示界面
 * @TODO 后续实现翻页功能,语音朗读功能
 * @author rex.lei
 *
 */
public class ShowTextActivity extends Activity implements OnTouchListener{
	
	private TextView mContentInfoTv;
	
	private CharSequence mContentSequence;
	
	private PointF pointStart = new PointF();  
	private PointF pointEnd = new PointF();
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_textview);
		
		initData();
		initView();
	}

	private void initData() {
		// TODO Auto-generated method stub
		Bundle bundle = getIntent().getExtras();
		if (bundle != null){
			mContentSequence = (CharSequence) bundle.get("Content");
		}
	}

	private void initView() {
		mContentInfoTv = (TextView) findViewById(R.id.tv_text_info_view);
		
		mContentInfoTv.setMovementMethod(ScrollingMovementMethod.getInstance()); 
		
		if (!TextUtils.isEmpty(mContentSequence)){
			mContentSequence = new ExpressionUtil().StringToSpannale(this, 
					new StringBuffer(mContentSequence));
		} else {
			mContentSequence = getString(R.string.no_message);
		}
		mContentInfoTv.setText(mContentSequence);
		
		
		mContentInfoTv.setOnTouchListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			pointStart.set(event.getX(), event.getY()); 
			break;
			
		case MotionEvent.ACTION_MOVE:
			//可实现缩放效果
			break;
			
		case MotionEvent.ACTION_UP:
			pointEnd = new PointF(event.getX(), event.getY());
			if (pointEnd != null && pointEnd.equals(pointStart)){
            	finish();
            }
			break;
			
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			pointStart.set(event.getX(), event.getY()); 
			break;
			
		case MotionEvent.ACTION_MOVE:
			//可实现缩放效果
			break;
			
		case MotionEvent.ACTION_UP:
			pointEnd = new PointF(event.getX(), event.getY());
			if (pointEnd != null && pointEnd.equals(pointStart)){
            	finish();
            }
			break;
			
		default:
			break;
		}
		return false;
	}
}
