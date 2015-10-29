package com.tcl.wechat.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.controller.ActivityManager;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.utils.DataFileTools;
import com.tcl.wechat.view.listener.UserInfoEditListener;

/**
 * 删除用户对话框
 * @author rex.lei
 *
 */
public class DelUserDialog extends Dialog {
	
	private static final String TAG = DelUserDialog.class.getSimpleName();
	
	private Context mContext;
	
	private View mView;
	
	private BindUser mBindUser;
	
	private TextView mDelUserContentTv;
	
	private Button mCancelBtn, mConfrimBtn;
	
	private ActivityManager mActivityManager;
	
	private UserInfoEditListener mEditListener;
	

	public DelUserDialog(Context context) {
		this(context, R.style.dialogStyle);
	}
	
	public DelUserDialog(Context context, int theme) {
		super(context, theme);
		setCancelable(true);
		mContext = context;
		
		mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_delete_friend, null);
		mDelUserContentTv = (TextView) mView.findViewById(R.id.tv_deluser_info);
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
				// TODO Auto-generated method stub
				confrimOnClick();
			}
		});
		
		setContentView(mView);
		
		mActivityManager = ActivityManager.getInstance();
		mEditListener = mActivityManager.getUserInfoEditListener();
	}
	
	public void showDailog(BindUser bindUser){
		mBindUser = bindUser;
		initView();
		super.show();
	}
	
	private void initView() {
		
		if (mBindUser == null || mBindUser.getNickName() == null){
			Log.w(TAG, "BindUser is NULL!!");
			dismiss();
			return ;
		}
		
		Drawable userIcon = mContext.getResources().getDrawable(R.drawable.default_deluser_icon); 
		int width = userIcon.getMinimumWidth();
		int height = userIcon.getMinimumHeight();
		
		Log.i(TAG, "HeadImageUrl：" + mBindUser.getHeadImageUrl());
		
		if (!TextUtils.isEmpty(mBindUser.getHeadImageUrl())){
			Bitmap bitmap = DataFileTools.getInstance().getBindUserCircleIcon(mBindUser.getHeadImageUrl());
			
			userIcon = new BitmapDrawable(mContext.getResources(), bitmap);
			
			userIcon.setBounds(0, 0, width, height);
		} 
		
		StringBuffer userInfo = new StringBuffer(); 
		String remarkName = mBindUser.getRemarkName();
		String nickName = mBindUser.getNickName();
		if (remarkName != null && !remarkName.equals(nickName)){
			userInfo.append(remarkName).append("(").append(nickName).append(")");
		} else {
			userInfo.append(nickName);
		}
		
		mDelUserContentTv.setPadding(10, 0, 0, 0);
		mDelUserContentTv.setCompoundDrawables(userIcon, null, null, null);
		mDelUserContentTv.setText(String.format(mContext.getString(R.string.hint_delfriend), userInfo.toString()));
	}
	
	/**
	 * 确定删除用户
	 * @param view
	 */
	private void confrimOnClick(){
		if (mEditListener != null){
			mEditListener.onConfirmEditUser(mBindUser);
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
		super.dismiss();
	}
	
}
