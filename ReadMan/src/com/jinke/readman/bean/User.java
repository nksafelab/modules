package com.jinke.readman.bean;

import java.security.acl.LastOwnerException;

import android.util.Log;

public class User {
	
	private static User user = null;
	
	private User(String username){
		this.username = username;
	}
	
	private String username = null;
	private String phone = null;
	private String city = null;
	private String email = null;
	private static String lastTime = null;
	private static String token;
	private static String tokenpwd;
	
	private static int page;
	
	
	public synchronized static String getTime() {
		return lastTime;
	}
	public synchronized static int getPage() {
		return page;
	}
	public synchronized static String getToken() {
		return token;
	}
	public synchronized static String getTokenPWD() {
		return tokenpwd;
	}
	public synchronized static void setPage(int page) {
		user.page = page;
	}
	public synchronized static void setToken(String token,String tokenpwd) {
		user.token = token;
		user.tokenpwd = tokenpwd;
	}
	
	public synchronized static void setTime(String time) {
		user.lastTime = time;
	}

	public synchronized static void setUserName(String username){
		if(user == null){
			user = new User(username);
		}else{
			
		}
	}
	
	public synchronized static User getInstance(){
		if(user == null){
			Log.v("User", "it should never be happended!!!!");
		}
			return user;
	}
	
	public String getUserName(){
		return username;
	}
	
}
