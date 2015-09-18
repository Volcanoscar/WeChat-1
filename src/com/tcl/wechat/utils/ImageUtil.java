package com.tcl.wechat.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

/**
 * 图像处理工具类
 * @author rex.lei
 *
 */
public class ImageUtil {

	private static final String TAG = ImageUtil.class.getSimpleName();
	
	private static final int TIME_OUT = 8000;
	private static final int BUFFER_SIZE = 1024;
	
	private ImageUtil() {
		super();
	}
	
	private static class ImageUtilInstance{
		private static final ImageUtil mInstance = new ImageUtil();
	}
	/**
	 * 获取接口实例
	 * @return
	 */
	public static ImageUtil getInstance(){
		return ImageUtilInstance.mInstance;
	}
	
	/**
	 * 获取下载文件的InputStream对象
	 * @param url
	 * @return
	 */
	public InputStream getInputStream(String pathUrl){
		InputStream inputStream = null;
		HttpURLConnection connection = null;
		URL url = null;
		try {
			url = new URL(pathUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setConnectTimeout(TIME_OUT);
			connection.setReadTimeout(TIME_OUT);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept-Encoding", "identity");
			inputStream = connection.getInputStream();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null){
					inputStream.close();
				}
				if (connection != null){
					connection.disconnect();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return inputStream;
	}
	
	/**
	 * 下载文本信息
	 * @param urlStr下载地址
	 * @param flg 是否转化成国标GBK
	 * @return String字符串
	 */
	public String downloadFile(String urlStr, boolean flg){
		StringBuffer sBuffer = new StringBuffer();
		BufferedReader reader = null;
		
		try {
			if (flg){
				reader = new BufferedReader(new InputStreamReader(
						getInputStream(urlStr), "UTF-8"));
			} else {
				reader = new BufferedReader(new InputStreamReader(
						getInputStream(urlStr)));
			}
			String strLen = null;
			while ((strLen = reader.readLine()) != null){
				sBuffer.append(strLen);
			}
			if (sBuffer != null){
				return sBuffer.toString();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (reader != null){
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 获取本地图片资源
	 * @param context
	 * @param path Assets目录下面的文件全名（包含后缀）
	 * @return Bitmap对象
	 * @deprecated		
	 * 		注意：该接口不建议使用，因为耦合了context,建议直接传入输入流
	 * @see getLocalBitmap(InputStream inputStream)
	 */
	public Bitmap getLocalBitmap(Context context, String path){
		if (path == null || TextUtils.isEmpty(path)){
			return null;
		}
		Bitmap bitmap = null;
		InputStream inputStream = null;
		try {
			inputStream = context.getResources().getAssets().open(path);
			bitmap = BitmapFactory.decodeStream(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null){
					inputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bitmap;
	}
	
	/**
	 * 获取本地图片资源
	 * @param inputStream 输入流
	 * @return Bitmap对象
	 */
	public Bitmap getLocalBitmap(InputStream inputStream){
		if (inputStream == null){
			return null;
		}
		return BitmapFactory.decodeStream(inputStream);
	}
	
	/**
	 * 获取网络图片资源
	 * @param imgUrl String类型， 图片资源url
	 * @return Bitmap对象
	 */
	public Bitmap downloadBitmap(String imgUrl){
		Bitmap bitmap = null;
		URL url = null;
		InputStream inputStream = null;
		HttpURLConnection conn = null;
		
		try {
			url = new URL(imgUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			inputStream = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(inputStream);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null){
					inputStream.close();
				}
				if (conn != null){
					conn.disconnect();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bitmap;
	}
	
	/**
	 * 下载数据
	 * @param url
	 * @param filePath
	 * @return
	 */
	public boolean downLoadAndSaveImage(String filePath, String fileName){
		if (TextUtils.isEmpty(filePath)){
			Log.i(TAG, "filePath is NULL");
			return false;
		}
		File file = new File(filePath);
		if (file != null && !file.exists()){
			file.mkdirs();
		}
		downLoadAndSaveImage(file, fileName);
		return false;
	}
	
	/**
	 * 保存下载文件到指定路径
	 * @param filePath 制定保存路径
	 * @param fileName 图片名称，这里是图片的链接地址
	 * @return boolean对象，TRUE:下载保存成功， FALSE:失败
	 */
	public boolean downLoadAndSaveImage(File filePath, String fileName){
		if (fileName == null){
			return false;
		}
		if (filePath == null || filePath.equals("")){
			return false;
		}
		String imageFilePath = filePath.getPath() + File.separator + fileName;
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
			url = new URL(fileName);
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setDoInput(true);
			httpConn.setConnectTimeout(TIME_OUT);
			httpConn.setReadTimeout(TIME_OUT);
			httpConn.setRequestMethod("GET");
			downFileSize = httpConn.getContentLength();
			
			int statue = httpConn.getResponseCode();
			if (statue == HttpURLConnection.HTTP_OK){
				buffer = new byte[BUFFER_SIZE];
				fOutputStream = new FileOutputStream(tempFile);
				inStream = httpConn.getInputStream();
				
				int size = -1;
				if ((size = inStream.read(buffer)) != -1){
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
			tempFile.renameTo(new File(filePath, fileName));
			return true;
		}
		return false;
	}
	
	/**
	 * 根据资源ID获取Bitmap对象
	 * @param res
	 * @param srcId
	 * @return
	 */
	public Bitmap getBitmap(Resources res,int resId){
		if (res == null){
			return null;
		}
		return BitmapFactory.decodeResource(res, resId);
	}
	
	/**
	 * 根据资源ID获取Drawable对象
	 * @param res
	 * @param resId
	 * @return
	 * @deprecated 该接口建议本地调用
	 */
	public Drawable getDrawable(Resources res,int resId){
		if (res == null){
			return null;
		}
		return res.getDrawable(resId);
	}

	/**
	 * InputSteam转化为Byte
	 * @param in 输入流
	 * @return byte[] 数组
	 */
	public byte[] InputStreamToByte(InputStream in){
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		byte[] outData = null;
		int count = -1;
		try {
			while ((count = in.read(data, 0, BUFFER_SIZE)) != -1){
				outputStream.write(data, 0, count);
			}
			outputStream.flush();
			outData = outputStream.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			data = null;
			try {
				if (outputStream != null){
					outputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return outData;
	}
	
	/**
	 * InputSteam 转为 String
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public String InputStreamToString(InputStream in) {
		byte[] data = InputStreamToByte(in);
		if (data != null){
			try {
				return new String(data, "ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 将Drawable转化为Bitmap
	 * @param drawable
	 * @return
	 */
	public Bitmap drawableToBitmap(Drawable drawable){
		if (drawable == null){
			return null;
		}
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        Bitmap.Config config = (drawable.getOpacity() != PixelFormat.OPAQUE) 
        		? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        
        return bitmap;
	}
	
	/**
	 * Bitmap转化为Drawable
	 * @param res
	 * @param bitmap
	 * @return
	 */
	public Drawable bitmapToDrawable(Resources res, Bitmap bitmap){
		//new BitmapDrawable(bitmap);
		return new BitmapDrawable(res, bitmap);
	}
	
	public byte[] bitmapToByte(Bitmap bitmap){
		ByteArrayOutputStream byteArrayOutputStream = 
				new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, 
				byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}
	
	/**
	 * 根据byte数组生成Bitmap对象
	 * @param b
	 * @return
	 */
	public Bitmap byteToBitmap(byte[] b){
		if (b == null || b.length <= 0){
			return null;
		}
		return BitmapFactory.decodeByteArray(b, 0, b.length);
	}
	
	/**
	 * Bitmap 缩放
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return 缩放后的Bitmap对象
	 */
	public Bitmap zoomBitmap(Bitmap bitmap, int width, int height){ 
		if (bitmap == null){
			return null;
		}
	    int w = bitmap.getWidth(); 
	    int h = bitmap.getHeight(); 
	    Matrix matrix = new Matrix(); 
	    float scaleWidth = ((float) width / w); 
	    float scaleHeight = ((float) height / h); 
	    matrix.postScale(scaleWidth,scaleHeight); 
	    Bitmap newbmp = Bitmap.createBitmap(bitmap,0, 0, w, h, matrix, true); 
	    return newbmp; 
	} 
	
	public Bitmap createCircleImage(Bitmap bitmap){  
		if (bitmap == null){
			Log.e(TAG, "createCircleImage ERROR, bitmap is NULL!!");
			return null;
		}
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int min = Math.min(width, height);
		
        final Paint paint = new Paint();  
        paint.setAntiAlias(true);  
        Bitmap outPutBitmap = Bitmap.createBitmap(min, min, Config.ARGB_8888);  
        /** 
         * 产生一个同样大小的画布 
         */  
        Canvas canvas = new Canvas(outPutBitmap);  
        /** 
         * 首先绘制圆形 
         */  
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);  
        /** 
         * 使用SRC_IN 
         */  
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));  
        /** 
         * 绘制图片 
         */  
        canvas.drawBitmap(bitmap, 0, 0, paint);  
        
        return outPutBitmap;  
    }  
	
}
