package jinke.readings;

import jinke.readings.CHistoryActivity.bindThread;

import com.jinke.rloginservice.IReadingsLoginService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CMain extends Activity {
	/** Called when the activity is first created. */
	private static final String TAG = "CMain";
	private Bundle savedInstanceState;
	private AlertDialog.Builder builder;
    private String PreInput = null;

	private void dialog() {
		
		builder = new Builder(CMain.this);
		//builder.setMessage("Readings当前尚未登录，是否跳转到登录界面");
		builder.setMessage(getString(R.string.Dialog_Login_Msg).toString());
		builder.setTitle(getString(R.string.Dialog_Login_Title).toString());
		builder.setNegativeButton(getString(R.string.Button_exit).toString(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				unbindService(isLoginConn);
				CMain.this.finish();
			}
		});

		builder.setPositiveButton(getString(R.string.Button_ok).toString(), new DialogInterface.OnClickListener() {
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
					//login
					Intent mIntent = new Intent();
					mIntent.setClass(CMain.this, CHistoryActivity.class);
					//zhs trans parameter
                    if(PreInput!=null)
                        mIntent.putExtra("search", PreInput); 
					CMain.this.startActivity(mIntent);
					unbindService(isLoginConn);
					CMain.this.finish(); 
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
	private boolean isLogin(){
		try {
			return loginService.isLogin();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.savedInstanceState = savedInstanceState;
		System.out.println("onCreate@CMain--------------------");
        //zhs get parameter 
        Intent myIntent = getIntent();
        PreInput = myIntent.getStringExtra("searchflag");
		
		Intent intent = new Intent("com.jinke.rloginservice.IReadingsLoginService");
		if(bindService(intent, isLoginConn, BIND_AUTO_CREATE)){
			//Toast.makeText(CMain.this, "bindService() Success",Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(CMain.this, "bindService() ERROR",Toast.LENGTH_LONG).show();
		}
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onDestory() called");
//		unbindService(isLoginConn);
		super.onDestroy();
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onRestart() called");
		onCreate(savedInstanceState);
		
		super.onRestart();
	}
	
}
