package com.jinke.readman;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;      

import com.jinke.flipboard.download.DownLoadThread;
import com.jinke.flipboard.download.IdentifyUtil;
import com.jinke.flipboard.widget.SearchEditText;
import com.jinke.flipboard.widget.mFlipPageWidget;
import com.jinke.flipboard.widget.mWebView;
import com.jinke.readman.bean.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class flipper extends Activity{
	private String username;
	private String RSS_KEY = "http://61.181.14.184:8084/NewReadman/getRss.do?username="+username+"&page=1";
	private String WEIBO_KEY = "http://61.181.14.184:8084/NewReadman/getSinaweibo.do?username="+username+"&size=1&type=0";
	private static final String SHARE_KEY = "";
	private static final String WIKI_KEY = "";
	private static final String BLOG_KEY = "";
	
	private static final String RSS_PATH = "http://61.181.14.184:8084/NewReadman/getRss.do?";
	private static final String WEIBO_PATH = "http://61.181.14.184:8084/NewReadman/getSinaweibo.do?";
	private static final String SHARE_PATH = "";
	private static final String WIKI_PATH = "";
	private static final String BLOG_PATH = "";
	
	private Intent mIntent;
	private String key;
	private String load_url;
//	private String username = "marklee";
	boolean finish = false;
	
	private String type = "";
	
	Bitmap mCurPageBitmap, mNextPageBitmap;
	Canvas mCurPageCanvas, mNextPageCanvas;
	private boolean ondrawflag = false;
	
	private static final String TAG = "flipper";
	private mFlipPageWidget flipWgt;
	private ProgressBar mProgressBar;
	private View view;
	private mWebView webView;
	private List<mWebView> webviews = new ArrayList<mWebView>();
	private List<View> mViews = new ArrayList<View>();
	private void initKeyUrl(){
		RSS_KEY = "http://61.181.14.184:8084/NewReadman/getRss.do?username="+username+"&page=1";
		WEIBO_KEY = "http://61.181.14.184:8084/NewReadman/getSinaweibo.do?username="+username+"&size=1&type=0";

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		/**全屏设置，隐藏窗口所有装饰*/  
		//    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
		//    	WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		/**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效*/ 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.flipper);
		username = User.getInstance().getUserName();
		Log.v(TAG, "username:"+username);
		initKeyUrl();
		mIntent = getIntent();
		key = mIntent.getExtras().getString("key");
		
		Log.e("key", key);
		
		flipWgt = (mFlipPageWidget)findViewById(R.id.mFlipWgt);
		flipWgt.setUsername(username);
		if(RSS_KEY.equals(key)){
			type = "rss";
			load_url = RSS_PATH+"username="+username+"&page=1";
		}else if(WEIBO_KEY.equals(key)){
			type = "weibo";
			Log.e("weibo key", key);
			load_url = WEIBO_PATH+"username="+username+"&size=1&type=0";
		}else{
			Log.e("weibo key", "!@@@@@@@@@@@@@@@");
		}
		
		mProgressBar = (ProgressBar)findViewById(R.id.mProgress_bar);
		
		initFlipWidget(1);
		
		finish = true;
	}
	private void initFlipWidget(int size){
		LayoutInflater inflater = LayoutInflater.from(flipper.this);
		flipWgt.removeAllViews();
		for (int i = 0; i < 3; i++) {
			view = inflater.inflate(R.layout.flipper_sub_webview, null, false);
			webView = (mWebView)view.findViewById(R.id.mWebView);
			
			final ProgressBar mprogressBar = (ProgressBar)view.findViewById(R.id.mProgress_bar);
			final EditText search_edit = (EditText)view.findViewById(R.id.search_Edit);
			final Button search_button = (Button)view.findViewById(R.id.search_Button);
			search_button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					search_edit.setVisibility(View.GONE);
					search_button.setVisibility(View.GONE);
					Toast.makeText(flipper.this, search_edit.getText().toString(), Toast.LENGTH_LONG).show();
					String key = "";
					try {
						key = URLEncoder.encode(search_edit.getText().toString(), "utf-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String url = "http://61.181.14.184:8084/NewReadman/search.jsp?key="+key;
					Log.e("touch", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+url);
					Intent mIntent = new Intent();
					mIntent.putExtra("url", url);
					mIntent.setClass(flipper.this, content_fenye.class);
					flipper.this.startActivity(mIntent);
				}
			});
			//imgView.setBackgroundResource(R.drawable.bgpic);
			webView.getSettings().setSupportZoom(false);
			webView.getSettings().setJavaScriptEnabled(true);  
			webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
			//webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			//webView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
			webView.setScrollbarFadingEnabled(false);
			
			webView.setSearchCallback(new searchCallback() {
				
				@Override
				public void callback(boolean flag) {
					// TODO Auto-generated method stub
					Toast.makeText(flipper.this, "flag:"+flag, Toast.LENGTH_LONG).show();
					if(flag){
						search_edit.setVisibility(View.VISIBLE);
						search_button.setVisibility(View.VISIBLE);
						
					}else{
						search_edit.setVisibility(View.GONE);
						search_button.setVisibility(View.GONE);
					}
					
				}
			});
			webView.setTimeCallback(new TimeCallback() {
				
				@Override
				public void callback(int size) {
					// TODO Auto-generated method stub
					Toast.makeText(flipper.this, "size:"+size, Toast.LENGTH_LONG).show();
					key = WEIBO_KEY;
					load_url = WEIBO_PATH+"username="+username+"&size="+size+"&type=0";
					flipWgt.initByTime(size);
				}
			});
			webView.setOverCallback(new OverCallback() {
				
				@Override
				public void callback(boolean over) {
					// TODO Auto-generated method stub
					flipWgt.setOver(over);
				}
			});
			
			webView.setWebViewClient(new WebViewClient(){
				
				//重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {  
//					view.loadUrl(url);
					Log.v("urlLoading", url);
					if("localview".equals(url.substring(0, 9))){
						
						url = "http"+url.substring(9, url.length());
						view.loadUrl(url);
						
						
						return true;
					}
					
					if("cacheclean".equals(url.substring(0, 10))){
						
						url = "http"+url.substring(10, url.length());
						view.loadUrl(url);
//						webView.reload = true;
						flipWgt.reload();
						
						return true;
					}
					
					Intent cIntent = new Intent();
					cIntent.putExtra("url", url);
					cIntent.setClass(flipper.this, content_fenye.class);
					flipper.this.startActivity(cIntent);
					return true;
				}
				
				@Override
				public void onPageFinished(WebView webview,
						String url) {
					// TODO Auto-generated method stub
					super.onPageFinished(webview, url);
					Log.v("onPage", "=============onPageFinished URL:"+url);
					mprogressBar.setVisibility(View.GONE);
					
					
				}
				
				@Override
				public void onPageStarted(WebView view,
						String url, Bitmap favicon) {
					// TODO Auto-generated method stub
					super.onPageStarted(view, url, favicon);
					Log.v("onPage", "=============onPageStarted URL:"+url);
					
					mprogressBar.setVisibility(View.VISIBLE);
				}
				
			});
			Log.e("AndroidRuntime", "filpper null:"+(flipper.this == null));
			webView.setFlipper(flipper.this);
			//==========
			if(finish){
				
				
				
			webView.ondrawflag = true;
			View view = flipper.this.getWindow().getDecorView();
	        view.setDrawingCacheEnabled(true);
	        view.buildDrawingCache();
	        Bitmap b1 = view.getDrawingCache();
			
	        // 获取状态栏高度
	        Rect frame = new Rect();
	        flipper.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
	        int statusBarHeight = frame.top;

	        // 获取屏幕长和高
	        int width = flipper.this.getWindowManager().getDefaultDisplay().getWidth();
	        //Toast.makeText(flipper.this, "width:"+width, Toast.LENGTH_SHORT).show();
	        int height = flipper.this.getWindowManager().getDefaultDisplay()
	                .getHeight();
	        // 去掉标题栏
	        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
	        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
	                - statusBarHeight);
	        view.destroyDrawingCache();
	        
			
	        webView.abortAnimation();

			}
			
			//========
