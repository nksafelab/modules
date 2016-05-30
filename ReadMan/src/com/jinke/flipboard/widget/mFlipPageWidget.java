package com.jinke.flipboard.widget;

import java.util.ArrayList;
import java.util.List;
import com.jinke.readman.R;
import com.jinke.readman.flipper;
import com.jinke.readman.bean.User;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Scroller;
import android.widget.Toast;

/**
*@ClassName:mFlipPageWidget
*@Description:TODO(what to do)
*@author: nk_jinke_yujinyang
*@date:2011-9-21下午03:53:22
*@tags:
 */
public class mFlipPageWidget extends ViewGroup {
	private VelocityTracker mVelocityTracker;
	private final String TAG = "mFlipBoardWidget";
	public boolean isfenye = true;
	public static final int FLIP_DIRECTION_PRE = 1;
	public static final int FLIP_DIRECTION_NEXT = 2;

	private boolean isFlipPre = true;
	private boolean isFlipNext = true;

	private static final int INVALID_SCREEN = -1;
	private int mDefaultScreen;
	private boolean mFirstLayout = true;
	private int mCurrentScreen;
	private int mNextScreen = INVALID_SCREEN;

	private Scroller mScroller;
	// 记录了上次鼠标按下时的XY值，在ACTION_MOVE中赋值；
	private float mLastMotionX;
	private float mLastMotionY;
	// 记录触摸状态
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private int mTouchState = TOUCH_STATE_REST;
	public static int mDefaultSlop;
	public static int mTouchSlop; // 这个值表示需要滑动多少距离的时候才翻到下一页

	private int mDataIndex = 100; // 当前View中的数据在总数据所在位置
	private int mNextDataIndex = INVALID_SCREEN;
	private IFlipWidgetListener mListener;
	private boolean isAddFinish = false;
	private boolean isChangeScreen = false;
	
	private int mCPage = 1;
	private int mCSize = 1;
//	private int mTPage = 16;
	private int mTPage = User.getPage();
	private int mCView = 1;
	
	mWebView wv;
	boolean flag = true;
	GestureDetector mGestureDetector;
	
	boolean fenye = false;
	
	private List<mWebView> webviews = new ArrayList<mWebView>();
	private List<View> mViews = new ArrayList<View>();
	
	public void setViews(List<View> views){
		this.mViews = views;	
	}
	public int getMCurrentView(){
		return mCView;
	}
	public void setWebViews(List<mWebView> webviews){
		this.webviews = webviews;
	}
	
	
	private String username = "";
	public void setUsername(String username){
		this.username = username;
//		this.username = "marklee";
	}
	private String type = "";
	private int STATUS = 0;
	private String Load_URL = "";
	
	public void reload(){
		for(int i=0;i<mViews.size();i++){
			
			View v = mViews.get(i);
			mWebView wv = (mWebView)v.findViewById(R.id.mWebView);
//			wv.getUrl();
			
			if(i != mCView){
				Log.e("reload", "reload:"+i);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				wv.loadUrl(wv.getUrl());
			}
		}
	}
	
	public void setStatus(int status){
		STATUS = status;
		switch (STATUS) {
		case 1://news
			
			Load_URL = "http://61.181.14.184:8084/NewReadman/getRss.do?username=";
			type = "rss";
			//http://61.181.14.184:8084/NewReadman/getRss.do?username=
			break;
		case 2://weibo
			
			mTPage = 20;
			type = "weibo";
			Load_URL = "http://61.181.14.184:8084/NewReadman/getSinaweibo.do?username=";
			//http://61.181.14.184:8084/NewReadman/getSinaweibo.do?username=
			break;

		default:
			break;
		}
	}
	public void initWebView(int i){
		setStatus(i);
		switch (i) {
		case 1:
			
			//setView(mViews.get(2), Load_URL+"marklee&page=2");
			Log.v(TAG, "flip initwebview setview 2-----------------------------");
			setView(mViews.get(2), type, 2, Load_URL+username+"&page=2");
			break;
		case 2:
			//setView(mViews.get(2), Load_URL+"marklee&size=5");
			setView(mViews.get(2), type, 2,Load_URL+username+"&size=5");
			
			break;

		default:
			break;
		}
	}
	
