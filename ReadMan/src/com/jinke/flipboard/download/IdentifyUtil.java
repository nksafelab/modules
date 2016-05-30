package com.jinke.flipboard.download;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class IdentifyUtil {
	
	private static String path = "/sdcard/readings/identify.ini";
	static String devi = "123456";
	
	public static String getIdentifyID(){
		String devID = read(path);
		if(devID == "" && devID == null){
			write(path, devi);
			return devi;
		}
		return devID;
	}
	
	  public static boolean deleteAllFile(String folderFullPath){
	        boolean ret = false;
	        File file = new File(folderFullPath);
	        if(file.exists()){
	          if(file.isDirectory()){
	              File[] fileList = file.listFiles();
	    for (int i = 0; i < fileList.length; i++) {
	     String filePath = fileList[i].getPath();
	     deleteAllFile(filePath);
	    }
	   }
	             if(file.isFile()){
	              file.delete();
	             }
	        }
	        return ret;
	    }
	
	public static boolean exits(String path){
	   	 File f = new File(path);
		       if (f.exists()) {
		    	   System.out.println("exits("+path+")@IdentifyUtil-------文件存在----------------");
		        return true;
		       } else {
		    	   System.out.println("exits("+path+")@IdentifyUtil-------文件不存在----------------");
			       
			        return false;
		       }
	   }
	
	public static void write(String path, String content) {
	      String s = new String();
	      String s1 = new String();
	      try {
	       File f = new File(path);
	       if (f.exists()) {
	    	  // System.out.println("write("+path+")@PushReciever-------文件存在----------------");
	       } else {
	    	   System.out.println("write("+path+")@IdentifyUtil-------文件不存在----------------");
	       
	        if (f.createNewFile()) {
	        	System.out.println("write("+path+")@IdentifyUtil-------文件创建成功！----------------");
	        } else {
	        	System.out.println("write("+path+")@IdentifyUtil-------文件创建失败！----------------");

	        }

	       }
	       BufferedReader input = new BufferedReader(new FileReader(f));
	       BufferedWriter output = new BufferedWriter(new FileWriter(f));
	       output.write(content);
	       output.close();
	      } catch (Exception e) {
	       e.printStackTrace();
	      }
	}

	public static String read(String file) {
		   String s = null;
		   StringBuffer sb = new StringBuffer();
		   File f = new File(file);
		   if (f.exists()) {
		    //System.out.println("read(+"+file+")文件存在-----------------");

		    try {
		     BufferedReader br = new BufferedReader(new InputStreamReader(
		       new FileInputStream(f)));

		     while ((s = br.readLine()) != null) {
		      sb.append(s);
		     }
		     System.out.println("read(+"+file+")@IdentifyUtil-----------------"+sb);
		     
		    } catch (Exception e) {
		     e.printStackTrace();
		    }
		   }else{
			   System.out.println("read(+"+file+")文件不存在-----------------");
		    write(path, devi);
		    return "";
		   }
		   return sb.toString();
		}
	
	 // byte数组转换为16进制字符串   
    public static String byte2hex(byte[] data) {   
        StringBuffer sb = new StringBuffer();   
        for (int i = 0; i < data.length; i++) {   
            String temp = Integer.toHexString(((int) data[i]) & 0xFF);   
            for (int t = temp.length(); t < 2; t++) {   
                sb.append("0");   
            }   
            sb.append(temp);   
        }   
        return sb.toString();   
    }   
  
    // 16进制转换为byte数组   
    public static byte[] hex2byte(String hexStr) {   
        byte[] bts = new byte[hexStr.length() / 2];   
        for (int i = 0, j = 0; j < bts.length; j++) {   
            bts[j] = (byte) Integer.parseInt(hexStr.substring(i, i + 2), 16);   
            i += 2;   
        }   
        return bts;   
    }   
    
    public static void main(String[] args) {
		String username = "haha";
		
		byte[] ub = username.getBytes();
		
		System.out.println(username);
		
	}
    
}
