package com.royer.bangstopwatch.adapters;

import java.util.ArrayList;

import com.royer.bangstopwatch.R;
import com.royer.bangstopwatch.Lap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LapArrayAdapter extends ArrayAdapter<Lap> {
	
	private LayoutInflater mInflater ;
	
	private ArrayList<Lap> mValues ;
	
	private String	strTimeFormat;
	
	private static class ViewHolder {
		
		public TextView	LapNumber ;
		
		public TextView LapTime;
		public TextView LapTimeMilli;
		
		public TextView AbsTime;
		public TextView AbsTimeMilli;
	}

	public LapArrayAdapter(Context context, ArrayList<Lap> values) {
		super(context, R.layout.lap_entry, values);
		
        // Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);
		
		mValues = values;
		
		strTimeFormat = context.getString(R.string.time_fmt_no_milli);
	}
	
	private String getTimeString(long time) {
		time += 50;	// truncation tolerance
		
		long hour = time / 3600000 % 100;
		long min = time / 60000 % 60 ;
		long sec = time / 1000 % 60 ;
		
		String str = String.format(strTimeFormat, hour, min, sec);
		
		return str;
	}
	
	private String getTimeMilliString(long time) {
		
		String str = String.format("%02d", time / 10 % 100);	
		
		return str;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewholder;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.lap_entry,null);
			
			viewholder = new ViewHolder();
			
			viewholder.LapNumber = (TextView)convertView.findViewById(R.id.lapNumber);
			
			viewholder.LapTime = (TextView)convertView.findViewById(R.id.lapTime);
			viewholder.LapTimeMilli = (TextView)convertView.findViewById(R.id.lapTimeMilli);
			
			viewholder.AbsTime = (TextView)convertView.findViewById(R.id.absTime);
			viewholder.AbsTimeMilli = (TextView)convertView.findViewById(R.id.absTimeMilli);
			
			convertView.setTag(viewholder);
			
		} else {
			
			viewholder = (ViewHolder)convertView.getTag();
			
		}
		
		viewholder.LapNumber.setText(String.format(" %d", position + 1));
		
		viewholder.LapTime.setText(getTimeString(mValues.get(position).get_laptime()));
		viewholder.LapTimeMilli.setText(getTimeMilliString(mValues.get(position).get_laptime()));
		
		viewholder.AbsTime.setText(getTimeString(mValues.get(position).get_abstime()));
		viewholder.AbsTimeMilli.setText(getTimeMilliString(mValues.get(position).get_abstime()));
		
		return convertView;
		
	}
}
