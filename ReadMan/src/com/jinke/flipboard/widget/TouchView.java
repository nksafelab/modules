package com.jinke.flipboard.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * 
*@ClassName:TouchView
*@Description:TODO(what to do)
*@auchor: nk_jinke_yujinyang
*@date:2011-11-1下午03:10:36
*@tags:
 */
public class TouchView extends ImageView
{
	Context context;
    static final int NONE = 0;
    static final int DRAG = 1;	   
    static final int ZOOM = 2;     
    static final int BIGGER = 3;   
    static final int SMALLER = 4;  
    private int mode = NONE;	    

    private float beforeLenght;   
    private float afterLenght;    
    private float scale = 0.04f; 
   
    private int screenW;
    private int screenH;
    
    private int start_x;
    private int start_y;
	private int stop_x ;
	private int stop_y ;
	
    private TranslateAnimation trans; //���?���߽�Ķ���
	

    
    /**
	 * @param context
	 * @param attrs
	 */
	public TouchView(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public TouchView(Context context,int w,int h)
	{
		super(context);
		this.setPadding(0, 0, 0, 0);
		screenW = w;
		screenH = h;
	}
	
	/**
	 * 
	 *@Title: spacing
	 *@Description: TODO (description the fun)
	 *@return float
	 *@params @param event
	 *@params @return
	 *@throws
	 */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
    
    /**
     * 
     */
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
                        mode = ZOOM;
                		beforeLenght = spacing(event);
                }
                break;
        case MotionEvent.ACTION_UP:
        	
        		int disX = 0;
        		int disY = 0;
	        	if(getHeight()<=screenH || this.getTop()<0)
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
	        		Log.e("animation", "trans");
            		trans = new TranslateAnimation(disX, 0, disY, 0);
            		trans.setDuration(500);
            		this.startAnimation(trans);
	        	}
	        	mode = NONE;
        		break;
        case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                	if(Math.abs(stop_x-start_x-getLeft())<88 && Math.abs(stop_y - start_y-getTop())<85)
                	{
                    	this.setPosition(stop_x - start_x, stop_y - start_y, stop_x + this.getWidth() - start_x, stop_y - start_y + this.getHeight());           	
                    	stop_x = (int) event.getRawX();
                    	stop_y = (int) event.getRawY();
                	}
                } 
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
	 * 
	 *@Title: setScale
	 *@Description: TODO (description the fun)
	 *@return void
	 *@params @param temp
	 *@params @param flag
	 *@throws
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
	 * 
	 *@Title: setPosition
	 *@Description: TODO (description the fun)
	 *@return void
	 *@params @param left
	 *@params @param top
	 *@params @param right
	 *@params @param bottom
	 *@throws
	 */
    private void setPosition(int left,int top,int right,int bottom) {  
    	this.layout(left,top,right,bottom);           	
    }

}
