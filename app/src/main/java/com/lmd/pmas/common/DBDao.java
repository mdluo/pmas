package com.lmd.pmas.common;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 把公用的CURD方法抽象成一个DBDao类
 * @author LMD
 * @param <T> 模型模板
 */
public abstract class DBDao<T> {
	
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	
	private String tableName;
	private String className;
	
	/**
	 * 构造方法
	 * @param context 上下文环境
	 * @param tableName 数据表名
	 */
	public DBDao(Context context, String tableName, String className){
		dbHelper = new DBHelper(context);
		db = dbHelper.getReadableDatabase();
		this.tableName = tableName;
		this.className = className;
	}
	
	/**
	 * 默认插入方法
	 * @param model 要插入的模型对象
	 * @return 插入的ID
	 */
	public int insert(T model) {
		ContentValues dataCV = CommonUtils.transBean2CV(model, CommonUtils.WITHOUT_ID);
		return (int) db.insert(tableName, null, dataCV);
	}
	
	/**
	 * 默认删除方法
	 * @param whereClause 含有?的条件语句
	 * @param whereArgs 替换?的条件字符串数组
	 * @return 删除的条目数
	 */
	public int delete(String whereClause, String[] whereArgs) {
		return db.delete(tableName, whereClause, whereArgs);
	}
	
	/**
	 * 默认清空方法
	 * @return
	 */
	public int clear() {
		delete(null, null);
		ContentValues tempCV = new ContentValues();
		tempCV.put("seq", 0);
		return db.update("sqlite_sequence", tempCV, "name = ?", new String[]{tableName});
	}
	
	/**
	 * 默认更新方法
	 * @param model 要更新的模型对象
	 * @param whereClause 含有?的条件语句
	 * @param whereArgs 替换?的条件字符串数组
	 * @return 更新的条目数
	 */
	public int update(T model, String whereClause, String[] whereArgs) {
		ContentValues dataCV = CommonUtils.transBean2CV(model, CommonUtils.WITH_ID);
		return db.update(tableName, dataCV, whereClause, whereArgs);
	}
	
	/**
	 * 默认查找方法
	 * @param selection 含有?的条件语句
	 * @param selectionArgs 替换?的条件字符串数组
	 * @return 查找到的模型对象数组
	 */
	@SuppressWarnings({ "unchecked" })
	public ArrayList<T> query(String selection, String[] selectionArgs) {
		ArrayList<T> arrayList = new ArrayList<T>();
		try {
			Cursor cursor = db.query(tableName, null, selection, selectionArgs, null, null, null);
			while(cursor != null && cursor.moveToNext()){
				T model = (T) Class.forName(className).newInstance();
				CommonUtils.transCursor2Bean(cursor, model);
				arrayList.add(model);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrayList;
	}
}