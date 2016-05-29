package com.example.service23;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.service.R;

public class Custom_Adapter_Call_Show extends BaseAdapter{

	private Context mContext;
	private ArrayList<Object_CallDetails> listCallDetails;
	public static MediaPlayer mediaPlayer;
	private View pause = null;
	private View play = null;
	
	// Gets the context so it can be used later
	public Custom_Adapter_Call_Show(Context c,ArrayList<Object_CallDetails> listCallDetails) {
		mContext = c;
		this.listCallDetails = listCallDetails;
	}

	// Total number of things contained within the adapter
	public int getCount() {
		return listCallDetails.size();
	}

	// Require for structure, not really used in my code.
	public Object getItem(int position) {
		return null;
	}

	// Require for structure, not really used in my code. Can
	// be used to get the id of an item in the adapter for
	// manual control.
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		
		if (convertView == null) {
	        // This a new view we inflate the new layout
			
	        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        convertView = inflater.inflate(R.layout.custom_row_call_show, parent, false);
	    }
		
		 final Object_CallDetails obj = listCallDetails.get(position);
		 TextView txtName = (TextView)convertView.findViewById(R.id.txtName);
		 TextView txtNumber = (TextView)convertView.findViewById(R.id.txtNumber);
		 TextView txtCalltype = (TextView)convertView.findViewById(R.id.txtCalltype);
		 TextView txtDuration = (TextView)convertView.findViewById(R.id.txtDuration);
		 TextView txtTime = (TextView)convertView.findViewById(R.id.txtTime);
		 final ImageView viewPlay = (ImageView)convertView.findViewById(R.id.imageViewPlay);
		 final ImageView viewPause = (ImageView)convertView.findViewById(R.id.imageViewPause);
		 LinearLayout linearUpload  = (LinearLayout)convertView.findViewById(R.id.linearisUpload);
		 
		 //Log.i("SUSHIL", "sushil is upload id "+obj.getIsUpload());
		 if(obj.getIsUpload()==1){
			 linearUpload.setBackgroundColor(mContext.getResources().getColor(R.color.app_green));
		 }else{
			 linearUpload.setBackgroundColor(mContext.getResources().getColor(R.color.app_red));
		 }
		 
		 if(obj.getName()!=null)
			 txtName.setText(obj.getName());
		 else
			 txtName.setText("");
		 
		 if(obj.getPhoneNo()!=null)
			 txtNumber.setText(obj.getPhoneNo());
		 if(obj.getCallTypeId()!=0){
			 if(obj.getCallTypeId()==1){
				 txtCalltype.setText("Incoming"); 
				 txtCalltype.setTextColor(mContext.getResources().getColor(R.color.app_green));
				 txtDuration.setTextColor(mContext.getResources().getColor(R.color.app_green));
				 txtTime.setTextColor(mContext.getResources().getColor(R.color.app_green));
			 }
			 else if(obj.getCallTypeId()==2){
				 txtCalltype.setText("Outgoing");
				 txtCalltype.setTextColor(mContext.getResources().getColor(R.color.app_orange));
				 txtDuration.setTextColor(mContext.getResources().getColor(R.color.app_orange));
				 txtTime.setTextColor(mContext.getResources().getColor(R.color.app_orange));
			 }
			 else if(obj.getCallTypeId()==3){
				 txtCalltype.setText("Missed"); 
				 txtCalltype.setTextColor(mContext.getResources().getColor(R.color.app_red));
				 txtDuration.setTextColor(mContext.getResources().getColor(R.color.app_red));
				 txtTime.setTextColor(mContext.getResources().getColor(R.color.app_red));
			 }
			 else if(obj.getCallTypeId()==4){
				 txtCalltype.setText("Cut");
				 txtCalltype.setTextColor(mContext.getResources().getColor(R.color.app_red));
				 txtDuration.setTextColor(mContext.getResources().getColor(R.color.app_red));
				 txtTime.setTextColor(mContext.getResources().getColor(R.color.app_red));
			 }
			 else if(obj.getCallTypeId()==5){
				 txtCalltype.setText("Unknown");
				 txtCalltype.setTextColor(mContext.getResources().getColor(R.color.app_gray));
				 txtDuration.setTextColor(mContext.getResources().getColor(R.color.app_gray));
				 txtTime.setTextColor(mContext.getResources().getColor(R.color.app_gray));
			 }
		 }
		 if(obj.getDurationInSec()!=0)
			 txtDuration.setText(timeConversion(obj.getDurationInSec()));
		 if(obj.getTimeStamp()!=null)
			 txtTime.setText(obj.getTimeStamp());
		 
