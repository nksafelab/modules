package jinke.readings.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import jinke.readings.CMainUI;
import jinke.readings.CTest2;
import jinke.readings.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PushReciever extends BroadcastReceiver{

//	private Context context;
//	
	public PushReciever(){
	}
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		CMainUI.btnPeople.setBackgroundResource(R.drawable.push_new_up);
		CTest2.scanFile("/sdcard/readings/");
		if(CTest2.push_adapter == null){
		
		}else{
			CTest2.push_adapter.notifyDataSetChanged();
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
	    	   System.out.println("====	write("+path+")@PushReciever-------文件不存在----------------");
	       
	        if (f.createNewFile()) {
	        	System.out.println("====	write("+path+")@PushReciever-------文件创建成功！----------------");
	        } else {
	        	System.out.println("====	write("+path+")@PushReciever-------文件创建失败！----------------");

	        }

	       }
	       BufferedReader input = new BufferedReader(new FileReader(f));

//	       while ((s = input.readLine()) != null) {
//	        s1 += s;
//	       }
//	       System.out.println("文件内容：" + s1);
//	       input.close();
//	       s1 += content;

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
			   //System.out.println("read()@PushReciever-------文件存在----------------");

		    try {
		     BufferedReader br = new BufferedReader(new InputStreamReader(
		       new FileInputStream(f)));

		     while ((s = br.readLine()) != null) {
		      sb.append(s);
		     }
		     System.out.println("	read()@PushReciever-------从"+file+"中读出内容----------------"+sb);
		     
		    } catch (Exception e) {
		     e.printStackTrace();
		    }
		   }else{
			   System.out.println("	read("+file+")@PushReciever-------文件不存在----------------");
		   }
		   return sb.toString();
		}
	
	//扫描一层文件列表
    public static int scanFileNum(String rootpath)
    {
 		File file = new File(rootpath);
    	if(!file.isDirectory())
    	{
    		return 0;
    	}
    	
    	File[] file1 = file.listFiles();
    		
    	return file1.length;
    	
    }
    
    public static boolean exits(String path){
    	 File f = new File(path);
	       if (f.exists()) {
	    	   System.out.println("	exits("+path+")@PushReciever-------文件存在----------------");
	        return true;
	       } else {
	    	   System.out.println("	exits("+path+")@PushReciever-------文件不存在----------------");
		       
		        return false;
	       }
    }

}
