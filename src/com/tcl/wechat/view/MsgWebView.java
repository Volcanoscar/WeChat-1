package com.tcl.wechat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.tcl.wechat.R;

/**
 * 自定义Webview控件
 * @author rex.lei
 *
 */
public class MsgWebView extends RelativeLayout{

	private LayoutInflater mInflater;
	
	/** 加载进度条样式 */
	public static int Circle = 0x01;	//圆形加载进度条
	public static int Horizontal = 0x02;//水平加载进度条
	
	private WebView mWebView = null; 
	/** 水平进度条 */
	private ProgressBar mHorizontalBar = null;  
	/** 圆形进度条布局 */
	private RelativeLayout mCircleBar  = null;  
	
	/** 水平进度条的高 */
	private int mHorizontalBarHeight = 8;  
	
	/** 判断是否已经加入进度条 */
	private boolean isAdd = false;  
	/** 进度条样式, 默认水平加载进度条*/
	private int mProgressStyle = Horizontal;  
	
	public MsgWebView(Context context) {
		this(context, null);
	}
	
	public MsgWebView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public MsgWebView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mInflater = LayoutInflater.from(context);
		
		initView(context);
	}

	private void initView(Context context) {
		mWebView = new WebView(context);
		addView(mWebView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		mWebView.setWebChromeClient(new WebChromeClient(){

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				if(newProgress == 100){
					if(mProgressStyle == Horizontal){
						mHorizontalBar.setVisibility(View.GONE);
					}else{
						mCircleBar.setVisibility(View.GONE);
					}
				}else{
					if(!isAdd){
						if(mProgressStyle == Horizontal){
							mHorizontalBar = (ProgressBar) mInflater.inflate(R.layout.progress_horizontal, null);
							mHorizontalBar.setMax(100);
							mHorizontalBar.setProgress(0);
							addView(mHorizontalBar, LayoutParams.MATCH_PARENT, mHorizontalBarHeight);
						}else{
							mCircleBar = (RelativeLayout) mInflater.inflate(R.layout.progress_circle, null);
							addView(mCircleBar, LayoutParams.MATCH_PARENT,  LayoutParams.MATCH_PARENT);
						}
						isAdd = true;
					}
					
					if(mProgressStyle == Horizontal){
						mHorizontalBar.setVisibility(View.VISIBLE);
						mHorizontalBar.setProgress(newProgress);
					}else{
						mCircleBar.setVisibility(View.VISIBLE);
					}
				}
			}
		});
	}
	
	public void setBarHeight(int height){
		mHorizontalBarHeight = height;
	}
	
	public void setProgressStyle(int style){
		mProgressStyle = style;
	}
	
	public void setClickable(boolean value){
		mWebView.setClickable(value);
	}
	
	public void setUseWideViewPort(boolean value){
		mWebView.getSettings().setUseWideViewPort(value);
	}
	
	public void setSupportZoom(boolean value){
		mWebView.getSettings().setSupportZoom(value);
	}
	
	public void setBuiltInZoomControls(boolean value){
		mWebView.getSettings().setBuiltInZoomControls(value);
	}
	
	@SuppressLint("SetJavaScriptEnabled") 
	public void setJavaScriptEnabled(boolean value){
		mWebView.getSettings().setJavaScriptEnabled(value);
	}
	
	public void setCacheMode(int value){
		mWebView.getSettings().setCacheMode(value);
	}

	public void setWebViewClient(WebViewClient value){
		mWebView.setWebViewClient(value);
	}
	
	public void loadUrl(String url){
		mWebView.loadUrl(url);
	}
	

}
