package com.royer.bangstopwatch.app;

import com.actionbarsherlock.app.SherlockFragment;
import com.royer.bangstopwatch.R ;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StopwatchFragment extends SherlockFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.stopwatch);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.stopwatch, container,false);
	}

}
