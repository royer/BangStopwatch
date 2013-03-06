package com.royer.bangstopwatch.app;

//import android.R;
import com.royer.bangstopwatch.R ;
import android.app.Activity;
import android.os.Bundle;

public class StopwatchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.stopwatch);
	}

}
