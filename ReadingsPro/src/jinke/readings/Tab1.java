package jinke.readings;

import java.io.InputStream;
import java.util.List;

import jinke.readings.Entity.WenkuInfo;
import jinke.readings.download.CNetTransfer;
import jinke.readings.download.HttpDownloader;
import jinke.readings.parse.IParse;
import jinke.readings.parse.ParseUtils;
import jinke.readings.parse.WenkuInfoParse;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class Tab1 extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab1);
//		String szUrl = "http://61.181.14.184/readman/search/getbaiduwenku.asp?key=yP25-g@@%20&base64=1";
//		String xml = "";
//		try {
//			xml = HttpDownloader.download(szUrl);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		//System.out.println(xml);
//		
//		List<WenkuInfo> infoList = ParseUtils.parse(xml);
//		String title = "";
//		for(int i = 0;i<infoList.size();i++){
//			WenkuInfo info = infoList.get(i);
//			title += info.getTitle()+"--";
//			System.out.println(info.getTitle());
//		}
//		
//		TextView tit = (TextView)findViewById(R.id.title);
//		tit.setText(title);
//		tit.setTextColor(Color.BLACK);
		
		IParse wenkuParse = new WenkuInfoParse();
		String szUrl = "http://61.181.14.184/readman/search/getbaiduwenku.asp?key=yP25-g@@%20&base64=1";
		InputStream oIS = null;
		try {
			oIS = CNetTransfer.GetXmlInputStream(szUrl);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			wenkuParse.doParse(oIS);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	

}
