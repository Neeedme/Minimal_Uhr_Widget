package com.suni.minimal_uhr;

import java.util.Calendar;

import com.suni.minimal_uhr.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public  class UpdateService extends Service {
	private float fontSize;
	private int inactive,active,background;
	@Override
	public void onCreate() {
		 
		BroadcastReceiver bReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	        	
	        AppWidgetManager awm = AppWidgetManager.getInstance(context);
	   		   
	    		 int[] ids = awm.getAppWidgetIds(new ComponentName(context, SUNIWidget.class));
	    		 Intent i = new Intent(context,com.suni.minimal_uhr.UpdateService.class);
	    		 i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
	    		 
				 context.startService(i);
				 Log.d("OnRecieve","ACtionTimeTick");
	        		
	        		Intent intent1 = new Intent(getApplicationContext(), com.suni.minimal_uhr.UpdateService.class);
	        		PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, intent1, 0);
	        		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	        		alarm.setRepeating(AlarmManager.RTC, System.currentTimeMillis()+500,60000, pintent);
	        }
	    };
	    registerReceiver(bReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
	    } 
	
	
	@Override
    public int onStartCommand(Intent intent,int flag , int startId) {
		refreshWidget(intent);
		return START_NOT_STICKY;
    }
    
	public void refreshWidget(Intent intent) {
		Log.d("UPDATE", "RUNNING");
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		 active=pref.getInt("FG",R.color.defaultForeground);
		 fontSize=pref.getFloat("FS",15);
		 inactive=pref.getInt("BG",R.color.defaultInactive);
		 background=pref.getInt("BG2",R.color.defaultBackground);
		
		
		 Calendar zeit = Calendar.getInstance(); 
		zeit.setTimeInMillis(System.currentTimeMillis());
   	int h = zeit.get(Calendar.HOUR_OF_DAY);
       int m = zeit.get(Calendar.MINUTE);
        
       // Build the widget update for today
       RemoteViews  v = new RemoteViews(this.getPackageName(), R.layout.widget);
       resetColor(v);
       Aktualisieren(h,m,v);
       Schriftgroesse(v);
       
       

       PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,getAlarmPackage(this), 0);
       v.setOnClickPendingIntent(R.id.layout, pendingIntent);
       v.setInt(R.id.layout,"setBackgroundColor", background);
  

       AppWidgetManager manager = AppWidgetManager.getInstance(this);
       int[] AWID = manager.getAppWidgetIds(new ComponentName(getApplicationContext(), SUNIWidget.class));
       
       
       Intent clickIntent = new Intent(this.getApplicationContext(),
  	          UpdateService.class);
  	      clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
  	      clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
           AWID);
  	      PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, clickIntent,
  	          PendingIntent.FLAG_UPDATE_CURRENT);
  	      v.setOnClickPendingIntent(R.id.bg1, pIntent);
       
       if(AWID!=null)
       	manager.updateAppWidget(AWID, v);
       else {
       	ComponentName c = new ComponentName("com.suni.minimal_uhr","SUNIWidget");
       	manager.updateAppWidget(c, v);
       }
	}
    public Intent getAlarmPackage(Context context)
    {
    	PackageManager packageManager = context.getPackageManager();
    	Intent AlarmClockIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);

    	String clockImpls[][] = {
    			{ "Standard Alarm", "com.android.alarmclock",
    				"com.android.alarmclock.AlarmClock" },
    			{ "HTC Alarm ClockDT", "com.htc.android.worldclock",
    				"com.htc.android.worldclock.WorldClockTabControl" },
    			{ "Standard Alarm ClockDT", "com.android.deskclock",
    				"com.android.deskclock.AlarmClock" },
    			{ "Froyo Nexus Alarm ClockDT",
    				"com.google.android.deskclock",
    				"com.android.deskclock.DeskClock" },
    			{ "Moto Blur Alarm ClockDT",
    				"com.motorola.blur.alarmclock",
    				"com.motorola.blur.alarmclock.AlarmClock" },
    			{ "Samsung Galaxy S", "com.sec.android.app.clockpackage",
    			"com.sec.android.app.clockpackage.ClockPackage" } };

    	boolean foundClockImpl = false;

    	for (int i = 0; i < clockImpls.length; i++)
    	{
    		String packageName = clockImpls[i][1];
    		String className = clockImpls[i][2];
    		try
    		{
    			ComponentName cn = new ComponentName(packageName, className);
    			packageManager.getActivityInfo(cn,PackageManager.GET_META_DATA);
    			AlarmClockIntent.setComponent(cn);
    			foundClockImpl = true;
    		} catch (NameNotFoundException nf) {}
    	}
    	if (foundClockImpl)
    		return AlarmClockIntent;
    	else
    		return null;
    }	
    
    @Override
    public IBinder onBind(Intent intent) {
    	// TODO Auto-generated method stub
    	return null;
    }
    
    
    public  void resetColor(RemoteViews v){
		v.setTextColor(R.id.w1, inactive);v.setTextColor(R.id.w41, inactive);v.setTextColor(R.id.w81, inactive);
		v.setTextColor(R.id.w2, inactive);v.setTextColor(R.id.w42, inactive);v.setTextColor(R.id.w82, inactive);
		v.setTextColor(R.id.w3, inactive);v.setTextColor(R.id.w43, inactive);v.setTextColor(R.id.w83, inactive);
		v.setTextColor(R.id.w4, inactive);v.setTextColor(R.id.w44, inactive);v.setTextColor(R.id.w84, inactive);
		v.setTextColor(R.id.w5, inactive);v.setTextColor(R.id.w45, inactive);v.setTextColor(R.id.w85, inactive);
		v.setTextColor(R.id.w6, inactive);v.setTextColor(R.id.w46, inactive);v.setTextColor(R.id.w86, inactive);
		v.setTextColor(R.id.w7, inactive);v.setTextColor(R.id.w47, inactive);v.setTextColor(R.id.w87, inactive);
		v.setTextColor(R.id.w8, inactive);v.setTextColor(R.id.w48, inactive);v.setTextColor(R.id.w88, inactive);
		v.setTextColor(R.id.w9, inactive);v.setTextColor(R.id.w49, inactive);v.setTextColor(R.id.w89, inactive);
		v.setTextColor(R.id.w10, inactive);v.setTextColor(R.id.w50, inactive);v.setTextColor(R.id.w90, inactive);
		v.setTextColor(R.id.w11, inactive);v.setTextColor(R.id.w51, inactive);v.setTextColor(R.id.w91, inactive);
		v.setTextColor(R.id.w12, inactive);v.setTextColor(R.id.w52, inactive);v.setTextColor(R.id.w92, inactive);
		v.setTextColor(R.id.w13, inactive);v.setTextColor(R.id.w53, inactive);v.setTextColor(R.id.w93, inactive);
		v.setTextColor(R.id.w14, inactive);v.setTextColor(R.id.w54, inactive);v.setTextColor(R.id.w94, inactive);
		v.setTextColor(R.id.w15, inactive);v.setTextColor(R.id.w55, inactive);v.setTextColor(R.id.w95, inactive);
		v.setTextColor(R.id.w16, inactive);v.setTextColor(R.id.w56, inactive);v.setTextColor(R.id.w96, inactive);
		v.setTextColor(R.id.w17, inactive);v.setTextColor(R.id.w57, inactive);v.setTextColor(R.id.w97, inactive);
		v.setTextColor(R.id.w18, inactive);v.setTextColor(R.id.w58, inactive);v.setTextColor(R.id.w98, inactive);
		v.setTextColor(R.id.w19, inactive);v.setTextColor(R.id.w59, inactive);v.setTextColor(R.id.w99, inactive);
		v.setTextColor(R.id.w20, inactive);v.setTextColor(R.id.w60, inactive);v.setTextColor(R.id.w100, inactive);
		v.setTextColor(R.id.w21, inactive);v.setTextColor(R.id.w61, inactive);v.setTextColor(R.id.w101, inactive);
		v.setTextColor(R.id.w22, inactive);v.setTextColor(R.id.w62, inactive);v.setTextColor(R.id.w102, inactive);
		v.setTextColor(R.id.w23, inactive);v.setTextColor(R.id.w63, inactive);v.setTextColor(R.id.w103, inactive);
		v.setTextColor(R.id.w24, inactive);v.setTextColor(R.id.w64, inactive);v.setTextColor(R.id.w104, inactive);
		v.setTextColor(R.id.w25, inactive);v.setTextColor(R.id.w65, inactive);v.setTextColor(R.id.w105, inactive);
		v.setTextColor(R.id.w26, inactive);v.setTextColor(R.id.w66, inactive);v.setTextColor(R.id.w106, inactive);
		v.setTextColor(R.id.w27, inactive);v.setTextColor(R.id.w67, inactive);v.setTextColor(R.id.w107, inactive);
		v.setTextColor(R.id.w28, inactive);v.setTextColor(R.id.w68, inactive);v.setTextColor(R.id.w108, inactive);
		v.setTextColor(R.id.w29, inactive);v.setTextColor(R.id.w69, inactive);v.setTextColor(R.id.w109, inactive);
		v.setTextColor(R.id.w30, inactive);v.setTextColor(R.id.w70, inactive);v.setTextColor(R.id.w110, inactive);
		v.setTextColor(R.id.w31, inactive);v.setTextColor(R.id.w71, inactive);
		v.setTextColor(R.id.w32, inactive);v.setTextColor(R.id.w72, inactive);
		v.setTextColor(R.id.w33, inactive);v.setTextColor(R.id.w73, inactive);
		v.setTextColor(R.id.w34, inactive);v.setTextColor(R.id.w74, inactive);
		v.setTextColor(R.id.w35, inactive);v.setTextColor(R.id.w75, inactive);
		v.setTextColor(R.id.w36, inactive);v.setTextColor(R.id.w76, inactive);
		v.setTextColor(R.id.w37, inactive);v.setTextColor(R.id.w77, inactive);
		v.setTextColor(R.id.w38, inactive);v.setTextColor(R.id.w78, inactive);
		v.setTextColor(R.id.w39, inactive);v.setTextColor(R.id.w79, inactive);
		v.setTextColor(R.id.w40, inactive);v.setTextColor(R.id.w80, inactive);
		
		
		
	}
	
	 
		 
	public   void Aktualisieren(int hour,int min, RemoteViews v) {
		v.setTextColor(R.id.w1,active);
        v.setTextColor(R.id.w2,active);

        v.setTextColor(R.id.w4,active);
        v.setTextColor(R.id.w5,active);
        v.setTextColor(R.id.w6,active);
    	
      
        
        int rest = min%5;
        min = min - rest;
        if(min>15) hour+=1;

        if(hour > 12) hour -=12;
        
        if(hour==0) hour =12;
        if(min==15) {
          v.setTextColor(R.id.w41,active);
          v.setTextColor(R.id.w42,active);
          v.setTextColor(R.id.w43,active);
          v.setTextColor(R.id.w44,active);
        }
        if(min==45) {
          v.setTextColor(R.id.w38,active);
          v.setTextColor(R.id.w39,active);
          v.setTextColor(R.id.w40,active);

        }
        if((min>19 && min<30)||min>45) {
          v.setTextColor(R.id.w19,active);
          v.setTextColor(R.id.w20,active);
          v.setTextColor(R.id.w21,active);
         }
        // ist es vor , nach oder punkt??
        else if((min>0 && min<15) || (min>30&&min<45)) {
          v.setTextColor(R.id.w23,active);
          v.setTextColor(R.id.w24,active);
          v.setTextColor(R.id.w25,active);
          v.setTextColor(R.id.w26,active);
         
        }
        if(min>=20 && min<=40){
          v.setTextColor(R.id.w34,active);
          v.setTextColor(R.id.w35,active);
          v.setTextColor(R.id.w36,active);
          v.setTextColor(R.id.w37,active);
        
    }
        Anzeige(hour,min,v);
        getRest(rest,v);
    }
	
	public  void Anzeige(int H, int M,RemoteViews v) {
		
	        switch(H) {
	        case 1: { v.setTextColor(R.id.w45,active);
	                  v.setTextColor(R.id.w46,active);
	                  v.setTextColor(R.id.w47,active);
	                  v.setTextColor(R.id.w48,active);break;}
	        case 2: { v.setTextColor(R.id.w52,active);
	                  v.setTextColor(R.id.w53,active);
	                  v.setTextColor(R.id.w54,active);
	                  v.setTextColor(R.id.w55,active);break;}
	        case 3: { v.setTextColor(R.id.w56,active);
	                  v.setTextColor(R.id.w57,active);
	                  v.setTextColor(R.id.w58,active);
	                  v.setTextColor(R.id.w59,active);break;}
	        case 4: { v.setTextColor(R.id.w63,active);
	                  v.setTextColor(R.id.w64,active);
	                  v.setTextColor(R.id.w65,active);
	                  v.setTextColor(R.id.w66,active);break;}
	        case 5: { v.setTextColor(R.id.w67,active);
	                  v.setTextColor(R.id.w68,active);
	                  v.setTextColor(R.id.w69,active);
	                  v.setTextColor(R.id.w70,active);break;}
	        case 6: { v.setTextColor(R.id.w73,active);
	                  v.setTextColor(R.id.w74,active);
	                  v.setTextColor(R.id.w75,active);
	                  v.setTextColor(R.id.w76,active);
	                  v.setTextColor(R.id.w77,active);break;}
	        case 7: { v.setTextColor(R.id.w78,active);
	                  v.setTextColor(R.id.w79,active);
	                  v.setTextColor(R.id.w80,active);
	                  v.setTextColor(R.id.w81,active);
	                  v.setTextColor(R.id.w82,active);
	                  v.setTextColor(R.id.w83,active);break;}
	        case 8: { v.setTextColor(R.id.w85,active);
	                  v.setTextColor(R.id.w86,active);
	                  v.setTextColor(R.id.w87,active);
	                  v.setTextColor(R.id.w88,active);break;}
	        case 9: { v.setTextColor(R.id.w89,active);
	                  v.setTextColor(R.id.w90,active);
	                  v.setTextColor(R.id.w91,active);
	                  v.setTextColor(R.id.w92,active);break;}
	        case 10: { v.setTextColor(R.id.w93,active);
	                  v.setTextColor(R.id.w94,active);
	                  v.setTextColor(R.id.w95,active);
	                  v.setTextColor(R.id.w96,active);break;}
	        case 11: { v.setTextColor(R.id.w97,active);
	                  v.setTextColor(R.id.w98,active);
	                  v.setTextColor(R.id.w99,active);break;}
	        case 12: { v.setTextColor(R.id.w100,active);
	                  v.setTextColor(R.id.w101,active);
	                  v.setTextColor(R.id.w102,active);
	                  v.setTextColor(R.id.w103,active);
	                  v.setTextColor(R.id.w104,active);break;}
	        }

	      switch(M){
	          case 5:
	          case 25:
	          case 35:
	          case 55: {v.setTextColor(R.id.w8,active);
	                   v.setTextColor(R.id.w9,active);
	                   v.setTextColor(R.id.w10,active);
	                   v.setTextColor(R.id.w11,active); break; }
	          case 10:
	          case 20:
	          case 40:
	          case 50:{v.setTextColor(R.id.w12,active);
	                   v.setTextColor(R.id.w13,active);
	                   v.setTextColor(R.id.w14,active);
	                   v.setTextColor(R.id.w15,active); break; }
	          case 45:
	          case 15:{v.setTextColor(R.id.w27,active);
	                   v.setTextColor(R.id.w28,active);
	                   v.setTextColor(R.id.w29,active);
	                   v.setTextColor(R.id.w30,active);
	                   v.setTextColor(R.id.w31,active);
	                   v.setTextColor(R.id.w32,active);
	                   v.setTextColor(R.id.w33,active);
	                   break; }
	          case 0:{
	                   v.setTextColor(R.id.w108,active);
	                   v.setTextColor(R.id.w109,active);
	                   v.setTextColor(R.id.w110,active);break; }
	         
	      }
	      
	      if(M==0 && H==1) {
	    	  v.setTextColor(R.id.w48,inactive);
          }
	    }
	
