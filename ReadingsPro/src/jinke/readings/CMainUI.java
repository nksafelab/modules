package jinke.readings;

import java.io.InputStream;

import com.jinke.rloginservice.IReadingsLoginService;
import com.jinke.rloginservice.UserInfo;

import jinke.readings.download.CNetTransfer;
import jinke.readings.util.IdentifyUtil;
import jinke.readings.util.PushReciever;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CMainUI extends Activity implements OnClickListener {

	private Button btnChannels, btnMyRadio, btnSearchButton = null;
	public static Button btnPeople;
	private Button buttom_button_back;
	private Button buttom_button_functionmenu;
	
	public static EditText edit;
	
	private FrameLayout frame = null;

	protected LocalActivityManager mLocalActivityManager;
	private View mLaunchedView;
	private Bundle bunlde;
	public static final int FOCUS_AFTER_DESCENDANTS = 0x40000;

	private final int RECENT = 0;
	private final int PUSH = 1;
	private final int REQUEST = 2;
	private final int SEARCH = 3;
	private Intent intent = null;
	private String search_context = null;
	
	public static final int WENKU = 1;
	public static final int IASK = 2;
	public static final int MK = 3;
	public static final int WENKU3G = 4;
	public static final int YOUKU = 5;
	public static final int XIAOSHUO = 6;
	public static final int GOOGLEBOOK = 7;
	public static final int TXTR = 8;
	public static final int CARTOON = 9;
	
	private String B_WENKU;
	private String X_IASK;
	private String Q_QUNLUO;
	private String B_3G;
	private String Y_YOUKU;
	private String B_XIAOSHUO;
	private String G_GOOGLE; 
	private String T_TXTR; 
	private String D_CARTOON;
	
	private ProgressBar bindBar;
	private TextView bindBarInfo;
	AlertDialog.Builder builder;
	
	private String devID;
	private String username;
	private String bindResult;
	
	private TextView usernameTextView;
	//private String sourcePath = "/sdcard/readings/searchSource"; rm by yjy
	private String sourcePath = Environment.getDataDirectory()+"/data/jinke.readings/searchSource";
	
	private String dialogTitle = "";
	private String bindStr = "";
	private boolean isBind;
	
	private int[] select= { WENKU, IASK,MK ,WENKU3G,YOUKU,XIAOSHUO,GOOGLEBOOK,TXTR,CARTOON};
	private boolean[] selected = {false,false,false,false,false,false,false,false,false};
	
	private Intent intent1;
	private IReadingsLoginService loginService;
	private UserInfo userInfo;
	
	private ServiceConnection serConn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.v("CHistoryActivity", "onServiceDisconnected() called");
			loginService = null;
		}
 		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.v("CHistoryActivity", "onServiceConnected() called");
			loginService = IReadingsLoginService.Stub.asInterface(service);
			try {
				userInfo = loginService.getUserInfo();
				String username = userInfo.getUsername();
				if(username.length() > 13){
					username = username.substring(0, 13)+"...";
				}
				usernameTextView.setText(username);
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
		bunlde = savedInstanceState;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cmainui);
		
		usernameTextView = (TextView)findViewById(R.id.usernameTextView);
		
		Intent intent = new Intent("com.jinke.rloginservice.IReadingsLoginService");
		if(bindService(intent, serConn, BIND_AUTO_CREATE)){
			//Toast.makeText(CHistoryActivity.this, "bindService() Success",Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(CMainUI.this, "bindService() ERROR",Toast.LENGTH_LONG).show();
		}
		
		B_WENKU = CMainUI.this.getString(R.string.SearchSource_baiduwenku).toString();
		X_IASK = CMainUI.this.getString(R.string.SearchSource_iask).toString();
		Q_QUNLUO = CMainUI.this.getString(R.string.SearchSource_mk).toString();
		B_3G = CMainUI.this.getString(R.string.SearchSource_baiduwenku3g).toString();
		Y_YOUKU = CMainUI.this.getString(R.string.SearchSource_youku).toString();
		B_XIAOSHUO = CMainUI.this.getString(R.string.SearchSource_baiduxs).toString();
		G_GOOGLE = CMainUI.this.getString(R.string.SearchSource_googlebook).toString();
		T_TXTR = CMainUI.this.getString(R.string.SearchSource_txtr).toString();
		D_CARTOON = CMainUI.this.getString(R.string.SearchSource_cartoon).toString();
		
		intent1 = getIntent();
		Bundle b = intent1.getExtras();

		
		setupView();
		
		buttom_button_back = (Button)findViewById(R.id.buttom_button_back);
		buttom_button_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		buttom_button_functionmenu = (Button)findViewById(R.id.buttom_button_functionmenu);
		buttom_button_functionmenu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CMainUI.this.openOptionsMenu();
			}
		});
		
		
