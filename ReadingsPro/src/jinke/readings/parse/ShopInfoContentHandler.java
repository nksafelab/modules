package jinke.readings.parse;

import java.util.List;

import jinke.readings.Entity.WenkuInfo;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class ShopInfoContentHandler extends DefaultHandler{
	private WenkuInfo shopInfo; 
	String tagName;
	
	
	
	public ShopInfoContentHandler(WenkuInfo shopInfo) {
		super();
		this.shopInfo = shopInfo;
	}
	
	

	public WenkuInfo getShopInfo() {
		return shopInfo;
	}



	public void setShopInfo(WenkuInfo shopInfo) {
		this.shopInfo = shopInfo;
	}



	@Override
	public void startDocument() throws SAXException {
//		System.out.println("-------begin--------");
	}
	
	
	@Override
	public void endDocument() throws SAXException {
		//System.out.println("-------end----------"+shopInfo);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		tagName = localName;
		//System.out.println("startElement------- "+tagName);
		if(tagName.equals("item")){
			shopInfo = new WenkuInfo();
			//System.out.println("new EntityShop");
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		//System.out.println("endElement----localName--- "+localName);
		if(localName.equals("item")){
			//System.out.println("shopInfo"+shopInfo);
		}
		
	}
	
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
			String temp = new String(ch,start,length);
			
			if(!(temp.equals(" ")||temp.equals("  "))){
				if(tagName.equals("storeid")){
					//shopInfo.setStoreid(temp);
					System.out.println("character-----set--------input "+temp);
				}
				else if(tagName.equals("storename")){
					//shopInfo.setStorename(temp);
					System.out.println("character----set--------input "+temp);
				}
				else if(tagName.equals("locationX")){
					//shopInfo.setLocationX(temp);
					System.out.println("character------set-------input "+temp);
				}
				
			}
	}

}
