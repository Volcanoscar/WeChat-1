package com.tcl.wechat.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * 文件处理帮助类
 * @author rex.lei
 *
 */
public class FileTools{

    /**
     * 拷贝文件
     * @param from 原文件 
     * @param to 目标文件
     * @return true:拷贝成功 false:拷贝失败
     */
    public static boolean copySystemDir(File from, File to) {

        try {
            FileUtils.copyDirectory(from, to);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 拷贝文件
     * @param from
     * @param to
     * @return
     */
    public static boolean copySystemDir(String from, String to) {
        return copySystemDir(new File(from), new File(to));
    }
    
    public static boolean cleanDirectory(File dir) {
        try {
            FileUtils.cleanDirectory(dir);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean copyFile(String from,String to){
        return copyFile(new File(from),new File(to));
    }
    
    public static boolean copyFile(File from, File to) {

        try {
            FileUtils.copyFile(from, to);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
