package com.tcl.wechat.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.database.DeviceDao;

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
public class NanoHTTPD{
	
	/**
	 * HTTP response.
	 * Return one of these from serve().
	 */
	public class Response{
		
		/**
		 * Default constructor: response = HTTP_OK, data = mime = 'null'
		 */
		public Response(){
			this.status = HTTP_OK;
		}

		/**
		 * Basic constructor.
		 */
		public Response( String status, String mimeType, InputStream data ){
			this.status = status;
			this.mimeType = mimeType;
			this.data = data;
		}

		/**
		 * Convenience method that makes an InputStream out of
		 * given text.
		 */
		public Response( String status, String mimeType, String txt ) {
			this.status = status;
			this.mimeType = mimeType;
			try {
				this.data = new ByteArrayInputStream( txt.getBytes("UTF-8"));
			} catch ( java.io.UnsupportedEncodingException uee ) {
				uee.printStackTrace();
			}
		}

		/**
		 * Adds given line to the header.
		 */
		public void addHeader( String name, String value ) {
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

	private static final String TAG = NanoHTTPD.class.getSimpleName();
	
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

	/**
	 * Socket
	 */
	private int myTcpPort;
	private Socket mySocket;
	private ServerSocket myServerSocket;
	private Thread myThread;
	private static int theBufferSize = 16 * 1024;

	// ==================================================
	// Socket & server code
	// ==================================================

	/**
	 * Starts a HTTP server to given port.<p>
	 * Throws an IOException if the socket is already in use
	 */
	private String memberid;
	private String remotetime = "";
	
	public NanoHTTPD(int port) throws IOException {
		
		myTcpPort = port;
		memberid = DeviceDao.getInstance().getMemberId();
		
		myServerSocket = new ServerSocket(myTcpPort);
		myThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					while (true) {
						new HTTPSession(myServerSocket.accept());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Start the Server
	 */
	public void start() {
		
		if (myThread != null) {
			myThread.setDaemon(true);
			myThread.start();
		}
	}
	
	/**
	 * Stop the server.
	 */
	public void stop(){
		try{
			myServerSocket.close();
			myThread.join();
		}catch ( IOException ioe ) {
			ioe.printStackTrace();
		} catch ( InterruptedException e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles one session, i.e. parses the HTTP request
	 * and returns the response.
	 */
	private class HTTPSession implements Runnable{
		
		public HTTPSession(Socket socket ){
			mySocket = socket;
			Thread thread = new Thread();
			thread.setDaemon(true);
			thread.start();
		}

		@Override
		public void run() {
			
			InputStream is = null;
			BufferedReader reader = null;
			try {
				is = mySocket.getInputStream();
				if (is == null){
					return ;
				}
				
				// Read the first 8192 bytes.
				// The full header should fit in here.
				// Apache's default header limit is 8KB.
				// Do NOT assume that a single read will get the entire header at once!
				final int bufferSize = 8192;
				byte[] buffer = new byte[bufferSize];
				int splitbyte = 0;
				int rlen = 0;
				int readLen = is.read(buffer, 0, bufferSize);
				while (readLen > 0) {
					rlen += readLen;
					splitbyte = findHeaderEnd(buffer, rlen);
					if (splitbyte > 0){
						break;
					}
					readLen = is.read(buffer, rlen, bufferSize - rlen);
				}
				
				// Create a BufferedReader for parsing the header.
				reader = new BufferedReader(new InputStreamReader(
						new ByteArrayInputStream(buffer, 0, rlen)));
				
				Properties pre = new Properties();
				Properties parms = new Properties();
				Properties header = new Properties();
//				Properties files = new Properties();
				
				// Decode the header into parms and header java properties
				decodeHeader(reader, pre, parms, header);
//				String method = pre.getProperty("method");
				String uri = pre.getProperty("uri");
				if(uri == null){
					return;
				}
				uri = new String(uri.getBytes("iso-8859-1"));
				httpcmd(uri);
			} catch (IOException e) {
				try {
					sendError( HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + e.getMessage());
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			} finally {
				try {
					if (reader != null){
						reader.close();
					}
					if (is != null){
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void httpcmd(String uri){
		Log.i(TAG, "[httpcmd]uri:" + uri);
		
		String uritmp = uri.substring(uri.indexOf("/")+1);
		String[] cmdStr=uritmp.split("/");
		if("check".equals(cmdStr[1])){
			memberid = DeviceDao.getInstance().getMemberId();
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("error", false);
				jsonObject.put("msg", memberid);
				
				String responseString  = "success_jsonpCallback('" + jsonObject.toString() + "')";
				Log.i(TAG, "responseString:" + responseString);
				sendResponse( HTTP_OK, MIME_PLAINTEXT, null, new ByteArrayInputStream(responseString.getBytes()));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if ("remote".equals(cmdStr[1])){
			Log.i(TAG,"remotetime =" + remotetime);
			if(remotetime != null && !remotetime.equals(cmdStr[3])){
				remotetime = cmdStr[3];		
			}
			
			String responseString  = "success_jsonpCallback('')";
			Log.i(TAG, "responseString :"+responseString);
			sendResponse(HTTP_OK, MIME_PLAINTEXT, null, new ByteArrayInputStream( responseString.getBytes()));
		} else if ("media".equals(cmdStr[1])){
			JSONObject jsonObject_media = new JSONObject();
			try {										
				jsonObject_media.put("error", false);
				jsonObject_media.put("result", true);
				jsonObject_media.put("msg", "null");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			String responseString  = "success_jsonpCallbackDb('" + jsonObject_media.toString() + "')";
			Log.i(TAG, "responseString :" + responseString);
			sendResponse( HTTP_OK, MIME_PLAINTEXT, null, new ByteArrayInputStream( responseString.getBytes()));
		}
		
	}
	
	/**
	 * Decodes the sent headers and loads the data into
	 * java Properties' key - value pairs
	 * @param in
	 * @param pre
	 * @param parms
	 * @param header
	 * @throws InterruptedException
	 */
	private  void decodeHeader(BufferedReader in, Properties pre, Properties parms, Properties header) {
		if (in == null){
			return ;
		}
		try {
			String readLine = in.readLine();
			if (readLine == null){
				return ;
			}
			StringTokenizer tokenizer = new StringTokenizer(readLine);
			if (!tokenizer.hasMoreTokens()){
				sendError( HTTP_BADREQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html" );
			}
			
			String method = tokenizer.nextToken();
			pre.put("method", method);
			if (!tokenizer.hasMoreTokens()){
				sendError( HTTP_BADREQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html" );
			}
			
			String uri = tokenizer.nextToken();
			int qmi = uri.indexOf( '?' );
			if ( qmi >= 0 ) {
				decodeParms( uri.substring( qmi+1 ), parms );
				uri = decodePercent( uri.substring( 0, qmi ));
			} else {
				uri = decodePercent(uri);
			}
			
			// If there's another token, it's protocol version,
			// followed by HTTP headers. Ignore version but parse headers.
			// NOTE: this now forces header names lowercase since they are
			// case insensitive and vary by client.
			if (tokenizer.hasMoreTokens()) {
				String line = in.readLine();
				while ( line != null && line.trim().length() > 0 ){
					int p = line.indexOf( ':' );
					if ( p >= 0 ){
						header.put( line.substring(0,p).trim().toLowerCase(), line.substring(p+1).trim());
					}
					line = in.readLine();
				}
			}
			pre.put("uri", uri);
		} catch (IOException e) {
			try {
				sendError( HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + e.getMessage());
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Find byte index separating header from body.
	 * It must be the last byte of the first two sequential new lines.
	**/
	private int findHeaderEnd(final byte[] buf, int rlen){
		int splitbyte = 0;
		while (splitbyte + 3 < rlen) {
			if (buf[splitbyte] == '\r' && buf[splitbyte + 1] == '\n' && buf[splitbyte + 2] == '\r' && buf[splitbyte + 3] == '\n'){
				return splitbyte + 4;
			}
			splitbyte++;
		}
		return 0;
	}
		
	/**
	 * Decodes the percent encoding scheme. <br/>
	 * For example: "an+example%20string" -> "an example string"
	 */
	private String decodePercent( String str ){
		if (TextUtils.isEmpty(str)){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for( int i=0; i < str.length(); i++ ){
			char c = str.charAt( i );
			switch (c) {
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
	/**
	 * Decodes parameters in percent-encoded URI-format
	 * ( e.g. "name=Jack%20Daniels&pass=Single%20Malt" ) and
	 * adds them to given Properties. NOTE: this doesn't support multiple
	 * identical keys due to the simplicity of Properties -- if you need multiples,
	 * you might want to replace the Properties with a Hashtable of Vectors or such.
	 */
	private void decodeParms(String parms, Properties properties){
		if (TextUtils.isEmpty(parms)){
			return ;
		}
		StringTokenizer tokenizer = new StringTokenizer( parms, "&" );
		while ( tokenizer.hasMoreTokens()){
			String e = tokenizer.nextToken();
			int sep = e.indexOf('=');
			if ( sep >= 0 ){
				properties.put( decodePercent( e.substring( 0, sep )).trim(),
						   decodePercent( e.substring( sep+1 )));
			}
		}
	}
	
	/**
	 * Returns an error message as a HTTP response and
	 * throws InterruptedException to stop further request processing.
	 * @throws InterruptedException 
	 */
	private void sendError( String status, String msg ) throws InterruptedException{
		sendResponse( status, MIME_PLAINTEXT, null, new ByteArrayInputStream( msg.getBytes()));
		throw new InterruptedException();
	}
	
	/**
	 * Sends given response to the socket.
	 * @param status
	 * @param mime
	 * @param header
	 * @param data
	 */
	private void sendResponse( String status, String mime, Properties header, InputStream inputStream){
		if (TextUtils.isEmpty(status)){
			throw new Error( "sendResponse(): Status can't be null." );
		}
		OutputStream outputStream = null;
		PrintWriter writer = null;
		try {
			outputStream = mySocket.getOutputStream();
			writer = new PrintWriter(outputStream);
			writer.print("HTTP/1.0 " + status + " \r\n");
			
			if ( mime != null ){
				writer.print("Content-Type: " + mime + "\r\n");
			}
			
			if (header == null || header.getProperty("Date") == null){
				writer.print( "Date: " + gmtFrmt.format( new Date()) + "\r\n");
			}
			
			if (header != null){
				Enumeration<Object> e = header.keys();
				while (e.hasMoreElements()){
					String key = (String)e.nextElement();
					String value = header.getProperty( key );
					writer.print( key + ": " + value + "\r\n");
				}
			}
			writer.print("\r\n");
			writer.flush();
			
			if (inputStream != null){

				int pending = inputStream.available();	// This is to support partial sends, see serveFile()
				byte[] buff = new byte[theBufferSize];
				while (pending>0)
				{
					int read = inputStream.read( buff, 0, ((pending>theBufferSize) ? theBufferSize : pending ));
					if (read <= 0)	break;
					outputStream.write( buff, 0, read );
					pending -= read;
				}
			}
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null){
					outputStream.close();
				}
				if (writer != null){
					writer.close();
				}
				if (inputStream != null){
					inputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

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

