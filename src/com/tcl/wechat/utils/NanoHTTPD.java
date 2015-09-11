package com.tcl.wechat.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tcl.wechat.db.DeviceDao;
//import com.tcl.common.mediaplayer.aidl.MediaBean;
//import com.tcl.os.system.SystemProperties;
//import com.tcl.tvmanager.TTvCommonManager;
//import com.tcl.tvmanager.TTvManager;
//import com.tcl.tvmanager.TTvUtils;
//import com.tcl.tvmanager.vo.EnTCLCallBackSetSourceMsg;
//import com.tcl.tvmanager.vo.EnTCLInputSource;
//import android.tclwidget.TCLToast;


/**
 * A simple, tiny, nicely embeddable HTTP 1.0 (partially 1.1) server in Java
 *
 * <p> NanoHTTPD version 1.25,
 * Copyright &copy; 2001,2005-2012 Jarno Elonen (elonen@iki.fi, http://iki.fi/elonen/)
 * and Copyright &copy; 2010 Konstantinos Togias (info@ktogias.gr, http://ktogias.gr)
 *
 * <p><b>Features + limitations: </b><ul>
 *
 *    <li> Only one Java file </li>
 *    <li> Java 1.1 compatible </li>
 *    <li> Released as open source, Modified BSD licence </li>
 *    <li> No fixed config files, logging, authorization etc. (Implement yourself if you need them.) </li>
 *    <li> Supports parameter parsing of GET and POST methods (+ rudimentary PUT support in 1.25) </li>
 *    <li> Supports both dynamic content and file serving </li>
 *    <li> Supports file upload (since version 1.2, 2010) </li>
 *    <li> Supports partial content (streaming)</li>
 *    <li> Supports ETags</li>
 *    <li> Never caches anything </li>
 *    <li> Doesn't limit bandwidth, request time or simultaneous connections </li>
 *    <li> Default code serves files and shows all HTTP parameters and headers</li>
 *    <li> File server supports directory listing, index.html and index.htm</li>
 *    <li> File server supports partial content (streaming)</li>
 *    <li> File server supports ETags</li>
 *    <li> File server does the 301 redirection trick for directories without '/'</li>
 *    <li> File server supports simple skipping for files (continue download) </li>
 *    <li> File server serves also very long files without memory overhead </li>
 *    <li> Contains a built-in list of most common mime types </li>
 *    <li> All header names are converted lowercase so they don't vary between browsers/clients </li>
 *
 * </ul>
 *
 * <p><b>Ways to use: </b><ul>
 *
 *    <li> Run as a standalone app, serves files and shows requests</li>
 *    <li> Subclass serve() and embed to your own program </li>
 *    <li> Call serveFile() from serve() with your own base directory </li>
 *
 * </ul>
 *
 * See the end of the source file for distribution license
 * (Modified BSD licence)
 */
public class NanoHTTPD
{
	
	public void start() {
		if (myThread != null) {
			myThread.setDaemon(true);
			myThread.start();
		}
	}
	/**
	 * HTTP response.
	 * Return one of these from serve().
	 */
	public class Response
	{
		/**
		 * Default constructor: response = HTTP_OK, data = mime = 'null'
		 */
		public Response()
		{
			this.status = HTTP_OK;
		}

		/**
		 * Basic constructor.
		 */
		public Response( String status, String mimeType, InputStream data )
		{
			this.status = status;
			this.mimeType = mimeType;
			this.data = data;
		}

		/**
		 * Convenience method that makes an InputStream out of
		 * given text.
		 */
		public Response( String status, String mimeType, String txt )
		{
			this.status = status;
			this.mimeType = mimeType;
			try
			{
				this.data = new ByteArrayInputStream( txt.getBytes("UTF-8"));
			}
			catch ( java.io.UnsupportedEncodingException uee )
			{
				uee.printStackTrace();
			}
		}

		/**
		 * Adds given line to the header.
		 */
		public void addHeader( String name, String value )
		{
			header.put( name, value );
		}

		/**
		 * HTTP status code after processing, e.g. "200 OK", HTTP_OK
		 */
		public String status;

