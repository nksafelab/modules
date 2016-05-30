package jinke.readings;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jinke.readings.Entity.WenkuInfo;
import jinke.readings.RecentReadingList.ViewHolder;
import jinke.readings.datebase.CDBPersistent;
import jinke.readings.parse.CBitmapParse;
import jinke.readings.parse.IParse;
import jinke.readings.parse.WenkuInfoParse;
import jinke.readings.util.Base64Encode;
import jinke.readings.util.CNetQueueThread;
import jinke.readings.util.CTask;
import jinke.readings.util.CThread;
import jinke.readings.util.IdentifyUtil;
import jinke.readings.util.PushReciever;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SearchResultList extends Activity{
	
	private CDBPersistent cDBpersistent;
	private byte[] arrbt = null;

	private List<View> lsLVItem = new ArrayList<View>();
	Queue<CTask> queueTask = null;
	
	//private CAdapter adapter = new CAdapter(SearchResultList.this, lsLVItem);
	private CAdapter adapter;
	private int nID = 0;
	public static final int WENKU = 1;
	public static final int IASK = 2;
	public static final int MK = 3;
	public static final int WENKU3G = 4;
	public static final int YOUKU = 5;
	public static final int XIAOSHUO = 6;
	public static final int GOOGLEBOOK = 7;
	
	private static final int INFOLENGTH = 40;
	private static final int TITLELENGTH = 20;
	
	
	private String sourcePath = "/sdcard/readings/searchSource";
	private boolean flags = false;
	
	public final class ViewHolder{
//		public ImageView side;
		public ImageView img;
		public TextView title;
		public TextView info;
		public TextView source;
		public Button viewBtn;
		public Button deleteBtn;
	}
	
	private String search_context = null;
	
	 public void DisplayToast(String str) {  
		   Toast.makeText(this, str, Toast.LENGTH_SHORT).show();  
	}
	
	TextView barInfo = null;
	//TextView tvFoot = null;
	ProgressBar myBar = null;
	ListView lv = null;
	List<WenkuInfo> listInfo = new ArrayList<WenkuInfo>();
	
	class CAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater;
		List<View> lsView;

		public CAdapter(Context context, List<View> lsView ) {
			this.mInflater = LayoutInflater.from(context);
			//this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			this.lsView = lsView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lsLVItem.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			String srcStr = "来源";
			
			int source = listInfo.get(position).getSource();
			
			if (convertView == null) {
				//System.out.println("converView@getView : null");
				holder=new ViewHolder();  
				
				switch (source) {
				case MK:
					srcStr ="群落阅读";
					//System.out.println("getView@source MK position:"+position);
					convertView = mInflater.inflate(R.layout.vlist, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					holder.deleteBtn = (Button)convertView.findViewById(R.id.view_btn2);
					break;
				case IASK:
					srcStr = "新浪IASK";
					//System.out.println("getView@source IASK position:"+position);
					convertView = mInflater.inflate(R.layout.vlist1, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				case WENKU:
					srcStr = "百度文库";
					//System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				case WENKU3G:
					srcStr = "百度文库3G";
					//System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				case YOUKU:
					srcStr = "优酷空间";
					//System.out.println("getView@source YOUKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist_youku, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					//holder.img.setBackgroundResource(R.drawable.default_img);
					
					break;
				case XIAOSHUO:
					srcStr = "百度小说";
					//System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				case GOOGLEBOOK:
					srcStr = "谷歌图书";
					//System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				default:
					break;
				}
				
				convertView.setTag(holder);
				
			}else {
				//System.out.println("converView@getView : not null");
				
				//holder = (ViewHolder)convertView.getTag();
				
				holder=new ViewHolder();  
				
				switch (source) {
				case MK:
					srcStr ="群落阅读";
					//System.out.println("getView@source MK position:"+position);
					convertView = mInflater.inflate(R.layout.vlist, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					holder.deleteBtn = (Button)convertView.findViewById(R.id.view_btn2);
					break;
				case IASK:
					srcStr = "新浪IASK";
					//System.out.println("getView@source IASK position:"+position);
					convertView = mInflater.inflate(R.layout.vlist1, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				case WENKU:
					srcStr = "百度文库";
					//System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				case WENKU3G:
					srcStr = "百度文库3G";
					//System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				case YOUKU:
					srcStr = "优酷空间";
					//System.out.println("getView@source YOUKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist_youku, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					//holder.img.setBackgroundResource(R.drawable.default_img);
					break;
				case XIAOSHUO:
					srcStr = "百度小说";
					//System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				case GOOGLEBOOK:
					srcStr = "谷歌图书";
					//System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				default:
					break;
				}
				
			}
			
			
//			if(position % 2 == 1){
//				convertView.setBackgroundColor(Color.LTGRAY);
//			}else{
//				convertView.setBackgroundColor(Color.WHITE);
//			} 
			
			if(holder!= null){
				
				String ab = listInfo.get(position).getTitle();
				//System.out.println("abst1 length : "+ab1.length());
				if(ab.length() > TITLELENGTH){
					ab = ab.substring(0, TITLELENGTH);
				}
				
				holder.title.setText(ab);
				
				if(listInfo.get(position).getThumb() != null){
					//System.out.println("thumb not null");
					holder.img.setImageBitmap(listInfo.get(position).getThumb());
				}else{
					//System.out.println("thumb null");
				}
				
				String ab1 = listInfo.get(position).getAbst1();
				//System.out.println("abst1 length : "+ab1.length());
				if(ab1 !=null && ab1.length() > INFOLENGTH){
					ab1 = ab1.substring(0, INFOLENGTH);
				}
				holder.info.setText(ab1);
				//holder.info.setText(listInfo.get(position).getAbst1());	
				holder.source.setText(srcStr);
					if(holder.viewBtn != null){
						
						final int po = position;
				
					holder.viewBtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							//System.out.println("Button clicked"+po);
							
							WenkuInfo entity = listInfo.get(po);
							
							Bundle oBundle = new Bundle();
							oBundle.putString("link",entity.getLink());
							
							Intent oIntent = new Intent();
							
							oIntent.setClass(SearchResultList.this, CWebView.class);
							oIntent.putExtras(oBundle);
							
							startActivity(oIntent);
							
						}
					});
				}else{
					//System.out.println("Button1 is null");
				}
				
				if(holder.deleteBtn != null){
					holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							//System.out.println("Button 2 clicked");
							//((Button)findViewById(R.id.view_btn2)).setBackgroundResource(R.drawable.delete_down);
							DisplayToast("导出功能--待实现");
						}
					});
				}else{
					//System.out.println("Button2 is null");
				}
			}
			return convertView;
			
			
			//return lsView.get(position);
		}
	}
	
	
	
	Handler handler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//System.out.println("get handler message");
			System.out.println("handler1()@SearchResultList---------收到搜索结果信息，开始展示list-------------------");
			//Queue<CTask> queueTask = new LinkedList<CTask>();
			if(queueTask == null){
				queueTask = new LinkedList<CTask>();
			}
			List<WenkuInfo> ls = (List<WenkuInfo>) msg.obj;
			if(ls != null){
				//System.out.println("ls not null");
				//System.out.println(ls.size());
			}else{
				//System.out.println("ls null");
			}
			listInfo.addAll(ls);
			LayoutInflater oInflater = LayoutInflater.from(SearchResultList.this);
			
			View vItemRoot = null;
			//TextView lvGameClassifyItemTextView = null;
		
			WenkuInfo entity = null;
			CTask oTask = null;
			//ViewHolder holder ;
			
			
			for (int i = 0; i < ls.size(); i++, nID++) {
				entity = ls.get(i);
				oTask = new CTask();
				//System.out.println("@handler----"+listInfo.size());
				
				oTask.nNum = nID;
				oTask.szUrl = entity.getCover();
				oTask.source = entity.getSource();
				if(entity.getCover() == null){
					
				}else{
				}
				//System.out.println("szUrl:####################"+entity.getCover());
				queueTask.offer(oTask);
				try{
				vItemRoot = oInflater.inflate(R.layout.vlist, null);
				}catch (Exception e) {
					 
				}
				lsLVItem.add(vItemRoot);
			}//for
			
			ListView lvSubInfo = (ListView) findViewById(R.id.lvGameInfo);
			
			
			adapter = new CAdapter(SearchResultList.this, lsLVItem);

			lvSubInfo.setAdapter(adapter);
			//System.out.println("listVIew adapter size: "+listInfo.size());
			
			//System.out.println("SearchResultList---------@handler() : receive msg ,display done! set progressBar gone");
			
			myBar.setVisibility(View.GONE);
			barInfo.setVisibility(View.GONE);
			
			updateBitmapList(queueTask);
			
			System.out.println("handler1()@SearchResultList--------- 开始-updateBitMap-------------------------");
			
			//不是从数据库中读取的，才将搜索结果写入数据库
			if(flags != true){
				
				System.out.println("handler1()@SearchResultList-----------不是从数据库中取出的结果，将结果插入数据库-------------");
				
				cDBpersistent = new CDBPersistent(SearchResultList.this,"recent_reading");
				
				cDBpersistent.open();
				
				cDBpersistent.insert(ls,"search_his",search_context);
				
				
				cDBpersistent.close();
			}
			
			
			
			super.handleMessage(msg);
			
		}
	};
	
	
	private void updateBitmapList(Queue<CTask> queueTask) {
		System.out.println("updateBitmapList@SearchResultList------------------");
		IParse parse = new CBitmapParse();
		CNetQueueThread bmpThread = new CNetQueueThread(queueTask, parse,
				handlerImgUpdate);
		bmpThread.start();

	}
	
	Handler handlerImgUpdate = new Handler(Looper.myLooper()) {

		@Override
		public void handleMessage(Message msg) {
			CTask oTask = (CTask) msg.obj;
			updateBitmap(oTask);
			adapter.notifyDataSetChanged();
			super.handleMessage(msg);
		}
	};
	private void updateBitmap(CTask oTask) {
		//System.out.println("updateBitmap@SearchResultList------------------");
		View vItem = lsLVItem.get(oTask.nNum);
		
		
		ImageView img = (ImageView) vItem.findViewById(R.id.img);
		img.setImageBitmap(oTask.bmThumb);
		
		

		if(oTask.bmThumb == null){
			
			//System.out.println("updateBitmap: thumb null");
			
			if(oTask.source == 5){
				System.out.println("updateBitmap@SearchResultList------------------优酷数据源，不使用默认图标----------");
			}else{
				Resources res=getResources();
				Bitmap bmp=BitmapFactory.decodeResource(res, R.drawable.default_book);
				listInfo.get(oTask.nNum).setThumb(bmp);
			}
			
		}else{
			//System.out.println("updateBitmap: thumb not null");
			listInfo.get(oTask.nNum).setThumb(oTask.bmThumb);
		}
		
		//System.out.println("SearchResultList@updateBitmap");
		
	}
	
	private class DBThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
