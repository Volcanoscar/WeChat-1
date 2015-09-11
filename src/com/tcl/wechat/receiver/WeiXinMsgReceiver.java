/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.receiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.Urlandmd5;
import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.common.WeiConstant.PlayStyle;
import com.tcl.wechat.db.MyUsers;
import com.tcl.wechat.db.WeiRecordDao;
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.modle.BinderUser;
import com.tcl.wechat.modle.WeiXinMsg;
import com.tcl.wechat.ui.activity.MainActivity;
import com.tcl.wechat.utils.BaseUIHandler;
import com.tcl.wechat.utils.CommonsFun;
import com.tcl.wechat.utils.ExpressionUtil;
import com.tcl.wechat.utils.SystemRelfect;
import com.tcl.wechat.utils.UIUtils;
import com.tcl.wechat.xmpp.WeiXmppCommand;

/**
 * 微信消息接收器
 * @author rex.lei
 *
 */
public class WeiXinMsgReceiver extends BroadcastReceiver{
	
	private static final String TAG = WeiXinMsgReceiver.class.getSimpleName();

	private Context mContext = null;
	
	private static Handler newsHandler = null;
	private static Handler newsShareUIHandler = null;
	private  Handler imageload_Handler =  new Handler(){
	       public void handleMessage(Message msg) {
	           // TODO Auto-generated method stub
	    	 Log.i(TAG, "msg="+msg.what);
	    	 if (msg.what == LOADIMAGE_OVER) {
	    		 Urlandmd5 urlandmd5 = (Urlandmd5) msg.obj;
	    		 Log.i(TAG, "url="+urlandmd5.geturl()+";\n"+"saveurl="+urlandmd5.getsaveurl());
	    		 if(recoredDao==null)
	    			 recoredDao = new WeiRecordDao(mContext); 
	    		 recoredDao.updateContent(urlandmd5.geturl(), urlandmd5.getsaveurl());		
	    	 }		    	 
	       }
	    };
	boolean mIsInitSuccess;
	WeiUserDao userDao = null;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	.showStubImage(0)
	.showImageForEmptyUri(0)
	.showImageOnFail(0)
	.cacheInMemory()
	.cacheOnDisc()
	.bitmapConfig(Bitmap.Config.RGB_565)
	.handler(imageload_Handler)
	.build();
	private com.nostra13.universalimageloader.core.ImageLoader image_loader=com.nostra13.universalimageloader.core.ImageLoader.getInstance();
	//private String fileName = null;
	private Thread thread;
	private WeiRecordDao recoredDao;
	//
	private static TextView imagetips ,username;
	private static TextView texttips,contentTextView;//文本对话框
	private static StringBuffer contentstr= new StringBuffer();
	private static ImageView headImageView,texthead;
	private static  Dialog  imageDialog,textDialog; 
	private static String imageopenid ,textopenid=null;
	private static ArrayList<String> list =new ArrayList<String>();
	private final int DOWN_OVER = 100;
	private static final int LOADIMAGE_OVER = 10100;


	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Received push notification!");
		
		mContext = context;
		// 初始化					
		recoredDao = new WeiRecordDao(context);
		final WeiXinMsg weiXinMsg = (WeiXinMsg)intent.getExtras().getSerializable("weiXinMsg");
		String type = weiXinMsg.getMsgtype();
		
