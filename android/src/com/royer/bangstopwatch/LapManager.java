package com.royer.bangstopwatch;

import java.util.ArrayList;

public class LapManager {

	// the beginning time stamp, usually get from Timekeeper
	private long tmStart ;
	
	private ArrayList<Lap> _laps = new ArrayList<Lap>();
	
	public LapManager() {
		tmStart = 0;
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
}
