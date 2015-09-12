package com.tcl.wechat.xml;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.tcl.wechat.modle.AppInfo;



/**
 * @author zongss 解析系统中的配置文件，并生成预安装应用列表
 * 
 * 
 */
public class ParseConfigXml
{
	private String tag ="ParseConfigXml";
	public ParseConfigXml(List<AppInfo> list)
	{
		appInfos = list;
	}

	private List<AppInfo> appInfos = new ArrayList<AppInfo>();

	public List<AppInfo> getSysXmlAppList(String xml)
	{
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance(); // 创建一个SAXParserFactory
			XMLReader reader = factory.newSAXParser().getXMLReader();
			reader.setContentHandler(new MyConentHandler());
			reader.parse(new InputSource(new StringReader(xml)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	class MyConentHandler extends DefaultHandler
	{
		AppInfo appinfo = null;

		@Override
		public void startDocument() throws SAXException
		{
			super.startDocument();
			Log.i(tag,"解析系统配置文件--->start");
		}

		@Override
		public void endDocument() throws SAXException
		{
			super.endDocument();
			Log.e(tag,"解析系统配置文件--->end");
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException
		{
			super.startElement(uri, localName, qName, attributes);
			if (localName.trim().equals("applicationItem"))
			{

				appinfo = new AppInfo();
				for (int i = 0; i < attributes.getLength(); i++)
				{
					String name = attributes.getLocalName(i).trim();
					String value = attributes.getValue(i).trim();
					//Log.i(tag,"ParseConfigXml-----" + name + "=" + value);
					if (name == null)
					{
						return;
					}
				
					else if (name.equals("appName"))
					{
						if (value.length() == 0)
							value = "app";
						appinfo.setAppName(value);
					}
					else if (name.equals("appPackageName"))
					{
						appinfo.setPackageName(value);
					}
					
				}// end attribute for
				appInfos.add(appinfo);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException
		{
			// TODO Auto-generated method stub
			super.endElement(uri, localName, qName);
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException
		{
			// TODO Auto-generated method stub
			super.characters(ch, start, length);
		}

	}
}
