package com.tcl.wechat.ui.activity;

import java.io.File;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.common.IConstant.DownloadState;
import com.tcl.wechat.database.WeiRecordDao;
import com.tcl.wechat.model.WeiXinMessage;
import com.tcl.wechat.model.file.DownloadFile;
import com.tcl.wechat.utils.WeixinToast;
import com.tencent.wechat.AirKissHelper;

/**
 * 文件预览界面
 * @author rex.lei
 * 
 * 分为三类文件：
 * 	1）图片，直接显示
 * 	2）音乐，直接播放
 * 	3）文件，调用第三方应用打开
 */
public class FilePreviewActivity extends BaseActivity{
	
	private static final String TAG = FilePreviewActivity.class.getSimpleName();
	
	private static final int MSG_UPDATE_PROGRESS = 0x01;
	private static final int MSG_DOWNLOAD_CONPLETE = 0x02;
	private static final int MSG_DOWNLOAD_FAILED = 0x03;
	
	
	private ImageView mFileIconImg;
	private TextView mFileNameTv;
	private Button mFileOpenBtn;
	private TextView mFileHintTv;
	private ProgressBar mDownLoadPBar;
	
	private WeiXinMessage mWeiXinMessage;
	
	private DownloadFile mDownloadFile;
	
	private String mFileName ;
	
	private boolean bFileDownloadProgress = false;
	
