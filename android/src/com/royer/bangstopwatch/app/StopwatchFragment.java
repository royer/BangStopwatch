 /**
  * Copyright (C) 2013 Royer Wang
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *  http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package com.royer.bangstopwatch.app;

import java.util.Timer;
import java.util.TimerTask;

import com.actionbarsherlock.app.SherlockFragment;
import com.royer.bangstopwatch.LapManager;
import com.royer.bangstopwatch.R ;
import com.royer.bangstopwatch.Timekeeper;
import com.royer.bangstopwatch.adapters.LapArrayAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class StopwatchFragment extends SherlockFragment {

	public static final String TAG = "StopwatchFragment" ;
	
	
	private int[] res_img_Digits = { 
			R.drawable.digitaldigit0, R.drawable.digitaldigit1, 
			R.drawable.digitaldigit2, R.drawable.digitaldigit3, 
			R.drawable.digitaldigit4, R.drawable.digitaldigit5, 
			R.drawable.digitaldigit6, R.drawable.digitaldigit7, 
			R.drawable.digitaldigit8, R.drawable.digitaldigit9 };

	private int[] res_img_smallDigits = { 
			R.drawable.digitaldigit0_small, R.drawable.digitaldigit1_small, 
			R.drawable.digitaldigit2_small, R.drawable.digitaldigit3_small, 
			R.drawable.digitaldigit4_small, R.drawable.digitaldigit5_small, 
			R.drawable.digitaldigit6_small, R.drawable.digitaldigit7_small, 
			R.drawable.digitaldigit8_small, R.drawable.digitaldigit9_small };
	
	private ImageView[] view_timeDisplay = new ImageView[8];
	
	private Timer _timer = new Timer();
	private TimerTask _task = null;
	
	private Timekeeper _timekeeper ;
	
	private Button btnStart ;
	
	
	private LapArrayAdapter	mLapAdapter ;
	private LapManager		mLapManager = null ;
	private ListView		mLapList ;
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		InitTimeDisplayView();
		
		_timekeeper = new Timekeeper();
		
		mLapList = (ListView)getView().findViewById(R.id.listLap);
		InitLapList();
		
		btnStart = (Button)getView().findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeState();
			}
		});
	}
	
	public void InitLapList() {
		
		mLapManager = new LapManager();
		mLapAdapter = new LapArrayAdapter(getActivity(),mLapManager.get_laps());
		
		mLapList.setAdapter(mLapAdapter);
	}



	protected void changeState() {
		if (_timekeeper.isrunning()) {
			_timekeeper.stop();
			
		} else {
			
			_timekeeper.start();
			
			// Bang Stopwatch does not support restart, so it always start 
			// new stopwatch when click start button
			mLapManager.clear() ;
			mLapManager.setTmStart(_timekeeper.get_startStamp());
		}
		updateState();
	}

	protected void onLap() {
		mLapManager.appandLap(SystemClock.elapsedRealtime());
		mLapAdapter.notifyDataSetChanged();
	}


	private void updateState() {
		if (_timekeeper.isrunning()) {
			btnStart.setText("Stop");
			startTimerTask();
		} else {
			btnStart.setText(R.string.start);
			stopTimerTask();
		}
		
	}



	private void InitTimeDisplayView() {
		view_timeDisplay[0] = (ImageView)getView().findViewById(R.id.tenHourImg); 
		view_timeDisplay[1] = (ImageView)getView().findViewById(R.id.HourImg);
		view_timeDisplay[2] = (ImageView)getView().findViewById(R.id.tenMinuteImg);
		view_timeDisplay[3] = (ImageView)getView().findViewById(R.id.minuteImg);
		view_timeDisplay[4] = (ImageView)getView().findViewById(R.id.tenSecondImg);
		view_timeDisplay[5] = (ImageView)getView().findViewById(R.id.secondImg);
		view_timeDisplay[6] = (ImageView)getView().findViewById(R.id.tenhundredthImg);
		view_timeDisplay[7] = (ImageView)getView().findViewById(R.id.hundredthImg);
		
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.stopwatch, container,false);
	}

	private void startTimerTask() {
		if (_task == null) {
			_task = new TimerTask() {
				@Override
				public void run() {
					updateTime();
				}
			};
			_timer.scheduleAtFixedRate(_task, 0, 100);
		}
	}
	
	public void updateTime() {
		getActivity().runOnUiThread(new Runnable(){

			@Override
			public void run() {
				printTime();
				
			}
		}) ;
	}
	
	public void printTime() {
		long time = _timekeeper.getElapsedTime() ;
		//Hours
		view_timeDisplay[0].setImageResource(res_img_Digits[(int)(time/36000000)]);
		view_timeDisplay[1].setImageResource(res_img_Digits[(int)((time / 3600000) % 10)]);
		
		//Mintues
		view_timeDisplay[2].setImageResource(res_img_Digits[(int)(time / 600000 % 6)]);
		view_timeDisplay[3].setImageResource(res_img_Digits[(int)(time / 60000 % 10)]);
		
		//Seconds
		view_timeDisplay[4].setImageResource(res_img_Digits[(int)(time / 10000 % 6)]);
		view_timeDisplay[5].setImageResource(res_img_Digits[(int)(time / 1000 % 10)]);
		
		//milliseconds
		view_timeDisplay[6].setImageResource(res_img_smallDigits[(int)(time / 100 % 10)]);
		view_timeDisplay[7].setImageResource(res_img_smallDigits[(int)(time /10 % 10 )]);
	}



	private void stopTimerTask() {
		if (_task != null) {
			_task.cancel();
			_task = null ;
		}
	}
}
