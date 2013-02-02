package com.suni.minimal_uhr;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class ColorPickerPreference
extends
Preference

 {

View mView;
int mDefaultValue;
private int mValue;
private float mDensity = 0;
 OnSharedPreferenceChangeListener listener;
 SharedPreferences pref;


public ColorPickerPreference(Context context) {
	super(context);
	init(context, null);
}

	public ColorPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
}

public ColorPickerPreference(Context context, AttributeSet attrs, int defStyle) {
super(context, attrs, defStyle);
init(context, attrs);
}


private void init(Context context, AttributeSet attrs) {
	mDensity = getContext().getResources().getDisplayMetrics().density;

	
	listener =new OnSharedPreferenceChangeListener () {
		   public void onSharedPreferenceChanged(SharedPreferences prefs,String key) {
				 if(key.equals(getKey())) { 
					 mValue= prefs.getInt(key, Color.argb(127,255,255,255));
					 setPreviewColor();
				 }
				 } 
				
	   };
	   
	   pref=PreferenceManager.getDefaultSharedPreferences(context);
	   pref.registerOnSharedPreferenceChangeListener(listener);
	   
	   mValue= pref.getInt(getKey(),getDefaultColor(getKey()));
}

@Override
protected void onBindView(View view) {
	super.onBindView(view);
	mView = view;
	setPreviewColor();
}

private void setPreviewColor() {
	if (mView == null) return;
	ImageView iView = new ImageView(getContext());
	LinearLayout widgetFrameView = ((LinearLayout)mView.findViewById(android.R.id.widget_frame));
	if (widgetFrameView == null) return;
	widgetFrameView.setPadding(
			widgetFrameView.getPaddingLeft(),
			widgetFrameView.getPaddingTop(),
			(int)(mDensity * 8),
			widgetFrameView.getPaddingBottom()
			);
	// remove already created preview image
	int count = widgetFrameView.getChildCount();
	if (count > 0) {
		widgetFrameView.removeViews(0, count);
	}

	iView.setBackgroundDrawable(new AlphaPatternDrawable((int)(5*mDensity)));
	iView.setImageBitmap(getPreviewBitmap());
	iView.bringToFront();
	Log.d("colorPrefernce", Integer.toString(iView.getWidth()));
	widgetFrameView.addView(iView);
	widgetFrameView.setVisibility(View.VISIBLE);

}

private Bitmap getPreviewBitmap() {
	int d = (int) (mDensity * 31); //30dip
	int color = mValue;
	int w = (int) (1.5*d);
	int h = d;
	Bitmap bm = Bitmap.createBitmap(w, h, Config.ARGB_8888);
	int c=color;
	for (int i = 0; i < w; i++) {
		for (int j = 0; j < h; j++) {
			c = (i <= 1 || j <= 1 || i >= w-2|| j >= h-2) ? Color.GRAY : color;
			bm.setPixel(i, j, c);
		}
	}

	return bm;
}

static public int getDefaultColor(String key) {
	if(key.equals("FG"))
		return R.color.defaultForeground;
	else if(key.equals("BG"))
		return R.color.defaultInactive;
	else
		return R.color.defaultBackground;
	
}




}