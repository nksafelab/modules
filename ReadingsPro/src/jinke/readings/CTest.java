package jinke.readings;

import java.util.ArrayList;
import java.util.List;

import jinke.readings.Entity.SynInfo;
import jinke.readings.parse.IParse;
import jinke.readings.parse.SynInfoParse;
import jinke.readings.parse.WenkuInfoParse;
import jinke.readings.util.CThread;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CTest extends Activity {
	
	List<SynInfo> lsSynInfo = new ArrayList<SynInfo>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.spectext);
		
		String szUrl = "http://61.181.14.184:8084/MultiSearch/search.do?method=searchbookInDatebase&bookname=book&xmltag=1";
		
		IParse parse = new SynInfoParse(1);
		
//		CThread thread = new CThread(szUrl, parse, handler1, 1);
//		thread.start();
	
		
	}
	
	Handler handler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//System.out.println("get handler message");
			Log.v("CTest", "========get handler message================================");
			super.handleMessage(msg);
			
			lsSynInfo = (List<SynInfo>)msg.obj;
			SynInfo s = new SynInfo();
			for(int i=0;i<lsSynInfo.size();i++){
				s = lsSynInfo.get(i);
				Log.v("CTest", "========================================");
				Log.v("CTest", "title"+s.getTitle());
				Log.v("CTest", "author"+s.getAuthor());
				Log.v("CTest", "intrduction"+s.getIntrduction());
				Log.v("CTest", "publiser"+s.getPubliser());
				Log.v("CTest", "thumburl"+s.getThumburl());
				for(int j=0;j<s.getLs_subInfo().size();j++){
				}
				Log.v("CTest", "========================================");
			}
			
			
		}
	};

}
