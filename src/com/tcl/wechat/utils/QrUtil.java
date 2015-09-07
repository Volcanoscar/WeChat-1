package com.tcl.wechat.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.widget.ImageView;

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
	public void createQRCode(ImageView img, String content){
		createQRCode(img, content, null);
	}
	
	/**
	 * 生成二维码名片
	 * @param content
	 * @param resId
	 */
	public void createQRCode(ImageView img, String content, int resId){
		if (img == null){
			return ;
		}
		//获取个人图像
		Bitmap icon = BitmapFactory.decodeResource(
				mResources, resId);
		createQRCode(img, content, icon);
	}
	
	/**
	 * 生成二维码名片
	 * @param content
	 */
	public void createQRCode(ImageView img, String content, Bitmap centerIcon){
		//产生二维码名片
		Bitmap qRCodeBitmap = QRCodeCreator.create(content, 450);
		
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
		img.setImageBitmap(bitmap);
	}
}
