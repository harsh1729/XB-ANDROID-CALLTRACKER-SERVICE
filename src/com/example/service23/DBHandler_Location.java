package com.example.service23;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHandler_Location extends SQLiteOpenHelper{

	//CREATE TABLE LocationDetails (Id integer 
	//NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	//Latitude double,Longitude double,"TimeStamp" text), IsCallLocation integer DEFAULT 0)
	
	public static final String TABLE_LOCATION = "LocationDetails";
	
	public static final String KEY_LOCATION_ID = "Id";
	public static final String KEY_LOCATION_LAT= "Latitude";
	public static final String KEY_LOCATION_LONG = "Longitude";
	private final String KEY_LOCATION_TIMESTAMP = "TimeStamp";
	public static final String KEY_LOCATION_ISCALL_LOCATION = "IsCallLocation";
	
	Context context;

	public DBHandler_Location(Context context) {
		super(context, DBHandler_Main.DB_NAME, null, DBHandler_Main.DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	public long insertLocation(Object_Location ob) {

		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		long locId = 0;
			
			try{
			//values.put(KEY_LOCATION_ID, ob.getId());
			values.put(KEY_LOCATION_LAT, ob.getLatitude());
			values.put(KEY_LOCATION_LONG, ob.getLongitude());
			values.put(KEY_LOCATION_TIMESTAMP,ob.getTimeStamp() );
			values.put(KEY_LOCATION_ISCALL_LOCATION, ob.getIsCallLocation());
			
			locId = db.insert(TABLE_LOCATION, null, values);
			}catch(Exception ex){
				
			}
		
		db.close();
		
		return locId;
	}
	
	public ArrayList<Object_Location>  getExceptCallLocation(){
		
		String selectQuery = "SELECT * FROM " + TABLE_LOCATION + " WHERE "+ KEY_LOCATION_ISCALL_LOCATION + " = 0";

		selectQuery+= " ORDER BY "+ KEY_LOCATION_ID +" ASC LIMIT 20";
		SQLiteDatabase db = this.getReadableDatabase();

		ArrayList<Object_Location> list = new ArrayList<Object_Location>();

		Cursor cur = db.rawQuery(selectQuery, null);
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					
					Object_Location obj = new Object_Location();

					obj.setId(cur.getLong(cur.getColumnIndex(KEY_LOCATION_ID)));
					obj.setLatitude((cur.getDouble(cur
							.getColumnIndex(KEY_LOCATION_LAT))));
					obj.setLongitude((cur.getDouble(cur
							.getColumnIndex(KEY_LOCATION_LONG))));
					obj.setTimeStamp(cur.getString(cur.getColumnIndex(KEY_LOCATION_TIMESTAMP)));
					obj.setIsCallLocation(cur.getInt(cur.getColumnIndex(KEY_LOCATION_ISCALL_LOCATION)));
					
					list.add(obj);
				}while(cur.moveToNext());
			}
		}
			
		return list;
	}
	
	public Object_Location  getCallLocation(int locationId){
		
		String selectQuery = "SELECT * FROM " + TABLE_LOCATION + " WHERE "+ KEY_LOCATION_ID + " = "+locationId;

		SQLiteDatabase db = this.getReadableDatabase();

		Object_Location obj = null;

		Cursor cur = db.rawQuery(selectQuery, null);
		if (cur != null) {
			if (cur.moveToFirst()) {
				
					obj = new Object_Location();

					obj.setId(cur.getLong(cur.getColumnIndex(KEY_LOCATION_ID)));
					obj.setLatitude((cur.getDouble(cur
							.getColumnIndex(KEY_LOCATION_LAT))));
					obj.setLongitude((cur.getDouble(cur
							.getColumnIndex(KEY_LOCATION_LONG))));
					obj.setTimeStamp(cur.getString(cur.getColumnIndex(KEY_LOCATION_TIMESTAMP)));
					obj.setIsCallLocation(cur.getInt(cur.getColumnIndex(KEY_LOCATION_ISCALL_LOCATION)));
					
			}
		}
			
		return obj;
	}
	
	
	public void  deleteLocations(JSONArray jsonArrayIds){
		String deleteQueryFormat = "DELETE FROM " + TABLE_LOCATION +" WHERE "+ KEY_LOCATION_ID + " = ";

		SQLiteDatabase db = this.getWritableDatabase();
		
		if (jsonArrayIds != null) { 
			   int len = jsonArrayIds.length();
			   for (int i=0;i<len;i++){ 
				   
					try {
						String deleteQuery = deleteQueryFormat+ jsonArrayIds.getLong(i);
						db.execSQL(deleteQuery);
					} catch (JSONException e) {
						Log.i("HARSH1", "Exception in deleting Location");
					}
					
			   } 
			} 
		
		db.close();
	}
}
