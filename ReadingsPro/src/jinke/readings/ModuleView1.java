package jinke.readings;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ModuleView1 extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		tv.setText("ModuleView1");
		this.setContentView(tv);
	}

}
