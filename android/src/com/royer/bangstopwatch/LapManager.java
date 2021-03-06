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
	 *        the current time                 
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
	
	/**
	 * delete a lap by position, it must adjust next lap value
	 * add this during time to next lap during time 
	 * @param position
	 */
	public void DeleteLap(int position) {
		Lap thelap = _laps.get(position);
		if (position < (_laps.size() - 1)) {
			Lap nextlap = _laps.get(position + 1) ;
			nextlap.set_laptime(nextlap.get_laptime() + thelap.get_laptime());
		}
		
		_laps.remove(position);
	}

	
	public long getTotalElapsedTime() {
		
		long t = 0;
		
		if (_laps.size() > 0) {
			t = _laps.get(_laps.size() - 1).get_abstime();
		}
		return t;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(tmStart);
		Lap[] arylap = _laps.toArray(new Lap[0]) ;
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
