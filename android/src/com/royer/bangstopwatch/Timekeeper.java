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

import android.os.SystemClock;

public class Timekeeper {

	private long	_startStamp  = 0;
	//private long	_elapsedTime = 0;
	
	private boolean	_isrunning = false;
	
	public long get_startStamp() {
		return _startStamp;
	}
	
	/**
	 * On first call or if {@link #reset()} was called, a new measurement will start.
	 * Otherwise measurement is resumed.
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
		
		if (_startStamp != 0) {
			time = SystemClock.elapsedRealtime() - _startStamp;
		}
		
		return time;
	}

	public boolean isrunning() {
		
		return _isrunning;
	}

	public void stop() {
		
		_isrunning = false;
	}
}
