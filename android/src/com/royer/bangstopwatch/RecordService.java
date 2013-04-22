package com.royer.bangstopwatch;


import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class RecordService extends Service {
	
	private static final String TAG="RecordService";
	
	private static final int RECORDER_BPP = 16;
	private static final int RECORDER_SAMPLERATE = 44100 ;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	
	/**
	 * if peaks continue time great this value (ms), it don't conside a counting sound
	*/
	private static final int MAX_PEAKS_TIME = 490 ;
	
	/**
	 * if peaks continue time less this value (ms), it don't conside a counting sound
	*/
	private static final int MIN_PEAKS_TIME = 50;
	
	/*
	 * the value that conside a counting sound start
	 */
	private static final float ENTRY_VALUE = 1.0f;
	
	/*
	 * the value that conside a counting sound end, it means silence start
	 */
	private static final float LEAVE_VALUE = 0.95f;
	
	/*
	 * when the value less leave value (ms), it should continue this time, that can be conside a true silence
	 */
	private static final int SLIENCE_DURING_TIME = 70;
	
	/*
	 * RMS(root mean square): the sound block rms should great this value
	 */
	private static final float LIMIT_RMS = 0.001f ;
	

	private int	bufferSize = 0;
	
	private AudioRecord	recorder = null;
	private RecordThread	mRecordThread = null ;
	private boolean isRecording = false;
	
	private final IBinder mBinder = new LocalBinder() ;
	
	private Bang	mBang = null ;
	
	private ArrayList<Long> mLaps ;
	private Lock	arrayLock ;
	
	public RecordService() {
		
		arrayLock = new ReentrantLock();
		mLaps = new ArrayList<Long>();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG,"service onCreate");
		
		bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
		
	}

	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//return super.onStartCommand(intent, flags, startId);
		Log.d(TAG,"service onStartCommand");
		return START_STICKY ;
	}



	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG,"service onBind");
		return mBinder ;
	}
	
	



	@Override
	public void onDestroy() {
		super.onDestroy();
		stopRecord();
		Log.d(TAG,"service onDestroy");
	}



	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}



	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		Log.d(TAG,"service onRebind");
	}



	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(TAG,"service onStart");
	}



	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "service onUnbind");
		return super.onUnbind(intent);
	}
	
	public void setBang(Bang bang) {
		
		synchronized(this) {
			mBang = bang;
		}
	}
	
	public void unsetBang() {
		synchronized(this) {
			mBang = null ;
		}
	}
	
	public void startRecord() {
		if (isRecording == false) {
			isRecording = true;
			mRecordThread = new RecordThread(this);
			mRecordThread.start();
		}
	}
	
	public void stopRecord() {
		isRecording = false ;
		mRecordThread = null;
	}
	
	public ArrayList<Long> getLaps(int fromindex) {
		
		ArrayList<Long> ary = new ArrayList<Long>() ;
		arrayLock.lock();
		try {
			if (fromindex < mLaps.size()) {
				ary.addAll(mLaps.subList(fromindex, mLaps.size()));
			}
			
		}
		finally {
			arrayLock.unlock();
		}
		return ary;
	}
	
	private void AddLap(long currenttime) {
		arrayLock.lock();
		try {
			mLaps.add(currenttime);
		}
		finally {
			arrayLock.unlock();
		}
	}
	
	private void clearLaps() {
		arrayLock.lock();
		try {
			mLaps.clear();
		}
		finally {
			arrayLock.unlock();
		}
	}
	
	public class LocalBinder extends Binder {
		public RecordService getService() {
			return RecordService.this;
		}
	}

	private class RecordThread extends Thread {
		
		final RecordService mService ;
		
		public RecordThread(RecordService service) {
			super();
			mService = service ;
		}

		@Override
		public void run() {
			
			short databuff[] = new short[mService.bufferSize];
			
			int read = 0; 
			int offset = 0;
			boolean incountpeaking = false;
			int cbcontinuepeak = 0;
			int cbsilent = 0;
			
			/*
			int MAXGUNPEAKS = RECORDER_SAMPLERATE * Prefs.getMaxpeaks(getApplicationContext()) / 1000 ; 
			MINGUNPEAKS = RECORDER_SAMPLERATE * Prefs.getMinpeaks(getApplicationContext()) /1000 ;
			ENTRYVALUE = Prefs.getEntryvalue(getApplicationContext());
			LEAVEVALUE = Prefs.getLeavevalue(getApplicationContext());
			SLIENCEDURS = RECORDER_SAMPLERATE * Prefs.getSilencedur(getApplicationContext()) / 1000 ;
			float limitrms = Prefs.getRMS(getApplicationContext());
			*/
			
			int MAXGUNPEAKS = RECORDER_SAMPLERATE * mService.MAX_PEAKS_TIME / 1000 ; 
			int MINGUNPEAKS = RECORDER_SAMPLERATE * mService.MIN_PEAKS_TIME /1000 ;
			float ENTRYVALUE = mService.ENTRY_VALUE ;
			float LEAVEVALUE = mService.LEAVE_VALUE ;
			int SLIENCEDURS = RECORDER_SAMPLERATE * mService.SLIENCE_DURING_TIME / 1000 ;
			float limitrms = mService.LIMIT_RMS ;
			
			float rms = 0.0f;
			
			recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING,bufferSize);
			recorder.startRecording();
			
			
			while ( isRecording ) {
				read = recorder.read(databuff, offset, mService.bufferSize) ;
				
				if (read > 0) {
					for (int i = 0; i < read ; i++) {
						float temps = (float)Math.abs(databuff[i])/(float)Short.MAX_VALUE ;
						
						if (incountpeaking) {
							if (temps > LEAVEVALUE) {
								cbcontinuepeak++;
								rms += temps * temps;
								cbsilent = 0;
							} else {
								cbsilent++;
								if (cbsilent < SLIENCEDURS) {
									cbcontinuepeak++;
									rms += temps * temps ;
								} else {
									if (cbcontinuepeak > MINGUNPEAKS && cbcontinuepeak < MAXGUNPEAKS) {
										// find gun sound
										rms = (float)Math.sqrt(rms/cbcontinuepeak);
										//Log.d("dd","rms="+rms+" CB = "+cbcontinuepeak);
										if (rms > limitrms) {
											mService.AddLap(SystemClock.elapsedRealtime());
											synchronized(this) {
												if (mService.mBang != null)
													mService.mBang.onBang() ;
											}
										}
										//reset flag for next detect
									} else {
										Log.d("dd","cbcontinuepeak = "+ cbcontinuepeak);
										
									}
									incountpeaking = false ;
									cbcontinuepeak = 0;
								}
							}
							
						} else {
							if (temps > ENTRYVALUE) {
								rms += temps * temps;
								incountpeaking = true;
								cbsilent = 0;
							}
							
						}
					}
				}
				else {
					Log.d(TAG,"record error. read = " + read) ;
				}
			}
			
			recorder.stop();
			recorder.release();
			recorder = null ;
		}
		
	}
	
}
