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

import jinke.readings.Entity.Book;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;



public class BookParse extends DefaultHandler implements IParse {

	

	private List<Book> lsBook = new ArrayList<Book>();
	private String szTagName = "";
	private int source;
	private String flagStr = "";
	private Book book;
	private int totalPage;
	

	public BookParse()
	{
		
	}
	public BookParse(int source){
		this.source = source;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		//System.out.println("startElement");
		
		if (localName.equals("item")) {
			book = new Book();
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
			lsBook.add(book);
			book = null;
		}

	}

	public void characters(char[] ch, int start, int length) {
		//System.out.println("characters");
		String msg;
		String str = "";
		if (szTagName.equals("bookName")) {
			flagStr += new String(ch, start, length);
			book.setBookName(flagStr);
		}else if(szTagName.equals("author")){
			flagStr += new String(ch, start, length);
			book.setAuthor(flagStr);
		}else if(szTagName.equals("fileType")){
			flagStr += new String(ch, start, length);
			book.setFileType(flagStr);
		}else if(szTagName.equals("bookID")){
			flagStr += new String(ch, start, length);
			book.setBookID(flagStr);
		}else if(szTagName.equals("url")){
			flagStr += new String(ch, start, length);
			book.setUrl(flagStr);
		}else if(szTagName.equals("source")){
			flagStr += new String(ch, start, length);
			book.setSource(flagStr);
		}
	}


	@Override
	public boolean doParse(InputStream oIS) throws Exception {
		try
		{
			//System.out.println("Parse!!!!!!!!!");
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		SAXParser parser = saxFactory.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		reader.setContentHandler(this);
			
		InputStreamReader isr = new InputStreamReader(oIS,"utf-8");
		
		//added by zhong
		BufferedInputStream bis = new BufferedInputStream(oIS);
		ByteArrayBuffer bab = new ByteArrayBuffer(512);
		int current = 0;
		while ((current = bis.read()) != -1) {
			bab.append((byte) current);
		}
		
		//String szData = EncodingUtils.getString(bab.toByteArray(), HTTP.UTF_8);
		String szData = new String(bab.toByteArray(), "utf-8");
		bis.close();
		//System.out.println("锟斤拷始锟斤拷锟斤拷");
//		System.out.println("doParse@BookParse:===================" + );
		Log.e("request", "result:"+szData);
		
	reader.parse(new InputSource(new StringReader(szData.trim())));
		//reader.parse(new InputSource(isr, ));
		//end added
		//reader.parse(new InputSource(isr));
		
		}
		catch (IOException e) {
			System.out.println("doParseException:" + e.getMessage());
		}
		oIS.close();
		return true;
	}

	@Override
	public Object output() {
		return lsBook;
	}
	@Override
	public int totalPage() {
		// TODO Auto-generated method stub
		return totalPage;
	}
	
	
	
}
