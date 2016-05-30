package jinke.readings.wedget;

import java.lang.reflect.Field;

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;
import android.widget.Scroller;

public class Horizontal extends HorizontalScrollView {
	// 控制上边条与展示文框联动
	private View mView;

	public Horizontal(Context context) {

		super(context);
		// TODO Auto-generated constructor stub
	}

	public Horizontal(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

	}

	public Horizontal(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// TODO Auto-generated constructor stub
	}

	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mView != null) {
			mView.scrollTo(l, t);
		}
	}

	public void setScrollView(View view) {
		mView = view;
	}

	@Override
	public void setSmoothScrollingEnabled(boolean smoothScrollingEnabled) {
		// TODO Auto-generated method stub
		super.setSmoothScrollingEnabled(false);
	}

	@Override
	public void fling(int velocityX) {
		// TODO Auto-generated method stub
		super.fling(velocityX / 6);
	} 
	

}
