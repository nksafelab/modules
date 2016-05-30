package jinke.readings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jinke.readings.Entity.WenkuInfo;
import jinke.readings.download.HttpDownloader;
import jinke.readings.parse.ParseUtils;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author yujinyang
 * 获取百度文库搜索到的信息列表进行显示
 * 《三国》
 */
public class RecentReadingList_old extends ListActivity {


	private List<WenkuInfo> mData;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mData = getData();
		MyAdapter adapter = new MyAdapter(this);
		setListAdapter(adapter);
	}

	private List<WenkuInfo> getData() {
		
		String szUrl = "http://61.181.14.184/readman/search/getbaiduwenku.asp?key=yP25-g@@%20&base64=1";
		String xml = "";
		try {
			xml = HttpDownloader.download(szUrl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println("RecentReadingList@getData:"+xml);
		
		List<WenkuInfo> list = ParseUtils.parse(xml);
		
		
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("title", "展望中国0");
//		map.put("info", "但是有时候，列表不光会用来做显示用，我们同样可以在在上面添加按钮。添加按钮首先要写一个有按钮的xml文件，然后自然会想到用上面的方法定义一个适配器");
//		map.put("img", R.drawable.b1);
//		map.put("tag", 1);
//		list.add(map);
//
//		map = new HashMap<String, Object>();
//		map.put("title", "G1");
//		map.put("info", "但是有时候，列表不光会用来做显示用，我们同样可以在在上面添加按钮。添加按钮首先要写一个有按钮的xml文件，然后自然会想到用上面的方法定义一个适配器");
//		map.put("img", R.drawable.b2);
//		map.put("tag", 1);
//		list.add(map);
//
//		map = new HashMap<String, Object>();
//		map.put("title", "G2");
//		map.put("info", "但是有时候，列表不光会用来做显示用，我们同样可以在在上面添加按钮。添加按钮首先要写一个有按钮的xml文件，然后自然会想到用上面的方法定义一个适配器");
//		map.put("img", R.drawable.b3);
//		map.put("tag", 2);
//		list.add(map);
//		
//		map = new HashMap<String, Object>();
//		map.put("title", "G3");
//		map.put("info", "但是有时候，列表不光会用来做显示用，我们同样可以在在上面添加按钮。添加按钮首先要写一个有按钮的xml文件，然后自然会想到用上面的方法定义一个适配器");
//		map.put("img", R.drawable.b3);
//		map.put("tag", 2);
//		list.add(map);
//		
//		map = new HashMap<String, Object>();
//		map.put("title", "G4");
//		map.put("info", "但是有时候，列表不光会用来做显示用，我们同样可以在在上面添加按钮。添加按钮首先要写一个有按钮的xml文件，然后自然会想到用上面的方法定义一个适配器");
//		map.put("img", R.drawable.b3);
//		map.put("tag", 2);
//		list.add(map);
//		
//		map = new HashMap<String, Object>();
//		map.put("title", "G5");
//		map.put("info", "但是有时候，列表不光会用来做显示用，我们同样可以在在上面添加按钮。添加按钮首先要写一个有按钮的xml文件，然后自然会想到用上面的方法定义一个适配器");
//		map.put("img", R.drawable.b3);
//		map.put("tag", 2);
//		list.add(map);
		
		return list;
	}
	
	// ListView 中某项被选中后的逻辑
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		Log.v("MyListView4-click", mData.get(position).getTitle());
	}
	
	/**
	 * listview中点击按键弹出对话框
	 */
	public void showInfo(){
		new AlertDialog.Builder(this)
		.setTitle("我的listview")
		.setMessage("介绍...")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.show();
		
	}
	
	
	
	public final class ViewHolder{
		public ImageView side;
		public ImageView img;
		public TextView title;
		public TextView info;
		public Button viewBtn;
	}
	
	
	public class MyAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		
		
		public MyAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			System.out.println(mData.size());
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
		int tag = 0;
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			//System.out.println("getView----position"+position);
			
			
			//tag = (Integer)mData.get(position).get("tag");
			//String tit = (String)mData.get(position).get("title");
			//System.out.println("position:"+position+"----tag:"+tag +"-----title"+tit);
			
			
			
			ViewHolder holder = null;
			if (convertView == null) {
				
				holder=new ViewHolder();  
				
				convertView = mInflater.inflate(R.layout.vlist, null);
				
//				switch (tag) {
//				case 1:
//					convertView = mInflater.inflate(R.layout.vlist, null);
//					System.out.println("tag = "+tag +"----vlist");
//					break;
//
//				case 2:
//					convertView = mInflater.inflate(R.layout.vlist1, null);
//					System.out.println("tag = "+tag +"----vlist2");
//					break;
//				}
				
				
				holder.side = (ImageView)convertView.findViewById(R.id.side);
				holder.img = (ImageView)convertView.findViewById(R.id.img);
				holder.title = (TextView)convertView.findViewById(R.id.title);
				holder.info = (TextView)convertView.findViewById(R.id.info);
				holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
				
				convertView.setTag(holder);
				
			}else {
				
				holder = (ViewHolder)convertView.getTag();
			}
			
			if(position % 2 == 1){
				holder.side.setBackgroundResource(R.drawable.side1);
			}else{
				holder.side.setBackgroundResource(R.drawable.side2);
			}
			
			holder.img.setBackgroundResource(R.drawable.b1);
			holder.title.setText(mData.get(position).getTitle());
			holder.info.setText((String)mData.get(position).getAbst1());
			
			
			holder.viewBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showInfo();					
				}
			});
			
			
			return convertView;
		}
		
		
	}
	
	
	
	
}