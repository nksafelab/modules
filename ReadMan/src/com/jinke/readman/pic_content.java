package com.jinke.readman;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import com.jinke.flipboard.widget.TouchView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class pic_content extends Activity{
	private static final String TAG = "contentWebView";
	TouchView tv;
	Button saveButton;
	Bitmap b;
	String picUrl;
	Handler handler;
//	String path = "/data/data/com.jinke.readman/pic/";
	String path = "/sdcard/readman_pic/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		/**全屏设置，隐藏窗口所有装饰*/  
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		/**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效*/  
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		picUrl = getIntent().getExtras().getString("picUrl");
		Log.v("pic", picUrl);
		setContentView(R.layout.pic_view);
		tv = (TouchView)findViewById(R.id.tv);
		
		picThread p = new picThread(1);
		p.start();
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what == 0){
					tv.setImageBitmap(b);
					saveButton.setVisibility(View.VISIBLE);
				}
				else{
					Toast.makeText(pic_content.this, "存储完毕"	, Toast.LENGTH_LONG).show();
					saveButton.setVisibility(View.INVISIBLE);
				}
			}
		};
		
		saveButton = (Button)findViewById(R.id.save_button);
		saveButton.setVisibility(View.INVISIBLE);
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				picThread p = new picThread(0);
				p.start();
			}
		});
		
	}
	
	
	class picThread extends Thread{
		int type;
		public picThread(int type){
			this.type = type;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(type == 1){
				b = getBitmapFromUrl(picUrl);
				handler.sendEmptyMessage(0);
			}else{
				try {
					saveFile(b, getCurrentTime(0)+".jpg");
					handler.sendEmptyMessage(1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
     * 到Url去下載圖片回傳BITMAP回來
     * @param imgUrl
     * @return
     */
    private Bitmap getBitmapFromUrl(String imgUrl) {
                URL url;
                Bitmap bitmap = null;
                try {
                        url = new URL(imgUrl);
                        InputStream is = url.openConnection().getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(is);
                        bitmap = BitmapFactory.decodeStream(bis);
                        bis.close();
                } catch (MalformedURLException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return bitmap;
        }
    
    /** 
     * 保存文件 
     * @param bm 
     * @param fileName 
     * @throws IOException 
     */  
    public void saveFile(Bitmap bm, String fileName) throws IOException {  
        File dirFile = new File(path);  
        if(!dirFile.exists()){  
            dirFile.mkdir();  
            Log.e("pic", "creat dir");
        }  
        File myCaptureFile = new File(path + fileName);  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);  
        bos.flush();  
        bos.close();  
    }  
    
    public String getCurrentTime(int backtime){
		
		Calendar can = Calendar.getInstance();
		can.add(Calendar.MINUTE, backtime);
		System.out.println(can.get(Calendar.HOUR_OF_DAY));
		System.out.println(can.get(Calendar.MINUTE));
		
		return can.get(Calendar.YEAR)+"-"+(can.get(Calendar.MONTH)+1)+"-"+can.get(Calendar.DAY_OF_MONTH)+"-"+can.get(Calendar.HOUR_OF_DAY)+"-"+can.get(Calendar.MINUTE);
	}
}
