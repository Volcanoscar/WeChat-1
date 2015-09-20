package com.tcl.wechat.action.imageloader;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * 图片加载类
 * @author rex.lei
 *
 */
public class ImageLoader {
	
	private static final String TAG = ImageLoader.class.getSimpleName();
	
	private static final int DEFAULT_THREAD_COUNT = 1;
	
	private static ImageLoader mInstance;
	
	/**
	 * 图片缓存核心对象
	 */
	private LruCache<String, Bitmap> mLruCache;
	
	/**
	 * 线程池
	 */
	private ExecutorService mThreadProol;
	
	/**
	 * 任务队列
	 */
	private LinkedList<Runnable> mTaskQueue;
	
	/**
	 * 后台轮询线程
	 */
	private Thread mPollThread;
	
	private Handler mPoolThreadHandler;
	
	/**
	 * 线程池个数，默认为1
	 */
	private int mThreadPollCnt = 1;
	
	private Semaphore mPollThreadSemaphore = new Semaphore(0);
	
	private Semaphore mSemaphore;
	
	/**
	 * 队列调度方式
	 */
	private Type mType = Type.LIFO;
	
	public enum Type{
		FIFO,
		LIFO
	}
	
	private ImageLoader(Type type, int threadCnt ) {
		super();
		mType = type;
		mThreadPollCnt = threadCnt;
		init();
	}
	
	public static ImageLoader getInstance(){
		if (mInstance == null){
			synchronized (ImageLoader.class) {
				if (mInstance == null){
					mInstance = new ImageLoader(Type.LIFO, DEFAULT_THREAD_COUNT);
				}
			}
		}
		return mInstance;
	}
	
	public static ImageLoader getInstance(Type type, int threadCnt){
		if (mInstance == null){
			synchronized (ImageLoader.class) {
				if (mInstance == null){
					mInstance = new ImageLoader(type, threadCnt);
				}
			}
		}
		return mInstance;
	}
	
	private void init() {
		// TODO Auto-generated method stub
		mPollThread = new Thread(mPollThreadRunnable);
		mPollThread.start();
		
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheMemory = maxMemory / 8;
		
		mLruCache = new LruCache<String, Bitmap>(cacheMemory){
			
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			};
		};
		
		//创建线程池
		mThreadProol = Executors.newFixedThreadPool(mThreadPollCnt);
		mTaskQueue = new LinkedList<Runnable>();
		
