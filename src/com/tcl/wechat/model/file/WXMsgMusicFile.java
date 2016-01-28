package com.tcl.wechat.model.file;

public class WXMsgMusicFile{
	
	private String title;
	private String artist;
	private String url;
	private String low_url;
	private String data_url;
	private String low_data_url;
	private String from_appname;
	
	public WXMsgMusicFile() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WXMsgMusicFile(String title, String artist, String url, String low_url,
			String data_url, String low_data_url, String from_appname) {
		super();
		this.title = title;
		this.artist = artist;
		this.url = url;
		this.low_url = low_url;
		this.data_url = data_url;
		this.low_data_url = low_data_url;
		this.from_appname = from_appname;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLow_url() {
		return low_url;
	}

	public void setLow_url(String low_url) {
		this.low_url = low_url;
	}

	public String getData_url() {
		return data_url;
	}

	public void setData_url(String data_url) {
		this.data_url = data_url;
	}

	public String getLow_data_url() {
		return low_data_url;
	}

	public void setLow_data_url(String low_data_url) {
		this.low_data_url = low_data_url;
	}

	public String getFrom_appname() {
		return from_appname;
	}

	public void setFrom_appname(String from_appname) {
		this.from_appname = from_appname;
	}

	@Override
	public String toString() {
		return "WXMsgMusic [title=" + title + ", artist=" + artist + ", url="
				+ url + ", low_url=" + low_url + ", data_url=" + data_url
				+ ", low_data_url=" + low_data_url + ", from_appname="
				+ from_appname + "]";
	}
}