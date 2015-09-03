package com.tcl.wechat.ui.activity;

import android.app.ListActivity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

/**
 * 好友列表界面
 * @author rex.lei
 *
 */
public class FriendListActivity extends ListActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}
}
