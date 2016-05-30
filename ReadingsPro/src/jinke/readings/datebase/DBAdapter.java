package jinke.readings.datebase;

import java.util.Calendar;
import java.util.List;

import jinke.readings.Entity.WenkuInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.method.KeyListener;

public class DBAdapter {
	
	//数据库名，表名，版本号
	private static final String DATEBASE_NAME ="mDatebase";
	private static final String DATEBASE_TABLE = "recent_Reading";
	private static final int DATEBASE_VERSION = 1;
	
	//字段名
	public static final String KEY_TITLE = "title";
	public static final String KEY_LINK = "link";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_ABST1 = "abst1";
	public static final String KEY_IMG = "sourceImg";
	public static final String KEY_CREATED = "created";
	
	//操作对象
	private DBHelper mDBHelper;
	
	//数据库本身
	private SQLiteDatabase mDateBase;
	
	//建表语句
	private static final String DATEBASE_CREAT = "create table recent_reading ("
		+ "_id integer primary key autoincrement, "
		+ "title text, "
		+ "link text, "
		+ "abst1 text, "
		+ "sourceImg text, "
		+ "created text);";
	
	//上下文对象
	private Context mCtx;
	
	private static class DBHelper extends SQLiteOpenHelper{

		public DBHelper(Context context) {
			super(context, DATEBASE_NAME, null, DATEBASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DATEBASE_CREAT);
			System.out.println("DBHelper@onCreate: execSQL DATEBASE_CREAT");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS recent_Reading");
			onCreate(db);
		}
	}
	
	//构造函数
	public DBAdapter(Context c){
		this.mCtx = c;
	}
	
	public DBAdapter open(){
		
		mDBHelper = new DBHelper(mCtx);
		
		mDateBase = mDBHelper.getWritableDatabase();
		
		return this;
	}
	//关闭数据库操作类
	public void close(){
		mDBHelper.close();
	}
	
	public boolean InsertRecord(WenkuInfo info) throws Exception{
		
		ContentValues initialValue = new ContentValues();
		
		initialValue.put(KEY_TITLE, info.getTitle());
		initialValue.put(KEY_LINK, info.getLink());
		initialValue.put(KEY_ABST1, info.getAbst1());
		initialValue.put(KEY_IMG, info.getSourceImg());
		Calendar calender = Calendar.getInstance();
		
		String created = calender.get(Calendar.YEAR)+"年" +
						calender.get(Calendar.MONTH)+"月" +
						calender.get(Calendar.DAY_OF_MONTH)+"日" +
						calender.get(Calendar.HOUR_OF_DAY)+"时" +
						calender.get(Calendar.MINUTE)+"分";
		initialValue.put(KEY_CREATED, created);
		
		return mDateBase.insert(DATEBASE_TABLE, null, initialValue) > 0;
	}
	
	//更新
	public boolean UpdateRecord(WenkuInfo info) throws Exception{
		
		ContentValues record = new ContentValues();
		
		record.put(KEY_TITLE, info.getTitle());
		record.put(KEY_LINK, info.getLink());
		record.put(KEY_ABST1, info.getAbst1());
		record.put(KEY_IMG, info.getSourceImg());
		Calendar calender = Calendar.getInstance();
		
		String created = calender.get(Calendar.YEAR)+"年" +
						calender.get(Calendar.MONTH)+"月" +
						calender.get(Calendar.DAY_OF_MONTH)+"日" +
						calender.get(Calendar.HOUR_OF_DAY)+"时" +
						calender.get(Calendar.MINUTE)+"分";
		record.put(KEY_CREATED, created);
		
		return mDateBase.update(DATEBASE_TABLE, record, "title = ?", new String[]{info.getTitle()}) > 0 ;
		
		//return true;
		
	}
	
	public boolean InsertOrUpdateRecord(WenkuInfo info) throws Exception{
		
		if(exitsRecord(info)){
			//该info已存在，更新
			return UpdateRecord(info);
		}else{
			//该info不存在，插入该条记录
			return InsertRecord(info);
		}
		
	}
	
	
	public boolean InsertRecordList(List<WenkuInfo> infoList) throws Exception{
		
		for(WenkuInfo info :infoList){
			if(!InsertRecord(info)){
				return false;
			}
		}
		
		return true;
	}
	
	public boolean DeleteRecord(long rowID){
		return mDateBase.delete(DATEBASE_TABLE, KEY_ROWID +"="+rowID, null) > 0;
		
	}
	
	//获得所有数据，返回Cursor
	public Cursor getAllRecord(){
		
		return mDateBase.query(DATEBASE_TABLE, new String[]{KEY_ROWID,KEY_TITLE,KEY_LINK,KEY_IMG,KEY_CREATED },
				null, null, null, null, null);
	}
	
	//获得一条数据，返回Cursor
	public Cursor getRecord(WenkuInfo info){
		
		Cursor mCursor = mDateBase.query(DATEBASE_TABLE, new String[]{KEY_ROWID,KEY_TITLE,KEY_LINK,KEY_IMG,KEY_CREATED  },
				KEY_TITLE + "=" + info.getTitle(), null, null, null, null);
		
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	/**
	 * 查看数据库中是否存在该info，存在返回true，不存在返回false;
	 * @param info
	 * @return
	 */
	public boolean exitsRecord(WenkuInfo info){
		
		System.out.println("DBAdapter@exitsRecord:");
		Cursor mCursor = mDateBase.query(DATEBASE_TABLE, new String[]{KEY_ROWID,KEY_TITLE,KEY_LINK,KEY_IMG,KEY_CREATED  },
				"title = ?", new String[]{info.getTitle()}, null, null, null);
		
		if(mCursor != null){
			System.out.println("DBAdapter@exitsRecord:  exits");
			return true;
		}else{
			System.out.println("DBAdapter@exitsRecord:  not  exits");
			return false;
		}
		
	}
	
	
}
