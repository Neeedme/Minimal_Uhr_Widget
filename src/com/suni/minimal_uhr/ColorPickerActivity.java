package com.suni.minimal_uhr;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;

import android.os.Bundle;

import android.preference.PreferenceManager;

import android.text.Editable;
import android.text.TextWatcher;

import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.suni.minimal_uhr.ColorCircle.OnColorChangedListener;

public class ColorPickerActivity extends Activity 
        implements OnColorChangedListener  {
        
        ColorCircle mColorCircle;
        ColorSlider mSaturation;
        ColorSlider mValue;      
  
        Intent mIntent;
        String key;
        
        EditText et;
        AlphaPicker alphaSelector;
        Button b;
        static boolean isTextUsed=true;
       
       
        private boolean color_changed = false;
		private boolean colorPicked = false;
        
        @Override
        protected void onSaveInstanceState(Bundle outState) {
                super.onSaveInstanceState(outState);
                outState.putBoolean("color_changed", this.color_changed);
        }
        
        @Override
        protected void onRestoreInstanceState(Bundle savedInstanceState) {
                super.onRestoreInstanceState(savedInstanceState);
                this.color_changed = savedInstanceState.getBoolean("color_changed");
        }
        
        @Override //Alert when pressing back button
        public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && this.color_changed) {
                        
                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
                	builder.setMessage(R.string.BackPressedAlertTitle)
                	       .setCancelable(false)
                	       .setPositiveButton(R.string.BackPressedAlertYES, new DialogInterface.OnClickListener() {
                	           public void onClick(DialogInterface dialog, int id) {
                	        	   onColorPicked(null, mColorCircle.getColor());
                	           }
                	       })
                	       .setNegativeButton(R.string.BackPressedAlertNO, new DialogInterface.OnClickListener() {
                	           public void onClick(DialogInterface dialog, int id) {
                	                ColorPickerActivity.this.finish();
                	           }
                	       });
                	 builder.create().show();
                        return super.onKeyDown(keyCode, event);
                } else
                        return super.onKeyDown(keyCode, event);
        }
        
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                overridePendingTransition(R.anim.colorpickeranim,R.anim.exit);
                
                setContentView(R.layout.colorpicker);
 
                // Get Intent;In its Extra Color+Keys is saved
                mIntent = getIntent();
                if (mIntent == null) {
                	mIntent = new Intent();
                }
        
                int color;
                
                color = mIntent.getIntExtra("EXTRA_COLOR", Color.BLACK);
                key = mIntent.getStringExtra("key");
                initializeColor(color);    
               
                Toast.makeText(getBaseContext(),R.string.PressCenterAdvice, Toast.LENGTH_SHORT).show();
        }
        
        void initializeColor(int color) {
        	mColorCircle = (ColorCircle) findViewById(R.id.colorcircle);  
        	mColorCircle.setOnColorChangedListener(this);
        	mColorCircle.setColor(color);
        	
        	mSaturation = (ColorSlider) findViewById(R.id.saturation);
        	mSaturation.setOnColorChangedListener(this);
        	mSaturation.setColors(color, Color.BLACK);

        	mValue = (ColorSlider) findViewById(R.id.value);
        	mValue.setOnColorChangedListener(this);
        	mValue.setColors(Color.WHITE, color);
        
        	et = (EditText) findViewById(R.id.Hex);
        
        	et.setText(String.format("#%08X", (0xFFFFFFFF & color)));
        	
        	//AlphaSlider Init Begins
        	alphaSelector =(AlphaPicker) findViewById(R.id.colorhsvalpha);
        	alphaSelector.setOnAlphaChangedListener(new AlphaPicker.OnAlphaChangedListener() {
            
        		public void alphaChanged(AlphaPicker sender, int alpha) {
        			int c = mColorCircle.getColor();
        			int c2 = Color.argb(alpha, Color.red(c), Color.green(c), Color.blue(c));
        			mColorCircle.setColor(c2);
        			 et.setText(String.format("#%08X", (0xFFFFFFFF & c2)));
            }
        	});
        	alphaSelector.setColor(color|0xFF000000);
        	alphaSelector.setAlpha(Color.alpha(color));
        //Alpha Init ends
 
		//HexTefield Init Begins
		TextWatcher t = new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				try {
					if(isTextUsed) {
						int c = Color.parseColor(s.toString());

						alphaSelector.setColor(c|0xFF000000);
						alphaSelector.setAlpha(Color.alpha(c));
        	    
						mColorCircle.setColor(c);
                
						mValue.setColors(0xFFFFFFFF, c);
						mSaturation.setColors(c, 0xff000000);
				
						}
				} catch(Exception e){}
				
				isTextUsed=true;
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			public void afterTextChanged(Editable s) {}
		}; 
		
		et.addTextChangedListener(t);
		//HexTextField Init ends */
		
		//Button for contrast color init begins
		b = (Button) findViewById(R.id.ButtonContrast);	
		final SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        if(!(key.equals("FG"))) { //If Background or inactive color is changed
        	b.setText(R.string.FGContrast);
        	b.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					int c = p.getInt("FG", Color.BLACK);
					onColorChanged(b,~c|0xFF000000);
				}
			});
        }else {  //if not foreground was pressed
        	b.setText(R.string.BGContrast);
        	b.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					int c = p.getInt("BG2", Color.BLACK);
					onColorChanged(b,~c|0xFF000000);
				}
			});
        }
        //Button for contrast color init ends
        		
    }
                
        
        
 /*   class ColorPickerState {
        int mColor;
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        ColorPickerState state = new ColorPickerState();
        state.mColor = this.mColorCircle.getColor();
        return state;
    } */
        
        

       public int toGray(int color) {
                int a = Color.alpha(color);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                int gray = (r + g + b) / 3;
                return Color.argb(a, gray, gray, gray);
        } 
 
		@SuppressLint("NewApi")
		public void onColorChanged(View view, int newColor) {
        	    isTextUsed=false; //Don't trigger TextWatcher of HexEditText
        	    newColor = Color.argb((int) alphaSelector.getAlpha(), Color.red(newColor), Color.green(newColor), Color.blue(newColor));
        	    et.setText(String.format("#%08X", (0xFFFFFFFF & newColor)));
        	    
        	    if (view == mColorCircle) { //Circle changed color
    				  alphaSelector.setColor(newColor|0xFF000000);
    				  mColorCircle.setColor(newColor);
    				  
                      mValue.setColors(0xFFFFFFFF, newColor);
                      mSaturation.setColors(newColor, 0xff000000);
   
                } else if (view == mSaturation) { //saturation 
                	    alphaSelector.setColor(newColor|0xFF000000);
                        mColorCircle.setColor(newColor);
  
                } else if (view == mValue) { //value
                	    alphaSelector.setColor(newColor|0xFF000000);
                	  
                	    
                	    mSaturation.setColors(newColor, 0xff000000);
                	    
                        mColorCircle.setColor(newColor);
                    
                }else if(view==b) { //Button for contrast color pressed
                	alphaSelector.setColor(newColor|0xFF000000);
  				    alphaSelector.setAlpha(Color.alpha(newColor));
  				  
                    mValue.setColors(0xFFFFFFFF, newColor);
                    mSaturation.setColors(newColor, 0xff000000);
                    
                    mColorCircle.setColor(newColor);        
                }
                
                this.color_changed = true;
        }

        public void onColorPicked(View view, int newColor) {
        	 //Put color in SharedPreferences with Key given by Intent Extra
        	final SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
                p.edit().putInt(key, newColor).commit();
                colorPicked=true;
                finish();
        }
        
        @Override
        public void onPause() {
        	super.onPause();
        	if(colorPicked)
        		overridePendingTransition(R.anim.colorpickeranim, R.anim.exit2);
        	else
        		overridePendingTransition(R.anim.colorpickeranim, R.anim.exit);
        }
    }