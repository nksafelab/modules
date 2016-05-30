package com.jinke.flipboard.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

public class SearchEditText extends EditText{
  private Paint paint;
  public SearchEditText(Context context) {
    super(context);
    //定义画笔
    paint = getPaint();
    //定义笔画粗细样式
    paint.setStyle(Paint.Style.STROKE);
    //定义笔画颜色
    paint.setColor(Color.GRAY);
  }
  
  /**
 * @param context
 * @param attrs
 */
public SearchEditText(Context context, AttributeSet attrs) {
	super(context, attrs);
	 //定义画笔
    paint = getPaint();
    //定义笔画粗细样式
    paint.setStyle(Paint.Style.FILL);
    //定义笔画颜色
    paint.setColor(Color.RED);
	// TODO Auto-generated constructor stub
}

@Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int w = getWidth();
    int h = getHeight();
    //下边框
    canvas.drawLine(0, h, w, h, paint);
//    //右边框
//    canvas.drawLine(w, 0, w, h, paint);
//    //左边框
//    canvas.drawLine(0, 0, 0, h, paint);
//    //上边框
//    canvas.drawLine(0, 0, w, 0, paint);
  }
}