//			
//			try {
//				Thread.sleep(1000000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
			showFirstGroupList();
		}
		
	}
	
	private void showFirstGroupList() {
		
		System.out.println("showFirestGroupList()@SearchResultList---------获得搜索关键字，开始搜索--------------");
		
		//Bundle bundle = this.getIntent().getExtras();
		
		cDBpersistent = new CDBPersistent(SearchResultList.this,"recent_reading");
		
		cDBpersistent.open();
		cDBpersistent.insertKey(search_context);
		Cursor cursor = cDBpersistent.getAllInfo(search_context);
		//cDBpersistent.insertKey(search_context);
		
		cDBpersistent.close();
		
		System.out.println("showFirestGroupList()@SearchResultList---------将搜索关键字存入数据库，试图从数据库中搜索-----------");
		
		
		if(cursor.getCount() != 0){
			System.out.println("showFirestGroupList()@SearchResultList---------从数据库中搜索到结果-----------");
			flags = true;
			WenkuInfo info;
			for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
			{
				info = new WenkuInfo();
				info.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				info.setAbst1(cursor.getString(cursor.getColumnIndex("abst1")));
				info.setLink(cursor.getString(cursor.getColumnIndex("link")));
				
				info.setSource(cursor.getInt(cursor.getColumnIndex("source")));
				
				arrbt = cursor.getBlob(cursor.getColumnIndex("thumb"));
				Bitmap bm = BitmapFactory.decodeByteArray(arrbt, 0, arrbt.length);
				
				info.setThumb(bm);
				listInfo.add(info);
				
			}
			Message msg = new Message();
			msg.obj = listInfo;
			handler1.sendMessage(msg);
			
		}else{
			System.out.println("showFirestGroupList()@SearchResultList---------从数据库中没有搜索到结果,启新线程搜索-----------");
			String search_context_utf8 = null;
			String search_context_gbk = null;
			try {
				search_context_utf8 = URLEncoder.encode(search_context, "UTF-8");
				System.out.println("search_context_3g"+search_context_utf8);
				search_context_utf8 = search_context_utf8.replace("+", "%20");
				System.out.println("search_context_3g"+search_context_utf8);
				
				search_context_gbk = URLEncoder.encode(search_context, "GB2312");
				System.out.println("search_context_3g"+search_context_gbk);
				search_context_gbk = search_context_gbk.replace("+", "%20");
				System.out.println("search_context_3g"+search_context_gbk);
	
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String mkUrl = getString(R.string.url_mk)+"?key="+search_context_gbk;
			String szUrl = getString(R.string.url_baiduwenku)+"?key="+search_context_gbk;
			String szUrl_3G = getString(R.string.url_baiduwenku3g)+"?key="+search_context_utf8;
			String iaskUrl = getString(R.string.url_iask)+"?key="+search_context_gbk;
			String youkuURL = getString(R.string.url_youku)+"?key="+search_context_utf8;;
			String xs = getString(R.string.url_baiduxs)+"?key="+search_context_utf8;
			String googleUrl = getString(R.string.url_googlebook)+"&title="+search_context_utf8;
			
			//System.out.println("xs--------------"+xs);
			
			IParse parse = new WenkuInfoParse(WENKU);
			IParse parse1 = new WenkuInfoParse(IASK);
			IParse parse2 = new WenkuInfoParse(MK);
			IParse parse3 = new WenkuInfoParse(WENKU3G);
			IParse parse4 = new WenkuInfoParse(YOUKU);
			IParse parse5 = new WenkuInfoParse(XIAOSHUO);
			IParse parse6 = new WenkuInfoParse(GOOGLEBOOK);
			//System.out.println("handler");
			
			ExecutorService pool = Executors.newFixedThreadPool(3); 
			
			CThread thread = new CThread(szUrl, parse, handler1, WENKU);
			CThread thread1 = new CThread(iaskUrl, parse1, handler1, IASK);
			CThread thread2 = new CThread(mkUrl, parse2, handler1, MK);
			CThread thread3 = new CThread(szUrl_3G, parse3, handler1, WENKU3G);
			CThread thread4 = new CThread(youkuURL, parse4, handler1, YOUKU);
			CThread thread5 = new CThread(xs, parse5, handler1, XIAOSHUO);
			CThread thread6 = new CThread(googleUrl, parse6, handler1, GOOGLEBOOK);
			
			String source = PushReciever.read(sourcePath);
			int s = 12;
			if(source != ""){
				s = Integer.parseInt(source);
				System.out.println("showFirestGroupList()@SearchResultList---------从搜索源设置文件钟，获取设置的搜索源信息-------"+ s);
				for(int i = 0;i<source.length();i++){
					switch (s%10) {
					case 1:
						s = s/10;
						System.out.println("showFirestGroupList()@SearchResultList---------文库搜索源-------");
						pool.execute(thread); 
						
						break;
					case 2:
						s = s/10;
						System.out.println("showFirestGroupList()@SearchResultList---------新浪IASK搜索源-------");
						pool.execute(thread1);
						
						break;
					case 3:
						s = s/10;
						System.out.println("showFirestGroupList()@SearchResultList----------群落阅读搜索源------");
						pool.execute(thread2);
						
						break;
					case 4:
						s = s/10;
						System.out.println("showFirestGroupList()@SearchResultList-----------百度3G搜索源-----");
						pool.execute(thread3);
						
						break;
					case 5:
						s = s/10;
						System.out.println("showFirestGroupList()@SearchResultList-----------优酷搜索源-----");
						pool.execute(thread4);
						
						break;
					case 6:
						s = s/10;
						System.out.println("showFirestGroupList()@SearchResultList------------百度小说搜索源----");
						pool.execute(thread5);
						
						break;
					case 7:
						s = s/10;
						System.out.println("showFirestGroupList()@SearchResultList------------谷歌图书----");
						pool.execute(thread6);
						
						break;

					default:
						System.out.println("showFirestGroupList()@SearchResultList------------default-文库搜索源--新浪IASK搜索源----");
						pool.execute(thread); 
						pool.execute(thread1); 
						break;
					} 
				}
			}else{
				pool.execute(thread); 
				pool.execute(thread1); 
			}
			
//			int s = Integer.parseInt(source);
			
			
//			pool.execute(thread); 
//			pool.execute(thread1); 
//			pool.execute(thread2);
//			pool.execute(thread3);
//			pool.execute(thread4);
//			pool.execute(thread5);
			//锟截憋拷锟竭程筹拷 
			
			pool.shutdown(); 
			
			}//end else
			//thread.start();
	}
	
	private void showGroupList(){
		
		System.out.println("RecentReading List");
		cDBpersistent = new CDBPersistent(SearchResultList.this,"recent_reading");
		
		cDBpersistent.open();
		
		Cursor cursor = cDBpersistent.getAllInfo();
		
		cDBpersistent.close();
		
		WenkuInfo info;
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
		{
			info = new WenkuInfo();
			info.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			info.setAbst1(cursor.getString(cursor.getColumnIndex("abst1")));
			info.setLink(cursor.getString(cursor.getColumnIndex("link")));
			
			info.setSource(cursor.getInt(cursor.getColumnIndex("source")));
			
			arrbt = cursor.getBlob(cursor.getColumnIndex("thumb"));
			Bitmap bm = BitmapFactory.decodeByteArray(arrbt, 0, arrbt.length);
			
			info.setThumb(bm);
			listInfo.add(info);
			
		}
		ListView lvSubInfo = (ListView) findViewById(R.id.lvGameInfo);
		
		CAdapter1 adapter = new CAdapter1(SearchResultList.this, listInfo.size() );
		
		lvSubInfo.setAdapter(adapter);
		
		
		myBar.setVisibility(View.GONE);
		barInfo.setVisibility(View.GONE);
		
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.searchresult_lv);
		Intent i = this.getIntent();
		Bundle b = i.getExtras();
		
		
		search_context = b.getString("search_context");
		
		
		
		
		barInfo = (TextView)findViewById(R.id.barInfo2);
		myBar = (ProgressBar)findViewById(R.id.myBarInfo);
		myBar.setVisibility(View.VISIBLE);
		barInfo.setVisibility(View.VISIBLE);
		myBar.setMax(100);
		
		System.out.println("SearchResultList------------@onCreate:  setProgressBar");
		
		lv = (ListView) findViewById(R.id.lvGameInfo);
		
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
					WenkuInfo entity = listInfo.get(arg2);
					
					/*记录最近阅读
					//System.out.println("Item Clicked "+arg2);
					
					//System.out.println("source:"+entity.getSource()+"link:"+entity.getLink());
					
					cDBpersistent = new CDBPersistent(SearchResultList.this,"recent_reading");
					
					cDBpersistent.open();
					
					cDBpersistent.deleteAll();
					
					cDBpersistent.insertInfo(entity);
					
					Cursor cursor = cDBpersistent.getAllInfo();
					
					cursor.close();
					
					cDBpersistent.close();
					*/
					
					Bundle oBundle = new Bundle();
					oBundle.putString("link",entity.getLink());
					
//					Intent oIntent = new Intent();
//					
//					oIntent.setClass(SearchResultList.this, CWebView.class);
//					oIntent.putExtras(oBundle);
					
					//Uri uri = Uri.parse("file://data/data/test.html");
					
					
					// add by liym 20110627
					Intent intent;
					switch (entity.getSource()) {
					case WENKU3G:
/*
						String filepath = Uri.parse(entity.getLink()) + ".olb";
						String param = "@jinke" + entity.getTitle() + "@jinke" + filepath;
						String type = "application/olb";
						
				        System.out.println("=================================");				        
				        System.out.println("param :"+param);
				        System.out.println("filepath :"+filepath);
				        System.out.println("=================================");
						
						File f = new File(filepath);
						intent = new Intent();
				        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        intent.setAction(android.content.Intent.ACTION_VIEW);  
				        intent.setDataAndType(Uri.fromFile(f),type);
				        intent.putExtra("JKVIEWERONLINE",param);
				        startActivity(intent);
*/
						String cmdKill = "/data/box/pkill eu.licentia.necessitas.industrius.example.jkviewer";
						String param = "@jinke" + entity.getTitle()
							+ "@jinke" + Uri.parse(entity.getLink()) + ".olb";
						String cmd = "am start -n eu.licentia.necessitas.industrius.example.jkviewer/eu.licentia.necessitas.industrius.QtActivity -e JKVIEWER " + param;
						//System.out.println("=================================");				        
				        //System.out.println("cmd :"+cmd);
				        //System.out.println("=================================");
				        try {
				        	Process process = Runtime.getRuntime().exec(cmdKill);
				        	process.waitFor();
				        }
				        catch(InterruptedException ie){
				        	ie.printStackTrace();
				        }
				        catch(IOException ioe){
				        	ioe.printStackTrace();
				        }
				        
				        try{
				        	Runtime.getRuntime().exec(cmd);
				        }
				        catch(IOException ioe) {
				        	ioe.printStackTrace();
				        }
						break;
					default:
						Uri uri = Uri.parse(entity.getLink());
						intent = new Intent(Intent.ACTION_VIEW, uri);  
						startActivity(intent);
					}
				
			}
			
		    public String getMIMEType(File f)
		    {
		      String type="";
		      String fName=f.getName();
		      /* 取得扩展名 */
		      String end=fName.substring(fName.lastIndexOf(".")
		      +1,fName.length()).toLowerCase();
		     
		      /* 依扩展名的类型决定MimeType */
		      if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
		      end.equals("xmf")||end.equals("ogg")||end.equals("wav"))
		      {
		        type = "audio";
		      }
		      else if(end.equals("3gp")||end.equals("mp4"))
		      {
		        type = "video";
		      }
		      else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
		      end.equals("jpeg")||end.equals("bmp"))
		      {
		        type = "image";
		      }
		      else if(end.equals("apk"))
		      {
		        /* android.permission.INSTALL_PACKAGES */
		        type = "application/vnd.android.package-archive";
		      }
		      else if(end.equals("wol")||end.equals("doc")||end.equals("txt")||end.equals("rtf")||end.equals("pdb")||end.equals("chm")||
		    		  end.equals("lit") || end.equals("olb"))
		      {
		    	  
		      }
		      else
		      {
		        type="*";
		      }
		      /*如果无法直接打开，就跳出软件列表给用户选择 */
		      if(end.equals("apk")||end.equals("wol")||end.equals("doc")||end.equals("txt")||end.equals("rtf")||end.equals("pdb")||end.equals("chm")||
		    		  end.equals("lit"))
		      {
		      }
		      else {
		        type += "/*"; 
		      }
		      return type; 
		    } 
		});
		
		if(b.getString("type") != null && b.getString("type").equals("recent_reading")){
			showGroupList();
		}else{
			//showFirstGroupList();
			DBThread showThread = new DBThread();
			showThread.start();
		}
		
		
	}
	
