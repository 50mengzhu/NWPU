package com.zyw.nwpu.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zyw.nwpulib.model.NewsEntity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NewsDBO {
	private SQLHelper helper = null;

	public NewsDBO(Context context) {
		helper = new SQLHelper(context);
	}

	/**
	 * 保存一个栏目到栏目表
	 */
	public boolean addCache(NewsEntity item) {
		boolean flag = false;
		SQLiteDatabase database = null;

		// 从数据库查询，查看该新闻条目是否已经保存
		if (isExistInCache(item.getSource_url())) {
			// 数据库已存在
			return false;
		} else {
			long id = -1;
			try {
				database = helper.getWritableDatabase();// 获取数据库对象
				ContentValues values = new ContentValues();// 数据条目
				values.put(SQLHelper.NEWSID, item.getNewsId());
				values.put(SQLHelper.CATID, item.getCatId());
				values.put(SQLHelper.TITLE, item.getTitle());
				values.put(SQLHelper.ABSTRACT, item.getNewsAbstract());
				values.put(SQLHelper.PICURL, item.getPicUrl());
				values.put(SQLHelper.SOURCEURL, item.getSource_url());
				values.put(SQLHelper.COMMENT_NUM, item.getCommentNum());
				values.put(SQLHelper.LIKE_NUM, item.getLikeNum());
				values.put(SQLHelper.PUBDATE, item.getPublishTime());
				values.put(SQLHelper.READSTATUS, item.getReadStatus());

				id = database.insert(SQLHelper.TABLE_NEWS, null, values);// 插入数据
				flag = (id != -1 ? true : false);
			} catch (Exception e) {
			} finally {
				// 关闭数据库
				if (database != null) {
					database.close();
				}
			}
			return flag;
		}
	}

	public boolean isExistInCache(String id) {
		SQLiteDatabase database = null;
		Cursor cursor = null;
		int num = 0;
		try {
			database = helper.getReadableDatabase();
			cursor = database.rawQuery("select * from " + SQLHelper.TABLE_NEWS
					+ " where " + SQLHelper.SOURCEURL + " = ?",
					new String[] { id });
			cursor.moveToNext();
			num = cursor.getCount();
		} catch (Exception e) {
			Log.d("", e.getLocalizedMessage());
		} finally {
			if (database != null) {
				database.close();
			}
		}
		boolean flag = false;
		if (num == 0) {
			flag = false;
		} else {
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除栏目表中的一项
	 */
	public boolean deleteCache(String whereClause, String[] whereArgs) {
		// boolean flag = false;
		// SQLiteDatabase database = null;
		// int count = 0;
		// try {
		// database = helper.getWritableDatabase();
		// count = database.delete(SQLHelper.TABLE_CHANNEL, whereClause,
		// whereArgs);
		// flag = (count > 0 ? true : false);
		// } catch (Exception e) {
		// // TODO: handle exception
		// } finally {
		// if (database != null) {
		// database.close();
		// }
		// }
		// return flag;
		return true;
	}

	public Map<String, String> viewCache(String selection,
			String[] selectionArgs) {
		SQLiteDatabase database = null;
		Cursor cursor = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			database = helper.getReadableDatabase();
			cursor = database.query(true, SQLHelper.TABLE_CHANNEL, null,
					selection, selectionArgs, null, null, null, null);
			int cols_len = cursor.getColumnCount();
			while (cursor.moveToNext()) {
				for (int i = 0; i < cols_len; i++) {
					String cols_name = cursor.getColumnName(i);
					String cols_values = cursor.getString(cursor
							.getColumnIndex(cols_name));
					if (cols_values == null) {
						cols_values = "";
					}
					map.put(cols_name, cols_values);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return map;
	}

	public List<Map<String, String>> listCache(String selection,
			String[] selectionArgs) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		SQLiteDatabase database = null;
		Cursor cursor = null;
		try {
			database = helper.getReadableDatabase();
			cursor = database.query(false, SQLHelper.TABLE_NEWS, null,
					selection, selectionArgs, null, null, null, null);
			int cols_len = cursor.getColumnCount();
			while (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				// 遍历各列 读取列名和列植
				for (int i = 0; i < cols_len; i++) {

					String cols_name = cursor.getColumnName(i);
					String cols_values = cursor.getString(cursor
							.getColumnIndex(cols_name));
					if (cols_values == null) {
						cols_values = "";
					}
					map.put(cols_name, cols_values);
				}
				list.add(map);
			}

		} catch (Exception e) {
			String t = e.getMessage();
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return list;
	}

	public void clearFeedTable() {
		String sql = "DELETE FROM " + SQLHelper.TABLE_NEWS + ";";
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(sql);
		revertSeq();
	}

	private void revertSeq() {
		String sql = "update sqlite_sequence set seq=0 where name='"
				+ SQLHelper.TABLE_NEWS + "'";
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(sql);
	}

}
