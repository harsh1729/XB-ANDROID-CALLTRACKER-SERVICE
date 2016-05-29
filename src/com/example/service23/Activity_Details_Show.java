package com.example.service23;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.service.R;

public class Activity_Details_Show extends Activity {

	TextView txtDate;
	private int year;
	private int month;
	private int day;
	static final int DATE_DIALOG_ID = 333;
	private static final AttributeSet DatePickerDialog = null;
	private Custom_Adapter_Call_Show adaptor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_show);

		inti();
	}

	private void inti() {
		// startService(new Intent(getBaseContext(), Custom_Service.class));
       setCurrentDateOnview();
       setDataList(txtDate.getText().toString());
		ImageView imagedate = (ImageView)findViewById(R.id.imagedate);
		imagedate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);
			}
		});
		ImageView imageRefresh = (ImageView)findViewById(R.id.imageRefresh);
		imageRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Refresh();
			}
		});
		
	}
	
	
	private void Refresh(){
		if(adaptor.mediaPlayer!=null){
			adaptor.mediaPlayer.stop();
			adaptor.mediaPlayer.release();
			adaptor.mediaPlayer = null;
		}
		Intent i = new Intent(this,Activity_Details_Show.class);
		startActivity(i);
		this.finish();
	}

	private void setDataList(String date){
		if(adaptor.mediaPlayer!=null){
			adaptor.mediaPlayer.stop();
			adaptor.mediaPlayer.release();
			adaptor.mediaPlayer = null;
		}
		DBHandler_CallDetails dbhCall = new DBHandler_CallDetails(this);
		ArrayList<Object_CallDetails> listCallDetails = dbhCall
				.getAllCallDetails(date);
		ListView list = (ListView) findViewById(R.id.listViewDetails);
		TextView txtContent = (TextView)findViewById(R.id.noContent);
        if(listCallDetails.size()!=0){
		adaptor = new Custom_Adapter_Call_Show(this,
				listCallDetails);
		txtContent.setVisibility(View.GONE);
		list.setVisibility(View.VISIBLE);
		list.setAdapter(adaptor);
        }else{
        	list.setVisibility(View.GONE);
        	txtContent.setVisibility(View.VISIBLE);
        }
	}
	public void setCurrentDateOnview() {

		txtDate = (TextView) findViewById(R.id.dateView);
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		//txtDate.setText(month + 1 + "-" + day + "-" + year);
		txtDate.setText(stringComplete(year)+"-"+stringComplete(month+1)+"-"+stringComplete(day));

		// dpresult.init(year, month, day, null);

	}

	 private String stringComplete(int c){
	    	String st = "";
	    	if(c<10){
	    		st = "0"+c;
	    	}else{
	    		st = c+"";
	    	}
	    	return st;
	    }
	 
	 protected Dialog onCreateDialog(int id){
			switch (id){
			case DATE_DIALOG_ID:
				return new DatePickerDialog(this,datePickerListener,year,month,day);
			}
			return null;
		}
		private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener(){
			
			public void onDateSet(DatePicker view,int selectedYear,int selectedMonth,int selectedDay) {
				year= selectedYear;
				month= selectedMonth;
				day =selectedDay;
				txtDate.setText(stringComplete(year)+"-"+stringComplete(month+1)+"-"+stringComplete(day));
				//dpresult.init(year, month, day, null);
				setDataList(txtDate.getText().toString());
			}
		};
		
				
			
	public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
		}

}
