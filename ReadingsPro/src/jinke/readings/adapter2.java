package jinke.readings;



import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class adapter2 extends ArrayAdapter<String>{

	public adapter2(Context context, int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub

		this.fileArray = (ArrayList<String>) objects;
		this.ctx = context;
		//Log.v("Constructor","---------------------"); 
	}

	private ArrayList<String> fileArray;
	private Context ctx;
	private String CreateIP = "";
	private String LocalHost = "";
	
	public void setStr(String cip,String loh)
	{
		this.CreateIP = cip;
		this.LocalHost = loh;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		System.out.println("changed @getView called");
		
		//Log.v("WHAT", "===============================================");
		String filePath = fileArray.get(position);
		//Log.v("TEST", filePath);
		String filename = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
		Log.v("TEST",filename);
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(convertView == null){
			convertView = inflater.inflate(R.layout.push_list_item, null);
		}
		
		ImageView iv = (ImageView) convertView.findViewById(R.id.pushImg);
		
		
			iv.setImageResource(R.drawable.default_book);
		
		
		TextView tv = (TextView) convertView.findViewById(R.id.pushInfo);	
		tv.setText(filename);
		System.out.println("file name "+ position + " "+ filename);
		tv.setTextColor(Color.BLACK);

		
		return convertView;
	}
}