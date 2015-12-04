package com.tcl.wechat.view;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tcl.wechat.R;

/**
 * 
 * @author rex.lei
 *
 */
public class PopupWindowToast extends PopupWindow{
	
	private TextView mContentTv;
	
	public PopupWindowToast(Activity context) {
		super(context);
		View view  = LayoutInflater.from(context)
				.inflate(R.layout.alter_toast, null);
		mContentTv = (TextView) view.findViewById(R.id.tv_toast_content);
		this.setContentView(view);
		this.setContentView(view);
		this.setWidth(LayoutParams.WRAP_CONTENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(false);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
		
	}
	
	public void setContent(String content){
		
		mContentTv.setText(content);
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			dismiss();
		};
	};
	
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		
		if (this.isShowing()) {
			return ;
		}
		super.showAtLocation(parent, gravity, x, y);
		
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				mHandler.sendEmptyMessageDelayed(0, 1000);
			}
		}, 1000);
	}
}
