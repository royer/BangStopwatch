package com.royer.bangstopwatch;

public class Lap {

	private long	_abstime;
	
	private long	_laptime;
	
	public Lap() {
		_abstime = _laptime = 0;
	}
	
	public Lap(long abstime, long laptime) {
		set_abstime(abstime);
		set_laptime(laptime);
	}

	public long get_abstime() {
		return _abstime;
	}

	public void set_abstime(long _abstime) {
		this._abstime = _abstime;
	}

	public long get_laptime() {
		return _laptime;
	}

	public void set_laptime(long _laptime) {
		this._laptime = _laptime;
	}
	
}
