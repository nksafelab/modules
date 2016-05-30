package jinke.readings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jinke.readings.datebase.CDBPersistent;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * @author jinyang
 *
 */
public class CTest3 extends Activity {

	
	private CDBPersistent cDBpersistent;
	
	private ListView listView;
	private String title;
	private byte[] arrbt = null;
	private List<Map<String,Object>> l = new ArrayList<Map<String, Object>>();;
	//private List<String> data = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.test3);
		
		cDBpersistent = new CDBPersistent(CTest3.this,"recent_reading");
		
		cDBpersistent.open();
		
		Cursor cursor = cDBpersistent.getAllInfo();
		
		//cursor.close();
		
		cDBpersistent.close();
		Map<String, Object> map = new HashMap<String, Object>();
		
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
		{
			title = cursor.getString(1);
		    
		    arrbt = cursor.getBlob(cursor.getColumnIndex("thumb"));
		    Bitmap bm = BitmapFactory.decodeByteArray(arrbt, 0, arrbt.length);
		    
		    map = new HashMap<String, Object>();
		    map.put("title", title);
		    map.put("bitmap", bm);
		    l.add(map);
		}
		
		for(int i=0;i<l.size();i++){
			System.out.println("CTest3--title"+(String)(l.get(i)).get("title"));
		}
		
		TextView t1 = (TextView)findViewById(R.id.t1);
		t1.setText((String)(l.get(0)).get("title"));
		TextView t2 = (TextView)findViewById(R.id.t2);
		t2.setText((String)(l.get(1)).get("title"));
		
		ImageView i1 = (ImageView)findViewById(R.id.i1);
		i1.setImageBitmap((Bitmap)(l.get(0)).get("bitmap"));
		ImageView i2 = (ImageView)findViewById(R.id.i2);
		i2.setImageBitmap((Bitmap)(l.get(1)).get("bitmap"));
		
		
	}
	
	
}