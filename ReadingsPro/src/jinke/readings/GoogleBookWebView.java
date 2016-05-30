package jinke.readings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class GoogleBookWebView extends Activity{
	
	private String book_id = "";
	private int width = 0;
	private int height = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cwebview);
		Intent mIntent = getIntent();
		if(mIntent != null){
			book_id = mIntent.getStringExtra("book_id");
		}
		
		WebView webView = (WebView) findViewById(R.id.mWebView);
		// 让浏览器支持JavaScript
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		// 清除浏览器缓存
		webView.clearCache(true);
		// 指定浏览器需要解析的url地址
		
		width = webView.getWidth();
		height = webView.getHeight();
		
		System.out.println("webView:width*height:"+width+"*"+height);
		
		String url = "http://61.181.14.184:8084/googleBook/embedviewer.jsp?isbn="+book_id;
		
		System.out.println("-------------"+url);
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webView.loadUrl("http://61.181.14.184:8084/googleBook/embedviewer.jsp?isbn="+book_id);
		System.out.println("webView:width*height:"+width+"*"+height);
	}	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		System.out.println("webView:width*height:"+width+"*"+height);
		super.onStart();
	}
}
