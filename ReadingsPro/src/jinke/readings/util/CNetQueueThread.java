package jinke.readings.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import jinke.readings.download.CNetTransfer;
import jinke.readings.parse.IParse;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class CNetQueueThread implements Runnable {
	private Queue<CTask> queue = null;
	private CDelegate delegate = null;
	private Handler handler = null;
	private IParse parse = null;
	private int nType = 0;
	private int nView = 0;
	private  enum EType {
		MESSAGE(1), DELEGATE(2), PROXY(3);
		private int nType = 0;
		private EType(int nType) { 
			this.nType = nType;
		}
	}
	private EType eType = null;
	
	//delegate
	public CNetQueueThread(Queue<CTask> queue, IParse parse, CDelegate delegate) {
		this.queue = queue;
		this.delegate = delegate;
		this.eType = EType.DELEGATE;
	}
	
	//Message
	public CNetQueueThread(Queue<CTask> queue, IParse parse, Handler hanlder) {
		this.queue = queue;
		//System.out.println("queue size of CNetQueue " + queue.size());
		this.handler = hanlder;
		this.parse = parse;
		this.eType = EType.MESSAGE;
		
	}
	//Message
	public CNetQueueThread(Queue<CTask> queue, IParse parse, Handler hanlder,int nView) {
		this.queue = queue;
		//System.out.println("queue size of CNetQueue " + queue.size());
		this.handler = hanlder;
		this.parse = parse;
		this.eType = EType.MESSAGE;
		this.nView = nView;
		
	}

	@Override
	public void run() {
		CTask task;
		int nCount = this.queue.size();
		for (int i = 0; i < nCount; i++) {
			task = this.queue.poll();
			if (null != task) {
				
				try {
					InputStream oIS = new CNetTransfer().getBitStreamEx(task.szUrl);
					Log.e("ThumbThread", "thumbUrl:"+task.szUrl);
					parse.doParse(oIS);
					task.bmThumb = (Bitmap)parse.output();
					switch (this.eType) {
					case MESSAGE:
						Message msg = Message.obtain(handler);
						msg.obj = task;
						msg.arg1 = nView;
						handler.sendMessage(msg);
						break;
					case DELEGATE:
						this.delegate.invoke(task);
						break;
					default:
						break;
					}
				} catch (Exception e) {
					System.out.println("Exception-------run()@CNetQueue更新bitMap------获得图片异常，结果为------------ " + e.getMessage());
					e.printStackTrace();
				}
			}
			else {
				System.out.println("run()@CNetQueue更新bitMap------没有图片信息------------ ");
			}
		}
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	public void parse() {

	}

}
