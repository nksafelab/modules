package com.jinke.flipboard.widget;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;

import com.jinke.flipboard.download.DownLoadThread;
import com.jinke.flipboard.download.IdentifyUtil;
import com.jinke.readman.content;
import com.jinke.readman.content_fenye;
import com.jinke.readman.flipper;
import com.jinke.readman.flipper.OverCallback;
import com.jinke.readman.flipper.TimeCallback;
import com.jinke.readman.flipper.searchCallback;
import com.jinke.readman.bean.User;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Scroller;
import android.widget.Toast;

/**
 * 
*@ClassName:mWebView
*@Description:TODO(what to do)
*@author: nk_jinke_yujinyang
*@date:2011-9-21下午03:56:20
*@tags:
 */
public class mWebView extends WebView{
	
	private static final String TAG = "mWebView";
	public boolean reload = false;
	public String mCurrentUrl;
	private int index = -1;    
	private String cachePath = "/data/data/com.jinke.readman/cache/";
	private flipper mflipper;
	private boolean fenye = false;
	
	
	
	public void setFlipper(flipper mf){
		Log.e("AndroidRuntime", "set null:"+(mf == null));
		this.mflipper = mf;
	}
	
	@Override
	public void reload() {
		// TODO Auto-generated method stub
		super.reload();
		
	}
	
	@Override
	public void loadUrl(String url) {
		Log.v(TAG, "override loadUrl() called");
		if("index".equals(url)){
			Log.v(TAG, "	load index.html");
			if(IdentifyUtil.exits(cachePath+"index.html")){
				super.loadUrl("file://"+cachePath+"index.html");
				Log.v(TAG, "loadUrl() called; use cache,load:"+"file://"+cachePath+"index.html");
			}else{
				super.loadUrl("http://61.181.14.184:8084/NewReadman/flipboard.jsp?username=");
				Log.v(TAG, "loadUrl() called;"+"file://"+cachePath+"index.html"+" not exit");
				Log.v(TAG, "loadUrl() called;"+" load:"+"http://61.181.14.184:8084/NewReadman/flipboard.jsp?username=marklee");
			}
		}
		else{
			Log.v(TAG, "override loadUrl() called"+url);
			super.loadUrl(url);
		}
	}
	
	public void loadUrl(String type,int page,String url) {
		// TODO Auto-generated method stub
		Log.v("mWebView", "mWebView loadUrl");
		mCurrentUrl = url;
		if("rss".equals(type)){
			Log.v("mWebView", "mWebView load rss Url");
			if(IdentifyUtil.exits(cachePath+"rss"+page+".html")){
				//loadUrl("file:///data/data/aa.a/cache/1.html");
				url = "file://"+cachePath+"rss"+page+".html";
				loadUrl(url);
				Log.v("CacheExit", "page:"+page+"Exit");
				Log.v("CacheExit", "LoadURL:"+url);
			}else{ 
				Log.v("CacheExit", "page:"+page+"NOT Exit");
				Log.v("CacheExit", "LoadURL:"+url);
				loadUrl(url);
			}
			
		}else if("weibo".equals(type)){
			page = 1 + (page-1)*4;
			if(IdentifyUtil.exits(cachePath+"weibo"+page+".html")){
				//loadUrl("file:///data/data/aa.a/cache/1.html");
				url = "file://"+cachePath+"weibo"+page+".html";
				loadUrl(url);
				Log.v("CacheExit", "weibo:"+page+"Exit");
				Log.v("CacheExit", "LoadURL:"+url);
			}else{
				Log.v("CacheExit", "weibo:"+page+"NOT Exit");
				Log.v("CacheExit", "LoadURL:"+url);
				loadUrl(url);
			}
			
		}
	}
	
	public mWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mPath0 = new Path();
		mPath1 = new Path();
		createDrawable();
		over = false;
		mPaint = new Paint();
//		mPaint.setStyle(Paint.Style.FILL);

		ColorMatrix cm = new ColorMatrix();
		float array[] = { 0.55f, 0, 0, 0, 80.0f, 0, 0.55f, 0, 0, 80.0f, 0, 0,
				0.55f, 0, 80.0f, 0, 0, 0, 0.2f, 0 };
		cm.set(array);
		mColorMatrixFilter = new ColorMatrixColorFilter(cm);
		mMatrix = new Matrix();
		mScroller = new Scroller(getContext());

