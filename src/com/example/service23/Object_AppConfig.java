package com.example.service23;

import android.content.Context;
import android.content.SharedPreferences;

public class Object_AppConfig {

	
	private Context context;
	
	private final String KEY_APP_CONFIG = "appConfig";
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor = null;
	
	public Object_AppConfig(Context context){
		
		this.context = context;
		prefs = this.context.getSharedPreferences(KEY_APP_CONFIG, 0);
		editor = prefs.edit();
	}
	
	public boolean get_Status() {
		boolean status = false ;
		
		if(prefs != null)
			status = prefs.getBoolean("appConfig_Firttime_Status",false);	
		
		return status;
	}
	public void set_Status(boolean status) {
		
		if (editor != null) {
			editor.putBoolean("appConfig_Firttime_Status", status);
			editor.commit();
		}

	}
}
