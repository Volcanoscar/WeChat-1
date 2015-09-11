package com.tcl.wechat.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;


public class ExpressionUtil {
	
	private  final static String TAG = "ExpressUtil";
	
	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 * @param context
	 * @param spannableString
	 * @param patten
	 * @param start
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
    public void dealExpression(Context context,SpannableString spannableString, Pattern patten, int start) throws SecurityException, NoSuchFieldException, NumberFormatException, IllegalArgumentException, IllegalAccessException {
    	Matcher matcher = patten.matcher(spannableString);
//      Log.i(TAG, "------dealExpression---------");
//    	Log.i(TAG, "SpannableString = " + spannableString);
    	int indexOfKey = -1;
    	while (matcher.find()) {
            String key = matcher.group();
//            Log.i(TAG, "key = " + key);
            if (matcher.start() < start) {
                continue;
            }
            indexOfKey = indexOf(key);
//          Log.d(TAG, "indexOf(key) = " + indexOf(key));
            if(indexOfKey == -1) {
            	continue;
            }else{
//            	Field field = R.drawable.class.getDeclaredField("smiley_" + indexOfKey);
//				int resId = Integer.parseInt(field.get(null).toString());		//通过上面匹配得到的字符串来生成图片资源id
//	            if (resId != 0) {
//	                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);	
//	                ImageSpan imageSpan = new ImageSpan(context,bitmap,ImageSpan.ALIGN_BASELINE);				//通过图片资源id来得到bitmap，用一个ImageSpan来包装
//	                int end = matcher.start() + key.length();					//计算该图片名字的长度，也就是要替换的字符串的长度
//	                spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);	//将该图片替换字符串中规定的位置中
////	                if (end < spannableString.length()) {						//如果整个字符串还未验证完，则继续。。
////	                    dealExpression(context,spannableString,  patten, end);
////	                }
////	                break;
//	            }
            }
        }
    }
    
    /**
     * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
     * @param context
     * @param str
     * @return
     */
    public SpannableString getExpressionString(Context context,String str,String zhengze){
//    	Log.i(TAG, "str = " + str);
    	SpannableString spannableString = new SpannableString(str);
    	//通过传入的正则表达式来生成一个pattern,默认区分大小写，可以添加flag
        Pattern sinaPatten = Pattern.compile(zhengze);
        try {
            dealExpression(context,spannableString, sinaPatten, 0);
        } catch (Exception e) {
            Log.e("dealExpression", e.getMessage());
        }
        return spannableString;
    }
    
    
    /**匹配Emoji表情符，用于转化为资源名.顺序排列，不能打乱*/
    public final static String[] QQ_STRINGS = {"/::)","/::~","/::B","/::|","/:8-)",
    	"/::<","/::$","/::X","/::Z","/::'(","/::-|","/::@","/::P","/::D","/::O","/::(","/::+","/:--b",
    	"/::Q","/::T","/:,@P","/:,@-D","/::d","/:,@o","/::g","/:|-)","/::!","/::L",
    	"/::>","/::,@","/:,@f","/::-S","/:?","/:,@x","/:,@@","/::8","/:,@!","/:!!!",
    	"/:xx","/:bye","/:wipe","/:dig","/:handclap","/:&-(","/:B-)","/:<@","/:@>","/::-O",
    	"/:>-|","/:P-(","/::'|","/:X-)","/::*","/:@x","/:8*","/:pd","/:<W>","/:beer",
    	"/:basketb","/:oo","/:coffee","/:eat","/:pig","/:rose","/:fade","/:showlove","/:heart","/:break",
    	"/:cake","/:li","/:bome","/:kn","/:footb","/:ladybug","/:shit","/:moon","/:sun","/:gift",
    	"/:hug","/:strong","/:weak","/:share","/:v","/:@)","/:jj","/:@@","/:bad","/:lvu",
    	"/:no","/:ok","/:love","/:<L>","/:jump","/:shake","/:<O>","/:circle","/:kotow","/:turn",
    	"/:skip","/:oY","/:#-0","/:hiphot","/:kiss","/:<&","/:&>"}; 
    
    /**
     * 找到Emoji表情字符在QQ_STRINGS中的序号
     * @param outqqStr
     * @return int 序号
     */
    public int indexOf(String outqqStr){
    	int p = 0;
    	for (String inqqStr : QQ_STRINGS) {
			if (inqqStr.equals(outqqStr)) {
//				Log.i(TAG,"p = " + p);
				return p;
			}
			p++;
		}
//    	Log.i(TAG,"p = -1");
    	return -1;
    }
	

}