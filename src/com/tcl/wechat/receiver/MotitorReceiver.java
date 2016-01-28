package com.tcl.wechat.receiver;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.utils.DataFileTools;
import com.tcl.wechat.xmpp.WeiXmppService;

/**
 * 用户状态检测器
 * @author rex.lei
 *
 */
public class MotitorReceiver extends BroadcastReceiver implements IConstant{

	private static final String TAG = MotitorReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		Log.d(TAG, "action:" + action);
		if (Intent.ACTION_BOOT_COMPLETED.equals(action)){
			
			//启动服务
			startService(context);
			
		} else if (ACTION_ONLINE_ALARM.equals(action)){
			/*Bundle bundle = intent.getExtras();
			if (bundle != null){
				OnLineStatus status = (OnLineStatus) bundle.getSerializable("OnLineStatus");
				if (status == null){
					Log.w(TAG, "MotitorReceiver ERROR,getParcelableExtra is NULL! ");
					return ;
				}
				String openid = status.getOpenid();
				OnLineStatusMonitor.getInstance().stopMonitor(openid);
				WeiXinMsgControl.getInstance().notifyUserStatusChaned(status);
			}*/
		} else if (ACTION_DATA_CLEARED.equals(action)){
			clearData();
		} else if (ACTION_START_SERVICE.equals(action)){
			Intent service = new Intent(context, WeiXmppService.class);
			service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(service);
		}
	}
	
	/**
	 * 启动服务
	 * @param context
	 */
	private void startService(Context context){
		Intent serviceIntent = new Intent(context, WeiXmppService.class);
    	context.startService(serviceIntent); 
	}
	
	/**
	 * 清除缓存数据：
	 * 	1)聊天信息（音频、视频、图片）
	 * 	2)用户信息（用户头像）
	 * 	3)临时文件
	 * @MethodName: clearData 
	 * @Description: TODO 
	 * @param 
	 * @return void
	 * @throws
	 */
	private void clearData(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//清除缓存数据
				boolean bRet = true;
//				Log.i(TAG, "start to clear data!!");
//				bRet &= deletefile(DataFileTools.getRecordImagePath());
//				bRet &= deletefile(DataFileTools.getRecordAudioPath());
//				bRet &= deletefile(DataFileTools.getRecordVideoPath());
				bRet &= deletefile(DataFileTools.getCachePath());
				Log.i(TAG, "clear data finish, Ret = " + bRet);
			}
		}).start();
	}
	
	/**
	 * 删除文件
	 */
	public boolean deletefile(String delpath){
		try {
			File file = new File(delpath);
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + File.separator + filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
					} else if (delfile.isDirectory()) {
						deletefile(delpath + File.separator + filelist[i]);
					}
				}
				//add by rex.lei  2016.1.28
				//file.delete();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
