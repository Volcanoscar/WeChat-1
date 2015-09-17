package com.tcl.wechat.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tcl.wechat.WeChatApplication;
import com.tcl.wechat.common.WeiConstant.CommandAction;
import com.tcl.wechat.common.WeiConstant.SystemShared;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.data.DataFileTools;
import com.tcl.wechat.utils.MD5Util;
import com.tcl.wechat.utils.SystemShare.SharedEditer;

/**
 * 业务数据下载代理类  ----该部分需要进一步优化
 * 	1)下载数据：下载过程中，失败，则实现3次下载
 * 	2）用户头像数据MD5加密处理
 * 	3）下载用户之前检测用户数据是否更新
 * 	   如果用户更新了头像，则应该删除之前的用户头像数据
 *  4）每次用户登录，检测用户信息更新，如果更新，则存储数据库
 * @author rex.lei
 *
 */
public class DownLoadProxy {
	
	private static final String TAG = DownLoadProxy.class.getSimpleName();
	
	private static final int TIME_OUT = 8000;
	
	private static final int BUFFER_SIZE = 1024;
	
	/**
	 * 下载次数
	 */
	private int DOWN_LOAD_CNT = 3;
	
 	private Context mContext;
	
	private SharedEditer mEditer;
	
	private static class DownLoadProxyInstnce{
		private static final DownLoadProxy Instance = new DownLoadProxy();
	}

	private DownLoadProxy() {
		super();
		mContext = WeChatApplication.gContext;
		mEditer = new SharedEditer();
	}
	
	public static DownLoadProxy getInstanc(){
		return DownLoadProxyInstnce.Instance;
	}
	
	
	/**
	 * 下载用户头像
	 * @return
	 */
	public void startDownloadUserHeadIcon(final ArrayList<BindUser> allUsers){
		
		if (allUsers == null || allUsers.isEmpty()){
			Log.w(TAG, "UserList is isEmpty, NO User need to download!");
			return ;
		}
		Log.d(TAG, "start to download UserHeadIcon!");
		//启动服务下载用户头像
		 new Thread(new Runnable() {
			
			@Override
			public void run() {
				String cachePath = DataFileTools.getInstance().getCachePath();
				Log.i(TAG, "cachePath:" + cachePath);
				
				if (cachePath == null){
					return ;
				}
				for (int i = 0; i < allUsers.size(); i++) {
					String headImageUrl = allUsers.get(i).getHeadImageUrl();
					Log.i(TAG, "headImageUrl:" + headImageUrl);
					if (headImageUrl != null){
						//TODO 在此要对下载是否成功进行判断
						downloadAndSavaImage(headImageUrl, cachePath);
					}
				}
				
				//如果已经进入主界面，则通知更新图像
				if (mEditer.getBoolean(SystemShared.KEY_FLAG_ENTER, false)){
					Intent intent = new Intent(CommandAction.ACTION_UPDATE_BINDUSER);
					mContext.sendBroadcast(intent);
				}
			}
		}).start();
	}
	
	/**
	 * 下载二维码
	 * @return
	 */
	public boolean startDownloadQr(final String qrUrl){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String cachePath = DataFileTools.getInstance().getCachePath();
				
				if (cachePath == null){
					return ;
				}
				downloadAndSavaImage(qrUrl, cachePath);
			}
		}).start();
		return false;
	}
	
	/**
	 * 下载并保存数据
	 * @param url 下载地址Url
	 * @param savePath 保存数据路径
	 * @return
	 */
	private boolean downloadAndSavaImage(String url, String savePath){
		
		if (url == null){
			return false;
		}
		if (savePath == null || savePath.equals("")){
			return false;
		}
		
		String fileName = MD5Util.hashKeyForDisk(url);
		File saveFilePath = new File(savePath);
		if (!saveFilePath.exists()){
			saveFilePath.mkdirs();
		}
		
		//三次下载
		for (int i = 0; i < DOWN_LOAD_CNT; i++) {
			if (download(url, fileName, saveFilePath)){
				return true;
			}
			Log.w(TAG, "Download failed, try angin! Cnt:" + (i + 1));
		}
		return false;
	}
	
	/**
	 * 下载
	 * @param imgUrl 下载地址
	 * @param fileName MD5加密的文件名称
	 * @param savePath 保存路径 ../WeChat/cache/
	 * @return
	 */
	private boolean download(String imgUrl, String fileName, File savePath){
		
		if (fileName == null){
			return false;
		}
		
		if (savePath == null || savePath.equals("")){
			return false;
		}
		
		String imageFilePath = savePath.getPath() + File.separator + fileName;
		File tempFile = new File(imageFilePath + ".bak");
		if (tempFile != null && tempFile.exists()){
			tempFile.delete();
		}
		//从网络上下载资源
		URL url = null;
		InputStream inStream = null;
		FileOutputStream fOutputStream = null;
		HttpURLConnection httpConn = null;
		byte[] buffer = null;
		long imgFileSize = 0;
        long downFileSize = 0;
		
		try {
			url = new URL(imgUrl);
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setDoInput(true);
			httpConn.setConnectTimeout(TIME_OUT);
			httpConn.setReadTimeout(TIME_OUT);
			httpConn.setRequestMethod("GET");
			downFileSize = httpConn.getContentLength();
			
			int statue = httpConn.getResponseCode();
			Log.i(TAG, "statue:" + statue);
			
			if (statue == HttpURLConnection.HTTP_OK){
				buffer = new byte[BUFFER_SIZE];
				fOutputStream = new FileOutputStream(tempFile);
				inStream = httpConn.getInputStream();
				
				int size = -1;
				while ((size = inStream.read(buffer)) != -1){
					fOutputStream.write(buffer, 0, size);
				}
				fOutputStream.flush();
			}
 		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (inStream != null){
					inStream.close();
				}
				if (fOutputStream != null){
					fOutputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// 检测海报是否下载正常
		imgFileSize = tempFile.length();
		if (imgFileSize != downFileSize){
			tempFile.delete();
		} else {
			//保存资源文件
			File saveFile = new File(savePath, fileName);
			tempFile.renameTo(saveFile);
			return true;
		}
		return false;
	}
	

}
