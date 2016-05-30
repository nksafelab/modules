package jinke.readings;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import jinke.readings.Entity.SynInfo;
import jinke.readings.Entity.Syn_subInfo;
import jinke.readings.Entity.WenkuInfo;
import jinke.readings.SearchResultActivity.CAdapter;
import jinke.readings.SearchResultActivity.ViewHolder;
import jinke.readings.download.CNetTransfer;
import jinke.readings.parse.CBitmapParse;
import jinke.readings.parse.IParse;
import jinke.readings.parse.SynInfoParse;
import jinke.readings.util.CNetQueueThread;
import jinke.readings.util.CTask;
import jinke.readings.util.CThread;
import jinke.readings.util.PriceQueueThread;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SynDResultListActivity extends Activity {

	private final int SYN_DATA_SEARCH = 1;
	private final int SYN_PAPER_SEARCH = 2;
	private final int SEARCH = 3;

	private String search_content;
	private String search_content_utf8;
	private int search_type = 0;

	private ListView syn_listview;
	private ProgressBar myBar;
	private TextView barInfo;
	private int nID = 0;

	List<SynInfo> listInfo = new ArrayList<SynInfo>();
	private List<View> lsLVItem = new ArrayList<View>();
	private Queue<CTask> queueTask = null;
	private SynAdapter sadapter;
	private boolean flags = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.synsearchresult_lv);

		/*
		 * 获得上个Activity传递来的参数,初始化本Activity参数
		 */
		getIntentExtras();

		initView();

		try {
			search_content_utf8 = URLEncoder.encode(search_content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		searchStart();

	}

	private void searchStart() {
		String szUrl = "http://61.181.14.184:8084/MultiSearch/search.do?method=searchzhituInDatebase&bookname="
				+ search_content_utf8 + "&xmltag=1";
		Log.v("SynSearchResult", szUrl);
		IParse parse = new SynInfoParse(1);
		CThread thread = new CThread(szUrl, parse, handler, 1);
		thread.start();
		myBar.setVisibility(View.VISIBLE);
		barInfo.setVisibility(View.VISIBLE);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// System.out.println("get handler message");
			myBar.setVisibility(View.GONE);
			barInfo.setVisibility(View.GONE);
			
			listInfo = (List<SynInfo>) msg.obj;
			
			//Queue<CTask> queueTask = new LinkedList<CTask>();
			if(queueTask == null){
				queueTask = new LinkedList<CTask>();
			}

			LayoutInflater oInflater = LayoutInflater.from(SynDResultListActivity.this);
			View vItemRoot = null;
			SynInfo entity = null;
			CTask oTask = null;
			if(listInfo != null){
				for (int i = 0; i < listInfo.size(); i++, nID++) {
					entity = listInfo.get(i);
					oTask = new CTask();
					System.out.println("@handler----"+listInfo.size());
					
					oTask.nNum = nID;
					oTask.szUrl = entity.getThumburl();
					
					Syn_subInfo s;
					for(int j=0;j<entity.getLs_subInfo().size();j++){
						s = entity.getLs_subInfo().get(j);
						switch (s.getWebsrc()) {
						case 1:
							oTask.priceUrl1 = s.getWeburl();
							break;
						case 2:
							oTask.priceUrl2 = s.getWeburl();
							break;
						default:
							break;
						}
					}
					
					queueTask.offer(oTask);
					try{
						vItemRoot = oInflater.inflate(R.layout.syn_listitem, null);
					}catch (Exception e) {
						
					}
					lsLVItem.add(vItemRoot);
				}//for
			}

			
			if(listInfo != null){
				sadapter = new SynAdapter(SynDResultListActivity.this, null);
				syn_listview.setAdapter(sadapter);
			}
			
			updateBitmapList(queueTask);
			
			super.handleMessage(msg);

		}
	};
	
	private void updateBitmapList(Queue<CTask> queueTask) {
		System.out.println("updateBitmapList");
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
			sadapter.notifyDataSetChanged();
			
			super.handleMessage(msg);
		}
	};
	private void updateBitmap(CTask oTask) {
		View vItem = lsLVItem.get(oTask.nNum);
		ImageView img = (ImageView) vItem.findViewById(R.id.syn_listitem_img);
		img.setImageBitmap(oTask.bmThumb);
		if(oTask.bmThumb == null){
				Resources res=getResources();
				Bitmap bmp=BitmapFactory.decodeResource(res, R.drawable.default_book);
				listInfo.get(oTask.nNum).setThumb(bmp);
		}else{
			listInfo.get(oTask.nNum).setThumb(oTask.bmThumb);
		}
	}

	private void initView() {
		syn_listview = (ListView) findViewById(R.id.syn_listview);
		myBar = (ProgressBar) findViewById(R.id.syn_myBar);
		barInfo = (TextView) findViewById(R.id.syn_barInfo);
	}

	private void getIntentExtras() {
		Intent mIntent = getIntent();
		Bundle mBundle = mIntent.getExtras();
		if (mBundle != null) {
			search_content = mBundle.getString("search_content");
			search_type = mBundle.getInt("search_type");
			switch (search_type) {
			case SYN_DATA_SEARCH:
				// toast("数图搜索:"+search_content);
				break;
			case SYN_PAPER_SEARCH:
				// toast("纸图搜索:"+search_content);
				break;

			default:
				break;
			}
		}
	}

	private void toast(String msg) {
		Toast.makeText(SynDResultListActivity.this, msg, Toast.LENGTH_SHORT)
				.show();
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

		public SynAdapter(Context context, List<View> lsView) {
			this.mInflater = LayoutInflater.from(context);
			// this.mInflater =
			// (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.lsView = lsView;
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
				convertView = mInflater.inflate(R.layout.synd_listitem, null);

				holder.syn_listitem_img = (ImageView) convertView
							.findViewById(R.id.syn_listitem_img);
				holder.syn_listitem_buy360Img = (ImageView) convertView
						.findViewById(R.id.syn_listitem_buy360Img);
				holder.syn_listitem_dangdangImg = (ImageView) convertView
						.findViewById(R.id.syn_listitem_dangdangImg);
				holder.syn_listitem_title = (TextView) convertView
						.findViewById(R.id.syn_listitem_title);
				holder.syn_listitem_360fee = (TextView) convertView
						.findViewById(R.id.syn_listitem_360fee);
				holder.syn_listitem_dangdangfee = (TextView) convertView
						.findViewById(R.id.syn_listitem_dangdangfee);
				holder.syn_listitem_author = (TextView) convertView
						.findViewById(R.id.syn_listitem_author);
				holder.syn_listitem_publisher = (TextView) convertView
						.findViewById(R.id.syn_listitem_publisher);
				holder.syn_listitem_tags = (TextView) convertView
						.findViewById(R.id.syn_listitem_tags);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if(listInfo.get(position).getThumb() != null){
				holder.syn_listitem_img.setImageBitmap(listInfo.get(position).getThumb());
			}else{
//				holder.syn_listitem_img.setBackgroundResource(R.drawable.default_book);
			}
			
			
			holder.syn_listitem_title.setText(listInfo.get(position).getTitle());
			holder.syn_listitem_author.setText("作者:\t"+listInfo.get(position).getAuthor());
			holder.syn_listitem_publisher.setText("出版社:\t"+listInfo.get(position).getPubliser());
			
			String tags = listInfo.get(position).getIntrduction();
			if(tags!=null && tags.length()>=20){
				tags = listInfo.get(position).getIntrduction().substring(0,20).trim();
			}
			holder.syn_listitem_tags.setText("摘要:\t"+tags);
			
			int size_limit = listInfo.get(position).getLs_subInfo().size();
			if(size_limit >2){
				size_limit = 2;
			}
			for(int i=0;i<size_limit;i++)
			{
				if(listInfo.get(position).getLs_subInfo().get(i).getWebsrc() == 1){
					final String url = listInfo.get(position).getLs_subInfo().get(i).getWeburl();
					if(flags){
						holder.syn_listitem_buy360Img.setBackgroundResource(R.drawable.fengyun);
						holder.syn_listitem_buy360Img.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								toast(url);
								Uri uri = Uri.parse(url); 
								Intent intent =new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);
							}
						});
					}else{
						holder.syn_listitem_dangdangImg.setBackgroundResource(R.drawable.fengyun);
						holder.syn_listitem_dangdangImg.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								toast(url);
								Uri uri = Uri.parse(url); 
								Intent intent =new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);
							}
						});
					}
					
					
				}else if(listInfo.get(position).getLs_subInfo().get(i).getWebsrc() == 2){
					final String url = listInfo.get(position).getLs_subInfo().get(i).getWeburl();
					
					if(flags){
						holder.syn_listitem_buy360Img.setBackgroundResource(R.drawable.hongxiu);
						holder.syn_listitem_buy360Img.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								toast(url);
								Uri uri = Uri.parse(url); 
								Intent intent =new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);
							}
						});
					}else{
						holder.syn_listitem_dangdangImg.setBackgroundResource(R.drawable.hongxiu);
						holder.syn_listitem_dangdangImg.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								toast(url);
								Uri uri = Uri.parse(url); 
								Intent intent =new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);
							}
						});
					}
					
				}else if(listInfo.get(position).getLs_subInfo().get(i).getWebsrc() == 3){
					
					final String url = listInfo.get(position).getLs_subInfo().get(i).getWeburl();
					
					if(flags){
						holder.syn_listitem_buy360Img.setBackgroundResource(R.drawable.huanjian);
						holder.syn_listitem_buy360Img.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								toast(url);
								Uri uri = Uri.parse(url); 
								Intent intent =new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);
							}
						});
					}else{
						holder.syn_listitem_dangdangImg.setBackgroundResource(R.drawable.huanjian);
						holder.syn_listitem_dangdangImg.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								toast(url);
								Uri uri = Uri.parse(url); 
								Intent intent =new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);
							}
						});
					}
				}else if(listInfo.get(position).getLs_subInfo().get(i).getWebsrc() == 4){
					final String url = listInfo.get(position).getLs_subInfo().get(i).getWeburl();
					
					if(flags){
						holder.syn_listitem_buy360Img.setBackgroundResource(R.drawable.sina);
						holder.syn_listitem_buy360Img.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								toast(url);
								Uri uri = Uri.parse(url); 
								Intent intent =new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);
							}
						});
					}else{
						holder.syn_listitem_dangdangImg.setBackgroundResource(R.drawable.sina);
						holder.syn_listitem_dangdangImg.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								toast(url);
								Uri uri = Uri.parse(url); 
								Intent intent =new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);
							}
						});
					}
				}
				
				
				
				
				
			}
			
				
			return convertView;
		}
	}

}
