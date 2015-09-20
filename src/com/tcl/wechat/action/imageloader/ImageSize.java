package com.tcl.wechat.action.imageloader;

/**
 * 图片尺寸
 * @author rex.lei
 *
 */
public class ImageSize {

	private int width;
	
	private int height;

	public ImageSize() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ImageSize(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public String toString() {
		return "ImageSize [width=" + width + ", height=" + height + "]";
	}
}
