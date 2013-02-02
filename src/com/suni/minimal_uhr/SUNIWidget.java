package com.suni.minimal_uhr;

import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;

public class SUNIWidget extends AppWidgetProvider{

	private  boolean hasStarted=false;
	
	
	@Override
	public void onDisabled(Context c) {
		c.stopService(new Intent(c, UpdateService.class));
	
		super.onDisabled(c);
	}
	
	

	@Override
	public void onEnabled(Context context) {
		    // android.content.IntentFilter intentFilter = new android.content.IntentFilter(Intent.ACTION_TIME_TICK);
            // context.getApplicationContext().registerReceiver(this,intentFilter);
             hasStarted=true;
             AppWidgetManager awm = AppWidgetManager.getInstance(context);
             
    		 int[] ids = awm.getAppWidgetIds(new ComponentName(context, SUNIWidget.class));
    		 Intent i = new Intent(context,com.suni.minimal_uhr.UpdateService.class);
    		 i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
    		 
			 context.startService(i);
		 }
	
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
	
	 if("com.suni.minimal_uhr.Update".equals(intent.getAction())
			// ||  Intent.ACTION_TIME_TICK.equals(intent.getAction())
			 
			 ){ 
		 
		AppWidgetManager awm = AppWidgetManager.getInstance(context);
		int[] ids = awm.getAppWidgetIds(new ComponentName(context, SUNIWidget.class));
		Intent i = new Intent(context,com.suni.minimal_uhr.UpdateService.class);
		i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		context.startService(i);
	    
	   
	   }else {
		   super.onReceive(context,intent);	 
	   }
	}
	
	
	@Override
	public void onUpdate(final Context context,AppWidgetManager awm,final int[] appwidgetIds) {
		if(!hasStarted) {
				onEnabled(context);
				return;
		}
			    int[] ids = awm.getAppWidgetIds(new ComponentName(context, SUNIWidget.class));
				Intent i = new Intent(context,com.suni.minimal_uhr.UpdateService.class);
				i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
				context.startService(i);
      }	

	}