		/**
		 * MIME type of content, e.g. "text/html"
		 */
		public String mimeType;

		/**
		 * Data of the response, may be null.
		 */
		public InputStream data;

		/**
		 * Headers for the HTTP response. Use addHeader()
		 * to add lines.
		 */
		public Properties header = new Properties();
	}

	/**
	 * Some HTTP response status codes
	 */
	public static final String
		HTTP_OK = "200 OK",
		HTTP_PARTIALCONTENT = "206 Partial Content",
		HTTP_RANGE_NOT_SATISFIABLE = "416 Requested Range Not Satisfiable",
		HTTP_REDIRECT = "301 Moved Permanently",
		HTTP_NOTMODIFIED = "304 Not Modified",
		HTTP_FORBIDDEN = "403 Forbidden",
		HTTP_NOTFOUND = "404 Not Found",
		HTTP_BADREQUEST = "400 Bad Request",
		HTTP_INTERNALERROR = "500 Internal Server Error",
		HTTP_NOTIMPLEMENTED = "501 Not Implemented";

	/**
	 * Common mime types for dynamic content
	 */
	public static final String
		MIME_PLAINTEXT = "text/plain",
		MIME_HTML = "text/html",
		MIME_DEFAULT_BINARY = "application/octet-stream",
		MIME_XML = "text/xml";

	// ==================================================
	// Socket & server code
	// ==================================================

	/**
	 * Starts a HTTP server to given port.<p>
	 * Throws an IOException if the socket is already in use
	 */
	private Context ct;
	private String memberid;
	private DeviceDao weiDeviceDao;
	private String remotetime="";
	private String tag = "NanoHTTPD";
	private static int UNSUPORTCHANNEL = 10000;
	public NanoHTTPD( int port, File wwwroot,Context m_ct ) throws IOException
	{
		
		
		Log.d(tag, "-------------------------------------NanoHTTPD ------ 1");
		weiDeviceDao = new DeviceDao(m_ct);
		Log.d(tag, "-------------------------------------NanoHTTPD ------ 2");

		memberid = weiDeviceDao.find();
		myTcpPort = port;
		ct = m_ct;
		
		
		//this.myRootDir = wwwroot;
		myServerSocket = new ServerSocket( myTcpPort );
		myThread = new Thread( new Runnable()
			{
				public void run()
				{
					try
					{
						while( true ){
							Log.e("lyr","===============>>>>>>>>>>>>wait socket");
							new HTTPSession( myServerSocket.accept());
						}
					}
					catch ( IOException ioe )
					{}
				}
			});
		/*myThread.setDaemon( true );
		myThread.start();*/
	}

	/**
	 * Stops the server.
	 */
	public void stop()
	{
		try
		{
			myServerSocket.close();
			myThread.join();
		}
		catch ( IOException ioe ) {}
		catch ( InterruptedException e ) {}
	}
	
	private boolean onkeyAction(String keyString) {
		
//		WeiConstant.mKeyAndMouseControl.setKey((short)KeyMap.parseKeyCode(keyString));
		return true;
	}
	/**
	 * Handles one session, i.e. parses the HTTP request
	 * and returns the response.
	 */
	private class HTTPSession implements Runnable
	{
		public HTTPSession( Socket s )
		{
			mySocket = s;
			Thread t = new Thread( this );
			t.setDaemon( true );
			t.start();
		}

