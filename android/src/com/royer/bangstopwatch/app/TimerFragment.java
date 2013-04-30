package com.royer.bangstopwatch.app;

import java.util.Timer;
import java.util.TimerTask;

import com.actionbarsherlock.app.SherlockFragment;
import com.royer.bangstopwatch.*;

import android.support.v4.app.DialogFragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

//TODO change to use Handler to control timer thread with UI thread, not use post ;

public class TimerFragment extends SherlockFragment implements 
CountdownDialog.NotifyCountdownListener {
	
	public static final String TAG = "TimerFragment" ;
	
	public static final String STATE_HAVINGCOUNTDOWN = 
			"royer.bangstopwatch.timer.havingcountdown";
	public static final String STATE_STATUS = 
			"royer.bangstopwatch.timer.status" ;
	public static final String STATE_STARTTIME = 
			"royer.bangstopwatch.timer.starttime";
	
	public static final String STATE_TBPOSITION = 
			"royer.bangstopwatch.timer.tbposition";
	public static final String STATE_SETTINGVAL = 
			"royer.bangstopwatch.timer.settingval";
	public static final String STATE_CURRENTVAL = 
			"royer.bangstopwatch.timer.currentval";
	
	public enum Status {
		NORMAL, SETTING, STARTING, RUNNING
	}
	
	
	/**
	 * for record which part of timeboard is in seeting status
	 * NONE:   Time Board not is setting status
	 * @author royer
	 *
	 */
	public enum TBPosition {
		NONE,SECONDS, MINUTES, HOURS
	}
	
	

	
	Status		mStatus = Status.NORMAL;
	
	TimeBoard 	mTimeBoard = new TimeBoard();
	
	long		lStartTime = 0;
	Timer		countdownTimer = null ;
	
	
	
	MediaPlayer	mediaplayer = null ;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(TAG,"OnCreate") ;
	}
	
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setInputButtonsListener();
		
		setStartButtonListener();
		
		setTimeBoardImgListener();
		
		
		if (savedInstanceState != null) {
			mStatus = Status.values()[savedInstanceState.getInt(STATE_STATUS)];
			lStartTime = savedInstanceState.getLong(STATE_STARTTIME);
			mTimeBoard.restoreState(savedInstanceState);
			
			
			if (mStatus == Status.RUNNING) {
				startOrResumeCountdownTimer() ;
			}

		}
		
		updateStartButtonStatus();
		mTimeBoard.updateBoard();
		
		
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.timer, container, false);
	}
	
	

	@Override
	public void onStart() {
		super.onStart();
		
		if (mStatus == Status.SETTING) {
			mTimeBoard.startBlink() ;
		}
	}



	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putInt(STATE_STATUS, mStatus.ordinal()) ;
		outState.putLong(STATE_STARTTIME, lStartTime);
		mTimeBoard.saveState(outState);
	
		
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		
	}



	
	private void setInputButtonsListener() {

		View.OnClickListener inputButtonsListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onInputButtonsClick(v.getId()) ;
			}
		};
		
		Button button = (Button)getView().findViewById(R.id.btn0) ;
		button.setOnClickListener(inputButtonsListener) ;
		
		button = (Button)getView().findViewById(R.id.btn1);
		button.setOnClickListener(inputButtonsListener);
		
		button = (Button)getView().findViewById(R.id.btn2);
		button.setOnClickListener(inputButtonsListener);
		
		button = (Button)getView().findViewById(R.id.btn3);
		button.setOnClickListener(inputButtonsListener);
		
		button = (Button)getView().findViewById(R.id.btn4);
		button.setOnClickListener(inputButtonsListener);
		
		button = (Button)getView().findViewById(R.id.btn5);
		button.setOnClickListener(inputButtonsListener);
		
		button = (Button)getView().findViewById(R.id.btn6);
		button.setOnClickListener(inputButtonsListener);
		
		button = (Button)getView().findViewById(R.id.btn7);
		button.setOnClickListener(inputButtonsListener);
		
		button = (Button)getView().findViewById(R.id.btn8);
		button.setOnClickListener(inputButtonsListener);
		
		button = (Button)getView().findViewById(R.id.btn9);
		button.setOnClickListener(inputButtonsListener);
		
		ImageButton imgbutton = (ImageButton)getView().findViewById(R.id.btnDelete);
		imgbutton.setOnClickListener(inputButtonsListener);
	}
	
	
	
	private void setStartButtonListener() {
		
		Button btn = (Button)getView().findViewById(R.id.btnStart);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onStartButtonClick();
				
			}
		});
	}
	
	private void setTimeBoardImgListener() {
		
		mTimeBoard.setImageView() ;
		
	}
	
	public void setStatus(Status s) {
		mStatus = s ;
	}
	
	

	private void onInputButtonsClick(int buttonid) {
		
		if (mStatus == Status.STARTING || mStatus == Status.RUNNING) 
			return ;
		
		if (mTimeBoard.getCurrentPosition() == TBPosition.NONE) {
			return ;
		}
		int number = -1;
		switch (buttonid) {
		case R.id.btn0:
			number = 0;
			break;
		case R.id.btn1:
			number = 1;
			break;
		case R.id.btn2:
			number = 2;
			break;
		case R.id.btn3:
			number = 3;
			break;
		case R.id.btn4:
			number = 4;
			break;
		case R.id.btn5:
			number = 5;
			break;
		case R.id.btn6:
			number = 6;
			break;
		case R.id.btn7:
			number = 7;
			break;
		case R.id.btn8:
			number = 8;
			break;
		case R.id.btn9:
			number = 9;
			break;
		case R.id.btnDelete:
			mTimeBoard.onDeleteInput();
			return ;
		}
		if (number != -1) 
			mTimeBoard.onDigitInput(number);
		
		return ;
	}
	
	
	private void onStartButtonClick() {
		
		if ((mStatus == Status.NORMAL || mStatus == Status.SETTING) && 
				mTimeBoard.getSettingValue() > 0) {
			prepareStart();
		} else if (mStatus == Status.RUNNING) {
			toCalcelPractice();
		}
		
		updateStartButtonStatus();
	}
	
	private void prepareStart() {
		
		setStatus(Status.STARTING) ;
		
		mTimeBoard.stopBlink();
		
		
		DialogFragment newFragment = CountdownDialog.NewInstance(5,this.getTag());
		newFragment.show(getFragmentManager(), "countdownDialog");

	}
	
	private void toStartPractice() {
		
		setStatus(Status.RUNNING);
		
		lStartTime = SystemClock.elapsedRealtime();
		
		startOrResumeCountdownTimer();
		
	}
	
	private void startOrResumeCountdownTimer() {
		if (countdownTimer == null) {
			countdownTimer = new Timer();
			
			mTimeBoard.startCountdown() ;
			
			TimerTask	tt = new TimerTask(){

				@Override
				public void run() {
					
					long elaspedtime = SystemClock.elapsedRealtime() - lStartTime ;
					if (elaspedtime >= ((long)mTimeBoard.getSettingValue() * 1000) ) {
						getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run() {
								onTimerFinished();
							}
							
						});
					} else {
						int old = mTimeBoard.getCountdownVal() ;
						int left = (int)((float)(mTimeBoard.getSettingValue() * 1000 - elaspedtime ) / 1000.0 + 0.5);
						if (left != old) {
							mTimeBoard.setCountdownVal(left) ;
							getActivity().runOnUiThread(new Runnable(){

								@Override
								public void run() {
									mTimeBoard.updateBoard() ;
								}
								
							});
						}
					}
				}
				
			} ;
			
			countdownTimer.scheduleAtFixedRate(tt, 0, 100);
		}
	}
	
	private void toCalcelPractice() {
		
		setStatus(Status.NORMAL);
	}

	@Override
	public void onCountdownDismiss(boolean done) {
		if (done == true) {
			toStartPractice();
			updateStartButtonStatus() ;
		} else {
			mStatus = Status.NORMAL ;
		}
	}
	
	public void onTimerFinished() {

		setStatus(Status.NORMAL) ;
		
		if (countdownTimer != null) {
			countdownTimer.cancel() ;
			countdownTimer.purge() ;
			countdownTimer = null ;
		}
		
		if (mediaplayer == null) {
			
			mediaplayer = MediaPlayer.create(getActivity(), R.raw.countdownend);
			mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
					if (mp == mediaplayer) {
						mediaplayer = null ;
					}
				}
			});
			
			mediaplayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					mp.reset();
					return false;
				}
			});
			
			mediaplayer.start();
			
		}
		
		updateStartButtonStatus() ;
		
		mTimeBoard.updateBoard() ;
	}
	
	private void updateStartButtonStatus() {
		Button	btn = (Button)getView().findViewById(R.id.btnStart);
		
		if (mStatus == Status.NORMAL || mStatus == Status.SETTING || mStatus == Status.STARTING) {
			btn.setText(R.string.start);
		} else {
			btn.setText(R.string.stop);
		}
	}
	
	

	/**
	 * Time Board current information. 
	 * record which part is in setting
	 * the setting value
	 * @author royer
	 *
	 */
	public class TimeBoard implements View.OnClickListener {

		TBPosition	whichinsetting ;
		
		/**
		 *  the setting count down time (second)
		 */
		private int			mSettingVal;
		
		/**
		 * current count down (second)
		 */
		private int			mCountdownVal ;
		
		private int[] res_img_Digits = { 
				R.drawable.digitaldigit0, R.drawable.digitaldigit1, 
				R.drawable.digitaldigit2, R.drawable.digitaldigit3, 
				R.drawable.digitaldigit4, R.drawable.digitaldigit5, 
				R.drawable.digitaldigit6, R.drawable.digitaldigit7, 
				R.drawable.digitaldigit8, R.drawable.digitaldigit9 };
		
		private ImageView[]	iv = new ImageView[6];
		
		
		private Timer	  timerForBlink = null;
		
		public TimeBoard() {
			whichinsetting = TBPosition.NONE;
			mSettingVal = 0;
		}
		
		public void setImageView() {
			iv[0] = (ImageView)getView().findViewById(R.id.tenHourImg);
			iv[1] = (ImageView)getView().findViewById(R.id.HourImg);
			iv[2] = (ImageView)getView().findViewById(R.id.tenMinuteImg);
			iv[3] = (ImageView)getView().findViewById(R.id.minuteImg);
			iv[4] = (ImageView)getView().findViewById(R.id.tenSecondImg);
			iv[5] = (ImageView)getView().findViewById(R.id.secondImg);
			
			for (ImageView v : iv) {
				v.setOnClickListener(this);
			}
		}
		
		public void saveState(Bundle outState) {
			outState.putInt(STATE_SETTINGVAL,mSettingVal);
			outState.putInt(STATE_CURRENTVAL, mCountdownVal);
			outState.putInt(STATE_TBPOSITION, whichinsetting.ordinal());
		}
		
		public void restoreState(Bundle savedState) {
			mSettingVal = savedState.getInt(STATE_SETTINGVAL);
			mCountdownVal = savedState.getInt(STATE_CURRENTVAL);
			whichinsetting = TBPosition.values()[savedState.getInt(STATE_TBPOSITION)] ;
		}
		
		public int getSettingValue() {
			return mSettingVal ;
		}
		
		public int getSetHours() {
			return getHour(mSettingVal) ;
		}
		
		public int getSetMinutes() {
			return getMinute( mSettingVal );
		}
		
		public int getSetSeconds() {
			return getSecond( mSettingVal );
		}
		
		public int getCountdownVal() {
			return mCountdownVal ;
		}
		
		public void setCountdownVal(int val) {
			mCountdownVal = val ;
		}
		
		public void startCountdown() {
			mCountdownVal = mSettingVal ;
		}
		
		public TBPosition getCurrentPosition() {
			return whichinsetting ;
		}
		
		public void setCurrentPosition(TBPosition p) {
			whichinsetting = p ;
		}
		
		public void updateBoard() {
			
			int val = 0 ; // seconds
			if (mStatus != Status.RUNNING) {
				// update TimeBoard to display the setting value
				val = mSettingVal ;
			} else {
				val = mCountdownVal ;
			}
			iv[0].setImageResource(res_img_Digits[val/36000]);
			iv[1].setImageResource(res_img_Digits[(val/3600) % 10]);
			iv[2].setImageResource(res_img_Digits[(val % 3600)/600]) ;
			iv[3].setImageResource(res_img_Digits[((val % 3600)/60) % 10 ]);
			iv[4].setImageResource(res_img_Digits[(val % 60) / 10]);
			iv[5].setImageResource(res_img_Digits[(val % 60) % 10]);
		}
		
		public void onDigitInput(int number) {
			
			if (getCurrentPosition() == TBPosition.NONE)
				return ;
			
			int hours = getSetHours();
			int mins = getSetMinutes();
			int sec = getSetSeconds();
			
			switch(whichinsetting) {
			case HOURS:
				hours = (hours % 10) * 10 + number ;
				break;
			case MINUTES:
				mins = (mins % 10) * 10 + number ;
				if (mins > 60 )
					mins = number ;
				break;
			case SECONDS:
				sec = (sec % 10) * 10 + number ;
				if (sec > 60)
					sec = number ;
				break;
			}
			
			mSettingVal = hours * 3600 + mins * 60 + sec ;
			
			updateBoard();
		}
		
		public void onDeleteInput() {
			
			if (getCurrentPosition() == TBPosition.NONE) 
				return ;
			
			int hours = getSetHours();
			int mins = getSetMinutes();
			int sec = getSetSeconds();
			
			switch(getCurrentPosition()) {
			case HOURS:
				hours = 0;
				break;
			case MINUTES:
				mins = 0;
				break;
			case SECONDS:
				sec = 0;
				break;
			}
			
			mSettingVal = hours * 3600 + mins * 60 + sec ;
			updateBoard();
		}

		@Override
		public void onClick(View v) {
			if (mStatus != Status.NORMAL && mStatus != Status.SETTING) 
				return ;
			
			int imgviewid = v.getId();
			
			TBPosition	old = getCurrentPosition();
			
			switch(imgviewid) {
			case R.id.tenHourImg:
			case R.id.HourImg:
				setCurrentPosition(TBPosition.HOURS);
				break;
			case R.id.tenMinuteImg:
			case R.id.minuteImg:
				setCurrentPosition(TBPosition.MINUTES);
				break;
			case R.id.tenSecondImg:
			case R.id.secondImg:
				setCurrentPosition(TBPosition.SECONDS);
				break;
			}
			
			if (old == TBPosition.NONE && getCurrentPosition() != old) {
				setStatus(Status.SETTING);
				startBlink();
			} else if (old != TBPosition.NONE && getCurrentPosition() != old) {
				// just change between hours, minutes and second ;
				// visable the old imageView that may be in invisible status
				switch(old) {
				case HOURS:
					iv[0].setVisibility(View.VISIBLE);
					iv[1].setVisibility(View.VISIBLE);
					break;
				case MINUTES:
					iv[2].setVisibility(View.VISIBLE);
					iv[3].setVisibility(View.VISIBLE);
					break;
				case SECONDS:
					iv[4].setVisibility(View.VISIBLE);
					iv[5].setVisibility(View.VISIBLE);
					break;
				}
				
				Blink(false);
			}
			return ;
		}
		
		private void Blink(boolean bVisiable) {
			if (mStatus == Status.SETTING && whichinsetting != TBPosition.NONE) {
				switch(whichinsetting) {
				case HOURS:
					iv[0].setVisibility(bVisiable?View.VISIBLE:View.INVISIBLE);
					iv[1].setVisibility(bVisiable?View.VISIBLE:View.INVISIBLE);
					break;
				case MINUTES:
					iv[2].setVisibility(bVisiable?View.VISIBLE:View.INVISIBLE);
					iv[3].setVisibility(bVisiable?View.VISIBLE:View.INVISIBLE);
					break;
				case SECONDS:
					iv[4].setVisibility(bVisiable?View.VISIBLE:View.INVISIBLE);
					iv[5].setVisibility(bVisiable?View.VISIBLE:View.INVISIBLE);
					break;
				}
			}
		}
		
		public void startBlink() {
			
			if (timerForBlink != null)
				return ;
			
			Blink(false);
			timerForBlink = new Timer();
			
			TimerTask	ttforDisappear = new TimerTask() {

				@Override
				public void run() {
					
					getView().post(new Runnable(){

						@Override
						public void run() {
							Blink(false);
							
						}
						
					});
				}
				
			};
			timerForBlink.scheduleAtFixedRate(ttforDisappear, 0, 1000);
			
			TimerTask	ttforAppear = new TimerTask() {

				@Override
				public void run() {
					
					getView().post(new Runnable(){

						@Override
						public void run() {
							Blink(true);
						}
						
					});
				}
				
			};
			timerForBlink.scheduleAtFixedRate(ttforAppear, 300, 1000);
		}
	
		public void stopBlink() {
			if (timerForBlink != null) {
				timerForBlink.cancel() ;
				timerForBlink = null ;
			}
			
			switch(whichinsetting) {
			case HOURS:
				iv[0].setVisibility(View.VISIBLE);
				iv[1].setVisibility(View.VISIBLE);
				break;
			case MINUTES:
				iv[2].setVisibility(View.VISIBLE);
				iv[3].setVisibility(View.VISIBLE);
				break;
			case SECONDS:
				iv[4].setVisibility(View.VISIBLE);
				iv[5].setVisibility(View.VISIBLE);
				break;
			}
			
			setCurrentPosition(TBPosition.NONE);
		}
	}
	
	static int getHour(int seconds) {
		return seconds / 3600 ;
	}
	static int getMinute(int seconds) {
		return ( seconds % 3600 ) / 60 ;
	}
	static int getSecond(int seconds) {
		return ( seconds % 60 );
	}



	
	
}
