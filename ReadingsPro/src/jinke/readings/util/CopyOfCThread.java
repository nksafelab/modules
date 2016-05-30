package jinke.readings.util;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.util.List;

import jinke.readings.Entity.WenkuInfo;
import jinke.readings.download.CNetTransfer;
import jinke.readings.parse.IParse;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class CopyOfCThread implements Runnable {
	
	private String szUrl = null;
	private IParse parse = null;
	private InvocationHandler delegator = null;
	private CDelegate delegate = null;
	private Handler handler = null;
	
	private String username;
	private String key;
	
	private String baiduUrl = null;
	private String iaskUrl = null;
	
	private int source = 0;
	
	

	private enum EType {
		MESSAGE(1), DELEGATE(2), PROXY(3);
		private int nType = 0;

		private EType(int nType) {
			this.nType = nType;
		}
	}

	private EType eType = null;

	// Proxy锟斤拷式
	public CopyOfCThread(String szUrl, IParse parse, InvocationHandler delegator) {
		this.szUrl = szUrl;
		this.parse = parse;
		this.delegator = delegator;
		this.eType = EType.PROXY;
	}

	// delegate锟斤拷式
	public CopyOfCThread(String szUrl, IParse parse, CDelegate delegate) {
		this.szUrl = szUrl;
		this.parse = parse;
		this.delegate = delegate;
		this.eType = EType.DELEGATE;
	}

	// Message锟斤拷式
	public CopyOfCThread(String szUrl, IParse parse, Handler handler) {
		this.szUrl = szUrl;
		this.parse = parse;
		this.handler = handler;
		this.eType = EType.MESSAGE;
	}
	// Message锟斤拷式
	public CopyOfCThread(String szUrl, IParse parse, Handler handler, int source) {
		this.szUrl = szUrl;
		this.parse = parse;
		this.handler = handler;
		this.eType = EType.MESSAGE;
		this.source = source;
		Log.e("CThread", "begin nView:"+source);
	}
	// Message锟斤拷式
	public CopyOfCThread(String szUrl, IParse parse, Handler handler, int source,String username,String key) {
		this.szUrl = szUrl;
		this.parse = parse;
		this.handler = handler;
		this.eType = EType.MESSAGE;
		this.source = source;
		
		this.username = username;
		System.out.println("CThread@@@@@@@@@@@@@@ username:"+username);
		this.key = key;
	}


	@Override
	public void run() {
		
		
		
		try {
			
			//System.out.println(Thread.currentThread().getName()+"锟斤拷锟斤拷执锟叫★拷锟斤拷锟斤拷"); 
			
			InputStream oIS = CNetTransfer.GetXmlInputStream(this.szUrl);
			
			System.out.println("run()@CThread------- 获取xml InputStream---Thread.currentThread()----"+Thread.currentThread().getName());
			if (parse.doParse(oIS)) {

				switch (this.eType) {
				case MESSAGE:
					List<WenkuInfo> ls = (List<WenkuInfo>)this.parse.output();
					if(ls.size() == 0){
						Message msg = Message.obtain(this.handler);
						msg.arg2 = -1;
						handler.sendMessage(msg);
						System.out.println("SearchResultList is null @CThread:--------------------------------");
					}else{
						asyncMessageOperate(this.handler, this.parse.output());
						
						
					}
					break;

				case DELEGATE:
					this.delegate.invoke(this.parse.output());
					break;

				case PROXY:
					break;

				default:
					break;
				}
				// this.delegate.invoke(new Object[] { parse.output()});
			}

		} catch (Exception e) {
			
			Message msg = Message.obtain(this.handler);
			
			msg.arg2 = -1;
			
			handler.sendMessage(msg);
			
			System.out.println("Exception@CThread:--------------------------------"+e.getMessage());
		}
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	private boolean asyncMessageOperate(Handler handler, Object oData) {
		//System.out.println("asyncMessageOperate@CThread:" + "始");
		Message msg = Message.obtain(handler);
		//System.out.println("asyncMessageOperate@CThread:" + "锟斤拷始1");
		msg.obj = oData;
		msg.arg1 = 	this.source;
		Log.e("CThread", "nView:"+this.source);
		//System.out.println(Thread.currentThread().getName()+"sendMessage"+msg.arg1);
		
		//List<WenkuInfo> ls = (List<WenkuInfo>)oData;
		//System.out.println("asyncMessageOperate@CThread:" + "锟斤拷始");
		handler.sendMessage(msg);
		//System.out.println("asyncMessageOperate@CThread sendMessage"+ls.size());

		return false;
	}

}
