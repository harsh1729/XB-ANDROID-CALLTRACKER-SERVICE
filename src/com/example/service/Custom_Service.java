package com.example.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class Custom_Service extends Service {

	private double latitude;
	private double longitude;

	BroadcastReceiver mReceiver;
	LocationManager locationManager;

	MyLocationListener locationListener;
	boolean gps_enabled = false;
	boolean network_enabled = false;

	boolean stateCallStarted = false;

	// use this as an inner class like here or as a top-level class
	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				String action = intent.getAction();
				String state = intent
						.getStringExtra(android.telephony.TelephonyManager.EXTRA_STATE);

				if (action
						.equals(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
					Log.i("HARSH1", "action for phone state changed");

					if (state.equals("IDLE")) {
						if (stateCallStarted) { // A Call Ended
							
							
							new Thread(new Runnable() {
								public void run() {
										try {
											Thread.sleep(1 * 1000);
											insertCallDetails();
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
								}
							}).start();
							stateCallStarted = false;
						}
					} else if (state.equals("OFFHOOK")
							|| state.equals("RINGING")) {
						stateCallStarted = true;
					}

				} else {
					Log.i("HARSH1", "Some other action");
				}

				Log.i("DARSH", "State is -->" + state);
			} catch (Exception e) {
				Log.e("Phone Receive Error", " " + e);
			}
		}

		// constructor
		public MyReceiver() {

		}
	}

	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			//Log.i("HARSH1", "onLocationChanged --> " + location.getLatitude()+ "," + location.getLongitude());

			if(location == null){
				
				latitude = Globals.NO_LOCATION;
				longitude = Globals.NO_LOCATION;
			}else{
				if(location.getLatitude() == 0 && location.getLongitude() == 0){
					latitude = Globals.NO_LOCATION;
					longitude = Globals.NO_LOCATION;
				}else{
					latitude = location.getLatitude();
					longitude = location.getLongitude();
				}
				
			}
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i("HARSH1", "onProviderDisabled");
			latitude = Globals.NO_LOCATION;
			longitude = Globals.NO_LOCATION;

		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i("HARSH1", "onProviderEnabled");

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i("HARSH1", "onStatusChanged");

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
		registerReceiver(mReceiver, filter);
		//

		locationListener = new MyLocationListener();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				100, locationListener);

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
		 * && !network_enabled) { Log.e("HARSH1", "Nothing Enabled"); }
		 * 
		 * // if gps is enabled, get location updates if (gps_enabled) {
		 * Log.e("HARSH1", "gps_enabled, requesting updates.");
		 * locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		 * 0, 0, locationListener); }else if (network_enabled) { Log.e("HARSH1",
		 * "network_enabled, requesting updates.");
		 * locationManager.requestLocationUpdates
		 * (LocationManager.NETWORK_PROVIDER, 0, 0, locationListener); }
		 * 
		 * //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		 * 0, 0, locationListener);
		 */
		// /
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
		
		insertCallDetails();
		
		return START_STICKY;
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		//Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
		Log.i("HARSH", "Service Destroyed");
		startService(new Intent(getBaseContext(), Custom_Service.class));
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
				Log.i("HARSH1", "URL -->" + url);

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
								Log.i("HARSH1",
										"VOLLEY EX--> "
												+ ex.getLocalizedMessage()
												+ "\n" + ex.getMessage() + "\n"
												+ ex.getStackTrace());

							}
						});

				Custom_VolleyAppController.getInstance().addToRequestQueue(
						jsonArrayRQST);
			} catch (Exception ex) {

				Log.i("HARSH1",
						"EX--> " + ex.getLocalizedMessage() + "\n"
								+ ex.getMessage() + "\n" + ex.getStackTrace());
			}
		}

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

			Log.i("HARSH1",
					"gotLocationResponce--> " + jsonObjResponce.toString());
			try {
				// {"registerStatus":true,"loginStatus":true,"mappedStatus":true},{"loginStatus":false}
				if (jsonObjResponce.has("insertedRows")) {

					JSONArray ids = jsonObjResponce
							.getJSONArray("insertedRows");
					DBHandler_Location dbL = new DBHandler_Location(this);
					dbL.deleteLocations(ids);
				}
			} catch (Exception ex) {
				Log.i("HARSH1", "Exception in reading Location Responce");
			}
		}

	}

	// /////// CALL DATA RELATED FUNTIONS
	private void insertCallDetails() {

		Cursor managedCursor = this.getContentResolver().query(
				CallLog.Calls.CONTENT_URI, null,
				CallLog.Calls._ID + " > " + Globals.getLastCallId(this), null,
				CallLog.Calls.DATE + " ASC");

		ArrayList<Object_CallDetails> listCalls = new ArrayList<Object_CallDetails>();

		while (managedCursor.moveToNext()) {
			Object_CallDetails obj = new Object_CallDetails();

			obj.setId(managedCursor.getLong(managedCursor
					.getColumnIndex(CallLog.Calls._ID)));
			obj.setName(managedCursor.getString(managedCursor
					.getColumnIndex(CallLog.Calls.CACHED_NAME)));
			obj.setPhoneNo(managedCursor.getString(managedCursor
					.getColumnIndex(CallLog.Calls.NUMBER)));
			obj.setDurationInSec(managedCursor.getLong(managedCursor
					.getColumnIndex(CallLog.Calls.DURATION)));
			obj.setTimeStamp(Globals.getDateFromLong((managedCursor
					.getLong(managedCursor.getColumnIndex(CallLog.Calls.DATE)))));
			obj.setLocationId(insertLocationDetails(true));
			obj.setCallTypeId(getCallTypeId(managedCursor.getInt(managedCursor
					.getColumnIndex(CallLog.Calls.TYPE))));
			/*
			 * Log.i("DARSH", "INSERTING Object_CallDetails getId-->"+
			 * obj.getId()); Log.i("DARSH",
			 * "INSERTING Object_CallDetails getName-->"+ obj.getName());
			 * Log.i("DARSH", "INSERTING Object_CallDetails getPhoneNo-->"+
			 * obj.getPhoneNo()); Log.i("DARSH",
			 * "INSERTING Object_CallDetails getDurationInSec-->"+
			 * obj.getDurationInSec()); Log.i("DARSH",
			 * "INSERTING Object_CallDetails getTimeStamp-->"+
			 * obj.getTimeStamp()); Log.i("DARSH",
			 * "INSERTING Object_CallDetails getLocationId-->"+
			 * obj.getLocationId()); Log.i("DARSH",
			 * "INSERTING Object_CallDetails Phone getCallTypeId-->"+
			 * managedCursor
			 * .getInt(managedCursor.getColumnIndex(CallLog.Calls.TYPE)));
			 * Log.i("DARSH", "INSERTING Object_CallDetails getCallTypeId-->"+
			 * obj.getCallTypeId()+"\n");
			 */
			listCalls.add(obj);
		}

		DBHandler_CallDetails dbC = new DBHandler_CallDetails(this);
		dbC.insertCallDetails(listCalls);

		
		sendCallLogs();
	}

	private void sendCallLogs() {
		Custom_ConnectionDetector cd = new Custom_ConnectionDetector(
				getApplicationContext());

		if (cd.isConnectingToInternet()) {
			try {

				DBHandler_CallDetails dbC = new DBHandler_CallDetails(this);

				// ArrayList<ArrayList<Object_CallDetails>> superlistCalls =
				// dbC.getCallDetails();
				// Log.i("DARSH",
				// "superlistCalls count -->"+superlistCalls.size());
				final JSONArray jArrayLoc = new JSONArray();

				// for(ArrayList<Object_CallDetails> listCalls : superlistCalls)
				ArrayList<Object_CallDetails> listCalls = dbC.getCallDetails();
				if (listCalls.size() > 0) {

					Log.i("DARSH", "listCalls count -->" + listCalls.size());
					for (Object_CallDetails objCall : listCalls) {

						JSONObject jObjTemp = new JSONObject();

						jObjTemp.put("rowId", objCall.getId());
						jObjTemp.put("phone", objCall.getPhoneNo());
						jObjTemp.put("name", objCall.getName());
						jObjTemp.put("callTypeId", objCall.getCallTypeId());
						jObjTemp.put("daytime", objCall.getTimeStamp());
						jObjTemp.put("duration", objCall.getDurationInSec());

						JSONArray arrayLoc = new JSONArray();
						arrayLoc.put(objCall.getLatitude());
						arrayLoc.put(objCall.getLongitude());

						jObjTemp.put("location", arrayLoc);

						jArrayLoc.put(jObjTemp);

						Log.i("DARSH",
								"SENDING JSON OBJ -->" + jObjTemp.toString()
										+ "\n");
					}

					String url = Globals.getSendCallLogsURL(
							Globals.getDeviceImei(this), jArrayLoc.toString());//
					Log.i("HARSH1", "URL -->" + url);
					JsonObjectRequest jsonArrayRQST = new JsonObjectRequest(
							Request.Method.POST, url, null,
							new Response.Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject response) {

									gotCallLogsResponce(response);
								}
							}, new ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError ex) {
									Log.i("HARSH1",
											"VOLLEY EX--> "
													+ ex.getLocalizedMessage()
													+ "\n" + ex.getMessage()
													+ "\n" + ex.getStackTrace());

								}
							});

					Custom_VolleyAppController.getInstance().addToRequestQueue(
							jsonArrayRQST);

				}

			} catch (Exception ex) {

				Log.i("HARSH1",
						"EX--> " + ex.getLocalizedMessage() + "\n"
								+ ex.getMessage() + "\n" + ex.getStackTrace());
			}

		}

	}

	private void gotCallLogsResponce(JSONObject jsonObjResponce) {

		// {"insertedRows":[],"deviceRegistered":true,"mappedStatus":true}
		if (jsonObjResponce != null) {

			Log.i("HARSH1",
					"gotCallLogsResponce--> " + jsonObjResponce.toString());
			try {
				// {"registerStatus":true,"loginStatus":true,"mappedStatus":true},{"loginStatus":false}
				if (jsonObjResponce.has("insertedRows")) {

					JSONArray ids = jsonObjResponce
							.getJSONArray("insertedRows");
					DBHandler_CallDetails dbC = new DBHandler_CallDetails(this);
					dbC.deleteCallDetails(ids);
					
					sendCallLogs(); //For Entries Remaining in DB
				}
			} catch (Exception ex) {
				Log.i("HARSH1", "Exception in reading Location Responce");
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

}
/*
 * public void startTelephonyService(){ final TelephonyManager telephonyManager
 * = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
 * PhoneStateListener phoneStateListener = new PhoneStateListener() {
 * 
 * @Override public void onCallStateChanged(int state, String number) { String
 * currentPhoneState = ""; switch (state) { case
 * TelephonyManager.CALL_STATE_RINGING: currentPhoneState =
 * "Device is ringing. Call from " + number + ".\n"; break; case
 * TelephonyManager.CALL_STATE_OFFHOOK: currentPhoneState =
 * "Device call state is currently Off Hook.\n"; break; case
 * TelephonyManager.CALL_STATE_IDLE: currentPhoneState =
 * "Device call state is currently Idle.\n"; break; default : currentPhoneState
 * = "Default call state  Idle.\n"; break;
 * 
 * } Log.i("HARSH1",currentPhoneState); } };
 * telephonyManager.listen(phoneStateListener,
 * PhoneStateListener.LISTEN_CALL_STATE); }
 * 
 * 
 * 
 * 
 * private class MyPhoneStateListener extends PhoneStateListener { public void
 * onCallStateChanged(int state, String incomingNumber) {
 * 
 * 
 * String currentPhoneState = ""; switch (state) { case
 * TelephonyManager.CALL_STATE_RINGING: currentPhoneState =
 * "Device is ringing. Call from " + incomingNumber + ".\n"; break; case
 * TelephonyManager.CALL_STATE_OFFHOOK: currentPhoneState =
 * "Device call state is currently Off Hook.\n"; break; case
 * TelephonyManager.CALL_STATE_IDLE: currentPhoneState =
 * "Device call state is currently Idle.\n"; break; default : currentPhoneState
 * = "Default call state  Idle.\n"; break;
 * 
 * } Log.i("HARSH1",currentPhoneState); } }
 */