		mSemaphore = new Semaphore(mThreadPollCnt);
	}
	
	private Runnable mPollThreadRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			mPoolThreadHandler = new Handler(){
				public void handleMessage(Message msg) {
					
					//TODO 线程池取出一个任务执行
					mThreadProol.execute(getTask());
					
					try {
						mSemaphore.acquire();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			mPollThreadSemaphore.release();
			Looper.loop();
		}
	};

	private Handler mUIHandler = new Handler(){
		public void handleMessage(Message msg) {
			//获取得到的图片
			ImageHolder holder = (ImageHolder) msg.obj;
			ImageView imgView = holder.getmImageView();
			
			if (imgView.getTag().equals(holder.getmPath())){
				imgView.setImageBitmap(holder.getmBitmap());
			}
		};
	};
	
	/**
	 * 加载图片
	 * @param path
	 * @param imgView
	 */
	public void loadImage(String path, ImageView imageView){
		imageView.setTag(path);
		
		Bitmap bitmap = getBitmapFromLruCache(path);
		if (bitmap != null){
			
			refreash(bitmap, imageView, path);
		} else {
			
			addTask(new LoadImageTask(imageView, path));
		}
	}
	
	
	@SuppressLint("NewApi") 
	private ImageSize getImageSize(ImageView imageView){
		if (imageView == null){
			return null;
		}
		
		DisplayMetrics metrics = imageView.getContext().getResources()
				.getDisplayMetrics();
		
		LayoutParams params = imageView.getLayoutParams();
		int width = imageView.getWidth();
		if (width <= 0){
			width = params.width;
		}
		if (width <= 0){
			width = imageView.getMaxWidth();
		}
		if (width <= 0 ){
			width = metrics.widthPixels;
		}
		
		int height = imageView.getHeight();
		if (height <= 0){
			height = params.height;
		}
		if (height <= 0){
			height = imageView.getMaxHeight();
		}
		if (width <= 0 ){
			width = metrics.heightPixels;
		}
		
		return new ImageSize(width, height);
	}
	
	/**
	 * 计算SampleSize
	 * @param options
	 * @param size
	 * @return
	 */
	private int caculateInsampleSize(Options options, ImageSize size) {
		if (size == null || size.getWidth() == 0 || size.getHeight() == 0){
			return 0;
		}
		
		// TODO Auto-generated method stub
		int width = options.outWidth;
		int height = options.outHeight;
		
		int inSampleSize = 1;
		if (width > size.getWidth() || height > size.getHeight()){
			int widthRadio = Math.round(width/ size.getWidth());
			int heightRadio = Math.round(height / size.getHeight());
			
			inSampleSize = Math.max(widthRadio, heightRadio);
		}
		return inSampleSize;
	}
	
	/**
	 * 图片压缩
	 * @param path
	 * @param size
	 * @return
	 */
	private Bitmap decodeSampleBitmapFromPath(String path, ImageSize size) {
		
		if (TextUtils.isEmpty(path) || size == null){
			return null;
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; //不进行内存加载
		BitmapFactory.decodeFile(path, options);
		
		options.inSampleSize = caculateInsampleSize(options, size);
		options.inJustDecodeBounds = false; //加载内存
		return BitmapFactory.decodeFile(path, options);
	}
	
	/**
	 * 加载图片类
	 * @author rex.lei
	 *
	 */
	private class LoadImageTask implements Runnable{

		ImageView imageView ;
		String path;
		
		public LoadImageTask(ImageView imageView, String path) {
			super();
			this.imageView = imageView;
			this.path = path;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//加载图片
			ImageSize size = getImageSize(imageView);
			//压缩图片
			Bitmap bitmap = decodeSampleBitmapFromPath(path, size);
			//图片加入缓存
			addBitmapToLruCache(bitmap, path);
			//更新界面
			refreash(bitmap, imageView, path);
			
			mSemaphore.release();
		}
	}
	
	/**
	 * 更新
	 * @param bitmap
	 * @param imageView
	 * @param path
	 */
	private void refreash(Bitmap bitmap, ImageView imageView, String path) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		ImageHolder holder = new ImageHolder(bitmap, imageView, path);
		msg.obj = holder;
		mUIHandler.sendMessage(msg);
	}
	
	/**
	 * 取出任务
	 * @return
	 */
	private Runnable getTask() {
		if (mType == Type.FIFO){
			return mTaskQueue.removeFirst();
		} else if  (mType == Type.LIFO){
			return mTaskQueue.removeLast();
		}
		return null;
	};
	
	/**
	 * 添加任务
	 * @param runnable
	 */
	private synchronized void addTask(Runnable runnable) {
		if (runnable == null){
			return ;
		}
		mTaskQueue.add(runnable);
		if (mPoolThreadHandler == null){
			try {
				mPollThreadSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mPoolThreadHandler.sendEmptyMessage(0);
	}

	/**
	 * 图片加入缓存
	 * @param bitmap
	 * @param path
	 */
	private void addBitmapToLruCache(Bitmap bitmap, String path) {
		// TODO Auto-generated method stub
		if (getBitmapFromLruCache(path) == null){
			mLruCache.put(path, bitmap);
		}
	}
	
	/**
	 * 从缓存中获取图片
	 * @param path
	 * @return
	 */
	private Bitmap getBitmapFromLruCache(String path) {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(path) || mLruCache == null){
			return null;
		}
		return mLruCache.get(path);
	}
	
}
