package com.zyw.nwpu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "database.db";// 数据库名称
	public static final int VERSION = 1;
	public static final String TABLE_CHANNEL = "channel";// 数据表
	public static final String TABLE_NEWS = "news";// 新闻数据表

	public static final String ID = "id";//
	public static final String NAME = "name";
	public static final String ORDERID = "orderId";
	public static final String SELECTED = "selected";

	// 表项
	public static final String NEWSID = "newsid";
	public static final String CATID = "catid";
	public static final String TITLE = "title";
	public static final String ABSTRACT = "abstract";
	public static final String PICURL = "picurl";
	public static final String SOURCEURL = "sourceurl";
	public static final String LIKE_NUM = "nlike";
	public static final String COMMENT_NUM = "ncomment";
	public static final String PUBDATE = "pubdate";
	public static final String READSTATUS = "readstatus";

	private Context context;

	public SQLHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO 创建数据库后，对数据库的操作
		String sql = "create table if not exists " + TABLE_CHANNEL
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " + ID
				+ " INTEGER , " + NAME + " TEXT , " + ORDERID + " INTEGER , "
				+ SELECTED + " SELECTED)";
		String sql2 = "create table if not exists " + TABLE_NEWS
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " + ID
				+ " INTEGER , " + NEWSID + " TEXT , " + CATID + " TEXT , "
				+ TITLE + " TEXT , " + ABSTRACT + " TEXT , " + PICURL
				+ " TEXT , " + SOURCEURL + " TEXT , " + COMMENT_NUM
				+ " INTEGER , " + LIKE_NUM + " INTEGER , " + PUBDATE
				+ " TEXT , " + READSTATUS + " SELECTED)";
		db.execSQL(sql);
		db.execSQL(sql2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 更改数据库版本的操作
		onCreate(db);
	}

}
