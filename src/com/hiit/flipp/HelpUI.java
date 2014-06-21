package com.hiit.flipp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class HelpUI extends Activity {

	
	/***************** Declaration Regarding Menu Section**********************/
	ImageView menuIcon;
	RelativeLayout menu;
	RelativeLayout mainLayout;
	private boolean checkMenuStatus;
	Button profile, billing, help, logout;
	
	private void InitializeComponant()
	{
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
	}
	
}
