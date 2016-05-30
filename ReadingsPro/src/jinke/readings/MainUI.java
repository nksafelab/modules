package jinke.readings;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;



public class MainUI extends ActivityGroup {
	
	//private Intent intent;
	private Button searchButton;
	private Button requestButton;
	
	private FrameLayout frame = null;
	protected LocalActivityManager mLocalActivityManager;
	private View mLaunchedView;
	private Bundle bunlde;
	public static final int FOCUS_AFTER_DESCENDANTS = 0x40000;
	private TabHost tabHost;
	
	
	//added by zhong

    private String mDefaultTab = null;  

   private int mDefaultTabIndex = -1;  


	public void setDefaultTab(String tag) {
        mDefaultTab = tag;
        mDefaultTabIndex = -1;
    }

    /***
     * Sets the default tab that is the first tab highlighted.
     * 
     * @param index the index of the default tab
     */
    public void setDefaultTab(int index) {
        mDefaultTab = null;
        mDefaultTabIndex = index;
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        String cur = state.getString("currentTab");
        if (cur != null) {
            tabHost.setCurrentTabByTag(cur);
        }
        if (tabHost.getCurrentTab() < 0) {
            if (mDefaultTab != null) {
                tabHost.setCurrentTabByTag(mDefaultTab);
            } else if (mDefaultTabIndex >= 0) {
                tabHost.setCurrentTab(mDefaultTabIndex);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle icicle) {        
        super.onPostCreate(icicle);

                if (tabHost.getCurrentTab() == -1) {
            tabHost.setCurrentTab(0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String currentTabTag = tabHost.getCurrentTabTag();
        if (currentTabTag != null) {
            outState.putString("currentTab", currentTabTag);
        }
    }

    /***
     * Updates the screen state (current list and other views) when the
     * content changes.
     * 
     *@see Activity#onContentChanged()
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        tabHost = (TabHost) findViewById(R.id.myTabhost);

        tabHost.setup(getLocalActivityManager());
    }


    /***
     * Returns the {@link TabHost} the activity is using to host its tabs.
     *
     * @return the {@link TabHost} the activity is using to host its tabs.
     */
    public TabHost getTabHost() {
        return tabHost;
    }

    /***
     * Returns the {@link TabWidget} the activity is using to draw the actual tabs.
     *
     * @return the {@link TabWidget} the activity is using to draw the actual tabs.
     */
    public TabWidget getTabWidget() {
        return tabHost.getTabWidget();
    }

	//end added
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainui);
		
		tabHost = getTabHost();//(TabHost)findViewById(R.id.myTabhost); // The activity TabHost
		tabHost.setup(this.getLocalActivityManager());
		TabHost.TabSpec spec; // Reusable TabSpec for each tab
		// Reusable Intent for each tab


		searchButton = (Button)findViewById(R.id.searchButton);
		requestButton = (Button)findViewById(R.id.requestButton);
		
		Resources res = getResources(); // Resource object to get Drawables
		Intent intent;
		// 创建search的tabSpec并加入TabHost
		intent = new Intent().setClass(this, RecentReadingList.class);
		spec = tabHost.newTabSpec("info").setIndicator("")
				.setContent(intent);
		tabHost.addTab(spec);
		// 创建info的tabSpec并加入TabHost
		intent = new Intent().setClass(this, SearchResultList.class);
		spec = tabHost.newTabSpec("info").setIndicator("")
				.setContent(intent);
		tabHost.addTab(spec);

		// 创建paper的tabSpec并加入TabHost
		intent = new Intent().setClass(this, RecentReadingList_old.class);
		spec = tabHost.newTabSpec("paper").setIndicator("")
				.setContent(intent);
		tabHost.addTab(spec);
		
		
		TabWidget tabWidget = tabHost.getTabWidget();
		
		tabWidget.setStripEnabled(false);
		
		for (int i =0; i < tabWidget.getChildCount(); i++) {
			
			
			int width =70;

	         int height =30;

			tabWidget.getChildAt(i).getLayoutParams().height = height;

            tabWidget.getChildAt(i).getLayoutParams().width = width;
            
            View vvv = tabWidget.getChildAt(i);
            	switch (i) {
				case 0:
					vvv.setBackgroundDrawable(getResources().getDrawable(R.drawable.recentreading));
					break;

				case 1:
					vvv.setBackgroundDrawable(getResources().getDrawable(R.drawable.push));
					break;
				case 2:
					vvv.setBackgroundDrawable(getResources().getDrawable(R.drawable.requestbook));
					break;
				default:
					break;
				}
		          // vvv.setBackgroundDrawable(getResources().getDrawable(R.drawable.push));

		   }
		/**

         * 此方法是为了去掉系统默认的色白的底角

         * 

         * 在 TabWidget中mBottomLeftStrip、mBottomRightStrip

         * 都是私有变量，但是我们可以通过反射来获取

         * 

         * 由于还不知道Android 2.2的接口是怎么样的，现在先加个判断好一些

         */
		Field mBottomLeftStrip; 

        Field mBottomRightStrip;
        
           try { 
              mBottomLeftStrip = tabWidget.getClass().getDeclaredField ("mBottomLeftStrip"); 

              mBottomRightStrip = tabWidget.getClass().getDeclaredField ("mBottomRightStrip"); 

              if(!mBottomLeftStrip.isAccessible()) { 

                mBottomLeftStrip.setAccessible(true); 

              } 

              if(!mBottomRightStrip.isAccessible()){ 

                mBottomRightStrip.setAccessible(true); 

              } 

             mBottomLeftStrip.set(tabWidget, getResources().getDrawable (R.drawable.no)); 

             mBottomRightStrip.set(tabWidget, getResources().getDrawable (R.drawable.no)); 

           } catch (Exception e) { 

             e.printStackTrace(); 

           } 
		
           searchButton.setOnClickListener(new OnClickListener() {
			private Intent oIntent;
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				oIntent = new Intent(MainUI.this, SearchResultList.class);
				ChangeActivity("search", oIntent);
			}
		});
		
           
		
		tabHost.setCurrentTab(0);

	}
	
	public LocalActivityManager getLocalActivityManagerEx() {
		return new LocalActivityManager(this, true);
	}

	
	// 装载链接的试图到frma中显示
	private void ChangeActivity(String Id, Intent intent) {

		if (null == mLocalActivityManager) {
			mLocalActivityManager = getLocalActivityManagerEx();
			mLocalActivityManager.dispatchCreate(bunlde);
		}
		final Window w = mLocalActivityManager.startActivity(Id, intent);
		final View wd = w != null ? w.getDecorView() : null;

		if (mLaunchedView != wd && mLaunchedView != null) {
			if (mLaunchedView.getParent() != null) {
				frame.removeView(mLaunchedView);
			}
		}

		if (null != mLaunchedView && mLaunchedView == wd) {
			return;
		}
		mLaunchedView = wd;
		if (mLaunchedView != null) {
			// mLaunchedView.setVisibility(View.VISIBLE);
			mLaunchedView.setFocusableInTouchMode(true);
			((ViewGroup) mLaunchedView)
					.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
		}
		frame.addView(mLaunchedView, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		// containerBody.removeAllViews();
		// containerBody.addView(getLocalActivityManagerEx().startActivity(Id,
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView());
	}

}
