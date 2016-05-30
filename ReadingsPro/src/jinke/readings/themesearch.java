package jinke.readings;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import jinke.readings.Entity.SynInfo;
import jinke.readings.Entity.Syn_subInfo;
import jinke.readings.parse.CBitmapParse;
import jinke.readings.parse.IParse;
import jinke.readings.parse.SynInfoParse_theme;
import jinke.readings.parse.ThemeParse;
import jinke.readings.util.CNetQueueThread;
import jinke.readings.util.CTask;
import jinke.readings.util.CThread;
import jinke.readings.wedget.Workspace;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class themesearch extends Activity {
	
	private Button themesearch_button;
	private Button theme_button;
	private EditText searchEdt;
	private ProgressBar theme_bar;
	private TextView theme_loading;
	private ListView theme_list;
	private InputMethodManager imm;
	private Workspace workspace;
	private ArrayAdapter<String> theme_adapter;
	
	private List<SynInfo> listInfo = new ArrayList<SynInfo>();
	private List<Syn_subInfo> ls_subInfo = new ArrayList<Syn_subInfo>();
	
	private Queue<CTask> queueTask = null;
	
	LayoutInflater inflate;
	View view;
    TextView t;
    RelativeLayout l;
    ProgressBar result_bar;
    TextView result_loading;
    
    private SynAdapter sadapter;
	
	private ArrayList<String> data = new ArrayList<String>();
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.themesearch);
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        
        
        
        inflate = LayoutInflater.from(themesearch.this);
        workspace = (Workspace)findViewById(R.id.workspace);
        searchEdt = (EditText)findViewById(R.id.search_Edit);
        
        themesearch_button = (Button)findViewById(R.id.searchButton);
        theme_button = (Button)findViewById(R.id.themeButton);
        theme_button.setVisibility(View.GONE);
        theme_bar = (ProgressBar)findViewById(R.id.result_bar);
        theme_loading = (TextView)findViewById(R.id.result_loading);
        
        searchEdt.setFocusable(true);
        searchEdt.setFocusableInTouchMode(true);
        searchEdt.clearFocus();
        
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE); 
        
        themesearch_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imm.hideSoftInputFromWindow(searchEdt.getWindowToken(), 0);
				String title = searchEdt.getText().toString();
				
				search(title);
				
			}
		});
        
        Intent mIntent = getIntent();
        String search_context = "";
        if(mIntent != null){
        	search_context = mIntent.getExtras().getString("search_context");
        	searchEdt.setText(search_context);
        	search(search_context);
        }
        
 	    }//end onCreat
    
    private void search(String title){
    	String theme_URL = "http://61.181.14.184:8084/MultiSearch/theme.do?method=getWordFromWikiPage&title="+title+"&inputT=0&xmltag=1";
//		view = workspace.getChildAt(0);
		view = workspace.getChildAt(workspace.getCurrentScreen());
		Log.e("current", "current:"+workspace.getCurrentScreen());
		
		
		l = (RelativeLayout)view.findViewById(R.id.linear2);
    	l.setOnClickListener(null);
        t = (TextView)view.findViewById(R.id.result_title);
        t.setText("主题:"+title);
        
        theme_bar.setVisibility(View.VISIBLE);
        theme_loading.setVisibility(View.VISIBLE);
        
        workspace.removeAllViews();
        workspace.addView(view);
        
        workspace.bringChildToFront(view);
        workspace.snapToScreen(0);
        try {
			title = URLEncoder.encode(title, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String result_URL = "http://61.181.14.184:8084/MultiSearch/theme.do?method=searchThemeXml&themeWord="+title;
        IParse result_parse = new SynInfoParse_theme();
		CThread result_thread = new CThread(result_URL, result_parse, result_handler);
		result_thread.start();
        
        
        IParse parse = new ThemeParse();
		CThread thread = new CThread(theme_URL, parse, themehandler);
		thread.start();
    }
    
 // 获得线程传回来的List，更新界面TextView，并更新数据库
	Handler back_result_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// System.out.println("get handler message");
			Log.e("return", "back_result_handler");
			if(msg.arg1 == -1){
//				dialog("不能链接到服务器，请检测网络");
				
//				int nView = msg.arg2;
//				Log.e("AndroidRuntime", "nView"+nView+" exception");
//				View v = workspace.getChildAt(nView);
//				Log.v("view", "nView"+nView+(v == null));
//				if(v != null && workspace != null){
//					workspace.removeView(v);
//				}
				Log.e("result", "errir:");
			}else{
				theme_bar.setVisibility(View.GONE);
				theme_loading.setVisibility(View.GONE);
				int nView = msg.arg1;
				
				listInfo = (List<SynInfo>)msg.obj;
				Log.e("AndroidRuntime", "nView"+nView+" size:"+listInfo.size());
//				if(listInfo.size() == 0){
//					View v = workspace.getChildAt(nView);
//					Log.v("view", "nView"+nView+(v == null));
//					if(v != null && workspace != null){
//						workspace.removeView(v);
//					}
//				}else{
								
				SynInfo info;
				//update bitmap=======================
				
				queueTask = new LinkedList<CTask>();
				
				CTask oTask = null;
				Log.e("CThread","nView:"+nView+(listInfo == null));
				for(int i=0;i<listInfo.size();i++){
					Log.e("title", "==================================");
					info = listInfo.get(i);
					Log.e("title", info.getTitle());
					Log.e("author", info.getAuthor());
					Log.e("publisher", info.getPubliser());
					Log.e("intrduction", info.getIntrduction().trim());
					
					oTask = new CTask();
					oTask.nNum = i;
					oTask.szUrl = info.getThumburl();
					
					queueTask.offer(oTask);
					
					updateBitmapList(queueTask,nView);
				}
				
				
				if(listInfo != null){
					
					view = workspace.getChildAt(nView);
					if(view != null){
						TextView t = (TextView)view.findViewById(R.id.result_title);
						Log.e("result", "nView:"+nView+" title:"+t.getText());
						ListView syn_listview = (ListView)view.findViewById(R.id.result_list);
						sadapter = new SynAdapter(themesearch.this, null,listInfo);
						syn_listview.setAdapter(sadapter);
					}
					
				}
//			}
			}//endif
			super.handleMessage(msg);

		}
	};
    
 // 获得线程传回来的List，更新界面TextView，并更新数据库
	Handler result_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// System.out.println("get handler message");
			
			view = workspace.getChildAt(0);
			ListView syn_listview = (ListView)view.findViewById(R.id.result_list);
			
			theme_bar.setVisibility(View.GONE);
			theme_loading.setVisibility(View.GONE);
			
			if(msg.arg1 == -1){
//				dialog("不能链接到服务器，请检测网络");
				Log.e("result", "errir:");
			}else{
				listInfo = (List<SynInfo>)msg.obj;
				Log.e("result", "return:"+listInfo.size());
				SynInfo info;

				if(listInfo != null){
					sadapter = new SynAdapter(themesearch.this, null,listInfo);
					syn_listview.setAdapter(sadapter);
					
				}
				
				//update bitmap=======================
				
				queueTask = new LinkedList<CTask>();
				
				CTask oTask = null;
				for(int i=0;i<listInfo.size();i++){
					Log.e("title", "==================================");
					info = listInfo.get(i);
					Log.e("title", info.getTitle());
					Log.e("author", info.getAuthor());
					Log.e("publisher", info.getPubliser());
					Log.e("intrduction", info.getIntrduction().trim());
					
					List<Syn_subInfo> ls_subInfo = info.getLs_subInfo();
					Syn_subInfo subInfo;
						for(int j=0;j<ls_subInfo.size();j++){
							subInfo = ls_subInfo.get(j);
							Log.e("result_handler", "	websrc:"+subInfo.getWebsrc());
							Log.e("result_handler", "	weburl:"+subInfo.getWeburl());
						}
					
					oTask = new CTask();
					oTask.nNum = i;
					oTask.szUrl = info.getThumburl();
					
					queueTask.offer(oTask);
					
					updateBitmapList(queueTask,0);
				}
				
				
			}//endif
			super.handleMessage(msg);

		}
	};
	private void updateBitmapList(Queue<CTask> queueTask, int nView) {
		System.out.println("updateBitmapList");
		IParse parse = new CBitmapParse();
		CNetQueueThread bmpThread = new CNetQueueThread(queueTask, parse,
				handlerImgUpdate,nView);
		
		bmpThread.start();
		
	}
	Handler handlerImgUpdate = new Handler(Looper.myLooper()) {
		
		@Override
		public void handleMessage(Message msg) {
			CTask oTask = (CTask) msg.obj;
			int nView = msg.arg1;
			
			
			
			
			View view = workspace.getChildAt(nView);
			if(view != null){
				ListView l = (ListView)view.findViewById(R.id.result_list);
				if(l != null){
//					View item_view = l.getChildAt(oTask.nNum);
//					View i = l.getAdapter().getView(oTask.nNum, null, l);
//					TextView t = (TextView)i.findViewById(R.id.title);
//					t.setText("@@"+t.getText());
				
				
					SynAdapter adapter = (SynAdapter)l.getAdapter();
					if(adapter != null)
					if(adapter.listInfo != null){
						if(adapter.listInfo.size() != 0){
							Log.e("AndroidRuntime", (adapter.listInfo.get(oTask.nNum) == null) + "nView"+nView);
							adapter.listInfo.get(oTask.nNum).setThumb(oTask.bmThumb);
		//					Log.e("title", "===========nView"+nView+" nNum:"+oTask.nNum+" t:"+(t == null) + "title:"+t.getText());
		//					Log.e("title", "           url:"+oTask.szUrl);
							
							adapter.notifyDataSetChanged();
						}
					}
				}
			}
			super.handleMessage(msg);
		}
	};
    
    // 获得线程传回来的List，更新界面TextView，并更新数据库
	Handler themehandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// System.out.println("get handler message");
			if(msg.arg1 == -1){
//				dialog("不能链接到服务器，请检测网络");
			}else{
				data = (ArrayList<String>)msg.obj;
				
				if(data.size() == 0){
//					dialog("没有搜索结果,请尝试更换关键词");
				}else{
					for(int i=1;i<data.size();i++){
						//System.out.println("BookName: "+lsBook.get(i).getBookName());
						Log.v("theme",i+ "ThemeTitle:"+data.get(i));
						//Toast.makeText(main.this, "comes:"+data.size(), Toast.LENGTH_SHORT).show();
				        	view = inflate.inflate(R.layout.search_result, null,false);
				        	l = (RelativeLayout)view.findViewById(R.id.linear2);
				        	l.setOnClickListener(null);
				        	
				        	t = (TextView)view.findViewById(R.id.result_title);
				        	t.setText(i+"相似主题:"+data.get(i));
				        	String t = data.get(i);
				        	try {
								t = URLEncoder.encode(t, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				        	String result_URL = "http://61.181.14.184:8084/MultiSearch/theme.do?method=searchThemeXml&themeWord="+t;
				        	Log.v("theme",i+ "ThemeTitle URL:"+result_URL);
				        	IParse result_parse = new SynInfoParse_theme();
							CThread result_thread = new CThread(result_URL, result_parse, back_result_handler,i);
							result_thread.start();
				        	
//				        	t = (TextView)view.findViewById(R.id.result_same_title);
//				        	t.setText("相似主题");
				        	
				        	workspace.addView(view);
						
					}
				}//end if
			}//endif
			super.handleMessage(msg);

		}
	};
    
	    private List<String> getData(){
 	         
 	        return data;
 	    }
	    
	    
	    public final class ViewHolder {
			public ImageView syn_listitem_img;
			public ImageView syn_listitem_buy360Img;
			public ImageView syn_listitem_dangdangImg;
			public TextView syn_listitem_title;
			public TextView syn_listitem_360fee;
			public TextView syn_listitem_dangdangfee;
			public TextView syn_listitem_author;
			public TextView syn_listitem_publisher;
			public TextView syn_listitem_tags;
		}

		class SynAdapter extends BaseAdapter {

			private LayoutInflater mInflater;
			List<View> lsView;
			public List<SynInfo> listInfo = new ArrayList<SynInfo>();
			
			public SynAdapter(Context context, List<View> lsView,List<SynInfo> listInfo) {
				this.mInflater = LayoutInflater.from(context);
				// this.mInflater =
				// (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				this.lsView = lsView;
				this.listInfo = listInfo;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return listInfo.size();
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

				if (convertView == null) {

					holder = new ViewHolder();

					System.out.println("getView@source MK position:" + position);
					convertView = inflate.inflate(R.layout.syn_theme_listitem, null);
					
					holder.syn_listitem_title = (TextView) convertView
							.findViewById(R.id.title);
					holder.syn_listitem_author = (TextView) convertView.findViewById(R.id.author);
					holder.syn_listitem_publisher = (TextView) convertView.findViewById(R.id.syn_listitem_publisher);
					holder.syn_listitem_tags = (TextView) convertView.findViewById(R.id.syn_listitem_tags);
					holder.syn_listitem_img = (ImageView) convertView.findViewById(R.id.syn_listitem_img);
					
					holder.syn_listitem_buy360Img = (ImageView)convertView.findViewById(R.id.buy360Img);
					holder.syn_listitem_360fee = (TextView)convertView.findViewById(R.id.syn_listitem_360fee);
					holder.syn_listitem_dangdangImg = (ImageView)convertView.findViewById(R.id.syn_listitem_dangdangImg);
					holder.syn_listitem_dangdangfee = (TextView)convertView.findViewById(R.id.syn_listitem_dangdangfee);
					
					convertView.setTag(holder);

				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				if(position == 0){
					Log.e("adapter", "	position:"+position+"============================");
					Log.e("adapter", "	title:"+listInfo.get(position).getTitle());
				}
				boolean img360 = false;
				boolean imgdang = false;
				
				Log.v("listInfo", listInfo.size()+"");
				List<Syn_subInfo> ls_subInfo = listInfo.get(position).getLs_subInfo();
				Syn_subInfo subInfo;
					for(int j=0;j<ls_subInfo.size();j++){
						subInfo = ls_subInfo.get(j);
						if(subInfo.getWebsrc() == 1){
							Log.e("adapter","position"+ position+ "setdangdang ");
							imgdang = true;
						}
						if(subInfo.getWebsrc() == 2){
							Log.e("adapter","position"+ position+ "set 360 ");
							img360 = true;
						}
						Log.e("adapter", "	websrc:"+subInfo.getWebsrc());
						Log.e("adapter", "	weburl:"+subInfo.getWeburl());
					}
				
					
				
				
				if(listInfo.get(position).getThumb() != null){
					holder.syn_listitem_img.setImageBitmap(listInfo.get(position).getThumb());
				}else{
					holder.syn_listitem_img.setBackgroundResource(R.drawable.default_book);
				}
				
				if(holder.syn_listitem_title != null)
				holder.syn_listitem_title.setText(listInfo.get(position).getTitle());
				
				if(holder.syn_listitem_author != null)
					holder.syn_listitem_author.setText("作者: "+listInfo.get(position).getAuthor());
				
				if(holder.syn_listitem_publisher != null)
					holder.syn_listitem_publisher.setText("出版社: "+listInfo.get(position).getPubliser());
				
				if(holder.syn_listitem_tags != null)
					holder.syn_listitem_tags.setText("摘要: "+listInfo.get(position).getIntrduction().trim());
				
					
				return convertView;
			}
		}
		
		@Override
		public void finish() {
			// TODO Auto-generated method stub
			super.finish();
			Log.v("finish", "finish==============");
		}
	    
}
