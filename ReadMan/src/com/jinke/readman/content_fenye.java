package com.jinke.readman;

import java.security.KeyStore.LoadStoreParameter;
import com.jinke.flipboard.hash.UrlHash;
import com.jinke.flipboard.widget.mWebView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class content_fenye extends Activity{
	private static final String TAG = "contentWebView";
	private WebView contentWV;
	private ProgressBar mprogressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		/**全屏设置，隐藏窗口所有装饰*/  
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		/**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效*/  
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.content_fenye);
		
		mprogressBar = (ProgressBar)findViewById(R.id.content_progress_bar_fenye);
		Intent mIntent = getIntent();
		String url = mIntent.getStringExtra("url");
		Log.v(TAG, "contentWebView loadURL:"+url);
		
		contentWV = (WebView)findViewById(R.id.contentWV_fenye);
		contentWV.getSettings().setSupportZoom(false);
		contentWV.getSettings().setJavaScriptEnabled(true);  
		contentWV.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		contentWV.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		
		contentWV.setWebViewClient(new WebViewClient(){
 			@Override
 			public boolean shouldOverrideUrlLoading(WebView view, String url) {  //重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
 				Log.e("content", "loadUrl:"+url);
 				if("quit://".equals(url)){
 					Log.e("content", "Quit:"+url);
 					content_fenye.this.finish();
 				}else if(url.startsWith("pic://")){
// 					Toast.makeText(content_fenye.this, "pic url:"+url.substring(6, url.length()), Toast.LENGTH_SHORT).show();
 					startActivity(new Intent().setClass(content_fenye.this, pic_content.class).putExtra("picUrl", url.substring(6, url.length())));
 				}else{
 					view.loadUrl(url);
 				}
 				
 			    return true;
 			}
 			
 			@Override
			public void onPageFinished(WebView webview,
					String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(webview, url);
				
				mprogressBar.setVisibility(View.GONE);
				
			}
			
			@Override
			public void onPageStarted(WebView view,
					String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				
				mprogressBar.setVisibility(View.VISIBLE);
			}
			
		});
 			
		contentWV.loadUrl(url);
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
//		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "返回主页").setIcon(
//				android.R.drawable.ic_menu_close_clear_cancel);
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
			Intent cIntent = new Intent();
			cIntent.setClass(content_fenye.this, index.class);
			startActivity(cIntent);
			content_fenye.this.finish();
			break;
		case Menu.FIRST + 2:
			// 用户绑定
			// Toast.makeText(this, "保存菜单被点击了", Toast.LENGTH_LONG).show();
			//IdentifyUtil.deleteAllFile("/data/data/com.jinke.readman/cache/webviewCache");
			
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
	
}
