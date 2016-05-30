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
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import jinke.readings.wedget.Horizontal;
import jinke.readings.wedget.MyHorizontal;

public class SynMainUIActivity extends Activity implements OnClickListener {

	 //20111215
	private MyHorizontal mHorizontal1;
	private MyHorizontal mHorizontal2;
//	private ViewFlipper mViewFlipper;
//	private GestureDetector mGestureDetector;

	// ////////////////////////////////
	private Button btnSearchButton = null;
	private Button syn_data_btnSearchButton = null;
	private Button syn_paper_btnSearchButton = null;

	private Button buttom_button_back;
	private Button buttom_button_functionmenu;

	public static EditText edit;
	public static EditText syn_data_edit;
	public static EditText syn_paper_edit;

	private FrameLayout frame = null;
	private FrameLayout syn_paper_frame = null;
	private FrameLayout syn_data_frame = null;

	protected LocalActivityManager mLocalActivityManager;

	private View mLaunchedView;
	private View syn_paper_mLaunchedView;
	private View syn_data_mLaunchedView;

	private Bundle bunlde;
	public static final int FOCUS_AFTER_DESCENDANTS = 0x40000;

	private final int SYN_DATA_SEARCH = 1;
	private final int SYN_PAPER_SEARCH = 2;
	private final int SEARCH = 3;

	private Intent intent = null;
	private String search_context = null;
	private String syn_data_search_context = null;
	private String syn_paper_search_context = null;

	public static final int WENKU = 1;
	public static final int IASK = 2;
	public static final int MK = 3;
	public static final int WENKU3G = 4;
	public static final int YOUKU = 5;
	public static final int XIAOSHUO = 6;
	public static final int GOOGLEBOOK = 7;
	public static final int TXTR = 8;
	public static final int CARTOON = 9;
	public static final int P2P = 0;

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
	// private String sourcePath = "/sdcard/readings/searchSource"; rm by yjy
	private String sourcePath = Environment.getDataDirectory()
			+ "/data/jinke.readings/searchSource";

	private String dialogTitle = "";
	private String bindStr = "";
	private boolean isBind;

	private int[] select = { WENKU, IASK, MK, WENKU3G, YOUKU, XIAOSHUO,
			GOOGLEBOOK, TXTR, CARTOON, P2P };
	private boolean[] selected = { false, false, false, false, false, false,
			false, false, false, false };

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
				username = userInfo.getUsername();
				if (username.length() > 13) {
					username = username.substring(0, 13) + "...";
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
		setContentView(R.layout.syncmainui123);

//		mViewFlipper = (ViewFlipper) findViewById(R.id.details);
//		mGestureDetector = new GestureDetector(this);
//		mViewFlipper.addView(getView(R.id.table1));
		

		/*
		 * 给各view的顶层控件设置onClickListener。
		 */
//		initHorizontal();

		usernameTextView = (TextView) findViewById(R.id.usernameTextView);
		Intent intent = new Intent(
				"com.jinke.rloginservice.IReadingsLoginService");
		if (bindService(intent, serConn, BIND_AUTO_CREATE)) {
			// Toast.makeText(CHistoryActivity.this,
			// "bindService() Success",Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(SynMainUIActivity.this, "bindService() ERROR",
					Toast.LENGTH_LONG).show();
		}

		/*
		 * 初始化国际化字符串 B_WENKU; X_IASK; Q_QUNLUO ...
		 */
		initString();

		intent1 = getIntent();
		Bundle b = intent1.getExtras();
		search_context = b.getString("search_context");
		syn_data_search_context = b.getString("search_context");
		syn_paper_search_context = b.getString("search_context");
		username= b.getString("username");
		b.putString("username", username);

		/*
		 * 获得xml中各个控件:button,frame..
		 */
		setupView();

		intent1.putExtras(b);
		intent1.setClass(SynMainUIActivity.this, SearchResultActivity.class);
		/*
		 * 改变view1中的Frame的Activity
		 */
		ChangeActivity("hot", intent1, true, true, true);
		/*
		 * 设置搜索栏中当前的搜索关键字
		 */
		initSearchEdit(b);

	}

