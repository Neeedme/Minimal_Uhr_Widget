package com.suni.minimal_uhr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;

import com.suni.minimal_uhr.R;

public class AlphaPicker extends LinearLayout {

        private Drawable seekSelector;
        private ImageView imgSeekSelector;
        private int minOffset = 0;
        private ImageView imgAlpha;
        
        private int alpha = 0;
        private int color = Color.WHITE;
        
        private boolean dirty = true;
        
        private OnAlphaChangedListener listener;

        public AlphaPicker(Context context) {
                super(context);
                init();
        }

        public AlphaPicker(Context context, AttributeSet attrs) {
                super(context, attrs);
                init();
        }

        public void setMinContentOffset(int minOffset) {
                this.minOffset = minOffset;
                LayoutParams params = new LayoutParams(imgAlpha.getLayoutParams());
                params.setMargins(0, getOffset(), 0, getSelectorOffset());
                imgAlpha.setLayoutParams(params);
        }

        private void init() {
                seekSelector = getContext().getResources().getDrawable(
                                R.drawable.color_seekselector);
                buildUI();
        }

        private void buildUI() {
                setOrientation(LinearLayout.VERTICAL);
                setGravity(Gravity.CENTER_HORIZONTAL);
                setWillNotDraw(false);

                imgSeekSelector = new ImageView(getContext());
                imgSeekSelector.setImageDrawable(seekSelector);
                LayoutParams paramsSeek = new LayoutParams(seekSelector
                                .getIntrinsicWidth(), seekSelector.getIntrinsicHeight());
                imgAlpha = new ImageView(getContext());
                imgAlpha.setBackgroundDrawable(new AlphaPatternDrawable(8));
                imgAlpha.setScaleType(ScaleType.FIT_XY);
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT);
                params.setMargins(0, getOffset(), 0, getSelectorOffset());
                addView(imgAlpha, params);
                addView(imgSeekSelector, paramsSeek);
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
                if(dirty)
                {
                        dirty = false;
                        setAlphaImage();
                }
                super.onDraw(canvas);
        }
        
        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                placeSelector();
        }
        
        private void placeSelector()
        {
                int alphaY = (int)(((float)(255 - alpha) / 255.f) * imgAlpha.getMeasuredWidth());
                
                int halfHeight = getSelectorOffset();
                imgSeekSelector.layout( alphaY -halfHeight  ,0, alphaY -halfHeight+ imgSeekSelector.getWidth(), imgSeekSelector.getHeight());                
        }
        
        private int getSelectorOffset()
        {
                return (int) Math.ceil(imgSeekSelector.getHeight() / 2.f);
        }

        private int getOffset() {
                return Math.max(minOffset, (int) Math.ceil((double) seekSelector
                                .getIntrinsicWidth() / 2));
        }
        
      
        
        @Override
        public boolean onTouchEvent(MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                        
                        setPosition((int)event.getX());
                        return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                        
                        return true;
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE)
                {
                        setPosition((int)event.getX());
                        return true;
                }
                return super.onTouchEvent(event);
        }
        
        private void setPosition(int x)
        {
                int alphaY = x - imgAlpha.getLeft();     
                alpha = 255 - Math.min(255, Math.max(0, (int)(((float)alphaY / imgAlpha.getWidth()) * 255.f)));
                
                onAlphaChanged();
                placeSelector();
        }
        
        public void setAlpha(int alpha)
        {
                if(this.alpha == alpha)
                        return;
                this.alpha = alpha;
                placeSelector();
        }
        
        public float getAlpha()
        {
                return alpha;
        }
        
        public void setColor(int color)
        {
                if(this.color == color)
                        return;
                this.color = color;
                setAlphaImage();
        }

        private void setAlphaImage()
        {
                if(imgAlpha.getHeight() <= 0)
                {
                        dirty = true;
                        invalidate();
                        return;
                }
                Paint paint = new Paint();
                Shader shader;
                Bitmap drawCache = null;
                if (drawCache == null) {
                        
                        int colorFullAlpha = color | 0xFF000000;
                        int colorNoAlpha = color & 0x00FFFFFF;

                        shader = new LinearGradient(imgAlpha.getWidth(), 0, 0, 0,
                                        colorNoAlpha, colorFullAlpha, TileMode.CLAMP);

                        paint.setShader(shader);

                        drawCache = Bitmap.createBitmap(imgAlpha.getWidth(), imgAlpha.getHeight(),
                                        Bitmap.Config.ARGB_8888);
                        Canvas cacheCanvas = new Canvas(drawCache);
                        cacheCanvas.drawRect(0.f, 0.f, imgAlpha.getWidth(), imgAlpha.getHeight(),
                                        paint);
                        
                        imgAlpha.setImageBitmap(drawCache);
                }
        }
        
        public void setOnAlphaChangedListener(OnAlphaChangedListener listener)
        {
                this.listener = listener;
        }
        
        private void onAlphaChanged()
        {
                if(listener != null)
                        listener.alphaChanged(this, alpha);
        }
        
        public interface OnAlphaChangedListener
        {
                public void alphaChanged(AlphaPicker sender, int alpha);
        }
}
