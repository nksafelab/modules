package jinke.readings.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import jinke.readings.download.CNetTransfer;
import jinke.readings.parse.IParse;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class PriceQueueThread implements Runnable {
	private Queue<CTask> queue = null;
	private CDelegate delegate = null;
	private Handler handler = null;
	private int nType = 0;
	private  enum EType {
		MESSAGE(1), DELEGATE(2), PROXY(3);
		private int nType = 0;
		private EType(int nType) { 
			this.nType = nType;
		}
	}
	private EType eType = null;
	
	//delegate
	public PriceQueueThread(Queue<CTask> queue, IParse parse, CDelegate delegate) {
		this.queue = queue;
		this.delegate = delegate;
		this.eType = EType.DELEGATE;
	}
	
	//Message
	public PriceQueueThread(Queue<CTask> queue, Handler hanlder) {
		this.queue = queue;
		//System.out.println("queue size of CNetQueue " + queue.size());
		this.handler = hanlder;
		this.eType = EType.MESSAGE;
		
	}

	@Override
	public void run() {
		CTask task;
		int nCount = this.queue.size();
		for (int i = 0; i < nCount; i++) {
			task = this.queue.poll();
			if (null != task) {
				
				try {
//					InputStream oIS = new CNetTransfer().getBitStreamEx(task.szUrl);
					if(task.priceUrl1 != null){
						task.price1 = CNetTransfer.getUrl("http://61.181.14.184:8084/MultiSearch/search.do?method=getPrice&WebURLbyZJ="
								+task.priceUrl1+"&webSiteToken=1");
						int start = task.price1.indexOf("￥");
						task.price1 = task.price1.substring(start, start+6);
						Log.e("PriceThread", "getPrice1:"+task.price1);
					}
					if(task.priceUrl2 != null){
						task.price2 = CNetTransfer.getUrl("http://61.181.14.184:8084/MultiSearch/search.do?method=getPrice&WebURLbyZJ="
								+task.priceUrl2+"&webSiteToken=2");
						int start = task.price2.indexOf("FFE5");
						task.price2 = "￥"+task.price2.substring(start+4, start+9);
						Log.e("PriceThread", "getPrice2:"+task.price2.trim());
					}
					
					
					switch (this.eType) {
					case MESSAGE:
						Message msg = Message.obtain(handler);
						msg.obj = task;
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
