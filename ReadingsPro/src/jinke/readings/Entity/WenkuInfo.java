package jinke.readings.Entity;

import android.graphics.Bitmap;

public class WenkuInfo {
	private String title;
	private String cover;
	private String link;
	private String abst1;
	private String abst2;
	private String abst3;
	private String sourceImg;
	private Bitmap thumb;
	private int source;
	
	
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getAbst1() {
		return abst1;
	}
	public void setAbst1(String abst1) {
		this.abst1 = abst1;
	}
	public String getAbst2() {
		return abst2;
	}
	public void setAbst2(String abst2) {
		this.abst2 = abst2;
	}
	public String getAbst3() {
		return abst3;
	}
	public void setAbst3(String abst3) {
		this.abst3 = abst3;
	}
	public String getSourceImg() {
		return sourceImg;
	}
	public void setSourceImg(String sourceImg) {
		this.sourceImg = sourceImg;
	}
	public Bitmap getThumb() {
		return thumb;
	}
	public void setThumb(Bitmap thumb) {
		this.thumb = thumb;
	}
	
	

}
