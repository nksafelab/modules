package jinke.readings.datebase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CDateBase{
	/**
	 * 创建该Context对应的数据库，名称为DBname
	 * @param context
	 * @param DBname
	 * @return
	 */
	public static SQLiteDatabase openOrCreateDB(Context context,String DBname){
		return context.openOrCreateDatabase(DBname, Context.MODE_PRIVATE, null);
	}
	
	/**
	 * 获得该Context中名称为DBname的只读数据库
	 * @param context
	 * @param DBname
	 * @return
	 */
	public static SQLiteDatabase getReaderDB(Context context,String DBname){
		String path = context.getDatabasePath(DBname).getPath();
		return SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
	}
	
	/**
	 * 对db数据库执行sqlStr语句，sql语句中参数列表为args[]
	 * @param db
	 * @param sqlStr
	 * @param args
	 * @return
	 */
	public static void execSql(SQLiteDatabase db,String sqlStr,String[] args){
		db.execSQL(sqlStr, args);
	}
	
	/**
	 * 对数据库db执行sql语句，该sql语句不建议是返回数据的类型，如select请使用select方法
	 * @param db
	 * @param sqlStr
	 */
	public static void execSql(SQLiteDatabase db,String sqlStr){
		db.execSQL(sqlStr);
	}
	
	/**
	 * 对数据库db运行sql语句，返回Cursor类型的结果集
	 * @param db
	 * @param sqlStr
	 * @param args
	 * @return
	 */
	public static Cursor select(SQLiteDatabase db,String sqlStr,String[] args){
		return db.rawQueryWithFactory(null, sqlStr, args, null);
	}
	
	/**
	 * 关闭数据库db
	 * @param db
	 */
	public static void closeDB(SQLiteDatabase db){
		db.close();
	}
	
	
	
	
	

}
