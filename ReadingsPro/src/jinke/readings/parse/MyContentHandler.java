package jinke.readings.parse;

import java.util.List;

import jinke.readings.Entity.WenkuInfo;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class MyContentHandler extends DefaultHandler{
	private List<WenkuInfo> infoList= null;
	private WenkuInfo info = null; 
	String tagName;
	
	public MyContentHandler(List<WenkuInfo> shopList) {
		super();
		this.infoList = shopList;
	}


	public List<WenkuInfo> getShopList() {
		return infoList;
	}


	public void setShopList(List<WenkuInfo> shopList) {
		this.infoList = shopList;
	}


	@Override
	public void startDocument() throws SAXException {
		//System.out.println("-------begin--------");
	}
	
	
	@Override
	public void endDocument() throws SAXException {
		//System.out.println("-------end----------");
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		tagName = localName;
		System.out.println("startElement------- "+tagName);
		if(tagName.equals("item")){
			info = new WenkuInfo();
			//System.out.println("new EntityShop");
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		//System.out.println("endElement----localName--- "+localName);
		if(localName.equals("item")){
			infoList.add(info);
			//System.out.println("add shop");
		}
		
	}
	
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
			
			String temp = new String(ch,start,length);
			
			if(!(temp.equals(" ")||temp.equals("  "))){
				if(tagName.equals("title")){
					//shop.setStoreid(temp);
					//System.out.println("character-----set--------input "+temp);
					info.setTitle(temp);
				}
				else if(tagName.equals("cover")){
					//shop.setStorename(temp);
					//System.out.println("character----set--------input "+temp);
					info.setCover(temp);
				}
				else if(tagName.equals("link")){
					//shop.setStorename(temp);
					//System.out.println("character----set--------input "+temp);
					info.setLink(temp);
				}
				else if(tagName.equals("abst1")){
					//shop.setStorename(temp);
					//System.out.println("character----set--------input "+temp);
					info.setAbst1(temp);
				}
				else if(tagName.equals("abst2")){
					//shop.setStorename(temp);
					//System.out.println("character----set--------input "+temp);
					info.setAbst2(temp);
				}
				else if(tagName.equals("abst3")){
					//shop.setStorename(temp);
					//System.out.println("character----set--------input "+temp);
					info.setAbst3(temp);
				}
				else if(tagName.equals("sourceImg")){
					//shop.setStorename(temp);
					//System.out.println("character----set--------input "+temp);
					info.setSourceImg(temp);
				}
			}
	}

}
