package com.tcl.wechat.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.tcl.wechat.R;
import com.tcl.wechat.controller.ActivityManager;
import com.tcl.wechat.logcat.DLog;
import com.tcl.wechat.view.listener.UserInfoEditListener;

/**
 * 编辑用户信息Dialog
 * @author rex.lei
 *
 */
public class EditUserDialog  extends Dialog {
	
	private static final String TAG = EditUserDialog.class.getSimpleName();

	private Context mContext;
	
	private View mView;
	
	private String mEditUserId;
	
	private EditText mEditUserName;
	
	private Button mCancelBtn, mConfrimBtn;
	
	private ActivityManager mActivityManager;
	
	private UserInfoEditListener mEditListener;
	
	public EditUserDialog(Context context) {
		this(context, R.style.dialogStyle);
	}
	
	public EditUserDialog(Context context, int theme) {
		super(context, theme);
		mView = LayoutInflater.from(context)
				.inflate(R.layout.dialog_edit_friend, null);
		setContentView(mView);
		
		mContext = context;
		setCancelable(true);
		
		mActivityManager = ActivityManager.getInstance();
		mEditListener = mActivityManager.getUserInfoEditListener();
		
	}

	public void showDialog(String openid){
		super.show();
		mEditUserId = openid;
		DLog.d(TAG, "mEditUserId:" + mEditUserId);
		initView();
	}
	
	private void initView() {
		mEditUserName = (EditText) mView.findViewById(R.id.edt_edit_username);
		mCancelBtn = (Button) mView.findViewById(R.id.btn_cancel);
		mConfrimBtn = (Button) mView.findViewById(R.id.btn_confirm);
		
		mCancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cancelOnClick();
			}
		});
		
		mConfrimBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confrimOnClick();
			}
		});
	}

	/**
	 * 确定删除用户
	 * @param view
	 */
	private void confrimOnClick(){
		if (mEditListener != null){
			StringBuffer data = new StringBuffer(mEditUserId).append("#")
					.append(mEditUserName.getEditableText().toString());
			DLog.d(TAG, "eventData:" + data);
			mEditListener.onConfirmEditUser(1, data.toString());
		}
		dismiss();
	}
	
	/**
	 * 取消删除用户
	 * @param view
	 */
	private void cancelOnClick(){
		if (mEditListener != null){
			mEditListener.onCancleEditUser();
		}
		dismiss();
	}
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		if (mEditListener != null){
			mEditListener.onCancleEditUser();
		}
		
		InputMethodManager manager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		
		super.dismiss();
	}
}
