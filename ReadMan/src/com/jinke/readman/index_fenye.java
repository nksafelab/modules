package com.jinke.readman;

import java.security.KeyStore.LoadStoreParameter;

import com.jinke.flipboard.download.IdentifyUtil;
import com.jinke.flipboard.download.PageThread;
import com.jinke.flipboard.hash.UrlHash;
import com.jinke.flipboard.widget.mWebView;
import com.jinke.readman.bean.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class index_fenye extends Activity{
	
	private WebView indexWV;
	private UrlHash mUrlHash;
	private ProgressBar mprogressBar;
	private String username;
	private boolean offline;
	private boolean refrash = false;
	private static final String TAG = "index";
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		/**全屏设置，隐藏窗口所有装饰*/  
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		/**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效*/  
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.index_fenye);
		offline = getIntent().getBooleanExtra("offline", false);
		
		username = User.getInstance().getUserName();
		Log.v(TAG, "username:"+username);
		
		PageThread pt = new PageThread();
		pt.start();
		
		mUrlHash = new UrlHash(index_fenye.this);
		
		mprogressBar = (ProgressBar)findViewById(R.id.index_progress_bar_fenye);
		indexWV = (WebView)findViewById(R.id.indexWV_fenye);
		indexWV.getSettings().setSupportZoom(false);
		indexWV.getSettings().setJavaScriptEnabled(true);  
		indexWV.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
		indexWV.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		indexWV.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		
		indexWV.setWebViewClient(new WebViewClient(){
				
				
 			@Override
 			public boolean shouldOverrideUrlLoading(WebView view, String url) {  //重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
 				Log.e("Click url:", url.substring(0, 7)+":");
 				String sub_url = url.substring(0, 7);
 				if("readman".equals(sub_url)){
 					indexWV.loadUrl("http"+url.substring(7, url.length()));
 					Log.e("load:", "http"+url.substring(7, url.length()));
 				}else{
 					mUrlHash.go(username,url);
 				}
 				
 			    return true;
 			}
 			@Override
			public void onPageFinished(WebView webview,
					String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(webview, url);
				Log.e("finish:", url);
				mprogressBar.setVisibility(View.GONE);
				if(refrash && !offline)
					webview.loadUrl("javascript:window.local_obj.showSource('<head>'+" +
	                "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
				
			}
			
			@Override
			public void onPageStarted(WebView view,
					String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				Log.e("start:", url);
				mprogressBar.setVisibility(View.VISIBLE);
			}
 			  
		});
		
		
		if(offline && IdentifyUtil.exits("/data/data/com.jinke.readman/cache/index.html")){
			Log.e("CacheExit", "index exit");
			indexWV.loadUrl("file:///data/data/com.jinke.readman/cache/index.html");
		}else{
			refrash = true;
			indexWV.loadUrl("http://61.181.14.184:8084/NewReadman/flipboard.do?username="+username);
		}
		
		Log.v(TAG, "webview loadURL:"+"http://61.181.14.184:8084/NewReadman/flipboard.do?username="+username);
	}
	
	 public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回键
			//	        if ((keyCode == KeyEvent.KEYCODE_BACK) && webviews.get(flipWgt.getMCurrentView()).canGoBack()) {   
			//	        	webviews.get(flipWgt.getMCurrentView()).goBack();   
			//	            return true;   
			//	        }else 
				        	if(keyCode == KeyEvent.KEYCODE_BACK){
				        	ConfirmExit();//按了返回键，但已经不能返回，则执行退出确认
				        	return true; 
				        }    
				        return super.onKeyDown(keyCode, event);   
				    }
			public void ConfirmExit(){//退出确认
				AlertDialog.Builder ad=new AlertDialog.Builder(index_fenye.this);
				ad.setTitle("退出");
				ad.setMessage("是否退出软件?");
				ad.setPositiveButton("是", new DialogInterface.OnClickListener() {//退出按钮
					@Override
					public void onClick(DialogInterface dialog, int i) {
						// TODO Auto-generated method stub
						index_fenye.this.finish();//关闭activity
						
					}
				});
				ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int i) {
						//不退出不用执行任何操作
					}
				});
				ad.show();//显示对话框
			}
	
	private void gotoRSS(){
		Intent mIntent = new Intent();
		mIntent.setClass(index_fenye.this, flipper.class);
		startActivity(mIntent);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		IdentifyUtil.deleteAllFile("/data/data/com.jinke.readman/cache");
	}
	final class InJavaScriptLocalObj {
		public void showSource(String html) {
			IdentifyUtil.write("/data/data/com.jinke.readman/cache/index.html", html);
			Log.d("HTML", html);
		}
	}
}