	public View setView(View convertView,String type,int page,String url) {
		// TODO Auto-generated method stub
		if(convertView == null){
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.flipper_sub_webview, null);
		}
			mWebView wv = (mWebView)convertView.findViewById(R.id.mWebView);
			Log.v(TAG, "CurrentURL:"+wv.getUrl());
			if(url.equals(wv.getUrl())){
				
			}else{
			wv.clearView();
			
			//wv.loadUrl(url);
			wv.loadUrl(type, page,url);
			}
			
		return convertView;
	}
	
	private List<View> views;
	/**
	 * Used to inflate the Workspace from XML.
	 * @param context
	 * @param attrs
	 */
	public mFlipPageWidget(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		
	}

	/**
	 * Used to inflate the Workspace from XML.
	 */
	public mFlipPageWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mDefaultScreen = 1;
		initView();
		
		
	}
	


	private void initView() {
		Log.v(TAG, "initView");
		mScroller = new Scroller(getContext());
		mCurrentScreen = mDefaultScreen;
		Log.v(TAG, "initView:mCurrentScreen="+mCurrentScreen);
		final ViewConfiguration configuration = ViewConfiguration
				.get(getContext());
		mDefaultSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		mTouchSlop = mDefaultSlop;
	}

	
	
	private String getNextUrl(){
		switch (STATUS) {
		case 1://news
			
			return Load_URL+username+"&page="+(mCPage+1);
		case 2:
			return Load_URL+username+"&size="+(mCSize+4);
		default:
			return "";
		}
		
	}
	private String getPreUrl(){
		switch (STATUS) {
		case 1://news
			
			return Load_URL+username+"&page="+(mCPage-1);
		case 2:
			
			return Load_URL+username+"&size="+(mCSize-4);
			
		default:
			return "";
		}
		
	}
	@Override
	public void computeScroll() {
//		Log.v(TAG, "computeScroll");
		if (mScroller.computeScrollOffset()) {
			if ((isAddFinish)
					&& (mScroller.getCurrX() == (mScroller.getFinalX()))) {
				if (mNextDataIndex > mDataIndex) {
					Log.v(TAG, "mCurrentPage:"+mCPage);
					Log.v(TAG, "m Total Page:"+mTPage);
					
					View convertView = mViews.get(mCView);
					if(convertView == null){
						LayoutInflater inflater = LayoutInflater.from(getContext());
						convertView = inflater.inflate(R.layout.flipper_sub_webview, null);
					}
						mWebView wv = (mWebView)convertView.findViewById(R.id.mWebView);
						Log.v(TAG, "!!!!!!!!!!!!!!!!!requestFocus"+mCView);
//						wv.requestFocus();
					
					
					if(mCPage >= mTPage){
						Toast.makeText(getContext(), "最后一页", Toast.LENGTH_SHORT).show();
					}else{
						
						mCPage++;
						mCSize+= 4;
	                    mCView = mCPage%3;
	                    //String url = "http://61.181.14.184:8084/NewReadman/getRss.do?username=marklee&page="+(mCPage+1);
	                    String url = getNextUrl();
						if( mCView == 2){
								
		                    	Log.v("Flipper", "#########webview 0 load url:"+url);
//		                    	setView(views.get(0), "http://61.181.14.184:8084/demo/news"+(currentPage+1)+".html");
		                    	//setView(mViews.get(0),url);
		                    	setView(mViews.get(0), type, mCPage+1, url);
		                   }else{
		                    	Log.v("Flipper", "#########webview "+(mCView+1)+" load url:"+url);
//		                    	setView(views.get((currentView+1)), "http://61.181.14.184:8084/demo/news"+(currentPage+1)+".html");
		                    	//setView(mViews.get(mCView+1),url);
		                    	setView(mViews.get(mCView+1), type, mCPage+1, url);
		                    }
							
						scrollToNext(mNextDataIndex);
						
						
						
					}
					Log.v(TAG, "mCurrentPage====================:"+mCPage);
					
				} else if (mNextDataIndex < mDataIndex) {
					
					Log.v(TAG, "mCurrentPage:"+mCPage);
					Log.v(TAG, "m Total Page:"+mTPage);
					
					View convertView = mViews.get(mCView);
					if(convertView == null){
						LayoutInflater inflater = LayoutInflater.from(getContext());
						convertView = inflater.inflate(R.layout.flipper_sub_webview, null);
					}
						mWebView wv = (mWebView)convertView.findViewById(R.id.mWebView);
						Log.v(TAG, "!!!!!!!!!!!!!!!!!requestFocus"+mCView);
//						wv.requestFocus();
					
					if(mCPage <= 1){
						Toast.makeText(getContext(), "最前页", Toast.LENGTH_SHORT).show();
					}else{
						
						mCPage--;
						mCSize-=4;
	                    mCView = mCPage%3;
	                    //String url = "http://61.181.14.184:8084/NewReadman/getRss.do?username=marklee&page="+(mCPage-1);
	                    String url = getPreUrl();
						if( mCView == 0){
		                    	Log.v("Flipper", "#########webview 2 load url !");
//		                    	setView(views.get(0), "http://61.181.14.184:8084/demo/news"+(currentPage+1)+".html");
		                    	//setView(mViews.get(2),url);
		                    	setView(mViews.get(2),type, mCPage-1, url);
		                   }else{
		                    	Log.v("Flipper", "#########webview "+(mCView-1)+" load url !");
//		                    	setView(views.get((currentView+1)), "http://61.181.14.184:8084/demo/news"+(currentPage+1)+".html");
		                    	//setView(mViews.get(mCView-1),url);
		                    	setView(mViews.get(mCView-1), type, mCPage-1, url);
		                    }
						
						scrollToPrev(mNextDataIndex);
					}
				}
				if (isChangeScreen) {
					if (mListener != null) {
						mListener.onFlipCompleted();
					}
					if (mNextScreen != INVALID_SCREEN) {
						mCurrentScreen = Math.max(0,
								Math.min(mNextScreen, getChildCount() - 1));
						mNextScreen = INVALID_SCREEN;
						
						//Log.v(TAG, "conputeScroll,isChangeScreen true, mCurrentScreen="+mCurrentScreen);
						
					}
				}
				isAddFinish = false;
			}
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	ArrayList<PointF> graphics = new ArrayList<PointF>();
	/*
	 * (non-Javadoc)
	 * @see android.view.ViewGroup#dispatchDraw(android.graphics.Canvas)
	 * 绘制ViewGroup的View
	 */
	@Override
	public void dispatchDraw(Canvas canvas) {
//		if(fenye == true){
//			
//			
//		}else{
		
//			Log.v("touch", "dispatchDraw");
			
			boolean restore = false;
			boolean fastDraw = mTouchState != TOUCH_STATE_SCROLLING
					&& mNextScreen == INVALID_SCREEN;
			// If we are not scrolling or flinging, draw only the current screen
			if (fastDraw) {
				// mNextScreen == Invalid  &&  mTouchState == TOUCH_STATE_REST
				final int count = getChildCount();
				for (int i = 0; i < count; i++) {
					drawChild(canvas, getChildAt(i), getDrawingTime());
				}
			} else {
				final long drawingTime = getDrawingTime();
				// If we are flinging, draw only the current screen and the target
				// screen
				if (mNextScreen >= 0 && mNextScreen < getChildCount()
						&& Math.abs(mCurrentScreen - mNextScreen) == 1) {
					drawChild(canvas, getChildAt(mCurrentScreen), drawingTime);
					Log.v(TAG, "dispatchDraw,not fastDraw,mCurrentScrent="+mCurrentScreen);
					drawChild(canvas, getChildAt(mNextScreen), drawingTime);
				} else {
					// If we are scrolling, draw all of our children
					final int count = getChildCount();
					for (int i = 0; i < count; i++) {
						drawChild(canvas, getChildAt(i), drawingTime);
					}
				}
			}
			
			if (restore) {
				canvas.restore();
			}
//		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.View#onMeasure(int, int)
	 * 重写了父类的onMeasure()，主要功能是设置屏幕的显示大小。由每个child的measure()方法设置
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//Log.v(TAG, "onMeasure");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		// 仅当ViewGroup为fill_parent才处于EXACTLY模式
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"Workspace can only be used in EXACTLY mode.");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"Workspace can only be used in EXACTLY mode.");
		}

		// The children are given the same width and height as the workspace
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		if (mFirstLayout) {
			scrollTo(mCurrentScreen * width, 0);
			Log.v(TAG, "onMeasure,mFirstLayout true,mCurrentScreen="+mCurrentScreen);
			mFirstLayout = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
	 * 重写了父类的onLayout()，主要功能是设置屏幕的显示位置。由child的layout()方法设置
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		//Log.v(TAG, "onLayout");
		int childLeft = 0;
		int tempWidth = 0;
		// 设置布局，将子视图顺序横屏排列
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				//Log.v(TAG, "childWidth:"+childWidth);
				tempWidth = childWidth;
				childLeft = (mDataIndex + i - 1) * childWidth;
				child.layout(childLeft, 0, childLeft + childWidth,
						child.getMeasuredHeight());
				//Log.v(TAG, "child.getMeasuredHeight:"+child.getMeasuredHeight());
			}
		}
		snapToScreen(mDataIndex, tempWidth);
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.ViewGroup#dispatchUnhandledMove(android.view.View, int)
	 * 重写了父类的onLayout()，主要功能是设置屏幕的显示位置。由child的layout()方法设置
	 */
	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		if (direction == View.FOCUS_LEFT) {
			if (getCurrentScreen() > 0) {
				snapToScreen(getCurrentScreen() - 1);
				return true;
			}
		} else if (direction == View.FOCUS_RIGHT) {
			if (getCurrentScreen() < getChildCount() - 1) {
				snapToScreen(getCurrentScreen() + 1);
				return true;
			}
		}
		return super.dispatchUnhandledMove(focused, direction);
	}

	private int getCurrentScreen() {
		for (int i = 0; i < getChildCount(); i++) {
			Log.v(TAG, "getCurrent,getChildAt"+i+".id="+getChildAt(i).getId()+"  mDataIndex="+mDataIndex);
			if (getChildAt(i).getId() == mDataIndex) {
				Log.v(TAG, "getCurrent,return  id=mDataIndex="+mDataIndex);
				return i;
			}
		}
		Log.v(TAG, "getCurrent,return  mCurrentScreen"+mCurrentScreen);
		return mCurrentScreen;
	}

	private int getNextScreen(int dataIndex) {
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i).getId() == dataIndex) {
				return i;
			}
		}
		return mNextScreen;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 * 重写了父类的onInterceptTouchEvent()，主要功能是在onTouchEvent()方法之前处理
	 * touch事件。包括：down、up、move事件。
	 * 当onInterceptTouchEvent()返回true时进入onTouchEvent()。
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		Log.v("touch", "onInterceptTouchEvent");
		if(ev.getX() >500 && ev.getY() < 200){
			fenye = true;
			Log.v("touch", "onInterceptTouchEvent fen ye!! return false");
			
		}
		if(ev.getX() <100 && ev.getY() < 200){
			fenye = true;
			Log.v("touch", "onInterceptTouchEvent fen ye!! return false");
			
		}
		if(ev.getY() > 600){
			fenye = false;
		}
			
		if(fenye == true){
			Log.v("touch", "fenye!!!!!!!!!!!!!!!");
			return false;
		}else{
			
			Log.v("touch", "flipper");
			
			final int action = ev.getAction();
			if ((action == MotionEvent.ACTION_MOVE)
					&& (mTouchState != TOUCH_STATE_REST)) {
				return true;
			}
			final float x = ev.getX();
			final float y = ev.getY();
	
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				/*
				 * 记录xy与mLastMotionX、mLastMotionY差值的绝对值。xDiff和yDiff大于
				 * touchSlop时就认为界面拖动了足够大的距离，屏幕就可以移动了。
				 */
				final int xDiff = (int) Math.abs(x - mLastMotionX);
				final int yDiff = (int) Math.abs(y - mLastMotionY);
				final int touchSlop = mTouchSlop;
				boolean xMoved = (xDiff > touchSlop) && (xDiff >= 2 * yDiff);
				boolean yMoved = yDiff > touchSlop;
				if (xMoved || yMoved) {
					if (xMoved) {
						// Scroll if the user moved far enough along the X axis
						mTouchState = TOUCH_STATE_SCROLLING;
					}
				}
				break;
			case MotionEvent.ACTION_DOWN:
				// Remember location of down touch
				mLastMotionX = x;
				mLastMotionY = y;
				mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
						: TOUCH_STATE_SCROLLING;
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				// Release the drag
				mTouchState = TOUCH_STATE_REST;
				
				break;
			}
			Log.v("touch", "onInterceptTouchEvent return:"+(mTouchState != TOUCH_STATE_REST));
			
			return mTouchState != TOUCH_STATE_REST;
