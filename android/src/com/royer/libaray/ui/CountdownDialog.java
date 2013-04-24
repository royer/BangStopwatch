package com.royer.libaray.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.royer.bangstopwatch.R;

public class CountdownDialog extends SherlockDialogFragment {
	
	private int mCountdown ;
	private int mCounting;
	
	private static final String	ARGS_COUNTS = "Counts" ;
	
	public static CountdownDialog NewInstance(int countsecond) {
		
		CountdownDialog d = new CountdownDialog() ;
		
		Bundle args = new Bundle() ;
		args.putInt(ARGS_COUNTS, countsecond);
		d.setArguments(args);
		return d;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mCountdown = getArguments().getInt(ARGS_COUNTS);
		setStyle(DialogFragment.STYLE_NO_FRAME,android.R.style.Theme_Panel);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.countdown, container,false);
		
		return v;
	}
	
	

}
