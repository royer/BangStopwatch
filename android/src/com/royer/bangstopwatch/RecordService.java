package com.royer.bangstopwatch;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class RecordService extends Service {
	
	private static final String TAG="RecordService";
	
	private final IBinder mBinder = new LocalBinder() ;
	
	public RecordService() {
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
	
	
	public class LocalBinder extends Binder {
		public RecordService getService() {
			return RecordService.this;
		}
	}


	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d(TAG,"service onCreate");
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
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
	
	
	
	
}
