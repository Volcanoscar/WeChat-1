package com.tcl.wechat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 带倾斜角度TextView
 * @author rex.lei
 *
 */
public class RotateTextView extends TextView {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";
	private static final String ATTR_ROTATE = "rotate";
	private static final int DEFAULTVALUE_DEGREES = 45;
	
	private int mDegrees ;
	
	public RotateTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDegrees = attrs.getAttributeIntValue(NAMESPACE, ATTR_ROTATE, DEFAULTVALUE_DEGREES);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.rotate(mDegrees,getMeasuredWidth()/2,getMeasuredHeight()/2);
	}
}
