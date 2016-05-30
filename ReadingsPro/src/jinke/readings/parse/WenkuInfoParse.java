package jinke.readings.parse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import jinke.readings.R;
import jinke.readings.Entity.WenkuInfo;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;



public class WenkuInfoParse extends DefaultHandler implements IParse {

	private WenkuInfo eSotreInfo = new WenkuInfo();
	private List<WenkuInfo> lsWenku = new ArrayList<WenkuInfo>();
	private String szTagName = "";
	private int source;
	private String flagStr = "";
	public static final int WENKU = 1;
	public static final int IASK = 2;
	public static final int MK = 3;
	public static final int WENKU3G = 4;
	public static final int YOUKU = 5;
	public static final int XIAOSHUO = 6;
	public static final int GOOGLEBOOK = 7;
	public static final int TXTR = 8;
	public static final int CARTOON = 9;
	public WenkuInfoParse()
	{
		
	}
	public WenkuInfoParse(int source){
		this.source = source;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		//System.out.println("startElement");
		
		if (localName.equals("item")) {
			eSotreInfo = new WenkuInfo();
			eSotreInfo.setSource(this.source);
		} else {
			szTagName = localName;
		}

	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		//System.out.println("EndElement");
		flagStr ="";
		szTagName = "";
		
		szTagName = "";
		if (localName.equals("item")) {
			lsWenku.add(eSotreInfo);
			eSotreInfo = null;
		}

	}

	public void characters(char[] ch, int start, int length) {
		//System.out.println("characters");
		String msg;
		String str = "";
		if (szTagName.equals("title")) {
			flagStr += new String(ch, start, length);
			
			eSotreInfo.setTitle(flagStr);
			//System.out.println("title"+new String(ch, start, length));
			//System.out.println("title:--"+flagStr);
		} else if (szTagName.equals("cover")) {
			eSotreInfo.setCover(new String(ch, start, length));
			//System.out.println("cover"+new String(ch, start, length));
		} else if (szTagName.equals("link")) {
			eSotreInfo.setLink(new String(ch, start, length));
			//System.out.println("link"+new String(ch, start, length));
		} else if (szTagName.equals("abst1")) {
			eSotreInfo.setAbst1(new String(ch, start, length));
			//System.out.println("abst1"+new String(ch, start, length));
		} else if (szTagName.equals("abst2")) {
			eSotreInfo.setAbst2(new String(ch, start, length));
			//System.out.println("abst2"+new String(ch, start, length));
		} else if (szTagName.equals("abst3")) {
			eSotreInfo.setAbst3(new String(ch, start, length));
			//System.out.println("abst3"+new String(ch, start, length));
		} else if (szTagName.equals("sourceImg")) {
			eSotreInfo.setSourceImg(new String(ch, start, length));
			//System.out.println("sourceImg"+new String(ch, start, length));
		} 
	}



	@Override
	public boolean doParse(InputStream oIS) throws Exception {
		String Encode_Str = "";
		switch (source) {
		case WENKU3G:
		case YOUKU:
		case XIAOSHUO:
		case GOOGLEBOOK:
		case TXTR:
		case CARTOON:
			Encode_Str = "UTF-8";
			break;
		default:
			Encode_Str = "gb2312";
			break;
		}
		
		try
		{
			//System.out.println("Parse!!!!!!!!!");
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			SAXParser parser = saxFactory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setContentHandler(this);
				
	//		InputStreamReader isr = new InputStreamReader(oIS,"gb2312");
			InputStreamReader isr = new InputStreamReader(oIS,Encode_Str);
			
			//added by zhong
			BufferedInputStream bis = new BufferedInputStream(oIS);
			ByteArrayBuffer bab = new ByteArrayBuffer(512);
			int current = 0;
			while ((current = bis.read()) != -1) {
				bab.append((byte) current);
			}
			
			String szData = EncodingUtils.getString(bab.toByteArray(), Encode_Str);
	//		String szData = new String(bab.toByteArray(), "gb2312");
			bis.close();
			//System.out.println("锟斤拷始锟斤拷锟斤拷");
			System.out.println("doParse @ WenkuInfoParse:" + szData);
			reader.parse(new InputSource(new StringReader(szData.trim())));
			//reader.parse(new InputSource(isr, ));
			//end added
			//reader.parse(new InputSource(isr));
		
		}
		catch (IOException e) {
			System.out.println("doParseException@WenkuInfoParse--------------------" + e.getMessage());
		}
		oIS.close();
		return true;
	}

	@Override
	public Object output() {
		return lsWenku;
	}
	@Override
	public int totalPage() {
		// TODO Auto-generated method stub
		return 0;
	}
}