//	private View getView(int portraitContainerbody) {
//		// TODO Auto-generated method stub
//		TableLayout fl = (TableLayout) findViewById(portraitContainerbody);
//		
//		return fl;
//	}

	private Handler myChangeActivity = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Bundle b;
			super.handleMessage(msg);
			switch (msg.what) {
//			case SYN_DATA_SEARCH:
//				Toast.makeText(SynMainUIActivity.this, "数图搜索按钮",
//						Toast.LENGTH_SHORT).show();
//				ChangeActivity("hot", intent, false, false, true);
//				break;
//			case SYN_PAPER_SEARCH:
//				Toast.makeText(SynMainUIActivity.this, "纸图搜索按钮",
//						Toast.LENGTH_SHORT).show();
//				ChangeActivity("hot", intent, false, true, false);
//				break;
			case SEARCH:
				intent = new Intent(SynMainUIActivity.this,
						SearchResultActivity.class);
				b = new Bundle();
				b.putString("search_context", search_context);
				b.putString("username", username);
				intent.putExtras(b);
				ChangeActivity("hot", intent, true, true, true);
				break;
			default:
				break;
			}
		}
	};

	public LocalActivityManager getLocalActivityManager() {
		return new LocalActivityManager(this, true);
	}

	private void initSyn_data_Frame() {

		Intent syn_data_intent = new Intent();
		Bundle mb = new Bundle();
		mb.putString("search_content", syn_data_search_context);
		mb.putInt("search_type", SYN_DATA_SEARCH);
		mb.putString("username", username);
		syn_data_intent.setClass(SynMainUIActivity.this,
				SynDResultListActivity.class);
		syn_data_intent.putExtras(mb);

		final Window syn_data_w = mLocalActivityManager.startActivity(
				"syn_data", syn_data_intent);
		final View syn_data_wd = syn_data_w != null ? syn_data_w.getDecorView()
				: null;
		if (syn_data_mLaunchedView != syn_data_wd
				&& syn_data_mLaunchedView != null) {
			System.out.println("mLaunchedView !=null & !=wd,");
			if (syn_data_mLaunchedView.getParent() != null) {
				syn_data_frame.removeView(syn_data_mLaunchedView);
				// System.out.println("remove mLaunchedView");
			}
		}
		if (null != syn_data_mLaunchedView
				&& syn_data_mLaunchedView == syn_data_wd) {
			System.out
					.println("mLaunchedView !=null & ==wd,return do nothing!");
			return;
		}
		syn_data_mLaunchedView = syn_data_wd;
		if (syn_data_mLaunchedView != null) {
			// mLaunchedView.setVisibility(View.VISIBLE);
			syn_data_mLaunchedView.setFocusableInTouchMode(true);
			((ViewGroup) syn_data_mLaunchedView)
					.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
		}
		syn_data_frame.addView(syn_data_mLaunchedView,
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
	}

	private void initSyn_paper_Frame() {

		Intent syn_paper_intent = new Intent();
		Bundle mbb = new Bundle();
		mbb.putString("search_content", syn_paper_search_context);
		mbb.putString("username", username);
		mbb.putInt("search_type", SYN_PAPER_SEARCH);
		syn_paper_intent.setClass(SynMainUIActivity.this,
				SynResultListActivity.class);
		syn_paper_intent.putExtras(mbb);

		final Window syn_paper_w = mLocalActivityManager.startActivity(
				"syn_paper", syn_paper_intent);
		final View syn_paper_wd = syn_paper_w != null ? syn_paper_w
				.getDecorView() : null;
		if (syn_paper_mLaunchedView != syn_paper_wd
				&& syn_paper_mLaunchedView != null) {
			System.out.println("mLaunchedView !=null & !=wd,");
			if (syn_paper_mLaunchedView.getParent() != null) {
				syn_paper_frame.removeView(syn_paper_mLaunchedView);
				// System.out.println("remove mLaunchedView");
			}
		}
		if (null != syn_paper_mLaunchedView
				&& syn_paper_mLaunchedView == syn_paper_wd) {
			System.out
					.println("mLaunchedView !=null & ==wd,return do nothing!");
			return;
		}
		syn_paper_mLaunchedView = syn_paper_wd;
		if (syn_paper_mLaunchedView != null) {
			// mLaunchedView.setVisibility(View.VISIBLE);
			syn_paper_mLaunchedView.setFocusableInTouchMode(true);
			((ViewGroup) syn_paper_mLaunchedView)
					.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
		}
		syn_paper_frame.addView(syn_paper_mLaunchedView,
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
	}

	private void init_Frame(String Id, Intent intent) {
		final Window w = mLocalActivityManager.startActivity(Id, intent);
		final View wd = w != null ? w.getDecorView() : null;
		if (mLaunchedView != wd && mLaunchedView != null) {
			System.out.println("mLaunchedView !=null & !=wd,");
			if (mLaunchedView.getParent() != null) {
				frame.removeView(mLaunchedView);
			}
		}
		if (null != mLaunchedView && mLaunchedView == wd) {
			System.out
					.println("mLaunchedView !=null & ==wd,return do nothing!");
			return;
		}
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
	}

	public void ChangeActivity(String Id, Intent intent, boolean flag,
			boolean syn_paper_flag, boolean syn_data_flag) {
		if (null == mLocalActivityManager) {
			mLocalActivityManager = getLocalActivityManager();
			mLocalActivityManager.dispatchCreate(bunlde);
		}
		mLocalActivityManager.removeAllActivities();

		init_Frame(Id, intent);
		initSyn_paper_Frame();
		initSyn_data_Frame();
//		
//		if (flag)
//			init_Frame(Id, intent);
//
//		if (syn_paper_flag)
//			initSyn_paper_Frame();
//		if (syn_data_flag)
//			initSyn_data_Frame();

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.searchButton:
			EditText edit = (EditText) findViewById(R.id.search_Edit);
			search_context = edit.getText().toString();
			syn_data_search_context = edit.getText().toString();
			syn_paper_search_context = edit.getText().toString();
			myChangeActivity.sendEmptyMessage(SEARCH);
			break;

//		case R.id.syn_data_searchButton:
//			syn_data_search_context = syn_data_edit.getText().toString();
//			myChangeActivity.sendEmptyMessage(SYN_DATA_SEARCH);
//			break;
//		case R.id.searchButton:
//			EditText edit = (EditText) findViewById(R.id.search_Edit);
//			search_context = edit.getText().toString();
//			myChangeActivity.sendEmptyMessage(SEARCH);
//			break;

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
		menu.add(Menu.NONE, Menu.FIRST + 1, 1,
				getString(R.string.Menu_searchSourceSetting).toString())
				.setIcon(android.R.drawable.ic_menu_set_as);
		// setIcon()方法为菜单设置图标，这里使用的是系统自带的图标，同学们留意一下,以
		// android.R开头的资源是系统提供的，我们自己提供的资源是以R开头的
		// menu.add(Menu.NONE, Menu.FIRST + 2, 2,
		// getString(R.string.Menu_userBinding).toString()).setIcon(
		// android.R.drawable.ic_menu_edit);
		menu.add(Menu.NONE, Menu.FIRST + 3, 3,
				getString(R.string.Menu_userUnBinding).toString()).setIcon(
				android.R.drawable.ic_menu_help);
		menu.add(Menu.NONE, Menu.FIRST + 4, 4,
				getString(R.string.Menu_reLogin).toString()).setIcon(
				android.R.drawable.ic_menu_add);
		// menu.add(Menu.NONE, Menu.FIRST + 5, 5, "详细").setIcon(
		// android.R.drawable.ic_menu_info_details);
		// menu.add(Menu.NONE, Menu.FIRST + 6, 6, "发送").setIcon(
		// android.R.drawable.ic_menu_send);
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
					.setTitle(
							getString(R.string.Menu_searchSourceSetting)
									.toString())
					.setMultiChoiceItems(
							// new String[] { "百度文库", "新浪iask", "群落阅读",
							// "百度文库3G",
							// "优酷", "百度小说","谷歌图书(英文搜索源)","TXTR(英文搜索源)","动漫部落"},
							// selected,
							new String[] { B_WENKU, X_IASK, Q_QUNLUO, B_3G,
									Y_YOUKU, B_XIAOSHUO, G_GOOGLE, T_TXTR,
									D_CARTOON, "P2P" }, selected,
							new DialogInterface.OnMultiChoiceClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which, boolean isChecked) {
									// TODO Auto-generated method stub
									selected[which] = isChecked;
								}
							})
					.setPositiveButton(
							getString(R.string.Button_ok).toString(),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									String selectedStr = "";
									for (int i = 0; i < selected.length; i++) {
										if (selected[i] == true) {
											// System.out
											// .println("selected source : "
											// + select[i]);
											selected[i] = false;
											selectedStr += select[i];
										}
									}
									// System.out.println("selectedStr: "
									// + selectedStr);

									PushReciever.write(sourcePath, selectedStr);

								}
							}).setNegativeButton(
							getString(R.string.Button_cancle).toString(), null)
					.show();

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
		// System.out.println("select source is :------------" + s);
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
			case 0:
				s = s / 10;
				selected[9] = true;
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
		if (isBind == true) {
			msg = " 当前用户：" + username + "\n 当前设备ID：" + devID
					+ "\n 每个设备最多可绑定5个帐号，确定执行绑定？";
			dialogTitle = "用户绑定";
			bindStr = "http://61.181.14.184:8084/Readings/bangdingDevice.do?devid="
					+ devID + "&username=" + username;
		} else {
			msg = " 当前用户：" + username + "\n 当前设备ID：" + devID + "\n 确定执行解绑？";
			dialogTitle = "用户解绑";
			bindStr = "http://61.181.14.184:8084/Readings/jiebangdingDevice.do?devid="
					+ devID + "&username=" + username;
		}

		builder = new Builder(SynMainUIActivity.this);
		builder.setMessage(msg);

		builder.setTitle(dialogTitle);

		builder.setNegativeButton(getString(R.string.Button_cancle).toString(),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.setPositiveButton(getString(R.string.Button_ok).toString(),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						// System.out.println("------------bindResult-----------"+bindResult);

						LayoutInflater inflater = getLayoutInflater();
						View layout = inflater.inflate(R.layout.dialog,
								(ViewGroup) findViewById(R.id.dialog));

						bindBar = (ProgressBar) layout
								.findViewById(R.id.bindBar);
						bindBarInfo = (TextView) layout
								.findViewById(R.id.bindBarInfo);
						bindBar.setVisibility(ProgressBar.VISIBLE);
						bindBarInfo.setVisibility(TextView.VISIBLE);

						builder.setMessage("");

						builder.setTitle(dialogTitle).setView(layout)
								.setPositiveButton(
										getString(R.string.Button_ok)
												.toString(), null)
								.setNegativeButton(
										getString(R.string.Button_cancle)
												.toString(), null).show();

						bindThread bt = new bindThread(bindStr);
						bt.start();

						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	class bindThread extends Thread {

		private String urlStr;

		public bindThread(String urlStr) {
			this.urlStr = urlStr;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// String urlStr =
			// "http://61.181.14.184:8084/Readings/bangdingDevice.do?devid="
			// + devID + "&username=" + username;
			System.out
					.println("bindThread@CHistoryActivity-------------------bindThread url-----------"
							+ urlStr);
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

			System.out
					.println("bindThread@CHistoryActivity:---------------绑定结果-------------"
							+ bindResult);
			if (bindResult != null) {
				int start = bindResult.lastIndexOf("<");
				// System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!start" + start);
				bindResult = bindResult.substring(start - 1, start);
				// System.out.println(bindResult);

				if (bindResult != "")
					msg.arg1 = Integer.parseInt(bindResult);
				bindHandler.sendMessage(msg);
			} else {
				msg.arg1 = 4;
				bindHandler.sendMessage(msg);
			}

			// Message msg = new Message();
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// msg.arg1 = 4;
			// bindHandler.sendMessage(msg);
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
			if (isBind) {
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
			} else {
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
			// bindBarInfo.setVisibility(TextView.GONE);
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
		syn_data_frame.removeView(syn_data_mLaunchedView);
		syn_paper_frame.removeView(syn_paper_mLaunchedView);
		super.onDestroy();
	};

//	private void initHorizontal() {
//		mHorizontal1 = (SwipeView) findViewById(R.id.horizon1);
//		mHorizontal2 = (SwipeView) findViewById(R.id.horizon2);
//		mHorizontal1.setScrollView(mHorizontal2);
//		mHorizontal2.setScrollView(mHorizontal1);
//	}

	private void setupView() {

		buttom_button_back = (Button) findViewById(R.id.buttom_button_back);
		buttom_button_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		buttom_button_functionmenu = (Button) findViewById(R.id.buttom_button_functionmenu);
		buttom_button_functionmenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SynMainUIActivity.this.openOptionsMenu();
			}
		});

		btnSearchButton = (Button) findViewById(R.id.searchButton);
		btnSearchButton.setOnClickListener(this);

		// syn_data_btnSearchButton = (Button)
		// findViewById(R.id.syn_data_searchButton);
		// syn_data_btnSearchButton.setOnClickListener(this);
		//
		// syn_paper_btnSearchButton = (Button)
		// findViewById(R.id.syn_paper_searchButton);
		// syn_paper_btnSearchButton.setOnClickListener(this);

		btnSearchButton.requestFocus();

		frame = (FrameLayout) findViewById(R.id.portrait_containerBody);
		syn_paper_frame = (FrameLayout) findViewById(R.id.syn_paper_portrait_containerBody);
		syn_data_frame = (FrameLayout) findViewById(R.id.syn_data_portrait_containerBody);

	}

	private void initString() {
		B_WENKU = SynMainUIActivity.this.getString(
				R.string.SearchSource_baiduwenku).toString();
		X_IASK = SynMainUIActivity.this.getString(R.string.SearchSource_iask)
				.toString();
		Q_QUNLUO = SynMainUIActivity.this.getString(R.string.SearchSource_mk)
				.toString();
		B_3G = SynMainUIActivity.this.getString(
				R.string.SearchSource_baiduwenku3g).toString();
		Y_YOUKU = SynMainUIActivity.this.getString(R.string.SearchSource_youku)
				.toString();
		B_XIAOSHUO = SynMainUIActivity.this.getString(
				R.string.SearchSource_baiduxs).toString();
		G_GOOGLE = SynMainUIActivity.this.getString(
				R.string.SearchSource_googlebook).toString();
		T_TXTR = SynMainUIActivity.this.getString(R.string.SearchSource_txtr)
				.toString();
		D_CARTOON = SynMainUIActivity.this.getString(
				R.string.SearchSource_cartoon).toString();
	}

	private void initSearchEdit(Bundle b) {
		edit = (EditText) findViewById(R.id.search_Edit);
		if (b != null) {
			String context = (String) b.get("search_context");
			edit.setText(context);
		}
		edit.setFocusable(true);
		edit.setFocusableInTouchMode(true);
		edit.clearFocus();

		// syn_data_edit = (EditText) findViewById(R.id.syn_data_search_Edit);
		// if (b != null) {
		// String context = (String) b.get("search_context");
		// syn_data_edit.setText(context);
		// }
		// syn_data_edit.setFocusable(true);
		// syn_data_edit.setFocusableInTouchMode(true);
		// syn_data_edit.clearFocus();
		//
		// syn_paper_edit = (EditText) findViewById(R.id.syn_paper_search_Edit);
		// if (b != null) {
		// String context = (String) b.get("search_context");
		// syn_paper_edit.setText(context);
		// }
		// syn_paper_edit.setFocusable(true);
		// syn_paper_edit.setFocusableInTouchMode(true);
		// syn_paper_edit.clearFocus();
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		return this.mGestureDetector.onTouchEvent(event);
//
//	}
//	@Override
//	public boolean onDown(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//			float velocityY) {
//		// TODO Auto-generated method stub
//		
//		if (e1.getX() > e2.getX()) {// move to left
//			mViewFlipper.showNext();
//		} else if (e1.getX() < e2.getX()) {
//			mViewFlipper.setInAnimation(getApplicationContext(),
//					R.anim.push_right_in);
//			mViewFlipper.setOutAnimation(getApplicationContext(),
//					R.anim.push_right_out);
//			mViewFlipper.showPrevious();
//			mViewFlipper.setInAnimation(getApplicationContext(),
//					R.anim.push_left_in);
//			mViewFlipper.setOutAnimation(getApplicationContext(),
//					R.anim.push_left_out);
//		} else {
//			return false;
//		}
//		return true;
//		
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
//
//	@Override
//	public boolean onDoubleTap(MotionEvent e) {
//		// TODO Auto-generated method stub
//		if (mViewFlipper.isFlipping()) {
//			mViewFlipper.stopFlipping();
//		} else {
//			mViewFlipper.startFlipping();
//		}
//		return true;
//	}
//
//	@Override
//	public boolean onDoubleTapEvent(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onSingleTapConfirmed(MotionEvent e) {
//		// TODO Auto-generated method stub
//		return false;
//	}

	//	
	// final byte DIR_RIGHT=1; //屏幕向右滑动
	// final byte DIR_LEFT=2; //屏幕向左滑动
	// byte mDir=DIR_LEFT; //滑动方向 默认在左边
	// float x=0; //横向屏幕滑动的down的x坐标
	// //屏幕的滑动效果:滑到小于半屏幕回到原点，大于半屏幕切换到下一个屏幕
	// void screenScrollOntouch(MotionEvent e){
	// if(e.getAction()==MotionEvent.ACTION_DOWN){
	// x=e.getX();
	// y = e.getY();
	// }
	// if(e.getAction()==MotionEvent.ACTION_UP){
	// if(mDir==DIR_LEFT){ //第一屏幕
	// if(e.getX()-x<-DIS/2){ //往左滑动大于屏幕的一半半
	// mScreenHS.smoothScrollTo(DIS, 0);
	// mDir=DIR_RIGHT;
	// }else if(e.getX()-x>-DIS/2&&e.getX()-x<0){ //往左滑动小于屏幕的一半
	// mScreenHS.smoothScrollTo((int) -(x-e.getX()), 0);
	// }
	// }else if(mDir==DIR_RIGHT){ //第二屏幕
	// if(e.getX()-x>DIS/2){
	// mScreenHS.smoothScrollTo(-DIS, 0);
	// mDir=DIR_LEFT;
	// }else if(e.getX()-x<DIS/2&&e.getX()-x>0){
	// mScreenHS.smoothScrollTo((int) (e.getX()-x)+DIS, 0);
	// }
	// // if(e.getY()+DOWNDIS<y){ //第三屏幕
	// // showThiedScreen();
	// // }else if(e.getY()>y+DOWNDIS){ //第二屏幕
	// showmScondScreen();
	// // }
	// }
	// }
	//        
	// //添加屏幕移动
	// if(e.getAction()==MotionEvent.ACTION_MOVE){
	// if(mDir==DIR_LEFT){
	// mScreenHS.smoothScrollTo((int) (x-e.getX()), 0);
	// //Log.d("LEFT", "go to left");
	//                  
	// }else if(mDir==DIR_RIGHT){
	// mScreenHS.smoothScrollTo((int) (x-e.getX()+DIS), 0);
	// //Log.d("RIGHT", "go to right");
	// }
	// }
	// }

}
