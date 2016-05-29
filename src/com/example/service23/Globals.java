package com.example.service23;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;


public class Globals {
	  
	static final int CALL_TYPE_INCOMING = 1; 
	static final int CALL_TYPE_OUTGOING= 2; 
	static final int CALL_TYPE_MISSED = 3; 
	static final int CALL_TYPE_CUT = 4; 
	static final int CALL_TYPE_UNKNOWN = 5; 
	
	static final int NO_LOCATION = 333; 
	static final String DEFAULT_APP_SERVER_PATH ="http://r3narang.in/agents/";
	//http://xercesblue.in/calltrackerTest/agents/
	public static final int VOLLEY_TIMEOUT_MILLISECS = 5000;
	
	static public Point getAppButtonSize(Activity context ){
		
		int screenWidth = Globals.getScreenSize(context).x;
		
		Point size = new Point();
		
		size.x = 4*screenWidth/10;
		size.y = size.x/3;
		
		return size;
	}
	@SuppressLint("NewApi")
	static public Point getScreenSize(Activity currentActivity){
		Display display = currentActivity.getWindowManager().getDefaultDisplay();
		Point size = new Point();


		if (android.os.Build.VERSION.SDK_INT >= 13) 
		{
			display.getSize(size);
		} 
		else 
		{
			 size.x = display.getWidth();
			 size.y = display.getHeight();
		}
		
		return size;
	}
static public int getAppFontSize(Activity context){
		
		
		return (getScreenSize(context).x/120 + 12);
	}
	
static public int getAppFontSize_Small(Activity context){
		
		return (getScreenSize(context).x/120 + 10);
	}
	static public int getAppFontSize_Large(Activity context){
		
		return (getScreenSize(context).x/120 + 15);
	}
	
