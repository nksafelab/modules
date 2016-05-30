package jinke.readings;

import java.util.ArrayList;
import java.util.List;

import jinke.readings.parse.HotWordParse;
import jinke.readings.parse.IParse;
import jinke.readings.util.CThread;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;




public class HotWordActivity extends Activity{
	
	private TextView t1,t2,t3,t4,t5,t6,t7,t8;
	private List<TextView> lt = null;
	private List<String> ls = null;
	private ListView hotList;
	private ArrayAdapter<String> adapter ;  
	private TextView barInfo = null;
	private ProgressBar myBar = null;
	
	
	Handler hothandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//System.out.println("get handler message");
			
			ls = (List<String>) msg.obj;
			
			for(int i = 0; i<ls.size();i++){
				//((TextView)lt.get(i)).setText(ls.get(i));
				//adapter.add(ls.get(i));
			}
			
			
			super.handleMessage(msg);
			
		}
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotword);
		adapter = new ArrayAdapter<String>(this,R.layout.hot_list_item);
		setTextView();
		
		
		
		String szUrl = getString(R.string.url_hotword)+"?n=8";
		IParse parse = new HotWordParse(8);
		CThread thread = new CThread(szUrl, parse, hothandler);
		thread.start();
		
		
//		hotList.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// TODO Auto-generated method stub
//				System.out.println(arg2+"clicked"+ls.get(arg2));
//				String search_context = ls.get(arg2);
//				Intent intent = new Intent(HotWordActivity.this, CMainUI.class);
//				Bundle b = new Bundle();
//				b.putString("search_context", search_context);
//				CMainUI.edit.setText(search_context);
//				intent.putExtras(b);
//				startActivity(intent);
//				
//			}
//		});
		
	}
	
	private void setTextView(){
		lt = new ArrayList<TextView>();
		
		t1 = (TextView)findViewById(R.id.hot1);
		lt.add(t1);
		t2 = (TextView)findViewById(R.id.hot2);
		lt.add(t2);
		t3 = (TextView)findViewById(R.id.hot3);
		lt.add(t3);
		t4 = (TextView)findViewById(R.id.hot4);
		lt.add(t4);
		t5 = (TextView)findViewById(R.id.hot5);
		lt.add(t5);
		t6 = (TextView)findViewById(R.id.hot6);
		lt.add(t6);
		t7 = (TextView)findViewById(R.id.hot7);
		lt.add(t7);
		t8 = (TextView)findViewById(R.id.hot8);
		lt.add(t8);
		
	}
	
}
