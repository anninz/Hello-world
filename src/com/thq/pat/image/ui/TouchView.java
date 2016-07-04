package com.thq.pat.image.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
/**
 * 继承ImageView 实现了多点触碰的拖动和缩放
 * @author Administrator
 *
 */
public class TouchView extends ImageView
{
    static final int NONE = 0;
    static final int DRAG = 1;     //拖动中
    static final int ZOOM = 2;     //缩放中
    static final int BIGGER = 3;   //放大ing
    static final int SMALLER = 4;  //缩小ing
    private int mode = NONE;       //当前的事件 

    private float beforeLenght;   //两触点距离
    private float afterLenght;    //两触点距离
    private float scale = 0.04f;  //缩放的比例 X Y方向都是这个值 越大缩放的越快
   
    public int getScreenW() {
        return screenW;
    }

    public void setScreenW(int screenW) {
        this.screenW = screenW;
    }

    public int getScreenH() {
        return screenH;
    }

    public void setScreenH(int screenH) {
        this.screenH = screenH;
    }

    private int screenW;
    private int screenH;
    
    /*处理拖动 变量 */
    private int start_x;
    private int start_y;
    private int stop_x ;
    private int stop_y ;
    
    private TranslateAnimation trans; //处理超出边界的动画
    private ScaleAnimation scales; //处理超出边界的动画
    float scale_flag=0;
    
    public TouchView(Context context,int w,int h)
    {
        super(context);
        this.setPadding(0, 0, 0, 0);
        screenW = w;
        screenH = h;
    }
    public TouchView(Context context,AttributeSet attrs)
    {
        super(context,attrs);
        this.setPadding(0, 0, 0, 0);

    }
    public TouchView(Context context)
    {
        super(context);
        this.setPadding(0, 0, 0, 0);
        
    }

    
    /**
     * 计算两点间的距离
     */
    @SuppressLint("NewApi")
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }
    
    /**
     * 处理触碰..
     */
    @SuppressLint("NewApi")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {   
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                stop_x = (int) event.getRawX();
                stop_y = (int) event.getRawY();
                start_x = (int) event.getX();
                start_y = stop_y - this.getTop();
                if(event.getPointerCount()==2)
                    beforeLenght = spacing(event);
                break;
        case MotionEvent.ACTION_POINTER_DOWN:
                if (spacing(event) > 10f) {
                getParent().requestDisallowInterceptTouchEvent(true);
                        mode = ZOOM;
                        beforeLenght = spacing(event);
                }
                break;
        case MotionEvent.ACTION_UP:
            /*判断是否超出范围     并处理*/
            getParent().requestDisallowInterceptTouchEvent(false);
                int disX = 0;
                int disY = 0;
                if(getHeight()<=screenH)// || this.getTop()<0)
                {           
                    if(this.getTop()<0 )
                    {
                        int dis = getTop();
                        this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());
                        disY = dis - getTop();
                    }
                    else if(this.getBottom()>screenH)
                    {
                        disY = getHeight()- screenH+getTop();
                        this.layout(this.getLeft(), screenH-getHeight(), this.getRight(), screenH);
                    }
                }
                if(getWidth()<=screenW)
                {
                    if(this.getLeft()<0)
                    {
                        disX = getLeft();
                        this.layout(0, this.getTop(), 0+getWidth(), this.getBottom());
                    }
                    else if(this.getRight()>screenW)
                    {
                        disX = getWidth()-screenW+getLeft();
                        this.layout(screenW-getWidth(), this.getTop(), screenW, this.getBottom());
                    }
                }
                if(disX!=0 || disY!=0)
                {
                    trans = new TranslateAnimation(disX, 0, disY, 0);
                    trans.setDuration(500);
                    this.startAnimation(trans);
                }
                mode = NONE;
                break;
        case MotionEvent.ACTION_POINTER_UP:
               getParent().requestDisallowInterceptTouchEvent(false);
                mode = NONE;
                if(getWidth()<screenW*0.75)
                {
                    float ff=(float) (getWidth()/(0.75*screenW));
                    scales =new ScaleAnimation(ff, 1.0f, ff,1.0f,  Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    scales.setDuration(500);
                    scale_flag=(float) (((0.75*screenW-getWidth())/2.00)/getWidth());
                    setScale(scale_flag, 3);
                    this.startAnimation(scales);
                    //scales.setFillAfter(true);
                    //scale_flag=ff;
                }

                if(getWidth()>screenW*2)
                {
                    float ff=(float) (getWidth()/(2.00*screenW));
                    Log.d("------", String.valueOf(ff));
                    scales =new ScaleAnimation(ff,1.0f,   ff, 1.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    scales.setDuration(500);
                    scale_flag=(float) (((getWidth()-2*screenW)/2.00)/getWidth());
                    setScale(scale_flag,4);
                    this.startAnimation(scales);
                    //scales.setFillAfter(true);    
                }
                break;
        case MotionEvent.ACTION_MOVE:
                /*处理拖动*/
                if (mode == DRAG) {
/*                  Log.d("DRAG.......", String.valueOf(getWidth())+"+"+String.valueOf(getHeight()));
                    Log.d("DRAG.......", String.valueOf(getLeft())+"+"+String.valueOf(getRight()));
                    if (getWidth()>screenW||getHeight()>screenH) {
                        Log.d("DRAG......in.", String.valueOf(start_x)+"+"+String.valueOf(stop_x));*/
                        if (stop_x-getLeft()<start_x&&getRight()>screenW) {
                            //Log.d("stop_x<start_x", String.valueOf(stop_x-start_x));
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        else if (stop_x-getLeft()>start_x&&getLeft()<0) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        else {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    //}
                    if(Math.abs(stop_x-start_x-getLeft())<88 && Math.abs(stop_y - start_y-getTop())<85)
                    {
                        this.setPosition(stop_x - start_x, stop_y - start_y, stop_x + this.getWidth() - start_x, stop_y - start_y + this.getHeight());              
                        stop_x = (int) event.getRawX();
                        stop_y = (int) event.getRawY();
                    }
                } 
                /*处理缩放*/
                else if (mode == ZOOM) {
                    if(spacing(event)>10f)
                    {
                        afterLenght = spacing(event);
                        float gapLenght = afterLenght - beforeLenght;                     
                        if(gapLenght == 0) {  
                           break;
                        }
                        else if(Math.abs(gapLenght)>5f)
                        {
                            if(gapLenght>0) { 
                                this.setScale(scale,BIGGER);   
                            }else { 
                                this.setScale(scale,SMALLER);   
                            }                             
                            beforeLenght = afterLenght; 
                        }
                    }
                }
                break;
        }
        return true;    
    }
    
    /**
     * 实现处理缩放
     */
    private void setScale(float temp,int flag) {   
        
        if(flag==BIGGER) {   
            this.setFrame(this.getLeft()-(int)(temp*this.getWidth()),    
                          this.getTop()-(int)(temp*this.getHeight()),    
                          this.getRight()+(int)(temp*this.getWidth()),    
                          this.getBottom()+(int)(temp*this.getHeight()));      
        }else if(flag==SMALLER){   
            this.setFrame(this.getLeft()+(int)(temp*this.getWidth()),    
                          this.getTop()+(int)(temp*this.getHeight()),    
                          this.getRight()-(int)(temp*this.getWidth()),    
                          this.getBottom()-(int)(temp*this.getHeight()));
        }   
    }
    
    /**
     * 实现处理拖动
     */
    private void setPosition(int left,int top,int right,int bottom) {  
        this.layout(left,top,right,bottom);             
    }

}
