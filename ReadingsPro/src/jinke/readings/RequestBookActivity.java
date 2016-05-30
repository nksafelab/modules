package jinke.readings;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class RequestBookActivity extends Activity{

	
	private EditText request_book_title;
	private EditText request_book_author;
	private EditText request_book_abst;
	private EditText request_book_cost;
	private EditText request_book_tags1;
	private EditText request_book_tags2;
	
	private String title;
	private String author;
	private String abst;
	private String cost;
	private String tags1;
	private String tags2;
	
	private Button request,reset;
	
	private String type = "";
	
	private String url = "http://61.181.14.184:8088/Readings/saveRequestbook.do?";
	
	private void initEditText(){
		request_book_title = (EditText)findViewById(R.id.request_book_title);
		request_book_author = (EditText)findViewById(R.id.request_book_author);
		request_book_abst = (EditText)findViewById(R.id.request_book_abst);
		request_book_cost = (EditText)findViewById(R.id.request_book_cost);
		request_book_tags1 = (EditText)findViewById(R.id.request_book_tags1);
		
		request = (Button)findViewById(R.id.request_book_requestBtn);
		request.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				title = request_book_title.getText().toString();
				author = request_book_author.getText().toString();
				abst = request_book_abst.getText().toString();
				cost = request_book_cost.getText().toString();
				tags1 = request_book_tags1.getText().toString();
				
				url += "name="+title+"&bookname="+title+"&author="+author+"&brief="+abst+"&score="+cost+"&tag1="+type+"&tag2="+tags1; 
				
				System.out.println("URL:"+url);
				
			}
		});
		
		
		reset = (Button)findViewById(R.id.request_book_resetBtn);
		reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				request_book_title.setText("");
				request_book_author.setText(""); 
				request_book_abst.setText("");
				request_book_cost.setText("");
				request_book_tags1.setText("");
				request_book_tags2.setText("");
			}
		});
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.request_book);
		
		initEditText();
		
		Spinner spinner = (Spinner) findViewById(R.id.Spinner01);
		
		final CharSequence[] seq = {"经管","科技","流行","生活","文化","文学","其他"};

		ArrayAdapter< CharSequence > adapter = new ArrayAdapter< CharSequence >(this, android.R.layout.simple_spinner_item, seq);
	
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			type = (String) seq[arg2];
			System.out.println("chose"+type);
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}

		});
		
		
		
		
		
		
	}
}