		public void run()
		{
			try
			{
				InputStream is = mySocket.getInputStream();
				
				Log.e(tag,"============================>>>>>>>>>>>>>>>>>>>>");
				if ( is == null) return;

				// Read the first 8192 bytes.
				// The full header should fit in here.
				// Apache's default header limit is 8KB.
				// Do NOT assume that a single read will get the entire header at once!
				final int bufsize = 8192;
				byte[] buf = new byte[bufsize];
				
				int splitbyte = 0;
				int rlen = 0;
				{
					int read = is.read(buf, 0, bufsize);
					while (read > 0)
					{
						rlen += read;
						splitbyte = findHeaderEnd(buf, rlen);
						if (splitbyte > 0)
							break;
						read = is.read(buf, rlen, bufsize - rlen);
					}
				}

				// Create a BufferedReader for parsing the header.
				String headS=new String(buf);
				Log.v(tag, "@@@--------------------------------");
				Log.v(tag, "@@@@@recv http head :"+headS);
				Log.v(tag, "++++++++++++++++++++++++++++++++");
			
				ByteArrayInputStream hbis = new ByteArrayInputStream(buf, 0, rlen);
				
				BufferedReader hin = new BufferedReader( new InputStreamReader( hbis ));
				Properties pre = new Properties();
				Properties parms = new Properties();
				Properties header = new Properties();
				Properties files = new Properties();

				// Decode the header into parms and header java properties
				decodeHeader(hin, pre, parms, header);
				String method = pre.getProperty("method");
				String uri = pre.getProperty("uri");
				if(uri==null)
					return;
				uri = new String(uri.getBytes("iso-8859-1"));
				Log.v(tag,"get uri:"+new String(uri.getBytes("iso-8859-1"))+"\n");
				
				//澶勭悊鎺ユ敹鍒扮殑鍛戒护
				httpcmd(uri);
				
				is.close();
			}
			catch ( IOException ioe )
			{
				try
				{
					sendError( HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
				}
				catch ( Throwable t ) {}
			}
			catch ( InterruptedException ie )
			{
				// Thrown by sendError, ignore and exit the thread.
			}
		}
		/*****澶勭悊鎺ユ敹鍒扮殑鍛戒护*****/
		private void httpcmd(String uri){
			/*http://host:port/wechat/remote/up/闅忔満鏁�			  http://host:port/wechat/remote/down/闅忔満鏁�			  http://host:port/wechat/check/闅忔満鏁�			  http://HOST:PORT/wechat/media/player/albumId/vrsAlbumId/vrsTvId/vrsChnId/history/customer/device/Math.random()
			  http://HOST:PORT/wechat/media/player/videoid/videotype/videouistyle/Math.random()*/	
			
			String uritmp = uri.substring(uri.indexOf("/")+1);//鍘绘帀绗竴涓�绗﹀彿
			String[] cmdStr=uritmp.split("/");
			for(int i =0;i<cmdStr.length;i++)
				Log.i(tag,"cmdStr["+i+"]="+cmdStr[i]);		
			if(cmdStr[1].equals("check")){
					//////////////鏀跺埌鍛戒护缁欏鎴风鍥炲鍝嶅簲
					if(memberid==null||memberid.equals(""))
						memberid = weiDeviceDao.find();
					JSONObject jsonObject = new JSONObject();
					try {										
						jsonObject.put("error", false);
						jsonObject.put("msg", memberid);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String responseString  = "success_jsonpCallback('"+jsonObject.toString()+"')";
					Log.v(tag, "responseString :"+responseString);
					sendResponse( HTTP_OK, MIME_PLAINTEXT, null, new ByteArrayInputStream( responseString.getBytes()));
					/////////////////////////
			}else if(cmdStr[1].equals("remote")){
				Log.i(tag,"remotetime="+remotetime);
				if(!remotetime.equals(cmdStr[3])){
				 onkeyAction(cmdStr[2]);
				 remotetime = cmdStr[3];		
					/////////////////////////
				}
				 String responseString  = "success_jsonpCallback('')";
				 Log.v(tag, "responseString :"+responseString);
				 sendResponse( HTTP_OK, MIME_PLAINTEXT, null, new ByteArrayInputStream( responseString.getBytes()));
			}else if(cmdStr[1].equals("media")){

				JSONObject jsonObject_media = new JSONObject();
				try {										
					jsonObject_media.put("error", false);
					jsonObject_media.put("result", true);
					jsonObject_media.put("msg", "null");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String responseString  = "success_jsonpCallbackDb('"+jsonObject_media.toString()+"')";
				Log.v(tag, "responseString :"+responseString);
				sendResponse( HTTP_OK, MIME_PLAINTEXT, null, new ByteArrayInputStream( responseString.getBytes()));
				
//				Log.i(tag,"sys.scan.state="+SystemProperties.get("sys.scan.state").equals("on"));
//				if(SystemProperties.get("sys.scan.state").equals("on")){
//					Log.i(tag,"sys.scan,---return ");

					return;
				}
				
				//瑙嗛绫伙紝濡傛灉褰撳墠鍦═V妯″紡涓嬶紝闇�鍒囨崲淇℃簮
//				preparePlay(uri);
				
				 
				
			}
		}
		 
		 
		Handler handler = new Handler(ct.getMainLooper()){
		       @Override
		       public void handleMessage(Message msg) {
		           // TODO Auto-generated method stub
		    	 Log.i(tag, "handleMessage  ---------  msg =  "+msg.what);
		    	 if(msg.what == UNSUPORTCHANNEL){
//		    		 TCLToast.makeText(ct, ct.getString(R.string.unsupportchannel), Toast.LENGTH_LONG).show();
		    		 return;
		    	 }
//		    	 if (msg.what == EnTCLCallBackSetSourceMsg.EN_TCL_SET_SOURCE_END_SUCCEED.ordinal()) {
//		    		 Log.v(tag, "淇℃簮鍒囨崲鎴愬姛");
//		   			starttoplay(urlStr);
//						
//		    	 }else if(msg.what == EnTCLCallBackSetSourceMsg.EN_TCL_SET_SOURCE_END_FAILED.ordinal()){
//		    		 //TCLToast.makeText(ct, "鏃犳硶鎾斁锛屽垏鎹俊婧愬け璐�, Toast.LENGTH_SHORT).show();
//		    		
//		    	 }else if(msg.what == EnTCLCallBackSetSourceMsg.EN_TCL_SET_SOURCE_START.ordinal()){
//		    		
//		    		 return;
//		    	 }
		    	 
		    	//閲婃斁handler;
//					TTvManager.getInstance(ct).releaseHandler(TTvUtils.TV_HANDLER_INDEX_TV_SET_SOURCE); 
		       }
		    };
		/**
		 * Decodes the sent headers and loads the data into
		 * java Properties' key - value pairs
		**/
		private  void decodeHeader(BufferedReader in, Properties pre, Properties parms, Properties header)
			throws InterruptedException
		{
			try {
				// Read the request line	
				String inLine = in.readLine();
				if (inLine == null) return;
				StringTokenizer st = new StringTokenizer( inLine );
				if ( !st.hasMoreTokens())
					sendError( HTTP_BADREQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html" );

				String method = st.nextToken();
				pre.put("method", method);

				if ( !st.hasMoreTokens())
					sendError( HTTP_BADREQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html" );

				String uri = st.nextToken();

				// Decode parameters from the URI
				int qmi = uri.indexOf( '?' );
				if ( qmi >= 0 )
				{
					decodeParms( uri.substring( qmi+1 ), parms );
					uri = decodePercent( uri.substring( 0, qmi ));
				}
				else uri = decodePercent(uri);

				// If there's another token, it's protocol version,
				// followed by HTTP headers. Ignore version but parse headers.
				// NOTE: this now forces header names lowercase since they are
				// case insensitive and vary by client.
				if ( st.hasMoreTokens())
				{
					String line = in.readLine();
					while ( line != null && line.trim().length() > 0 )
					{
						int p = line.indexOf( ':' );
						if ( p >= 0 )
							header.put( line.substring(0,p).trim().toLowerCase(), line.substring(p+1).trim());
						line = in.readLine();
					}
				}

				pre.put("uri", uri);
			}
			catch ( IOException ioe )
			{
				sendError( HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
			}
		}

		
		/**
		 * Find byte index separating header from body.
		 * It must be the last byte of the first two sequential new lines.
		**/
		private int findHeaderEnd(final byte[] buf, int rlen)
		{
			int splitbyte = 0;
			while (splitbyte + 3 < rlen)
			{
				if (buf[splitbyte] == '\r' && buf[splitbyte + 1] == '\n' && buf[splitbyte + 2] == '\r' && buf[splitbyte + 3] == '\n')
					return splitbyte + 4;
				splitbyte++;
			}
			return 0;
		}


		/**
		 * Decodes the percent encoding scheme. <br/>
		 * For example: "an+example%20string" -> "an example string"
		 */
		private String decodePercent( String str ) throws InterruptedException
		{
			try
			{
				StringBuffer sb = new StringBuffer();
				for( int i=0; i<str.length(); i++ )
				{
					char c = str.charAt( i );
					switch ( c )
					{
						case '+':
							sb.append( ' ' );
							break;
						case '%':
							sb.append((char)Integer.parseInt( str.substring(i+1,i+3), 16 ));
							i += 2;
							break;
						default:
							sb.append( c );
							break;
					}
				}
				return sb.toString();
			}
			catch( Exception e )
			{
				sendError( HTTP_BADREQUEST, "BAD REQUEST: Bad percent-encoding." );
				return null;
			}
		}

		/**
		 * Decodes parameters in percent-encoded URI-format
		 * ( e.g. "name=Jack%20Daniels&pass=Single%20Malt" ) and
		 * adds them to given Properties. NOTE: this doesn't support multiple
		 * identical keys due to the simplicity of Properties -- if you need multiples,
		 * you might want to replace the Properties with a Hashtable of Vectors or such.
		 */
		private void decodeParms( String parms, Properties p )
			throws InterruptedException
		{
			if ( parms == null )
				return;

			StringTokenizer st = new StringTokenizer( parms, "&" );
			while ( st.hasMoreTokens())
			{
				String e = st.nextToken();
				int sep = e.indexOf( '=' );
				if ( sep >= 0 )
					p.put( decodePercent( e.substring( 0, sep )).trim(),
						   decodePercent( e.substring( sep+1 )));
			}
		}

		/**
		 * Returns an error message as a HTTP response and
		 * throws InterruptedException to stop further request processing.
		 */
		private void sendError( String status, String msg ) throws InterruptedException
		{
			sendResponse( status, MIME_PLAINTEXT, null, new ByteArrayInputStream( msg.getBytes()));
			throw new InterruptedException();
		}

		/**
		 * Sends given response to the socket.
		 */
		private void sendResponse( String status, String mime, Properties header, InputStream data )
		{
			try
			{
				if ( status == null )
					throw new Error( "sendResponse(): Status can't be null." );

				OutputStream out = mySocket.getOutputStream();
				PrintWriter pw = new PrintWriter( out );
				pw.print("HTTP/1.0 " + status + " \r\n");

				if ( mime != null )
					pw.print("Content-Type: " + mime + "\r\n");

				if ( header == null || header.getProperty( "Date" ) == null )
					pw.print( "Date: " + gmtFrmt.format( new Date()) + "\r\n");

				if ( header != null )
				{
					Enumeration e = header.keys();
					while ( e.hasMoreElements())
					{
						String key = (String)e.nextElement();
						String value = header.getProperty( key );
						pw.print( key + ": " + value + "\r\n");
					}
				}

				pw.print("\r\n");
				pw.flush();

				if ( data != null )
				{
					int pending = data.available();	// This is to support partial sends, see serveFile()
					byte[] buff = new byte[theBufferSize];
					while (pending>0)
					{
						int read = data.read( buff, 0, ( (pending>theBufferSize) ?  theBufferSize : pending ));
						if (read <= 0)	break;
						out.write( buff, 0, read );
						pending -= read;
					}
				}
				Log.v("lyr", "&&&&&&&&&&&&out.flush() :");
				out.flush();
				out.close();
				if ( data != null )
					data.close();
			}
			catch( IOException ioe )
			{
				// Couldn't write? No can do.
				try { mySocket.close(); } catch( Throwable t ) {}
			}
		}

		private Socket mySocket;
	

	private int myTcpPort;
	private final ServerSocket myServerSocket;
	private Thread myThread;


	private static int theBufferSize = 16 * 1024;

	// Change these if you want to log to somewhere else than stdout
	protected static PrintStream myOut = System.out; 
	protected static PrintStream myErr = System.err;

	/**
	 * GMT date formatter
	 */
	private static java.text.SimpleDateFormat gmtFrmt;
	static
	{
		gmtFrmt = new java.text.SimpleDateFormat( "E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
}

