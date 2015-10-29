package com.tcl.wechat.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import com.tcl.wechat.R;

public class WebViewActivity extends Activity{
	
	private static final String TAG = WebViewActivity.class.getSimpleName();
	
	private WebView mWebView;
	
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

	private void initView() {
		// TODO Auto-generated method stub
		mWebView = (WebView) findViewById(R.id.webview);
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
