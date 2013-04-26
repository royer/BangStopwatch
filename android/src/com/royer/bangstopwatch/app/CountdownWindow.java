package com.royer.bangstopwatch.app;

import java.util.Timer;
import java.util.TimerTask;

import com.royer.bangstopwatch.R ;

//import android.R;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;




public class CountdownWindow {
	
	public interface CountdownListener {
		public void OnCountdownFinished();
	}
	
	private static final String STATE_CDW_RUNNING = "royer.bangstopwatch.cdw.running" ;
	private static final String STATE_CDW_STARTSTAMP = "royer.bangstopwatch.cdw.startstamp";
	private static final String STATE_CDW_COUNTDOWNSEC = "royer.bangstopwatch.cdw.second";
	
	boolean mRunning ;
	long	mStartStamp ;
	int		mCountdownSec;
	
	int		mLeftSecond ;
	
	PopupWindow	mPopupWindow ;
	TextView	mTxtView;
	
	Timer		mTimer;
	TimerTask	mTimerTask;
	
	MediaPlayer	mediaplayer = null;
	
	final Activity			mActivity ;
	final CountdownListener	mListener ;
	
	public CountdownWindow(Activity activity,CountdownListener listener) {
		
		mActivity = activity;
		
		mListener = listener;
		
		mRunning = false;
		mStartStamp = 0;
		mCountdownSec = 0;
		mLeftSecond = 0;
		mPopupWindow = null ;
		
		mTimer = new Timer();
		mTimerTask = null ;
		
		buildPopupWindow();
	}
	
	private void buildPopupWindow() {
		LinearLayout layout = new LinearLayout(mActivity) ;
		layout.setOrientation(LinearLayout.VERTICAL);
		mTxtView = new TextView(mActivity) ;
		
		mTxtView.setGravity(Gravity.CENTER);
		mTxtView.setTextSize(40);
		//mTxtView.setText("5");
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		layout.addView(mTxtView, params);
		
		mPopupWindow = new PopupWindow(layout,100,100,false);
	}
	
	public boolean isRunning() {
		return mRunning ;
	}
	public void updateText() {
		mTxtView.setText(Integer.toString(mLeftSecond));
	}
	
	public void Start(View parent, int countdownsecond) {
		
		mCountdownSec = countdownsecond ;
		mStartStamp = SystemClock.elapsedRealtime() ;
		
		mLeftSecond = mCountdownSec + 1;
		mRunning = true;
		
		//mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0) ;
		
		//startTimerTask();
		restart(parent);
		
	}

	private void startTimerTask() {
		if (mTimerTask == null) {
			mTimerTask = new TimerTask() {

				@Override
				public void run() {
					long currenttime = SystemClock.elapsedRealtime();
					long lms = mStartStamp + (mCountdownSec) * 1000 - currenttime;
					long lf = lms / 1000 + (lms % 1000 > 10 ? 1 : 0) ;
					if (lf != mLeftSecond) {
						mLeftSecond = (int)lf;
						
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								updateText();
								
							}
							
						});
						if ((mStartStamp + (mCountdownSec) * 1000 - currenttime) <= 10) {
							
							PlaySound(R.raw.countdownend) ;
							
							mActivity.runOnUiThread(new Runnable(){

								@Override
								public void run() {
									
									CountdownDone();
								}
								
							});
							return;
						} else {
							PlaySound(R.raw.countdowning);
						}
					}
				}
				
			} ;
			mTimer.scheduleAtFixedRate(mTimerTask, 0, 10);
		}
	}
	
	public void restart(View parent) {
		long currenttime = SystemClock.elapsedRealtime();
		long lms = mStartStamp + mCountdownSec * 1000 - currenttime ;
		mLeftSecond = (int)(lms / 1000 + ((lms % 1000) > 10 ? 1 : 0)) ;
		
		mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0) ;
		updateText();
		
		startTimerTask();
	}
	
	public void cancel() {
		if (mRunning) {
			if (mTimerTask != null) {
				mTimerTask.cancel();
				mTimerTask = null; 
			}
			
			mPopupWindow.dismiss();
		}
		
		if (mediaplayer != null) {
			mediaplayer.release();
			mediaplayer = null;
		}
	}
	
	private void CountdownDone() {
		
		
		mTimerTask.cancel();
		mTimerTask = null ;
		mRunning = false ;
		
		mPopupWindow.dismiss();
		mListener.OnCountdownFinished() ;
	}
	
	private void PlaySound(int resid) {
		if (mediaplayer != null) {
			mediaplayer.release();
			mediaplayer = null ;
		}
		
		mediaplayer = MediaPlayer.create(mActivity, resid);
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

	
	public void writeToBundle(Bundle dest) {
		dest.putBoolean(STATE_CDW_RUNNING,mRunning);
		dest.putLong(STATE_CDW_STARTSTAMP, mStartStamp);
		dest.putInt(STATE_CDW_COUNTDOWNSEC, mCountdownSec);
	}
	
	public void readFromBundle(Bundle in) {
		mRunning = in.getBoolean(STATE_CDW_RUNNING);
		mStartStamp = in.getLong(STATE_CDW_STARTSTAMP);
		mCountdownSec = in.getInt(STATE_CDW_COUNTDOWNSEC);
	}
}
