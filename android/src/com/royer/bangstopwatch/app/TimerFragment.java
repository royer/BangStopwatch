package com.royer.bangstopwatch.app;

import com.actionbarsherlock.app.SherlockFragment;
import com.royer.bangstopwatch.*;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TimerFragment extends SherlockFragment implements SaveRestoreMyData {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.timer, container, false);
	}

	@Override
	public void onSaveMyData(Bundle onSavedInstance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnRestoreMyData(Bundle onSavedInstance) {
		// TODO Auto-generated method stub
		
	}

}
