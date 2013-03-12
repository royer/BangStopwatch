package com.royer.bangstopwatch;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

public class LapManager implements Parcelable {

	// the beginning time stamp, usually get from Timekeeper
	private long tmStart ;
	
	private ArrayList<Lap> _laps = new ArrayList<Lap>();
	
	public LapManager() {
		tmStart = 0;
	}
	
	private LapManager(Parcel in) {
		readFromParcel(in);
	}


	public long getTmStart() {
		return tmStart;
	}

	public void setTmStart(long tmStart) {
		this.tmStart = tmStart;
	}

	public ArrayList<Lap> get_laps() {
		return _laps;
	}
	
	/** 
	 * Deletes all Laps and resets the members
	 */
	public void clear() {
		_laps.clear();
		setTmStart(0) ;
	}
	
	/**
	 * add a Lap by current time
	 * 
	 * @param tmCurrent
	 *                 the current time                 
	 */
	public void appandLap(long tmCurrent) {
		
		long laptime = 0;
		long abstime = tmCurrent - tmStart ;
		
		if (_laps.isEmpty()) {
			
			laptime = abstime ;
			
		} else {
			Lap lastlap = _laps.get(_laps.size()-1);
			laptime = abstime - lastlap.get_abstime();
		}
		
		Lap newlap = new Lap(abstime, laptime);
		
		_laps.add(newlap);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(tmStart);
		Lap[] arylap = (Lap[]) _laps.toArray() ;
		dest.writeParcelableArray(arylap, flags);
		
	}

	private void readFromParcel(Parcel in) {
		
		tmStart = in.readLong();
		
		Lap[] arylap = (Lap[])in.readParcelableArray(
				Lap.class.getClassLoader());
		
		_laps.addAll(Arrays.asList(arylap));
		
	}
	
	public static final Parcelable.Creator<LapManager> CREATOR = 
			new Parcelable.Creator<LapManager>() {

				@Override
				public LapManager createFromParcel(Parcel source) {
					
					return new LapManager(source);
				}

				@Override
				public LapManager[] newArray(int size) {
					return new LapManager[size];
				}
			};

}
