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

public class HorizontalM extends HorizontalScrollView {


	// 控制上边条与展示文框联动
	private View mView;

	public HorizontalM(Context context) {

		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public HorizontalM(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.setSmoothScrollingEnabled(false);
		initScroller();
	}

	public HorizontalM(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setSmoothScrollingEnabled(false);
		initScroller();

		// TODO Auto-generated constructor stub
	}

	protected void onScrollChanged(int l, int t, int oldl, int oldt,int duration) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mView != null) {
			((HorizontalScrollView) mView).smoothScrollTo(l, t);
		}
	}
	

	public void setScrollView(View view) {
		mView = view;
	}

	static final int ANIMATED_SCROLL_GAP = 600;
	private long mLastScroll;

	private Field mScrollerField;
	ScrollerEx scrollerEx = null;

	private interface ScrollerEx {

		void create(Context context, Interpolator interpolator);

		Object getScroller();

		void abortAnimation();

		void startScroll(int startX, int startY, int dx, int dy, int duration);

		boolean isFinished();

	}
	
	private void initScroller() {
		try {
			mScrollerField = HorizontalScrollView.class
					.getDeclaredField("mScroller");
			mScrollerField.setAccessible(true);
			String type = mScrollerField.getType().getSimpleName();

			if ("OverScroller".equals(type)) {
				scrollerEx = new ScrollerEx() {
					private OverScroller mScroller = null;

					public void startScroll(int startX, int startY, int dx,
							int dy, int duration) {
						mScroller.startScroll(startX, startY, dx, dy, duration);
					}

					public boolean isFinished() {
						return mScroller.isFinished();
					}

					public Object getScroller() {
						return mScroller;
					}

					public void create(Context context,
							Interpolator interpolator) {
						mScroller = new OverScroller(context, interpolator);
					}

					public void abortAnimation() {
						if (mScroller != null) {
							mScroller.abortAnimation();
						}
					}
				};
			} else {
				scrollerEx = new ScrollerEx() {
					private Scroller mScroller = null;

					public void startScroll(int startX, int startY, int dx,
							int dy, int duration) {
						mScroller.startScroll(startX, startY, dx, dy, duration);
					}

					public boolean isFinished() {
						return mScroller.isFinished();
					}

					public Object getScroller() {
						return mScroller;
					}

					public void create(Context context,
							Interpolator interpolator) {
						mScroller = new Scroller(context, interpolator);
					}

					public void abortAnimation() {
						if (mScroller != null) {
							mScroller.abortAnimation();
						}
					}
				};
			}

		} catch (Exception ex) {

		}
	}

	public final void smoothScrollBy(int dx, int dy, int addDuration) {

		float tension = 0f;

		scrollerEx.abortAnimation();

		Interpolator ip = new OvershootInterpolator(tension);
		scrollerEx.create(getContext(), ip);

		try {
			mScrollerField.set(this, scrollerEx.getScroller());
		} catch (Exception e) {
		}

		long duration = AnimationUtils.currentAnimationTimeMillis()
				- mLastScroll;
		if (duration > ANIMATED_SCROLL_GAP) {
			scrollerEx.startScroll(getScrollX(), getScrollY(), dx, dy,
					addDuration);

			awakenScrollBars();

			invalidate();
		} else {
			if (!scrollerEx.isFinished()) {
				scrollerEx.abortAnimation();
			}
			scrollBy(dx, dy);
		}
		mLastScroll = AnimationUtils.currentAnimationTimeMillis();

	}

	public final void smoothScrollTo(int x, int y, int duration) {
		smoothScrollBy(x - getScrollX(), y - getScrollY(), duration);
	}
	

	final byte DIR_RIGHT = 1;// 屏幕向右滑动
	final byte DIR_LEFT = 2; // 屏幕向左滑动
	byte mDir = DIR_LEFT; // 滑动方向 默认在左边
	float x = 0; // 横向屏幕滑动的down的x坐标
	float y = 0;
    static final int DIS = 600;
    HorizontalM mScreenHS = null;
	
	// 屏幕的滑动效果:滑到小于半屏幕回到原点，大于半屏幕切换到下一个屏幕
	
	 
	void screenScrollonTouch(MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			x = e.getX();
			y = e.getY();
		}
		if (e.getAction() == MotionEvent.ACTION_UP) {
			if (mDir == DIR_LEFT) { // 第一屏幕
				if (e.getX() - x < -DIS / 2) { // 往左滑动大于屏幕的一半
					mScreenHS.smoothScrollTo(DIS, 0);
					mDir = DIR_RIGHT;
				} else if (e.getX() - x > -DIS / 2 && e.getX() - x < 0) { // 往左滑动小于屏幕的一半
					mScreenHS.smoothScrollTo((int) -(x - e.getX()), 0);
				}
			} else if (mDir == DIR_RIGHT) { // 第二屏幕
				if (e.getX() - x > DIS / 2) {
					mScreenHS.smoothScrollTo(-DIS, 0);
					mDir = DIR_LEFT;
				} else if (e.getX() - x < DIS / 2 && e.getX() - x > 0) {
					mScreenHS.smoothScrollTo((int) (e.getX() - x) + DIS, 0);
				}

			}
		}

		// 添加屏幕移动
		if (e.getAction() == MotionEvent.ACTION_MOVE) {
			if (mDir == DIR_LEFT) {
				mScreenHS.smoothScrollTo((int) (x - e.getX()), 0);
				// Log.d("LEFT", "go to left");

			} else if (mDir == DIR_RIGHT) {
				mScreenHS.smoothScrollTo((int) (x - e.getX() + DIS), 0);
				// Log.d("RIGHT", "go to right");
			}
		}
	}

	
}
