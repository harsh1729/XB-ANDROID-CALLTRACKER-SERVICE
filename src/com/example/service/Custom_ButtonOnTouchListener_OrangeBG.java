package com.example.service;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class Custom_ButtonOnTouchListener_OrangeBG implements OnTouchListener {

	final View view;
	Context context;

	public Custom_ButtonOnTouchListener_OrangeBG(final View view , Context context ) {
		super();
		this.view = view;
		this.context = context;
	}

	public boolean onTouch(final View view, final MotionEvent motionEvent) {
		Button btn =  (Button)view;
		if(btn!= null)
			switch (motionEvent.getAction()) {

			case MotionEvent.ACTION_DOWN:      	  
				//btn.setTextColor(this.context.getResources().getColor(R.color.app_white));
				//btn.setBackgroundColor(this.context.getResources().getColor(R.color.app_green_dark));
				btn.setBackgroundResource(R.drawable.button_orange_pressed);
				break;

			case MotionEvent.ACTION_UP: 
				btn.setBackgroundResource(R.drawable.button_orange);
				break;

			case MotionEvent.ACTION_CANCEL: 
				btn.setBackgroundResource(R.drawable.button_orange);
				break;

			default   :
				break;

			}
		return false;
	}

}