//		String num = "";
//		num = PushReciever.read("/sdcard/readings/init");
//		//System.out.println("read(/sdcard/readings/init)@CMainUI-----------从init中获得的之前readings中文件个数：------"+num);
//		int numm = PushReciever.scanFileNum("/sdcard/readings/")-1;
//		String nummm = ""+numm;
//		//System.out.println("scanFileNum(/sdcard/readings/)@CMainUI-----------扫描readings文件夹中中文件个数：------"+numm);
//		//int tem = Integer.getInteger(num);
//		//System.out.println("num:"+num+" numm:"+numm);
//		if(!num.equals(nummm)){
//			btnPeople.setBackgroundResource(R.drawable.push_new_up);
//			//System.out.println("setBackgroundResource@CMainUI-----------有新的推送文件，更改图标------");
//		}
		
		
		
		
		
//		PushReciever pr = new PushReciever();
//		IntentFilter filter = new IntentFilter();
//		filter.addAction("jinke.readings.pushreceiver");
//		registerReceiver(pr, filter);
		
		
				search_context = b.getString("search_context");
				b.putString("username", username);
				intent1.putExtras(b);
				intent1.setClass(CMainUI.this, SearchResultActivity.class);
				ChangeActivity("hot", intent1);
		edit = (EditText)findViewById(R.id.search_Edit);
		
		if(b != null){
			String context = (String)b.get("search_context");
			edit.setText(context);
		}
		
		edit.setFocusable(true);
		edit.setFocusableInTouchMode(true);
		edit.clearFocus();
		
	}


	void setupView() {
		btnChannels = (Button) findViewById(R.id.recent);
		btnChannels.setOnClickListener(this);

		btnPeople = (Button) findViewById(R.id.push);
		btnPeople.setOnClickListener(this);

		btnMyRadio = (Button) findViewById(R.id.request);
		btnMyRadio.setOnClickListener(this);

		btnSearchButton = (Button)findViewById(R.id.searchButton);
		btnSearchButton.setOnClickListener(this);
		
		btnSearchButton.requestFocus();
		
		
		frame = (FrameLayout) findViewById(R.id.portrait_containerBody);
	}

	private Handler myChangeActivity = new Handler() {
		

		@Override
		public void handleMessage(Message msg) {
			Bundle b;
			super.handleMessage(msg);
			switch (msg.what) {
			case RECENT:
				intent = new Intent(CMainUI.this, SearchResultList.class);
				b = new Bundle();
				b.putString("type", "recent_reading");
				intent.putExtras(b);
				ButtonGroup(RECENT);
				ChangeActivity("recent", intent);
				break;

			case PUSH:
				intent = new Intent(CMainUI.this, CTest2.class);
				ButtonGroup(PUSH);
				ChangeActivity("push", intent);
				break;

			case REQUEST:
				intent = new Intent(CMainUI.this, CTest2.class);
				ButtonGroup(REQUEST);
				ChangeActivity("request", intent);
				break;
			case SEARCH:
				intent = new Intent(CMainUI.this, SearchResultActivity.class);
				b = new Bundle();
				b.putString("search_context", search_context);
				b.putString("username", username);
				intent.putExtras(b);
				ChangeActivity("hot", intent);
				
				
//				intent1.setClass(CMainUI.this, SearchResultActivity.class);
//				ChangeActivity("hot", intent1);
				break;


			default:
				break;
			}
		}
	};

	public LocalActivityManager getLocalActivityManager() {
		return new LocalActivityManager(this, true);
	}

	// װ�����ӵ���ͼ��frma����ʾ
	public void ChangeActivity(String Id, Intent intent) {
		//frame = (FrameLayout) findViewById(R.id.portrait_containerBody);
		if (null == mLocalActivityManager) {
			mLocalActivityManager = getLocalActivityManager();
			mLocalActivityManager.dispatchCreate(bunlde);
			//System.out.println("null == mLocalActivityManager");
		}
		//System.out.println("mLocalActivityManager not null ,startActivity");
		
		mLocalActivityManager.removeAllActivities();
		
		final Window w = mLocalActivityManager.startActivity(Id, intent);
		
		
		
		//System.out.println("startActivity@ChangeActivity");
		
		final View wd = w != null ? w.getDecorView() : null;
		
		//w not null, activityManger startActivity success, wd = w.getDecorView()
		//w null,     activityManger startActivity failed, wd = null

		if (mLaunchedView != wd && mLaunchedView != null) {
			System.out.println("mLaunchedView !=null & !=wd,");
			if (mLaunchedView.getParent() != null) {
				frame.removeView(mLaunchedView);
				//System.out.println("remove mLaunchedView");
			}
		}

		if (null != mLaunchedView && mLaunchedView == wd) {
			System.out.println("mLaunchedView !=null & ==wd,return do nothing!");
			return;
		}
		
		// mLaunchedView == null || mLaunchedView !=null, !=wd 
		
		mLaunchedView = wd;
		if (mLaunchedView != null) {
			// mLaunchedView.setVisibility(View.VISIBLE);
			mLaunchedView.setFocusableInTouchMode(true);
			((ViewGroup) mLaunchedView)
					.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
		}
		frame.addView(mLaunchedView, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		// containerBody.removeAllViews();
		// containerBody.addView(getLocalActivityManager().startActivity(Id,
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView());
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.recent:
			myChangeActivity.sendEmptyMessage(RECENT);
			break;

		case R.id.push:
			myChangeActivity.sendEmptyMessage(PUSH);
			break;

		case R.id.request:
			myChangeActivity.sendEmptyMessage(REQUEST);
			break;
		case R.id.searchButton:
			EditText edit = (EditText)findViewById(R.id.search_Edit);
			
		
			
			search_context = edit.getText().toString();
			myChangeActivity.sendEmptyMessage(SEARCH);
			break;

		default:
			break;
		}
	}
	
	public void ButtonGroup(int msg){
		switch (msg) {
		case RECENT:
			((Button)findViewById(R.id.recent)).setBackgroundResource(R.drawable.recentreading_down);
			((Button)findViewById(R.id.push)).setBackgroundResource(R.drawable.push_up);
			((Button)findViewById(R.id.request)).setBackgroundResource(R.drawable.requestbook_up);
			break;

		case PUSH:
			((Button)findViewById(R.id.push)).setBackgroundResource(R.drawable.push_down);
			((Button)findViewById(R.id.recent)).setBackgroundResource(R.drawable.recentreading_up);
			((Button)findViewById(R.id.request)).setBackgroundResource(R.drawable.requestbook_up);
			break;

		case REQUEST:
			((Button)findViewById(R.id.request)).setBackgroundResource(R.drawable.requestbook_down);
			((Button)findViewById(R.id.recent)).setBackgroundResource(R.drawable.recentreading_up);
			((Button)findViewById(R.id.push)).setBackgroundResource(R.drawable.push_up);
			break;
		default:
			break;
		}
		
	}
	
	
	
	// 点击Menu时，系统调用当前Activity的onCreateOptionsMenu方法，并传一个实现了一个Menu接口的menu对象供你使用
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * add()方法的四个参数，依次是： 1、组别，如果不分组的话就写Menu.NONE,
		 * 2、Id，这个很重要，Android根据这个Id来确定不同的菜单 3、顺序，那个菜单现在在前面由这个参数的大小决定
		 * 4、文本，菜单的显示文本
		 */
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, getString(R.string.Menu_searchSourceSetting).toString()).setIcon(
				android.R.drawable.ic_menu_set_as);
		// setIcon()方法为菜单设置图标，这里使用的是系统自带的图标，同学们留意一下,以
		// android.R开头的资源是系统提供的，我们自己提供的资源是以R开头的
