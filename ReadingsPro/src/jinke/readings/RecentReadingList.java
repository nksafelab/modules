package jinke.readings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jinke.readings.Entity.WenkuInfo;
import jinke.readings.RecentReadingList_old.ViewHolder;
import jinke.readings.datebase.CDBPersistent;
import jinke.readings.datebase.DBAdapter;
import jinke.readings.download.HttpDownloader;
import jinke.readings.parse.ParseUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RecentReadingList extends Activity{
	
	private ListView listView;
	private List<Map<String, Object>> mData;
	private DBAdapter mDBAdapt;
	private CDBPersistent cDBpersistent;
//	private Button searchButton;
//	private Button requestButton;
	
	/**
	 * listview�е��������Ի���
	 */
	public void showInfo(){
		System.out.println("button click");
		new AlertDialog.Builder(this)
		.setTitle("�ҵ�listview")
		.setMessage("����...")
		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.show();
		
	}
	
	/**
	 * listview�е��������Ի���
	 */
	public void showInfo(String info){
		new AlertDialog.Builder(this)
		.setTitle("�����ʾ")
		.setMessage(info+"...")
		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				
				
				
				
				
			}
		})
		.show();
		
	}
	
	private List<Map<String, Object>> getData() {
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "G0");
		map.put("info", "������ʱ���б?�����������ʾ�ã�����ͬ���������������Ӱ�ť����Ӱ�ť����Ҫдһ���а�ť��xml�ļ���Ȼ����Ȼ���뵽������ķ�������һ��������");
		map.put("img", R.drawable.b1);
		map.put("tag", 1);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "G1");
		map.put("info", "������ʱ���б?�����������ʾ�ã�����ͬ���������������Ӱ�ť����Ӱ�ť����Ҫдһ���а�ť��xml�ļ���Ȼ����Ȼ���뵽������ķ�������һ��������");
		map.put("img", R.drawable.b2);
		map.put("tag", 1);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "G2");
		map.put("info", "������ʱ���б?�����������ʾ�ã�����ͬ���������������Ӱ�ť����Ӱ�ť����Ҫдһ���а�ť��xml�ļ���Ȼ����Ȼ���뵽������ķ�������һ��������");
		map.put("img", R.drawable.b3);
		map.put("tag", 2);
		list.add(map);
		
		map = new HashMap<String, Object>();
		map.put("title", "G3");
		map.put("info", "������ʱ���б?�����������ʾ�ã�����ͬ���������������Ӱ�ť����Ӱ�ť����Ҫдһ���а�ť��xml�ļ���Ȼ����Ȼ���뵽������ķ�������һ��������");
		map.put("img", R.drawable.b3);
		map.put("tag", 2);
		list.add(map);
		
		map = new HashMap<String, Object>();
		map.put("title", "G4");
		map.put("info", "������ʱ���б?�����������ʾ�ã�����ͬ���������������Ӱ�ť����Ӱ�ť����Ҫдһ���а�ť��xml�ļ���Ȼ����Ȼ���뵽������ķ�������һ��������");
		map.put("img", R.drawable.b3);
		map.put("tag", 2);
		list.add(map);
		
		map = new HashMap<String, Object>();
		map.put("title", "G5");
		map.put("info", "������ʱ���б?�����������ʾ�ã�����ͬ���������������Ӱ�ť����Ӱ�ť����Ҫдһ���а�ť��xml�ļ���Ȼ����Ȼ���뵽������ķ�������һ��������");
		map.put("img", R.drawable.b3);
		map.put("tag", 2);
		list.add(map);
		
		return list;
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recentreading);
		
		//searchButton = (Button)findViewById(R.id.searchButton);
		
		mData = getData();
		MyAdapter adapter = new MyAdapter(this);
		
		
		
		listView = (ListView)findViewById(R.id.listView);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				System.out.println("item click"+ arg2);
				
//				WenkuInfo info = new WenkuInfo();
//				info.setTitle((String)mData.get(arg2).get("title"));
//				info.setLink("www.baidu.com");
//				//info.setAbst1((String)mData.get(arg2).get("info"));
//				info.setAbst1("�˴�ӦΪ abst1");
//				info.setSourceImg("icon bit");
//				
//				int img = (Integer)mData.get(arg2).get("img");
//				
//				Resources res=getResources();
//
//				Bitmap bmp=BitmapFactory.decodeResource(res, img);
//				
//				
//				
//				info.setThumb(bmp);
//				
//				
////				mDBAdapt = new DBAdapter(RecentReadingList.this);
////				mDBAdapt.open();
////				
////				try {
////					if(mDBAdapt.InsertOrUpdateRecord(info)){
////						System.out.println("�޸���ݿ�ɹ�");
////					}else{
////						System.out.println("����");
////					}
////				} catch (Exception e) {
////					// TODO Auto-generated catch block
////					System.out.println(e.getMessage());
////				}
////				mDBAdapt.close();
//				
//				cDBpersistent = new CDBPersistent(RecentReadingList.this);
//				
//				cDBpersistent.open();
//				
//				//cDBpersistent.deleteAll();
//				
//				cDBpersistent.insertInfo(info);
//				
//				
//				
//				Cursor cursor = cDBpersistent.getAllInfo();
//				
//				cursor.close();
//				
//				cDBpersistent.close();
				
			}
		});
		
//		listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//					long arg3) {
//				// TODO Auto-generated method stub
//				//Log.v("MyListView4-click", (String)mData.get(position).get("title"));
//				showInfo("��������Ķ�");
//				System.out.println("MyListView4-click@"+(String)mData.get(position).get("title"));
//			}
//			
//		});
//		
		listView.setAdapter(adapter);
		
		
		
//		searchButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.setClass(RecentReadingList.this, SearchResultList.class);
//				startActivity(intent);
//			}
//		});
		
		
	}
	
	
	
	public final class ViewHolder{
		public ImageView side;
		public ImageView img;
		public TextView title;
		public TextView info;
		public Button viewBtn;
		public Button deleteBtn;
	}
	
	
	public class MyAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		
		
		public MyAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if (convertView == null) {
				
				holder=new ViewHolder();  
				
				convertView = mInflater.inflate(R.layout.vlist, null);
				
				//holder.side = (ImageView)convertView.findViewById(R.id.side);
				holder.img = (ImageView)convertView.findViewById(R.id.img);
				holder.title = (TextView)convertView.findViewById(R.id.title);
				holder.info = (TextView)convertView.findViewById(R.id.info);
				holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
				holder.deleteBtn = (Button)convertView.findViewById(R.id.view_btn2);
				convertView.setTag(holder);
				
			}else {
				
				holder = (ViewHolder)convertView.getTag();
			}
			
//			if(position % 2 == 1){
//				holder.side.setBackgroundResource(R.drawable.side1);
//			}else{
//				holder.side.setBackgroundResource(R.drawable.side2);
//			} 
			
			holder.img.setBackgroundResource((Integer)mData.get(position).get("img"));
			holder.title.setText((String)mData.get(position).get("title"));
			holder.info.setText((String)mData.get(position).get("info"));	
			holder.viewBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					System.out.println("Button clicked");
				}
			});
			
			
			
			
			
			holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					System.out.println("Button 2 clicked");
					//((Button)findViewById(R.id.view_btn2)).setBackgroundResource(R.drawable.delete_down);
					
				}
			});
			
			return convertView;
		}
		
	}

}
