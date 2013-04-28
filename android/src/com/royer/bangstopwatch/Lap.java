package com.royer.bangstopwatch;

import android.os.Parcel;
import android.os.Parcelable;

public class Lap implements Parcelable {

	/**
	 * from start to this lap total elapsed time
	 */
	private long	_abstime;
	
	/**
	 * from previous lap to this lap elapsed time
	 */
	private long	_laptime;
	
	public Lap() {
		_abstime = _laptime = 0;
	}
	
	public Lap(Parcel in) {
		readFromParcel(in);
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(_abstime);
		dest.writeLong(_laptime);
	}

	private void readFromParcel(Parcel in) {
		_abstime = in.readLong() ;
		_laptime = in.readLong();
	}
	
	
	public static final Parcelable.Creator<Lap> CREATOR = 
			new Parcelable.Creator<Lap>() {

				@Override
				public Lap createFromParcel(Parcel source) {
					return new Lap(source);
				}

				@Override
				public Lap[] newArray(int size) {
					return new Lap[size];
				}
			};
	
}