		 if(obj.getCallRecording()!=null && !obj.getCallRecording().isEmpty()){
			 viewPlay.setVisibility(View.VISIBLE);
			 
		if(obj.getName()!=null)	 
		 Log.i("SUSHIL","get recording data file path "+obj.getCallRecording()+" recorder client name "+obj.getName());
		else
			Log.i("SUSHIL","get recording data file path "+obj.getCallRecording()+" recorder client name empty ");
			///final View viewLast =  getView(positonStopBtn, convertView, parent); 
		 viewPlay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					/*if(positonStopBtn!=position){
						ImageView viewPlayLast = (ImageView)viewLast.findViewById(R.id.imageViewPlay);
						ImageView viewPauseLast = (ImageView)viewLast.findViewById(R.id.imageViewPause);
						viewPauseLast.setVisibility(View.GONE);
						viewPlayLast.setVisibility(View.VISIBLE);
					}*/
					if(viewPause!=pause && pause!=null){
						pause.setVisibility(View.GONE);
						play.setVisibility(View.VISIBLE);
					}
					viewPause.setVisibility(View.VISIBLE);
					viewPlay.setVisibility(View.GONE);
					//playMp3(obj.getCallRecording());
					String path = obj.getCallRecording();
					play(path,viewPause,viewPlay);
					play = viewPlay;
					pause = viewPause;
				}
			});
			 viewPause.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						viewPause.setVisibility(View.GONE);
						viewPlay.setVisibility(View.VISIBLE);
						pauseAudio();
						/*play = viewPlay;
						pause = viewPause;*/
					}
				});
		 }else{
			 viewPlay.setVisibility(View.GONE);
			 viewPause.setVisibility(View.GONE);
		 }
		
	        // Now we can fill the layout with the right values
	     convertView.setClickable(true);
		 convertView.setOnClickListener(new  OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertShow(obj.getId(),obj.getCallRecording());
			}
		});
		 
		// positonStopBtn = position;
	    return convertView;
	   }
	
	
	private void alertShow(final long id,final String Selectedpath){
		
		String[] itemShare = {"Share audio","Delete","Delete all","Cancel"};
		String[] item = {"Delete","Delete All","Cancel"};
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Choose Action");
		
		
		if(Selectedpath!=null && !Selectedpath.isEmpty()){
		
		builder.setItems(itemShare, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				DBHandler_CallDetails db = new DBHandler_CallDetails(mContext);
				if(item==0){
					if(Selectedpath!=null && !Selectedpath.isEmpty())
					    shareFile(Selectedpath);
				}
				else if (item == 1) {
					db.delete(id);
				if(Selectedpath!=null && !Selectedpath.equals("")){
					File file = new File(Selectedpath);
					file.delete();
				}
					reFresh();
				} else if (item == 2) {
					db.deleteAll();
					delete();
					reFresh();
				}
				
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
		
		}else{
			
			builder.setItems(item, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					DBHandler_CallDetails db = new DBHandler_CallDetails(mContext);
					if (item == 0) {
						db.delete(id);
					if(Selectedpath!=null && !Selectedpath.equals("")){
						File file = new File(Selectedpath);
						file.delete();
					}
						reFresh();
					} else if (item == 1) {
						db.deleteAll();
						delete();
						reFresh();
					}
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
	
	
	
	
	
	
	/*private String getfilepath(byte[] mp3SoundByteArray) {
		String filepath = "";
	    try {
	        // create temp file that will hold byte array
	    	File audiofile = null;
	        File sampleDir = new File(
					Environment
							.getExternalStorageDirectory(),
					"/androidservice");
			if (!sampleDir.exists()) {
				sampleDir.mkdirs();
			}
			String file_name = "Record";
			try {
			audiofile = File.createTempFile(
						file_name, ".mp3", sampleDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
			audiofile.deleteOnExit();
	        FileOutputStream fos = new FileOutputStream(audiofile);
	        fos.write(mp3SoundByteArray);
	        fos.close();
           
	        filepath = audiofile.getAbsolutePath();
	       
	    } catch (IOException ex) {
	      
	        ex.printStackTrace();
	    }
	    return filepath;
	}
	*/
	private void play(String path,final View vPause,final View vplay){
		      /* Uri uri = Uri.parse(path);
	        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	        ((Activity)mContext).startActivity(intent);*/
		
		if(mediaPlayer!=null){
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		     mediaPlayer = new MediaPlayer();
        try {
			  mediaPlayer.setDataSource(path);
		      mediaPlayer.prepare();
              mediaPlayer.start();
              mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					vPause.setVisibility(View.GONE);
					vplay.setVisibility(View.VISIBLE);
					//delete();
				}
			});
        } catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
        	Toast.makeText(mContext, "Can't play this audio", Toast.LENGTH_SHORT).show();
        	vPause.setVisibility(View.GONE);
			vplay.setVisibility(View.VISIBLE);
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			Toast.makeText(mContext, "Can't play this audio", Toast.LENGTH_SHORT).show();
			vPause.setVisibility(View.GONE);
			vplay.setVisibility(View.VISIBLE);
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			Toast.makeText(mContext, "Can't play this audio", Toast.LENGTH_SHORT).show();
			vPause.setVisibility(View.GONE);
			vplay.setVisibility(View.VISIBLE);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(mContext, "Can't play this audio", Toast.LENGTH_SHORT).show();
			vPause.setVisibility(View.GONE);
			vplay.setVisibility(View.VISIBLE);
			e.printStackTrace();
		}
	}
	
	private void pauseAudio(){
		if(mediaPlayer!=null){
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
	
	 private String timeConversion(long totalSeconds) {
	        int hours = (int)totalSeconds / 60 / 60;
	        int minutes = (int)(totalSeconds - (hoursToSeconds(hours)))
	                / 60;
	        int seconds = (int)totalSeconds
	                - ((hoursToSeconds(hours)) + (minutesToSeconds(minutes)));

	        return stringComplete(hours) + ":" + stringComplete(minutes) + ":" + stringComplete(seconds);
	    }

	    private  int hoursToSeconds(int hours) {
	        return hours * 60 * 60;
	    }

	    private  int minutesToSeconds(int minutes) {
	        return minutes * 60;
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
	    
	    private void delete(){
			File dir = new File(
					Environment
					.getExternalStorageDirectory(),
			"/androidservice"); 
			//dir.delete()
		if (dir.isDirectory()) {
			        String[] children = dir.list();
			        for (int i = 0; i < children.length; i++) {
			            new File(dir, children[i]).delete();
			        }
			    }
		}
	    
	    private void shareFile(String filepath){
	    	    File file = new File(filepath);
	    	    //Uri uri = Uri.parse(filepath);
	    	    Intent share = new Intent(Intent.ACTION_SEND);
	    	    share.setType("audio/*");
	    	    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
	    	    share.putExtra(Intent.EXTRA_SUBJECT, "Call tracker audio");
	    	    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
	    	    share.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
	    	    //share.setPackage(packageName)
	    	    ((Activity)mContext).startActivity(Intent.createChooser(share, "Audio file here"));
	    	    
	    }
	    
	    
	    private void reFresh(){
            if(mediaPlayer!=null){
	    		mediaPlayer.stop();
	    		mediaPlayer.release();
	    		mediaPlayer = null;
	    	}
	    	Intent i = new Intent(mContext,Activity_Details_Show.class);
	    	((Activity)mContext).startActivity(i);
	    	
	    	((Activity)mContext).finish();
	    }
	    
	}