		if (type.equals("image")){
			Log.d(TAG, "图片连接="+weiXinMsg.getUrl());
			recoredDao.save(weiXinMsg);
			context.getContentResolver().notifyChange(MyUsers.CONTENT_RECORD, null);

			//收到消息在后台下载，否则token两小时内就会过期
			image_loader.loadImage(weiXinMsg.getUrl(), options, null);
			
			//更新新消息条数
			WeiUserDao userDao = new WeiUserDao(context);
			userDao.addNews(weiXinMsg.getOpenid());
			//如果当前页面在主页，需要刷新新消息条数
			if(newsHandler!=null){
				newsHandler.sendEmptyMessage(CommandType.FRESH_NEWS);
			}
			//符合下面三种情况才弹出提示框  1、设置是消息开启2、不是离线消息3、系统没有在搜台
			if(WeiConstant.SET_SHARE_STYLE && weiXinMsg.getofflinemsg().equals("false")
					&& !SystemRelfect.getProperties("sys.scan.state", "off").equals("on")){			
				startPlay(context,weiXinMsg,type);
			}
		}else if(type.equals("video")){
			startDownLoad(weiXinMsg,context);			
		}else if(type.equals("text")){
			//文本
			if(!SystemRelfect.getProperties("sys.scan.state", "off").equals("on")){

				Log.i(TAG,"type="+type+";文本信息msg="+weiXinMsg.getContent());
				
				
				showTextDialog(weiXinMsg, context);
			}
			/*new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub							
					 WeiConstant.logoRecognition.reponseCurTVStatus(null,weiXinMsg.getOpenid());					
				}
			}).start();*/
			
			//WeiConstant.mTuneChannel.tunechannel_tv(weiXinMsg.getContent());
		}
		/*else if (type.equals("voiceassistant")){//在微信输入文本控制TV，通过调用TV端的语音小助手控制TV
			Log.i(TAG,"voiceassistant控制信息msg="+weiXinMsg.getContent());
			if(!SystemRelfect.getProperties("sys.scan.state", "off").equals("on")){
				Intent voiceintent = new Intent("com.iflytek.xiri2.START");
				voiceintent.putExtra("startmode", "text");
				voiceintent.putExtra("text", weiXinMsg.getContent());
	            context.startService(voiceintent);
			}
		}*/
		else if (type.equals("voice")){//在微信输入文本控制TV，通过调用TV端的语音小助手控制TV
			Log.i(TAG,"voice控制信息msg="+weiXinMsg.getRecognition());
			if(!SystemRelfect.getProperties("sys.scan.state", "off").equals("on")){
				if(!WeiConstant.WechatConfigure.CurConfigure.equals(WeiConstant.WechatConfigure.SimpleVer)){
					Intent voiceintent = new Intent("com.iflytek.xiri2.START");
					voiceintent.putExtra("startmode", "text");
//					voiceintent.putExtra("text", weiXinMsg.getRecognition());
		            context.startService(voiceintent);
				}else{
//					TCLToast.makeText(context, context.getString(R.string.unsupportvoice), Toast.LENGTH_LONG).show();
				}				
			}
	
		}else if(type.equals("barrage")){
			Log.i(TAG,"弹幕消息内容："+weiXinMsg.getContent());
			if(!SystemRelfect.getProperties("sys.scan.state", "off").equals("on")
					&&!WeiConstant.WechatConfigure.CurConfigure.equals(WeiConstant.WechatConfigure.SimpleVer)){

	 
				 
				//服务器反馈
				/*final HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("channelcode",weiXinMsg.getchannelname());		
				new WeiXmppCommand(null, CommandType.COMMAND_RESPONSEBARRRAGE, hashMap).execute();*/
				
				return;
			}
		}
		else if(type.equals("tvprogramnotice")){//预约节目提醒
			Log.i(TAG,"预约提醒的频道号："+weiXinMsg.getchannelname()+";节目名称="+weiXinMsg.getContent());
			//显示对话框提醒
			if(!SystemRelfect.getProperties("sys.scan.state", "off").equals("on")){

				showBookingDialog(weiXinMsg);
				//服务器反馈
				final HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("channelcode",weiXinMsg.getchannelname());		
				new WeiXmppCommand(null, CommandType.COMMAND_TVPROGRAMNOTICE, hashMap).execute();
				
				return ;
			}
		}
		//收到消息给服务器发送反馈		
		final HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("openid",weiXinMsg.getOpenid());
		hashMap.put("msgtype",weiXinMsg.getMsgtype());
		hashMap.put("msgid", weiXinMsg.getmsgid());
		new WeiXmppCommand(null, CommandType.COMMAND_MSGRESPONSE, hashMap).execute();
	}
	
	public static void setHandler(BaseUIHandler Handler){
		newsHandler = (BaseUIHandler) Handler;
	}

	public static void setShareUIHandler(BaseUIHandler Handler){
		newsShareUIHandler = (BaseUIHandler) Handler;
	}
	private CharSequence StringToSpannale(StringBuffer content,Context context){
		//将聊天内容中的QQ表情字符转化为表情显示
			CharSequence contentCharSeq = content.toString();		
			//Log.i(TAG,"contentstr.toString() = " + content.toString());
			//如果新收到的聊天内容中有/:标识，则进行表情字符匹配
			Pattern p = Pattern.compile("/:");
			Matcher m = p.matcher(content.toString());
			if (m.find()) {
				//判断QQ表情的正则表达式 
				String qqfaceRegex = "/::\\)|/::~|/::B|/::\\||/:8-\\)|/::<|/::\\$|/::X|/::Z|/::'\\(|/::-\\||/::@|/::P|/::D|/::O|/::\\(|/::\\+|/:--b|/::Q|/::T|/:,@P|/:,@-D|/::d|/:,@o|/::g|/:\\|-\\)|/::!|/::L|/::>|/::,@|/:,@f|/::-S|/:\\?|/:,@x|/:,@@|/::8|/:,@!|/:!!!|/:xx|/:bye|/:wipe|/:dig|/:handclap|/:&-\\(|/:B-\\)|/:<@|/:@>|/::-O|/:>-\\||/:P-\\(|/::'\\||/:X-\\)|/::\\*|/:@x|/:8\\*|/:pd|/:<W>|/:beer|/:basketb|/:oo|/:coffee|/:eat|/:pig|/:rose|/:fade|/:showlove|/:heart|/:break|/:cake|/:li|/:bome|/:kn|/:footb|/:ladybug|/:shit|/:moon|/:sun|/:gift|/:hug|/:strong|/:weak|/:share|/:v|/:@\\)|/:jj|/:@@|/:bad|/:lvu|/:no|/:ok|/:love|/:<L>|/:jump|/:shake|/:<O>|/:circle|/:kotow|/:turn|/:skip|/:oY|/:#-0|/:hiphot|/:kiss|/:<&|/:&>";
				SpannableString spannableContent = new ExpressionUtil().getExpressionString(context, content.toString(), qqfaceRegex);//
				contentCharSeq = spannableContent;
			}
			return contentCharSeq;
	}
	private void showTextDialog(WeiXinMsg weiXinMsg,Context context){	
		String content = weiXinMsg.getContent();
		 if(textopenid==null){
			 textopenid = weiXinMsg.getOpenid();	
			 contentstr.append(content+"     "+UIUtils.Milli2Date(weiXinMsg.getCreatetime(),context));
		 }else{		 
			contentstr.insert(0, content+"     "+UIUtils.Milli2Date(weiXinMsg.getCreatetime(),context)
					+"\n");
			/* contentstr.append("\n"+Util.Milli2Date(weiXinMsg.getCreatetime(),context)
					 +"  "+content);*/
		 }
	
		if(textDialog!=null && textDialog.isShowing()){
			if(textopenid.equals(weiXinMsg.getOpenid())){//同一个用户,只要增加内容				
				contentTextView.setText(StringToSpannale(contentstr, context));//
				return;
			}
			else {//换了一个新用户，修改头像，名称和数目,这个需要测试验证
				if(userDao==null)
					userDao = new WeiUserDao(mContext);
				List<BinderUser> binderUserlist = userDao.find(weiXinMsg.getOpenid());
				String shareName = null;
				String headurl = null;
				if(binderUserlist!=null&&binderUserlist.size()>0){
					shareName = binderUserlist.get(0).getNickname();
					headurl = binderUserlist.get(0).getHeadimgurl();
				}else{
					Log.i(TAG,"找不到绑定的用户信息，不显示");
					return;
				}
				contentstr.delete(0, contentstr.length());				
				contentstr.append(content+"     "+UIUtils.Milli2Date(weiXinMsg.getCreatetime(),context));
				textopenid = weiXinMsg.getOpenid();
//				String asktips = "\""+shareName+mContext.getString(R.string.sharetexttips);
//				texttips.setText(asktips);			
				contentTextView.setText(StringToSpannale(contentstr, context));//
				image_loader.displayImage(headurl, texthead);
				return;
			}
		}

		if(userDao==null)
			userDao = new WeiUserDao(mContext);
		List<BinderUser> binderUserlist = userDao.find(weiXinMsg.getOpenid());
		
		
		
		if (binderUserlist != null){
			Log.i(TAG, "binderUserlist");
			for (int i = 0; i < binderUserlist.size(); i++) {
				Log.i(TAG, "BinderUser:" + binderUserlist.get(i).toString());
			}
		}
		
		String shareName = null;
		String headurl = null;
		if(binderUserlist!=null&&binderUserlist.size()>0){
			shareName = binderUserlist.get(0).getNickname();
			headurl = binderUserlist.get(0).getHeadimgurl();
		}else{
			Log.i(TAG,"找不到绑定的用户信息，不显示");
			return;
		}
		Button okBt;
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);
		lp1.width = 700;
		lp1.height = 280;
		
		
		
