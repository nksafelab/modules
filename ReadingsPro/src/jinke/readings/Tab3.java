package jinke.readings;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class Tab3 extends TabActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab3);
		
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Reusable TabSpec for each tab
		
		Intent intent;
		// 创建search的tabSpec并加入TabHost
		intent = new Intent().setClass(this, RecentReadingList.class);
		spec = tabHost.newTabSpec("info").setIndicator("tab1")
				.setContent(intent);
		tabHost.addTab(spec);
		// 创建info的tabSpec并加入TabHost
		intent = new Intent().setClass(this, SearchResultList.class);
		spec = tabHost.newTabSpec("info").setIndicator("tab2")
				.setContent(intent);
		tabHost.addTab(spec);

		// 创建paper的tabSpec并加入TabHost
		intent = new Intent().setClass(this, RecentReadingList_old.class);
		spec = tabHost.newTabSpec("paper").setIndicator("tab3")
				.setContent(intent);
		tabHost.addTab(spec);
		
		tabHost.setCurrentTab(0);
		
	}
	
	

}
