package com.lmd.pmas.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	public static final String DB_NAME = "pmas.db";
	public static final int DB_VERSION = 1;
	
	Context context;
	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		// 联系人表
		db.execSQL("CREATE TABLE IF NOT EXISTS " +
				"pmas_contact_contact (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"gr_id INTEGER, " +
				"name TEXT, " +
				"index_name TEXT, " +
				"birthday INTEGER, " +
				"phone TEXT, " +
				"email TEXT, " +
				"address TEXT" +
				")");

		// 联系人群组表
		db.execSQL("CREATE TABLE IF NOT EXISTS " +
				"pmas_contact_group (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"name TEXT, " +
				"description TEXT " +
				")");
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		//context.deleteDatabase(DB_NAME);
	}}