//			webView.loadUrl(load_url);
			if(i == 1)
				if("weibo".equals(type))
					webView.loadUrl("weibo", 1, load_url);
				else
					webView.loadUrl("rss", 1, load_url);
			
			webView.setIndex(i);
			
				webView.requestFocus();
			
			
			Log.v(TAG, "newmain_url:"+load_url);
			webviews.add(webView);
			
			
			flipWgt.addView(view);
			mViews.add(view);
			
		}//for
		flipWgt.setViewId();
		flipWgt.setViews(mViews);
		flipWgt.setWebViews(webviews);
		
		if(RSS_KEY.equals(key)){
			flipWgt.initWebView(1,0);
		}else if(WEIBO_KEY.equals(key)){
			flipWgt.initWebView(2,size + 4);
		}
		
	}
	
	
	// 点击Menu时，系统调用当前Activity的onCreateOptionsMenu方法，并传一个实现了一个Menu接口的menu对象供你使用
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * add()方法的四个参数，依次是： 1、组别，如果不分组的话就写Menu.NONE,
		 * 2、Id，这个很重要，Android根据这个Id来确定不同的菜单 3、顺序，那个菜单现在在前面由这个参数的大小决定
		 * 4、文本，菜单的显示文本                            
		 */
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "返回主页").setIcon(
				android.R.drawable.ic_menu_more);
		// setIcon()方法为菜单设置图标，这里使用的是系统自带的图标，同学们留意一下,以
		// android.R开头的资源是系统提供的，我们自己提供的资源是以R开头的
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "刷新微博").setIcon(
				android.R.drawable.ic_menu_info_details);
		//			menu.add(Menu.NONE, Menu.FIRST + 3, 3, getString(R.string.Menu_userUnBinding).toString()).setIcon(
		//					android.R.drawable.ic_menu_help);
		//			menu.add(Menu.NONE, Menu.FIRST + 4, 4, getString(R.string.Menu_reLogin).toString()).setIcon(
		//					android.R.drawable.ic_menu_add);
		//			menu.add(Menu.NONE, Menu.FIRST + 5, 5, "详细").setIcon(
		//					android.R.drawable.ic_menu_info_details);
		//			menu.add(Menu.NONE, Menu.FIRST + 6, 6, "发送").setIcon(
		//					android.R.drawable.ic_menu_send);
		// return true才会起作用
		return true;
		
	}
	
	// 菜单项被选择事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			// Toast.makeText(this, "删除菜单被点击了", Toast.LENGTH_LONG).show();
			//				this.webView.loadUrl("http://61.181.14.184:8084/demo/flipboard.html");
			//flipWgt.gotoHome();
			flipper.this.finish();
			break;
		case Menu.FIRST + 2:
			// 用户绑定
			 Toast.makeText(this, "刷新菜单被点击了", Toast.LENGTH_LONG).show();
			 
			flipWgt.refrash();
			DownLoadThread dlt = new DownLoadThread("weibo");
			dlt.start();
			
			break;
		}
		
		return false;
	}
	
	// 选项菜单被关闭事件，菜单被关闭有三种情形，menu按钮被再次点击、back按钮被点击或者用户选择了某一个菜单项
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		// Toast.makeText(this, "选项菜单关闭了", Toast.LENGTH_LONG).show();
	}
	
	// 菜单被显示之前的事件
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// Toast.makeText(this,
		// "选项菜单显示之前onPrepareOptionsMenu方法会被调用，你可以用此方法来根据打当时的情况调整菜单",
		// Toast.LENGTH_LONG).show();
		// 如果返回false，此方法就把用户点击menu的动作给消费了，onCreateOptionsMenu方法将不会被调用
		
		return true;
	}	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//IdentifyUtil.deleteAllFile("/data/data/com.jinke.readman/cache");
	}
	
	
	public interface searchCallback{
		public void callback(boolean flag);
	}
	
	public interface TimeCallback{
		public void callback(int size);
	}
	
	public interface OverCallback{
		public void callback(boolean over);
	}
	
}
