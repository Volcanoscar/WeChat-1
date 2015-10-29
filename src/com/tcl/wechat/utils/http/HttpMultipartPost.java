package com.tcl.wechat.utils.http;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.controller.listener.UploadListener;
import com.tcl.wechat.utils.http.HttpMultipartEntity.ProgressListener;

/**
 * 文件上传类
 * @author rex.lei
 *
 */
public class HttpMultipartPost extends AsyncTask<String, Integer, String>{
	
	private static final String TAG = HttpMultipartPost.class.getSimpleName();
	
	/**
	 * 上传文件名称（绝对路径）
	 */
	private String mUploadFileName;
	
	/**
	 * 文件上传进度监听器
	 */
	private UploadListener mUploadListener;
	
	private long mTotalSize = 0;;
	
	public HttpMultipartPost(String fileName, UploadListener listener) {
		super();
		this.mUploadFileName = fileName;
		this.mUploadListener = listener;
	}
	
	@Override
	protected String doInBackground(String... params) {
		String serverUrl = params[0];
		Log.d(TAG, "serverUrl:" + serverUrl);
		if (TextUtils.isEmpty(mUploadFileName) || TextUtils.isEmpty(serverUrl)){
			return null;
		}
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(serverUrl);
		HttpMultipartEntity entity = new HttpMultipartEntity();
		entity.setProgressListener(new ProgressListener() {
			
			@Override
			public void onProgressUpdate(int progress) {
				// TODO Auto-generated method stub
				publishProgress((int) ((progress / (float) mTotalSize) * 100));
			}
		});
		
		entity.addPart("uploadFile", new FileBody(new File(mUploadFileName)));
		
		mTotalSize = entity.getContentLength();
		
		httpPost.setEntity(entity);
		try {
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			return EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Log.d(TAG, "result:" + result);
		if (mUploadListener != null){
			mUploadListener.onResult(result);
		}
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		if (mUploadListener != null){
			mUploadListener.onProgressUpdate(progress[0]);
		}
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected void onCancelled(String result) {
		// TODO Auto-generated method stub
		super.onCancelled(result);
	}
	
}