//		View v = View.inflate(mContext, R.layout.textdl_layout, null);
//		texttips = (TextView) v.findViewById(R.id.textView1);			
//		String asktips = "\""+shareName+mContext.getString(R.string.sharetexttips);
//		texttips.setText(asktips);
//		contentTextView = (TextView)v.findViewById(R.id.textcontent);
//		contentTextView.setText(StringToSpannale(contentstr, context));	
//		contentTextView.setMovementMethod(ScrollingMovementMethod.getInstance()); 
//		texthead = (ImageView)v.findViewById(R.id.head);
//		image_loader.displayImage(headurl, texthead);
		
		
//		textDialog = new Dialog (mContext,R.style.CustomDialog);
//		textDialog.setContentView(v,lp1);
//		textDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		textDialog.show();		

//		okBt = (Button) v.findViewById(R.id.loadOkId);
//		okBt.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				// TODO Auto-generated method stub
//				textDialog.dismiss();
//				contentstr.delete(0, contentstr.length());
//			}
//		});
//		Log.i(TAG,"length="+contentstr.length());
//		
//		okBt.requestFocus();

	}
	public void startPlay(final Context context,final WeiXinMsg weiXinMsg,String type){
		//如果当前页面在账号详情页面，需要刷新新消息条数
		if(newsShareUIHandler!=null){
			newsShareUIHandler.sendEmptyMessage(CommandType.FRESH_NEWS);
		}
		if(type.equals("image")){			
				// TODO Auto-generated method stub
			if(CommonsFun.isTopActivity(context)){//不弹出对话框情况：1、当前已经弹出一个对话框2、当前在图片播放页面
				//更新为已读
				recoredDao.updateRead(weiXinMsg.getOpenid(), true);
				String url = weiXinMsg.getUrl();
				list.clear();
				list.add(url);
				Intent it = new Intent();
				it.putExtra("index", 0);
				it.putExtra("playstyle", PlayStyle.REALTIME_SHARE_PLAY);
				
				it.putExtra("openid", weiXinMsg.getOpenid());
				it.putStringArrayListExtra("playList", list);							
				it.setClass(mContext.getApplicationContext(),MainActivity.class);
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);			
				it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				mContext.startActivity(it);					
				Log.i(TAG,"startPlay--url="+list.get(0));
			}else{
				showImageDialog(context, weiXinMsg);
			}
								
		}else if(type.equals("video")){
			String openid = weiXinMsg.getOpenid();
			if(userDao==null)
				userDao = new WeiUserDao(mContext);
			List<BinderUser> binderUserlist = userDao.find(openid);
			String shareName = null;
			String headurl = null;
			if(binderUserlist!=null&&binderUserlist.size()>0){
				shareName = binderUserlist.get(0).getNickname();
				headurl = binderUserlist.get(0).getHeadimgurl();
			}else{
				Log.i(TAG,"找不到绑定的用户信息，视频不显示");
				return;
			}
			
//			Button okBt,cancelBt;
//			TextView tView;
//			RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
//					RelativeLayout.LayoutParams.FILL_PARENT);
//			lp1.width = 700;
//			lp1.height = 280;
//			View v = View.inflate(mContext, R.layout.videodl_layout, null);
//			TextView tips = (TextView) v
//					.findViewById(R.id.textView1);	
//			TextView videotips = (TextView)v.findViewById(R.id.videotips);
//			videotips.setText(R.string.videotips);
//			String asktips = "\""+shareName+mContext.getString(R.string.sharevideotips);
//			ImageView headImageView = (ImageView)v.findViewById(R.id.head);
//			image_loader.displayImage(headurl, headImageView);
//			tips.setText(asktips);
//			final Dialog  videoDialog = new Dialog (mContext,R.style.CustomDialog);
//			videoDialog.setContentView(v,lp1);
//			videoDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//			videoDialog.show();		

//			okBt = (Button) v.findViewById(R.id.loadOkId);
//			okBt.setOnClickListener(new View.OnClickListener()
//			{
//				@Override
//				public void onClick(View v)
//				{
//					// TODO Auto-generated method stub
////					videoDialog.dismiss();
//					preparePlay(context, weiXinMsg);
//					//更新新消息条数
//					WeiUserDao userDao = new WeiUserDao(context);
//					userDao.reduceNews(weiXinMsg.getOpenid(),1);
//						
//				}
//			});
//			cancelBt = (Button) v.findViewById(R.id.loadCancelId);
//			cancelBt.setOnClickListener(new View.OnClickListener()
//			{
//				@Override
//				public void onClick(View v)
//				{
//					// TODO Auto-generated method stub
////					videoDialog.dismiss();
//					
//					
//				}
//			});
//			videoDialog.setOnCancelListener( new OnCancelListener() {
//				
//				@Override
//				public void onCancel(DialogInterface arg0) {
//					// TODO Auto-generated method stub
//					Log.i(TAG,"videoDialog  cancel");
//				
//				}
//			});
		}
	}
	
	public void showImageDialog(final Context context ,final WeiXinMsg weiXinMsg){
		Log.i(TAG,"imageopenid="+imageopenid+";weixinMsg.getOpenid="+weiXinMsg.getOpenid());
		 if(imageopenid==null){
			 imageopenid = weiXinMsg.getOpenid();
		 }
		String url = weiXinMsg.getUrl();
		list.add(url);
		if(imageDialog!=null && imageDialog.isShowing()){
			if(imageopenid.equals(weiXinMsg.getOpenid())){//同一个用户,只要修改数目
//				String asktips = mContext.getString(R.string.shareimagetips1)+list.size()
//						+mContext.getString(R.string.shareimagetips2);
//				imagetips.setText(asktips);
				 
				return;
			}
			else {//换了一个新用户，修改头像，名称和数目,这个需要测试验证
				if(userDao==null)
					userDao = new WeiUserDao(mContext);
				List<BinderUser> binderUserlist = userDao.find(weiXinMsg.getOpenid());
				String shareName = null;
				String headurl = null;
				if(binderUserlist!=null&&binderUserlist.size()>0){
					shareName = binderUserlist.get(0).getNickname();
					headurl = binderUserlist.get(0).getHeadimgurl();
				}else{
					Log.i(TAG,"找不到绑定的用户信息，不显示");
					return;
				}
				list.clear();
				String url_new = weiXinMsg.getUrl();
				list.add(url_new);
				imageopenid = weiXinMsg.getOpenid();
//				username.setText("\""+shareName);
//				String asktips = mContext.getString(R.string.shareimagetips);
//				imagetips.setText(asktips);
//				image_loader.displayImage(headurl, headImageView);
				
				Log.i(TAG,"找不到绑定的用户信息，不显示-----------------");
				return;
			}
		}

		if(userDao==null)
			userDao = new WeiUserDao(mContext);
		List<BinderUser> binderUserlist = userDao.find(weiXinMsg.getOpenid());
		String shareName = null;
		String headurl = null;
		if(binderUserlist!=null&&binderUserlist.size()>0){
			shareName = binderUserlist.get(0).getNickname();
			headurl = binderUserlist.get(0).getHeadimgurl();
		}else{
			Log.i(TAG,"找不到绑定的用户信息， 图片不显示");
			return;
		}
		Button okBt,cancelBt;
		TextView tView;
		
		
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);
		lp1.width = 700;
		lp1.height = 280;
