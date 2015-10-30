package com.tcl.wechat.action.recorder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import android.media.MediaRecorder;
import android.util.Log;

import com.tcl.wechat.action.recorder.interf.MediaRecorderImpl;
import com.tcl.wechat.action.recorder.listener.AudioPrepareCompletedListener;

/**
 * 音频管理类
 * @author rex.lei
 *
 */
public class RecorderAudioManager implements MediaRecorderImpl{
	
	private static final String TAG = "RecorderAudioManager";
	
	private static final int VOICE_MAX_LEVEL = 14;
	
	private String mFileDir;   //音频文件存储路径
	private String mCurrentFilePath;
	private MediaRecorder mMediaRecorder;
	private boolean bPrepared = false;
	
	private AudioPrepareCompletedListener mListener;
	/**
	 * 设置监听器
	 * @param listener
	 */
	public void setAudioStateListener(AudioPrepareCompletedListener listener){
		mListener = listener;
	}
	
	/**
	 * 实例对象类
	 * @author rex.lei
	 */
	private static class RecorderInstance{
		private static final RecorderAudioManager mInstance 
									= new RecorderAudioManager();
	}

	private RecorderAudioManager() {
		super();
	}
	
	public static RecorderAudioManager getInstance(){
		return RecorderInstance.mInstance;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void prepare() {
		File file = getFilePath();
		mCurrentFilePath = file.getAbsolutePath();
		
		try {
			bPrepared = false;
			mMediaRecorder = new MediaRecorder();
			//设置输出文件
			mMediaRecorder.setOutputFile(file.getAbsolutePath());
			//设置音频源为MIC
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			//设置音频格式
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			//设置音频编码格式个AMR
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			//设置最大录音文件大小2M
			mMediaRecorder.setMaxFileSize(2 * 1024 * 1024);
			
			mMediaRecorder.prepare();
			mMediaRecorder.start();
			
			//准备完成
			bPrepared = true;
			if (mListener != null){
				mListener.prepareCompleted();
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取音频文件路径
	 * @return
	 */
	public File getFilePath(){
		File file = null;
		try {
			Log.i(TAG, "FileDir:" + mFileDir);
			File dir = new File(mFileDir);
			if (!dir.exists()){
				dir.mkdirs();
			}
			String fileName = createFileName();
			Log.i(TAG, "fileName:" + fileName);
			file = new File(dir, fileName);
			Log.i(TAG, "filePath:" + file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * 生成音频文件名称
	 * @return 文件名称
	 */
	public String createFileName(){
		return UUID.randomUUID().toString() + ".amr";
	}
	
	@Override
	public void cancel() {
		release();
		if (mCurrentFilePath != null){
			File file = new File(mCurrentFilePath);
			file.delete();
			mCurrentFilePath = null;
		}
	}

	@Override
	public void release() {
		if (mMediaRecorder != null){
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}

	@Override
	public int getLevel() {
		if (bPrepared){
			//range 0-14;
			try {
				return VOICE_MAX_LEVEL * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 1;
	}

	public String getCurrentPath() {
		return mCurrentFilePath;
	}

	public void setFilePath(String path) {
		this.mFileDir = path;
	}
	
	/**
	 * 获取音频文件播放长度
	 * @param file
	 * @return
	 */
	public static long getDuration(File file) {  
		long duration = -1;  
        int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };  
        RandomAccessFile randomAccessFile = null;  
        try {  
            randomAccessFile = new RandomAccessFile(file, "rw");  
            long length = file.length();//文件的长度  
            int pos = 6;//设置初始位置  
            int frameCount = 0;//初始帧数  
            int packedPos = -1;  
            byte[] datas = new byte[1];//初始数据值  
            while (pos <= length) {  
                randomAccessFile.seek(pos);  
                if (randomAccessFile.read(datas, 0, 1) != 1) {  
                    duration = length > 0 ? ((length - 6) / 650) : 0;  
                    break;  
                }  
                packedPos = (datas[0] >> 3) & 0x0F;  
                pos += packedSize[packedPos] + 1;  
                frameCount++;  
            }  
            duration += frameCount * 20;//帧数*20  
        } catch (Exception e){
        	e.printStackTrace();
        }finally {  
            try {
				if (randomAccessFile != null) {  
				    randomAccessFile.close();  
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
        }  
        return duration;  
    }  
}
