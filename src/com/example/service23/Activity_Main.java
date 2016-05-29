package com.example.service23;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.service.R;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Main extends Activity {
	    private static final int ADMIN_INTENT = 15;
		private static final String description = "This app supports system level components";
		private ComponentName mComponentName;
		private boolean loopMapStatus = true;
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);  
	      setContentView(R.layout.activity_main);
	     
	      	
		   TextView txt = (TextView)findViewById(R.id.txtDeviceId);
		   txt.setText(Globals.getDeviceImei(this));
		   
	      new Thread(new Runnable() {
				public void run() {
					while (loopMapStatus) {
						try {
							Thread.sleep(10000);
							checkMapStatus();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
	      
	   }

	   public void checkMapStatus(){
		   
		   String status = Globals.getMappingStatus(this);
		  // status ="true";
		  if(status.trim().equals("true")){
			   
		   	  mComponentName = new ComponentName(this, Custom_AdminReceiver.class); 
		   	  
		   	  //REGISTER AS DIVICE ADMIN
		      Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		      intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
		      intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,description);
		      startActivityForResult(intent, ADMIN_INTENT);
		      
		    //HIDE APP ICON
		      /* CODE MOVED TO LOGIN
		      PackageManager packageManager = Activity_Main.this.getPackageManager();
		      ComponentName componentName = new ComponentName(Activity_Main.this,Activity_Main.class);
		      packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
		      
			*/
		     
		    //TRY TO ENABLE GPS
		      /*
		      try{
				   Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
				   intent.putExtra("enabled", true);
				   sendBroadcast(intent);
				   }catch(Exception ex){
					   
				   }
		      */
		      
		      //START SERVICE
		      startService(new Intent(getBaseContext(), Custom_Service.class));
				  
		      loopMapStatus = false;
		      //start Activity show details
		      Globals.setBool(true, this);
			  this.finish();
			  /*Intent i = new Intent(Activity_Main.this,Activity_Details_Show.class);
		      startActivity(i);*/
		      
		   }else{
			   
			  // Make server call to fetch map status
			   
			   try{
				   
					String url = Globals.getMapStatusURL(Globals.getDeviceImei(this));
					Log.i("HARSH1", url);
					JsonObjectRequest  jsonArrayRQST = new JsonObjectRequest(Request.Method.POST,url, null,  new Response.Listener<JSONObject>() {
				        @Override
				        public void onResponse(JSONObject response) {
				             gotMapStatusResponce(response);
				        }
				    }, new ErrorListener() {
						

						@Override
						public void onErrorResponse(VolleyError ex) {
							Toast.makeText(
									Activity_Main.this,
									"Error! Please try again.",
									Toast.LENGTH_SHORT).show();
							Log.i("HARSH1", "VOLLEY EX--> "+ex.getLocalizedMessage()+"\n"+ex.getMessage()+"\n"+ex.getStackTrace());
						
					
							
						}});
				
				Custom_VolleyAppController.getInstance().addToRequestQueue(
						jsonArrayRQST);
				}catch(Exception ex){
					Toast.makeText(
							Activity_Main.this,
							"Exception",
							Toast.LENGTH_SHORT).show();

					Log.i("HARSH1", "EX--> "+ex.getLocalizedMessage()+"\n"+ex.getMessage()+"\n"+ex.getStackTrace());
				}

		   }
	   }
	   
	   private void gotMapStatusResponce(JSONObject response){		
			String err =null;
			if(response != null){
				String jsonString = response.toString();
				Log.i("HARSH1", "JSON--> "+jsonString);
				if(jsonString != null && !jsonString.trim().equals(""))
				{
					try {
						//{"registerStatus":true,"loginStatus":true,"mappedStatus":true},{"loginStatus":false}
						
						JSONObject jsonObj = new JSONObject(jsonString);
					
								
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
									}
								}
						
					} catch (JSONException e) {
						err = "Exception Occured";
					}
					
				}else{
					err = "Responce is empty";
				}
				
			}else{
				err = "Responce is null";
			}
			if(err != null){
				Toast.makeText(
						this
						,err,
						Toast.LENGTH_SHORT).show();
			}
			
		}
	  
	   //Method to stop the service
	   public void stopService(View view) {
	      stopService(new Intent(getBaseContext(), Custom_Service.class));
	      
	      if(mComponentName != null){
	    	  DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager)getSystemService(  
					Context.DEVICE_POLICY_SERVICE);  
	    	  mDevicePolicyManager.removeActiveAdmin(mComponentName);  
	    	  
	      }
	      
	      /*
	      PackageManager packageManager = this.getPackageManager();
	      ComponentName componentName = new ComponentName(this,Activity_Main.class);
	      packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
	      */
	   	
	   }
	}