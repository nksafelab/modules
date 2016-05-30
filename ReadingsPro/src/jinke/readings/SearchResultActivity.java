package jinke.readings;

import hallelujah.util.ScreenShot;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jinke.readings.Entity.Book;
import jinke.readings.Entity.User;
import jinke.readings.Entity.WenkuInfo;
import jinke.readings.datebase.CDBPersistent;
import jinke.readings.download.CNetTransfer;
import jinke.readings.parse.BookParse;
import jinke.readings.parse.CBitmapParse;
import jinke.readings.parse.IParse;
import jinke.readings.parse.WenkuInfoParse;
import jinke.readings.util.CNetQueueThread;
import jinke.readings.util.CTask;
import jinke.readings.util.CThread;
import jinke.readings.util.PushReciever;
import jinke.readings.util.StrToHex;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SearchResultActivity extends Activity implements
		ListView.OnScrollListener, OnItemClickListener,
		android.view.View.OnClickListener {

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("remove on destory @@@@@@@@@@@@@@@");
		cDBpersistent.close();
		windowManager.removeView(txtOverlay);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		System.out.println("remove on destory @@@@@@@@@@@@@@@");
		windowManager.removeView(txtOverlay);
		super.onResume();
	}

	private CDBPersistent cDBpersistent;
	private byte[] arrbt = null;

	private List<View> lsLVItem = new ArrayList<View>();
	Queue<CTask> queueTask = null;

	// private CAdapter adapter = new CAdapter(SearchResultActivity.this,
	// lsLVItem);
	private CAdapter adapter;
	private int nID = 0;
	public static final int WENKU = 1;
	public static final int IASK = 2;
	public static final int MK = 3;
	public static final int WENKU3G = 4;
	public static final int YOUKU = 5;
	public static final int XIAOSHUO = 6;
	public static final int GOOGLEBOOK = 7;
	public static final int TXTR = 8;
	public static final int CARTOON = 9;
	public static final int P2P = 0;

	private static final int INFOLENGTH = 40;
	private static final int TITLELENGTH = 20;

	// private String sourcePath = "/sdcard/readings/searchSource"; rm by yjy
	private String sourcePath = Environment.getDataDirectory()
			+ "/data/jinke.readings/searchSource";

	private boolean flags = false;

	private List<Integer> sourceSearch = new ArrayList<Integer>();

	private Handler handler;
	private DisapearThread disapearThread;
	/** 标识List的滚动状态。 */
	private int scrollState;
	private TextView txtOverlay;
	private WindowManager windowManager;
	private int thread_num = 0;

	private String username = null;
	private String path = "http://61.181.14.184:8084/ReadingsSina/search.do?";
	private String request_book_urlGBK_Web = null;

	private String search_context_utf8 = null;
	private String search_context_gbk = null;
	private boolean sendMsg = false;

	public final class ViewHolder {
		// public ImageView side;
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

	private String B_WENKU;
	private String X_IASK;
	private String Q_QUNLUO;
	private String B_3G;
	private String Y_YOUKU;
	private String B_XIAOSHUO;
	private String G_GOOGLE;
	private String T_TXTR;
	private String D_CARTOON;

	TextView barInfo = null;
	// TextView tvFoot = null;
	ProgressBar myBar = null;
	ListView lv = null;
	List<WenkuInfo> listInfo = new ArrayList<WenkuInfo>();
	List<Book> request_list = new ArrayList<Book>();

	class CAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		List<View> lsView;

		public CAdapter(Context context, List<View> lsView) {
			this.mInflater = LayoutInflater.from(context);
			// this.mInflater =
			// (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			int source = 0;
			if (position < listInfo.size()) {
				source = listInfo.get(position).getSource();
			}
			if (convertView == null) {
				// System.out.println("converView@getView : null");
				holder = new ViewHolder();

				switch (source) {
				case MK:
					srcStr = Q_QUNLUO;
					System.out
							.println("getView@source MK position:" + position);
					convertView = mInflater.inflate(R.layout.vlist, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					holder.deleteBtn = (Button) convertView
							.findViewById(R.id.view_btn2);
					break;
				case IASK:
					srcStr = X_IASK;
					System.out.println("getView@source IASK position:"
							+ position);
					convertView = mInflater.inflate(R.layout.vlist1, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				case WENKU:
					srcStr = B_WENKU;
					System.out.println("getView@source WENKU position:"
							+ position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				case WENKU3G:
					srcStr = B_3G;
					System.out.println("getView@source WENKU position:"
							+ position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				case YOUKU:
					srcStr = Y_YOUKU;
					System.out.println("getView@source YOUKU position:"
							+ position);
					convertView = mInflater.inflate(R.layout.vlist_youku, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);

					holder.img = (ImageView) convertView.findViewById(R.id.img);
					// holder.img.setBackgroundResource(R.drawable.default_img);

					break;
				case XIAOSHUO:
					srcStr = B_XIAOSHUO;
					System.out.println("getView@source WENKU position:"
							+ position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				case GOOGLEBOOK:
					srcStr = G_GOOGLE;
					// System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				case TXTR:
					srcStr = T_TXTR;
					// System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				case CARTOON:
					srcStr = D_CARTOON;
					// System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				case P2P:
					srcStr = "P2P";
					// System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				default:
					break;
				}

				convertView.setTag(holder);

			} else {
				// System.out.println("converView@getView : not null");

				// holder = (ViewHolder)convertView.getTag();

				holder = new ViewHolder();

				switch (source) {
				case MK:
					srcStr = Q_QUNLUO;
					// System.out.println("getView@source MK position:"+position);
					convertView = mInflater.inflate(R.layout.vlist, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					holder.deleteBtn = (Button) convertView
							.findViewById(R.id.view_btn2);
					break;
				case IASK:
					srcStr = X_IASK;
					// System.out.println("getView@source IASK position:"+position);
					convertView = mInflater.inflate(R.layout.vlist1, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				case WENKU:
					srcStr = B_WENKU;
					// System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);

					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				case WENKU3G:
					srcStr = B_3G;
					// System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);

					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				case YOUKU:
					srcStr = Y_YOUKU;
					System.out.println("getView@source YOUKU position:"
							+ position);
					convertView = mInflater.inflate(R.layout.vlist_youku, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					// holder.img.setBackgroundResource(R.drawable.default_img);
					break;
				case XIAOSHUO:
					srcStr = B_XIAOSHUO;
					System.out.println("getView@source WENKU position:"
							+ position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				case GOOGLEBOOK:
					srcStr = G_GOOGLE;
					// System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				case TXTR:
					srcStr = T_TXTR;
					// System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				case CARTOON:
					srcStr = D_CARTOON;
					// System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				case P2P:
					srcStr = "P2P";
					// System.out.println("getView@source WENKU position:"+position);
					convertView = mInflater.inflate(R.layout.vlist2, null);
					holder.title = (TextView) convertView
							.findViewById(R.id.title);
					holder.img = (ImageView) convertView.findViewById(R.id.img);
					holder.info = (TextView) convertView
							.findViewById(R.id.info);
					holder.source = (TextView) convertView
							.findViewById(R.id.source);
					holder.viewBtn = (Button) convertView
							.findViewById(R.id.view_btn);
					break;
				default:
					break;
				}

			}

			// if(position % 2 == 1){
			// convertView.setBackgroundColor(Color.LTGRAY);
			// }else{
			// convertView.setBackgroundColor(Color.WHITE);
			// }

			if (holder != null) {

				String ab = listInfo.get(position).getTitle();
				// System.out.println("abst1 length : "+ab1.length());
				if (ab.length() > TITLELENGTH) {
					ab = ab.substring(0, TITLELENGTH);
				}

				holder.title.setText(ab);

				if (listInfo.get(position).getThumb() != null) {
					// System.out.println("thumb not null");
					holder.img
							.setImageBitmap(listInfo.get(position).getThumb());
				} else {
					// System.out.println("thumb null");
				}

				String ab1 = listInfo.get(position).getAbst1();
				// System.out.println("abst1 length : "+ab1.length());
				if (ab1 != null && ab1.length() > INFOLENGTH) {
					ab1 = ab1.substring(0, INFOLENGTH);
				}
				holder.info.setText(ab1);
				// holder.info.setText(listInfo.get(position).getAbst1());
				holder.source.setText(srcStr);
				if (holder.viewBtn != null) {

					final int po = position;

					holder.viewBtn.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							System.out.println("Button clicked" + po);

							WenkuInfo entity = listInfo.get(po);

							Bundle oBundle = new Bundle();
							oBundle.putString("link", entity.getLink());

							Intent oIntent = new Intent();

							oIntent.setClass(SearchResultActivity.this,
									CWebView.class);
							oIntent.putExtras(oBundle);

							startActivity(oIntent);

						}
					});
				} else {
					// System.out.println("Button1 is null");
				}

				if (holder.deleteBtn != null) {
					holder.deleteBtn
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									System.out.println("Button 2 clicked");
									// ((Button)findViewById(R.id.view_btn2)).setBackgroundResource(R.drawable.delete_down);
									DisplayToast("导出功能--待实现");
								}
							});
				} else {
					// System.out.println("Button2 is null");
				}
			}
			return convertView;

			// return lsView.get(position);
		}
	}

	Handler handler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// System.out.println("get handler message");

			myBar.setVisibility(View.GONE);
			barInfo.setVisibility(View.GONE);

			System.out
					.println("SearchResultActivity---------@handler() : receive msg ,begin display");
			// Queue<CTask> queueTask = new LinkedList<CTask>();
			if (queueTask == null) {
				queueTask = new LinkedList<CTask>();
			}

			List<WenkuInfo> ls;
			if (msg.arg2 == 0) {
				Log.e("request", "request receive!!!!!!!!!!!!");

				request_list = (List<Book>) msg.obj;

				if (msg.obj == null) {
					// 结果为空
					dialogIsRequestBook("您搜索书籍：" + search_context
							+ " 未找到，发送到求书平台吧", search_context);

					return;

				} else {
					if (request_list.size() == 0) {
						dialogIsRequestBook("您搜索书籍：" + search_context
								+ " 未找到，发送到求书平台吧", search_context);
						return;
					}
				}

				ls = new ArrayList<WenkuInfo>();
				WenkuInfo w;
				Book b = null;

				for (int i = 0; i < request_list.size(); i++) {
					b = request_list.get(i);
					Log.e("request", "======================================");
					Log.e("request", "Name:" + b.getBookName());
					Log.e("request", "bookid:" + b.getBookID());
					Log.e("request", "source:" + b.getSource());
					Log.e("request", "link:" + b.getUrl());
					Log.e("request", "type:" + b.getFileType());

					w = new WenkuInfo();
					w.setTitle(b.getBookName());
					w.setLink(b.getUrl());
					w.setAbst1(b.getFileType());
					w.setAbst2(b.getBookID());
					w.setAbst3(b.getSource());

					ls.add(w);
				}

			} else {

				ls = (List<WenkuInfo>) msg.obj;
			}

			if (ls != null)
				listInfo.addAll(ls);

			if (thread_num > 0) {
				thread_num--;

				System.out.println("##########thread return msg, wait for : "
						+ thread_num);
				if (thread_num == 0) {
					myBar.setVisibility(View.GONE);
					barInfo.setVisibility(View.GONE);
					System.out
							.println("##########all thread return, judge the infoList :"
									+ listInfo.size());
					if (listInfo.size() == 0) {
						dialogErr();
						return;
					}
				}

				if (msg.arg1 == -1) {
					// myBar.setVisibility(View.GONE);
					// barInfo.setVisibility(View.GONE);
					System.out
							.println("handler1@SearchResultActivity-------搜索结果存在异常！！！！！！！！！！！！！！！！！！！！！！！！");
					// Toast.makeText(SearchResultActivity.this, "搜索结果异常",
					// Toast.LENGTH_LONG).show();
					return;
				}
			}

			LayoutInflater oInflater = LayoutInflater
					.from(SearchResultActivity.this);

			View vItemRoot = null;
			// TextView lvGameClassifyItemTextView = null;

			WenkuInfo entity = null;
			CTask oTask = null;
			// ViewHolder holder ;

			if (ls != null) {
				System.out
						.println("SearchResultActivity---------@handler() ls.size = "
								+ ls.size()
								+ "  listInfo.size"
								+ listInfo.size());

				for (int i = 0; i < ls.size(); i++, nID++) {
					entity = ls.get(i);
					oTask = new CTask();
					System.out.println("@handler----" + listInfo.size());

					oTask.nNum = nID;
					// 不是从数据库中读取的，设置为cover
					if (flags != true) {
						oTask.szUrl = entity.getCover();
					} else {
						oTask.szUrl = entity.getSourceImg();
					}

					oTask.source = entity.getSource();
					if (entity.getCover() == null) {

					} else {
					}
					System.out.println("szUrl:####################"
							+ entity.getCover());
					queueTask.offer(oTask);
					try {
						vItemRoot = oInflater.inflate(R.layout.vlist, null);
					} catch (Exception e) {

					}

					lsLVItem.add(vItemRoot);

				}// for

				ListView lvSubInfo = (ListView) findViewById(R.id.lvGameInfo);

				adapter = new CAdapter(SearchResultActivity.this, lsLVItem);

				lvSubInfo.setAdapter(adapter);
				// changeFastScrollerDrawable(lvSubInfo);
				disapearThread = new DisapearThread();

				System.out.println("listVIew adapter size: " + listInfo.size());

				System.out
						.println("SearchResultActivity---------@handler() : receive msg ,display done! set progressBar gone");

				// myBar.setVisibility(View.GONE);
				// barInfo.setVisibility(View.GONE);

				updateBitmapList(queueTask);

				System.out
						.println("SearchResultActivity---------@handler() : receive msg ,begin updateBitMap");

				// 不是从数据库中读取的，才将搜索结果写入数据库
				if (msg.arg2 != 1 && msg.arg2 != 0) {

					System.out
							.println("SearchResultActivity---------@handler() : 写入数据库，记录是从新搜索源中获得的");
					for (int i = 0; i < sourceSearch.size(); i++) {
						System.out.println("新搜索源:" + sourceSearch.get(i));
					}

					// cDBpersistent = new
					// CDBPersistent(SearchResultActivity.this,"recent_reading");
					//				
					// cDBpersistent.open();

					cDBpersistent.insert(ls, "search_his", search_context);
					if (sendMsg) {

						try {
							// CNetTransfer.GetXmlInputStream(path+"username="+username+"&searchkey="+search_context);
							System.out.println("GET weibo @@@@@@@@@@@@@@@@@"
									+ CNetTransfer.getUrl(path + "username="
											+ username + "&searchkey="
											+ search_context));
							sendMsg = false;

							// System.out.println("GET weibo @@@@@@@@@@@@@@@@@"+CNetTransfer.Get(""));
							System.out
									.println("###################send msg to blog @SearchResultActivity:--------------------------------"
											+ path
											+ "username="
											+ username
											+ "&searchkey="
											+ search_context_utf8);
							// 首次接收到消息，发送到微博

						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out
									.println("WEIBO@@@@@@@@@@@@@@@@@@@@@@@@@@ exception:"
											+ e.getMessage());
							e.printStackTrace();
						}

					}
					// cDBpersistent.close();
				} else {
					System.out.println("记录是从数据库中获得，不操作");
				}
			}

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
			adapter.notifyDataSetChanged();

			// if (oTask.bmThumb != null && !oTask.bmThumb.isRecycled()){
			// oTask.bmThumb.recycle();
			// System.out.println("@@@@@@@@@@@@@@@@@@@@recycle bitmap @@@@@@@@@@@@@@");
			// }

			super.handleMessage(msg);
		}
	};

	private void updateBitmap(CTask oTask) {

		System.out.println("updateBitmap");

		View vItem = lsLVItem.get(oTask.nNum);

		ImageView img = (ImageView) vItem.findViewById(R.id.img);
		img.setImageBitmap(oTask.bmThumb);

		if (oTask.bmThumb == null) {

			System.out.println("updateBitmap: thumb null");

			if (oTask.source == 5) {
				System.out.println("YouKu do not set default bmp");
			} else {
				Resources res = getResources();
				Bitmap bmp = BitmapFactory.decodeResource(res,
						R.drawable.default_book);
				listInfo.get(oTask.nNum).setThumb(bmp);
				// bmp.recycle();
			}

		} else {
			System.out.println("updateBitmap: thumb not null");
			listInfo.get(oTask.nNum).setThumb(oTask.bmThumb);

		}

		// System.out.println("SearchResultActivity@updateBitmap");

	}

	private class DBThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			showFirstGroupList();
		}

	}

	// search_result_list
	private void showFirstGroupList() {

		System.out
				.println("showFirestGroupList()@SearchResultList---------获得搜索关键字，开始搜索--------------");
		System.out.println("@@@@@@@@@@2SearchResultActivity search_context :"
				+ search_context);
		// Bundle bundle = this.getIntent().getExtras();

		// cDBpersistent = new
		// CDBPersistent(SearchResultActivity.this,"recent_reading");
		//		
		// cDBpersistent.open();
		cDBpersistent.insertKey(search_context);
		System.out.println("get key source: --------------------"
				+ search_context);
		Cursor cursorSource = cDBpersistent.getKeySource(search_context);

		// cDBpersistent.close();

		// System.out.println("getAllInfo------------------"+cursor.getCount());
		System.out.println("Distinct source------------------"
				+ cursorSource.getCount());

		System.out
				.println("showFirestGroupList()@SearchResultList---------将搜索关键字存入数据库，试图从数据库中搜索-----------");

		String source_set = PushReciever.read(sourcePath);
		int ss = 12;
		if (source_set != "") {
			ss = Integer.parseInt(source_set);
			System.out
					.println("showFirestGroupList()@SearchResultList---------从搜索源设置文件钟，获取设置的搜索源信息-------"
							+ ss);
			for (int i = 0; i < source_set.length(); i++) {
				sourceSearch.add(ss % 10);
				ss = ss / 10;
			}
		} else {
			sourceSearch.add(1);
			sourceSearch.add(2);
		}

		int source_db = 0;
		int source_db_next = 0;
		if (cursorSource.getCount() != 0) {
			for (cursorSource.moveToFirst(); !cursorSource.isAfterLast(); cursorSource
					.moveToNext()) {
				source_db_next = cursorSource.getInt(cursorSource
						.getColumnIndex("source"));
				// System.out.println("getKeySource@SearchResultActivity:"+source_db_next);
				if (source_db_next != source_db) {
					System.out.println("Input To sourceDB List a new source"
							+ source_db_next);
					if (sourceSearch.remove(new Integer(source_db_next))) {
						System.out.println("数据库中存在" + source_db_next
								+ ". so remove do not search again");

						DBSearcgThread dbThread = new DBSearcgThread(
								source_db_next);
						thread_num++;
						dbThread.start();//

					} else {
						System.out.println("DB do not have source "
								+ source_db_next + ". excute search");
					}
					source_db = source_db_next;
				}
			}
		}

		System.out
				.println("############## excute search source is ################");
		for (int i = 0; i < sourceSearch.size(); i++) {
			System.out.println("sourceSearch:" + sourceSearch.get(i));
		}
		System.out.println("################ remain source ################");

		if (sourceSearch.size() == 0) {
			flags = true;
			System.out.println("数据库中结果完全，不重新写入");
		} else {
			System.out
					.println("showFirestGroupList()@SearchResultList---------数据库中结果不全,启新线程搜索-----------");

			String key_utfGBK = "";
			try {
				search_context_utf8 = URLEncoder
						.encode(search_context, "UTF-8");
				System.out.println("search_context_3g" + search_context_utf8);
				search_context_utf8 = search_context_utf8.replace("+", "%20");
				System.out.println("search_context_3g" + search_context_utf8);

				search_context_gbk = URLEncoder
						.encode(search_context, "GB2312");
				System.out.println("search_context_3g" + search_context_gbk);
				search_context_gbk = search_context_gbk.replace("+", "%20");
				System.out.println("search_context_3g" + search_context_gbk);

				key_utfGBK = URLEncoder.encode(search_context, "GBK");

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String mkUrl = getString(R.string.url_mk) + "?key="
					+ search_context_gbk;
			String szUrl = getString(R.string.url_baiduwenku) + "?key="
					+ search_context_gbk;
			String szUrl_3G = getString(R.string.url_baiduwenku3g) + "?key="
					+ search_context_utf8;
			String iaskUrl = getString(R.string.url_iask) + "?key="
					+ search_context_gbk;
			String youkuURL = getString(R.string.url_youku) + "?key="
					+ search_context_utf8;
			;
			String xs = getString(R.string.url_baiduxs) + "?key="
					+ search_context_utf8;
			String googleUrl = getString(R.string.url_googlebook) + "&title="
					+ search_context_utf8;
			String txtrUrl = getString(R.string.url_txtr) + "?key="
					+ search_context_utf8;
			String cartoonUrl = getString(R.string.url_cartoon) + "?key="
					+ search_context_utf8;
			String p2pUrl = getString(R.string.url_request) + key_utfGBK;

			// System.out.println("xs--------------"+xs);

			IParse parse = new WenkuInfoParse(WENKU);
			IParse parse1 = new WenkuInfoParse(IASK);
			IParse parse2 = new WenkuInfoParse(MK);
			IParse parse3 = new WenkuInfoParse(WENKU3G);
			IParse parse4 = new WenkuInfoParse(YOUKU);
			IParse parse5 = new WenkuInfoParse(XIAOSHUO);
			IParse parse6 = new WenkuInfoParse(GOOGLEBOOK);
			IParse parse7 = new WenkuInfoParse(TXTR);
			IParse parse8 = new WenkuInfoParse(CARTOON);
			IParse parse9 = new BookParse(P2P);
			// System.out.println("handler");

			ExecutorService pool = Executors.newFixedThreadPool(3);

			CThread thread = new CThread(szUrl, parse, handler1, WENKU);
			CThread thread1 = new CThread(iaskUrl, parse1, handler1, IASK);
			CThread thread2 = new CThread(mkUrl, parse2, handler1, MK);
			CThread thread3 = new CThread(szUrl_3G, parse3, handler1, WENKU3G);
			CThread thread4 = new CThread(youkuURL, parse4, handler1, YOUKU);
			CThread thread5 = new CThread(xs, parse5, handler1, XIAOSHUO);
			CThread thread6 = new CThread(googleUrl, parse6, handler1,
					GOOGLEBOOK);
			CThread thread7 = new CThread(txtrUrl, parse7, handler1, TXTR);
			CThread thread8 = new CThread(cartoonUrl, parse8, handler1, CARTOON);
			CThread thread9 = new CThread(p2pUrl, parse9, handler1, P2P);

			Log.e("request", "p2pUrl:" + p2pUrl);

			sendMsg = true;
			for (int i = 0; i < sourceSearch.size(); i++) {
				System.out.println("sourceSearch:" + sourceSearch.get(i));
				switch (sourceSearch.get(i)) {
				case 1:
					pool.execute(thread);
					thread_num++;
					System.out
							.println("showFirestGroupList()@SearchResultList---------文库搜索源-----!!--");
					break;
				case 2:
					pool.execute(thread1);
					thread_num++;
					System.out
							.println("showFirestGroupList()@SearchResultList---------新浪IASK搜索源-------"
									+ iaskUrl);
					break;
				case 3:
					pool.execute(thread2);
					thread_num++;
					System.out
							.println("showFirestGroupList()@SearchResultList----------群落阅读搜索源------");
					break;
				case 4:
					pool.execute(thread3);
					thread_num++;
					System.out
							.println("showFirestGroupList()@SearchResultList-----------百度3G搜索源-----");
					break;
				case 5:
					pool.execute(thread4);
					thread_num++;
					System.out
							.println("showFirestGroupList()@SearchResultList-----------优酷搜索源-----");
					break;
				case 6:
					pool.execute(thread5);
					thread_num++;
					System.out
							.println("showFirestGroupList()@SearchResultList------------百度小说搜索源----");
					break;
				case 7:
					System.out
							.println("showFirestGroupList()@SearchResultList------------谷歌图书----");
					pool.execute(thread6);
					thread_num++;
					break;
				case 8:
					System.out
							.println("showFirestGroupList()@SearchResultList------------TXTR----"
									+ txtrUrl);
					pool.execute(thread7);
					thread_num++;
					break;
				case 9:
					System.out
							.println("showFirestGroupList()@SearchResultList------------Cartoon----"
									+ cartoonUrl);
					pool.execute(thread8);
					thread_num++;
					break;
				case 0:
					System.out
							.println("showFirestGroupList()@SearchResultList------------Cartoon----"
									+ cartoonUrl);
					pool.execute(thread9);
					thread_num++;
					break;
				default:
					System.out
							.println("showFirestGroupList()@SearchResultList------------Default 12 ##----");
					pool.execute(thread);
					thread_num++;
					pool.execute(thread1);
					thread_num++;
					break;
				}
			}
			System.out.println("#########start thread num : " + thread_num);
			// String source = PushReciever.read(sourcePath);
			// int s = 12;
			// if(source != ""){
			// s = Integer.parseInt(source);
			// System.out.println("showFirestGroupList()@SearchResultList---------从搜索源设置文件钟，获取设置的搜索源信息-------"+
			// s);
			// for(int i = 0;i<source.length();i++){
			// switch (s%10) {
			// case 1:
			// s = s/10;
			// pool.execute(thread);
			// System.out.println("showFirestGroupList()@SearchResultList---------文库搜索源-----!!--"+szUrl);
			// break;
			// case 2:
			// s = s/10;
			// pool.execute(thread1);
			// System.out.println("showFirestGroupList()@SearchResultList---------新浪IASK搜索源-------");
			// break;
			// case 3:
			// s = s/10;
			// pool.execute(thread2);
			// System.out.println("showFirestGroupList()@SearchResultList----------群落阅读搜索源------");
			// break;
			// case 4:
			// s = s/10;
			// pool.execute(thread3);
			// System.out.println("showFirestGroupList()@SearchResultList-----------百度3G搜索源-----");
			// break;
			// case 5:
			// s = s/10;
			// pool.execute(thread4);
			// System.out.println("showFirestGroupList()@SearchResultList-----------优酷搜索源-----");
			// break;
			// case 6:
			// s = s/10;
			// pool.execute(thread5);
			// System.out.println("showFirestGroupList()@SearchResultList------------百度小说搜索源----");
			// break;
			// case 7:
			// s = s/10;
			// System.out.println("showFirestGroupList()@SearchResultList------------谷歌图书----"+googleUrl);
			// pool.execute(thread6);
			//						
			// break;
			//
			// default:
			// pool.execute(thread);
			// pool.execute(thread1);
			// break;
			// }
			// }
			// }else{
			// pool.execute(thread);
			// pool.execute(thread1);
			// }

			// int s = Integer.parseInt(source);

			// pool.execute(thread);
			// pool.execute(thread1);
			// pool.execute(thread2);
			// pool.execute(thread3);
			// pool.execute(thread4);
			// pool.execute(thread5);
			// 锟截憋拷锟竭程筹拷

			pool.shutdown();

		}// end else
		// thread.start();
	}

	private void toast(String mss) {
		Toast.makeText(SearchResultActivity.this, mss, Toast.LENGTH_SHORT)
				.show();
	}

	class sendInfotoGetBookThread extends Thread {
		Handler sendBookInfo;

		public sendInfotoGetBookThread(Handler handler) {
			// TODO Auto-generated constructor stub
			sendBookInfo = handler;
		}

		Message msg = new Message();

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {

				String result = CNetTransfer.GetXml(request_book_urlGBK);
				Log.v("lluu", result);
				Log.v("request", "result:" + result);
				int r = Integer.parseInt(result.substring(4, 5));
				Log.v("request", "result:" + r);
				msg.arg1 = r;
				sendBookInfo.sendMessage(msg);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.v("yichang", msg.arg1 + "");
				e.printStackTrace();
				Log.v("request", "GetXML Exception", e);
				msg.arg1 = -1;
				sendBookInfo.sendMessage(msg);
			}
		}
	}

	private String request_book_urlGBK = "";

	private void dialog(String mess, int i) {
		Builder builder = new Builder(SearchResultActivity.this);
		builder.setMessage(mess);

		builder.setTitle("求书信息");// "求书信息："

		builder.setPositiveButton(getString(R.string.Button_ok).toString(),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Log.v("request", "request_book_urlGBK1 Dialog:"
								+ request_book_urlGBK);

						Handler sendBook = new Handler() {
							@Override
							public void handleMessage(Message msg) {
								// TODO Auto-generated method stub
								if (msg.arg1 == 1) {
									// dialog("求书信息已成功发送");
									toast("求书信息已成功发送");

								} else if (msg.arg1 == -1) {
									// dialog("求书信息发送失败，请重新发送");
									toast("求书信息发送失败，请重新发送");
								}
							}
						};

						new sendInfotoGetBookThread(sendBook).start();

						// try {
						// String result = CNetTransfer
						// .GetXml(request_book_urlGBK);
						// Log.v("request", "result:" + result);
						// int r = Integer.parseInt(result.substring(0, 1));
						// Log.v("request", "result:" + r);
						// if (r == 1) {
						// // dialog("求书信息已成功发送");
						// toast("求书信息已成功发送");
						//
						// } else {
						// // dialog("求书信息发送失败，请重新发送");
						// toast("求书信息发送失败，请重新发送");
						// }
						//
						// } catch (Exception e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// Log.v("request", "GetXML Exception", e);
						// }

						// 发送到求书平台 add by luye 2011.11.18
						//
						//
						username = CHistoryActivity.username;
						String booknameHex = "";
						String tag1Hex = "";
						String tag2Hex = "";
						String tagIsNobook = "0";

						try {
							booknameHex = StrToHex.encode(search_context);
							tag1Hex = StrToHex.encode("其他");
							tag2Hex = StrToHex.encode("短信求书");
						} catch (UnsupportedEncodingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						request_book_urlGBK_Web = "http://61.181.14.184:8088/Readings/saveRequestbook.do?"
								+ "name="
								+ username
								+ "&bookname="
								+ booknameHex
								+ "&score=10"
								+ "&tag1="
								+ tag1Hex
								+ "&tag2="
								+ tag2Hex
								+ "&tagIsNobook=" + tagIsNobook;

						String webR = "";
						try {
							webR = CNetTransfer.GetXml(request_book_urlGBK_Web);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Log.e("RequestWeb:", "search_WebURL"
								+ request_book_urlGBK_Web);
						Log.e("RequestWeb:", "WebURL result:" + webR);
						// /////////////////////////////////////////////////////////////

						dialog.dismiss();
					}
				});
		builder.setNegativeButton(getString(R.string.Button_cancle).toString(),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	private AlertDialog.Builder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.searchresult_lv);

		B_WENKU = SearchResultActivity.this.getString(
				R.string.SearchSource_baiduwenku).toString();
		X_IASK = SearchResultActivity.this
				.getString(R.string.SearchSource_iask).toString();
		Q_QUNLUO = SearchResultActivity.this
				.getString(R.string.SearchSource_mk).toString();
		B_3G = SearchResultActivity.this.getString(
				R.string.SearchSource_baiduwenku3g).toString();
		Y_YOUKU = SearchResultActivity.this.getString(
				R.string.SearchSource_youku).toString();
		B_XIAOSHUO = SearchResultActivity.this.getString(
				R.string.SearchSource_baiduxs).toString();
		G_GOOGLE = SearchResultActivity.this.getString(
				R.string.SearchSource_googlebook).toString();
		T_TXTR = SearchResultActivity.this
				.getString(R.string.SearchSource_txtr).toString();
		D_CARTOON = SearchResultActivity.this.getString(
				R.string.SearchSource_cartoon).toString();

		cDBpersistent = new CDBPersistent(SearchResultActivity.this,
				"search_his");
		cDBpersistent.open();

		Intent i = this.getIntent();
		Bundle b = i.getExtras();
		search_context = b.getString("search_context");
		System.out.println("@@@@@@@@@@1SearchResultActivity search_context :"
				+ search_context);
		username = b.getString("username");
		System.out.println("##################username:" + username);
		handler = new Handler();

		// 初始化首字母悬浮提示框
		txtOverlay = (TextView) LayoutInflater.from(this).inflate(
				R.layout.popup_char_hint, null);
		txtOverlay.setVisibility(View.INVISIBLE);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 200,
				-300, WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);

		windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		windowManager.addView(txtOverlay, lp);

		barInfo = (TextView) findViewById(R.id.barInfo2);
		myBar = (ProgressBar) findViewById(R.id.myBarInfo);
		myBar.setVisibility(View.VISIBLE);
		barInfo.setVisibility(View.VISIBLE);
		myBar.setMax(100);

		System.out
				.println("SearchResultActivity------------@onCreate:  setProgressBar");

		lv = (ListView) findViewById(R.id.lvGameInfo);

		lv.setOnScrollListener(this);

		lv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub

				return false;
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				System.out
						.println("onItemClickListener@SearchResultActivity-------------------------------");

				WenkuInfo entity = listInfo.get(arg2);

				// add by liym 20110627
				Intent intent;
				switch (entity.getSource()) {
				case WENKU3G:
					System.out
							.println("	onItemClickListener@SearchResultActivity-------------source: WENKU");
					/*
					 * String filepath = Uri.parse(entity.getLink()) + ".olb";
					 * String param = "@jinke" + entity.getTitle() + "@jinke" +
					 * filepath; String type = "application/olb";
					 * 
					 * System.out.println("=================================");
					 * System.out.println("param :"+param);
					 * System.out.println("filepath :"+filepath);
					 * System.out.println("=================================");
					 * 
					 * File f = new File(filepath); intent = new Intent();
					 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 * intent.setAction(android.content.Intent.ACTION_VIEW);
					 * intent.setDataAndType(Uri.fromFile(f),type);
					 * intent.putExtra("JKVIEWERONLINE",param);
					 * startActivity(intent);
					 */

					String cmdKill = "/data/box/pkill eu.licentia.necessitas.industrius.example.jkviewer";
					String param = "@jinke" + entity.getTitle() + "@jinke"
							+ Uri.parse(entity.getLink()) + ".olb";
					String cmd = "am start -n eu.licentia.necessitas.industrius.example.jkviewer/eu.licentia.necessitas.industrius.QtActivity -e JKVIEWER "
							+ param;
					// System.out.println("=================================");
					// System.out.println("cmd :"+cmd);
					// System.out.println("=================================");
					try {

						Process process = Runtime.getRuntime().exec(cmdKill);

						process.waitFor();
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}

					try {

						System.out
								.println("========screen shot===================================================");
						Runtime.getRuntime().exec(cmd);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}

					ScreenShot
							.screentShot("/data/images-android/common/bookshelf.png");

					break;
				case GOOGLEBOOK:
					System.out
							.println("	onItemClickListener@SearchResultActivity-------------source: GOOGLEBOOK");
					intent = new Intent();
					intent.setClass(SearchResultActivity.this,
							GoogleBookWebView.class);
					intent.putExtra("book_id", entity.getAbst3());
					startActivity(intent);
					break;
				case P2P:
					Log.e("request", "request click!!!!!!!!!!!!!");

					/*
					 * w.setTitle(b.getBookName()); w.setLink(b.getUrl());
					 * w.setAbst1(b.getFileType()); w.setAbst2(b.getBookID());
					 * w.setAbst3(b.getSource());
					 */
					Log
							.e("request", "usernameGBK:"
									+ CHistoryActivity.username);
					Log.e("request", "bookIDGBK:" + entity.getAbst2());
					Log.e("request", "sourceGBK:" + entity.getAbst3());
					Log.e("request", "linkGBK:" + entity.getLink());
					Log.e("request", "bookNameGBK:" + entity.getTitle());
					Log.e("request", "fileTypeGBK:" + entity.getAbst1());
					try {
						String usernameGBK = URLEncoder.encode(
								CHistoryActivity.username, "GBK");
						String bookIDGBK = URLEncoder.encode(entity.getAbst2(),
								"GBK");
						String sourceGBK = URLEncoder.encode(entity.getAbst3(),
								"GBK");
						String linkGBK = URLEncoder.encode(entity.getLink(),
								"GBK");
						String bookNameGBK = URLEncoder.encode(entity
								.getTitle(), "GBK");
						String fileTypeGBK = URLEncoder.encode(entity
								.getAbst1(), "GBK");

						if ("makefile".equals(entity.getAbst3())) {
							// Toast.makeText(RequestBookActivity.this,
							// b.getBookName()+b.getSource(),
							// Toast.LENGTH_SHORT).show();
							// http://61.181.14.184:8088/Readings/notifyDownload.do?
							request_book_urlGBK = getString(R.string.request_book_url)
									+ "userName="
									+ usernameGBK
									+ "&bookID="
									+ bookIDGBK
									+ "&source="
									+ sourceGBK
									+ "&link="
									+ linkGBK
									+ "&bookName="
									+ bookNameGBK + "&fileType=" + fileTypeGBK;
							Log.v("request", "request_book_urlGBK1:share:"
									+ request_book_urlGBK);

						} else if ("共享".equals(entity.getAbst3())) {
							// Toast.makeText(RequestBookActivity.this,
							// b.getBookName()+b.getSource(),
							// Toast.LENGTH_SHORT).show();
							request_book_urlGBK = "http://61.181.14.184:8088/Readings/p2PDownload.do?"
									+ "userName="
									+ usernameGBK
									+ "&bookName="
									+ bookNameGBK
									+ "&url="
									+ linkGBK
									+ "&fileType=" + fileTypeGBK;
							Log.v("request", "request_book_urlGBK1:makefile:"
									+ request_book_urlGBK);
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					dialog("发送求书请求:\n           " + entity.getTitle(), 1);

					break;
				default:
					System.out
							.println("	onItemClickListener@SearchResultActivity-------------source: WebView");
					System.out.println("source others use IE open");
					Uri uri = Uri.parse(entity.getLink());
					intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}

			}

			public String getMIMEType(File f) {
				String type = "";
				String fName = f.getName();
				/* 取得扩展名 */
				String end = fName.substring(fName.lastIndexOf(".") + 1,
						fName.length()).toLowerCase();

				/* 依扩展名的类型决定MimeType */
				if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
						|| end.equals("xmf") || end.equals("ogg")
						|| end.equals("wav")) {
					type = "audio";
				} else if (end.equals("3gp") || end.equals("mp4")) {
					type = "video";
				} else if (end.equals("jpg") || end.equals("gif")
						|| end.equals("png") || end.equals("jpeg")
						|| end.equals("bmp")) {
					type = "image";
				} else if (end.equals("apk")) {
					/* android.permission.INSTALL_PACKAGES */
					type = "application/vnd.android.package-archive";
				} else if (end.equals("wol") || end.equals("doc")
						|| end.equals("txt") || end.equals("rtf")
						|| end.equals("pdb") || end.equals("chm")
						|| end.equals("lit") || end.equals("olb")) {

				} else {
					type = "*";
				}
				/* 如果无法直接打开，就跳出软件列表给用户选择 */
				if (end.equals("apk") || end.equals("wol") || end.equals("doc")
						|| end.equals("txt") || end.equals("rtf")
						|| end.equals("pdb") || end.equals("chm")
						|| end.equals("lit")) {
				} else {
					type += "/*";
				}
				return type;
			}
		});

		// showFirstGroupList();
		// System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!showFirstGroupList");
		DBThread showThread = new DBThread();
		showThread.start();

	}

	private class DisapearThread implements Runnable {
		public void run() {
			System.out.println("DisapearThread --------------------run");
			// 避免在1.5s内，用户再次拖动时提示框又执行隐藏命令。
			if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
				txtOverlay.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		// 以中间的ListItem为标准项。
		// txtOverlay.setText(String.valueOf(stringArr[firstVisibleItem +
		// (visibleItemCount >> 1)]
		// .charAt(0)));
		// System.out.println("!!!!!!!!!! listInfo size : !!!!!!!!!!!!!!!!"+listInfo.size());
		if (listInfo.size() != 0) {
			WenkuInfo wenk = listInfo.get(firstVisibleItem
					+ (visibleItemCount >> 1));
			String surStr = "";
			switch (wenk.getSource()) {
			case MK:
				surStr = Q_QUNLUO;
				break;
			case IASK:
				surStr = X_IASK;
				break;
			case WENKU:
				surStr = B_WENKU;
				break;
			case WENKU3G:
				surStr = B_3G;
				break;
			case YOUKU:
				surStr = Y_YOUKU;
				break;
			case XIAOSHUO:
				surStr = B_XIAOSHUO;
				break;
			case GOOGLEBOOK:
				surStr = G_GOOGLE;
				break;
			case TXTR:
				surStr = T_TXTR;
				break;
			case CARTOON:
				surStr = D_CARTOON;
				break;
			default:
				break;
			}
			txtOverlay.setText("" + surStr);
		} else {

		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		this.scrollState = scrollState;
		if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
			handler.removeCallbacks(disapearThread);
			// 提示延迟1.5s再消失
			boolean bool = handler.postDelayed(disapearThread, 1500);
			System.out.println("postDelayed=" + bool);
			// task = new TimerTask() {
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// SearchResultActivity.this.runOnUiThread(new Runnable() {
			//					
			// @Override
			// public void run() {
			// txtOverlay.setVisibility(View.INVISIBLE);
			// }
			// });
			//		    	
			// }
			// };
			// timer.schedule(task,4000);
			// Log.d("ANDROID_INFO", "postDelayed=" + bool);
		} else {
			txtOverlay.setVisibility(View.VISIBLE);
		}
	}

	/** 更改指定ListView的快速滚动条图标。 */
	private void changeFastScrollerDrawable(ListView list) {
		try {
			Field f = AbsListView.class.getDeclaredField("mFastScroller");
			System.out.println("changeFastScrollerDrawable");
			f.setAccessible(true);
			Object obj = f.get(list);
			f = f.getType().getDeclaredField("mThumbDrawable");
			f.setAccessible(true);
			System.out.println("f null : " + (f == null));
			Drawable drawable = (Drawable) f.get(obj);
			drawable = getResources().getDrawable(R.drawable.fast_scroller_img);
			f.set(obj, drawable);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private class DBSearcgThread extends Thread {
		private int source;
		private List<WenkuInfo> dbInfoList = new ArrayList<WenkuInfo>();

		public DBSearcgThread(int source) {
			this.source = source;
		}

		@Override
		public void run() {

			System.out
					.println("showFirestGroupList()@SearchResultList---------从数据库中搜索到结果-----------");

			// cDBpersistent = new
			// CDBPersistent(SearchResultActivity.this,"recent_reading");
			// cDBpersistent.open();

			Cursor cursor = cDBpersistent.getAllInfo(search_context, source
					+ "");

			// cDBpersistent.close();

			WenkuInfo info;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				info = new WenkuInfo();
				info.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				info.setAbst1(cursor.getString(cursor.getColumnIndex("abst1")));

				info.setLink(cursor.getString(cursor.getColumnIndex("link")));
				info.setSourceImg(cursor.getString(cursor
						.getColumnIndex("sourceImg")));
				System.out
						.println("insertToDB@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
								+ cursor.getString(cursor
										.getColumnIndex("sourceImg")));
				info.setSource(cursor.getInt(cursor.getColumnIndex("source")));

				arrbt = cursor.getBlob(cursor.getColumnIndex("thumb"));
				info.setAbst3(cursor.getString(cursor.getColumnIndex("abst3")));
				Bitmap bm = BitmapFactory.decodeByteArray(arrbt, 0,
						arrbt.length);

				info.setThumb(bm);
				dbInfoList.add(info);

			}

			cursor.close();
			Message msg = new Message();
			msg.obj = dbInfoList;
			System.out.println("丛数据库钟获得信息，发送handler消息给主线程，listInfo.size = "
					+ listInfo.size());
			msg.arg2 = 1;
			handler1.sendMessage(msg);
		}

	}

	private void dialogErr() {

		Builder builder = new Builder(SearchResultActivity.this);
		builder.setMessage(getString(R.string.NULL_ErrorDialog_context)
				.toString());

		builder.setTitle(getString(R.string.NULL_ErrorDialog_title).toString());

		// builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		// {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		// }
		// });

		builder.setPositiveButton(getString(R.string.Button_ok).toString(),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						SearchResultActivity.this.finish();
					}
				});

		builder.create().show();
	}

	/**
	 * 向求书平台发送消息
	 * */
	private void dialogIsRequestBook(String mess, String contentOfBookName) {
		Builder builder = new Builder(SearchResultActivity.this);
		builder.setMessage(mess);
		String booknameHex = "";
		String tag1Hex = "";
		String tag2Hex = "";
		String tagIsNobook = "1";
		try {
			booknameHex = StrToHex.encode(contentOfBookName);
			tag1Hex = StrToHex.encode("其他");
			tag2Hex = StrToHex.encode("短信求书");

		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		username = CHistoryActivity.username;

		request_book_urlGBK_Web = "http://61.181.14.184:8088/Readings/saveRequestbook.do?"
				+ "name="
				+ username
				+ "&bookname="
				+ booknameHex
				+ "&score=10"
				+ "&tag1="
				+ tag1Hex
				+ "&tag2="
				+ tag2Hex
				+ "&tagIsNobook="
				+ tagIsNobook;

		builder.setTitle("求书提示");// "求书信息："
		builder.setPositiveButton(getString(R.string.Button_ok).toString(),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Handler sendBooktoPlatformHandlder = new Handler() {
							public void handleMessage(Message msg) {

								if (msg.arg1 == 1) {
									// dialog("求书信息已成功发送");
									toast("求书信息已成功发送");

								} else if (msg.arg1 == -1) {
									// dialog("求书信息发送失败，请重新发送");
									toast("求书信息发送失败，请重新发送");
								}
							};
						};
						new sendBooktoPlatformThread(sendBooktoPlatformHandlder)
								.start();
						//				

						// try {
						// String result =
						// CNetTransfer.GetXml(request_book_urlGBK_Web);
						// Log.v("request", "result:"+result);
						// int r = Integer.parseInt(result.substring(0, 1));
						// Log.v("request", "result:"+r);
						// if(r == 1){
						// // dialog("求书信息已成功发送");
						// toast("求书信息已成功发送");
						//					 
						// }else{
						// // dialog("求书信息发送失败，请重新发送");
						// toast("求书信息发送失败，请重新发送");
						// }
						//					
						// } catch (Exception e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// Log.v("request", "GetXML Exception", e);
						// }

						dialog.dismiss();
						// SearchResultActivity.this.finish();
					}
				});
		builder.setNegativeButton(getString(R.string.Button_cancle).toString(),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
					}
				});

		builder.create().show();

	}

	class sendBooktoPlatformThread extends Thread {
		Handler getHandler;

		public sendBooktoPlatformThread(Handler a) {
			getHandler = a;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = new Message();
			try {

				String result = CNetTransfer.GetXml(request_book_urlGBK_Web);
				Log.v("lluuping", result);
				Log.v("request", "result:" + result);

				int r = 0;
				if (result.contains("success")) {

					// int r = Integer.parseInt(result.substring(0, 1));
					r = 1;
				}
				Log.v("request", "result:" + r);
				msg.arg1 = r;
				getHandler.sendMessage(msg);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.v("request", "GetXML Exception", e);
				msg.arg1 = -1;
				getHandler.sendMessage(msg);
			}

		}
	}

}
