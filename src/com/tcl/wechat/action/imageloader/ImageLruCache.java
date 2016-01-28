package com.tcl.wechat.action.imageloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.tcl.wechat.utils.DataFileTools;
import com.tcl.wechat.utils.MD5Util;

/**
 * Lru二级缓存
 * @author rex.lei
 *
 */
public class ImageLruCache implements ImageCache{
	
	private static final String TAG = ImageLruCache.class.getSimpleName();

	/**
	 * 内存最大缓存大小
	 */
	private static final int MAX_MEMORY_CACHE_SIZE = 5 * 1024 * 1024;
    
	/**
	 * 硬盘最大缓存大小
	 */
	private static final long MAX_DISK_CACHE_SIZE = 20 * 1024 * 1024;
	
	/**
     * 图片内存缓存核心类
     */
    private LruCache<String, Bitmap> mMemLruCache;
 
    /**
     * 图片硬盘缓存核心类
     */
    private DiskLruCache mDiskLruCache;
	
	public ImageLruCache() {
		super();
		
		mMemLruCache = new LruCache<String, Bitmap>(MAX_MEMORY_CACHE_SIZE){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// TODO Auto-generated method stub
				return value.getByteCount();
			}
		};
		
		File cacheDir = new File(DataFileTools.getRecordImagePath());
		if (!cacheDir.exists()){
			cacheDir.mkdirs();
		}
		try {
			mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1, MAX_DISK_CACHE_SIZE);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

	@Override
	public Bitmap getBitmap(String url) {
		String key = generateKey(url);
        Bitmap bmp = mMemLruCache.get(key);
        if (bmp == null) {
            bmp = getBitmapFromDiskLruCache(key);
            //从磁盘读出后，放入内存
            if(bmp != null){
            	mMemLruCache.put(key,bmp);
            }
        }
        return bmp;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		// TODO Auto-generated method stub
		String key = generateKey(url);
		mMemLruCache.put(key, bitmap);
        putBitmapToDiskLruCache(key, bitmap);
	}
	
	private Bitmap getBitmapFromDiskLruCache(String key) {
		InputStream inputStream = null;
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if(snapshot!=null){
                inputStream = snapshot.getInputStream(0);
                if (inputStream != null) {
                    Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                    return bmp;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try {
				if (inputStream != null){
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return null;
    }
	
	private boolean putBitmapToDiskLruCache(String key, Bitmap bitmap) {
        boolean bRet = false;
		try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if(editor != null){
                OutputStream outputStream = editor.newOutputStream(0);
                bRet = bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return bRet;
    }
	
	private String generateKey(String url){
        return MD5Util.hashKeyForDisk(url);
    }
}
