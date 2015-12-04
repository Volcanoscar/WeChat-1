package com.tcl.wechat.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tcl.wechat.R;
import com.tcl.wechat.view.MsgWebView;

public class WebViewActivity extends Activity{
	
	private static final String TAG = WebViewActivity.class.getSimpleName();
	
	private MsgWebView mWebView;
	
	private String mUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_webview);
		
		initData();
		initView();
		
	}

	private void initData() {
		mUrl = getIntent().getExtras().getString("LinkUrl");
		Log.i(TAG, "mUrl:" + mUrl);
	}

	@SuppressLint("SetJavaScriptEnabled") 
	private void initView() {
		// TODO Auto-generated method stub
		mWebView = (MsgWebView) findViewById(R.id.webview);
		mWebView.setBarHeight(8);
		mWebView.setClickable(true);
		mWebView.setUseWideViewPort(true);
		mWebView.setSupportZoom(true);
		mWebView.setBuiltInZoomControls(true);
		mWebView.setJavaScriptEnabled(true);
		mWebView.setCacheMode(WebSettings.LOAD_NO_CACHE);		
		mWebView.setWebViewClient(new WebViewClient() {

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				
				view.loadUrl(url);
				return true;
			}
		});
		mWebView.loadUrl(mUrl);
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
