package com.tcl.wechat.utils.http;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

public class HttpMultipartEntity extends MultipartEntity {

	private ProgressListener mListener;
	
	public interface ProgressListener {
		public void onProgressUpdate(int progress);
	}
	
	public void setProgressListener(ProgressListener listener){
		mListener = listener;
	}

	public HttpMultipartEntity() {
		super();
	}


	public HttpMultipartEntity(HttpMultipartMode mode, String boundary,
			Charset charset) {
		super(mode, boundary, charset);
	}


	public HttpMultipartEntity(HttpMultipartMode mode) {
		super(mode);
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream));
	}


	public class CountingOutputStream extends FilterOutputStream {
		
		private int mProgress;

		public CountingOutputStream(final OutputStream out) {
			super(out);
			this.mProgress = 0;
		}

		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			mProgress += len;
			if (mListener != null){
				mListener.onProgressUpdate(mProgress);
			}
		}

		public void write(int b) throws IOException {
			out.write(b);
			mProgress ++;
			if (mListener != null){
				mListener.onProgressUpdate(mProgress);
			}
		}
	}

}