	static public Bitmap scaleToWidth(Bitmap bitmap,int scaledWidth) {
		if (bitmap != null) {

			int bitmapHeight = bitmap.getHeight(); 
			int bitmapWidth = bitmap.getWidth(); 

			// scale According to WIDTH
			int scaledHeight = (scaledWidth * bitmapHeight) / bitmapWidth;

			try {

				bitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth,
						scaledHeight, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}
	
	public static String getLoginURL(String username, String password, String deviceId){
		//login.php?username=jaspal.singh&password=password&deviceId=2147483647
		
		String url = DEFAULT_APP_SERVER_PATH+"login.php?username=";
		
		try {
			String query = URLEncoder.encode(username+ "&password=" + password + "&deviceId="+ deviceId+"&deviceName="+getDeviceName(), "utf-8");
			url+= query;
		} catch (UnsupportedEncodingException e) {
			Log.i("HARSH1", "Exception getSendLocationURL");
		}
		//return  url;
		 return Globals.getValidURL(DEFAULT_APP_SERVER_PATH+"login.php?username="+username+ "&password=" + password + "&deviceId="+ deviceId+"&deviceName="+getDeviceName()); 
	}
	
	public static String getMapStatusURL(String deviceId){
		//calltracker/agents/getmappedstatus.php?deviceId=123456789
		
		return  Globals.getValidURL(DEFAULT_APP_SERVER_PATH+"getmappedstatus.php?deviceId="+ deviceId);
	}
	
	public static String  getSendLocationURL(String deviceId,String locArray){//

		return Globals.getValidURL(DEFAULT_APP_SERVER_PATH+"updateLocation.php?deviceId="+ deviceId+"&locationArray="+locArray);
		//+"&locationArray="+locArray)
	}
	
	public static String  getSendCallLogsURL(String deviceId){

		return  Globals.getValidURL(DEFAULT_APP_SERVER_PATH+"updateCallLog.php?deviceId="+ deviceId);
				//+"&callLog="+callLogsArray.toString()+"&file = "+file);
	}
	public static String  getSendCallLogsAudio(String deviceId){

		return  Globals.getValidURL(DEFAULT_APP_SERVER_PATH+"uploadAudio.php?deviceId="+ deviceId);
				//+"&callLog="+callLogsArray.toString()+"&file = "+file);
	}
	
	public static String getMappingStatus(Context mContext){
		
		SharedPreferences prefs = mContext.getSharedPreferences("appdata", 0);		
		return prefs.getString("mapstatus", "false");		
	}
	
	public static void setMappingStatus(String status,Context mContext){
		
		SharedPreferences prefs = mContext.getSharedPreferences("appdata", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("mapstatus", status);
		editor.commit();
	}
	
  public static long getLastCallId(Context mContext){
		
		SharedPreferences prefs = mContext.getSharedPreferences("appdata", 0);		
		return prefs.getLong("lastcallid", 0);		
	}
	
	public static void setLastCallId(long id,Context mContext){
		
		SharedPreferences prefs = mContext.getSharedPreferences("appdata", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong("lastcallid", id);
		editor.commit();
	}
	
   public static boolean getBool(Context mContext){
		
		SharedPreferences prefs = mContext.getSharedPreferences("appdata", 0);		
		return prefs.getBoolean("type", false);		
	}
  public static	void setBool(boolean type,Context mContext){
	  SharedPreferences prefs = mContext.getSharedPreferences("appdata", 0);
	  SharedPreferences.Editor editor = prefs.edit();
	  editor.putBoolean("type", type);
	  editor.commit();
  }
	
	public static String getDeviceImei(Context con){
		TelephonyManager telephonyManager = (TelephonyManager)con.getSystemService(Context.TELEPHONY_SERVICE);
		   String deviceImei = telephonyManager.getDeviceId();
		   if(deviceImei == null){
				deviceImei ="Unknown";
			}
		   
		   return deviceImei;
	}
	
	public static String getDeviceName() {
		String name = "Unknown";
		
	    String manufacturer = Build.MANUFACTURER;
	    String model = Build.MODEL;
	    //String product = Build.PRODUCT;
	    if (model.startsWith(manufacturer)) {
	    	name = capitalize(model);
	    	
	    } else {
	    	name = capitalize(manufacturer) + " " + model;
	    }
	    
	    //if(product !=null)
	    	//name+="_"+product;
	   //name = name.replace(" ", "_");
	    
	   return name;
	}


	public static String capitalize(String s) {
	    if (s == null || s.length() == 0) {
	        return "";
	    }
	    char first = s.charAt(0);
	    if (Character.isUpperCase(first)) {
	        return s;
	    } else {
	        return Character.toUpperCase(first) + s.substring(1);
	    }
	} 
	
	public static String getCurrentTime(){
		String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).format(new Date());
		
		Log.i("HARSH1", "currentDateandTime --> "+currentDateandTime);
		return currentDateandTime;
		
	}
	
	public static String getDateFromLong(long secs){
		String dateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).format(new Date(secs));
		
		Log.i("HARSH1", "long Date --> "+dateandTime);
		return dateandTime;
		
	}
	
	public static String getValidURL(String urlStr){
		 //"http://abc.dev.domain.com/0007AC/ads/800x480 15sec h.264.mp4";
		URL url = null;
		try {
			url = new URL(urlStr);
			URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			url = uri.toURL();
			
			//Log.i("HARSH111", uri.toString());
		} catch (MalformedURLException e) {
		} catch (URISyntaxException e) {
		}
		
		if(url != null)
			return url.toString();
		
		Log.i("HARSH", "Exception getValidURL");
		return "";
	}
	public static String getDate(){	 
		final Calendar c = Calendar.getInstance();
	    int mYear = c.get(Calendar.YEAR);
	    int mMonth = c.get(Calendar.MONTH);
	    int mDay = c.get(Calendar.DAY_OF_MONTH);
		return ""+mYear+"-"+Add(mMonth+1)+"-"+Add(mDay);
	}
	private static String Add(int c){
		String add = "";
		if(c<10){
			add = "0"+c;
		}
		else{
			add = ""+c;
		}
		return add;
		
	}
	
	public static HashMap< String, String>  getParams_UploadContentCallLogParams(Context con,String json){
		
		 //Object_AppConfig objConfig = new Object_AppConfig(con);
			HashMap< String, String> map = new HashMap<String, String>();
			   map.put("callLog",json);
				Log.i("SUSHIL",map.toString());
			return map;
		}
	public static HashMap< String, String>  getParams_UploadLocationParams(Context con,String json){
		//String jsonmain = json.toString().substring(0, json.toString().length()-1);
		 //Object_AppConfig objConfig = new Object_AppConfig(con);
			HashMap< String, String> map = new HashMap<String, String>();
				map.put("locationArray",json);
				Log.i("SUSHIL",map.toString());
			return map;
		}
	
	public static HashMap< String, String>  getParams_UploadAudioStringParams(Context con,String commaSeperatedKeys){
		
		HashMap< String, String> map = new HashMap<String, String>();
				//map.put("cliendId", Globals.CLIENT_ID+"");
				map.put("deviceId",getDeviceImei(con));
				map.put("keys",commaSeperatedKeys);
				 Log.i("SUSHIL", "getParams_uploadImage --->" + map);
			return map;
		}
}
