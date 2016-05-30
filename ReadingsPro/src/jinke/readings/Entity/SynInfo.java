package jinke.readings.Entity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class SynInfo {
	
	private String title;
	private String author;
	private String intrduction;
	private String publiser;
	private String thumburl;
	private List<Syn_subInfo> ls_subInfo = new ArrayList<Syn_subInfo>();
	private String info;
	private Bitmap thumb;
	private String price1;
	private String price2;
	
	
	
	public String getPrice1() {
		return price1;
	}
	public void setPrice1(String price1) {
		this.price1 = price1;
	}
	public String getPrice2() {
		return price2;
	}
	public void setPrice2(String price2) {
		this.price2 = price2;
	}
	public List<Syn_subInfo> getLs_subInfo() {
		return ls_subInfo;
	}
	public void setLs_subInfo(List<Syn_subInfo> ls_subInfo) {
		this.ls_subInfo = ls_subInfo;
	}
	public Bitmap getThumb() {
		return thumb;
	}
	public void setThumb(Bitmap thumb) {
		this.thumb = thumb;
	}
	public String getIntrduction() {
		return intrduction;
	}
	public void setIntrduction(String intrduction) {
		this.intrduction = intrduction;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getPubliser() {
		return publiser;
	}
	public void setPubliser(String publiser) {
		this.publiser = publiser;
	}
	public String getThumburl() {
		return thumburl;
	}
	public void setThumburl(String thumburl) {
		this.thumburl = thumburl;
	}
	
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
	
}