class CAdapter1 extends BaseAdapter {
		
		private LayoutInflater mInflater;
		private int size;

		public CAdapter1(Context context,int size ) {
			this.mInflater = LayoutInflater.from(context);
			//this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			this.size = size;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return size;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			
			String srcStr = "来源";
			int source = listInfo.get(position).getSource();
			//System.out.println("getView at "+position + " title"+listInfo.get(position).getTitle());
			
			//System.out.println("getView at "+position + " source"+source);
			if (convertView == null) {
				//System.out.println("converView@getView : null");E
				holder=new ViewHolder();  
				convertView = convertView = mInflater.inflate(R.layout.vlist, null);
				switch (source) {
				case MK:
					srcStr ="群落阅读";
					System.out.println("getView@source MK position:"+position);
					convertView = mInflater.inflate(R.layout.vlist, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					holder.deleteBtn = (Button)convertView.findViewById(R.id.view_btn2);
					break;
				case IASK:
					srcStr = "新浪IASK";
					System.out.println("getView@source IASK position:"+position);
					convertView = mInflater.inflate(R.layout.vlist1, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				case WENKU:
				
					srcStr = "百度文库";
					System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;	
				case WENKU3G:
					srcStr = "百度文库3G";
					System.out.println("getView@source WENKU3G position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;	
				case YOUKU:
					srcStr = "优酷空间";
					System.out.println("getView@source YOUKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist_youku, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					holder.img = (ImageView)findViewById(R.id.img);
					//holder.img.setBackgroundResource(R.drawable.default_img);
					break;	
				case XIAOSHUO:
					srcStr = "百度小说";
					System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				case GOOGLEBOOK:
					srcStr = "谷歌图书";
					//System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				default:
					break;
				}
				
				convertView.setTag(holder);
				
			}else {
				//System.out.println("converView@getView : not null");
				
				//holder = (ViewHolder)convertView.getTag();
				
				holder=new ViewHolder();  
				
				switch (source) {
				case MK:
					srcStr ="群落阅读";
					//System.out.println("getView@source MK position:"+position);
					convertView = mInflater.inflate(R.layout.vlist, null);
					
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					holder.deleteBtn = (Button)convertView.findViewById(R.id.view_btn2);
					break;
				case IASK:
					srcStr = "新浪IASK";
					//System.out.println("getView@source IASK position:"+position);
					convertView = mInflater.inflate(R.layout.vlist1, null);
					
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				case WENKU:
					srcStr = "百度文库";
					//System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				case WENKU3G:
					srcStr = "百度文库3G";
					//System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				case YOUKU:
					srcStr = "优酷空间";
					System.out.println("getView@source YOUKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist_youku, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					holder.img = (ImageView)findViewById(R.id.img);
					//holder.img.setBackgroundResource(R.drawable.default_img);
					break;
				case XIAOSHUO:
					srcStr = "百度小说";
					System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				case GOOGLEBOOK:
					srcStr = "谷歌图书";
					//System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView)convertView.findViewById(R.id.title);
					holder.img = (ImageView)convertView.findViewById(R.id.img);
					holder.info = (TextView)convertView.findViewById(R.id.info);
					holder.source = (TextView)convertView.findViewById(R.id.source);
					holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
					break;
				default:
					break;
				}
				
			}
			
			
			
			if(position % 2 == 1){
				convertView.setBackgroundColor(Color.LTGRAY);
			}else{
				convertView.setBackgroundColor(Color.WHITE);
			} 
			if(holder!= null){
				
				if(holder.img == null){
					System.out.println("holder img is null");
				}else{
					System.out.println("holder img is not null");
					holder.img.setImageBitmap(listInfo.get(position).getThumb());
				}
				
				//holder.img.setImageBitmap(listInfo.get(position).getThumb());
				
				
				
				holder.title.setText(listInfo.get(position).getTitle());
				
				
				String ab1 = listInfo.get(position).getAbst1();
				if(ab1 != null){
					System.out.println("abst1 length : "+ab1.length());
					
					if(ab1.length() > INFOLENGTH){
						ab1 = ab1.substring(0, INFOLENGTH);
					}
				}
				holder.info.setText(ab1);
				//holder.info.setText(listInfo.get(position).getAbst1());	
				
				
				
				holder.source.setText(srcStr);
				
					if(holder.viewBtn != null){
				
						final int po = position;
						
					holder.viewBtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							System.out.println("Button clicked"+po);
							
							WenkuInfo entity = listInfo.get(po);
							
							Bundle oBundle = new Bundle();
							oBundle.putString("link",entity.getLink());
							
							Intent oIntent = new Intent();
							
							oIntent.setClass(SearchResultList.this, CWebView.class);
							oIntent.putExtras(oBundle);
							
							startActivity(oIntent);
							
							
						}
					});
				}else{
					//System.out.println("Button1 is null");
				}
				
				if(holder.deleteBtn != null){
					holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							System.out.println("Button 2 clicked");
							//((Button)findViewById(R.id.view_btn2)).setBackgroundResource(R.drawable.delete_down);
							DisplayToast("导出功能--待完成");
						}
					});
				}else{
					//System.out.println("Button2 is null");
				}
			}
			return convertView;
			
			
			//return lsView.get(position);
		}
	}

}
	
