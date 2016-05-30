package jinke.readings.wedget;

import jinke.readings.wedget.PageControl.OnPageControlClickListener;

import android.content.Context; 
import android.graphics.Rect; 
import android.util.AttributeSet; 
import android.util.Log; 
import android.view.Display; 
import android.view.MotionEvent; 
import android.view.View; 
import android.view.ViewGroup; 
import android.view.WindowManager; 
import android.widget.HorizontalScrollView; 
import android.widget.LinearLayout; 

public class MyHorizontal extends HorizontalScrollView {
	private View mView;
	
	
	private static int DEFAULT_SWIPE_THRESHOLD = 200; 
    
    private LinearLayout mLinearLayout; 
    private Context mContext; 
    private int SCREEN_WIDTH; 
    private int mMotionStartX; 
    private int mMotionStartY; 
    private boolean mMostlyScrollingInX = false; 
    private boolean mMostlyScrollingInY = false; 
    private boolean mJustInterceptedAndIgnored = false; 
    protected boolean mCallScrollToPageInOnLayout = false; 
    private int mCurrentPage = 0; 
    private int mPageWidth = 0; 
    private OnPageChangedListener mOnPageChangedListener = null; 
    private SwipeOnTouchListener mSwipeOnTouchListener; 
    private View.OnTouchListener mOnTouchListener; 
    private PageControl mPageControl = null; 
 
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mView != null) {
			mView.scrollTo(l, t);
		}
	}

	public void setScrollView(View view) {
		mView = view;
	}
    
    /**
     * {@inheritDoc}
     */ 
    public MyHorizontal(Context context)  
    { 
        super(context); 
        mContext = context; 
        initSwipeView(); 
    } 
     
    /**
     * {@inheritDoc}
     */ 
    public MyHorizontal(Context context, AttributeSet attrs)  
    { 
        super(context, attrs); 
        mContext = context; 
        initSwipeView(); 
    } 
     
    /**
     * {@inheritDoc}
     */ 
    public MyHorizontal(Context context, AttributeSet attrs, int defStyle) 
    { 
        super(context, attrs,defStyle); 
        mContext = context; 
        initSwipeView(); 
    } 
     
    private void initSwipeView() 
    { 
        Log.v("SwipeView","Initialising SwipeView"); 
        mLinearLayout = new LinearLayout(mContext); 
        mLinearLayout.setOrientation(LinearLayout.HORIZONTAL); 
        super.addView(mLinearLayout, -1, new LayoutParams(600, 886)); 
         
        setSmoothScrollingEnabled(true); 
        setHorizontalFadingEdgeEnabled(false); 
        setHorizontalScrollBarEnabled(false); 
         
        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();  
        SCREEN_WIDTH = (int) (display.getWidth())-5; 
        mPageWidth = SCREEN_WIDTH-25; 
        mCurrentPage = 0; 
         
        mSwipeOnTouchListener = new SwipeOnTouchListener(); 
        super.setOnTouchListener(mSwipeOnTouchListener); 
    } 
     
    /**
     * {@inheritDoc}
     */ 
    @Override 
    public boolean onTrackballEvent(MotionEvent event) 
    { 
        return true; 
    } 
     
    @Override 
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) 
    { 
        return false; 
    }  
 
    @Override 
    public void requestChildFocus(View child, View focused) 
    {  
        requestFocus(); 
    } 
     
    
    @Override 
    public void addView(View child) 
    { 
        this.addView(child,-1); 
    } 
     
    
    @Override 
    public void addView (View child, int index) 
    { 
        ViewGroup.LayoutParams params; 
        if(child.getLayoutParams()==null) 
        { 
            params = new LayoutParams(mPageWidth,886); 
        } 
        else 
        { 
            params = child.getLayoutParams(); 
            params.width = mPageWidth; 
        } 
        this.addView(child, index, params); 
    } 
     
    
    @Override 
    public void addView (View child, ViewGroup.LayoutParams params) 
    { 
        params.width = mPageWidth; 
        this.addView (child, -1, params); 
    } 
     
    
    @Override 
    public void addView (View child, int index, ViewGroup.LayoutParams params) 
    { 
        requestLayout(); 
        invalidate(); 
        mLinearLayout.addView(child, index, params); 
    } 
      
    @Override 
    protected void onLayout(boolean changed, int l, int t, int r, int b) 
    { 
        super.onLayout(changed, l, t, r, b); 
        if(mCallScrollToPageInOnLayout) 
        { 
            scrollToPage(mCurrentPage); 
            mCallScrollToPageInOnLayout = false; 
        } 
    } 
   
    @Override 
    public void setOnTouchListener(View.OnTouchListener onTouchListener) 
    { 
        mOnTouchListener = onTouchListener; 
    } 
   
    public LinearLayout getChildContainer() 
    { 
        return mLinearLayout; 
    } 
     
   
    public int getSwipeThreshold() 
    { 
        return DEFAULT_SWIPE_THRESHOLD; 
    } 
     
    
    public void setSwipeThreshold(int swipeThreshold) 
    { 
        DEFAULT_SWIPE_THRESHOLD = swipeThreshold; 
    } 
     
    
    public int getCurrentPage() 
    { 
        return mCurrentPage; 
    } 
     
   
    public int getPageCount() 
    { 
        return mLinearLayout.getChildCount(); 
    } 
     
    
    public void scrollToPage(int page) 
    { 
        scrollToPage(page,false); 
    } 
     
    
    public void smoothScrollToPage(int page) 
    { 
        scrollToPage(page,true); 
    } 
     
    private void scrollToPage(int page, boolean smooth) 
    { 
        int oldPage = mCurrentPage; 
        if(page>=getPageCount() && getPageCount()>0) 
        { 
            page--; 
        } 
        else if(page<0) 
        { 
            page=0; 
        } 
         
        if(smooth) 
        { 
            smoothScrollTo(page*mPageWidth-10,0); 
        } 
        else 
        { 
            scrollTo(page*mPageWidth-10,0); 
        } 
        mCurrentPage = page; 
         
        if(mOnPageChangedListener!=null && oldPage!=page) 
        { 
            mOnPageChangedListener.onPageChanged(oldPage, page); 
        } 
        if(mPageControl!=null && oldPage!=page) 
        { 
            mPageControl.setCurrentPage(page); 
        } 
         
        mCallScrollToPageInOnLayout=!mCallScrollToPageInOnLayout; 
    } 
     
   
    public int setPageWidth(int pageWidth) 
    { 
        mPageWidth = pageWidth; 
        return (SCREEN_WIDTH - mPageWidth)/2; 
    } 
     
   
    public int calculatePageSize(MarginLayoutParams childLayoutParams)  
    { 
        return setPageWidth(childLayoutParams.leftMargin + childLayoutParams.width + childLayoutParams.rightMargin); 
    } 
     
   
    public int getPageWidth() 
    { 
        return mPageWidth; 
    } 
     
     
    public void setPageControl(PageControl pageControl) 
    { 
        mPageControl = pageControl; 
         
        pageControl.setPageCount(getPageCount()); 
        pageControl.setCurrentPage(mCurrentPage); 
        pageControl.setOnPageControlClickListener(new OnPageControlClickListener()  
        { 
            public void goForwards()  
            { 
                smoothScrollToPage(mCurrentPage+1); 
            } 
             
            public void goBackwards()  
            { 
                smoothScrollToPage(mCurrentPage-1); 
            } 
        }); 
    } 
     
     
    public PageControl getPageControl() 
    { 
        return mPageControl; 
    } 
     
    
    public interface OnPageChangedListener 
    { 
        
        public abstract void onPageChanged(int oldPage, int newPage); 
    } 
     
   
    public void setOnPageChangedListener(OnPageChangedListener onPageChangedListener) 
    { 
        mOnPageChangedListener = onPageChangedListener; 
    } 
     
     
    public OnPageChangedListener getOnPageChangedListener() 
    { 
        return mOnPageChangedListener; 
    } 
     
    @Override 
    public boolean onInterceptTouchEvent(MotionEvent ev) 
    { 
        boolean result = super.onInterceptTouchEvent(ev); 
         
        if(ev.getAction() == MotionEvent.ACTION_DOWN) 
        { 
            mMotionStartX = (int) ev.getX(); 
            mMotionStartY = (int) ev.getY(); 
            if(!mJustInterceptedAndIgnored) 
            { 
                mMostlyScrollingInX = false; 
                mMostlyScrollingInY = false; 
            } 
        } 
        else if(ev.getAction()==MotionEvent.ACTION_MOVE) 
        { 
            detectMostlyScrollingDirection(ev); 
        } 
         
        if(mMostlyScrollingInY) 
        { 
            return false; 
        } 
        if(mMostlyScrollingInX) 
        { 
            mJustInterceptedAndIgnored = true; 
            return true; 
        } 
         
        return result; 
    } 
     
    private void detectMostlyScrollingDirection(MotionEvent ev) 
    { 
        if(!mMostlyScrollingInX && !mMostlyScrollingInY)  
        { 
            float xDistance = Math.abs(mMotionStartX - ev.getX()); 
            float yDistance = Math.abs(mMotionStartY - ev.getY()); 
             
            if(yDistance>xDistance+5) 
            { 
                mMostlyScrollingInY = true; 
            } 
            else if(xDistance>yDistance+5) 
            { 
                mMostlyScrollingInX = true; 
            } 
        } 
    } 
     
    private class SwipeOnTouchListener implements View.OnTouchListener 
    { 
        private boolean mSendingDummyMotionEvent = false; 
        private int mDistanceX; 
        private int mPreviousDirection; 
        private boolean mFirstMotionEvent = true; 
         
        public boolean onTouch(View v, MotionEvent event)  
        { 
            if(mOnTouchListener!=null && !mJustInterceptedAndIgnored || mOnTouchListener!=null && mSendingDummyMotionEvent)  
            { 
                if(mOnTouchListener.onTouch(v, event)) 
                { 
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        actionUp(event); 
                    } 
                    return true; 
                } 
            } 
             
            if(mSendingDummyMotionEvent)
            { 
                mSendingDummyMotionEvent = false; 
                return false; 
            } 
 
            switch(event.getAction()) 
            { 
                case MotionEvent.ACTION_DOWN : 
                    return actionDown(event); 
                     
                case MotionEvent.ACTION_MOVE : 
                    return actionMove(event); 
                     
                case MotionEvent.ACTION_UP : 
                    return actionUp(event); 
            } 
            return false; 
        } 
         
        private boolean actionDown(MotionEvent event) 
        { 
            mMotionStartX = (int) event.getX(); 
            mMotionStartY = (int) event.getY(); 
            mFirstMotionEvent = false; 
            return false; 
        } 
         
        private boolean actionMove(MotionEvent event) 
        { 
            int newDistance = mMotionStartX - (int) event.getX(); 
            int newDirection; 
             
            if(newDistance<0) //backwards 
            { 
                newDirection =  (mDistanceX+4 <= newDistance) ? 1 : -1;   
            } 
            else //forwards 
            { 
                newDirection =  (mDistanceX-4 <= newDistance) ? 1 : -1;   
            } 
             
             
            if(newDirection != mPreviousDirection && !mFirstMotionEvent) 
            { 
                mMotionStartX = (int) event.getX(); 
                mDistanceX = mMotionStartX - (int) event.getX(); 
            } 
            else 
            { 
                mDistanceX = newDistance; 
            } 
 
            mPreviousDirection = newDirection;  
             
            if(mJustInterceptedAndIgnored) 
            { 
                mSendingDummyMotionEvent = true; 
                dispatchTouchEvent(MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_DOWN, mMotionStartX, mMotionStartY, event.getPressure(), event.getSize(), event.getMetaState(), event.getXPrecision(), event.getYPrecision(), event.getDeviceId(), event.getEdgeFlags())); 
                mJustInterceptedAndIgnored = false; 
                 
                return true; 
            } 
            return false; 
        } 
         
        private boolean actionUp(MotionEvent event) 
        { 
            float fingerUpPosition = getScrollX(); 
            float numberOfPages = mLinearLayout.getMeasuredWidth() / mPageWidth; 
            float fingerUpPage = fingerUpPosition/mPageWidth; 
            float edgePosition = 0; 
             
            if(mPreviousDirection == 1)  
            { 
                if(mDistanceX > DEFAULT_SWIPE_THRESHOLD) 
                { 
                    if(mCurrentPage<(numberOfPages-1)) 
                    { 
                        edgePosition = (int)(fingerUpPage+1)*mPageWidth; 
                    } 
                    else 
                    { 
                        edgePosition = (int)(mCurrentPage)*mPageWidth; 
                    } 
                } 
                else  
                { 
                    if(Math.round(fingerUpPage)==numberOfPages-1) 
                    { 
                        
                        edgePosition = (int)(fingerUpPage+1)*mPageWidth; 
                    } 
                    else  
                    { 
                        edgePosition = mCurrentPage*mPageWidth; 
                    } 
                } 
                 
            } 
            else 
            { 
                if(mDistanceX < -DEFAULT_SWIPE_THRESHOLD) 
                { 
                    edgePosition = (int)(fingerUpPage)*mPageWidth; 
                } 
                else  
                { 
                    if(Math.round(fingerUpPage)==0) 
                    { 
                      
                        edgePosition = (int)(fingerUpPage)*mPageWidth; 
                    } 
                    else 
                    { 
                        edgePosition = mCurrentPage*mPageWidth; 
                    } 
                     
                } 
            } 
             
            smoothScrollToPage((int)edgePosition/mPageWidth); 
            mFirstMotionEvent = true; 
            mDistanceX = 0; 
            mMostlyScrollingInX = false; 
            mMostlyScrollingInY = false; 
             
            return true; 
        } 
    } 
}
