package com.tcl.wechat.xmpp;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

/**
 * 终端携带令牌登录接入服务�?
 * @author junjian
 *
 */
public class UserLoginIQ extends IQ {
	
	private String content;
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UserLoginIQ() {
		
	}
	
	public UserLoginIQ(String content) {
		this.content = content;
	}
	@Override
	public String getChildElementXML() {
		String messageString = this.content;
        return messageString;
	}
	
	/**
     * An IQProvider for SparkVersion packets.
     *
     * @author lixw
     */
    public static class Provider implements IQProvider {

		@Override
		public IQ parseIQ(XmlPullParser arg0) throws Exception {
//			<iq type="result" id="Wq1x1-1">
//			<auth xmlns="tcl:im:portal">
//			<errorcode>0</errorcode>
//			</auth>
//			</iq>

			UserLoginIQ version = new UserLoginIQ();

            return version;
		}
    	
    }
}
