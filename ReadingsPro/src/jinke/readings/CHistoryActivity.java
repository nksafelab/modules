package jinke.readings;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.jinke.rloginservice.IReadingsLoginService;
import com.jinke.rloginservice.UserInfo;

import jinke.readings.datebase.CDBPersistent_Key;
import jinke.readings.download.CNetTransfer;
import jinke.readings.parse.HotWordParse;
import jinke.readings.parse.IParse;
import jinke.readings.util.CThread;
import jinke.readings.util.IdentifyUtil;
import jinke.readings.util.PushReciever;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CHistoryActivity extends Activity {

	private List<TextView> historyTextViewList;
	private List<TextView> hotTextViewList;
	private List<String> ls = null;
	private List<String> history_string;
	private List<String> hot_string;

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

	private int[] select = { WENKU, IASK, MK, WENKU3G, YOUKU, XIAOSHUO,GOOGLEBOOK,TXTR,CARTOON,P2P};
	private boolean[] selected = { false, false, false, false, false, false ,false,false,false,false};
	private TextView history_t1, history_t2, history_t3, history_t4,
			history_t5, history_t6, history_t7, history_t8, history_t9,
			history_t10, history_t11, history_t12;
	private TextView hot_t1, hot_t2, hot_t3, hot_t4, hot_t5, hot_t6, hot_t7,
			hot_t8, hot_t9, hot_t10, hot_t11, hot_t12;
	private Button search_button;
	private Button buttom_button_back;
	private Button theme_button;
	private Button buttom_button_functionmenu;
	private EditText edit;

	private int i = 0;
	private int j = 0;
	public static String username = "";
	private String devID;
	private String text;
	private String bindResult;
	
	//private String sourcePath = "/sdcard/readings/searchSource"; rm by yjy
	private String sourcePath = Environment.getDataDirectory()+"/data/jinke.readings/searchSource";
	
	private String dialogTitle = "";
	private String bindStr = "";
	
	private boolean isBind;

	private ProgressBar bindBar;
	private TextView bindBarInfo;
	private CDBPersistent_Key cDBPersistent_key;
	AlertDialog.Builder builder;
	private IReadingsLoginService loginService;
	private UserInfo userInfo;
	private TextView userNameTxv;
	
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
				if(username.length() > 13){
					username = username.substring(0, 13)+"...";
				}
				userNameTxv.setText(username);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	// 获得线程传回来的List，更新界面TextView，并更新数据库
	Handler hothandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			 System.out.println("get handler message");

			CDBPersistent_Key cDBPersistent_key = new CDBPersistent_Key(
					CHistoryActivity.this, "hotword");
			cDBPersistent_key.open();
			if(ls == null){
				System.out.println("##############################返回结果为空，不删除数据库中缓存数据");
				cDBPersistent_key.close();
			}else{
				System.out.println("##############################返回结果不空，删除数据库中缓存数据");
				
				cDBPersistent_key.deleteAll();
				cDBPersistent_key.close();
			}
			System.out.println("###############CHistoryActivity----------------------deleteAll() hotword");
			ls = (List<String>) msg.obj;
			String key = "";
			if(ls == null){
//				dialogOffLine();
				Toast.makeText(CHistoryActivity.this, getString(R.string.Toast_offLine).toString(), Toast.LENGTH_LONG).show();
				return;
			}
			for (int i = 0; i < ls.size(); i++) {
				key = ls.get(i);
				hotTextViewList.get(i).setText(key);
				
				hotTextViewList.get(i).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

								TextView tv = (TextView) v;

								String search_context = (String) tv
										.getText();
								//System.out.println("==========="+ search_context);
								Bundle b = new Bundle();
								b.putString("search_context",
										search_context);
								Intent intent = new Intent();
								intent.putExtras(b);
//								intent.setClass(CHistoryActivity.this,CMainUI.class);
								intent.setClass(CHistoryActivity.this,SynMainUIActivity.class);
								startActivity(intent);
							}
						});
				cDBPersistent_key.open();
				cDBPersistent_key.insertKey(key);
				cDBPersistent_key.close();
			}
			
			super.handleMessage(msg);

		}
	};
	
	private void updateHistory(){
		cDBPersistent_key = new CDBPersistent_Key(
				CHistoryActivity.this, "keyword");
		
		cDBPersistent_key.open();

		Cursor cursor = cDBPersistent_key.getAllKey();

		cDBPersistent_key.close();

		i = 0;
		history_string = new ArrayList<String>();
		if (cursor.getCount() != 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast() && i<12; cursor
					.moveToNext()) {
				if (historyTextViewList == null) {
					System.out.println("null");
				}
				System.out.println("CHistoryActivity : get(i)"+i);
				if (historyTextViewList.get(i) == null) {
					System.out.println("get(i) null");
				} else {
					historyTextViewList.get(i).setText(
							cursor.getString(cursor.getColumnIndex("key")));
					text = cursor.getString(cursor.getColumnIndex("key"));
					history_string.add(text);
					// 设置各个TextView点击事件
					historyTextViewList.get(i).setOnClickListener(
							new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									TextView tv = (TextView) v;
									String search_context = (String) tv
											.getText();
									System.out.println("==========="
											+ search_context);
									Bundle b = new Bundle();
									b.putString("search_context",
											search_context);
									Intent intent = new Intent();
									intent.putExtras(b);
//									intent.setClass(CHistoryActivity.this,CMainUI.class);
									intent.setClass(CHistoryActivity.this,SynMainUIActivity.class);
									System.out
											.println("CHistoryActivity---------------@startActivity()");
									startActivity(intent);
								}
							});
				}
				i++;
			}
		} else {

		}
		cursor.close();
	}
	
	private boolean login(){
		try {
			return loginService.login("marklee","marklee");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("CHistoryActivity--------------onCreate");		
		setContentView(R.layout.chistory);
		
		userNameTxv = (TextView)findViewById(R.id.usernameTextView);
		
		
		Intent intent = new Intent("com.jinke.rloginservice.IReadingsLoginService");
		if(bindService(intent, serConn, BIND_AUTO_CREATE)){
			//Toast.makeText(CHistoryActivity.this, "bindService() Success",Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(CHistoryActivity.this, "bindService() ERROR",Toast.LENGTH_LONG).show();
		}
		
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
				CHistoryActivity.this.openOptionsMenu();
			}
		});

		edit = (EditText) findViewById(R.id.search_Edit);

		edit.setFocusable(true);
		edit.setFocusableInTouchMode(true);
		edit.clearFocus();
        //add by zhs, for pre set search pararmeter.
        Intent myIntent = getIntent();
        String para = myIntent.getStringExtra("search");
        if(para!=null)
            edit.setText(para);

		search_button = (Button) findViewById(R.id.searchButton);
		search_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String search_context = search_context = edit.getText()
						.toString();
				Bundle b = new Bundle();
				b.putString("search_context", search_context);
				b.putString("username", username);
				Intent intent = new Intent();
				intent.putExtras(b);
