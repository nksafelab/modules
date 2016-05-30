package jinke.readings;

import java.io.File;
import java.util.ArrayList;

import jinke.readings.util.PushReciever;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CTest2 extends Activity{

	private ListView pushListView;
	private static ArrayList<String> curList = null;                 //存储文件列表
	private static ArrayList<String> curNameList = null;   
	public static adapter2 push_adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.specbtn);
		
		scanFile("/sdcard/readings/");
		
		System.out.println("curList.size : "+curList.size());
		
		 PushReciever.write("/mnt/sdcard/readings/init", ""+curList.size());
		
		pushListView = (ListView)findViewById(R.id.listview1);
		
		if(pushListView == null){
			System.out.println("listView null");
		}else{
			System.out.println("listView not null");
		}
		
		 //初始化
		
        //获得TF状态
        if(Environment.getExternalStorageState().equals("removed"))
        {
        	Toast.makeText(getApplicationContext(), "请插入TF卡", Toast.LENGTH_LONG).show();
        }
		
        push_adapter = new adapter2(CTest2.this, R.layout.push_list_item, curList);
        
		
        pushListView.setAdapter(push_adapter);
        
        pushListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				String name = (String)curNameList.get(position);
				System.out.println("position+ "+position + " id:  "+id+name);
				
				System.out.println("clicked file name:"+name);
				String path = "/sdcard/readings/"+name;
				System.out.println("clicked file path:"+path);
				
				File f = new File(path);
				Intent intent = new Intent();
		        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        intent.setAction(android.content.Intent.ACTION_VIEW);  
		        String type = getMIMEType(f);/* 调用getMIMEType()来取得MimeType */
		        intent.setDataAndType(Uri.fromFile(f),type);/* 设置intent的file与MimeType */
		        intent.putExtra("JKVIEWER",path);
		        startActivity(intent);
			}
		});
        
        push_adapter.notifyDataSetChanged();
        
	}
	  /* 判断文件MimeType的method */
    private String getMIMEType(File f)
    {
      String type="";
      String fName=f.getName();
      /* 取得扩展名 */
      String end=fName.substring(fName.lastIndexOf(".")
      +1,fName.length()).toLowerCase();
     
      /* 依扩展名的类型决定MimeType */
      if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
      end.equals("xmf")||end.equals("ogg")||end.equals("wav"))
      {
        type = "audio";
      }
      else if(end.equals("3gp")||end.equals("mp4"))
      {
        type = "video";
      }
      else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
      end.equals("jpeg")||end.equals("bmp"))
      {
        type = "image";
      }
      else if(end.equals("apk"))
      {
        /* android.permission.INSTALL_PACKAGES */
        type = "application/vnd.android.package-archive";
      }
      else if(end.equals("wol")||end.equals("doc")||end.equals("txt")||end.equals("rtf")||end.equals("pdb")||end.equals("chm")||
    		  end.equals("lit"))
      {
    	  type = "application/"+end;
      }
      else
      {
        type="*";
      }
      /*如果无法直接打开，就跳出软件列表给用户选择 */
      if(end.equals("apk")||end.equals("wol")||end.equals("doc")||end.equals("txt")||end.equals("rtf")||end.equals("pdb")||end.equals("chm")||
    		  end.equals("lit"))
      {
      }
      else
      {
        type += "/*"; 
      }
      return type; 
    } 
	
	//扫描一层文件列表
    public static void scanFile(String rootpath)
    {
 		File file = new File(rootpath);
    	if(!file.isDirectory())
    	{
    		return;
    	}
    	
    	File[] file1 = file.listFiles();
    	int sum = 0;
    	if(file1 != null)
    		sum = file1.length;
    	if(curList != null)
    		curList.clear();
    	else
    	    curList = new ArrayList<String>();
    	
    	if(curNameList != null)
    		curNameList.clear();
    	else
    		curNameList = new ArrayList<String>();
    	
    	for(int i = 0;i != sum;i++)
    	{
    		String filepath = file1[i].getAbsolutePath();
    		String filename = filepath.substring(filepath.lastIndexOf("/")+1, filepath.length());
    		System.out.println("---------"+filename);
    		if("init".equals(filename)){
    			
    		}else{
			curList.add(filepath);
			curNameList.add(filename);
    		}
    	}
    }

}
