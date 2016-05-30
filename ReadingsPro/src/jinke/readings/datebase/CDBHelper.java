package jinke.readings.datebase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CDBHelper extends SQLiteOpenHelper{

	private static final String DATEBASE_NAME = "readingDB";
	private static final int DATEBASE_VERSION = 1;
	private static final String DATEBASE_CREATED = "created";
	private static final String DATEBASE_TABLE =  "create table recent_reading ("
		+ "_id integer primary key autoincrement, "
		+ "title text, "
		+ "link text, "
		+ "abst1 text, "
		+ "sourceImg text, "
		+ "created text,"
		+ "thumb blob,"
		+ "source integer);";
	
	
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
		+ "key text,"
		+ "title text, "
		+ "link text, "
		+ "abst1 text, "
		+ "sourceImg text, "
		+ "created text,"
		+ "thumb blob,"
		+ "source integer,"
		+ "abst3 text);";
	
	
	public CDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	public CDBHelper(Context context, String name, CursorFactory factory) {
		super(context, name, factory, DATEBASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	public CDBHelper(Context context, String name) {
		super(context, name, null, DATEBASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	public CDBHelper(Context context) {
		super(context, DATEBASE_NAME, null, DATEBASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		//db.execSQL(DATEBASE_TABLE);
		db.execSQL(DATEBASE_TABLE_KEY);
		db.execSQL(DATEBASE_TABLE_HOT);
		db.execSQL(DATEBASE_TABLE_REC);
		db.execSQL(DATEBASE_TABLE_SEARCH_HIS);
		System.out.println("CDBHelper@onCreater");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
		db.execSQL("DROP TABLE IF EXISTS recent_reading");

		onCreate(db);
		
	}

}
