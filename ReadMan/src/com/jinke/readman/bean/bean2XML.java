package com.jinke.readman.bean;

import com.jinke.rloginservice.UserInfo;

public class bean2XML {
	public static void main(String[] args) {
		EBean eb = new EBean();
		eb.setName("yujinyang");
		System.out.println(APIUtils.getXMLModel(eb));
		
	}
}
