package com.tcl.wechat.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.tcl.wechat.encoder.QRCodeCreator;

/**
 * 生成二维码名片
 * @author rex.lei
 *
 */
public class QrUtil {

	private Resources mResources;
	
	public QrUtil(Context context) {
		super();
		mResources = context.getResources();
	}

	/**
	 * 生成二维码名片
	 * @param img
	 * @param content
	 */
	public Bitmap createQRCode(String content){
		return createQRCode( content, null);
	}
	
	/**
	 * 生成二维码名片
	 * @param content
	 * @param resId
	 */
	public Bitmap createQRCode(String content, int resId){
		//获取个人图像
		Bitmap icon = BitmapFactory.decodeResource(
				mResources, resId);
		return createQRCode(content, icon);
	}
	
	/**
	 * 生成二维码名片
	 * @param content
	 */
	public Bitmap createQRCode(String content, Bitmap centerIcon){
		
		//产生二维码名片
		Bitmap qRCodeBitmap = QRCodeCreator.create(content, 600);
		
		//二维码进行缩放
		centerIcon = ImageUtil.getInstance().zoomBitmap(centerIcon,60, 60);
		
		//合并二维码与个人图像
		Bitmap bitmap = Bitmap.createBitmap(
				qRCodeBitmap.getWidth(),
				qRCodeBitmap.getHeight(), 
				qRCodeBitmap.getConfig());
		
		Canvas canvas = new Canvas(bitmap);
		//二维码
 		canvas.drawBitmap(qRCodeBitmap, 0, 0, null);
 		
 		if (centerIcon != null){
 			//personIcon绘制在二维码中央
 			canvas.drawBitmap(centerIcon, 
 					(qRCodeBitmap.getWidth() - centerIcon.getWidth()) / 2, 
 					(qRCodeBitmap.getHeight() - centerIcon.getHeight()) / 2, 
 					null);
 		}
 		return bitmap;
	}
}
