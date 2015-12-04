package com.tcl.wechat.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.tcl.wechat.R;

/**
 * 加载动画
 * @author rex.lei
 *
 */
public class CustomProgressDialog extends Dialog {
	
	private static CustomProgressDialog customProgressDialog = null;
	
	public CustomProgressDialog(Context context){
		super(context);
	}
	
	public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }
	
	public static CustomProgressDialog createDialog(Context context){
		customProgressDialog = new CustomProgressDialog(context,R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.customprogressdialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		
		return customProgressDialog;
	}
 
    /**
     * 
     * [Summary]
     *       setTitile 标题
     * @param strTitle
     * @return
     *
     */
    public CustomProgressDialog setTitile(String strTitle){
    	return customProgressDialog;
    }
    
    /**
     * 
     * [Summary]
     *       setMessage 提示内容
     * @param strMessage
     * @return
     *
     */
    public CustomProgressDialog setMessage(String strMessage){
    	TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
    	
    	if (tvMsg != null){
    		tvMsg.setText(strMessage);
    	}
    	
    	return customProgressDialog;
    }
}