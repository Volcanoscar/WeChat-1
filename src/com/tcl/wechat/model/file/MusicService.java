package com.tcl.wechat.model.file;

public class MusicService{
	
	private WXMsgMusicFile wxmsg_music;

	public MusicService() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MusicService(WXMsgMusicFile wxmsg_music) {
		super();
		this.wxmsg_music = wxmsg_music;
	}

	public WXMsgMusicFile getWxmsg_music() {
		return wxmsg_music;
	}

	public void setWxmsg_music(WXMsgMusicFile wxmsg_music) {
		this.wxmsg_music = wxmsg_music;
	}

	@Override
	public String toString() {
		return "MusicService [wxmsg_music=" + wxmsg_music + "]";
	}
	
}