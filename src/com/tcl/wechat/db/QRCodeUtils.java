package com.tcl.wechat.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.utils.CommonsFun;

  
  
public class QRCodeUtils {  
	//private int QR_WIDTH = 400;
	//private int QR_HEIGHT = 400;
	private String tag = "CreateQR";
	private static final int PADDING_SIZE_MIN = 1; // 最小留白长度, 单位: px
    /** 
     * 编码字符串内容到目标File对象中 
	 * @ClassName: CreateQR
	 * @Description: 根据开机次数，本机信息以及网络二维码或者公众号二维码信息生成一个二维码
	 * @author liyulin
	 * @date 2015年1月23日 下午7:45:12
	 */
	
	public Bitmap createNewQR(String url,String mCFROM,int QR_WIDTH,int QR_HEIGHT,Context mContext) {
		Log.i(tag,"url="+url);		
		String uuid = ProviderFun.getuuid(mContext);
		Log.i(tag,"uuid="+uuid);
		StringBuilder sb = new StringBuilder();  
		sb.append(url).append("#");
		sb.append(CommonsFun.getSn()).append("|");
		sb.append(CommonsFun.getDeviceId(mContext)).append("|");
		sb.append(CommonsFun.getMAC()).append("|");
		sb.append( CommonsFun.getSoftVersion()).append("|");
		sb.append(CommonsFun.getUbootVersion()).append("|");//Uboot version
	
		sb.append(CommonsFun.getBootTimes(mContext)).append("|");
		sb.append(uuid).append("|");
		//CFROM：表示扫码来源（两个扫码来源come from，1是开机向导，2是微信互联），值有：1,2 。
		//生成一个二维码图片给微信自己用
		if(mCFROM.equals(WeiConstant.CFROM.Guide))
		 return encoderQRCode(sb.append("1").toString(),mContext,mCFROM, QR_WIDTH, QR_HEIGHT);
		else if(mCFROM.equals(WeiConstant.CFROM.WeiXin))
		 return encoderQRCode(sb.append("2").toString(),mContext,mCFROM, QR_WIDTH, QR_HEIGHT);
		return null;
		
		
	}

	// 要转换的地址或字符串,可以是中文
	public Bitmap encoderQRCode(String ticket,Context mContext,String mCFROM,int QR_WIDTH,int QR_HEIGHT) {
		Log.i(tag, "ticket="+ticket);
		Bitmap bitmapQR = null;
		try {
			// 判断ticket合法性
			if (ticket == null || "".equals(ticket) || ticket.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);


			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(ticket, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
	 
	        boolean isFirstBlackPoint = false;
	        int startX = 0;
	        int startY = 0;
	 
	        for (int y = 0; y < QR_HEIGHT; y++) {
	            for (int x = 0; x < QR_WIDTH; x++) {
	                if (bitMatrix.get(x, y)) {
	                    if (isFirstBlackPoint == false)
	                    {
	                        isFirstBlackPoint = true;
	                        startX = x;
	                        startY = y;
	                        Log.d("createQRCode", "x y = " + x + " " + y);
	                    }
	                    pixels[y * QR_WIDTH + x] = 0xff000000;
	                }else{
	                	pixels[y * QR_WIDTH + x] = 0xffffffff;
	                }
	            }
	        }
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			//saveBitmap2file(bitmap,"qr.bmp");
	        // 剪切中间的二维码区域，减少padding区域
	        if (startX <= PADDING_SIZE_MIN) 
	        	return bitmap;
	 
	        int x1 = startX - PADDING_SIZE_MIN;
	        int y1 = startY - PADDING_SIZE_MIN;
	        if (x1 < 0 || y1 < 0) return bitmap;
	 
	        int w1 = QR_WIDTH - x1 * 2;
	        int h1 = QR_HEIGHT - y1 * 2;
	        Log.d("createQRCode", "w1 h1 = " + w1 + " " + h1);
	        bitmapQR = Bitmap.createBitmap(bitmap, x1, y1, w1, h1);
	        return bitmapQR;
	     
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void saveBitmap2file(Bitmap bmp,String filename) {
	   Log.e(tag, "保存图片");
	   File f = new File(WeiConstant.DOWN_LOAD_FLASH_PATH, filename);
	   if (f.exists()) {
	    f.delete();
	   }
	   try {
	    FileOutputStream out = new FileOutputStream(f);
	    bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
	    out.flush();
	    out.close();
	    Log.i(tag, "已经保存");
	   } catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	   } catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	   }

	 }
	
	
	/*public void encoderQRCode(String ticket) {
		try {
		Qrcode qrcodeHandler = new Qrcode();
		qrcodeHandler.setQrcodeErrorCorrect('M');
		qrcodeHandler.setQrcodeEncodeMode('B');
		qrcodeHandler.setQrcodeVersion(7);
		 
		byte[] contentBytes = ticket.getBytes("utf-8");
		BufferedImage bufImg = new BufferedImage(275, 275, BufferedImage.TYPE_INT_RGB);
		Graphics2D gs = bufImg.createGraphics();
		gs.setBackground(Color.WHITE);
		gs.clearRect(0, 0, 275, 275);
		 
		// 设定图像颜色> BLACK
		gs.setColor(Color.BLACK);
		// 设置偏移量 不设置可能导致解析出错
		 
		int pixoff = 2;
		// 输出内容> 二维码
		if (contentBytes.length > 0 && contentBytes.length < 120) {
		boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
		for (int i = 0; i < codeOut.length; i++) {
		for (int j = 0; j < codeOut.length; j++) {
		if (codeOut[j][i]) {
		gs.fillRect(j * 6 + pixoff, i * 6 + pixoff, 6, 6);
		}
		}
		}
		} else {
		System.err.println("QRCode content bytes length = " + contentBytes.length + " not in [ 0,120 ]. ");
		}
		gs.dispose();
		bufImg.flush();
		 
		// 生成二维码QRCode图片
		ImageIO.write(bufImg, "png", new File("c:\\image.png"));
		} catch (Exception e) {
		e.printStackTrace();
		}
		 
		}*/

}
