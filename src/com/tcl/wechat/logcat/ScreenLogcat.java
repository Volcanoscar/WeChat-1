package com.tcl.wechat.logcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
/*
首先要声明权限：
<uses-permission android:name="android.permission.READ_LOGS" /> 
*/
public class ScreenLogcat {
	private LogDumper2 dumper;
	private WeakReference<TextView> tv;
	private WeakReference<Button> btn;
	private WeakReference<Button> sw;
	private int dumperState = 0;
	private final int PAUSE = 1;
	private final int RUNNING = 2;
	private final int STOP = 3;
	private Object lock = new Object();

	/**
	 * 输出logcat到屏幕上
	 * 
	 * @param rootView
	 *            传入要显示的View
	 * @param cmd
	 *            传入要输出的logcat命令，如：logcat -s AndroidRuntime
	 * @return 返回View用于在Activity里setContentView
	 */
	public View addLogcatToScreen(Context context, View rootView, String cmd) {
		// 新建layout
		FrameLayout layout = new FrameLayout(context);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		layout.addView(rootView);
		// 用于显示的logcat的TextView
		TextView textView = new TextView(context);
		textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		textView.setFocusable(false);
		textView.setTextColor(Color.RED);
		textView.setText("start Logcat\n");
		textView.setGravity(Gravity.BOTTOM);
		// textView.setHorizontallyScrolling(true);
		// textView.setMovementMethod(ScrollingMovementMethod.getInstance());
		textView.setVisibility(View.GONE);
		tv = new WeakReference<TextView>(textView);
		layout.addView(textView);
		// 用于切换滚动状态的按钮
		Button button = new Button(context, null);
		FrameLayout.LayoutParams par = new FrameLayout.LayoutParams(120, 100);
		par.setMargins(200, 0, 0, 0);
		button.setLayoutParams(par);
		button.setBackgroundColor(0x40008000);
		button.setText("停止滚动");
		button.setTextColor(Color.RED);
		button.setOnClickListener(new View.OnClickListener() {
			boolean isScrollable = false;

			@Override
			public void onClick(View v) {
				if (tv.get() == null || btn.get() == null)
					return;
				if (isScrollable) {
					tv.get().setMovementMethod(null);
					tv.get().setGravity(Gravity.BOTTOM);
					tv.get().setFocusable(false);
					isScrollable = false;
					btn.get().setText("停止滚动");
					btn.get().setTextColor(Color.RED);
				} else {
					tv.get().setMovementMethod(ScrollingMovementMethod.getInstance());
					tv.get().setGravity(Gravity.TOP);
					isScrollable = true;
					btn.get().setText("继续滚动");
					btn.get().setTextColor(Color.GREEN);
				}
			}
		});
		button.setVisibility(View.GONE);
		btn = new WeakReference<Button>(button);
		layout.addView(button);
		// 用于隐藏显示的按钮
		Button switcher = new ScreenLogcat.HiddenButton(context);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(200, 100);
		// params.gravity = Gravity.CENTER_HORIZONTAL;
		switcher.setLayoutParams(params);
		switcher.setBackgroundColor(0x00008080);
		sw = new WeakReference<Button>(switcher);
		layout.addView(switcher);
		// 开始线程
		if (dumper == null) {
			dumper = new LogDumper2(cmd);
		}
		dumper.start();
		synchronized (lock) {
			dumperState = RUNNING;
			dumper.pauseLogs();
			dumperState = PAUSE;
		}
		return layout;
	}

	/**
	 * 恢复logcat线程
	 */
	public void continueLogcatToScreen() {

		if (dumper != null && dumperState == PAUSE) {
			dumper.continueLogs();
		}
	}

	/**
	 * 暂停logcat线程
	 */
	public void pauseLogcatToScreen() {
		synchronized (lock) {
			if (dumper != null && dumperState == RUNNING) {
				dumper.pauseLogs();
				dumperState = PAUSE;
			}
		}
	}

	/**
	 * 结束logcat线程
	 */
	public void destroyLogcatToScreen() {
		dumper.stopLogs();
	}

	private class LogDumper2 extends Thread {
		private Process logcatProc;
		private BufferedReader mReader = null;
		private boolean mRunning = true;
		private boolean mPause = false;
		private String cmds = null;

		public LogDumper2(String cmd) {
			/**
			 * * * log level：*:v , *:d , *:w , *:e , *:f , *:s * * Show the
			 * current mPID process level of E and W log. * *
			 */
			int pid = android.os.Process.myPid();
			String mPID = Integer.toString(pid);
			// cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
			if (cmd == null)
				cmds = "logcat | grep \\(" + mPID + "\\)";
			else
				cmds = cmd;
		}

		public void stopLogs() {
			mRunning = false;
		}

		public void pauseLogs() {
			synchronized (lock) {
				mPause = true;
				dumperState = PAUSE;
			}
		}
		
		public void continueLogs() {
			synchronized (this) {
				mPause = false;
				notify();
			}
			synchronized (lock) {
				dumperState = RUNNING;
			}
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
					if (tv.get() != null)
						tv.get().post(new SetTextRunnable(line));
					synchronized (this) {
						if (mPause)
							try {
								wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
								Log.e("A","LogDumper2 InterruptedException");
							}
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
				// synchronized (lock) {
				dumperState = STOP;
				// }
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
			}
		}

	}

	private StringBuilder builder = null;
	private long lastTime;
	private class SetTextRunnable implements Runnable {
		private String str;
		
		public SetTextRunnable(String str) {
			this.str = str;
		}

		@Override
		public void run() {
			if (tv.get() != null) {
				//做一个缓存处理,200毫秒才打印一次
				long curTime = System.currentTimeMillis();
				if(curTime-lastTime<200){
					if(builder == null)
						builder = new StringBuilder();
					builder.append("~~" + str + "\n");
				}else{
					lastTime = curTime;
					if(builder!=null) {
						tv.get().append(builder.toString());
						builder = null;
					}
					else 
						tv.get().append("~~" + str + "\n");
					
				}
				// 内容太多时，清空
				if (tv.get().getText().length() > 1*1024*1024) {
					tv.get().setText("-------------too mang logcat,Screen clear----------------------\n");
				}
			}
		}
	}

	public class HiddenButton extends Button {
		private int count = 0;
		private long lastTime = 0;
		private boolean isLogcating = false;

		public HiddenButton(Context context) {
			super(context);
		}

		public HiddenButton(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public boolean performClick() {
			long curTime = SystemClock.elapsedRealtime();
			if (curTime - lastTime < 2000) {
				count++;
				if (count >= 4) {
					// 连续点击5次后
					if (isLogcating) {
						if (btn.get() != null)
							btn.get().setVisibility(View.GONE);
						if (tv.get() != null)
							tv.get().setVisibility(View.GONE);
						pauseLogcatToScreen();
						isLogcating = false;
					} else {
						if (btn.get() != null)
							btn.get().setVisibility(View.VISIBLE);
						if (tv.get() != null)
							tv.get().setVisibility(View.VISIBLE);
						continueLogcatToScreen();
						isLogcating = true;
					}
					count = 0;
				}
			} else {
				lastTime = curTime;
				count = 0;
			}
			return super.performClick();
		}

	}
	
}
