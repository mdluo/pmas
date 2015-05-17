package com.lmd.pmas.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

/**
 * 公共的函数库
 * @author LMD
 */
public class CommonUtils {

	public final static int WITH_ID = 0;
	public final static int WITHOUT_ID = 1;

	public static final String ALL_CHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String LETTER_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String NUMBER = "0123456789";
	
	/**
	 * 时间戳函数
	 * @return 返回时间戳
	 */
	static public int time() {
		return (int) (System.currentTimeMillis() / 1000);
	}
	
	@SuppressLint("DefaultLocale")
	static public String getSortKey(String sortKeyString) {

		String ch = sortKeyString.substring(0, 1);
		ch = ch.toUpperCase();
		if (!ch.matches("[A-Z]")) {
			ch = "#";
		}
		return ch;
	}

	/**
	 * 生产随机字符串
	 * @param length 返回随机数的长度
	 * @return
	 */
	public static String generateString(int length)
	{
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(LETTER_CHAR.charAt(random.nextInt(LETTER_CHAR.length())));
		}
		return sb.toString();
	}

	/**
	 * 将模型对象转化成ContentValues
	 * @param model模型对象
	 * @return 转化成的ContentValues
	 */
	public static ContentValues transBean2CV(Object model, int withIDorNot) {

		ContentValues cv = new ContentValues();

		Field[] fields = model.getClass().getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {

			String key = fields[i].getName();

			if ("_id".equals(key) && withIDorNot == WITHOUT_ID) {
				continue;
			}

			char toUpperCase = (char) (key.charAt(0) - 32);
			String keyUpper = key.replace(key.charAt(0), toUpperCase);

			try {
				Method getMethod = model.getClass().getDeclaredMethod(
						"get" + keyUpper);
				Object value = getMethod.invoke(model);
				cv.put(key, value.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cv;
	}

	/**
	 * 将数据库查询得到的Cursor对象转化成模型对象
	 * @param cursor Cursor对象
	 * @param model 模型对象(形参，需预先初始化)
	 */
	public static <T> void transCursor2Bean(Cursor cursor, T model) {

		Class<?> classType = model.getClass();
		Field[] fields = classType.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {

			String key = fields[i].getName();
			char initial = key.charAt(0);
			String keyUpper;
			if (initial != '_') {
				char toUpperCase = (char) (initial - 32);
				keyUpper = key.replace(initial, toUpperCase);
			}
			else {
				keyUpper = key;
			}

			try {
				Method getMethod = classType.getMethod("get" + keyUpper);
				Method setMethod = classType.getMethod("set" + keyUpper,
						new Class[] { getMethod.getReturnType() });

				Class<? extends Object> fieldType = setMethod
						.getParameterTypes()[0];
				Object value = formatValue(
						cursor.getString(cursor.getColumnIndex(key)), fieldType);

				setMethod.invoke(model, new Object[] { value });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将Object类型的参数1转化成参数2对象的对象
	 * @param fieldValue 属性值
	 * @param fieldType 属性类型
	 * @return formatValue 格式化数据
	 */
	private static Object formatValue(Object fieldValue,
			Class<? extends Object> fieldType) {
		if (fieldValue == null) {
			return null;
		}

		Object value = null;
		String fieldStr = fieldValue.toString();

		if (fieldType == Integer.class || "int".equals(fieldType.getName())) {
			if (fieldValue != null) {
				value = Integer.parseInt(fieldStr);
			}
		} else if (fieldType == Float.class
				|| "float".equals(fieldType.getName())) {
			if (fieldValue != null) {
				value = Float.parseFloat(fieldStr);
			}
		} else if (fieldType == Double.class
				|| "double".equals(fieldType.getName())) {
			if (fieldValue != null) {
				value = Double.parseDouble(fieldStr);
			}
		} else {
			value = fieldValue;
		}
		return value;
	}
}
