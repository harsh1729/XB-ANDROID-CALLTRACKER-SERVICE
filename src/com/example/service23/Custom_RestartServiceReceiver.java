package com.example.service23;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Custom_RestartServiceReceiver extends BroadcastReceiver {
	// private static final String TAG = "RestartServiceReceiver";
	Context mContext;
	//private final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		 Log.e("TAG", "onReceive");
		  //  context.startService(new Intent(context.getApplicationContext(),BackgroundService.class));
		    mContext = context;
			String action = intent.getAction();
			if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
	                        //check for boot complete event & start your service
				startService();
			}

	}
		private void startService() {
                //here, you will start your service
			Intent mServiceIntent = new Intent(mContext.getApplicationContext(),Custom_Service.class);
			mServiceIntent.setAction("com.bootservice.test.DataService");
			mContext.startService(mServiceIntent);
	}

}
