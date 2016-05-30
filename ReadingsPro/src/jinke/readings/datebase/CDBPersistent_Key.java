package jinke.readings.datebase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import jinke.readings.R;
import jinke.readings.Entity.WenkuInfo;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CDBPersistent_Key {
	
	private String table;
	private Context context;
	private SQLiteDatabase db;
	private CDBHelper_Key cDBhelper;
	
	private boolean flags = true;
	
	private static final String DATEBASE_TABLE_KEY =  "create table keyword ("
		+ "_id integer primary key autoincrement, "
		+ "key text, "
		+ "created text);";
	
	private static final String DATEBASE_TABLE_HOT =  "create table hotword ("
		+ "_id integer primary key autoincrement, "
		+ "key text, "
		+ "created text);";
	
	private static final String DATEBASE_TABLE_REC =  "create table recent_reading ("
		+ "_id integer primary key autoincrement, "
		+ "title text, "
		+ "link text, "
		+ "abst1 text, "
		+ "sourceImg text, "
		+ "created text,"
		+ "thumb blob,"
		+ "source integer);";
	
	private static final String DATEBASE_TABLE_SEARCH_HIS =  "create table search_his ("
		+ "_id integer primary key autoincrement, "
		+ "key text, "
		+ "title text, "
		+ "link text, "
		+ "abst1 text, "
		+ "sourceImg text, "
		+ "created text,"
		+ "thumb blob,"
		+ "source integer,"
		+ "abst3 text);";
	
	
	   //----以下两个成员变量是针对在SD卡中存储数据库文件使用----   
    private File path = new File("/sdcard/readings/dbfile"); //数据库文件目录  
    private File f = new File("/sdcard/readings/dbfile/readingDB"); //数据库文件 
	
	
	public CDBPersistent_Key(Context context,String table){
		this.context = context;
		this.table = table;
		System.out.println("CDBPersistent_key()@CDBPersistent_key---------from table:-----------"+table);
		cDBhelper = new CDBHelper_Key(context);
	}
	
	
	public String getCurrent(){
		
		Calendar calender = Calendar.getInstance();
		
		String created = calender.get(Calendar.YEAR)+":" +
						calender.get(Calendar.MONTH)+":" +
						calender.get(Calendar.DAY_OF_MONTH)+":" +
						calender.get(Calendar.HOUR_OF_DAY)+":" +
						calender.get(Calendar.MINUTE)+":";
		return created;
	}
	
	public boolean insert(String key){
		if(key == null){
			System.out.println("insert(String key)@CDBPersistent: info is null");
			return false;
		}
		
		ContentValues initalValues = new ContentValues();
		initalValues.put("key", key);
		initalValues.put("created", getCurrent());
		
		return db.insert(table, null, initalValues) > 0;
		
	}
	
	public void open(){
		//----如要在SD卡中创建数据库文件，先做如下的判断和创建相对应的目录和文件----  
//        if(!path.exists()){   //判断目录是否存在  
//          path.mkdirs();    //创建目录  
//          System.out.println("open()@CDBPersistentcreat----------数据库文件夹不存在 创建目录----------------");
//        }  
//        if(!f.exists()){      //判断文件是否存在  
//          try{  
//              f.createNewFile();  //创建文件  
//              
//              this.db = SQLiteDatabase.openOrCreateDatabase(f, null);  
//      		
//	      		this.db.execSQL(DATEBASE_TABLE_KEY); 
//	      		this.db.execSQL(DATEBASE_TABLE_HOT); 
//	      		this.db.execSQL(DATEBASE_TABLE_REC); 
//	      		this.db.execSQL(DATEBASE_TABLE_SEARCH_HIS); 
//	      		System.out.println("open()@CDBPersistentcreat----------数据库表不存在 创建表----------------");
//	      		
//          }catch(IOException e){  
//              e.printStackTrace();  
//          }  
//        }else{
//        	this.db = SQLiteDatabase.openOrCreateDatabase(f, null); 
//        }
		
		 //[2]--如果是在SD卡中创建数据库，那么实例化sqlitedb的操作如下  
		
		
		this.db = cDBhelper.getWritableDatabase();
		//this.db.beginTransaction();
	}
	
	public boolean insertKey(String key){
		this.db.beginTransaction();
		boolean flag = true;
		if(exits(key)){
			//System.out.println("not exit");
			flag = insert(key);
		}else{
			//System.out.println("exit");
			flag = updateInfo(key);
		}
		
		
		this.db.setTransactionSuccessful();
		this.db.endTransaction();
		
		return flag;
	}
	
	public void trans(){
		this.db.setTransactionSuccessful();
		this.db.endTransaction();
	}
	
	public void close(){
		
		this.cDBhelper.close();
		this.db.close();
	}
	
	public boolean updateInfo(String key){
		
		ContentValues initalValues = new ContentValues();
		initalValues.put("key", key);
		initalValues.put("created", getCurrent());
		
		return this.db.update(table, initalValues, "key = ?", new String[]{key}) > 0;
	}
	
	public boolean deleteAll(){
		this.db.delete(table, null, null);
//		this.db.setTransactionSuccessful();
//		this.db.endTransaction();
		return true;
	}
	
	/**
	 * �����ڷ���true
	 * @param info
	 * @return
	 */
	public boolean exits(String key){
		Cursor cursor = null;
		
		cursor = this.db.query(table, new String[]{"_id","key","created"},
				"key = ?", new String[]{key}, null, null, null);
		
		if(cursor.getCount() == 0){
			//System.out.println("not exit");
			return true;
		}else{
			//System.out.println("exits");
			return false;
		}
		
		
	}
	
	public Cursor getAllKey(){
		
		//this.db = this.cDBhelper.getReadableDatabase();
		
		Cursor cursor = null;
		
		cursor = this.db.query(table, new String[]{"_id","key","created"},
				null, null, null, null, "created desc");
		
		System.out.println("CDBPersistent@getAllInfo--------:"+table+" "+cursor.getCount());
		
		//this.db.close();
//		String _id;
//		String key;
//		String created;
//		
//		System.out.println("_id	key	created");
//		for(int i = 0;i<cursor.getColumnCount();i++){
//			System.out.println("column"+i+" "+cursor.getColumnName(i));
//		}
//		System.out.println(cursor.getColumnName(0)
//						+""+cursor.getColumnName(1)
//						+""+cursor.getColumnName(2));
//		
//		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
//		{
//			_id = cursor.getString(cursor.getColumnIndex("_id"));
//			key = cursor.getString(cursor.getColumnIndex("key"));
//		    created = cursor.getString(cursor.getColumnIndex("created"));
//		    
//		    
//		    System.out.println(_id+"	"+key+"		"+created);
//		    
//		}
//		
		
		
		return cursor;
	}
	
}
