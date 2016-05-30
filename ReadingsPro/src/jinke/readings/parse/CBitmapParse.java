package jinke.readings.parse;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CBitmapParse implements IParse{

	private Bitmap bm = null;
	@Override
	public boolean doParse(InputStream oIS) throws Exception {
		
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = 4;
        bm = BitmapFactory.decodeStream(oIS, null , bitmapOptions);
		
		
		//bm = BitmapFactory.decodeStream(oIS);
		oIS.close();
		return true;
	}

	@Override
	public Object output() {
		
		return bm;
	}

	@Override
	public int totalPage() {
		// TODO Auto-generated method stub
		return 0;
	}

}
