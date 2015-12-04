package com.tcl.wechat.logcat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
/*
	首先要声明权限：
	<uses-permission android:name="android.permission.READ_LOGS" /> 
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/> 
 */
/**
 * 输出Logcat到本地文件
 * <p>
 * 默认输出到SD卡的Logcat文件夹，否则到该应用的文件下的Logcat文件夹
 */
public class LogcatManager {
	private static LogcatManager INSTANCE = null;
	private static String PATH_LOGCAT;
	private LogDumper mLogDumper = null;
	private int mPId;
	private SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static LogcatManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LogcatManager();
		}
		return INSTANCE;
	}

	private LogcatManager() {
		mPId = android.os.Process.myPid();
	}

	public void startLogcatToFile(Context context,String cmd) {
		String folderPath = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
					+ "Logcat";
			Log.i("A","Logcat to sdcard");
		} else {
			folderPath = context.getFilesDir().getAbsolutePath() + File.separator + "Logcat";
			Log.i("A","Logcat to fileDir");
		}
		LogcatManager.getInstance().start(folderPath,cmd);
	}
	
	public void startLogcatToFile(Context context){
		startLogcatToFile(context,null);
	}

	public void stopLogcatToFile() {
		LogcatManager.getInstance().stop();
	}

	private void setFolderPath(String folderPath) {
		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		if (!folder.isDirectory()) {
			throw new IllegalArgumentException("The logcat folder path is not a directory: " + folderPath);
		}

		PATH_LOGCAT = folderPath.endsWith("/") ? folderPath : folderPath + "/";
		Log.d("A",PATH_LOGCAT);
	}

	public void start(String saveDirectoy,String cmd) {
		setFolderPath(saveDirectoy);
		if (mLogDumper == null) {
			mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT, cmd);
		}
		mLogDumper.start();
	}

	private void stop() {
		if (mLogDumper != null) {
			mLogDumper.stopLogs();
			mLogDumper = null;
		}
	}

	private class LogDumper extends Thread {
		private Process logcatProc;
		private BufferedReader mReader = null;
		private boolean mRunning = true;
		String cmds = null;
		private String mPID;
		private FileOutputStream out = null;

		public LogDumper(String pid, String dir, String cmd) {
			mPID = pid;
			try {
				out = new FileOutputStream(new File(dir, "logcat-" + simpleDateFormat1.format(new Date())
						+ ".log"), true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			/**
			 * * * log level：*:v , *:d , *:w , *:e , *:f , *:s * * Show the
			 * current mPID process level of E and W log. * *
			 */
			// cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
			if( cmd == null)
				cmds = "logcat | grep \\(" + mPID + "\\)";
			else
				cmds = cmd;
		}

		public void stopLogs() {
			mRunning = false;
		}

		@Override
		public void run() {
			try {
				logcatProc = Runtime.getRuntime().exec(cmds);
				mReader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()), 1024);
				String line = null;
				while (mRunning && (line = mReader.readLine()) != null) {
					if (!mRunning) {
						break;
					}
					if (line.length() == 0) {
						continue;
					}
					if (out != null && line.contains(mPID)) {
						out.write((simpleDateFormat2.format(new Date()) + "  " + line + "\n").getBytes());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (logcatProc != null) {
					logcatProc.destroy();
					logcatProc = null;
				}
				if (mReader != null) {
					try {
						mReader.close();
						mReader = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					out = null;
				}
			}
		}

	}
	
}