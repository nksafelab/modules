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

import jinke.readings.Entity.SynInfo;
import jinke.readings.Entity.Syn_subInfo;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;


public class SynInfoParse_theme extends DefaultHandler implements IParse {

	private SynInfo synInfo = new SynInfo();
	private List<SynInfo> lsSynInfo = new ArrayList<SynInfo>();
	
	private List<Syn_subInfo> ls_subInfo = new ArrayList<Syn_subInfo>();
	private Syn_subInfo subInfo;
	
	private String szTagName = "";
	private int source;
	private String flagStr = "";
	
	private final int SYN_DATA_SEARCH = 1;
	private final int SYN_PAPER_SEARCH = 2;
	
	private boolean bookrsc = false;
	private boolean apprsc = false;
	private boolean shutursc = false;
	private boolean manhuarsc = false;
	
	public SynInfoParse_theme()
	{
		
	}
	public SynInfoParse_theme(int source){
		this.source = source;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		//System.out.println("startElement");
		
		
		if (localName.equals("bookrscitem")) {
			synInfo = new SynInfo();
			
		}else if(localName.equals("apprscitem")){
			synInfo = new SynInfo();
		}else if(localName.equals("shutursc")){
			synInfo = new SynInfo();
		}else if(localName.equals("manhuarsc")){
			synInfo = new SynInfo();
		}
		else if(localName.equals("bookrsc")){
			bookrsc = true;
		}else if(localName.equals("apprsc")){
			apprsc = true;
		}else if(localName.equals("shutursc")){
			shutursc = true;
		}else if(localName.equals("manhuarsc")){
			manhuarsc = true;
		}
		else {
			szTagName = localName;
		}
		
		if(bookrsc)
			if(localName.equals("bookrscsubobjitem")){
				Log.e("Parse", "new subInfo");
				subInfo = new Syn_subInfo();
			}
		if(apprsc)
			if(localName.equals("apprscsubobjitem")){
				Log.e("Parse", "new subInfo");
				subInfo = new Syn_subInfo();
			}
		if(shutursc)
			if(localName.equals("shuturscsubobjitem")){
				Log.e("Parse", "new subInfo");
				subInfo = new Syn_subInfo();
			}
		if(manhuarsc)
			if(localName.equals("manhuarscsubobjitem")){
				Log.e("Parse", "new subInfo");
				subInfo = new Syn_subInfo();
			}

	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		//System.out.println("EndElement");
		flagStr ="";
		szTagName = "";
		
		szTagName = "";
		if (localName.equals("bookrscitem")) {
			lsSynInfo.add(synInfo);
			synInfo = null;
		}else if(localName.equals("apprscitem")){
			if(synInfo.getTitle() != null)
			lsSynInfo.add(synInfo);
			synInfo = null;
		}else if(localName.equals("shutursc")){
			if(synInfo.getTitle() != null)
			lsSynInfo.add(synInfo);
			synInfo = null;
		}else if(localName.equals("manhuarsc")){
			if(synInfo.getTitle() != null)
			lsSynInfo.add(synInfo);
			synInfo = null;
		}
		else if(localName.equals("bookrsc")){
			bookrsc = false;
		}else if(localName.equals("apprsc")){
			apprsc = false;
		}else if(localName.equals("shutursc")){
			shutursc = false;
		}else if(localName.equals("manhuarsc")){
			manhuarsc = false;
		}
		
		
		if(bookrsc)
			if(localName.equals("bookrscsubobjitem")){
				synInfo.getLs_subInfo().add(subInfo);
				Log.e("Parse", "add subInfo");
				subInfo = null;
			}
		if(apprsc)
			if(localName.equals("apprscsubobjitem")){
				synInfo.getLs_subInfo().add(subInfo);
				Log.e("Parse", "add subInfo");
				subInfo = null;
			}
		if(shutursc)
			if(localName.equals("shuturscsubobjitem")){
				synInfo.getLs_subInfo().add(subInfo);
				Log.e("Parse", "add subInfo");
				subInfo = null;
			}
		if(manhuarsc)
			if(localName.equals("manhuarscsubobjitem")){
				synInfo.getLs_subInfo().add(subInfo);
				Log.e("Parse", "add subInfo");
				subInfo = null;
			}

	}

	public void characters(char[] ch, int start, int length) {
		//System.out.println("characters");
		String msg;
		String str = "";
		
//		if(bookrsc)
			if (szTagName.equals("rsctitle")) {
				flagStr += new String(ch, start, length);
				
				synInfo.setTitle(flagStr);
				//System.out.println("title"+new String(ch, start, length));
				System.out.println("title:--"+flagStr);
			} else if (szTagName.equals("author")) {
				synInfo.setAuthor(new String(ch, start, length));
	//			System.out.println("author"+new String(ch, start, length));
			} else if (szTagName.equals("intrduction")) {
				synInfo.setIntrduction(new String(ch, start, length));
	//			System.out.println("intrduction"+new String(ch, start, length));
			} else if (szTagName.equals("publiser")) {
				synInfo.setPubliser(new String(ch, start, length));
	//			System.out.println("publiser"+new String(ch, start, length));
			} else if (szTagName.equals("thumburl")) {
				synInfo.setThumburl(new String(ch, start, length));
	//			System.out.println("thumburl"+new String(ch, start, length));
			} else if (szTagName.equals("websrc")) {
				String tem = new String(ch, start, length);
				flagStr += new String(ch, start, length);
				if(!"".equals(tem)){
					
				if(flagStr.length() >=1 && subInfo != null)
				subInfo.setWebsrc(Integer.parseInt(flagStr.charAt(0)+""));
				System.out.println("websrc:"+flagStr+":");
				}
			} else if (szTagName.equals("weburl")) {
				flagStr += new String(ch, start, length);
				if(subInfo != null)
				subInfo.setWeburl(flagStr);
				System.out.println("weburl:"+new String(ch, start, length));
			} 
		
		
			
		
		
	}


	@Override
	public boolean doParse(InputStream oIS) throws Exception {
		String Encode_Str = "UTF-8";
//		switch (source) {
//		case SYN_DATA_SEARCH:
//		case SYN_PAPER_SEARCH:
//			Encode_Str = "UTF-8";
//			break;
//		default:
//			Encode_Str = "gb2312";
//			break;
//		}
		
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
			
			Log.v("SynInfoParse", szData);
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
		Log.e("output", lsSynInfo.size()+" null:"+(lsSynInfo == null));
		return lsSynInfo;
	}
	@Override
	public int totalPage() {
		// TODO Auto-generated method stub
		return 0;
	}
}
