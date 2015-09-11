package com.tcl.wechat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

public class RootSeeker
{

	private static final String TAG = "RootSeeker";
	private static final int SOCKET_PORT = 8090;
	private static final String SOCKET_IP = "127.0.0.1";

	//旧的RootSeeker，系统修改权限后使用新的RootSeeker。
	public static int exec(final String cmd)
	{

		// Log.d(TAG,
		// "<-----------------in exec() start --------------------->");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Log.d(TAG, "exec cmd: ooo " + cmd);

				Socket socket;
				BufferedReader in;
				PrintWriter out;
				char[] buf = new char[256];
				int ret = 0;

				try
				{
					Log.d(TAG, "new Socket(SOCKET_IP, SOCKET_PORT start); ");
					socket = new Socket(SOCKET_IP, SOCKET_PORT);
					Log.d(TAG, "new Socket(SOCKET_IP, SOCKET_PORT finish); ");
					in = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					out = new PrintWriter(socket.getOutputStream(), true);
					BufferedReader line = new BufferedReader(new InputStreamReader(
							System.in));
					out.println(cmd);
					if (in.read(buf) != -1 && new String(buf).equals(new String("ok")))
						ret = 0;
					else
						ret = -1;

					// Log.d(TAG, "command execute " + ((ret == 0) ? "ok" : "false") +
					// ", return: -->" +buf);
					Log.d(TAG, "buf=" + new String(buf));
					line.close();
					out.close();
					in.close();
					socket.close();
//					return 0; // FIXME
				}
				catch (IOException e)
				{
					Log.d(TAG, e.toString());
				}
			}
		}).start();
	
		return 0;
	}

	/*public static int exec(String cmd) {
        // TODO Auto-generated method stub
        LocalSocketAddress lsa = new android.net.LocalSocketAddress("sserver", LocalSocketAddress.Namespace.RESERVED);
        LocalSocket ls = new LocalSocket();
        byte[] buffer = new byte[16]; // receive result "ok" or "fail"
        InputStream is = null;
        OutputStream os = null;
        
        
        try {
            Log.d(TAG,"start connect");
            ls.connect(lsa);
            is = ls.getInputStream();
            os = ls.getOutputStream();
            os.write(cmd.getBytes());
            int rlen = is.read(buffer);
        //  String result = new String(buffer, 0, rlen);

            if(rlen != -1){
            String result = new String(buffer, 0, rlen);
                if (result.equals("ok")) {
                    Log.d(TAG, "OK");
                } else if (result.equals("fail")) {
                    Log.d(TAG, "FAIL");
                }
            }else{
                Log.d(TAG," read failed!!!!!!!!!!!");
            }
            
            is.close();
            os.close();
            ls.close();

            return 0;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return 0;
        
    }*/
	
	public static boolean chmod(String fileName)
	{
		String cmd = "chmod 777 " + fileName;
		System.out.println("cmd =" + cmd);
		try
		{
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(cmd);
			return true;
		}
		catch (Exception e)
		{

			e.printStackTrace();
			return false;
		}
	}

}