	private HashMap<String, String> mMIMETypeMap = new HashMap<String, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_preview);
		
		initData();
		initView();
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private void initData() {
		// TODO Auto-generated method stub
		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			return;
		}
		mWeiXinMessage = bundle.getParcelable("WeiXinMsgRecorder");
		if (mWeiXinMessage == null) {
			return;
		}
		mFileName = WeiRecordDao.getInstance().getFileName(mWeiXinMessage.getMsgid());
		initMIMEType();
	}

	private void initView() {
		
		mFileIconImg = (ImageView) findViewById(R.id.img_file_icon);
		mFileNameTv = (TextView) findViewById(R.id.tv_file_name);
		mFileOpenBtn = (Button) findViewById(R.id.btn_file_open);
		mFileHintTv = (TextView) findViewById(R.id.tv_file_hint);
		mDownLoadPBar = (ProgressBar) findViewById(R.id.pbar_file_download);
		
		if (mWeiXinMessage == null){
			return ;
		}
		Log.i(TAG, "mWeiXinMessage:" + mWeiXinMessage.toString());
		
		String fileType = mWeiXinMessage.getLabel();
		if ("mp3".equals(fileType)){
			mFileIconImg.setImageResource(R.drawable.file_mp3_icon_large);
		} else if ("wma".equals(fileType)){
			mFileIconImg.setImageResource(R.drawable.file_wma_icon_large);
		} else if ("png".equals(fileType)){
			mFileIconImg.setImageResource(R.drawable.file_png_icon_large);
		} else if ("jpg".equals(fileType)){
			mFileIconImg.setImageResource(R.drawable.file_jpg_icon_large);
		} else if ("jepg".equals(fileType)){
			mFileIconImg.setImageResource(R.drawable.file_jpg_icon_large);
		} else if ("doc".equals(fileType) 
				|| "docx".equals(fileType)){
			mFileIconImg.setImageResource(R.drawable.file_doc_icon_large);
		} else if ("ppt".equals(fileType)
				|| "pptx".equals(fileType)){
			mFileIconImg.setImageResource(R.drawable.file_ppt_icon_large);
		} else if ("xls".equals(fileType)
				|| "xlsx".equals(fileType)){
			mFileIconImg.setImageResource(R.drawable.file_xls_icon_large);
		} else if ("pdf".equals(fileType)){
			mFileIconImg.setImageResource(R.drawable.file_pdf_icon_large);
		} else if ("txt".equals(fileType)){
			mFileIconImg.setImageResource(R.drawable.file_txt_icon_large);
		} else {
			mFileIconImg.setImageResource(R.drawable.file_def_icon_large);
		}
		mFileNameTv.setText(mWeiXinMessage.getContent());
		
		//文件下载失败
		if (DownloadState.STATE_DOWNLOAD_FAILED.equals(mWeiXinMessage.getStatus())){
			mHandler.sendEmptyMessage(MSG_DOWNLOAD_FAILED);
			
		}
		//开始未下载
		else if (!DownloadState.STATE_DOWNLOAD_CONPLETED.equals(mWeiXinMessage.getStatus())){
				
			mDownLoadPBar.setVisibility(View.VISIBLE);
			mFileOpenBtn.setVisibility(View.GONE);
			mFileHintTv.setVisibility(View.GONE);
			
			//启动线程检测文件下载状态
			bFileDownloadProgress = true;
			new Thread(mFileMonitorRunnable).start();
		} 
		
		
	}
	
	private Runnable mFileMonitorRunnable = new Runnable() {
		
		@Override
		public void run() {
			try {
				mDownloadFile = AirKissHelper.getInstance().getDownFile(mWeiXinMessage.getMsgid());
				Log.d(TAG, "mDownloadFile:" + mDownloadFile);
				if (mDownloadFile == null){
					bFileDownloadProgress = false;
					mHandler.sendEmptyMessage(MSG_DOWNLOAD_CONPLETE);
					return ;
				}
				int delayTime = 0;
				int curProgress = -1;
				while (bFileDownloadProgress) {
					int progress = (int) (mDownloadFile.getDownLoadSize() * 100.0
							/ mDownloadFile.getTotalSize());
					
					Message message = Message.obtain();
					message.what = MSG_UPDATE_PROGRESS;
					message.arg1 = progress;
					message.obj = mDownloadFile.getSaveFile().getAbsolutePath();
					Log.d(TAG, "progress:" + progress + ",curProgress:" + curProgress + ",delayTime:" + delayTime);
					mHandler.sendMessage(message);
					
					delayTime++;
					if (delayTime == 30){
						if (curProgress == progress){
							Log.d(TAG, "progress:" + progress);
							mHandler.sendEmptyMessage(MSG_DOWNLOAD_FAILED);
							return ;
						}
						curProgress = progress;
						delayTime = 0;
					}
					
					Thread.sleep(200);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case MSG_UPDATE_PROGRESS:
				int progress = msg.arg1;
				mDownLoadPBar.setProgress(progress);
				if (progress >= 100){
					bFileDownloadProgress = false;
					mHandler.removeMessages(MSG_UPDATE_PROGRESS);
					
					mFileName = (String) msg.obj;
					mWeiXinMessage.setFileName(mFileName);
					
					mDownLoadPBar.setVisibility(View.GONE);
					mFileOpenBtn.setVisibility(View.VISIBLE);
					mFileHintTv.setVisibility(View.VISIBLE);
				}
				break;
			case MSG_DOWNLOAD_CONPLETE:
				mFileName = WeiRecordDao.getInstance().getFileName(mWeiXinMessage.getMsgid());
				mDownLoadPBar.setVisibility(View.GONE);
				mFileOpenBtn.setVisibility(View.VISIBLE);
				mFileHintTv.setVisibility(View.VISIBLE);
				break;
				
			case MSG_DOWNLOAD_FAILED:
				bFileDownloadProgress = false;
				removeMessages(MSG_DOWNLOAD_CONPLETE);
				removeMessages(MSG_DOWNLOAD_FAILED);
				mDownLoadPBar.setVisibility(View.GONE);
				mFileHintTv.setVisibility(View.VISIBLE);
				mFileOpenBtn.setVisibility(View.VISIBLE);
				mFileHintTv.setText(R.string.file_error);
				//文件下载失败。
				WeiRecordDao.getInstance().updateMessageState(mWeiXinMessage.getMsgid(), 
						DownloadState.STATE_DOWNLOAD_FAILED);
				break;

			default:
				break;
			}
		}
	};
	
	public void openFileClick(View view){
		try {
			Log.d(TAG, "mFileName:" + mFileName);
			File file =  new File(mFileName);
			if (file == null || !file.exists()){
				WeixinToast.makeText(this, R.string.file_open_failed).show();
				return ;
			}
			
			Intent intent = new Intent();
			if ("png".equals(mWeiXinMessage.getLabel())
					|| "jpg".equals(mWeiXinMessage.getLabel())
					|| "jpeg".equals(mWeiXinMessage.getLabel())
					|| "bmp".equals(mWeiXinMessage.getLabel())){
				if (!TextUtils.isEmpty(mFileName)){
					mWeiXinMessage.setFileName(mFileName);
					intent = new Intent(FilePreviewActivity.this, ShowImageActivity.class);
					intent.putExtra("WeiXinMsgRecorder", mWeiXinMessage);
					startActivity(intent);
				}
			} else {
				intent.setAction(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				 
				String type = getMIMEType(); 
				intent.setDataAndType(Uri.fromFile(file), type); 
				startActivity(intent);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}
	
	@SuppressLint("DefaultLocale")
	private String getMIMEType() {
		String type = "*/*";
		String fileName = mWeiXinMessage.getFileName();
		Log.d(TAG, "fileName:" + fileName);
		if (fileName == null) {
			return type;
		}
		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String fileSuffix = fileName.substring(dotIndex, fileName.length())
				.toLowerCase();
		Log.i(TAG, "fileSuffix:" + fileSuffix);
		if (TextUtils.isEmpty(fileSuffix)) {
			return type;
		}
		return mMIMETypeMap.get(fileSuffix);
	}


	private void initMIMEType(){
		mMIMETypeMap.put(".3gp", "video/3gpp");
		mMIMETypeMap.put(".apk", "application/vnd.android.package-archive");
		mMIMETypeMap.put(".asf", "video/x-ms-asf");
		mMIMETypeMap.put(".avi", "video/x-msvideo");
		mMIMETypeMap.put(".bin", "application/octet-stream");
		mMIMETypeMap.put(".bmp", "image/bmp");
		mMIMETypeMap.put(".c", "text/plain");
		mMIMETypeMap.put(".class", "application/octet-stream");
		mMIMETypeMap.put(".conf", "text/plain");
		mMIMETypeMap.put(".cpp", "text/plain");
		mMIMETypeMap.put(".doc", "application/msword");
		mMIMETypeMap.put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		mMIMETypeMap.put(".xls", "application/vnd.ms-excel");
		mMIMETypeMap.put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		mMIMETypeMap.put(".exe", "application/octet-stream");
		mMIMETypeMap.put(".gif", "image/gif");
		mMIMETypeMap.put(".gtar", "application/x-gtar");
		mMIMETypeMap.put(".gz", "application/x-gzip");
		mMIMETypeMap.put(".h", "text/plain");
		mMIMETypeMap.put(".htm", "text/html");
		mMIMETypeMap.put(".html", "text/html");
		mMIMETypeMap.put(".jar", "application/java-archive");
		mMIMETypeMap.put(".java", "text/plain");
		mMIMETypeMap.put(".jpeg", "image/jpeg");
		mMIMETypeMap.put(".jpg", "image/jpeg");
		mMIMETypeMap.put(".js", "application/x-javascript");
		mMIMETypeMap.put(".log", "text/plain");
		mMIMETypeMap.put(".m3u", "audio/x-mpegurl");
		mMIMETypeMap.put(".m4a", "audio/mp4a-latm");
		mMIMETypeMap.put(".m4b", "audio/mp4a-latm");
		mMIMETypeMap.put(".m4p", "audio/mp4a-latm");
		mMIMETypeMap.put(".m4u", "video/vnd.mpegurl");
		mMIMETypeMap.put(".m4v", "video/x-m4v");
		mMIMETypeMap.put(".mov", "video/quicktime");
		mMIMETypeMap.put(".mp2", "audio/x-mpeg");
		mMIMETypeMap.put(".mp3", "audio/x-mpeg");
		mMIMETypeMap.put(".mp4", "video/mp4");
		mMIMETypeMap.put(".mpc", "application/vnd.mpohun.certificate");
		mMIMETypeMap.put(".mpe", "video/mpeg");
		mMIMETypeMap.put(".mpeg", "video/mpeg");
		mMIMETypeMap.put(".mpg", "video/mpeg");
		mMIMETypeMap.put(".mpg4", "video/mp4");
		mMIMETypeMap.put(".mpga", "audio/mpeg");
		mMIMETypeMap.put(".msg", "application/vnd.ms-outlook");
		mMIMETypeMap.put(".ogg", "audio/ogg");
		mMIMETypeMap.put(".pdf", "application/pdf");
		mMIMETypeMap.put(".png", "image/png");
		mMIMETypeMap.put(".pps", "application/vnd.ms-powerpoint");
		mMIMETypeMap.put(".ppt", "application/vnd.ms-powerpoint");
		mMIMETypeMap.put(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
		mMIMETypeMap.put(".prop", "text/plain");
		mMIMETypeMap.put(".rc", "text/plain");
		mMIMETypeMap.put(".rmvb", "audio/x-pn-realaudio");
		mMIMETypeMap.put(".rtf", "application/rtf");
		mMIMETypeMap.put(".sh", "text/plain");
		mMIMETypeMap.put(".tar", "application/x-tar");
		mMIMETypeMap.put(".tgz", "application/x-compressed");
		mMIMETypeMap.put(".txt", "text/plain");
		mMIMETypeMap.put(".wav", "audio/x-wav");
		mMIMETypeMap.put(".wma", "audio/x-ms-wma");
		mMIMETypeMap.put(".wmv", "audio/x-ms-wmv");
		mMIMETypeMap.put(".wps", "application/vnd.ms-works");
		mMIMETypeMap.put(".xml", "text/plain");
		mMIMETypeMap.put(".z", "application/x-compress");
		mMIMETypeMap.put(".zip", "application/x-zip-compressed");
		mMIMETypeMap.put("", "*/*");
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
		bFileDownloadProgress = false;
	}
}
