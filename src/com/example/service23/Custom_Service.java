package com.example.service23;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

@SuppressLint("InlinedApi")
public class Custom_Service extends Service {

	// *********Recording Audio file variables******************
	//private AudioManager audioManager;
	private MediaRecorder recorder = null;
	private File audiofile;
	//private boolean recordstarted = false;
	//private String filepath;
	private boolean uploadlogs;
	// *********Recording Audio file variables******************

	private double latitude;
	private double longitude;
	//private String audioPrefix ="";
	BroadcastReceiver mReceiver;
	LocationManager locationManager;
	// private Context mContext;
	// private Intent mIntent;
	//private int deviceCallVol;
	MyLocationListener locationListener;
	boolean gps_enabled = false;
	boolean network_enabled = false;
	boolean stateCallStarted = false;
	private static final String ACTION_IN = "android.intent.action.PHONE_STATE";
	private static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";

	String phoneNumber = "";
    private long insertid = 0;
	// use this as an inner class like here or as a top-level class
	


	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// Log.i("SUSHIL", "onLocationChanged --> " +
			// location.getLatitude()+ "," + location.getLongitude());

			if (location == null) {

				latitude = Globals.NO_LOCATION;
				longitude = Globals.NO_LOCATION;
			} else {
				if (location.getLatitude() == 0 && location.getLongitude() == 0) {
					latitude = Globals.NO_LOCATION;
					longitude = Globals.NO_LOCATION;
				} else {
					latitude = location.getLatitude();
					longitude = location.getLongitude();
				}

			}

		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i("SUSHIL", "onProviderDisabled");
			latitude = Globals.NO_LOCATION;
			longitude = Globals.NO_LOCATION;

		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i("SUSHIL", "onProviderEnabled");

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i("SUSHIL", "onStatusChanged");

		}

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Let it continue running until it is stopped.
		Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
		// startTelephonyService();
		mReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		filter.addAction(ACTION_OUT);
		filter.addAction(ACTION_IN);
		registerReceiver(mReceiver, filter);
		//

		 //comenting Location listsner not required
		//locationListener = new MyLocationListener();
		//locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				//100, locationListener);

		//
		/*
		 * locationListener = new MyLocationListener(); locationManager =
		 * (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		 * 
		 * try { gps_enabled =
		 * locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); }
		 * catch (Exception e) { e.printStackTrace(); }
		 * 
		 * try { network_enabled =
		 * locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		 * } catch (Exception e) { e.printStackTrace(); }
		 * 
		 * // don't start listeners if no provider is enabled if (!gps_enabled
		 * && !network_enabled) { Log.e("SUSHIL", "Nothing Enabled"); }
		 * 
		 * // if gps is enabled, get location updates if (gps_enabled) {
		 * Log.e("SUSHIL", "gps_enabled, requesting updates.");
		 * locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		 * 0, 0, locationListener); }else if (network_enabled) { Log.e("SUSHIL",
		 * "network_enabled, requesting updates.");
		 * locationManager.requestLocationUpdates
		 * (LocationManager.NETWORK_PROVIDER, 0, 0, locationListener); }
		 * 
		 * //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		 * 0, 0, locationListener);
		 */
		// /
		/*
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(15 * 60 * 1000);
						sendLocationDetails();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		 */
		insertCallDetails();

		return START_STICKY;
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		// Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
		Log.i("HARSH", "Service Destroyed");
		startService(new Intent(getBaseContext(), Custom_Service.class));
	}

	  

	// /////// CALL DATA RELATED FUNTIONS
	
	public class MyReceiver extends BroadcastReceiver {
		Bundle bundle;
		//String countryCode = "";
		// String zero = "";
		// String outCall;
	

		@Override
		public void onReceive(Context context, Intent intent) {
			
			/*
			 * mContext = context; mIntent = intent; TelephonyManager tm =
			 * (TelephonyManager)
			 * context.getSystemService(Context.TELEPHONY_SERVICE); int events =
			 * PhoneStateListener.LISTEN_CALL_STATE;
			 * tm.listen(phoneStateListener, events);
			 */

			try {
				String action = intent.getAction();
				String state = intent
						.getStringExtra(android.telephony.TelephonyManager.EXTRA_STATE);

				if (action
						.equals(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
					bundle = intent.getExtras();
					/*state = intent
							.getStringExtra(android.telephony.TelephonyManager.EXTRA_STATE);*/
					// Log.i("SUSHIL", "action for phone state changed "+state);
					if (state.equals("IDLE")) {
						Log.i("EVENTLOG", "Phone state is IDLE");
						if (stateCallStarted) { // A Call Ended
							//if (recordstarted) {
							try{
								if(recorder!=null){
									
								recorder.stop();
								recorder.reset();
								recorder.release();
								recorder = null;
								audiofile = null;
								//audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, deviceCallVol, 0);
								//Log.i("SUSHIL", "Bolean check" + recordstarted);
								}
							//}
							}catch(Exception ex){
								recorder = null;
								//audioPrefix = "";
								audiofile = null;
							}
							new Thread(new Runnable() {
								public void run() {
									try {
										Thread.sleep(500);
										// Log.i("SUSHIL",
										// "Bolean check"+recordstarted);
										//Log.i("EVENTLOG", "call recording is "+recordstarted);
										insertCallDetails();
									} catch (InterruptedException e) {
										//recordstarted = false;
										//filepath = "";
										//audioPrefix = "";
										audiofile = null;
										e.printStackTrace();
									}
									catch (Exception e) {
										//recordstarted = false;
										//filepath = "";
										//audioPrefix = "";
										audiofile = null;
										e.printStackTrace();
									}
								}
							}).start();
							stateCallStarted = false;
							// recordstarted = false;
						}
					} else if (state.equals("RINGING")) {
						Log.i("EVENTLOG", "Phone state is RINGING");
						
						stateCallStarted = true;
						//!recordstarted
						if(audiofile == null){
							phoneNumber = getMax10DigitNo(bundle
									.getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
							//Number = Number.replace(" ", "");
							//countryCode = Number.substring(2, Number.length());
							//audioPrefix = getMax10DigitNo(Number);
						}
						//Number = Number.replace(" ", "");
						//countryCode = Number.substring(2, Number.length());
						// zero = Number.substring(0);
						
						// / Toast.makeText(context, "INCOMING: " + Number,
						// Toast.LENGTH_LONG).show();
					} else if (state.equals("OFFHOOK")) {
						Log.i("EVENTLOG", "Phone state is OFFHOOK");
						//filepath = "";
						stateCallStarted = true;
						
						//Log.i("SUSHIL", "phone no " + audioPrefix);
						//!recordstarted
						if (audiofile == null)
							  recordingSave();
						
						/*Object_AppConfig ob = new Object_AppConfig(context);
						if (!ob.get_Status()) {
							DBHandler_CallRecord bdhCheck = new DBHandler_CallRecord(
									context);
							if (bdhCheck.isValidNo(countryCode)) {
								if (!recordstarted)//if(getRecorder().
									filepath = recordingSave();
							}
						} else {
							if (!recordstarted)
								filepath = recordingSave();
							// Log.i("SUSHIL", "recording all ");
						}*/
						
					}

					else {
						Log.i("EVENTLOG", "Some other action");
					}

				}
				if (action.equals(ACTION_OUT)) {
					Log.i("EVENTLOG", "It is Outgoing call");
					//!recordstarted
					if(audiofile == null){
						phoneNumber = getMax10DigitNo( intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
						//Number = Number.replace(" ", "");
						//countryCode = Number.substring(2, Number.length());
						//audioPrefix = getMax10DigitNo(Number);
					}
					
					// zero = Number.substring(0);
					// Toast.makeText(context, "Outgoing call "+Number,
					// Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				Log.i("SUSHIL", " Error " + e);
			}

		}

		/*public MyReceiver() {

		}*/
	}

	private String getMax10DigitNo(String num){
		if(num!=null){
			num = num.replace(" ", "");
			if( num.length() >= 10){
				return num.substring(num.length()-10, num.length());
			}else{
				return num;
			}
		}
		return "";
	}
	
	/*private boolean getRecordStartedStatus(){
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return recordstarted;
	}*/
	private void recordingSave() {

		//filepath = "";
		
		/*File sampleDir = new File(Environment.getExternalStorageDirectory(),
				"/MyRecordings");
		if (!sampleDir.exists()) {
			sampleDir.mkdirs();
		}*/
		
		String dateInString = getDateString(0);//new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		
		File dateDir = new File(Environment.getExternalStorageDirectory(),"/MyRecordings/"+dateInString);
		if(!dateDir.exists()){
			dateDir.mkdirs();
		}
		//File dateDir = sampleDir.
		
		//String dateInString = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()).toString();
		String file_name =  Globals.getLastCallId(this)+"_"+phoneNumber+"_";//+dateInString; //"Record";
		try {
			audiofile = File.createTempFile(file_name, ".m4a", dateDir);
			//audiofile = new File(sampleDir, file_name);
		} catch (Exception e) {
			Toast.makeText(this, "error in creating temp file", Toast.LENGTH_SHORT);
			e.printStackTrace();
			audiofile = null;
			return;
		}
		
		//audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		//get the current volume set
		//deviceCallVol = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
		//set volume to maximum
		//audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
		
		recorder = new MediaRecorder();
		recorder.reset();
		recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		recorder.setOutputFile(audiofile.getAbsolutePath());
		recorder.setAudioEncodingBitRate(16);
		recorder.setAudioSamplingRate(44100);
		
		try {
			recorder.prepare();
		} 
		catch (Exception e) {
			Toast.makeText(this, "error in recorder prepare", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			audiofile = null;
			recorder = null;
			return;
		}
		recorder.start();
		//recordstarted = true;
		// }
		//Log.i("SUSHIL","SUSHIL audio file path  "+audiofile.getAbsolutePath());
		//return audiofile.getAbsolutePath();
	}

	
	private void insertCallDetails() {

		long getLastId = Globals.getLastCallId(this);
		ArrayList<String> listName = getListFilename(0);
		ArrayList<String> listNamesYesterday = null;
		
		Cursor managedCursor = this.getContentResolver().query(
				CallLog.Calls.CONTENT_URI, null,
				CallLog.Calls._ID + " > " + getLastId, null,
				CallLog.Calls.DATE + " ASC");

		ArrayList<Object_CallDetails> listCalls = new ArrayList<Object_CallDetails>();
		

		while (managedCursor.moveToNext()) {
			Object_CallDetails obj = new Object_CallDetails();
            long id = managedCursor.getLong(managedCursor
			.getColumnIndex(CallLog.Calls._ID));
           
			obj.setId(id);
			
			obj.setName(managedCursor.getString(managedCursor
					.getColumnIndex(CallLog.Calls.CACHED_NAME)));
			String phNo = managedCursor.getString(managedCursor
					.getColumnIndex(CallLog.Calls.NUMBER));
			obj.setPhoneNo(phNo);
			phNo = getMax10DigitNo(phNo);
			// remove(obj.getPhoneNo());
			obj.setDurationInSec(managedCursor.getLong(managedCursor
					.getColumnIndex(CallLog.Calls.DURATION)));
			obj.setTimeStamp(Globals.getDateFromLong((managedCursor
					.getLong(managedCursor.getColumnIndex(CallLog.Calls.DATE)))));
			obj.setLocationId(insertLocationDetails(true));
			obj.setCallTypeId(getCallTypeId(managedCursor.getInt(managedCursor
					.getColumnIndex(CallLog.Calls.TYPE))));
			
				//if (filepath!= null && !filepath.isEmpty()) 
				//if(audiofile != null){
			String audioFilePath = "";
			String myString = getLastId+"_"+phNo;//getLastId
			//Log.i("SUSHIL", "Audio file name list size "+listName);
			//Log.i("SUSHIL", "My string is  "+myString);
			
			audioFilePath = getAudioNameFromFolder(listName, myString);
			if(audioFilePath.isEmpty()){ //Audio might be in yesterday's folder
				if(listNamesYesterday == null){
					listNamesYesterday = getListFilename(-1);
				}
				audioFilePath = getAudioNameFromFolder(listNamesYesterday, myString);
			}
			
			Log.i("SUSHIL", "Audio file path "+audioFilePath);
			obj.setCallRecording(audioFilePath);
			
				/*if(phNo.contains(audioPrefix))
					{
						obj.setCallRecording(audiofile.getAbsolutePath());
						Log.i("EVENTLOG", "SUSHIL AUDIO FILE PATH .." + audiofile.getAbsolutePath());
					}
				         else{
					obj.setCallRecording("");
				}*/
			
			
			listCalls.add(obj);

		}

		//recordstarted = false;
		//filepath = "";
		//audioPrefix = "";
		//audiofile = null;
		
		DBHandler_CallDetails dbC = new DBHandler_CallDetails(this);
		dbC.insertCallDetails(listCalls);

		
		//Log.i("SUSHIL", "send call log from calling");
		//sendCallLogs();
		sendCallAudio();
		
	}

	private String getAudioNameFromFolder(ArrayList<String> listName , String myString){
		
		String audioFilePath = "";
		for (String str : listName) {
			if(str.contains(myString)){
				Log.i("SUSHIL", "Audio file found in current date folder");
				audioFilePath = str;
				break;
			}
		}
		return audioFilePath;
	}

    
    private ArrayList<String> getListFilename(int daysFromCurrent){
    	
    	ArrayList<String>nameList = new ArrayList<String>();
   	 try{
   	    String dateInString =getDateString(daysFromCurrent); //dateFormat.format(cal.getTime());
   	    File yourDir = new File(Environment.getExternalStorageDirectory(), "/MyRecordings/"+dateInString);
           for (File f : yourDir.listFiles()) 
                  {
                       if (f.isFile())
                          {
                             nameList.add(f.getAbsolutePath());
                          }

                 }
   	 }catch(Exception ex){
   		 audiofile = null;
   		 recorder=null;
   	 }
           
    	 return nameList;
    	
    }
    private String getDateString(int daysFromCurrent){
    	
    	Calendar cal = Calendar.getInstance();
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);//"yyyy-MM-dd",Locale.ENGLISH);
        cal.add(Calendar.DATE, daysFromCurrent);
	    String dateInString = dateFormat.format(cal.getTime());
	    
	    return dateInString;
    }


	/*
	 * private String remove(String number){ String Number = number; int lenth =
	 * number.length(); Log.i("SUSHIL", "number lenth "+lenth); if(lenth==13){
	 * Number = number.substring(0,2); } char arr[] = number.toCharArray(); //
	 * convert the String object to array of char for(char c: arr){
	 * Log.i("SUSHIL", "number character!...... "+c); } return Number; }
	 */

	/*private void sendCallLogs() {
		Custom_ConnectionDetector cd = new Custom_ConnectionDetector(
				getApplicationContext());

		if (cd.isConnectingToInternet()) {
			try {
				uploadlogs = false;
				DBHandler_CallDetails dbC = new DBHandler_CallDetails(this);

				// ArrayList<ArrayList<Object_CallDetails>> superlistCalls =
				// dbC.getCallDetails();
				// Log.i("DARSH",
				// "superlistCalls count -->"+superlistCalls.size());
				final JSONArray jArrayLoc = new JSONArray();
				HashMap<String, File> map = new HashMap<String, File>();
				//String file = "";
				// for(ArrayList<Object_CallDetails> listCalls : superlistCalls)
				ArrayList<Object_CallDetails> listCalls = dbC.getCallDetails();
				// Log.i("SUSHIL", "get data size "+listCalls.size());
				if (listCalls.size() > 0) {

					Log.i("SUSHIL", "listCalls count -->" + listCalls.size());
					for (Object_CallDetails objCall : listCalls) {

						JSONObject jObjTemp = new JSONObject();
						jObjTemp.put("rowId", objCall.getId());
						jObjTemp.put("phone", objCall.getPhoneNo());
						jObjTemp.put("name", objCall.getName());
						jObjTemp.put("callTypeId", objCall.getCallTypeId());
						jObjTemp.put("daytime", objCall.getTimeStamp());
						jObjTemp.put("duration", objCall.getDurationInSec());
						// DBHandler_CallRecord dbhcall = new
						// DBHandler_CallRecord(this);
						String recordpath = objCall.getCallRecording();
						if (recordpath != null && !recordpath.isEmpty()) {
							// file = getfilepath(record);
							// Log.i("SUSHIL", "file path is " +file);
							File audioFile = new File(recordpath);
							map.put(objCall.getId() + "", audioFile);
							// delete();
						} 
						Log.i("SUSHIL", "Audio map is " + map);

						// /jObjTemp.put("audiofile", new File(file));
						// String audio = Base64.encodeToString(record, 0);
						JSONArray arrayLoc = new JSONArray();
						arrayLoc.put(objCall.getLatitude());
						arrayLoc.put(objCall.getLongitude());

						jObjTemp.put("location", arrayLoc);

						jArrayLoc.put(jObjTemp);

						Log.i("SUSHIL",
								"SENDING JSON OBJ -->" + jObjTemp.toString()
										+ "\n");
					}
					try {
						if (map.size() != 0) {
							Log.i("SUSHIL", "map of Audio " + map);
							String commaSeperatedKeys = getCommaSeperatedKeys(map
									.keySet());
							Custom_VolleyAudioPost jsonObjectRQST = new Custom_VolleyAudioPost(
									Globals.getSendCallLogsAudio(Globals
											.getDeviceImei(this)), map,
									Globals.getParams_UploadAudioStringParams(
											this, commaSeperatedKeys),
									new Listener<JSONObject>() {

										@Override
										public void onResponse(
												JSONObject response) {
											Log.i("SUSHIL",
													"responce recevied "
															+ response);
											JSONObject obj = gotResponceAudio(response);
											ArrayList<String> listIds = new ArrayList<String>();
											for (int i = 0; i < jArrayLoc.length(); i++) {
												JSONObject jOBT = null;
												try {
													jOBT = jArrayLoc
															.getJSONObject(i);
													String rowId = jOBT
															.getString("rowId");
													String AudioName = obj
															.getString(rowId);
													jOBT.put("Audio", AudioName);
													listIds.add(rowId);
												} catch (JSONException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}
											uploadCallogs(jArrayLoc, listIds);
											
										}

									}, new ErrorListener() {
										@Override
										public void onErrorResponse(
												VolleyError err) {
											Log.i("SUSHIL",
													"VOLLEY EX--> "
															+ err.getLocalizedMessage()
															+ "\n"
															+ err.getMessage()
															+ "\n"
															+ err.getStackTrace());
										}
									});

							Custom_VolleyAppController.getInstance()
									.addToRequestQueue(jsonObjectRQST);
						} else {
							uploadlogs = true;
							uploadCallogs(jArrayLoc, null);
						}
					} catch (Exception ex) {
						Log.i("SUSHIL",
								"EX--> " + ex.getLocalizedMessage() + "\n"
										+ ex.getMessage() + "\n"
										+ ex.getStackTrace());
					}

				}
			} catch (Exception ex) {
				Log.i("SUSHIL",
						"EX--> " + ex.getLocalizedMessage() + "\n"
								+ ex.getMessage() + "\n" + ex.getStackTrace());
			}
		}

		// }

	}*/
    
   
    private void sendCallAudio(){
    	Custom_ConnectionDetector cd = new Custom_ConnectionDetector(
				getApplicationContext());
    	Log.i("SUSHIL", "send call audio is call ");
		if (cd.isConnectingToInternet()) {
			try {
				 
				DBHandler_CallDetails dbC = new DBHandler_CallDetails(this);
				ArrayList<Object_CallDetails> listAudio = dbC.getRecordAudio();
				if (listAudio.size() > 0) {
					HashMap<String, File> map = new HashMap<String, File>();
					for (int i = 0 ; i < listAudio.size(); i++) {
						Log.i("SUSHIL", "loop is continue ");
						Object_CallDetails obj  = listAudio.get(i);
						insertid = obj.getId();
						String recordpath = obj.getCallRecording();
						if (recordpath != null && !recordpath.isEmpty()) {
							
							File audioFile = new File(recordpath);
							map.put(obj.getId() + "", audioFile);
							Log.i("SUSHIL", "map "+map);
						}else{
							///
							//
							dbC.updateRecordAudioName(insertid, "");
							listAudio = dbC.getRecordAudio();
							map = new HashMap<String, File>();
							i=-1;
							Log.i("SUSHIL", "listAudio "+insertid);
							if (listAudio.size() > 0){
								continue;
								//sendCallAudio();
								//return;
							}else{
								//break;
								uploadCallogs();
							}
							
						}
					}
						//creat call for server
					
					
						if (map.size() != 0) {
							
							Log.i("SUSHIL", "map of Audio " + map);
							String commaSeperatedKeys = getCommaSeperatedKeys(map
									.keySet());
							Custom_VolleyAudioPost jsonObjectRQST = new Custom_VolleyAudioPost(
									Globals.getSendCallLogsAudio(Globals
											.getDeviceImei(this)), map,
									Globals.getParams_UploadAudioStringParams(
											this, commaSeperatedKeys),
									new Listener<JSONObject>() {

										@Override
										public void onResponse(
												JSONObject response) {
											Log.i("SUSHIL",
													"responce recevied "
															+ response);
											gotAudioRespose(response);
										}

									}, new ErrorListener() {
										@Override
										public void onErrorResponse(
												VolleyError err) {
											Log.i("SUSHIL",
													"VOLLEY EX--> "
															+ err.getLocalizedMessage()
															+ "\n"
															+ err.getMessage()
															+ "\n"
															+ err.getStackTrace());
										}
									});

							Custom_VolleyAppController.getInstance()
									.addToRequestQueue(jsonObjectRQST);
							
							
						}else{
							//uploadCallogs();
							//sendCallAudio();
						}
						
					
				}else{
					uploadCallogs();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    }

    private void gotAudioRespose(JSONObject response){
    	try{
    	if (response.has("audioName")) {
    		JSONObject obj = response.getJSONObject("audioName");
    		if(obj.has(insertid+"")){
    			String audioname = obj.getString(insertid+"");
    			DBHandler_CallDetails db = new DBHandler_CallDetails(this);
    			db.updateRecordAudioName(insertid,audioname);
    			sendCallAudio();
    		}
    	}
    	}catch(JSONException ex){
    		ex.printStackTrace();
    	}catch (Exception e) {
    		e.printStackTrace();
		}
    }
    
	private void uploadCallogs() {
		/*
		 * if(ids!= null){ DBHandler_CallRecord DBR = new
		 * DBHandler_CallRecord(this); DBR.deleteRecording(ids); }
		 */
		try{
		final JSONArray jarray = new JSONArray();
		DBHandler_CallDetails dbC = new DBHandler_CallDetails(this);
		ArrayList<Object_CallDetails> listCalls = dbC.getCallDetails();
		// Log.i("SUSHIL", "get data size "+listCalls.size());
		if (listCalls.size() > 0) {

			Log.i("SUSHIL", "listCalls count -->" + listCalls.size());
			for (Object_CallDetails objCall : listCalls) {

				JSONObject jObjTemp = new JSONObject();
				jObjTemp.put("rowId", objCall.getId());
				jObjTemp.put("phone", objCall.getPhoneNo());
				jObjTemp.put("name", objCall.getName());
				jObjTemp.put("callTypeId", objCall.getCallTypeId());
				jObjTemp.put("daytime", objCall.getTimeStamp());
				jObjTemp.put("duration", objCall.getDurationInSec());
				jObjTemp.put("Audio", objCall.getCallRecordingName());
				// DBHandler_CallRecord dbhcall = new
				// DBHandler_CallRecord(this);
				

				jarray.put(jObjTemp);

				Log.i("SUSHIL",
						"SENDING JSON OBJ -->" + jObjTemp.toString()
								+ "\n");
			}
		
			String url = Globals
					.getSendCallLogsURL(Globals.getDeviceImei(this));
			// String url =
			// "http://10.0.2.2/calltracker/agents/updateCallLog.php?deviceId=000000000000000&callLog=[%7B%22callTypeId%22:2,%22duration%22:22,%22daytime%22:%222015-04-14%2003:17:52%22,%22phone%22:%228875897798%22,%22rowId%22:1,%22location%22:[0,0]%7D,%7B%22callTypeId%22:2,%22duration%22:19,%22daytime%22:%222015-04-14%2003:18:48%22,%22phone%22:%228875897798%22,%22rowId%22:2,%22location%22:[0,0]%7D,%7B%22callTypeId%22:2,%22duration%22:93,%22daytime%22:%222015-04-14%2003:27:19%22,%22phone%22:%228875897798%22,%22rowId%22:3,%22location%22:[0,0]%7D]";
			Log.i("SUSHIL", "URL -->" + url);
			
			Custom_VolleyObjectRequest jsonObjectRQST = new Custom_VolleyObjectRequest(
					Request.Method.POST, url,
					Globals.getParams_UploadContentCallLogParams(this,
							jarray.toString()), new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							gotCallLogsResponce(response);

						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError err) {

							// Log.i("SUSHIL", "ERROR VolleyError");
							Log.i("SUSHIL",
									"VOLLEY EX--> " + err.getLocalizedMessage()
											+ "\n" + err.getMessage() + "\n"
											+ err.getStackTrace());

						}
					});

			System.out.println(jsonObjectRQST);
			Custom_VolleyAppController.getInstance().addToRequestQueue(
					jsonObjectRQST);
		   }else{
			  // upload all call logs
			 return;  
		   }
		}catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	/*private JSONObject gotResponceAudio(JSONObject obj) {
		uploadlogs = true;
		JSONObject array = null;
		if (obj.has("audioName")) {
			try {
				array = obj.getJSONObject("audioName");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return array;
	}*/

	private String getCommaSeperatedKeys(Set<String> setKeys) {

		String keysCommaSeperated = "";

		for (String key : setKeys) {
			keysCommaSeperated += key + ",";
		}

		if (keysCommaSeperated.contains(",")) {
			keysCommaSeperated = keysCommaSeperated.substring(0,
					keysCommaSeperated.length() - 1);
		}

		Log.i("SUSHIL", "keysCommaSeperated " + keysCommaSeperated);
		return keysCommaSeperated;
	}

	private void gotCallLogsResponce(JSONObject jsonObjResponce) {

		// {"insertedRows":[],"deviceRegistered":true,"mappedStatus":true}
		if (jsonObjResponce != null) {

			Log.i("SUSHIL",
					"gotCallLogsResponce--> " + jsonObjResponce.toString());
			try {
				// {"registerStatus":true,"loginStatus":true,"mappedStatus":true},{"loginStatus":false}
				if (jsonObjResponce.has("callLogs")) {
					JSONObject obj = jsonObjResponce.getJSONObject("callLogs");
					JSONArray ids = obj.getJSONArray("insertedRows");
					Log.i("SUSHIL", "ARRAy of ids " + ids.toString());
					DBHandler_CallDetails dbC = new DBHandler_CallDetails(this);
					dbC.updateCallDetails(ids);
				}
				if (jsonObjResponce.has("recordNo")) {
					DBHandler_CallRecord DbC = new DBHandler_CallRecord(this);
					JSONArray Numbers = jsonObjResponce
							.getJSONArray("recordNo");
					Log.i("SUSHIL",
							"gotNUmberResponce--> " + Numbers.toString());
					DbC.deletePhoneNO();
					for (int i = 0; i < Numbers.length(); i++) {

						JSONObject obj = Numbers.getJSONObject(i);
						String phone = obj.getString("phone");
						Log.i("SUSHIL", "gotResponce--> " + phone.toString());

						DbC.insertPhoneNO(phone);
					}
				}
				if (jsonObjResponce.has("allRecord")) {
					Boolean record = jsonObjResponce.getBoolean("allRecord");
					Object_AppConfig objapp = new Object_AppConfig(this);
					objapp.set_Status(record);
				}
				uploadCallogs(); // For Entries Remaining in DB
			} catch (Exception ex) {
				Log.i("SUSHIL", "Exception in reading Location Responce");
			}
		}

	}

	private int getCallTypeId(int callTypeId) {

		int returnID = 0;

		switch (callTypeId) {
		case CallLog.Calls.OUTGOING_TYPE:
			returnID = Globals.CALL_TYPE_OUTGOING;
			break;
		case CallLog.Calls.INCOMING_TYPE:
			returnID = Globals.CALL_TYPE_INCOMING;
			break;
		case CallLog.Calls.MISSED_TYPE:
			returnID = Globals.CALL_TYPE_MISSED;
			break;
		case 5:
			returnID = Globals.CALL_TYPE_CUT;
			break;

		default:
			returnID = Globals.CALL_TYPE_UNKNOWN;
			break;
		}

		return returnID;
	}

	
	
	
	
	// /////// LOCATION DATA RELATED FUNTIONS
		private void sendLocationDetails() {

			insertLocationDetails(false);

			// Call the service
			Custom_ConnectionDetector cd = new Custom_ConnectionDetector(
					getApplicationContext());

			if (cd.isConnectingToInternet()) {
				try {

					DBHandler_Location dbL = new DBHandler_Location(this);

					ArrayList<Object_Location> listLoc = dbL
							.getExceptCallLocation();

					final JSONArray jArrayLoc = new JSONArray();

					for (Object_Location objLoc : listLoc) {

						JSONArray jArrayTemp = new JSONArray();

						jArrayTemp.put(objLoc.getId());
						jArrayTemp.put(objLoc.getLatitude());
						jArrayTemp.put(objLoc.getLongitude());
						jArrayTemp.put(objLoc.getTimeStamp());

						jArrayLoc.put(jArrayTemp);
					}

					String url = Globals.getSendLocationURL(
							Globals.getDeviceImei(this), jArrayLoc.toString());//
					Log.i("SUSHIL", "URL -->" + url);

					// JSONObject jParam = new JSONObject();
					// jParam.put("deviceId", Globals.getDeviceImei(this));
					// jParam.put("locationArray", jArrayLoc);

					// Map<String,String> jParam = new HashMap<String, String>();
					// jParam.put("deviceId", Globals.getDeviceImei(this));
					// jParam.put("locationArray", jArrayLoc.toString());

					JsonObjectRequest jsonArrayRQST = new JsonObjectRequest(
							Request.Method.POST, url, null,
							new Response.Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject response) {

									gotLocationResponce(response);

								}
							}, new ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError ex) {
									Log.i("SUSHIL",
											"VOLLEY EX--> "
													+ ex.getLocalizedMessage()
													+ "\n" + ex.getMessage() + "\n"
													+ ex.getStackTrace());

								}
							});

					Custom_VolleyAppController.getInstance().addToRequestQueue(
							jsonArrayRQST);

				} catch (Exception ex) {

					Log.i("SUSHIL",
							"EX--> " + ex.getLocalizedMessage() + "\n"
									+ ex.getMessage() + "\n" + ex.getStackTrace());
				}

			} /*
			 * Custom_VolleyObjectRequest jsonObjectRQST = new
			 * Custom_VolleyObjectRequest(Request.Method.POST,
			 * url,Globals.getParams_UploadLocationParams(this,
			 * jArrayLoc.toString()) , new Listener<JSONObject>() {
			 * 
			 * @Override public void onResponse(JSONObject response){
			 * gotLocationResponce(response);
			 * 
			 * } }, new ErrorListener() {
			 * 
			 * @Override public void onErrorResponse(VolleyError err) {
			 * 
			 * Log.i("SUSHIL", "ERROR VolleyError"); Log.i("SUSHIL", "VOLLEY EX--> "
			 * + err.getLocalizedMessage() + "\n" + err.getMessage() + "\n" +
			 * err.getStackTrace());
			 * 
			 * 
			 * 
			 * } });
			 * 
			 * System.out.println(jsonObjectRQST);
			 * Custom_VolleyAppController.getInstance().addToRequestQueue(
			 * jsonObjectRQST); } catch(Exception ex){ Log.i("SUSHIL", "EX--> " +
			 * ex.getLocalizedMessage() + "\n" + ex.getMessage() + "\n" +
			 * ex.getStackTrace()); } }
			 */

		}

		private long insertLocationDetails(boolean isCallLoc) {

			DBHandler_Location dbL = new DBHandler_Location(this);

			Object_Location loc = new Object_Location();
			if (isCallLoc)
				loc.setIsCallLocation(1);
			else
				loc.setIsCallLocation(0);

			loc.setLatitude(latitude);
			loc.setLongitude(longitude);
			loc.setTimeStamp(Globals.getCurrentTime());

			return dbL.insertLocation(loc);
		}

		private void gotLocationResponce(JSONObject jsonObjResponce) {

			// {"insertedRows":[],"deviceRegistered":true,"mappedStatus":true}
			if (jsonObjResponce != null) {

				Log.i("SUSHIL",
						"gotLocationResponce--> " + jsonObjResponce.toString());
				try {
					// {"registerStatus":true,"loginStatus":true,"mappedStatus":true},{"loginStatus":false}
					if (jsonObjResponce.has("location")) {

						JSONObject obj = jsonObjResponce.getJSONObject("location");
						JSONArray ids = obj.getJSONArray("insertedRows");
						Log.i("SUSHIL", "gotLocationResponce--> " + ids.toString());
						DBHandler_Location dbL = new DBHandler_Location(this);
						dbL.deleteLocations(ids);
					}
					if (jsonObjResponce.has("recordNo")) {
						DBHandler_CallRecord DbC = new DBHandler_CallRecord(this);
						JSONArray Numbers = jsonObjResponce
								.getJSONArray("recordNo");
						Log.i("SUSHIL",
								"gotNUmberResponce--> " + Numbers.toString());
						DbC.deletePhoneNO();
						for (int i = 0; i < Numbers.length(); i++) {

							JSONObject obj = Numbers.getJSONObject(i);
							String phone = obj.getString("phone");
							Log.i("SUSHIL", "gotResponce--> " + phone.toString());

							DbC.insertPhoneNO(phone);
						}

					}
					if (jsonObjResponce.has("allRecord")) {
						Boolean record = jsonObjResponce.getBoolean("allRecord");
						Object_AppConfig objapp = new Object_AppConfig(this);
						objapp.set_Status(record);
					}
					/*
					 * if(jsonObjResponce.has("Deactive_phoneNo")){ JSONArray
					 * Numbers = jsonObjResponce.getJSONArray("Deactive_phoneNo");
					 * DBHandler_CallRecord DbC = new DBHandler_CallRecord(context);
					 * if(Numbers!=null){ DbC.deletePhoneNO(Numbers); } }
					 */
				} catch (Exception ex) {
					ex.printStackTrace();
					Log.i("SUSHIL", "Exception in reading Location Responce");
				}
			}

		}
}
