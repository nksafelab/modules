package jinke.readings.util;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.util.List;

import jinke.readings.download.CNetTransfer;
import jinke.readings.parse.IParse;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class CThread implements Runnable {
	
	private String szUrl = null;
	private Handler handler = null;
	private IParse parse = null;
	
	private int source = 0;



	// Message锟斤拷式
	public CThread(String szUrl, IParse parse, Handler handler) {
		this.szUrl = szUrl;
		this.parse = parse;
		this.handler = handler;
	}
	// Message锟斤拷式
	public CThread(String szUrl, IParse parse, Handler handler, int source) {
		this.szUrl = szUrl;
		this.parse = parse;
		this.handler = handler;
		this.source = source;
	}


	@Override
	public void run() {
		

		
		try {
			
			//System.out.println(Thread.currentThread().getName()+"锟斤拷锟斤拷执锟叫★拷锟斤拷锟斤拷"); 
			
			InputStream oIS = CNetTransfer.GetXmlInputStream(this.szUrl);
			
			System.out.println("run()@CThread------- 获取xml InputStream---Thread.currentThread()----"+Thread.currentThread().getName());
			System.out.println("run()@CThread------- 获取xml InputStream---Thread.currentThread()----"+this.szUrl);
			if (parse.doParse(oIS)) {

					asyncMessageOperate(this.handler, this.parse.output(),this.parse.totalPage());

				}

		} catch (Exception e) {
			Message msg = new Message();
			msg.arg1 = -1;
			msg.arg2 = source;
			handler.sendMessage(msg);
			Log.v("cthread", "exception", e);
			System.out.println("Exception@CThread:--------------------------------"+e.getMessage());
		}
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	private boolean asyncMessageOperate(Handler handler, Object oData,int totalPage) {
		System.out.println("asyncMessageOperate@CThread:" + "始");
		Message msg = Message.obtain(handler);
		msg.obj = oData;
		msg.arg2 = source;
		
		handler.sendMessage(msg);

		return false;
	}

}
