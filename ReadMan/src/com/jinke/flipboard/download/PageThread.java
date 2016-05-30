package com.jinke.flipboard.download;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.jinke.readman.bean.User;

import android.R;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class PageThread extends Thread{
	
	private String userName = User.getInstance().getUserName();
	private Handler handler;
	private String page_url = "http://61.181.14.184:8084/NewReadman/getRssAmount.do?username="+userName;
	private String token_url = "http://61.181.14.184:8084/NewReadman/getToken.do?username="+userName;
	private DefaultHttpClient httpclient;
	private HttpGet httpget;
	private ResponseHandler<String> responseHandler = new BasicResponseHandler(); 
	
	public String getHtml(String url) throws IOException, URISyntaxException{
		URI u=new URI(url);
		
		httpget = new HttpGet(u);   
		
		return httpclient.execute(httpget, responseHandler);  	
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		httpclient = new DefaultHttpClient(); 
		
		String page_String = "";
		String token_String = "";
		
		try {
			page_String = getHtml(page_url);
			
			Log.v("PageThread", page_String);
			
			int start = page_String.indexOf("<total>");
			int end = page_String.indexOf("</total>");
			Log.v("PageThread", page_String);
			int page = Integer.parseInt(page_String.substring(start+7, end));
			Log.v("PageThread", page+"");
			User.setPage(page);
			
			token_String = getHtml(token_url);
			Log.v("PageThread", "token"+token_String);
			start = token_String.indexOf("<accessToken>");
			end = token_String.indexOf("</accessToken>");
			String token = token_String.substring(start+13, end);
			Log.v("PageThread", "token:"+token_String.substring(start+13, end));
			start = token_String.indexOf("<password>");
			end = token_String.indexOf("</password>");
			String tokenpwd = token_String.substring(start+10, end);
			Log.v("PageThread", "token:"+token_String.substring(start+10, end));
			User.setToken(token, tokenpwd);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
