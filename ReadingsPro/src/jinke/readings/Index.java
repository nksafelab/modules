package jinke.readings;

import com.jinke.rloginservice.IReadingsLoginService;
import com.jinke.rloginservice.UserInfo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Index extends Activity{
	
	private static final String TAG = "ReadingsLoginServiceTest";
	
	private Button isLoginBtn;
	private Button isBindingBtn;
	private Button loginBtn;
	private Button unBindingBtn;
	private Button getSimIDBtn;
	private Button getUserInfoBtn;
	
	private IReadingsLoginService loginService;
	
	private ServiceConnection serConn = new ServiceConnection() {
		
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
		}
	};
	
	private boolean isLogin(){
		try {
			return loginService.isLogin();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	private boolean isBinding(){
		try {
			return loginService.isBinding();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
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
	private boolean unBinding(){
		try {
			return loginService.unBinding();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	private String getSimID(){
		try {
			return loginService.getSimID();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	private UserInfo getUserInfo(){
		try {
			return loginService.getUserInfo();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(serConn);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_rservice);
		
		isLoginBtn = (Button)findViewById(R.id.isLoginBtn);
		isBindingBtn = (Button)findViewById(R.id.isBindingBtn);
		loginBtn = (Button)findViewById(R.id.loginBtn);
		unBindingBtn = (Button)findViewById(R.id.unBindingBtn);
		getSimIDBtn = (Button)findViewById(R.id.getSimIDBtn);
		getUserInfoBtn = (Button)findViewById(R.id.getUserInfoBtn);
		
		Intent intent = new Intent("com.jinke.rloginservice.IReadingsLoginService");
		if(bindService(intent, serConn, BIND_AUTO_CREATE)){
			Toast.makeText(Index.this, "bindService() Success",Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(Index.this, "bindService() ERROR",Toast.LENGTH_LONG).show();
		}
		
		
		
		isLoginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("isLoginBtn clicked");
				Toast.makeText(Index.this, "isLoginBtn isLogin() "+isLogin(), Toast.LENGTH_LONG).show();
			}
		});
		isBindingBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(Index.this, "isBindingBtn isBinding() "+isBinding(), Toast.LENGTH_LONG).show();
			}
		});
		loginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(Index.this, "loginBtn login() "+login(), Toast.LENGTH_LONG).show();
			}
		});
		unBindingBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(Index.this, "unBindingBtn unBinding() "+unBinding(),Toast.LENGTH_LONG).show();
			}
		});
		getSimIDBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(Index.this, "getSimIDBtn getSimID() "+getSimID(),Toast.LENGTH_LONG).show();
			}
		});
        getUserInfoBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UserInfo userInfo = getUserInfo();
				if(userInfo != null){
				Toast.makeText(Index.this, "getUserInfoBtn getUserInfo() \n" +
						"username = "+userInfo.getUsername()+"\n" +
										"City = "+userInfo.getCity()+"\n" +
												"Phone = "+userInfo.getPhone()+"\n" +
														"Emain = "+userInfo.getEmail(),Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(Index.this, "getUserInfoBtn false",Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
