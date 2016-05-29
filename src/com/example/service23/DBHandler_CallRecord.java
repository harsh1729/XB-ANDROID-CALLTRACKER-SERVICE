package com.example.service23;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHandler_CallRecord extends SQLiteOpenHelper{
	
	//CREATE TABLE CallDetails (CallId integer NOT NULL PRIMARY KEY UNIQUE,
	//PhoneNo text,Name text,"TimeStamp" text,
	//DurationInSec integer,CallTypeId integer,LocationId integer)
	//************TABLE RECORD*******************
	private final String TABLE_CALLDETAILS = "RecordDetails";
	
	//private final String KEY_CALLDETAILS_ID = "Id";
	private final String KEY_CALLDETAILS_PHONE_NO= "PhoneNO";
	private final String KEY_CALLDETAILS_RECORDING = "RecordCall";
	private final String KEY_CALLDETAILS_RECORDING_DATE = "Daytime";
	private final String KEY_CALLDETAILS_ROWID = "RowId";
	//private final int MAX_CALL_RECORDS_TO_SERVER = 3;
//	CREATE TABLE "RecordDetails" ("Id" INTEGER PRIMARY KEY ,"PhoneNO" INTEGER,"RecordCall" BLOB,"Daytime" TEXT DEFAULT (null) , "RowId" INTEGER)
	//************TABLE CHECK PHONE***********************
    private final String TABLE_CALLRECORD_CHECK = "CheckRecording";
    private final String KEY_CALLRECORD_CHECK_PHONE_NO= "PhoneNo";
	Long rowinsert;
	Context context;
	//CREATE TABLE "CheckRecording" ("Id" INTEGER PRIMARY KEY  UNIQUE , "PhoneNo" INTEGER)
	//CREATE TABLE "RecordDetails" ("Id" INTEGER PRIMARY KEY  UNIQUE , "PhoneNO" INTEGER, "RecordCall" BLOB, "Date" TEXT DEFAULT null)
	public DBHandler_CallRecord(Context context) {
		super(context, DBHandler_Main.DB_NAME, null, DBHandler_Main.DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	

	public long insertCallRecord_Details(Object_CallDetails ob) {

		SQLiteDatabase db = this.getWritableDatabase();
		
		long finalId = 0;
		ContentValues values = new ContentValues();
		//for (Object_CallDetails ob : list) {
			
			try{
			values.put(KEY_CALLDETAILS_PHONE_NO, ob.getPhoneNo());
			values.put(KEY_CALLDETAILS_RECORDING, ob.getCallRecording());
			values.put(KEY_CALLDETAILS_RECORDING_DATE, ob.getCallRecording_Date());
			values.put(KEY_CALLDETAILS_ROWID, ob.getId());
			finalId =  db.insert(TABLE_CALLDETAILS, null, values);
			Log.i("SUSHIL","Insert id in databse"+finalId);
			}catch(Exception ex){
				ex.printStackTrace();
			//}
		}
		return finalId;
	}
	
/*public void insertPhoneNO(String Numbers) {
	Log.i("SUSHIL","Insert databse"+Numbers);
        SQLiteDatabase db = this.getWritableDatabase();
        
		ContentValues values = new ContentValues();
		if(Numbers!=null){
		//for(int i=0;i<Numbers.length();i++){
		try{
			long finalId = 0;
			values.put(KEY_CALLRECORD_CHECK_PHONE_NO,Numbers);
			finalId =  db.insert(TABLE_CALLRECORD_CHECK, null, values);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		  }
		 //}
		db.close();
      }*/
public void insertPhoneNO(String Numbers) {
    SQLiteDatabase db;
	db = this.getWritableDatabase();
    try {
		System.out.println("insert method is call");
  ContentValues valuePairs = new ContentValues();
		
	valuePairs.put(KEY_CALLRECORD_CHECK_PHONE_NO,Numbers);
	rowinsert = db.insert(TABLE_CALLRECORD_CHECK, null, valuePairs);
     System.out.println("inserted row : " + rowinsert);
    valuePairs.clear();
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	 //db.close();  
}

public void deletePhoneNO() {
	
	String deleteQueryFormat = "DELETE FROM " + TABLE_CALLRECORD_CHECK;
		//	" WHERE "+ KEY_CALLRECORD_CHECK_PHONE_NO + " = ";
SQLiteDatabase db = this.getWritableDatabase();
	 try {
					String deleteQuery = deleteQueryFormat;
					db.execSQL(deleteQuery);
				} catch (Exception e) {
					Log.i("SUSHIL", "Exception in deleting Location");
			   } 
	db.close();
}
public void deleteRecording(ArrayList<String>ArrayIds) {
	
	String deleteQueryFormat = "DELETE FROM " + TABLE_CALLDETAILS +" WHERE "+ KEY_CALLDETAILS_ROWID + " = ";

	SQLiteDatabase db = this.getWritableDatabase();
	
	if (ArrayIds != null) { 
		   int len = ArrayIds.size();
		   for (int i=0;i<len;i++){ 
			   
				try {
					String deleteQuery = deleteQueryFormat+ ArrayIds.get(i);
					db.execSQL(deleteQuery);
				} catch (Exception e) {
					Log.i("SUSHIL", "Exception in deleting CAll DETAILS");
				}
				
		   } 
		 Log.i("SUSHIL", "All deleting CAll REcording");
	}
}

public Boolean isValidNo(String PhoneNo){
	Log.i("SUSHIL", "phone come for check "+PhoneNo);
	//SELECT * FROM CheckRecording Where PhoneNo LIKE '%8875897798%'
	 Boolean exists = false;
	 String selectQuery = "SELECT * FROM "+ TABLE_CALLRECORD_CHECK +" WHERE "+KEY_CALLRECORD_CHECK_PHONE_NO+" LIKE '%"+PhoneNo+"%'";
	// "SELECT * FROM "+ TABLE_CALLRECORD_CHECK +" WHERE "+KEY_CALLRECORD_CHECK_PHONE_NO+" = '"+PhoneNo+"'";
	 Log.v("SUSHIL COMMENT DESC Query",selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();//myDataBase;//
		Cursor cursor = db.rawQuery(selectQuery, null);
      if(cursor != null)
		if (cursor.moveToFirst()) {
			exists = true;
		}
      Log.i("SUSHIL", "is valid return"+exists);
	 return exists;
}

	
	/*private ContentValues createContentValues(byte[] recoding ,ContentValues cv) {
	    //ContentValues cv = new ContentValues();
	    cv.put(KEY_CALLDETAILS_RECORDING, recoding);
	    return cv;
	}*/
	
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
	
	
/*	public byte[] getCallDetails(String phoneNo,String daytime){
		String selectQuery = "SELECT TC.* , TL."+DBHandler_Location.KEY_LOCATION_LAT+" , TL."+
		DBHandler_Location.KEY_LOCATION_LONG+" FROM " + TABLE_CALLDETAILS+" TC INNER JOIN "+
		DBHandler_Location.TABLE_LOCATION+" TL ON TL."+DBHandler_Location.KEY_LOCATION_ID +" = TC."+
		KEY_CALLDETAILS_LOCATION_ID +" ORDER BY "+ KEY_CALLDETAILS_LOCATION_ID +" ASC LIMIT "+ MAX_CALL_RECORDS_TO_SERVER;
		
		String selectQuery = "SELECT * FROM " + TABLE_CALLDETAILS+ " WHERE " + KEY_CALLDETAILS_PHONE_NO+ " = '"+phoneNo+"' AND " +KEY_CALLDETAILS_RECORDING_DATE+ " = '"+daytime+"'";
		Log.i("SUSHIL","selectQuery in databse"+ selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		
        byte[] recordcall = null;
		//ArrayList<Object_CallDetails > list = new ArrayList<Object_CallDetails >();
        try{
		Cursor cur = db.rawQuery(selectQuery, null);
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					Object_CallDetails obj = new Object_CallDetails();
                    //obj.setPhoneNo(cur.getString(cur.getColumnIndex(KEY_CALLDETAILS_PHONE_NO)));
				    obj.setCallRecording(cur.getBlob(cur
							.getColumnIndex(KEY_CALLDETAILS_RECORDING)));
				  recordcall =  obj.getCallRecording();
				}while(cur.moveToNext());
			}
		}
		Log.i("SUSHIL","selectQuery is output"+ recordcall);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return recordcall;
	}
	*/
	
	/*public void  deleteCallDetails(JSONArray jsonArrayIds){
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
	}*/
}
