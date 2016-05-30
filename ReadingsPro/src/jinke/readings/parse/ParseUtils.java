package jinke.readings.parse;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import jinke.readings.Entity.WenkuInfo;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class ParseUtils {
	
	public static List<WenkuInfo> parse(String xml){
		List<WenkuInfo> shopList = null;
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				XMLReader reader = factory.newSAXParser().getXMLReader();
				
				shopList = new ArrayList<WenkuInfo>();
				MyContentHandler shopListContenthandler = new MyContentHandler(shopList);
				//System.out.println("beginParse===================");
				reader.setContentHandler(shopListContenthandler);
				reader.parse(new InputSource(new StringReader(xml)));
				for(Iterator iterator = shopList.iterator();iterator.hasNext();){
					WenkuInfo shop = (WenkuInfo)iterator.next();
					//System.out.println(shop);
				}
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return shopList;
	}
	
	public static WenkuInfo infoPrase(String xml){
		WenkuInfo shopInfo = null;
		
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			XMLReader reader = factory.newSAXParser().getXMLReader();
			
			shopInfo = new WenkuInfo();
			ShopInfoContentHandler shopInfoContentHandler = new ShopInfoContentHandler(shopInfo);
			
			reader.setContentHandler(shopInfoContentHandler);
			reader.parse(new InputSource(new StringReader(xml)));
			
			shopInfo = shopInfoContentHandler.getShopInfo();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
//		//System.out.println("after prase------"+shopInfo);
		return shopInfo;
	}

}
