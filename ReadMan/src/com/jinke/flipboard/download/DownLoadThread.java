package com.jinke.flipboard.download;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import com.jinke.readman.bean.User;
import android.content.Context;
import android.util.Log;

public class DownLoadThread extends Thread{
	private Context context;
//	private String userName = "marklee";
	private String userName = User.getInstance().getUserName();
	private String path = "/data/data/com.jinke.readman/cache/";
	private String rssURL = "http://61.181.14.184:8084/NewReadman/getRss.do?username="+userName+"&page=";
	private String weiboURL = "http://61.181.14.184:8084/NewReadman/getSinaweibo.do?username="+userName+"&size=";
	private String url = "";
	private String fileName = "";
	private int length =16;
	
	
	private DefaultHttpClient httpclient;
	private HttpGet httpget;
	private ResponseHandler<String> responseHandler = new BasicResponseHandler(); 
	
	
	public DownLoadThread(String path){
		this.fileName = path;
		if("rss".equals(fileName)){
			url = rssURL;
		}else if("weibo".equals(fileName)){
			url = weiboURL;
			length = User.getPage();
			Log.e("download", "length:"+length);
		}
	}
	
	public DownLoadThread(String path,int length){
		this.length = length;
		this.fileName = path;
	}
	public DownLoadThread(String userName,String path,int length){
		this.userName = userName;
		this.length = length;
		this.fileName = path;
	}
	
	public String getHtml(String url) throws IOException, URISyntaxException{
		URI u=new URI(url);
		
		httpget = new HttpGet(u);   
		
		return httpclient.execute(httpget, responseHandler);  	
	}
	
	private void downRSS(){
		String context = "";
		for(int i=1;i<=length;i++){
			try {
				context = getHtml(url+i);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			IdentifyUtil.write(path+fileName+i+".html", context);
			Log.v("DownCacheThread", "download:"+path+fileName+i);
			
		}//end for
	}
	private void downWEIBO(){
		String context = "";
		Log.v("DownCacheThread", "download weibo");
		for(int i=1;i<=length;i++){
			try {
				context = getHtml(url+(1+(i-1)*4)+"&type=0");
				Log.v("WEIBO DownLoadURL","download URL:"+url+(1+(i-1)*4));
				Log.v("WEIBO", url+(1+(i-1)*4));
				Log.v("WEIBO", context);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			IdentifyUtil.write(path+fileName+(1+(i-1)*4)+".html", context);
			Log.v("DownCacheThread", "download:"+path+fileName+(1+(i-1)*4));
			//http://61.181.14.184:8084/NewReadman/getSinaweibo.do?username=marklee&size=1

			//not http://61.181.14.184:8084/NewReadman/getSinaweibo.do?usernamemarklee&size=1
			//yes http://61.181.14.184:8084/NewReadman/getSinaweibo.do?username=marklee&size=1
			
		}//end for
	}
	
	private boolean isRefrash(){
		
		if(! IdentifyUtil.exits(path+"weibo1.html")){
			return true;
		}
		
		File file = new File(path+"weibo1.html");
		String lastTime = (new SimpleDateFormat("yyyy-MM-dd'%20'HH:mm:ss")).format(file.lastModified());
		
		String url = "http://61.181.14.184:8084/NewReadman/weiboUpdate.do?" +
				"token="+User.getToken() +
				"&tokenpwd="+User.getTokenPWD() +
				"&time="+lastTime;
		
		Log.e("refrash", url);
		String context = "";
		try {
			context = getHtml(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("refrash", "context:"+context);
		Log.e("refrash", ""+ "1".equals(context.trim()));
		Log.e("DownCacheThread", "refrash return:"+ "1".equals(context.trim()));
		return "1".equals(context.trim());
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		Log.v("DownCacheThread", "download run");
		httpclient = new DefaultHttpClient(); 
		
		if("rss".equals(fileName)){
			downRSS();
		}else if("weibo".equals(fileName)){
			Log.v("DownCacheThread", "download weibo !");
			if(isRefrash()){
				Log.v("DownCacheThread", "download weibo!!!!!!!!!!!!");
				IdentifyUtil.deleteAllFile("/data/data/com.jinke.readman/cache");
				downWEIBO();
			}
			
		}
		
		
	}
}