		mTouch.x = 0.01f; // ����x,yΪ0,�����ڵ����ʱ��������
		mTouch.y = 0.01f;
		// TODO Auto-generated constructor stub
	}
	
	public mWebView(Context context , int index) {
		super(context);
		over = false;
		this.index = index;
		mPath0 = new Path();
		mPath1 = new Path();
		createDrawable();

		mPaint = new Paint();
//		mPaint.setStyle(Paint.Style.FILL);

		ColorMatrix cm = new ColorMatrix();
		float array[] = { 0.55f, 0, 0, 0, 80.0f, 0, 0.55f, 0, 0, 80.0f, 0, 0,
				0.55f, 0, 80.0f, 0, 0, 0, 0.2f, 0 };
		cm.set(array);
		mColorMatrixFilter = new ColorMatrixColorFilter(cm);
		mMatrix = new Matrix();
		mScroller = new Scroller(getContext());

		mTouch.x = 0.01f; // ����x,yΪ0,�����ڵ����ʱ��������
		mTouch.y = 0.01f;
		// TODO Auto-generated constructor stub
	}

	public mWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.index = index;
		over = false;
		mPath0 = new Path();
		mPath1 = new Path();
		createDrawable();

		mPaint = new Paint();
//		mPaint.setStyle(Paint.Style.FILL);

		ColorMatrix cm = new ColorMatrix();
		float array[] = { 0.55f, 0, 0, 0, 80.0f, 0, 0.55f, 0, 0, 80.0f, 0, 0,
				0.55f, 0, 80.0f, 0, 0, 0, 0.2f, 0 };
		cm.set(array);
		mColorMatrixFilter = new ColorMatrixColorFilter(cm);
		mMatrix = new Matrix();
		mScroller = new Scroller(getContext());

		mTouch.x = 0.01f; // ����x,yΪ0,�����ڵ����ʱ��������
		mTouch.y = 0.01f;
		// TODO Auto-generated constructor stub
	}

	public mWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		over = false;
		this.index = index;
		mPath0 = new Path();
		mPath1 = new Path();
		createDrawable();

		mPaint = new Paint();

		ColorMatrix cm = new ColorMatrix();
		float array[] = { 0.55f, 0, 0, 0, 80.0f, 0, 0.55f, 0, 0, 80.0f, 0, 0,
				0.55f, 0, 80.0f, 0, 0, 0, 0.2f, 0 };
		cm.set(array);
		mColorMatrixFilter = new ColorMatrixColorFilter(cm);
		mMatrix = new Matrix();
		mScroller = new Scroller(getContext());

		mTouch.x = 0.01f;
		mTouch.y = 0.01f;
		// TODO Auto-generated constructor stub
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	
	//==================================
	
	private int mWidth = 600;
	private int mHeight = 1024;
	private int mCornerX = 0; // ��ק���Ӧ��ҳ��
	private int mCornerY = 0;
	private Path mPath0;
	private Path mPath1;
	
