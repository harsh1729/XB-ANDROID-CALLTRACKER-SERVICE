package com.example.service23;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHandler_Main extends SQLiteOpenHelper {

	public static final String DB_NAME = "DB_AgentService.sqlite";
	public static final int DB_VERSION = 1;
	private static String DB_PATH = "";
	
	Context context;
	
	public DBHandler_Main(Context context) {
		super(context, DB_NAME,null, DB_VERSION);
		this.context = context;
		DB_PATH = this.context.getDatabasePath(DB_NAME).getPath();
	}
	
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public void createDataBase() {

		boolean dbExist = checkDataBase();

		if(dbExist){
			//do nothing - database already exist
			return;
		}

		//By calling this method an empty database will be created into the default system path
		//of your application so we will be able to overwrite that database with our database.
		SQLiteDatabase db = this.getReadableDatabase();

		
		try {

			copyDataBase();
			db.close();

		} catch (IOException e) {

			//throw new Error("Error copying database");
			Log.i("HARSH","Error copying database");

		}

	}

	private void copyDataBase() throws IOException{

		InputStream myInput = context.getAssets().open(DB_NAME);
		OutputStream myOutput = new FileOutputStream(DB_PATH );

		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}

		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

		Log.i("DBHandler_Test"," Database copied Succesfully !");

	}

	private boolean checkDataBase(){
		SQLiteDatabase checkDB = null;

		try{
			checkDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
			Log.i("DBHandler_Test","Datbase Exists !");

		}//catch(SQLiteCantOpenDatabaseException e){
		catch(SQLiteException e){
			Log.e("DBHandler_Test","ERROR ERROR");
			//database does't exist yet.

		}

		if(checkDB != null){

			checkDB.close();
			return true;

		}

		return false;
	}
}