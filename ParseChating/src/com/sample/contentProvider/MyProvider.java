package com.sample.contentProvider;

import java.sql.SQLException;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MyProvider extends ContentProvider {
	static final String PROVIDER_NAME="com.sample.contentProvider.MyProvider";
	static final String path="students";
	static final String id="sid";
	static final String name="sname";
	static final String age="sage";
	static String url="content://"+PROVIDER_NAME+"/"+path;
	static Uri myuri=Uri.parse(url);
	static final HashMap<String, String> map=new HashMap<>();
	static UriMatcher matcher;
	static final int uriCode=1;
	static SQLiteDatabase db;
	static{
	matcher=new UriMatcher(UriMatcher.NO_MATCH);
	matcher.addURI(PROVIDER_NAME, path,uriCode);
	matcher.addURI(PROVIDER_NAME, path+"/*", uriCode);
	}
	
	
	

	@Override
	public boolean onCreate() {
		MyDbHelper helper=new MyDbHelper(getContext());
		db=helper.getWritableDatabase();
		if(db!=null){
			return true;
		}else 
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder builder=new SQLiteQueryBuilder();
		builder.setTables(MyDbHelper.TABLE_NAME);
		switch (matcher.match(uri)) {
		case uriCode:
			builder.setProjectionMap(map);
			break;

		default:
			throw new IllegalArgumentException("Reading Problem");
		}
		if(sortOrder==null || sortOrder==""){
			sortOrder=name;
		}
		Cursor c=builder.query(db, projection, selection, selectionArgs, null, null, name);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (matcher.match(uri)) {
		case uriCode:
			return "vnd.android.cursor.dir/"+path;

		default:
			throw new IllegalArgumentException("Argument not Found");
		}
		
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		try{
			long rowId=db.insert(MyDbHelper.TABLE_NAME, "", values);
			if(rowId>0){
				switch (matcher.match(uri)) {
				case uriCode:
					Uri myuri=ContentUris.withAppendedId(uri, rowId);
					getContext().getContentResolver().notifyChange(myuri, null);
					return myuri;

				default:
					throw new SQLException("Insertion failed :"+uri);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return uri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count=0;
		
		switch (matcher.match(uri)) {
		case uriCode:
			count=db.delete(MyDbHelper.TABLE_NAME, selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Argument Exp:"+uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count =0;
		switch (matcher.match(uri)) {
		case uriCode:
			count=db.update(MyDbHelper.TABLE_NAME, values, selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Argument Exp:"+uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	static class MyDbHelper extends SQLiteOpenHelper{
		static final String DB_NAME="studentDb";
		static final String TABLE_NAME="student";
		static final int DB_VERSION=1;
		
		public MyDbHelper(Context cxt) {
			super(cxt, DB_NAME, null,DB_VERSION);
		}

		
		

		@Override
		public void onCreate(SQLiteDatabase db) {
		String mytable="create table "+TABLE_NAME+"(sid varchar2(20),sname varchar2(20),sage int)";
		db.execSQL(mytable);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table  if exists "+TABLE_NAME);
			onCreate(db);
			
		}
		
	}

}
