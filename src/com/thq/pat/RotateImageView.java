package com.thq.pat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RotateImageView extends ImageView {

        public int degree = 0;

        /**
         * @param context
         */
        public RotateImageView(Context context) {
            this(context, null);
        }

        /**
         * @param context
         * @param attrs
         */
        public RotateImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
        
       int circleHeight ,circleWidth;
        
        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
/*            
            final Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.pic8);
            
            circleHeight = getHeight();
            circleWidth = getWidth();

            canvas.rotate(degree, circleWidth / 2, circleHeight / 2);
            canvas.drawBitmap(bitmap, 0, 0, null);*/
            super.onDraw(canvas);
        }

        public void setDegree(int degree) {
            this.degree = degree;
            invalidate();
        }
    }