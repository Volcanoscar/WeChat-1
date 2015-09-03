package com.tcl.wechat.encoder;

import java.util.Hashtable;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * 生成二维码名片
 * @author rex.lei
 *
 */
public class QRCodeCreator {

	public final static int BLACK = 0xff000000;
	
	/**
	 * 二维码名片生成器
	 * @param content
	 * @param widthAndHeight
	 * @return
	 */
	public static Bitmap create(String content, int widthAndHeight){
		
		Hashtable<EncodeHintType, String> hashtable = new Hashtable<EncodeHintType, String>();
		hashtable.put(EncodeHintType.CHARACTER_SET, "utf-8");
		
		try {
			BitMatrix matrix = new MultiFormatWriter().encode(content, 
					BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
			int width = matrix.getWidth();
			int height = matrix.getHeight();
			
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (matrix.get(x, y)){
						pixels[y * width + x] = 0xff000000;
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			
			return bitmap;
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
}
