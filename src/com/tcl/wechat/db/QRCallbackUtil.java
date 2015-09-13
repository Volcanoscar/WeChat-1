package com.tcl.wechat.db;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.utils.UIUtils;



public class QRCallbackUtil {
	
	private Context mContext =null;
	private String tag = "QRCallbackUtil";
	boolean geturl = true;//轮询标志
	private int QR_WIDTH;
	private int QR_HEIGHT;
	private String CFROM=null;
	public QRCallbackUtil(Context mContext,int mQR_WIDTH,int mQR_HEIGHT,String mCFROM){
		this.mContext = mContext;
		QR_WIDTH = mQR_WIDTH;
		QR_HEIGHT = mQR_HEIGHT;
		CFROM = mCFROM;

	}
    
	 public void registerQrCallback(final QRCallback huiCallback){ 
         
         new Thread(){ 
			@Override 
             public void run() { 
                 super.run();              
                 String uri = ProviderFun.getQR_url(mContext);
        		 Log.i(tag,"回调的uri："+uri);
        		 if (uri != null && !uri.equals("")){
        			 huiCallback.callback(uri); 
        			 Log.i(tag,"000数据库已经有二维码，直接回调给其他进程");
        			 return;
        		 }	    			
    		     //轮询
    		     try {
					sleep(3000);
					
	    		     while(geturl){	    		    					
						String uritmp = ProviderFun.getQR_url(mContext);
		        		 Log.i(tag,"轮询的uri："+uri);
		        		 if (uritmp != null && !uritmp.equals("")){
		        			 huiCallback.callback(uritmp); 
		        			 Log.i(tag,"数据库已经有二维码，直接回调给其他进程");
		        			 geturl = false;
		        			 return;
		        		 }
		        		sleep(1000);
	    		     }		    		     
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}              
             } 
         }.start(); 
	 }
	 
 public void registerQrbmpCallback(final QRCallback huiCallback){ 
         
         new Thread(){ 
			@Override 
             public void run() { 
                 super.run();              
                 String uri = ProviderFun.getQR_url(mContext);
        		 Log.i(tag,"回调的uri："+uri);
        		//有网络URL
        		 if (uri != null && !uri.equals("")){
        			 Bitmap bitmap =  new QRCodeUtils().createNewQR(uri.substring(uri.indexOf("ticket=")+7),CFROM, QR_WIDTH, QR_HEIGHT,mContext);
        			 huiCallback.callback(bitmap); 
        			 Log.i(tag,"000数据库已经有二维码，直接回调给其他进程");
        			 return;
        		 }else{//没有Url，1、无网络 返回公众号  
        			 if (!UIUtils.isNetworkAvailable()){
        				 Bitmap bitmap =  new QRCodeUtils().createNewQR(WeiConstant.ticket,CFROM,QR_WIDTH, QR_HEIGHT,mContext); 
        				 huiCallback.callback(bitmap); 
        			 }else {//2、有网络，启动服务，轮询
            		     try {
        					sleep(3000);
        					int times =0;
        	    		     while(geturl&&times<100){	
        	    		    	 times++;
        						String uritmp = ProviderFun.getQR_url(mContext);
        		        		 Log.i(tag,"轮询的uri："+uri);
        		        		 if (uritmp != null && !uritmp.equals("")){
        		        			 Bitmap bitmap =  new QRCodeUtils().createNewQR(uritmp.substring(uritmp.indexOf("ticket=")+7),CFROM,QR_WIDTH, QR_HEIGHT,mContext);
        		        			 huiCallback.callback(bitmap);
        		        			 Log.i(tag,"数据库已经有二维码，直接回调给其他进程");
        		        			 geturl = false;
        		        			 return;
        		        		 }
        		        		sleep(2000);
        	    		     }
        		    		     
        				} catch (InterruptedException e1) {
        					// TODO Auto-generated catch block
        					e1.printStackTrace();
        				}
					}
        		 }          
             } 
         }.start(); 
	 }
 
	 public void unregisterQrCallback(){ 
		 geturl = false;
	 }
         
     /**
      * 回调接口
     * @author Administrator
      *
      */ 
     public interface QRCallback { 
         public void callback(String str); 
         public void callback(Bitmap bitmap); 
     } 
}
