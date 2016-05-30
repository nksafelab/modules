package jinke.readings.datebase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import jinke.readings.R;
import jinke.readings.Entity.WenkuInfo;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CDBPersistent {
	
	private String table;
	private Context context;
	private SQLiteDatabase db;
	private CDBHelper cDBhelper;
	private byte[] arrbt = null;
	
	  //----以下两个成员变量是针对在SD卡中存储数据库文件使用----   
    private File path = new File("/sdcard/readings/dbfile"); //数据库文件目录  
    private File f = new File("/sdcard/readings/dbfile/readingDB"); //数据库文件 
	
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
    
	public CDBPersistent(Context context,String table){
		this.context = context;
		this.table = table;
		cDBhelper = new CDBHelper(context);
	}
	
	
	
	public byte[] bitmapDecode(Bitmap bmp) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		if(bmp == null){
			
			Resources res=context.getResources();
			bmp=BitmapFactory.decodeResource(res, R.drawable.default_book);
			
			System.out.println("bitmapDecode@CDBPersistent----exception bmp is null,use default book");
			
		}
		
		
		bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
		return out.toByteArray();
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
			System.out.println("CDBPersistent@insert: info is null");
			return false;
		}
		
		ContentValues initalValues = new ContentValues();
		initalValues.put("key", key);
		initalValues.put("created", getCurrent());
		
		return db.insert("keyword", null, initalValues) > 0;
		
	}
	
	public boolean updateKey(String key){
		
		ContentValues initalValues = new ContentValues();
		initalValues.put("key", key);
		initalValues.put("created", getCurrent());
		
		return this.db.update("keyword", initalValues, "key = ?", new String[]{key}) > 0;
	}

	
	public boolean exits(String key){
		Cursor cursor = null;
		cursor = this.db.query("search_his", new String[]{"_id","key","created"},
				"key = ?", new String[]{key}, null, null, null);
		
		if(cursor.getCount() == 0){
			//System.out.println("not exit");
			return false;
		}else{
			//System.out.println("exits");
			return true;
		}
	}
	public boolean exits(String key,String table){
		Cursor cursor = null;
		cursor = this.db.query(table, new String[]{"_id","key","created"},
				"key = ?", new String[]{key}, null, null, null);
		
		if(cursor.getCount() == 0){
			//System.out.println("not exit");
			return false;
		}else{
			//System.out.println("exits");
			return true;
		}
	}
	
	/**
	 * insertKey to table[keyword]
	 * @param key
	 */
	public void insertKey(String key){
		
		
		
		if(!exits(key,"keyword")){
			
			this.db.beginTransaction();
			insert(key);
			this.db.setTransactionSuccessful();
			this.db.endTransaction();
			System.out.println("-----CDBPersistent---------insertKey---------");
		}else{
			
			updateKey(key);
			System.out.println("-----CDBPersistent---------updatekey---------");
		}
	}
	
	public boolean insert(WenkuInfo info){
		if(info == null){
			System.out.println("CDBPersistent@insert: info is null");
			return false;
		}
		
		ContentValues initalValues = new ContentValues();
		initalValues.put("title", info.getTitle());
		initalValues.put("link", info.getLink());
		initalValues.put("abst1", info.getAbst1());
		initalValues.put("sourceImg", info.getSourceImg());
		initalValues.put("source", info.getSource());
		initalValues.put("created", getCurrent());
		
		
		try {
			initalValues.put("thumb", this.bitmapDecode(info.getThumb()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//initalValues.put("thumb", "thumb");
		
		return db.insert(table, null, initalValues) > 0;
		
	}
	
	public boolean insert(WenkuInfo info,String table,String key){
		if(info == null){
			System.out.println("CDBPersistent@insert: info is null");
			return false;
		}
		
		ContentValues initalValues = new ContentValues();
		initalValues.put("key", key);
		initalValues.put("title", info.getTitle());
		initalValues.put("link", info.getLink());
		initalValues.put("abst1", info.getAbst1());
		initalValues.put("sourceImg", info.getCover());
		initalValues.put("source", info.getSource());
		initalValues.put("created", getCurrent());
		initalValues.put("abst3", info.getAbst3());
		
		
		try {
			initalValues.put("thumb", this.bitmapDecode(info.getThumb()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//initalValues.put("thumb", "thumb");
		
		return db.insert("search_his", null, initalValues) > 0;
		
	}
	
	public boolean insert(List<WenkuInfo> ls,String table,String key){
		
//		if(!exits(key)){
			System.out.println("insert@CDBPersistent--------------------------------key not exit,insert search_result!");
			this.db.beginTransaction();
			WenkuInfo w = null;
			for(int i=0;i<ls.size();i++){
				w = ls.get(i);
				insert(w,"search_his",key);
				//System.out.println("insert List "+insert(w,"search_his",key));
			}

			this.db.setTransactionSuccessful();
			this.db.endTransaction();
			
			return true;
			
//		}else{
//			System.out.println("insert@CDBPersistent--------------------------------key exit,do nothing!");
//			return true;
//		}
		
		
	}
	
	public void open(){
		
		//----如要在SD卡中创建数据库文件，先做如下的判断和创建相对应的目录和文件----  
//        if(!path.exists()){   //判断目录是否存在  
//          path.mkdirs();    //创建目录  
//          System.out.println("creat dir!!!!!!!!!!!!!!!!!!!!!!!!!");
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
//	      		System.out.println("creat table!!!!!!!!!!!!!!!!!!!!!!!!!!");
//          }catch(IOException e){  
//              e.printStackTrace();  
//          }  
//        }else{
//        	this.db = SQLiteDatabase.openOrCreateDatabase(f, null); 
//        }
//		
		

		
		this.db = cDBhelper.getWritableDatabase();
//		this.db.beginTransaction();
	}
	
	
	public boolean insertInfo(WenkuInfo info){
		this.db.beginTransaction();
		boolean flag = true;
		if(exits(info)){
			//System.out.println("������--�������");
			flag = insert(info);
		}else{
			//System.out.println("����--�������");
			flag = updateInfo(info);
		}
		
		
		this.db.setTransactionSuccessful();
		this.db.endTransaction();
		
		return flag;
	}
	
	public void close(){
		
		
		this.db.close();
	}
	
	public boolean updateInfo(WenkuInfo info){
		
		ContentValues initalValues = new ContentValues();
		initalValues.put("title", info.getTitle());
		initalValues.put("link", info.getLink());
		initalValues.put("abst1", info.getAbst1());
		initalValues.put("sourceImg", info.getSourceImg());
		initalValues.put("created", getCurrent());
		
		return this.db.update(table, initalValues, "title = ?", new String[]{info.getTitle()}) > 0;
	}
	
	public boolean deleteAll(){
		this.db.delete(table, null, null);
		return true;
	}
	
	/**
	 * �����ڷ���true
	 * @param info
	 * @return
	 */
	public boolean exits(WenkuInfo info){
		Cursor cursor = null;
		
		cursor = this.db.query(table, new String[]{"_id","title","link","abst1","sourceImg"},
				"title = ?", new String[]{info.getTitle()}, null, null, null);
		
		if(cursor.getCount() == 0){
			//System.out.println("not exit");
			return true;
		}else{
			//System.out.println("exits");
			return false;
		}
		
		
	}
	
	public Cursor getAllInfo(){
		
		//this.db = this.cDBhelper.getReadableDatabase();
		
		Cursor cursor = null;
		
		cursor = this.db.query(table, new String[]{"_id","title","link","abst1","sourceImg","created","thumb","source"},
				null, null, null, null, "created desc");
		
		System.out.println("CDBPersistent@getAllInfo: "+cursor.getCount());
		
		this.db.close();
		String _id;
		String title;
		String link;
		String abst1;
		String sourceImg;
		String created;
		int source;
		
//		System.out.println("_id	title	link	abst1	sourceImg	created		source");
//		for(int i = 0;i<cursor.getColumnCount();i++){
//			System.out.println("column"+i+" "+cursor.getColumnName(i));
//		}
//		System.out.println(cursor.getColumnName(0)
//						+""+cursor.getColumnName(1)
//						+""+cursor.getColumnName(2)
//						+""+cursor.getColumnName(3)
//						+""+cursor.getColumnName(4)
//						+""+cursor.getColumnName(5)
//						+""+cursor.getColumnName(6)
//						+""+cursor.getColumnName(7));
		
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
		{
			_id = cursor.getString(cursor.getColumnIndex("_id"));
		    title = cursor.getString(cursor.getColumnIndex("title"));
		    link = cursor.getString(cursor.getColumnIndex("link"));
		    abst1 = cursor.getString(cursor.getColumnIndex("abst1"));
		    sourceImg = cursor.getString(cursor.getColumnIndex("sourceImg"));
		    created = cursor.getString(cursor.getColumnIndex("created"));
		    source = cursor.getInt(cursor.getColumnIndex("source"));
		    
		    arrbt = cursor.getBlob(cursor.getColumnIndex("thumb"));
		    Bitmap bm = BitmapFactory.decodeByteArray(arrbt, 0, arrbt.length);
		    
		    System.out.println(_id+"	"+title+"+sourceImg+"+created+"	"+source);
		    
		}
		
		
		
		return cursor;
	}
/**
 * getAllInfo from table[search_his]
 * @param key
 * @return cursor
 */
public Cursor getAllInfo(String key){
		
		//this.db = this.cDBhelper.getReadableDatabase();
		
		Cursor cursor = null;
		
		cursor = this.db.query("search_his", new String[]{"_id","title","link","abst1","sourceImg","created","thumb","source","abst3"},
				"key = ?", new String[]{key}, null, null, "created desc");
		
		System.out.println("CDBPersistent@getAllInfo: "+cursor.getCount());
		
		//this.db.close();
//		String _id;
//		String title;
//		String link;
//		String abst1;
//		String sourceImg;
//		String created;
//		int source;
//		
//		System.out.println("_id	title	link	abst1	sourceImg	created		source");
//		for(int i = 0;i<cursor.getColumnCount();i++){
//			System.out.println("column"+i+" "+cursor.getColumnName(i));
//		}
//		System.out.println(cursor.getColumnName(0)
//						+""+cursor.getColumnName(1)
//						+""+cursor.getColumnName(2)
//						+""+cursor.getColumnName(3)
//						+""+cursor.getColumnName(4)
//						+""+cursor.getColumnName(5)
//						+""+cursor.getColumnName(6)
//						+""+cursor.getColumnName(7));
//		
//		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
//		{
//			_id = cursor.getString(cursor.getColumnIndex("_id"));
//		    title = cursor.getString(cursor.getColumnIndex("title"));
//		    link = cursor.getString(cursor.getColumnIndex("link"));
//		    abst1 = cursor.getString(cursor.getColumnIndex("abst1"));
//		    sourceImg = cursor.getString(cursor.getColumnIndex("sourceImg"));
//		    created = cursor.getString(cursor.getColumnIndex("created"));
//		    source = cursor.getInt(cursor.getColumnIndex("source"));
//		    
//		    arrbt = cursor.getBlob(cursor.getColumnIndex("thumb"));
//		    Bitmap bm = BitmapFactory.decodeByteArray(arrbt, 0, arrbt.length);
//		    
//		    System.out.println(_id+"	"+title+"+sourceImg+"+created+"	"+source);
//		    
//		}
//		
		
		
		return cursor;
	}
public Cursor getAllInfo(String key,String source){
	
	//this.db = this.cDBhelper.getReadableDatabase();
	System.out.println("key:"+key+" source:"+source);
	Cursor cursor = null;
	System.out.println("this db"+this.db);
	cursor = this.db.query("search_his", new String[]{"_id","title","link","abst1","sourceImg","created","thumb","source","abst3"},
			"key = ? AND source = ?", new String[]{key,source}, null, null, "created desc");
	
	System.out.println("CDBPersistent@getAllInfo by Source: "+cursor.getCount());
	
	return cursor;
}

/**
 * getAllInfo from table[search_his]
 * @param key
 * @return cursor
 */
public Cursor getKeySource(String key){

	Cursor cursor = null;
	System.out.println("CDBPersistent getKeysource key="+key);
	cursor = this.db.query("search_his", new String[]{"source"},
			"key = ?", new String[]{key}, null, null,null);
	System.out.println("CDBPersistent@getKeySource"+cursor.getCount());
	for(int i=0;i<cursor.getColumnCount();i++){
		
		System.out.print("column "+i+" name is "+cursor.getColumnName(i));
		
		
	}
	return cursor;

	}
	
}