//		menu.add(Menu.NONE, Menu.FIRST + 2, 2, getString(R.string.Menu_userBinding).toString()).setIcon(
//				android.R.drawable.ic_menu_edit);
		menu.add(Menu.NONE, Menu.FIRST + 3, 3, getString(R.string.Menu_userUnBinding).toString()).setIcon(
				android.R.drawable.ic_menu_help);
		menu.add(Menu.NONE, Menu.FIRST + 4, 4, getString(R.string.Menu_reLogin).toString()).setIcon(
				android.R.drawable.ic_menu_add);
//		menu.add(Menu.NONE, Menu.FIRST + 5, 5, "详细").setIcon(
//				android.R.drawable.ic_menu_info_details);
//		menu.add(Menu.NONE, Menu.FIRST + 6, 6, "发送").setIcon(
//				android.R.drawable.ic_menu_send);
		// return true才会起作用
		return true;

	}

	// 菜单项被选择事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			// 设置搜索源
			// Toast.makeText(this, "删除菜单被点击了", Toast.LENGTH_LONG).show();

			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.Menu_searchSourceSetting).toString())
					.setMultiChoiceItems(
//							new String[] { "百度文库", "新浪iask", "群落阅读", "百度文库3G",
//									"优酷", "百度小说","谷歌图书(英文搜索源)","TXTR(英文搜索源)","动漫部落"}, selected,
							new String[] { B_WENKU, X_IASK, Q_QUNLUO, B_3G,
									Y_YOUKU, B_XIAOSHUO ,G_GOOGLE,T_TXTR,D_CARTOON}, selected,
							new DialogInterface.OnMultiChoiceClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which, boolean isChecked) {
									// TODO Auto-generated method stub
									selected[which] = isChecked;
								}
							})
					.setPositiveButton(getString(R.string.Button_ok).toString(),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									String selectedStr = "";
									for (int i = 0; i < selected.length; i++) {
										if (selected[i] == true) {
//											System.out
//													.println("selected source : "
//															+ select[i]);
											selected[i] = false;
											selectedStr += select[i];
										}
									}
//									System.out.println("selectedStr: "
//											+ selectedStr);

									PushReciever.write(sourcePath, selectedStr);

								}
							}).setNegativeButton(getString(R.string.Button_cancle).toString(), null).show();

			break;
		case Menu.FIRST + 2:
			// 用户绑定
			dialog(true);
			// Toast.makeText(this, "保存菜单被点击了", Toast.LENGTH_LONG).show();
			break;
		case Menu.FIRST + 3:
			// 用户解绑定
			try {
				loginService.loginActivity();
			} catch (RemoteException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Toast.makeText(this, "帮助菜单被点击了", Toast.LENGTH_LONG).show();
			break;
		case Menu.FIRST + 4:
			// 重新登录
			try {
				loginService.loginActivity();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Toast.makeText(this, "添加菜单被点击了", Toast.LENGTH_LONG).show();
			break;
		case Menu.FIRST + 5:
			Toast.makeText(this, "详细菜单被点击了", Toast.LENGTH_LONG).show();
			break;
		case Menu.FIRST + 6:
			Toast.makeText(this, "发送菜单被点击了", Toast.LENGTH_LONG).show();
			break;
		}

		return false;
	}

	// 选项菜单被关闭事件，菜单被关闭有三种情形，menu按钮被再次点击、back按钮被点击或者用户选择了某一个菜单项
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		// Toast.makeText(this, "选项菜单关闭了", Toast.LENGTH_LONG).show();
	}

	// 菜单被显示之前的事件
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// Toast.makeText(this,
		// "选项菜单显示之前onPrepareOptionsMenu方法会被调用，你可以用此方法来根据打当时的情况调整菜单",
		// Toast.LENGTH_LONG).show();
		// 如果返回false，此方法就把用户点击menu的动作给消费了，onCreateOptionsMenu方法将不会被调用
		String source = PushReciever.read(sourcePath);

		devID = IdentifyUtil.getIdentifyID();
		int s = 0;
		if (source != "") {
			s = Integer.parseInt(source);
		} else {
			PushReciever.write(sourcePath, "12");
		}
		//System.out.println("select source is :------------" + s);
		for (int i = 0; i < source.length(); i++) {
			switch (s % 10) {
			case 1:
				s = s / 10;
				selected[0] = true;
				break;
			case 2:
				s = s / 10;
				selected[1] = true;
				break;
			case 3:
				s = s / 10;
				selected[2] = true;
				break;
			case 4:
				s = s / 10;
				selected[3] = true;
				break;
			case 5:
				s = s / 10;
				selected[4] = true;
				break;
			case 6:

				s = s / 10;
				selected[5] = true;
				break;
			case 7:
				s = s / 10;
				selected[6] = true;
				break;
			case 8:
				s = s / 10;
				selected[7] = true;
				break;
			case 9:
				s = s / 10;
				selected[8] = true;
				break;
			default:
				selected[0] = true;
				selected[1] = true;

				break;
			}
		}
		return true;
	}
	
	private void dialog(boolean isBind) {
		String msg = "";
		this.isBind = isBind;
		if(isBind == true){
			msg = " 当前用户：" + username + "\n 当前设备ID：" + devID
			+ "\n 每个设备最多可绑定5个帐号，确定执行绑定？";
			dialogTitle = "用户绑定";
			bindStr = "http://61.181.14.184:8084/Readings/bangdingDevice.do?devid="
				+ devID + "&username=" + username;
		}else{
			msg = " 当前用户：" + username + "\n 当前设备ID：" + devID
			+ "\n 确定执行解绑？";
			dialogTitle = "用户解绑";
			bindStr = "http://61.181.14.184:8084/Readings/jiebangdingDevice.do?devid="
				+ devID + "&username=" + username;
			}
		
		builder = new Builder(CMainUI.this);
		builder.setMessage(msg);

		builder.setTitle(dialogTitle);

		builder.setNegativeButton(getString(R.string.Button_cancle).toString(), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.setPositiveButton(getString(R.string.Button_ok).toString(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				// System.out.println("------------bindResult-----------"+bindResult);

				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(R.layout.dialog,
						(ViewGroup) findViewById(R.id.dialog));

				bindBar = (ProgressBar) layout.findViewById(R.id.bindBar);
				bindBarInfo = (TextView) layout.findViewById(R.id.bindBarInfo);
				bindBar.setVisibility(ProgressBar.VISIBLE);
				bindBarInfo.setVisibility(TextView.VISIBLE);

				builder.setMessage("");

				builder.setTitle(dialogTitle).setView(layout)
						.setPositiveButton(getString(R.string.Button_ok).toString(), null)
						.setNegativeButton(getString(R.string.Button_cancle).toString(), null).show();

				bindThread bt = new bindThread(bindStr);
				bt.start();

				dialog.dismiss();
			}
		});

		builder.create().show();
	}


	
	class bindThread extends Thread {

		private String urlStr;
		
		public bindThread(String urlStr){
			this.urlStr = urlStr;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
//			String urlStr = "http://61.181.14.184:8084/Readings/bangdingDevice.do?devid="
//					+ devID + "&username=" + username;
			System.out.println("bindThread@CHistoryActivity-------------------bindThread url-----------"+urlStr);
			try {

				InputStream in = CNetTransfer.GetXmlInputStream(urlStr);
				byte[] arrData = new byte[1024];
				in.read(arrData);
				bindResult = new String(arrData);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message msg = new Message();
			
			System.out.println("bindThread@CHistoryActivity:---------------绑定结果-------------"+bindResult);
			if(bindResult != null){
				int start = bindResult.lastIndexOf("<");
				//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!start" + start);
				bindResult = bindResult.substring(start - 1, start);
				//System.out.println(bindResult);
	
				if (bindResult != "")
					msg.arg1 = Integer.parseInt(bindResult);
				bindHandler.sendMessage(msg);
			}else{
				msg.arg1 = 4;
				bindHandler.sendMessage(msg);
			}

//			Message msg = new Message();
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			msg.arg1 = 4;
//			bindHandler.sendMessage(msg);
			super.run();
		}

	}

	Handler bindHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			int result = msg.arg1;
			String re = "请检测网络，稍后再试";
			String r = "返回1成功，返回值为2表示重复插入，返回值0表示用户绑定设备大于5个";
			if(isBind){
				switch (result) {
				case 1:
					re = "绑定成功";
					// System.out.println(re = "绑定成功");
					break;
				case 2:
					re = "重复绑定";
					// System.out.println(re = "重复绑定");
					break;
				case 0:
					re = "设备绑定用户数大于5个";
					// System.out.println(re = "设备绑定用户数大于5个");
					break;
				default:
					re = "请检测网络，稍后再试";
					break;
				}
			}else{
				switch (result) {
				case 1:
					re = "接绑成功";
					// System.out.println(re = "绑定成功");
					break;
				case 2:
					re = "解绑失败";
					// System.out.println(re = "重复绑定");
					break;
				default:
					re = "请检测网络，稍后再试";
					break;
				}
			}
			bindBar.setVisibility(ProgressBar.GONE);
			//bindBarInfo.setVisibility(TextView.GONE);
			bindBarInfo.setText(re);

			builder.setMessage(re);

			super.handleMessage(msg);
		}

	};
	
	protected void onDestroy() {
		mLocalActivityManager.removeAllActivities();
		System.out.println("CMainUI############# destory remove");
		unbindService(serConn);
		frame.removeView(mLaunchedView);
		super.onDestroy();
	};
	
}