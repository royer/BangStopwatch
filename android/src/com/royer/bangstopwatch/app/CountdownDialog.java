package com.royer.bangstopwatch.app;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.royer.bangstopwatch.R;

public class CountdownDialog extends SherlockDialogFragment {
	
	private static final String TAG = "CountdownDialog" ;
	
	private int mCountdown ;
	private int mCounting;
	private long lStartTime ;
	
	private static final String	ARGS_COUNTS = "Counts" ;
	private static final String ARGS_STARTTIME = "StartTime";
	private static final String ARGS_TAGFRAGMENT = "TagFragment";
	
	private static final String STATE_COUNTING = 
			"royer.bangstopwatch.countdowndialog.counting";
	
	Timer	timer ;
	Handler mHanderTimer ;
	
	MediaPlayer	mediaplayer ;
	
	public interface NotifyCountdownListener {
		public void onCountdownDismiss(boolean done) ; 
	}
	
	NotifyCountdownListener	mListener ;
	
	public static CountdownDialog NewInstance(int countsecond, String tagOfListenerFragment) {
		
		CountdownDialog d = new CountdownDialog() ;
		
		Bundle args = new Bundle() ;
		args.putInt(ARGS_COUNTS, countsecond);
		args.putLong(ARGS_STARTTIME, SystemClock.elapsedRealtime()) ;
		args.putString(ARGS_TAGFRAGMENT, tagOfListenerFragment) ;
		d.setArguments(args);
		return d;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		
		String tag = getArguments().getString(ARGS_TAGFRAGMENT);
		Fragment f = getFragmentManager().findFragmentByTag(tag);
		try {
			mListener = (NotifyCountdownListener)f ;
			
		} catch (ClassCastException e) {
			throw new ClassCastException(f.toString() + "must implement NotifyCountdownListener");
		}
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(TAG,"onCreate");
		
		mCountdown = getArguments().getInt(ARGS_COUNTS);
		lStartTime = getArguments().getLong(ARGS_STARTTIME);
		
		setStyle(DialogFragment.STYLE_NO_FRAME,android.R.style.Theme_Panel);
		
		//setCancelable(false);
		
		if (savedInstanceState == null) {
			mCounting = mCountdown ;
		} else {
			mCounting = savedInstanceState.getInt(STATE_COUNTING);
		}
		
		
		mHanderTimer = new Handler(Looper.getMainLooper()) {
			
			@Override
			public void handleMessage(Message msg) {
				onTimerMessage(msg);
			}
		} ;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView") ;
		View v = inflater.inflate(R.layout.countdown, container,false);
		
		
		return v;
	}
	
	


	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		
		Log.d(TAG,"OnViewStateRestored") ;
		
		TextView v = (TextView)getView().findViewById(R.id.txtCountdown) ;
		v.setText(String.valueOf(mCounting));
		
		timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				mHanderTimer.obtainMessage(1).sendToTarget();
			}
			
		}, 0, 50);
		
	}
	
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mHanderTimer.removeMessages(1, null);
		
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null ;
			
		}
	}
	
	

	@Override
	public void onSaveInstanceState(Bundle arg0) {
		super.onSaveInstanceState(arg0);
		
		arg0.putInt(STATE_COUNTING, this.mCounting);
	}
	
	

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		
		mListener.onCountdownDismiss(false) ;
	}

	private void onTimerMessage(Message msg) {
		long currenttm = SystemClock.elapsedRealtime() ;
		if (currenttm - lStartTime >= this.mCountdown * 1000) {
			if (timer != null) {
				timer.cancel();
				timer.purge() ;
				timer = null ;
				
				mHanderTimer.removeMessages(1, null);

				PlayerSound(R.raw.countdownend) ;
				dismiss() ;
				this.mListener.onCountdownDismiss(true) ;
			}
			
		} else {
			int cc = (int)(((float)(mCountdown * 1000 - currenttm + lStartTime) / 1000.0f) + 0.5f) ;
			
			if (cc != mCounting) {
				TextView tv = (TextView)getView().findViewById(R.id.txtCountdown) ;
				tv.setText(String.valueOf(cc)) ;
				
				mCounting = cc ;
				
				PlayerSound(R.raw.countdowning) ;
			}
		}
	}

	private void PlayerSound(int resourceid) {
		
		if (mediaplayer != null) {
			mediaplayer.release();
			mediaplayer = null ;
		}
		
		mediaplayer = MediaPlayer.create(this.getActivity(), resourceid);
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
}
