package com.tcl.wechat.ui.activity;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class BaseActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null) {
				InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}
	
	public void fixInputMethodManagerLeak(Context destContext) {
		if (destContext == null) {
			return;
		}

		InputMethodManager imm = (InputMethodManager) destContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm == null) {
			return;
		}

		String[] arr = new String[] { "mCurRootView", "mServedView", "mNextServedView" };
		Field f = null;
		Object obj_get = null;
		for (int i = 0; i < arr.length; i++) {
			String param = arr[i];
			try {
				f = imm.getClass().getDeclaredField(param);
				if (f.isAccessible() == false) {
					f.setAccessible(true);
				} // author: sodino mail:sodino@qq.com
				obj_get = f.get(imm);
				if (obj_get != null && obj_get instanceof View) {
					View v_get = (View) obj_get;
					if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
						f.set(imm, null); // 置空，破坏掉path to gc节点
					} else {
						// 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
						// if (QLog.isColorLevel()) {
						// QLog.d(ReflecterHelper.class.getSimpleName(),
						// QLog.CLR,
						// "fixInputMethodManagerLeak break, context is not suitable, get_context="
						// + v_get.getContext()+" dest_context=" + destContext);
						// }
						break;
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		fixInputMethodManagerLeak(this);
		super.onDestroy();
	}

}
