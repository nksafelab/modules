//package jinke.readings;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.Display;
//import android.view.GestureDetector;
//import android.view.GestureDetector.OnGestureListener;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.AbsoluteLayout;
//import android.widget.HorizontalScrollView;
//import android.widget.TableLayout;
//import android.widget.Toast;
//
//@SuppressWarnings("deprecation")
//public class Horizontal extends Activity implements OnGestureListener {
//	/** Called when the activity is first created. */
//	private HorizontalScrollView hs = null;
//	private View[] view = null;
//	private TableLayout tl = null;
//	private GestureDetector ges = null;
//	private int width;
//	private int height;
//	int x1, x2;
//	int index;
//	int widthIndex = 0;
//	int heightIndex = 0;
//	int page = 0;
//	boolean flag = false;
//	int j = 0;
//	int curWidth;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main);
//		Display dis = getWindowManager().getDefaultDisplay();
//		width = dis.getWidth();
//		height = dis.getHeight();
//		System.out.println("width " + width + "   height " + height);
//		tl = (TableLayout) findViewById(R.id.al);
//		hs = (HorizontalScrollView) findViewById(R.id.hs);
//		ges = new GestureDetector(this);
//
//		hs.setLongClickable(true);
//		view = new View[18];
//		for (int i = 0; i < view.length; i++) {
//			if (i != 0 && i % 3 == 0) {
//				j++;
//				if (j != 3) {
//					heightIndex++;
//				}
//
//				widthIndex = 0;
//			}
//			int pageWidth = page * width;
//			int baseWidth = widthIndex * 170;
//			curWidth = pageWidth + baseWidth;
//			view[i] = LayoutInflater.from(this).inflate(R.layout.base, null);
//			System.out.println("height index is " + heightIndex);
//			view[i].setLayoutParams(new AbsoluteLayout.LayoutParams(170, 300,
//					curWidth, heightIndex * 320));
//			System.out.println("x is " + curWidth);
//			System.out.println("y is " + heightIndex * 170);
//			widthIndex++;
//			al.addView(view[i]);
//			if (i == 8) {
//				page++;
//				heightIndex = 0;
//				flag = true;
//			}
//		}
//
//		for (int i = 0; i < view.length; i++) {
//			view[i].setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					System.out.println("click");
//					Toast.makeText(MainActivity.this, "click",
//							Toast.LENGTH_SHORT).show();
//				}
//			});
//		}
//	}
//
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		// TODO Auto-generated method stub
//		// if(ev.getAction() != MotionEvent.ACTION_MOVE&&ev.getAction() ==
//		// MotionEvent.ACTION_UP){
//		// System.out.println("key action is up");
//		// return true;
//		// }
//		return ges.onTouchEvent(ev);
//	}
//
//	@Override
//	public boolean onDown(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//			float velocityY) {
//		if (e2.getX() - e1.getX() > 50) {
//			// fling right
//			hs.smoothScrollTo(0, 0);
//		} else if (e1.getX() - e2.getX() > 50) {
//			// fling left
//			hs.smoothScrollTo(width, 0);
//		}
//		return false;
//	}
//
//	@Override
//	public void onLongPress(MotionEvent e) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
//			float distanceY) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void onShowPress(MotionEvent e) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public boolean onSingleTapUp(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//}