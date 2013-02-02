package com.suni.minimal_uhr;

import com.suni.minimal_uhr.R;


import android.preference.Preference;

import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;



import android.app.Activity;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;


import android.os.Bundle;

public class PrefActivity extends PreferenceActivity implements OnPreferenceClickListener {

int mAppWidgetId;
Dialog d;
final String FontSizePrefKey ="FS";
String key="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setResult(Activity.RESULT_CANCELED);		
		Intent intent = getIntent();
	     Bundle extras = intent.getExtras();
	     if (extras != null) {
	         mAppWidgetId = extras.getInt(
	                 AppWidgetManager.EXTRA_APPWIDGET_ID,
	                 AppWidgetManager.INVALID_APPWIDGET_ID);
	     }		
	     addPreferencesFromResource(R.xml.pref);
			Preference fg = findPreference("FG");
			Preference bg = findPreference("BG");
			Preference bg2 = findPreference("BG2");
			Preference FS =findPreference("FS");
			fg.setOnPreferenceClickListener(this);
			bg.setOnPreferenceClickListener(this);
			bg2.setOnPreferenceClickListener(this);
			FS.setOnPreferenceClickListener(this);
		
	    }

		
			public boolean onPreferenceClick(Preference p) {
				// TODO Auto-generated method stuff
				
					SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
					key=p.getKey();
					int c = pref.getInt(key, ColorPickerPreference.getDefaultColor(key));

					Intent i = new Intent(this, ColorPickerActivity.class);
					i.putExtra("key",key);
					i.putExtra("EXTRA_COLOR",c);
					startActivity(i);
				
			return true;
	   
	}
	
	@Override
	public void onBackPressed() {
	
		setResult(RESULT_OK, new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId));
		Intent i = new Intent("com.suni.minimal_uhr.Update");
		sendBroadcast(i);
		
		super.onBackPressed();
	}
	

	
}