//	Bitmap mCurPageBitmap = null; // ��ǰҳ
//	Bitmap mNextPageBitmap = null;

	PointF mTouch = new PointF(); // ��ק��
	PointF mBezierStart1 = new PointF(); // �����������ʼ��
	PointF mBezierControl1 = new PointF(); // ��������߿��Ƶ�
	PointF mBeziervertex1 = new PointF(); // ��������߶���
	PointF mBezierEnd1 = new PointF(); // ��������߽����

	PointF mBezierStart2 = new PointF(); // ��һ�����������
	PointF mBezierControl2 = new PointF();
	PointF mBeziervertex2 = new PointF();
	PointF mBezierEnd2 = new PointF();

	float mMiddleX;
	float mMiddleY;
	float mDegrees;
	float mTouchToCornerDis;
	ColorMatrixColorFilter mColorMatrixFilter;
	Matrix mMatrix;
	float[] mMatrixArray = { 0, 0, 0, 0, 0, 0, 0, 0, 1.0f };

	boolean mIsRTandLB; // �Ƿ�������������
	float mMaxLength = (float) Math.hypot(mWidth, mHeight);
	int[] mBackShadowColors;
	int[] mFrontShadowColors;
	GradientDrawable mBackShadowDrawableLR;
	GradientDrawable mBackShadowDrawableRL;
	GradientDrawable mFolderShadowDrawableLR;
	GradientDrawable mFolderShadowDrawableRL;

	GradientDrawable mFrontShadowDrawableHBT;
	GradientDrawable mFrontShadowDrawableHTB;
	GradientDrawable mFrontShadowDrawableVLR;
	GradientDrawable mFrontShadowDrawableVRL;

	Paint mPaint;
	public boolean ondrawflag = false;
	private int time = 5;
	Scroller mScroller;
	
	private boolean right = true;
	private boolean over = false;
	/**
	 * Author : hmg25 Version: 1.0 Description : ������ק���Ӧ����ק��
	 */
	public void calcCornerXY(float x, float y) {
		if (x <= mWidth / 2)
			mCornerX = 0;
		else
			mCornerX = mWidth;
		if (y <= mHeight / 2)
			mCornerY = 0;
		else
			mCornerY = mHeight;
		
		if ((mCornerX == 0 && mCornerY == mHeight)
				|| (mCornerX == mWidth && mCornerY == 0))
			mIsRTandLB = true;
		else
			mIsRTandLB = false;
		
		Log.e("touch", "mIsRTandLB"+mIsRTandLB);
	}

	private void dispearfenye(){
		fenye = false;
		over = false;
		ondrawflag = false;
		scb.callback(false);
		ocb.callback(false);
		Log.v("touch", "dispear!!!!!!!!!!!!!!!!!!!!!!!set fenye ="+fenye);
		invalidate();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		float x = event.getX();
		float y = event.getY();
		Log.v("bug", "x:"+x+" y:"+y+" fenye:"+fenye);
		
		
		
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
//			Log.e("bug", "mWebView ACTION_MOVE"+index);
			if(y>600){
				Log.v("bug", "ACTION_MOVE y>600 return true!!!!!!!!!!!!!!!!!!!!");
				return true;
			}
			if(over){
				Log.v("bug", "ACTION_MOVE over true,return true");
				return true;
			}else{
				Log.v("bug", "ACTION_MOVE over false, doTouchEvent");
				return doTouchEvent(event);
			}
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Log.e("bug", "mWebView ACTION_DOWN over:"+over);
			if(over){
				if(y>600){
					Log.e("bug", "mWebView ACTION_DOWN y>600 dispearfenye()");
					dispearfenye();
					return true;
				}
				
				if((x < 100 && y > 533 && y < 600)){
					fenye = true;
					over = false;
					Log.e("bug", "mWebView ACTION_DOWN 收回");
				}
				if((x >500 && y > 533 && y < 600)){
					fenye = true;
					over = false;
					Log.e("bug", "mWebView ACTION_DOWN 收回");
				}
				Log.e("bug", "mWebView ACTION_DOWN over"+over+" return true;");
				
				return true;
			}else{
				
				if(x>500 && y<200){
//					Log.e("touch", "mWebView ACTION_DOWN"+index+"fenye = true!!!!!!!!!!!!!!!!!!");
					fenye = true;
					over = false;
					right = true;
					Log.v("bug", "apear x>400 y<200!!!!!!!!!!!!!!!!!set fenye ="+fenye +" set over=false");
				}
				if(x<100 && y<200){
//					Log.e("touch", "mWebView ACTION_DOWN"+index+"fenye = true!!!!!!!!!!!!!!!!!!");
					fenye = true;
					over = false;
					right = false;
					Log.v("touch", "apear x>400 y<200!!!!!!!!!!!!!!!!!set fenye ="+fenye);
				}
				if(fenye == false){
					return super.onTouchEvent(event);
				}
				
				Log.e("touch", "mWebView ACTION_DOWN"+index);
				if(mflipper == null){
					Log.e("AndroidRuntime", "null:"+(mflipper == null));
				}else{
					ondrawflag = true;
					Log.e("AndroidRuntime", "null:"+(mflipper == null));
					View view = mflipper.getWindow().getDecorView();
			        view.setDrawingCacheEnabled(true);
			        view.buildDrawingCache();
			        Bitmap b1 = view.getDrawingCache();
					
			        // 获取状态栏高度
			        Rect frame = new Rect();
			        mflipper.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			        int statusBarHeight = frame.top;
		
			        // 获取屏幕长和高
			        int width = mflipper.getWindowManager().getDefaultDisplay().getWidth();
			        //Toast.makeText(flipper.this, "width:"+width, Toast.LENGTH_SHORT).show();
			        int height = mflipper.getWindowManager().getDefaultDisplay()
			                .getHeight();
			        // 去掉标题栏
			        view.destroyDrawingCache();
			        abortAnimation();
				}
				return doTouchEvent(event);
			}
			
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			Log.e("touch", "mWebView ACTION_UP mTouchToCornerDis:"+mTouchToCornerDis);
			if(over){
				
				Log.e("touch", "mWebView ACTION_UP over:================="+over);
				Log.v("touch", "x:"+x+" y:"+y+" over:"+over);
				if(right)
					whichTime(x,y);
				else{
					whichTheme(x,y);
				}
				
				return true;
			}else{
				return doTouchEvent(event);
			}
		}
		return doTouchEvent(event);
	}
	
	private void whichTime(float x,float y){
		int which = 0;
		String size = "";
		if((x < 300 && y > 533 && y < 600) || (x > 300 && y > 533 && y < 600)){
			which = 0;
			Toast.makeText(mflipper, "收回分页", Toast.LENGTH_SHORT).show();
			
			fenye = true;
			over = false;
			
		}else if ( x>67 && x <200 && y<600 && y>475 ){
			which = 5;
			size = getSize(which);
			Toast.makeText(mflipper, "5分钟"+size, Toast.LENGTH_SHORT).show();
		}else if ( x>125 && x <225 && y<475 && y>418 ){
			which = 10;
			size = getSize(which);
			Toast.makeText(mflipper, "10分钟", Toast.LENGTH_SHORT).show();
		}else if ( x>182 && x <282 && y<418 && y>360 ){
			which = 15;
			size = getSize(which);
			Toast.makeText(mflipper, "15分钟", Toast.LENGTH_SHORT).show();
		}else if ( x>240 && x <340 && y<360 && y>303 ){
			which = 20;
			size = getSize(which);
			Toast.makeText(mflipper, "20分钟", Toast.LENGTH_SHORT).show();
		}else if ( x>297 && x <397 && y<303 && y>245 ){
			which = 25;
			size = getSize(which);
			Toast.makeText(mflipper, "25分钟", Toast.LENGTH_SHORT).show();
		}else if ( x>355 && x <455 && y<245 && y>188 ){
			which = 30;
			size = getSize(which);
			Toast.makeText(mflipper, "30分钟", Toast.LENGTH_SHORT).show();
		}else if ( x>412 && x <512 && y<188 && y>130 ){
			which = 35;
			size = getSize(which);
			Toast.makeText(mflipper, "35分钟", Toast.LENGTH_SHORT).show();
		}else if ( x>470 && x <570 && y<130 && y>73 ){
			which = 40;
			size = getSize(which);
			Toast.makeText(mflipper, "40分钟", Toast.LENGTH_SHORT).show();
		}else if ( x>527 && x <600 && y<73 && y>0 ){
			which = 0;
			Toast.makeText(mflipper, "开始分页", Toast.LENGTH_SHORT).show();
		}
		if(which != 0){
			
			tcb.callback(Integer.parseInt(size.trim()));
			
//		String url = "http://61.181.14.184:8084/NewReadman/getWeiboByTime.do?token="+User.getToken()+"&tokenpwd="+User.getTokenPWD()+"&time="+which;
//		Log.e("touch", url);
//		Intent mIntent = new Intent();
//		mIntent.putExtra("url", url);
//		mIntent.setClass(mflipper, content_fenye.class);
//		mflipper.startActivity(mIntent);
		}
	}
	private void whichTheme(float x,float y){
		if ( x>145 && x <245 && y<244 && y>194 ){
			Toast.makeText(mflipper, "收藏", Toast.LENGTH_SHORT).show();
		}else if ( x>125 && x <225 && y<475 && y>418 ){
			Toast.makeText(mflipper, "10分钟", Toast.LENGTH_SHORT).show();
		}
		String url = "http://61.181.14.184:8084/NewReadman/getShoucang.do?token="+User.getToken()+"&tokenpwd="+User.getTokenPWD();
		Log.e("touch", url);
		Intent mIntent = new Intent();
		mIntent.putExtra("url", url);
		mIntent.setClass(mflipper, content_fenye.class);
		mflipper.startActivity(mIntent);
	}
	static final float width_test = (float) 599.999;
	
	public boolean doTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.e("bug", "doTouchEvent");
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			Log.e("touch", "mWebView ACTION_MOVE"+index);
			mTouch.x = event.getX();
			mTouch.y = event.getY();
			