public  void getRest(int r,RemoteViews v) {
     int c4= Color.TRANSPARENT;
        
        switch(r) {
            case 1: v.setInt(R.id.bg1, "setBackgroundColor", active);break;
            case 2: v.setInt(R.id.bg1, "setBackgroundColor", active);
            	    v.setInt(R.id.bg2, "setBackgroundColor", active);break;
            case 3: v.setInt(R.id.bg1, "setBackgroundColor", active);
             	    v.setInt(R.id.bg2, "setBackgroundColor", active);
             	    v.setInt(R.id.bg3, "setBackgroundColor", active);break;
            case 4: v.setInt(R.id.bg1, "setBackgroundColor",active);
            	    v.setInt(R.id.bg2, "setBackgroundColor", active);
            	    v.setInt(R.id.bg3, "setBackgroundColor", active);
            	    v.setInt(R.id.bg4, "setBackgroundColor", active);break;
            case 0: v.setInt(R.id.bg1, "setBackgroundColor", c4);
            	    v.setInt(R.id.bg2, "setBackgroundColor", c4);
            	    v.setInt(R.id.bg3, "setBackgroundColor", c4);
            	    v.setInt(R.id.bg4, "setBackgroundColor", c4);break; 
        	}
		}

private  void Schriftgroesse(RemoteViews v) {
	// TODO Auto-generated method stub
	float fontsize = fontSize;
	v.setFloat(R.id.w1, "setTextSize",  fontsize);v.setFloat(R.id.w41, "setTextSize",  fontsize);v.setFloat(R.id.w81, "setTextSize",  fontsize);
	v.setFloat(R.id.w2, "setTextSize",  fontsize);v.setFloat(R.id.w42, "setTextSize",  fontsize);v.setFloat(R.id.w82, "setTextSize",  fontsize);
	v.setFloat(R.id.w3, "setTextSize",  fontsize);v.setFloat(R.id.w43, "setTextSize",  fontsize);v.setFloat(R.id.w83, "setTextSize",  fontsize);
	v.setFloat(R.id.w4, "setTextSize",  fontsize);v.setFloat(R.id.w44, "setTextSize",  fontsize);v.setFloat(R.id.w84, "setTextSize",  fontsize);
	v.setFloat(R.id.w5, "setTextSize",  fontsize);v.setFloat(R.id.w45, "setTextSize",  fontsize);v.setFloat(R.id.w85, "setTextSize",  fontsize);
	v.setFloat(R.id.w6, "setTextSize",  fontsize);v.setFloat(R.id.w46, "setTextSize",  fontsize);v.setFloat(R.id.w86, "setTextSize",  fontsize);
	v.setFloat(R.id.w7, "setTextSize",  fontsize);v.setFloat(R.id.w47, "setTextSize",  fontsize);v.setFloat(R.id.w87, "setTextSize",  fontsize);
	v.setFloat(R.id.w8, "setTextSize",  fontsize);v.setFloat(R.id.w48, "setTextSize",  fontsize);v.setFloat(R.id.w88, "setTextSize",  fontsize);
	v.setFloat(R.id.w9, "setTextSize",  fontsize);v.setFloat(R.id.w49, "setTextSize",  fontsize);v.setFloat(R.id.w89, "setTextSize",  fontsize);
	v.setFloat(R.id.w10, "setTextSize",  fontsize);v.setFloat(R.id.w50, "setTextSize",  fontsize);v.setFloat(R.id.w90, "setTextSize",  fontsize);
	v.setFloat(R.id.w11, "setTextSize",  fontsize);v.setFloat(R.id.w51, "setTextSize",  fontsize);v.setFloat(R.id.w91, "setTextSize",  fontsize);
	v.setFloat(R.id.w12, "setTextSize",  fontsize);v.setFloat(R.id.w52, "setTextSize",  fontsize);v.setFloat(R.id.w92, "setTextSize",  fontsize);
	v.setFloat(R.id.w13, "setTextSize",  fontsize);v.setFloat(R.id.w53, "setTextSize",  fontsize);v.setFloat(R.id.w93, "setTextSize",  fontsize);
	v.setFloat(R.id.w14, "setTextSize",  fontsize);v.setFloat(R.id.w54, "setTextSize",  fontsize);v.setFloat(R.id.w94, "setTextSize",  fontsize);
	v.setFloat(R.id.w15, "setTextSize",  fontsize);v.setFloat(R.id.w55, "setTextSize",  fontsize);v.setFloat(R.id.w95, "setTextSize",  fontsize);
	v.setFloat(R.id.w16, "setTextSize",  fontsize);v.setFloat(R.id.w56, "setTextSize",  fontsize);v.setFloat(R.id.w96, "setTextSize",  fontsize);
	v.setFloat(R.id.w17, "setTextSize",  fontsize);v.setFloat(R.id.w57, "setTextSize",  fontsize);v.setFloat(R.id.w97, "setTextSize",  fontsize);
	v.setFloat(R.id.w18, "setTextSize",  fontsize);v.setFloat(R.id.w58, "setTextSize",  fontsize);v.setFloat(R.id.w98, "setTextSize",  fontsize);
	v.setFloat(R.id.w19, "setTextSize",  fontsize);v.setFloat(R.id.w59, "setTextSize",  fontsize);v.setFloat(R.id.w99, "setTextSize",  fontsize);
	v.setFloat(R.id.w20, "setTextSize",  fontsize);v.setFloat(R.id.w60, "setTextSize",  fontsize);v.setFloat(R.id.w100, "setTextSize",  fontsize);
	v.setFloat(R.id.w21, "setTextSize",  fontsize);v.setFloat(R.id.w61, "setTextSize",  fontsize);v.setFloat(R.id.w101, "setTextSize",  fontsize);
	v.setFloat(R.id.w22, "setTextSize",  fontsize);v.setFloat(R.id.w62, "setTextSize",  fontsize);v.setFloat(R.id.w102, "setTextSize",  fontsize);
	v.setFloat(R.id.w23, "setTextSize",  fontsize);v.setFloat(R.id.w63, "setTextSize",  fontsize);v.setFloat(R.id.w103, "setTextSize",  fontsize);
	v.setFloat(R.id.w24, "setTextSize",  fontsize);v.setFloat(R.id.w64, "setTextSize",  fontsize);v.setFloat(R.id.w104, "setTextSize",  fontsize);
	v.setFloat(R.id.w25, "setTextSize",  fontsize);v.setFloat(R.id.w65, "setTextSize",  fontsize);v.setFloat(R.id.w105, "setTextSize",  fontsize);
	v.setFloat(R.id.w26, "setTextSize",  fontsize);v.setFloat(R.id.w66, "setTextSize",  fontsize);v.setFloat(R.id.w106, "setTextSize",  fontsize);
	v.setFloat(R.id.w27, "setTextSize",  fontsize);v.setFloat(R.id.w67, "setTextSize",  fontsize);v.setFloat(R.id.w107, "setTextSize",  fontsize);
	v.setFloat(R.id.w28, "setTextSize",  fontsize);v.setFloat(R.id.w68, "setTextSize",  fontsize);v.setFloat(R.id.w108, "setTextSize",  fontsize);
	v.setFloat(R.id.w29, "setTextSize",  fontsize);v.setFloat(R.id.w69, "setTextSize",  fontsize);v.setFloat(R.id.w109, "setTextSize",  fontsize);
	v.setFloat(R.id.w30, "setTextSize",  fontsize);v.setFloat(R.id.w70, "setTextSize",  fontsize);v.setFloat(R.id.w110, "setTextSize",  fontsize);
	v.setFloat(R.id.w31, "setTextSize",  fontsize);v.setFloat(R.id.w71, "setTextSize",  fontsize);
	v.setFloat(R.id.w32, "setTextSize",  fontsize);v.setFloat(R.id.w72, "setTextSize",  fontsize);
	v.setFloat(R.id.w33, "setTextSize",  fontsize);v.setFloat(R.id.w73, "setTextSize",  fontsize);
	v.setFloat(R.id.w34, "setTextSize",  fontsize);v.setFloat(R.id.w74, "setTextSize",  fontsize);
	v.setFloat(R.id.w35, "setTextSize",  fontsize);v.setFloat(R.id.w75, "setTextSize",  fontsize);
	v.setFloat(R.id.w36, "setTextSize",  fontsize);v.setFloat(R.id.w76, "setTextSize",  fontsize);
	v.setFloat(R.id.w37, "setTextSize",  fontsize);v.setFloat(R.id.w77, "setTextSize",  fontsize);
	v.setFloat(R.id.w38, "setTextSize",  fontsize);v.setFloat(R.id.w78, "setTextSize",  fontsize);
	v.setFloat(R.id.w39, "setTextSize",  fontsize);v.setFloat(R.id.w79, "setTextSize",  fontsize);
	v.setFloat(R.id.w40, "setTextSize",  fontsize);v.setFloat(R.id.w80, "setTextSize",  fontsize);
	
		}
    


}