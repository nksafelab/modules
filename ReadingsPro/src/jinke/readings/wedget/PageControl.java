package jinke.readings.wedget;

import java.util.ArrayList; 

import android.content.Context; 
import android.content.res.TypedArray; 
import android.graphics.Color; 
import android.graphics.drawable.Drawable; 
import android.graphics.drawable.ShapeDrawable; 
import android.graphics.drawable.shapes.OvalShape; 
import android.graphics.drawable.shapes.Shape; 
import android.util.AttributeSet; 
import android.util.Log; 
import android.view.MotionEvent; 
import android.view.View; 
import android.widget.ImageView; 
import android.widget.LinearLayout; 

public class PageControl extends LinearLayout 
{ 
    private int mIndicatorSize = 7; 
     
    private Drawable activeDrawable; 
    private Drawable inactiveDrawable; 
     
    private ArrayList<ImageView> indicators; 
     
    private int mPageCount = 0; 
    private int mCurrentPage = 0; 
     
    private Context mContext; 
    private OnPageControlClickListener mOnPageControlClickListener = null; 
     
    public PageControl(Context context)  
    { 
        super(context); 
        mContext = context; 
        initPageControl(); 
    } 
     
    public PageControl(Context context, AttributeSet attrs)  
    { 
        super(context, attrs); 
        mContext = context; 
        //will now wait until onFinishInflate to call initPageControl() 
    } 
     
    @Override 
    protected void onFinishInflate() 
    { 
        initPageControl(); 
    } 
     
    private void initPageControl() 
    { 
        Log.v("PageControl","Initialising PageControl"); 
         
        indicators = new ArrayList<ImageView>(); 
         
        activeDrawable = new ShapeDrawable(); 
        inactiveDrawable = new ShapeDrawable(); 
         
        activeDrawable.setBounds(0, 0, mIndicatorSize, mIndicatorSize); 
        inactiveDrawable.setBounds(0, 0, mIndicatorSize, mIndicatorSize); 
         
        Shape s1 = new OvalShape(); 
        s1.resize(mIndicatorSize, mIndicatorSize); 
         
        Shape s2 = new OvalShape(); 
        s2.resize(mIndicatorSize, mIndicatorSize); 
         
        int i[] = new int[2]; 
        i[0] = android.R.attr.textColorSecondary; 
        i[1] = android.R.attr.textColorSecondaryInverse; 
        TypedArray a = mContext.getTheme().obtainStyledAttributes(i); 
 
        ((ShapeDrawable) activeDrawable).getPaint().setColor(a.getColor(0, Color.DKGRAY)); 
        ((ShapeDrawable) inactiveDrawable).getPaint().setColor(a.getColor(1, Color.LTGRAY)); 
 
        ((ShapeDrawable) activeDrawable).setShape(s1); 
        ((ShapeDrawable) inactiveDrawable).setShape(s2); 
         
        mIndicatorSize = (int) (mIndicatorSize * getResources().getDisplayMetrics().density); 
         
        setOnTouchListener(new OnTouchListener()  
        {    
            public boolean onTouch(View v, MotionEvent event)  
            { 
                if(mOnPageControlClickListener != null) 
                { 
                    switch(event.getAction()) 
                    { 
                        case MotionEvent.ACTION_UP : 
                             
                            if(PageControl.this.getOrientation() == LinearLayout.HORIZONTAL) 
                            { 
                                if(event.getX()<(PageControl.this.getWidth()/2)) //if on left of view 
                                { 
                                    if(mCurrentPage>0) 
                                    { 
                                        mOnPageControlClickListener.goBackwards(); 
                                    } 
                                } 
                                else //if on right of view 
                                { 
                                    if(mCurrentPage<(mPageCount-1)) 
                                    { 
                                        mOnPageControlClickListener.goForwards(); 
                                    } 
                                } 
                            } 
                            else 
                            { 
                                if(event.getY()<(PageControl.this.getHeight()/2)) //if on top half of view 
                                { 
                                    if(mCurrentPage>0) 
                                    { 
                                        mOnPageControlClickListener.goBackwards(); 
                                    } 
                                } 
                                else //if on bottom half of view 
                                { 
                                    if(mCurrentPage<(mPageCount-1)) 
                                    { 
                                        mOnPageControlClickListener.goForwards(); 
                                    } 
                                } 
                            } 
                             
                             
                            return false; 
                    } 
                } 
                return true; 
            } 
        }); 
    } 
     
   
    public void setActiveDrawable(Drawable d) 
    { 
        activeDrawable = d; 
         
        indicators.get(mCurrentPage).setBackgroundDrawable(activeDrawable); 
         
    } 
     
  
    public Drawable getActiveDrawable() 
    { 
        return activeDrawable; 
    } 
     
   
    public void setInactiveDrawable(Drawable d) 
    { 
        inactiveDrawable = d; 
         
        for(int i=0; i<mPageCount; i++) 
        { 
            indicators.get(i).setBackgroundDrawable(inactiveDrawable); 
        } 
         
        indicators.get(mCurrentPage).setBackgroundDrawable(activeDrawable); 
    } 
 
    
    public Drawable getInactiveDrawable() 
    { 
        return inactiveDrawable; 
    } 
     
   
    public void setPageCount(int pageCount) 
    { 
        mPageCount = pageCount; 
        for(int i=0;i<pageCount;i++) 
        { 
            final ImageView imageView = new ImageView(mContext); 
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mIndicatorSize, mIndicatorSize); 
            params.setMargins(mIndicatorSize/2, mIndicatorSize, mIndicatorSize/2, mIndicatorSize); 
            imageView.setLayoutParams(params); 
            imageView.setBackgroundDrawable(inactiveDrawable); 
 
            indicators.add(imageView); 
            addView(imageView); 
        } 
    } 
     
     
    public int getPageCount() 
    { 
        return mPageCount; 
    } 
     
    
    public void setCurrentPage(int currentPage) 
    { 
        if(currentPage<mPageCount) 
        { 
            indicators.get(mCurrentPage).setBackgroundDrawable(inactiveDrawable);//reset old indicator 
            indicators.get(currentPage).setBackgroundDrawable(activeDrawable);//set up new indicator 
            mCurrentPage = currentPage; 
        } 
    } 
     
   
    public int getCurrentPage() 
    { 
        return mCurrentPage; 
    } 
     
   
    public void setIndicatorSize(int indicatorSize) 
    { 
        mIndicatorSize=indicatorSize; 
        for(int i=0;i<mPageCount;i++) 
        { 
            indicators.get(i).setLayoutParams(new LayoutParams(mIndicatorSize, mIndicatorSize)); 
        } 
    } 
     
    
    public int getIndicatorSize() 
    { 
        return mIndicatorSize; 
    } 
     
    
    public interface OnPageControlClickListener 
    { 
        
        public abstract void goForwards(); 
         
       
        public abstract void goBackwards(); 
    } 
     
    
    public void setOnPageControlClickListener(OnPageControlClickListener onPageControlClickListener) 
    { 
        mOnPageControlClickListener = onPageControlClickListener; 
    } 
     
    
    public OnPageControlClickListener getOnPageControlClickListener() 
    { 
        return mOnPageControlClickListener; 
    } 
         
     
} 