//				intent.setClass(CHistoryActivity.this, CMainUI.class);
				intent.setClass(CHistoryActivity.this, SynMainUIActivity.class);
				startActivity(intent);
			}
		});
		
		theme_button = (Button)findViewById(R.id.themeButton);
		theme_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String search_context = search_context = edit.getText()
							.toString();
				Bundle b = new Bundle();
				b.putString("search_context", search_context);
				b.putString("username", username);
				Intent intent = new Intent();
				intent.putExtras(b);
		//		intent.setClass(CHistoryActivity.this, CMainUI.class);
				intent.setClass(CHistoryActivity.this, themesearch.class);
				startActivity(intent);
			}
		});

		historyTextViewList = new ArrayList<TextView>();
		hotTextViewList = new ArrayList<TextView>();

		history_t1 = (TextView) findViewById(R.id.t1);
		history_t2 = (TextView) findViewById(R.id.t2);
		history_t3 = (TextView) findViewById(R.id.t3);
		history_t4 = (TextView) findViewById(R.id.t4);
		history_t5 = (TextView) findViewById(R.id.t5);
		history_t6 = (TextView) findViewById(R.id.t6);
		history_t7 = (TextView) findViewById(R.id.t7);
		history_t8 = (TextView) findViewById(R.id.t8);
		history_t9 = (TextView) findViewById(R.id.t9);
		history_t10 = (TextView) findViewById(R.id.t10);
		history_t11 = (TextView) findViewById(R.id.t11);
		history_t12 = (TextView) findViewById(R.id.t12);
		historyTextViewList.add(history_t1);
		historyTextViewList.add(history_t2);
		historyTextViewList.add(history_t3);
		historyTextViewList.add(history_t4);
		historyTextViewList.add(history_t5);
		historyTextViewList.add(history_t6);
		historyTextViewList.add(history_t7);
		historyTextViewList.add(history_t8);
		historyTextViewList.add(history_t9);
		historyTextViewList.add(history_t10);
		historyTextViewList.add(history_t11);
		historyTextViewList.add(history_t12);

		hot_t1 = (TextView) findViewById(R.id.tt1);
		hot_t2 = (TextView) findViewById(R.id.tt2);
		hot_t3 = (TextView) findViewById(R.id.tt3);
		hot_t4 = (TextView) findViewById(R.id.tt4);
		hot_t5 = (TextView) findViewById(R.id.tt5);
		hot_t6 = (TextView) findViewById(R.id.tt6);
		hot_t7 = (TextView) findViewById(R.id.tt7);
		hot_t8 = (TextView) findViewById(R.id.tt8);
		hot_t9 = (TextView) findViewById(R.id.tt9);
		hot_t10 = (TextView) findViewById(R.id.tt10);
		hot_t11 = (TextView) findViewById(R.id.tt11);
		hot_t12 = (TextView) findViewById(R.id.tt12);
		hotTextViewList.add(hot_t1);
		hotTextViewList.add(hot_t2);
		hotTextViewList.add(hot_t3);
		hotTextViewList.add(hot_t4);
		hotTextViewList.add(hot_t5);
		hotTextViewList.add(hot_t6);
		hotTextViewList.add(hot_t7);
		hotTextViewList.add(hot_t8);
		hotTextViewList.add(hot_t9);
		hotTextViewList.add(hot_t10);
		hotTextViewList.add(hot_t11);
		hotTextViewList.add(hot_t12);

		// [2]--如果是在SD卡中创建数据库，那么实例化sqlitedb的操作如下
		// sqlitedb = SQLiteDatabase.openOrCreateDatabase(f, null);

		/* sqlite中的搜索历史 */
		updateHistory();
		/* sqlite中的搜索热词 */
		cDBPersistent_key = new CDBPersistent_Key(CHistoryActivity.this,
				"hotword");
		cDBPersistent_key.open();

		Cursor cursor1 = cDBPersistent_key.getAllKey();

		cDBPersistent_key.close();

		j = 0;
		hot_string = new ArrayList<String>();
		if (cursor1.getCount() != 0) {
			for (cursor1.moveToFirst(); !cursor1.isAfterLast(); cursor1
					.moveToNext()) {
				//System.out.println("count:" + cursor1.getCount());
				String hotkey = cursor1
						.getString(cursor1.getColumnIndex("key"));
				// System.out.print(cursor1.getString(cursor1.getColumnIndex("key")));
				// System.out.println("	"+cursor1.getString(cursor1.getColumnIndex("created")));

				//System.out.println("hot text:--------" + j + "-------" + text);
				if (hotTextViewList == null) {
					System.out.println("null");
				}
				if (hotTextViewList.get(j) == null) {
					System.out.println("get(i) null");
				} else {
					hotTextViewList.get(j).setText(
							cursor1.getString(cursor1.getColumnIndex("key")));
					hot_string.add(hotkey);
					hotTextViewList.get(j).setOnClickListener(
							new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub

									TextView tv = (TextView) v;

									String search_context = (String) tv
											.getText();
									//System.out.println("==========="+ search_context);
									Bundle b = new Bundle();
									b.putString("search_context",
											search_context);
									Intent intent = new Intent();
									intent.putExtras(b);
//									intent.setClass(CHistoryActivity.this,CMainUI.class);
									intent.setClass(CHistoryActivity.this,SynMainUIActivity.class);
									startActivity(intent);
								}
							});

				}
				j++;
			}
		} else {

		} 
		cursor1.close();
		//System.out.println("CHistoryActivity----------before start thread");
		/* 开启线程，后台取服务器热词数据 */
		String szUrl = getString(R.string.url_hotword) + "?n=12";
		IParse parse = new HotWordParse(12);
		System.out.println("CHistoryActivity----------onCreate   create thread load hotword");
		CThread thread = new CThread(szUrl, parse, hothandler);
		thread.start();
		//add by zhuhs 
        if(edit.getText().toString().length()!= 0)
        {
            Log.i("zhuhs",edit.getText().toString());
            Bundle b = new Bundle();
            b.putString("search_context", edit.getText().toString());
            Intent intents = new Intent();
            intents.putExtras(b);
            intents.setClass(CHistoryActivity.this, SynMainUIActivity.class);
            startActivity(intents);
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
			String B_WENKU = getString(R.string.SearchSource_baiduwenku).toString();
			String X_IASK = getString(R.string.SearchSource_iask).toString();
			String Q_QUNLUO = getString(R.string.SearchSource_mk).toString();
			String B_3G = getString(R.string.SearchSource_baiduwenku3g).toString();
			String Y_YOUKU = getString(R.string.SearchSource_youku).toString();
			String B_XIAOSHUO = getString(R.string.SearchSource_baiduxs).toString();
			String G_GOOGLE = getString(R.string.SearchSource_googlebook).toString();
			String T_TXTR = getString(R.string.SearchSource_txtr).toString();
			String D_CARTOON = getString(R.string.SearchSource_cartoon).toString();
			// Toast.makeText(this, "删除菜单被点击了", Toast.LENGTH_LONG).show();

			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.Menu_searchSourceSetting).toString())
					.setMultiChoiceItems(
//							new String[] { "百度文库", "新浪iask", "群落阅读", "百度文库3G",
//									"优酷", "百度小说" ,"谷歌图书(英文搜索源)","TXTR(英文搜索源)","动漫部落"}, selected,
							new String[] { B_WENKU, X_IASK, Q_QUNLUO, B_3G,
									Y_YOUKU, B_XIAOSHUO ,G_GOOGLE,T_TXTR,D_CARTOON,"P2P"}, selected,
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
			// Toast.makeText(this, "保存菜单被点击了", Toast.LENGTH_LONG).show();
			break;
		case Menu.FIRST + 3:
			try {
					loginService.loginActivity();
					CHistoryActivity.this.finish();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 用户解绑定
			// Toast.makeText(this, "帮助菜单被点击了", Toast.LENGTH_LONG).show();
			break;
		case Menu.FIRST + 4:
			// 重新登录
			try {
				loginService.loginActivity();
				CHistoryActivity.this.finish();
			} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
			/*
			Intent mIntent = new Intent();
			mIntent.setClass(CHistoryActivity.this, CMain.class);
			mIntent.putExtra("reset", true);
			startActivity(mIntent);
			// Toast.makeText(this, "添加菜单被点击了", Toast.LENGTH_LONG).show();
			 */
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
		
		builder = new Builder(CHistoryActivity.this);
		builder.setMessage(msg);

		builder.setTitle(dialogTitle);

		builder.setNegativeButton(getString(R.string.Button_cancle).toString(), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
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

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		System.out.println("CHistoryActivity------------onRestart");
		updateHistory();
		super.onRestart();
	}

	@Override
	protected void onResume() {
		System.out.println("CHistoryActivity------------onResume");
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("CHistoryActivity------------onDestroy");
		unbindService(serConn);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		System.out.println("CHistoryActivity------------onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		System.out.println("CHistoryActivity------------onStop");
		super.onStop();
	}

	
	
	
}
