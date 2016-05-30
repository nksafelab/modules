package jinke.readings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.BounceInterpolator;
import android.webkit.WebView;

public class CWebView extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.cwebview);
		
		Intent oIntent = getIntent();
		Bundle b = oIntent.getExtras();
	String link = (String)b.get("link");
		
		System.out.println("link -------"+link);
		
		//link = "http://www.baidu.com";
		
		WebView wv = (WebView)findViewById(R.id.mWebView);
		
		wv.loadUrl(link);
		
		
	}

}