//			Log.e("XY", "X:"+mTouch.x+" Y:"+mTouch.y+" X+Y:"+(mTouch.x+mTouch.y));
			
			float x = width_test - event.getX();
			float y = event.getY();
			
			if(y >width_test){
				return true;	
			}
			
			Log.v("Move", "x:"+mTouch.x+" y:"+mTouch.y);
			
			if(right){
				if(x > y) {
					mTouch.x = width_test -x;
					mTouch.y = x;
				}
				else{
					mTouch.x = width_test-y;
					mTouch.y = y;
				}
			}else{
				if(mTouch.x > mTouch.y) {
					
					mTouch.x = mTouch.y-0.01f;
				}
				else{
					mTouch.y = mTouch.x-0.01f;
				}
			}
			
			
			invalidate();
			mWebView.this.postInvalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Log.e("bug", "doTouchEvent ACTION_DOWN calcXY postInvalidate");
			time = 5;
			Log.e("touch", "mWebView ACTION_DOWN"+index);
			mTouch.x = event.getX();
			mTouch.y = event.getY();
			
			calcCornerXY(mTouch.x, mTouch.y);
			this.postInvalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
//			if (canDragOver()) {
			if(fenye){
//				startAnimation(1200);
				Log.e("touch", "ACTION_UP x:"+mTouch.x+" y:"+mTouch.y);
				if(mTouch.y< 300){
					Log.e("touch", "!!!!!!!!!!!!dispear ");
					dispearfenye();
				}else{
					if(right){
					mTouch.x = 10;
					mTouch.y = width_test-10;
					}else{
						mTouch.x = 590;
						mTouch.y = width_test-10;
					}
					over = true;
					ocb.callback(true);
				}
			} 