//		View v = View.inflate(mContext, R.layout.videodl_layout, null);
//		username = (TextView)v.findViewById(R.id.username);
//		
//		
//		imagetips = (TextView) v.findViewById(R.id.textView1);		
//		username.setText("\""+shareName);
//		String asktips = mContext.getString(R.string.shareimagetips);
//		imagetips.setText(asktips);
//		 headImageView = (ImageView)v.findViewById(R.id.head);
//		image_loader.displayImage(headurl, headImageView);
//	
//		imageDialog = new Dialog (mContext,R.style.CustomDialog);
//		imageDialog.setContentView(v,lp1);
//		imageDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		imageDialog.show();		

//		okBt = (Button) v.findViewById(R.id.loadOkId);
//		okBt.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				// TODO Auto-generated method stub
//				imageDialog.dismiss();
//				//更新新消息条数
//				WeiUserDao userDao = new WeiUserDao(context);
//				Log.i(TAG,"000weixinMsg.getOpenid="+weiXinMsg.getOpenid());
//				userDao.reduceNews(imageopenid,list.size());
//				recoredDao.updateRead(weiXinMsg.getOpenid(), true);
//				imageopenid = null;
//				Log.i(TAG,"111weixinMsg.getOpenid="+weiXinMsg.getOpenid());
//				preparePlay(context, weiXinMsg);
//			}
//		});
//		cancelBt = (Button) v.findViewById(R.id.loadCancelId);
//		cancelBt.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				// TODO Auto-generated method stub
//				imageDialog.dismiss();
//				//取消也要清空列表
//				list.clear();
//				imageopenid = null;
//				//如果当前页面在账号详情页面，需要刷新新消息条数
//				/*if(newsShareUIHandler!=null){
//					newsShareUIHandler.sendEmptyMessage(CommandType.FRESH_NEWS);
//				}*/				
//			}
//		});
//		imageDialog.setOnCancelListener( new OnCancelListener() {
//			
//			@Override
//			public void onCancel(DialogInterface arg0) {
//				// TODO Auto-generated method stub
//				Log.i(TAG,"imageDialog  cancel");
//				//取消也要清空列表
//				list.clear();
//				imageopenid = null;
//			}
//		});
		
	}
	private void preparePlay(Context context ,WeiXinMsg weiXinMsg){
//		Log.i(TAG,"222weixinMsg.getOpenid="+weiXinMsg.getOpenid());
//		if(weiXinMsg.getMsgtype().equals("image")||weiXinMsg.getMsgtype().equals("video")){//直播
//			TTvCommonManager mTTvCommonManager = TTvCommonManager.getInstance(context);
//			Log.i(TAG, "CurrentInputSource="+mTTvCommonManager.getCurrentInputSource());
//			if(!mTTvCommonManager.getCurrentInputSource().equals(EnTCLInputSource.EN_TCL_STORAGE)
//					&&!mTTvCommonManager.getCurrentInputSource().equals(EnTCLInputSource.EN_TCL_NONE)){
//				//将参数保存
//				mContext = context;
//				mWeiXinMsg = weiXinMsg;
//				TTvManager.getInstance(context).setHandler(handler, TTvUtils.TV_HANDLER_INDEX_TV_SET_SOURCE);
//				mTTvCommonManager.setInputSource(EnTCLInputSource.EN_TCL_STORAGE, false);
//			
//			}else{				
//				starttoplay(context, weiXinMsg);
//			}
//		}else{
//			starttoplay(context, weiXinMsg);
//		}
	}
	
	private static WeiXinMsg mWeiXinMsg ;
	Handler handler = new Handler(){
	       @Override
	       public void handleMessage(Message msg) {
	           // TODO Auto-generated method stub
//	    	 Log.i(TAG, "msg="+msg.what);
//	    	 if (msg.what == EnTCLCallBackSetSourceMsg.EN_TCL_SET_SOURCE_END_SUCCEED.ordinal()) {
//
//	   			starttoplay(mContext, mWeiXinMsg);
//					
//	    	 }else if(msg.what == EnTCLCallBackSetSourceMsg.EN_TCL_SET_SOURCE_END_FAILED.ordinal()){
//	    		 TCLToast.makeText(mContext, "无法播放，切换信源失败", Toast.LENGTH_SHORT).show();
//	    	 }else if(msg.what == EnTCLCallBackSetSourceMsg.EN_TCL_SET_SOURCE_START.ordinal()){
//	    		 Log.i(TAG, "正在切换信源，稍后");
//	    		 return;
//	    	 }
//	    	 
//	    	//释放handler;
//				TTvManager.getInstance(mContext).releaseHandler(TTvUtils.TV_HANDLER_INDEX_TV_SET_SOURCE); 
	       }
	    };
	private void starttoplay(Context mContext,WeiXinMsg weiXinMsg){
		if(weiXinMsg.getMsgtype().equals("video")){
			String url = weiXinMsg.getUrl();
			if (url != null){
			
//				VideoPlayerHelp videoPlayerHelp = new VideoPlayerHelp(mContext);
//				videoPlayerHelp.startPlayVideo_dialog(weiXinMsg,false);

			}
		}else if(weiXinMsg.getMsgtype().equals("image")){
			//启动播放					
			Intent it = new Intent();
			it.putExtra("index", 0);
			it.putExtra("playstyle", PlayStyle.REALTIME_SHARE_PLAY);
			
			it.putExtra("openid", weiXinMsg.getOpenid());
			it.putStringArrayListExtra("playList", list);							
			it.setClass(mContext.getApplicationContext(),MainActivity.class);
			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);			
			it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mContext.startActivity(it);					
			Log.i(TAG,"startPlay--url="+list.get(0));
			//打开播放器，清空列表
			list.clear();
		}
	}
	/**
	 * 开始下载视频
	 */
	public void startDownLoad(final WeiXinMsg weiXinMsg,final Context context){	
		//下载之前要判断是否有SD卡，如果有优先存储到sd卡，如果没有判断flash中已有视频是否大于400M.		
			String downPath = UIUtils.getDownLoadPath();	
		    Log.d(TAG, "当前下载路径="+downPath);	
		    try {
				if(downPath.equals(WeiConstant.DOWN_LOAD_SDCARD_PATH)){
					Log.i(TAG,"UIUtils.getSDFreeSize()="+UIUtils.getSDFreeSize());
					if (UIUtils.getSDFreeSize()<20){
						Log.i(TAG,"有SD卡，但是SD卡剩余空间少于20M，将视频存储到flash中");
//						TCLToast.makeText(context, context.getString(R.string.nospace), Toast.LENGTH_LONG).show();
						downPath = WeiConstant.DOWN_LOAD_FLASH_PATH;
						if(UIUtils.getVideoFolderSize()>400){
							//recoredDao = new WeiRecordDao(context);
							String filenamedel = recoredDao.findOldRecord();
							CommonsFun.delSrcFile(filenamedel);
						}
					}	    	
				}else if(downPath.equals(WeiConstant.DOWN_LOAD_FLASH_PATH)){
				    //本地存储的视频文件大于400M。删除老文件和数据库		    
					 Log.i(TAG,"UIUtils.getVideoFolderSize()="+UIUtils.getVideoFolderSize());
					if(UIUtils.getVideoFolderSize()>400){
						//recoredDao = new WeiRecordDao(context);
						String filenamedel = recoredDao.findOldRecord();
						CommonsFun.delSrcFile(filenamedel);
					}
					
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		final String downPath_final = downPath;			
//		thread = new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				//如果是微信有效连接下载播放
//				HttpDownloader httpDownloader = new HttpDownloader();
//				try {
//					Map<String,InputStream> map = httpDownloader.downloadMedia(weiXinMsg.getUrl(),downPath_final);
//					Set<String> key = map.keySet();
//					String fileName = null;
//			        for (Iterator it = key.iterator(); it.hasNext();) {
//			            String s = (String) it.next();
//			            fileName=s;
//			            Log.d(TAG, "当前key="+fileName);
//			        }
//			        int length = map.get(fileName).available();			       
//			             
//					String savepath = downPath_final+"/"+fileName;//fileName
//					Log.d(TAG, "当前播放视频路径是="+"fileName="+fileName+"length="+length);
//					CommonsFun.chmodfile(savepath);
//										
//					//刷新视频文件存储路径到数据库
//					weiXinMsg.setFileName(savepath);					
//					if (weiXinMsg.getUrl() != null ){						
//						Message msg = new Message();
//						msg.what = DOWN_OVER;
//						msg.obj = weiXinMsg;						
//						mHandler.sendMessage(msg); // 向Handler发送消息,更新UI
//						
//					}
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					Log.i(TAG,"startDownLoad--Exception-e="+e);
//					e.printStackTrace();
//				}
//			}
//		});
		thread.start();
		
	}
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what ==DOWN_OVER ){
				
				//更新新消息条数
				WeiXinMsg weiXinMsg = (WeiXinMsg) msg.obj;
				recoredDao.save(weiXinMsg);
				//记录写入后，再下载图片，这样才能修改记录。。。
				image_loader.loadImage(weiXinMsg.getThumbmediaid(), options, null);////下载视频缩略图	
				mContext.getContentResolver().notifyChange(MyUsers.CONTENT_RECORD, null);
				WeiUserDao userDao = new WeiUserDao(mContext);
				userDao.addNews(weiXinMsg.getOpenid());
				//如果当前页面在主页，需要刷新新消息条数
				if(newsHandler!=null){
					newsHandler.sendEmptyMessage(CommandType.FRESH_NEWS);
				}
				recoredDao.updateFileName(weiXinMsg.getUrl(), weiXinMsg.getFileName());			
				
				//符合下面三种情况才弹出提示框  1、设置是消息开启2、不是离线消息3、系统没有在搜台
				if(WeiConstant.SET_SHARE_STYLE && weiXinMsg.getofflinemsg().equals("false")
						&& !SystemRelfect.getProperties("sys.scan.state", "off").equals("on")){	
					startPlay(mContext,weiXinMsg,"video");
				}
				
			}
		}
		
	};
	private void showBookingDialog(final WeiXinMsg weiXinMsg){
//		String openid = weiXinMsg.getOpenid();
//		if(userDao==null)
//			userDao = new WeiUserDao(mContext);
//		List<BinderUser> binderUserlist = userDao.find(openid);
//		String shareName = null;
//		String headurl = null;
//		if(binderUserlist!=null&&binderUserlist.size()>0){
//			shareName = binderUserlist.get(0).getNickname();
//			headurl = binderUserlist.get(0).getHeadimgurl();
//		}else{
//			Log.i(TAG,"找不到绑定的用户信息，预约不显示");
//			return;
//		}		
//		Button okBt,cancelBt;
//		TextView tView;
//		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
//				RelativeLayout.LayoutParams.FILL_PARENT);
//		lp1.width = 700;
//		lp1.height = 280;
//		View v = View.inflate(mContext, R.layout.bookdl_layout, null);
//		TextView tips = (TextView) v
//				.findViewById(R.id.textView1);			
//		String asktips = "\""+shareName +mContext.getString(R.string.booktips)+weiXinMsg.getchannelname()+"  "+weiXinMsg.getContent()
//				+mContext.getString(R.string.booktips2);
//		ImageView headImageView = (ImageView)v.findViewById(R.id.head);
//		image_loader.displayImage(headurl, headImageView);
//		tips.setText(asktips);
//		final Dialog  b = new Dialog (mContext,R.style.CustomDialog);
//		b.setContentView(v,lp1);
//		b.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		b.show();		
//
//		okBt = (Button) v.findViewById(R.id.loadOkId);
//		okBt.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				// TODO Auto-generated method stub
//				b.dismiss();
//				if(!WeiConstant.WechatConfigure.CurConfigure.equals(WeiConstant.WechatConfigure.SimpleVer)){
//					Log.i(TAG,"channelname="+weiXinMsg.getchannelname());
//					WeiConstant.mTuneChannel.tunechannel_tv(weiXinMsg.getchannelname());
//				}
//										
//			}
//		});
	
		
	}
	
}
