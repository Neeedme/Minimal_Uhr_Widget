package com.suni.minimal_uhr;

import android.content.Context;


import android.preference.Preference;

import android.util.AttributeSet;

import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class SeekBarPreference extends Preference implements OnSeekBarChangeListener{

	public static int maximum    = 35;

	private TextView summary;
	private TextView curr;
	private SeekBar	seekbar;
	private float status=0;

		public SeekBarPreference(Context context) {
			super(context);
		}

		public SeekBarPreference(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}
		
		protected View onCreateView (ViewGroup p) {

			final Context ctx = getContext();
		
			LinearLayout layout = new LinearLayout( ctx );
		
			layout.setId( android.R.id.widget_frame );
			layout.setOrientation( LinearLayout.VERTICAL );
			layout.setPadding(15, 0, 5, 0);
			TextView title = new TextView( ctx );
			int textColor = title.getCurrentTextColor();
			title.setId( android.R.id.title );
			title.setSingleLine();
			title.setTextAppearance( ctx, android.R.style.TextAppearance_Medium );
		
			layout.addView( title );
			status=getPersistedFloat(15f);
		
			seekbar = new SeekBar( ctx );
			seekbar.setMax( maximum );
			seekbar.setOnSeekBarChangeListener( this );
			seekbar.setProgress((int) status);
 
			layout.addView( seekbar );
        
			summary = new TextView( ctx );
			summary.setId( android.R.id.summary );
			summary.setSingleLine();
			summary.setTextAppearance( ctx, android.R.style.TextAppearance_Small );
			summary.setTextColor( textColor );
			
			layout.addView( summary );
		
			curr = new TextView(ctx);
			curr.setSingleLine();
			curr.setTextAppearance(ctx, android.R.style.TextAppearance_Small);
			curr.setTextColor(textColor);
			
			layout.addView(curr);
        if(isPersistent())
        	curr.setText(Integer.toString((int) status));
        
		return layout;
	}
		public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
			if(fromUser)
				curr.setText(Integer.toString(progress));
		}

		private void updatePreference(float newValue){
			persistFloat(newValue);
		}

		public void onStartTrackingTouch(SeekBar seekBar) {}

		public void onStopTrackingTouch(SeekBar seekBar) {
			updatePreference((float)seekBar.getProgress());
		}
}