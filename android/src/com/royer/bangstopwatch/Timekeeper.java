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


package com.royer.bangstopwatch;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;

public class Timekeeper implements Parcelable {

	private long	_startStamp  = 0;
	
	/**
	 * keep the elapsed time to display when activity restart
	 */
	private long	_elapsedTime = 0;
	
	private boolean	_isrunning = false;
	
	/**
	 * constructor
	 */	
	public Timekeeper() {
		_startStamp = 0 ;
		_elapsedTime = 0;
		_isrunning = false;
	}
	
	/**
	 * constructor to use when re-constructing from a parcel
	 * 
	 * @param in 
	 *        a parcel from which to read 
	 */
	Timekeeper(Parcel in) {
		readFromParcel(in);
	}
	
	public long get_startStamp() {
		return _startStamp;
	}
	
	
	/**
	 * because the time watch panel show the last lap elapsed time, 
	 * so if there has laps, we need reset the elapsed time same as
	 * the last lap elapsed time.
	 * @param et
	 */
	public void set_elapsedTime(long et) {
		_elapsedTime = et;
	}
	
	
	/**
	 * not like normal stopwatch, Bang stopwatch always start a new measurement 
	 * 
	 */
	public void start() {
		if (_isrunning == false) {
			_startStamp = SystemClock.elapsedRealtime() ;
			_isrunning = true ;
		}
	}
	
	/**
	 * Resets the time measurement. Next call of {@link #start()} will start a
	 * new measurement.
	 */
	public void reset() {
		_startStamp = 0;
		_isrunning = false;
	}
	
	
	/**
	 * Get the number of milliseconds elapsed during measurement.
	 * 
	 * @return elapsed time in milliseconds
	 */
	public long getElapsedTime() {
		long time = 0;
		
		if (isrunning()) {
			time = SystemClock.elapsedRealtime() - _startStamp;
			_elapsedTime = time;
		} else {
			time = _elapsedTime ;
		}
		
		return time;
	}

	public boolean isrunning() {
		
		return _isrunning;
	}

	public void stop() {
		getElapsedTime() ; 
		_isrunning = false;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeLong(_startStamp);
		dest.writeLong(_elapsedTime);
		dest.writeInt(_isrunning ? 1 : 0) ;
		
		Log.d("timekepper","writetoParcel() _startStamp: " + _startStamp + 
				" _elapsedTime: " + _elapsedTime);
	}
	
	
	/**
	 * call from constructor to create from a parcel
	 *  
	 * @param in
	 *        a parcel from which to re-create
	 */
	private void readFromParcel(Parcel in) {
		_startStamp = in.readLong() ;
		_elapsedTime = in.readLong();
		_isrunning  = (in.readInt() == 1) ;
		Log.d("timekepper","readFromParcel() _startStamp: " + _startStamp + 
				" _elapsedTime: " + _elapsedTime);
	}
	
	
	public static final Parcelable.Creator<Timekeeper> CREATOR = 
			new Parcelable.Creator<Timekeeper>() {

				@Override
				public Timekeeper createFromParcel(Parcel source) {
					
					return new Timekeeper(source);
				}

				@Override
				public Timekeeper[] newArray(int size) {
					return new Timekeeper[size];
				}
			};
}
