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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.actionbarsherlock.app.SherlockFragment;
import com.royer.bangstopwatch.Bang;
import com.royer.bangstopwatch.MainActivity;
import com.royer.bangstopwatch.LapManager;
import com.royer.bangstopwatch.R ;
import com.royer.bangstopwatch.RecordService;
import com.royer.bangstopwatch.RecordService.LocalBinder;
import com.royer.bangstopwatch.Timekeeper;
import com.royer.bangstopwatch.adapters.LapArrayAdapter;
import com.royer.libaray.ui.CountdownWindow;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class StopwatchFragment extends SherlockFragment implements SaveRestoreMyData ,
CountdownWindow.CountdownListener,Bang
{

	public static final String TAG = "StopwatchFragment" ;
	
	private static final String STATE_TIMEKEEPER = 
			"royer.bangstopwatch.stopwatch.timekeeper" ;
	private static final String STATE_LAPS = 
			"royer.bangstopwatch.stopwatch.laps";
	private static final String STATE_COUNTDOWNWND = 
			"royer.bangstopwatch.stopwatch.countdownwnd" ;
	private static final String STATE_STATE = 
			"royer.bangstopwatch.stopwatch.state";
	private static final String STATE_BOUNDING = 
			"royer.bangstopwatch.stopwatch.bounding";
	
	
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
	
	private int state ;
	private static final int STATE_NONE = 0;
	private static final int STATE_COUNTDOWN  = 1;
	private static final int STATE_RUNNING = 2;

	
	
	private LapArrayAdapter	mLapAdapter ;
	private LapManager		mLapManager = null ;
	private ListView		mLapList ;
	
	CountdownWindow	wndCountdown ;
	
	RecordService	mService ;
	boolean			mBound = false;
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Log.d(TAG, "Enter onActivityCreated...") ;
		
		InitTimeDisplayView();
		
		mLapList = (ListView)getView().findViewById(R.id.listLap);
		
		wndCountdown = new CountdownWindow(getActivity(),this);
		
		btnStart = (Button)getView().findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (state == STATE_NONE) {
					
					AudioManager audiomanager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE) ;
					Log.d(TAG,"AudioMode = " + audiomanager.getMode());
					if (audiomanager.getMode() != AudioManager.MODE_NORMAL) {
						Context context = getActivity().getApplicationContext();
						
						Toast toast = Toast.makeText(context, R.string.strInCalling, 5);
						toast.show();
						return ;
					}

					state = STATE_COUNTDOWN;
					wndCountdown.Start(getActivity().findViewById(R.id.main), 5);
					
					
				} else {
					changeState();
					state = STATE_NONE;
					
					// unBind Recordservice
					if (mBound) {
						mService.stopRecord();
						mService.unsetBang();
						getActivity().unbindService(mConnection);
						getActivity().stopService(new Intent(getActivity(), RecordService.class));
						mBound = false;
					}
				}
				((MainActivity)getActivity()).EnableTab(1, state == STATE_NONE);
			}
		});
		
		if (savedInstanceState != null) {
			Log.d(TAG, "savedInstanceState " + savedInstanceState.toString());
			_timekeeper = savedInstanceState.getParcelable(STATE_TIMEKEEPER);
			mLapManager = savedInstanceState.getParcelable(STATE_LAPS);
			state = savedInstanceState.getInt(STATE_STATE);
			mBound = savedInstanceState.getBoolean(STATE_BOUNDING);
			((MainActivity)getActivity()).EnableTab(1, state == STATE_NONE);
			
			wndCountdown.readFromBundle(savedInstanceState) ;
			
			
			if (wndCountdown.isRunning()) {
				// popupwindow cann't show before Activity window has been displayed.
				// must use post method until all necessary start up life cycle methods get completed.
				getActivity().findViewById(R.id.main).post(new Runnable() {

					@Override
					public void run() {
						
						wndCountdown.restart(getActivity().findViewById(R.id.main));
						
					}
					
				});
			}
			

		} else {
			Log.d(TAG,"savedInstanceState == NULL") ;
			if (_timekeeper == null)
				_timekeeper = new Timekeeper();
			if (mLapManager == null)
				mLapManager = new LapManager();
		}
		InitLapList();
		
		printTime();
		updateState();
		
		
		Log.d(TAG, "Leave OnActivityCreated...");
	}
	
	public void InitLapList() {
		
		mLapAdapter = new LapArrayAdapter(getActivity(),mLapManager.get_laps());
		
		mLapList.setAdapter(mLapAdapter);
	}



	@Override
	public void onStart() {
		super.onStart();
		
		Log.d(TAG, "onStart()...") ;
		if (_timekeeper.isrunning()) {
			
			/**
			 * because in onStop() we stop the time task, so we need restart
			 * the time task
			 */
			startTimerTask();
		}
		
		if (state == this.STATE_RUNNING) {
			// it must bound the service
			Intent intent = new Intent(getActivity(), RecordService.class);
			getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}



	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "onStop()...") ;
		if (_timekeeper.isrunning()) {
			stopTimerTask();
		}
		
		if (state == this.STATE_RUNNING) {
			mService.unsetBang();
			getActivity().unbindService(mConnection);
		}
	}



	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy()...");
		stopTimerTask();
		wndCountdown.cancel();
		super.onDestroy();
	}





	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "onSaveInstanceState()") ;
		outState.putParcelable(STATE_TIMEKEEPER, _timekeeper);
		outState.putParcelable(STATE_LAPS, mLapManager);
		outState.putInt(STATE_STATE, state);
		outState.putBoolean(STATE_BOUNDING, mBound);
		wndCountdown.writeToBundle(outState);
		
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
			btnStart.setText(R.string.stop);
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
	
	private void stopTimerTask() {
		if (_task != null) {
			_task.cancel();
			_task = null ;
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
		view_timeDisplay[0].setImageResource(
				res_img_Digits[(int)(time/36000000)]);
		view_timeDisplay[1].setImageResource(
				res_img_Digits[(int)((time / 3600000) % 10)]);
		
		//Mintues
		view_timeDisplay[2].setImageResource(
				res_img_Digits[(int)(time / 600000 % 6)]);
		view_timeDisplay[3].setImageResource(
				res_img_Digits[(int)(time / 60000 % 10)]);
		
		//Seconds
		view_timeDisplay[4].setImageResource(
				res_img_Digits[(int)(time / 10000 % 6)]);
		view_timeDisplay[5].setImageResource(
				res_img_Digits[(int)(time / 1000 % 10)]);
		
		//milliseconds
		view_timeDisplay[6].setImageResource(
				res_img_smallDigits[(int)(time / 100 % 10)]);
		view_timeDisplay[7].setImageResource(
				res_img_smallDigits[(int)(time /10 % 10 )]);
	}

	@Override
	public void onSaveMyData(Bundle onSavedInstance) {
		
		Log.d(TAG, "onSaveMyData") ;
		onSavedInstance.putParcelable(STATE_TIMEKEEPER, _timekeeper);
		//onSavedInstance.putParcelable(STATE_COUNTDOWNWND, wndCountdown);
		wndCountdown.writeToBundle(onSavedInstance);
	}

	@Override
	public void OnRestoreMyData(Bundle onSavedInstance) {
		
		Log.d(TAG, "onRestoreMyData") ;
		_timekeeper = onSavedInstance.getParcelable(STATE_TIMEKEEPER);
		wndCountdown.readFromBundle(onSavedInstance);
	}

	@Override
	public void OnCountdownFinished() {
		state = STATE_RUNNING;
		changeState();
		
		// bind RecordService
		Intent intent = new Intent(getActivity(), RecordService.class);
		getActivity().startService(intent);
		getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	public void onAppWillQuit() {
		if (state == STATE_RUNNING) {
			Intent intent = new Intent(getActivity(),RecordService.class);
			mService.unsetBang();
			getActivity().unbindService(mConnection);
			getActivity().stopService(intent);
			state = STATE_NONE;
		}
	}
	
	public void onBang() {
		ArrayList<Long> ary = mService.getLaps(mLapManager.get_laps().size());
		synchronized(this) {
			for (Long l : ary) {
				mLapManager.appandLap(l);
			}
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mLapAdapter.notifyDataSetChanged();
				}
				
			});
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder)service;
			mService = binder.getService();
			mBound = true;
			
			if (state == STATE_RUNNING) {
				onBang();
			}
			mService.setBang(StopwatchFragment.this) ;
			mService.startRecord();
			Log.d(TAG,"onServiceConnected");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBound = false;
			
			Log.d(TAG,"onServiceDisconnected");
			
		}
		
	} ;

}
