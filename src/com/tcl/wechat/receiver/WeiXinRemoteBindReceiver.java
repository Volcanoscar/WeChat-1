/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.WeiRemoteBind;
import com.tcl.wechat.utils.BaseUIHandler;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.tcl.webchat.homepage.HomePageUIHandler;

/**
 * @ClassName: GetWeiXinNoticeReceiver
 */

public class WeiXinRemoteBindReceiver extends BroadcastReceiver{

	private static  BaseUIHandler mHandler = null;
	private Context mContext;
	private WeiUserDao weiUserDao;

//	private com.nostra13.universalimageloader.core.ImageLoader image_loader=com.nostra13.universalimageloader.core.ImageLoader.getInstance();
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mContext = context;
	    weiUserDao = WeiUserDao.getInstance();
		//获取远程绑定消息
		WeiRemoteBind weiRemoteBind = (WeiRemoteBind)intent.getExtras().getSerializable("remotebind");
	
		String openid = weiRemoteBind.getOpenid();
		String nickName = weiRemoteBind.getNickname();
		String sex = weiRemoteBind.getSex();
		String headImageurl = weiRemoteBind.getHeadimgurl();
		//两类消息1、远程绑定请求 2、远程绑定成功回复，只有openid，其他是null
		if(nickName==null&&sex==null&&headImageurl==null){
			//收到服务器回复之后才显示提示“加入成功”
			if(weiRemoteBind.getreply().equals("disallow"))
				return;
			BindUser mBinderUser = weiUserDao.getUser(openid);
//			weiUserDao.set(openid, "success");
			weiUserDao.updateStatus(openid, "success");
			
			String nameString = mBinderUser.getNickName();	
//			String addSucces = mContext.getString(R.string.addsuc1)+nameString+mContext.getString(R.string.addsuc2);
			//刷新UI
			if (mHandler != null){
				Log.d("GetWeiXinRemoteBindReceiver", "send bind to to uihandle");
				mHandler.sendEmptyMessage(CommandType.COMMAND_BINDER_TOUI);
			}
		}else{
			BindUser binderUser = new BindUser();
			if (openid !=null){
				binderUser.setOpenId(openid);
			}
			if (nickName !=null){
				binderUser.setNickName(nickName);
			}
			if (sex !=null){
				binderUser.setSex(sex);
			}
			if (headImageurl !=null){
				binderUser.setHeadimageurl(headImageurl);
				Log.i("GetWeiXinRemoteBindReceiver","headImageurl="+headImageurl);
			}
			binderUser.setStatus("wait");
				
			showAuthDialog(context,binderUser);
		}

	}
	private void showAuthDialog(Context context,final BindUser binderUser){
		Button okBt,cancelBt;
		TextView tView;
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);
		lp1.width = 700;
		lp1.height = 280;
//		View v = View.inflate(context, R.layout.apply_layout, null);
//		TextView tips = (TextView) v
//				.findViewById(R.id.textView1);			
//		String asktips = "\""+binderUser.getNickname()+context.getString(R.string.apply);
//		ImageView headImageView = (ImageView)v.findViewById(R.id.head);
//		image_loader.displayImage(binderUser.getHeadimgurl(), headImageView);
//		tips.setText(asktips);
//		final Dialog  b = new Dialog (context,R.style.CustomDialog);
//		b.setContentView(v,lp1);
//		b.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		b.show();		
		
//		tView = (TextView) v.findViewById(R.id.textView1);
//		okBt = (Button) v.findViewById(R.id.loadOkId);
//		okBt.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				// TODO Auto-generated method stub
//				b.dismiss();
//				final HashMap<String, String> hashMap = new HashMap<String, String>();
//				hashMap.put("openid",binderUser.getOpenid());
//				hashMap.put("reply","allow");
//				new WeiXmppCommand(null,  CommandType.SEND_REMOTEBIND_RESPONSE, hashMap).execute();
//				//点击同意后，更新数据库微信用户，此时status是wait
//				weiUserDao.save(binderUser);
//			}
//		});
//		cancelBt = (Button) v.findViewById(R.id.loadCancelId);
//		cancelBt.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				// TODO Auto-generated method stub
//				b.dismiss();
//				final HashMap<String, String> hashMap = new HashMap<String, String>();
//				hashMap.put("openid",binderUser.getOpenid());
//				hashMap.put("reply","disallow");
//				new WeiXmppCommand(null,  CommandType.SEND_REMOTEBIND_RESPONSE, hashMap).execute();								
//				
//			}
//		});
   }
	
	protected static boolean isTopActivity(Context context){
        String packageName = "com.tcl.weixin";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo>  tasksInfo = activityManager.getRunningTasks(1);  
        if(tasksInfo.size() > 0){  
            System.out.println("---------------包名-----------"+tasksInfo.get(0).topActivity.getPackageName());
            //应用程序位于堆栈的顶层  
            if(packageName.equals(tasksInfo.get(0).topActivity.getPackageName())){  
                return true;  
            }  
        }  
        return false;
    }
	public static void setHandler(BaseUIHandler Handler){
		mHandler = (BaseUIHandler) Handler;
	}
}