//			return true;
		}//end else
		
	}//end onInterceptTouchEvent

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 * 主要功能是处理onInterceptTouchEvent()返回值为true时传递过来的touch事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Log.v("touch", "onTouchEvent ");
			if(fenye == false){
				Log.v("touch", "onTouchEvent flipper");
			//		Log.v(TAG, "onTouchEvent");
			        if (mVelocityTracker == null) {    
			            mVelocityTracker = VelocityTracker.obtain();    
			        }    
			        mVelocityTracker.addMovement(ev);   
					
					final int action = ev.getAction();
					final float x = ev.getX();
			
					switch (action) {
					case MotionEvent.ACTION_DOWN:
						Log.v("touch", "onTouchEvent ACTION_DOWN");
						if (!mScroller.isFinished()) {
							mScroller.abortAnimation();
						}
						// Remember where the motion event started
						mLastMotionX = x;
						break;
					case MotionEvent.ACTION_MOVE:
						Log.v("touch", "onTouchEvent ACTION_MOVE");
						// 手指拖动屏幕的处理
						if ((mTouchState == TOUCH_STATE_SCROLLING) && ((!isAddFinish))) {
							// Scroll to follow the motion event
							final int deltaX = (int) (mLastMotionX - x);
							mLastMotionX = x;
							if (deltaX < 0) {
								
								if ((isFlipPre) && (getScrollX() > 0)) {
									if(mCPage <= 1){
										if(flag){
											Toast.makeText(getContext(), "最前页", Toast.LENGTH_SHORT).show();
											Log.v(TAG, "toase");
											flag = false;
										}
										
									}else{
									scrollBy(Math.max(-1 * getScrollX(), deltaX), 0);
									}
								}
							} else if (deltaX > 0) {
								final int availableToScroll = getChildAt(
										getChildCount() - 1).getRight()
										- getScrollX() - getWidth();
								if ((availableToScroll > 0) && (isFlipNext)) {
									if(mCPage >=mTPage){
										if(flag){
											Toast.makeText(getContext(), "最后一页", Toast.LENGTH_SHORT).show();
											Log.v(TAG, "toase");
											flag = false;
										}
										
									}else{
										scrollBy(Math.min(availableToScroll, deltaX), 0);
									}
								}
							}
						}
						break;
					case MotionEvent.ACTION_UP:
						Log.v("touch", "onTouchEvent ACTION_UP");
						final VelocityTracker velocityTracker = mVelocityTracker;       
			            velocityTracker.computeCurrentVelocity(1000);   
			            int velocityX = (int) velocityTracker.getXVelocity();       
			            
						// 弹起手指后，切换屏幕的处理
						if (mTouchState == TOUCH_STATE_SCROLLING) {
							snapToDestination();
						}
						mTouchState = TOUCH_STATE_REST;
						break;
					case MotionEvent.ACTION_CANCEL:
						mTouchState = TOUCH_STATE_REST;
						break;
					}
					return true;
		}else{
			Log.v(TAG, "onTouchEvent");
	        if (mVelocityTracker == null) {    
	            mVelocityTracker = VelocityTracker.obtain();    
	        }    
	        mVelocityTracker.addMovement(ev);   
			
			final int action = ev.getAction();
			final float x = ev.getX();
	
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				Log.v("touch", "onTouchEvent ACTION_DOWN");
				
				break;
			case MotionEvent.ACTION_MOVE:
				Log.v("touch", "onTouchEvent ACTION_MOVE");
				
				graphics.add(new PointF(ev.getX(),ev.getY()));
	            this.postInvalidate();
				
				break;
			case MotionEvent.ACTION_UP:
				Log.v("touch", "onTouchEvent ACTION_UP");
				
				break;
			case MotionEvent.ACTION_CANCEL:
				
				break;
			}
			return true;
		}
	}

	private void snapToDestination() {
		if (mListener != null) {
			mListener.onFlipStarted();
		}

		// 计算应该去哪个屏
		final int screenWidth = getWidth();
		final int whichScreen = (getScrollX() + (screenWidth / 2))
				/ screenWidth;
		// float a1 = (float) getScrollX();
		// float a2 = (float) screenWidth / 2;
		// float a3 = (float) (getScrollX() + (screenWidth / 2)) / screenWidth;
		// Log.v("a1:" + a1 + "; a2:" + a2 + "; a3:" + a3);
		// 切换
		snapToScreen(whichScreen);
	}

	void snapToScreen(int dataIndex) {
		if (!mScroller.isFinished())
			return;
		isAddFinish = true;
		mNextDataIndex = dataIndex;

		boolean changingScreens = dataIndex != mDataIndex;
		View focusedChild = getFocusedChild();
		if (focusedChild != null && changingScreens) {
			focusedChild.clearFocus();
		}
		final int cx = getScrollX(); // 启动的位置
		final int newX = dataIndex * getWidth(); // 最终停留的位置
		final int delta = newX - cx; // 滑动的距离，正值是往左滑<—，负值是往右滑—>
		if(delta > 0){
			mCView = (mCView+1)%3;
		}else{
		}
		mScroller.startScroll(cx, 0, delta, 0, Math.abs(delta) * 4);
//		webviews.get(mCView).requestFocus();
		invalidate();
	}

	private void snapToScreen(int dataIndex, int width) {
		if (getFocusedChild() != null && dataIndex != this.mDataIndex) {
			getFocusedChild().clearFocus();
		}
		final int delta = dataIndex * width - getScrollX();
		mScroller.startScroll(getScrollX(), 0, delta, 0, 500);
		invalidate();
	}

	private void scrollToNext(int dataIndex) {
		if (!isFlipPre)
			isFlipPre = true;

		isChangeScreen = true;
		mNextScreen = getNextScreen(dataIndex);
		mCurrentScreen = getCurrentScreen();
		mDataIndex = dataIndex;

		if (isFlipNext) {
			View view = getChildAt(0);
			removeViewAt(0);
			addView(view, 2);
			setViewId();
			invalidate();
//			 try {
//			 Thread.currentThread();
//			 Thread.sleep(1000);
//			 } catch (InterruptedException ex) {
//			 ex.printStackTrace();
//			 }
		}
	}


	private void scrollToPrev(int dataIndex) {
		if (!isFlipNext)
			isFlipNext = true;

		isChangeScreen = true;
		mNextScreen = getNextScreen(dataIndex);
		mCurrentScreen = getCurrentScreen();
		mDataIndex = dataIndex;

		if (isFlipPre) {
			View view = getChildAt(2);
			removeViewAt(2);
			addView(view, 0);
			setViewId();
			invalidate();
//			 try {
//			 Thread.currentThread();
//			 Thread.sleep(1000);
//			 } catch (InterruptedException ex) {
//			 ex.printStackTrace(); // 抛出错误信息
//			 }
		}
	}

	public void setViewId() {
		getChildAt(0).setId(mDataIndex - 1);
		getChildAt(1).setId(mDataIndex);
		getChildAt(2).setId(mDataIndex + 1);
	}

	public void addListener(IFlipWidgetListener listener) {
		this.mListener = listener;
	}

	public View getCurrentView() {
		if (mDataIndex == -1)
			return getChildAt(mDefaultScreen);

		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i).getId() == (mDataIndex)) {
				return getChildAt(i);
			}
		}
		return null;
	}

	public View getPreView() {
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i).getId() == (mDataIndex - 1)) {
				return getChildAt(i);
			}
		}
		return null;
	}

	public View getNextView() {
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i).getId() == (mDataIndex + 1)) {
				return getChildAt(i);
			}
		}
		return null;
	}

	public void switchToPage(int direction) {
		switch (direction) {
		case FLIP_DIRECTION_PRE:
			snapToScreen(mDataIndex - 1);
			break;
		case FLIP_DIRECTION_NEXT:
			snapToScreen(mDataIndex + 1);
			break;
		default:
			break;
		}
	}

	public void stopFlipPage(int direction) {
		switch (direction) {
		case FLIP_DIRECTION_PRE:
			isFlipPre = false;
			break;
		case FLIP_DIRECTION_NEXT:
			isFlipNext = false;
			break;
		default:
			break;
		}
	}

	public void startFlipPage(int direction) {
		switch (direction) {
		case FLIP_DIRECTION_PRE:
			isFlipPre = true;
			break;
		case FLIP_DIRECTION_NEXT:
			isFlipNext = true;
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Log.v("touch", "onDraw !!!!!!");
		Paint paint;
		  
            paint = new Paint(); //设置一个笔刷大小是3的黄色的画笔
            paint.setColor(Color.YELLOW);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(3); 
            
            canvas.drawCircle(100, 100, 90, paint); 
	}

}
