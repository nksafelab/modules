package jinke.readings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class SynContentWebView extends Activity{

	private WebView synWebView;
	private String load_url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.synwebview);
		
		Intent mIntent = getIntent();
		if(mIntent != null){
			Bundle mBundle = mIntent.getExtras();
			load_url = mBundle.getString("load_url");
		}
		
		synWebView = (WebView)findViewById(R.id.syn_webview);
		synWebView.loadUrl(load_url);
		
		
		
		
	}
}
