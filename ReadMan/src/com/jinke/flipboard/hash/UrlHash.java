package com.jinke.flipboard.hash;

import java.security.KeyStore.LoadStoreParameter;

import com.jinke.flipboard.download.DownLoadThread;
import com.jinke.readman.flipper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class UrlHash {
	private String username = "";
	
	private String RSS_KEY = "http://61.181.14.184:8084/NewReadman/getRss.do?username="+username+"&page=1";
	private String WEIBO_KEY = "http://61.181.14.184:8084/NewReadman/getSinaweibo.do?username=marklee&size=1";
	private String SHARE_KEY = "";
	private String WIKI_KEY = "";
	private String BLOG_KEY = "";
	
	private String RSS_PATH = "http://61.181.14.184:8084/NewReadman/getRss.do?username=marklee&page=1";
	private String WEIBO_PATH = "http://61.181.14.184:8084/NewReadman/getSinaweibo.do?username=marklee&size=1";
	private String SHARE_PATH = "";
	private String WIKI_PATH = "";
	private String BLOG_PATH = "";
	
	private Context context;
	private String url;
	private String key;
	private Intent mIntent;
	private Bundle mBundle;
	
	public UrlHash(Context context){
		this.context = context;
		mIntent = new Intent();
		mBundle = new Bundle();
		
	}

	private void initUrl(){
		RSS_KEY = "http://61.181.14.184:8084/NewReadman/getRss.do?username="+username+"&page=1";
		WEIBO_KEY = "http://61.181.14.184:8084/NewReadman/getSinaweibo.do?username="+username+"&size=1&type=0";
		
		
		RSS_PATH = "http://61.181.14.184:8084/NewReadman/getRss.do?username="+username+"&page=1";
		WEIBO_PATH = "http://61.181.14.184:8084/NewReadman/getSinaweibo.do?username="+username+"&size=1&type=0";
		
	}
	
	public void go(String username,String url){
		
		this.username = username;
		initUrl();
		Log.v("UrlHash", RSS_KEY);
		
		if(RSS_KEY.equals(url)){
			url = RSS_PATH;
			key = RSS_KEY;
			
			DownLoadThread dlt = new DownLoadThread("rss");
			dlt.start();
			
		}
		else if(WEIBO_KEY.equals(url)){
			url = WEIBO_PATH;
			key = WEIBO_KEY;
			
			DownLoadThread dlt = new DownLoadThread("weibo");
			dlt.start();
			
		}
		else if(SHARE_KEY.equals(url)){
			url = SHARE_PATH;
			key = SHARE_KEY;
		}
		else if(WIKI_KEY.equals(url)){
			url = WIKI_PATH;
			key = WIKI_KEY;
		}
		else if(BLOG_KEY.equals(url)){
			url = BLOG_PATH;
			key = BLOG_KEY;
		}
		
		Log.v("UrlHash", "key:"+key);
		
		mBundle.putString("key", key);
		
		mBundle.putString("url", url);
		Log.v("UrlHash", "url:"+url);
		mIntent.putExtras(mBundle);
		mIntent.setClass( context, flipper.class);
		context.startActivity(mIntent);
		
	}
	
	
}
