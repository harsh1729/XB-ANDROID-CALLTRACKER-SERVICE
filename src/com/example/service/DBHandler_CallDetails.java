package com.example.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHandler_CallDetails extends SQLiteOpenHelper{
	
	//CREATE TABLE CallDetails (CallId integer NOT NULL PRIMARY KEY UNIQUE,
	//PhoneNo text,Name text,"TimeStamp" text,
	//DurationInSec integer,CallTypeId integer,LocationId integer)
	
	private final String TABLE_CALLDETAILS = "CallDetails";
	
	private final String KEY_CALLDETAILS_ID = "CallId";
	private final String KEY_CALLDETAILS_PHONE_NO= "PhoneNo";
	private final String KEY_CALLDETAILS_NAME = "Name";
	private final String KEY_CALLDETAILS_TIMESTAMP = "TimeStamp";
	private final String KEY_CALLDETAILS_DURATION_SEC = "DurationInSec";
	private final String KEY_CALLDETAILS_CALL_TYPE_ID = "CallTypeId";
	private final String KEY_CALLDETAILS_LOCATION_ID = "LocationId";
	
	private final int MAX_CALL_RECORDS_TO_SERVER = 3;
	Context context;

	public DBHandler_CallDetails(Context context) {
		super(context, DBHandler_Main.DB_NAME, null, DBHandler_Main.DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	

	public void insertCallDetails(ArrayList<Object_CallDetails> list) {

		SQLiteDatabase db = this.getWritableDatabase();
		
		long finalId = 0;
		ContentValues values = new ContentValues();
		for (Object_CallDetails ob : list) {
			
			try{
			values.put(KEY_CALLDETAILS_ID, ob.getId());
			values.put(KEY_CALLDETAILS_CALL_TYPE_ID, ob.getCallTypeId());
			values.put(KEY_CALLDETAILS_DURATION_SEC, ob.getDurationInSec());
			values.put(KEY_CALLDETAILS_LOCATION_ID,ob.getLocationId() );
			values.put(KEY_CALLDETAILS_NAME, ob.getName());
			values.put(KEY_CALLDETAILS_PHONE_NO, ob.getPhoneNo());
			values.put(KEY_CALLDETAILS_TIMESTAMP, ob.getTimeStamp());
			
			db.insert(TABLE_CALLDETAILS, null, values);
			finalId = ob.getId();
			
			}catch(Exception ex){
				
			}
		}
		
		if(finalId !=0){
			Globals.setLastCallId(finalId, context);
		}
		db.close();
	}
	
	/*
	public ArrayList<ArrayList<Object_CallDetails >>  getCallDetails(){
		String selectQuery = "SELECT TC.* , TL."+DBHandler_Location.KEY_LOCATION_LAT+" , TL."+
		DBHandler_Location.KEY_LOCATION_LONG+" FROM " + TABLE_CALLDETAILS+" TC INNER JOIN "+
		DBHandler_Location.TABLE_LOCATION+" TL ON TL."+DBHandler_Location.KEY_LOCATION_ID +" = TC."+
		KEY_CALLDETAILS_LOCATION_ID +" ORDER BY "+ KEY_CALLDETAILS_LOCATION_ID +" ASC ";//LIMIT 5";

		SQLiteDatabase db = this.getReadableDatabase();

		ArrayList<ArrayList<Object_CallDetails >> superList = new ArrayList<ArrayList<Object_CallDetails >>();

		ArrayList<Object_CallDetails > list = null;
		Cursor cur = db.rawQuery(selectQuery, null);
		int counter = 1;
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					if(counter == 1){
						list = new ArrayList<Object_CallDetails>();
					}
					Object_CallDetails obj = new Object_CallDetails();

					obj.setId(cur.getLong(cur.getColumnIndex(KEY_CALLDETAILS_ID)));
					obj.setCallTypeId(cur.getInt(cur
							.getColumnIndex(KEY_CALLDETAILS_CALL_TYPE_ID)));
					obj.setDurationInSec(cur.getLong(cur
							.getColumnIndex(KEY_CALLDETAILS_DURATION_SEC)));
					obj.setLocationId(cur.getLong(cur
							.getColumnIndex(KEY_CALLDETAILS_LOCATION_ID)));
					obj.setName(cur.getString(cur
							.getColumnIndex(KEY_CALLDETAILS_NAME)));
					obj.setPhoneNo(cur.getString(cur
							.getColumnIndex(KEY_CALLDETAILS_PHONE_NO)));
					obj.setTimeStamp(cur.getString(cur
							.getColumnIndex(KEY_CALLDETAILS_TIMESTAMP)));
					obj.setLatitude(cur.getDouble(cur
							.getColumnIndex(DBHandler_Location.KEY_LOCATION_LAT)));
					obj.setLongitude(cur.getDouble(cur
							.getColumnIndex(DBHandler_Location.KEY_LOCATION_LONG)));
					
					list.add(obj);
					
					if(counter >= MAX_CALL_RECORDS_TO_SERVER){
						superList.add(list);
						counter = 1;
					}else{
						counter ++;
					}
					
					
				}while(cur.moveToNext());
				
				if(counter < MAX_CALL_RECORDS_TO_SERVER){
					superList.add(list);
				}
			}
		}
			
		return superList;
	}
	*/
	
	
	public ArrayList<Object_CallDetails >  getCallDetails(){
		String selectQuery = "SELECT TC.* , TL."+DBHandler_Location.KEY_LOCATION_LAT+" , TL."+
		DBHandler_Location.KEY_LOCATION_LONG+" FROM " + TABLE_CALLDETAILS+" TC INNER JOIN "+
		DBHandler_Location.TABLE_LOCATION+" TL ON TL."+DBHandler_Location.KEY_LOCATION_ID +" = TC."+
		KEY_CALLDETAILS_LOCATION_ID +" ORDER BY "+ KEY_CALLDETAILS_LOCATION_ID +" ASC LIMIT "+ MAX_CALL_RECORDS_TO_SERVER;

		SQLiteDatabase db = this.getReadableDatabase();

		ArrayList<Object_CallDetails > list = new ArrayList<Object_CallDetails >();
		
		Cursor cur = db.rawQuery(selectQuery, null);
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					
					Object_CallDetails obj = new Object_CallDetails();

					obj.setId(cur.getLong(cur.getColumnIndex(KEY_CALLDETAILS_ID)));
					obj.setCallTypeId(cur.getInt(cur
							.getColumnIndex(KEY_CALLDETAILS_CALL_TYPE_ID)));
					obj.setDurationInSec(cur.getLong(cur
							.getColumnIndex(KEY_CALLDETAILS_DURATION_SEC)));
					obj.setLocationId(cur.getLong(cur
							.getColumnIndex(KEY_CALLDETAILS_LOCATION_ID)));
					obj.setName(cur.getString(cur
							.getColumnIndex(KEY_CALLDETAILS_NAME)));
					obj.setPhoneNo(cur.getString(cur
							.getColumnIndex(KEY_CALLDETAILS_PHONE_NO)));
					obj.setTimeStamp(cur.getString(cur
							.getColumnIndex(KEY_CALLDETAILS_TIMESTAMP)));
					obj.setLatitude(cur.getDouble(cur
							.getColumnIndex(DBHandler_Location.KEY_LOCATION_LAT)));
					obj.setLongitude(cur.getDouble(cur
							.getColumnIndex(DBHandler_Location.KEY_LOCATION_LONG)));
					
					list.add(obj);

				}while(cur.moveToNext());
				
				
			}
		}
			
		return list;
	}
	
	
	public void  deleteCallDetails(JSONArray jsonArrayIds){
		String deleteQueryFormat = "DELETE FROM " + TABLE_CALLDETAILS +" WHERE "+ KEY_CALLDETAILS_ID + " = ";

		SQLiteDatabase db = this.getWritableDatabase();
		
		if (jsonArrayIds != null) { 
			   int len = jsonArrayIds.length();
			   for (int i=0;i<len;i++){ 
				   
					try {
						String deleteQuery = deleteQueryFormat+ jsonArrayIds.getLong(i);
						db.execSQL(deleteQuery);
					} catch (Exception e) {
						Log.i("HARSH1", "Exception in deleting CAll DETAILS");
					}
					
			   } 
			} 
		
		//DELETE LOCATION DATA 
		try {
			String whereClause = DBHandler_Location.KEY_LOCATION_ISCALL_LOCATION +" = 1 AND "+
					DBHandler_Location.KEY_LOCATION_ID +" NOT IN (SELECT "+KEY_CALLDETAILS_LOCATION_ID + " FROM "+
					TABLE_CALLDETAILS+" );";
			db.delete(DBHandler_Location.TABLE_LOCATION, whereClause, null);
		}catch (Exception e) {
			Log.i("HARSH1", "Exception in deleting CAll DETAILS Location");
		}
		db.close();
	}
}