//			else {
//				mTouch.x = mCornerX - 0.09f;
//				mTouch.y = mCornerY - 0.09f;
//			}

			this.postInvalidate();
		}
		Log.e("touch", "!!!!!!!!!!!!!!!!!doTouch fenye="+fenye);
		if(fenye){
			return true;
		}else{
			return super.onTouchEvent(event);
		}
		
//		return true;
	}

	/**
	 * Author : hmg25 Version: 1.0 Description : ���ֱ��P1P2��ֱ��P3P4�Ľ������
	 */
	public PointF getCross(PointF P1, PointF P2, PointF P3, PointF P4) {
		PointF CrossP = new PointF();
		// ��Ԫ����ͨʽ�� y=ax+b
		float a1 = (P2.y - P1.y) / (P2.x - P1.x);
		float b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x);

		float a2 = (P4.y - P3.y) / (P4.x - P3.x);
		float b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x);
		CrossP.x = (b2 - b1) / (a1 - a2);
		CrossP.y = a1 * CrossP.x + b1;
		return CrossP;
	}

	private void calcPoints() {
		Log.e("touch", "!!!!!!!!!!!!!!!!!!!!!!!!calcPoints mCornerX:"+mCornerX+" mCornerY:"+mCornerY);
		mMiddleX = (mTouch.x + mCornerX) / 2;
		mMiddleY = (mTouch.y + mCornerY) / 2;
		 if(mCornerX - mMiddleX==0)
	        {
	                return;
	        }
		 
		 mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
			* (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
	mBezierControl1.y = mCornerY;
	mBezierControl2.x = mCornerX;
	
	
	        if(mCornerY - mMiddleY==0)
	        {
	                return;
	        }

		mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
				* (mCornerX - mMiddleX) / (mCornerY - mMiddleY);

		mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x)
				/ 2;
		mBezierStart1.y = mCornerY;

		if (mTouch.x > 0 && mTouch.x < mWidth) {
			if (mBezierStart1.x < 0 || mBezierStart1.x > mWidth) {
			}
		}
		mBezierStart2.x = mCornerX;
		mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y)
				/ 2;

		mTouchToCornerDis = (float) Math.hypot((mTouch.x - mCornerX),
				(mTouch.y - mCornerY));

		mBezierEnd1 = getCross(mTouch, mBezierControl1, mBezierStart1,
				mBezierStart2);
		mBezierEnd2 = getCross(mTouch, mBezierControl2, mBezierStart1,
				mBezierStart2);

		mBeziervertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4;
		mBeziervertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4;
		mBeziervertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4;
		mBeziervertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4;
	}

	private void drawCurrentPageArea(Canvas canvas, Path path,int time) {
		
		mPath0.reset();
		mPath0.moveTo(mBezierStart1.x, mBezierStart1.y);
		mPath0.quadTo(mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x,
				mBezierEnd1.y);
		mPath0.lineTo(mTouch.x, mTouch.y);
		mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y);
		mPath0.quadTo(mBezierControl2.x, mBezierControl2.y, mBezierStart2.x,
				mBezierStart2.y);
		mPath0.lineTo(mCornerX, mCornerY);
		mPath0.close();
		
		
		
		//实心
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.WHITE);
		
		canvas.drawPath(mPath0, mPaint);
		
		
		mPaint.setColor(Color.RED);
		mPaint.setStrokeWidth(3);
		mPaint.setTextSize(25);
		if(right){
			if(time != 0)
//				canvas.drawText(time+ "分钟",mTouch.x+10,mTouch.y-10,mPaint);
				canvas.drawText(getCurrentTime(-time)+" ("+time+ "分钟前)",mTouch.x+10,mTouch.y-10,mPaint);
			if(over){
				Log.e("touch", "textaear time:"+time + "x:"+mTouch.x+" y:"+mTouch.y);
			}
		}else{
			if(time == 10)
				canvas.drawText("搜索",mTouch.x-100,mTouch.y-10,mPaint);
			if(time == 20)
				canvas.drawText("话题",mTouch.x-100,mTouch.y-10,mPaint);
			if(time == 30)
				canvas.drawText("收藏",mTouch.x-100,mTouch.y-10,mPaint);
			
			if(over){
				Log.e("touch", "textaear time:"+time + "x:"+mTouch.x+" y:"+mTouch.y);
			}
		}
		//空心
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.GRAY);
		mPaint.setStrokeWidth(1);
		canvas.drawPath(mPath0, mPaint);
		
		canvas.save();
		canvas.clipPath(path, Region.Op.XOR);
		canvas.restore();
	}

	private void drawNextPageAreaAndShadow(Canvas canvas, Bitmap bitmap) {
		mPath1.reset();
		mPath1.moveTo(mBezierStart1.x, mBezierStart1.y);
		mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
		mPath1.lineTo(mBeziervertex2.x, mBeziervertex2.y);
		mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
		mPath1.lineTo(mCornerX, mCornerY);
		mPath1.close();

		mDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl1.x
				- mCornerX, mBezierControl2.y - mCornerY));
		int leftx;
		int rightx;
		GradientDrawable mBackShadowDrawable;
		if (mIsRTandLB) {
			leftx = (int) (mBezierStart1.x);
			rightx = (int) (mBezierStart1.x + mTouchToCornerDis / 4);
			mBackShadowDrawable = mBackShadowDrawableLR;
		} else {
			leftx = (int) (mBezierStart1.x - mTouchToCornerDis / 4);
			rightx = (int) mBezierStart1.x;
			mBackShadowDrawable = mBackShadowDrawableRL;
		}
		canvas.save();
		canvas.clipPath(mPath0);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
		mBackShadowDrawable.setBounds(leftx, (int) mBezierStart1.y, rightx,
				(int) (mMaxLength + mBezierStart1.y));
		mBackShadowDrawable.draw(canvas);
		canvas.restore();
	}

	public void setScreen(int w, int h) {
		mWidth = w;
		mHeight = h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		Log.e("touch", "mWebView onDraw");
		super.onDraw(canvas);
		
		if(!ondrawflag){
//			Log.e("touch", "mWebView onDraw return !!!!!!!!!!!!!!!!!!!!!!!!!");
			return;
		}
		
		float tempx = mTouch.x;
		float tempy = mTouch.y;
		
//		canvas.drawColor(0xFFAAAAAA);
		for (int i = 0; i<10; i++){
			Log.e("for", "for:"+i);
			calcPoints();
			if( i != 9)
				drawCurrentPageArea(canvas, mPath0,i*5);
			else
				drawCurrentPageArea(canvas, mPath0,0);
			
			if(right){
				mTouch.x += (575.0/10);
				mTouch.y -= (575.0/10);
			}else{
				mTouch.x -= (575.0/10);
				mTouch.y -= (575.0/10);	
			}
			if (mTouch.x>=600 && mTouch.x<0)
				break;
			
			if (mTouch.y<0 && mTouch.y>=600)
				break;
			
		}
		
		mTouch.x = tempx;
		mTouch.y = tempy;
		
	}

	/**
	 * Author : hmg25 Version: 1.0 Description : ������Ӱ��GradientDrawable
	 */
	private void createDrawable() {
		int[] color = { 0x333333, 0xb0333333 };
		mFolderShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, color);
		mFolderShadowDrawableRL
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFolderShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, color);
		mFolderShadowDrawableLR
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mBackShadowColors = new int[] { 0xff111111, 0x111111 };
		mBackShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors);
		mBackShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mBackShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
		mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowColors = new int[] { 0x80111111, 0x111111 };
		mFrontShadowDrawableVLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
		mFrontShadowDrawableVLR
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		mFrontShadowDrawableVRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors);
		mFrontShadowDrawableVRL
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowDrawableHTB = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors);
		mFrontShadowDrawableHTB
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowDrawableHBT = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors);
		mFrontShadowDrawableHBT
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);
	}

	/**
	 * Author : hmg25 Version: 1.0 Description : ���Ʒ���ҳ����Ӱ
	 */
	public void drawCurrentPageShadow(Canvas canvas) {
		double degree;
		if (mIsRTandLB) {
			degree = Math.PI
					/ 4
					- Math.atan2(mBezierControl1.y - mTouch.y, mTouch.x
							- mBezierControl1.x);
		} else {
			degree = Math.PI
					/ 4
					- Math.atan2(mTouch.y - mBezierControl1.y, mTouch.x
							- mBezierControl1.x);
		}
		// ����ҳ��Ӱ������touch��ľ���
		double d1 = (float) 25 * 1.414 * Math.cos(degree);
		double d2 = (float) 25 * 1.414 * Math.sin(degree);
		float x = (float) (mTouch.x + d1);
		float y;
		if (mIsRTandLB) {
			y = (float) (mTouch.y + d2);
		} else {
			y = (float) (mTouch.y - d2);
		}
		mPath1.reset();
		mPath1.moveTo(x, y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierControl1.x, mBezierControl1.y);
		mPath1.lineTo(mBezierStart1.x, mBezierStart1.y);
		mPath1.close();
		
		
		
		float rotateDegrees;
		canvas.save();

		canvas.clipPath(mPath0, Region.Op.XOR);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		int leftx;
		int rightx;
		GradientDrawable mCurrentPageShadow;
		if (mIsRTandLB) {
			leftx = (int) (mBezierControl1.x);
			rightx = (int) mBezierControl1.x + 10;
			mCurrentPageShadow = mFrontShadowDrawableVLR;
		} else {
			leftx = (int) (mBezierControl1.x - 10);
			rightx = (int) mBezierControl1.x + 1;
			mCurrentPageShadow = mFrontShadowDrawableVRL;
		}

		rotateDegrees = (float) Math.toDegrees(Math.atan2(mTouch.x
				- mBezierControl1.x, mBezierControl1.y - mTouch.y));
		canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y);
		mCurrentPageShadow.setBounds(leftx,
				(int) (mBezierControl1.y - mMaxLength), rightx,
				(int) (mBezierControl1.y));
		mCurrentPageShadow.draw(canvas);
		canvas.restore();

		mPath1.reset();
		mPath1.moveTo(x, y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierControl2.x, mBezierControl2.y);
		mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
		mPath1.close();
		canvas.save();
		canvas.clipPath(mPath0, Region.Op.XOR);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		if (mIsRTandLB) {
			leftx = (int) (mBezierControl2.y);
			rightx = (int) (mBezierControl2.y + 10);
			mCurrentPageShadow = mFrontShadowDrawableHTB;
		} else {
			leftx = (int) (mBezierControl2.y - 10);
			rightx = (int) (mBezierControl2.y + 1);
			mCurrentPageShadow = mFrontShadowDrawableHBT;
		}
		rotateDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl2.y
				- mTouch.y, mBezierControl2.x - mTouch.x));
		canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y);
		float temp;
		if (mBezierControl2.y < 0)
			temp = mBezierControl2.y - mHeight;
		else
			temp = mBezierControl2.y;

		int hmg = (int) Math.hypot(mBezierControl2.x, temp);
		if (hmg > mMaxLength)
			mCurrentPageShadow
					.setBounds((int) (mBezierControl2.x - 25) - hmg, leftx,
							(int) (mBezierControl2.x + mMaxLength) - hmg,
							rightx);
		else
			mCurrentPageShadow.setBounds(
					(int) (mBezierControl2.x - mMaxLength), leftx,
					(int) (mBezierControl2.x), rightx);

		// Log.i("hmg", "mBezierControl2.x   " + mBezierControl2.x
		// + "  mBezierControl2.y  " + mBezierControl2.y);
		mCurrentPageShadow.draw(canvas);
		canvas.restore();
	}

	/**
	 * Author : hmg25 Version: 1.0 Description : ���Ʒ���ҳ����
	 */
	private void drawCurrentBackArea(Canvas canvas, Bitmap bitmap) {
		int i = (int) (mBezierStart1.x + mBezierControl1.x) / 2;
		float f1 = Math.abs(i - mBezierControl1.x);
		int i1 = (int) (mBezierStart2.y + mBezierControl2.y) / 2;
		float f2 = Math.abs(i1 - mBezierControl2.y);
		float f3 = Math.min(f1, f2);
		mPath1.reset();
		mPath1.moveTo(mBeziervertex2.x, mBeziervertex2.y);
		mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
		mPath1.lineTo(mBezierEnd1.x, mBezierEnd1.y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierEnd2.x, mBezierEnd2.y);
		mPath1.close();
		GradientDrawable mFolderShadowDrawable;
		int left;
		int right;
		if (mIsRTandLB) {
			left = (int) (mBezierStart1.x - 1);
			right = (int) (mBezierStart1.x + f3 + 1);
			mFolderShadowDrawable = mFolderShadowDrawableLR;
		} else {
			left = (int) (mBezierStart1.x - f3 - 1);
			right = (int) (mBezierStart1.x + 1);
			mFolderShadowDrawable = mFolderShadowDrawableRL;
		}
		canvas.save();
		canvas.clipPath(mPath0);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);

		mPaint.setColorFilter(mColorMatrixFilter);

		float dis = (float) Math.hypot(mCornerX - mBezierControl1.x,
				mBezierControl2.y - mCornerY);
		float f8 = (mCornerX - mBezierControl1.x) / dis;
		float f9 = (mBezierControl2.y - mCornerY) / dis;
		mMatrixArray[0] = 1 - 2 * f9 * f9;
		mMatrixArray[1] = 2 * f8 * f9;
		mMatrixArray[3] = mMatrixArray[1];
		mMatrixArray[4] = 1 - 2 * f8 * f8;
		mMatrix.reset();
		mMatrix.setValues(mMatrixArray);
		mMatrix.preTranslate(-mBezierControl1.x, -mBezierControl1.y);
		mMatrix.postTranslate(mBezierControl1.x, mBezierControl1.y);
		canvas.drawBitmap(bitmap, mMatrix, mPaint);
		// canvas.drawBitmap(bitmap, mMatrix, null);
		mPaint.setColorFilter(null);
		canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
		mFolderShadowDrawable.setBounds(left, (int) mBezierStart1.y, right,
				(int) (mBezierStart1.y + mMaxLength));
		mFolderShadowDrawable.draw(canvas);
		canvas.restore();
	}

	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			float x = mScroller.getCurrX();
			float y = mScroller.getCurrY();
			mTouch.x = x;
			mTouch.y = y;
			postInvalidate();
		}
	}

	private void startAnimation(int delayMillis) {
		int dx, dy;
		// dx ˮƽ���򻬶��ľ��룬��ֵ��ʹ�����������
		// dy ��ֱ���򻬶��ľ��룬��ֵ��ʹ�������Ϲ���
		if (mCornerX > 0) {
			dx = -(int) (mWidth + mTouch.x);
		} else {
			dx = (int) (mWidth - mTouch.x + mWidth);
		}
		if (mCornerY > 0) {
			dy = (int) (mHeight - mTouch.y);
		} else {
			dy = (int) (1 - mTouch.y); // ��ֹmTouch.y���ձ�Ϊ0
		}
		mScroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, dy,
				delayMillis);
	}

	public void abortAnimation() {
		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
		}
	}

	public boolean canDragOver() {
		if (mTouchToCornerDis > mWidth / 10)
			return true;
		return false;
	}

	/**
	 * Author : hmg25 Version: 1.0 Description : �Ƿ����߷����ұ�
	 */
	public boolean DragToRight() {
		if (mCornerX > 0)
			return false;
		return true;
	}
	
	
	public interface callback{
		public void search();
	}

	public String getCurrentTime(int backtime){
		
		Calendar can = Calendar.getInstance();
		can.add(Calendar.MINUTE, backtime);
		System.out.println(can.get(Calendar.HOUR_OF_DAY));
		System.out.println(can.get(Calendar.MINUTE));
		
//		String date=new java.text.SimpleDateFormat("HH时mm分").format(new java.util.Date());
		return can.get(Calendar.HOUR_OF_DAY)+":"+can.get(Calendar.MINUTE);
	}
	
	public String getSize(int which){
		String sizeUrl = "http://61.181.14.184:8084/NewReadman/getWeiboByTime.do?token="+User.getToken()+"&tokenpwd="+User.getTokenPWD()+"&time="+which;
		String size = "";
		try {
			InputStream in = new URL(sizeUrl).openStream();
			size = convertStreamToString(in); 
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return size;
	}
	
	public String convertStreamToString(InputStream is) {   
        /*  
         * To convert the InputStream to String we use the BufferedReader.readLine()  
         * method. We iterate until the BufferedReader return null which means  
         * there's no more data to read. Each line will appended to a StringBuilder  
         * and returned as String.  
         */  
		
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));   
        StringBuilder sb = new StringBuilder();   
    
        String line = null;   
        try {   
            while ((line = reader.readLine()) != null) {   
                sb.append(line + "\n");   
            }   
        } catch (IOException e) {   
            e.printStackTrace();   
        } finally {   
            try {   
                is.close();   
            } catch (IOException e) {   
                e.printStackTrace();   
            }   
        }   
    
        return sb.toString();   
    }    
}
