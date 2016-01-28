package com.tcl.wechat.model.file;

import com.tcl.wechat.model.IData.IData;
import com.tcl.wechat.model.IData.ModelImpl;

/**
 * 文件解析器
 * @author rex.lei
 *
 */
public class FileParser {
	
	
	/**
	 * 解析文件
	 * @param jsonData
	 * @return
	 */
	public static FileInfo parseFile(String jsonData){
		FileInfo fileInfo = null;
		ModelImpl mode = new ModelImpl(FileInfo.class);
		IData result = mode.doParser(jsonData);
		if (result instanceof FileInfo){
			fileInfo = (FileInfo) result;
		}
		return fileInfo;
	}
	
	/**
	 * 解析音乐文件
	 * @param jsonData
	 * @return
	 */
	public static MusicFileInfo parseMusicFile(String jsonData){
		MusicFileInfo musicFileInfo = null;
		ModelImpl mode = new ModelImpl(MusicFileInfo.class);
		IData result = mode.doParser(jsonData);
		if (result instanceof MusicFileInfo){
			musicFileInfo = (MusicFileInfo) result;
		}
		return musicFileInfo;
	}
	
}
