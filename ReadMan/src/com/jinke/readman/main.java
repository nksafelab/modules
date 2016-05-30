package com.jinke.readman;

import java.util.Random;

import com.jinke.readman.bean.User;
import com.jinke.rloginservice.IReadingsLoginService;
import com.jinke.rloginservice.UserInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class main extends Activity implements OnTouchListener,OnGestureListener{
	
	private static final String TAG = "main";
	
	private static final int FLING_MIN_DISTANCE = 100;	// X轴的坐标位移大于FLING_MIN_DISTANCE
	private static final int FLING_MIN_VELOCITY = 200;	// 移动速度大于FLING_MIN_VELOCITY个像素/秒
	
	private GestureDetector mGestureDetector;
	private ViewFlipper mFlipper;
	private View welView;
	private ImageView welcomeImg;
	private Random random;
	private boolean offline = false;
	private Bundle savedInstanceState;
	private AlertDialog.Builder builder;
	private void dialog() {
		
		builder = new Builder(main.this);
		builder.setMessage("Readings当前尚未登录，是否跳转到登录界面");
//		builder.setMessage(getString(R.string.Dialog_Login_Msg).toString());
//		builder.setTitle(getString(R.string.Dialog_Login_Title).toString());
		builder.setTitle("尚未登录");
//		builder.setNegativeButton(getString(R.string.Button_exit).toString(), new DialogInterface.OnClickListener() {
		builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				unbindService(isLoginConn);
				main.this.finish();
			}
		});

//		builder.setPositiveButton(getString(R.string.Button_ok).toString(), new DialogInterface.OnClickListener() {
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					loginService.loginActivity();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				unbindService(isLoginConn);
				dialog.dismiss();
			}
		});

		builder.create().show();
	}
	
private void dialogNet() {
		
		builder = new Builder(main.this);
		builder.setMessage("当前wifi尚未打开，请点击 \"设置\" 选择网络。");
//		builder.setMessage(getString(R.string.Dialog_Login_Msg).toString());
//		builder.setTitle(getString(R.string.Dialog_Login_Title).toString());
		builder.setTitle("网络链接");
//		builder.setNegativeButton(getString(R.string.Button_exit).toString(), new DialogInterface.OnClickListener() {
		builder.setNegativeButton("离线阅读", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				offline = true;
			}
		});

//		builder.setPositiveButton(getString(R.string.Button_ok).toString(), new DialogInterface.OnClickListener() {
		builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));//进入无线网络配置界面
				dialog.dismiss();
			}
		});

		builder.create().show();
	}
	private IReadingsLoginService loginService;
	private ServiceConnection isLoginConn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.v(TAG, "onServiceDisconnected() called");
			loginService = null;
		}
 		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.v(TAG, "onServiceConnected() called");
			loginService = IReadingsLoginService.Stub.asInterface(service);
			try {
				if(loginService.isLogin()){
					UserInfo userInfo = loginService.getUserInfo();
					User.setUserName(userInfo.getUsername());
					Log.v(TAG, "login!!!!!!!!!username:"+userInfo.getUsername());
//					User.setUserName("marklee");
					//login
					unbindService(isLoginConn);
					return;
				}else{
					//error
					dialog();
					
					//CMain.this.finish(); 
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**全屏设置，隐藏窗口所有装饰*/  
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		/**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效*/  
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.main);
		
		//android.permission.ACCESS_NETWORK_STATE.

		Intent intent = new Intent("com.jinke.rloginservice.IReadingsLoginService");
		if(bindService(intent, isLoginConn, BIND_AUTO_CREATE)){
			
		}else{
			
		}
		
		//注册一个用于手势识别的类
		mGestureDetector = new GestureDetector(main.this);
		
		//给mFlipper设置一个listener
		mFlipper = (ViewFlipper)findViewById(R.id.mflipper);
		mFlipper.setOnTouchListener(this);
		
		//允许长按住ViewFlipper,这样才能识别拖动等手势
		mFlipper.setLongClickable(true);
		
		/*向mflipper中添加view*/
		LayoutInflater inflater = getLayoutInflater();
		
		welView = inflater.inflate(R.layout.welcome,null);
		
		welcomeImg = (ImageView)welView.findViewById(R.id.welcomeImg);
		
		TransitionDrawable mDrawable;// = (TransitionDrawable) getResources().getDrawable(R.drawable.transition);
		
		random = new Random();
		if(random.nextInt(2) == 1){
			mDrawable = (TransitionDrawable) getResources().getDrawable(R.drawable.transition_random1);
		}else{
			mDrawable = (TransitionDrawable) getResources().getDrawable(R.drawable.transition);
		}
		
		welcomeImg.setImageDrawable(mDrawable);
		mDrawable.startTransition(1000);
		mFlipper.addView(welView);
		
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				checkNetworkInfo();
			}
		};
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					sleep(1000);
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		
		
		
	}
	
	private void gotoIndex(){
		Intent mIntent = new Intent();
		mIntent.setClass(main.this, index_fenye.class);
		mIntent.putExtra("offline", offline);
		startActivity(mIntent);
		main.this.finish();
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	/*
	 * 用户按下触摸屏、快速移动后松开即触发这个事件
	 * e1：第1个ACTION_DOWN MotionEvent
	 * e2：最后一个ACTION_MOVE MotionEvent
	 * velocityX：X轴上的移动速度，像素/秒
	 * velocityY：Y轴上的移动速度，像素/秒
	 * 触发条件 ：
	 * X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		
		Log.v(TAG, "onFling() called");
		
		Log.v(TAG, "from:"+e1.getX()+" to:"+e2.getX() +"Length:"+(e1.getX()-e2.getX())+" speed:"+Math.abs(velocityX));
		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			// 当像左侧滑动的时候
			gotoIndex();
			//Toast.makeText(main.this,"Jump to Index Activity", Toast.LENGTH_LONG).show();
			
			
		} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			//Toast.makeText(main.this,"Flip right ,do nothing", Toast.LENGTH_SHORT).show();
		}
		
		return false;
	}
	
	
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		// 一定要将触屏事件交给手势识别类去处理（自己处理会很麻烦的）
		return mGestureDetector.onTouchEvent(event);
	}
	
	 private void checkNetworkInfo()
	    {
	        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

	        //mobile 3G Data Network
	        State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
	        
	        //wifi
	        State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//	        Toast.makeText(main.this, "3G:"+mobile.toString()+"\nwifi:"+wifi.toString(), Toast.LENGTH_SHORT).show(); //显示3G网络连接状态
	        if(wifi != State.CONNECTED){
	        	dialogNet();
	        	
	        }else{
	        	Toast.makeText(main.this, "网络连接成功", Toast.LENGTH_SHORT).show();
	        }
	    }
	 
	 @Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
//		Toast.makeText(main.this, "Restart", Toast.LENGTH_SHORT).show(); 
		checkNetworkInfo();
	}
}