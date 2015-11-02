package com.tcl.wechat.view;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.http.RequestManager;
import com.android.http.RequestManager.RequestListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.action.recorder.Recorder;
import com.tcl.wechat.common.Config;
import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.controller.WeiXinMsgManager;
import com.tcl.wechat.controller.listener.UploadListener;
import com.tcl.wechat.database.WeiMsgRecordDao;
import com.tcl.wechat.model.WeiXinMsgRecorder;
import com.tcl.wechat.model.WeixinMsgInfo;
import com.tcl.wechat.utils.http.HttpMultipartPost;
import com.tcl.wechat.xmpp.ReplyResult;
import com.tcl.wechat.xmpp.XmppEvent;
import com.tcl.wechat.xmpp.XmppEventListener;

/**
 * 聊天界面图片更新显示控件
 * @author rex.lei
 *
 */
public class ChatMsgImageView extends LinearLayout implements IConstant{
	
	private static final String TAG = ChatMsgImageView.class.getSimpleName();
	
	private ImageView mSrcImageView;
	private ProgressView mProgressView;
	
	private boolean bUploadImageFlag = false; //是否正在上传图片  是：显示进度，否：不显示
	
	private WeiXinMsgRecorder mRecorder;

	public ChatMsgImageView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public ChatMsgImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public ChatMsgImageView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	private void initView(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.layout_image, this);
		mSrcImageView = (ImageView) view.findViewById(R.id.img_updateimage);
		mProgressView = (ProgressView) view.findViewById(R.id.img_progressview);
	}
	
	/**
	 * 设置图片
	 * @param recorder
	 */
	public void setBitmapImage(WeiXinMsgRecorder recorder){
		if (recorder == null){
			return ;
		}
		
		mRecorder = recorder;
		
		if (bUploadImageFlag) {
			return ;
		}
		mRecorder.setUrl(WeiMsgRecordDao.getInstance()
				.getRecorderUrl(recorder.getMsgid()));
		if (TextUtils.isEmpty(mRecorder.getUrl())){//Url为空，上传图片
			bUploadImageFlag = true;
			loadImage(recorder.getFileName());
			uploadImage();
		} else { //URL不为空，加载图片
			bUploadImageFlag = false;
			loadImage();
		}
	}
	
	/**
	 * 根据计算的inSampleSize，得到压缩后图片
	 * 
	 * @param pathName
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private Bitmap decodeSampledBitmapFromResource(String pathName,
			int reqWidth, int reqHeight){
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 计算inSampleSize，用于压缩图片
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight){
		// 源图片的宽度
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;

		if (width > reqWidth && height > reqHeight){
			// 计算出实际宽度和目标宽度的比率
			int widthRatio = Math.round((float) width / (float) reqWidth);
			int heightRatio = Math.round((float) height / (float) reqHeight);
			inSampleSize = Math.max(widthRatio, heightRatio);
		}
		return inSampleSize;
	}
	
	private void loadImage(String path){
		Bitmap bm = decodeSampledBitmapFromResource(path, 300, 300 );
		mSrcImageView.setImageBitmap(bm);
	}

	/**
	 * 加载图片
	 */
	private void loadImage(){
		
		String url = mRecorder.getUrl();//WeiMsgRecordDao.getInstance().getRecorderUrl(recorder.getMsgid());
		if (TextUtils.isEmpty(url)){
			setErrorImage();
			return ;
		}
		
		Log.i(TAG, "loadImage url:" + url);
		WeApplication.getImageLoader().get(url, new ImageListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				setErrorImage();
			}
			
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				Bitmap bitmap = response.getBitmap();
				
				Log.i(TAG, "bitmap:" + bitmap);
				if (bitmap != null){
					mSrcImageView.setImageBitmap(bitmap);
				} else {
					setErrorImage();
				}
			}
		}, 400, 400);
	}
	
	/**
	 * 加载错误显示图片
	 */
	private void setErrorImage(){
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), 
				R.drawable.pictures_no);
		mSrcImageView.setImageBitmap(bitmap);
	}
	
	/**
	 * 上传图片
	 * @param msgInfo 图片消息信息
	 */
	private void uploadImage(){
		
		//请求接入令牌
		RequestManager.getInstance().get(Config.URL_ACCESS_TOKEN, new RequestListener() {
			
			@Override
			public void onSuccess(String response, Map<String, String> arg1, String arg2,
					int arg3) {
					// TODO Auto-generated method stub
					String accesstoken = response;
					Log.i(TAG, "accesstoken:" + accesstoken);
					if (!TextUtils.isEmpty(accesstoken)){
						upload(accesstoken, mRecorder);
					}
				}
				
				@Override
				public void onRequest() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onError(String arg0, String arg1, int arg2) {
					// TODO Auto-generated method stub
					
				}
			}, 0);
		}
	
		/**
		 * 上传图片至服务器
		 * @param accesstoken 接入令牌
		 */
		private void upload(String accesstoken, final WeiXinMsgRecorder msgRecorder){
			
			String fileName = msgRecorder.getFileName();
			if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(accesstoken)) {
				return ;
			}
			mProgressView.setVisibility(View.VISIBLE);
			
			StringBuffer buffer = new StringBuffer();
			buffer.append(URL_TICKET).append("access_token=").append(accesstoken)
								.append("&type=").append(ChatMsgType.IMAGE);
			Log.i(TAG, "Url:" + buffer.toString());
			
			new HttpMultipartPost(fileName, new UploadListener() {
				
				@Override
				public void onResult(String result) {
					// TODO Auto-generated method stub
					
					try {
						if (!TextUtils.isEmpty(result)){
							setTag("");//清除标签
							JSONObject object = new JSONObject(result);
							String mediaid = (String)object.get("media_id");
							
							//生成对应的消息类型
							Recorder recorder = new Recorder();
							recorder.setFileName(recorder.getFileName());
							
							WeixinMsgInfo weixinMsgInfo = new WeixinMsgInfo();
							weixinMsgInfo.setFromusername(msgRecorder.getOpenid());
							weixinMsgInfo.setTousername(msgRecorder.getToOpenid());
							weixinMsgInfo.setMessageid(msgRecorder.getMsgid());
							weixinMsgInfo.setMsgtype(ChatMsgType.IMAGE);
							weixinMsgInfo.setRecorder(recorder);
							weixinMsgInfo.setMediaid(mediaid);
							WeiXinMsgManager.getInstance().replyMessage(listener, weixinMsgInfo);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				@Override
				public void onProgressUpdate(int progress) {
					// TODO Auto-generated method stub
					Log.i(TAG, "progress:" + progress);
					mProgressView.setProgress(progress);
					if (progress == 100) {
						mProgressView.setVisibility(View.GONE);	
					}
				}
				
				@Override
				public void onError(int errorCode) {
					// TODO Auto-generated method stub
					Log.w(TAG, "Upload image failed!!, errorCode:" + errorCode);
					setErrorImage();
				}
			}).executeOnExecutor(WeApplication.getExecutorPool(), buffer.toString());
		}
		
		private XmppEventListener listener = new XmppEventListener() {
			
			@Override
			public void onEvent(XmppEvent event) {
				// TODO Auto-generated method stub

				Log.i(TAG, "Event type:" + event.getType());
				switch (event.getType()) {
				case EventType.TYPE_SEND_WEIXINMSG:
					int reason = event.getReason();
					Log.i(TAG, "reason:" + reason);
					if (reason == EventReason.REASON_COMMON_SUCCESS){
						ReplyResult result = (ReplyResult) event.getEventData();
						String msgid = result.getMsgid();
						String url = result.getResult();
						if (!TextUtils.isEmpty(url)){
							Log.i(TAG, "Result url:" + url);
							//在此要判断是否添加成功
							WeiMsgRecordDao.getInstance().updateRecorderUrl(msgid, url);
						}
						mRecorder.setUrl(url);
						bUploadImageFlag = false;
					}
					break;
					
				case EventType.TYPE_UNBIND_EVENT:
					break;
					
				default:
					break;
				}
			}
		};  
 }  

