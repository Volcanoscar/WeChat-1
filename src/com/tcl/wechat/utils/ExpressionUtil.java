package com.tcl.wechat.utils;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

/**
 * 表情解析工具类
 * @author Administrator
 *
 */
public class ExpressionUtil {
	
	private DecimalFormat decimalFormat = new DecimalFormat("000");
	
	private static class ExpressionUtilaInstance{
		private static final ExpressionUtil mInstance = new ExpressionUtil();
	}
	
	private ExpressionUtil() {
		super();
	}

	public static ExpressionUtil getInstance(){
		return  ExpressionUtilaInstance.mInstance;
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
     * 表情转化为字符串
     * @param index
     * @return
     */
    public String smileyParser(String content){
    	while (content.contains("#[") && content.contains("]#")) {
			String tempString = content;
			int start = tempString.indexOf("#[");
			int end = tempString.indexOf("]#");
			String smiley = tempString.substring(start + 2, end);
			String key = smiley.substring(smiley.indexOf("_") + 1, smiley.indexOf("."));
			int indexOfKey = Integer.parseInt(key);
			
			if (indexOfKey > -1 && indexOfKey < QQ_STRINGS.length){
				smiley = "<![CDATA[" + QQ_STRINGS[indexOfKey] + "]]>";
			} 
			String repalce = tempString.substring(start, end + 2);
			content = tempString.replace(repalce, smiley);
		}
    	return content;
    }
    
    /**
     * 表情字符串转化为表情
     * @param content
     * @param context
     * @return
     */
    public CharSequence StringToSpannale(Context context, StringBuffer content){
		CharSequence contentCharSeq = content.toString();
		//如果新收到的聊天内容中有/:标识，则进行表情字符匹配
		Pattern p = Pattern.compile("/:");
		Matcher m = p.matcher(content.toString());
		if (m.find()) {
			//判断QQ表情的正则表达式 
			String qqfaceRegex = "/::\\)|/::~|/::B|/::\\||/:8-\\)|/::<|/::\\$|/::X|/::Z|/::'\\(|/::-\\||/::@|/::P|/::D|/::O|/::\\(|/::\\+|/:--b|/::Q|/::T|/:,@P|/:,@-D|/::d|/:,@o|/::g|/:\\|-\\)|/::!|/::L|/::>|/::,@|/:,@f|/::-S|/:\\?|/:,@x|/:,@@|/::8|/:,@!|/:!!!|/:xx|/:bye|/:wipe|/:dig|/:handclap|/:&-\\(|/:B-\\)|/:<@|/:@>|/::-O|/:>-\\||/:P-\\(|/::'\\||/:X-\\)|/::\\*|/:@x|/:8\\*|/:pd|/:<W>|/:beer|/:basketb|/:oo|/:coffee|/:eat|/:pig|/:rose|/:fade|/:showlove|/:heart|/:break|/:cake|/:li|/:bome|/:kn|/:footb|/:ladybug|/:shit|/:moon|/:sun|/:gift|/:hug|/:strong|/:weak|/:share|/:v|/:@\\)|/:jj|/:@@|/:bad|/:lvu|/:no|/:ok|/:love|/:<L>|/:jump|/:shake|/:<O>|/:circle|/:kotow|/:turn|/:skip|/:oY|/:#-0|/:hiphot|/:kiss|/:<&|/:&>";
			SpannableString spannableContent = getExpressionString(context, content.toString(), qqfaceRegex);//
			contentCharSeq = spannableContent;
		}
		return contentCharSeq;
	}
    
    
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
    public void dealExpression(Context context, SpannableString spannableString, Pattern patten, int start) throws SecurityException, NoSuchFieldException, NumberFormatException, IllegalArgumentException, IllegalAccessException {
    	Matcher matcher = patten.matcher(spannableString);
    	int indexOfKey = -1;
    	while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < start) {
                continue;
            }
            indexOfKey = indexOf(key);
            if(indexOfKey == -1) {
            	continue;
            }else{
				try {
					Bitmap mBitmap = BitmapFactory.decodeStream(context.getAssets()
							.open("face/png/smiley_" + decimalFormat.format(indexOfKey) + ".png"));
					if (mBitmap != null) {
						//通过图片资源id来得到bitmap，用一个ImageSpan来包装
					    ImageSpan imageSpan = new ImageSpan(context, mBitmap,ImageSpan.ALIGN_BASELINE);
					  	//计算该图片名字的长度，也就是要替换的字符串的长度
					    int end = matcher.start() + key.length();			
					    //将该图片替换字符串中规定的位置中
					    spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);	
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
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
    	SpannableString spannableString = new SpannableString(str);
    	//通过传入的正则表达式来生成一个pattern,默认区分大小写，可以添加flag
        Pattern sinaPatten = Pattern.compile(zhengze);
        try {
            dealExpression(context,spannableString, sinaPatten, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;
    }
    
    
    /**
     * 找到Emoji表情字符在QQ_STRINGS中的序号
     * @param outqqStr
     * @return int 序号
     */
    public int indexOf(String outqqStr){
    	int p = 0;
    	for (String inqqStr : QQ_STRINGS) {
			if (inqqStr.equals(outqqStr)) {
				return p;
			}
			p++;
		}
    	return -1;
    }
}