package com.royer.bangstopwatch;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import com.royer.bangstopwatch.*;
import com.royer.bangstopwatch.app.StopwatchFragment;
import com.royer.bangstopwatch.app.TimerFragment;

public class MainActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        
        ActionBar actionbar = getSupportActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayShowTitleEnabled(false);
        
        Tab tab = actionbar.newTab()
        		.setText("Stopwatch")
        		.setTabListener(new TabListener<StopwatchFragment>(this, "Stopwatch",StopwatchFragment.class));
        actionbar.addTab(tab);
        
        tab = actionbar.newTab()
        		.setText("Timer")
        		.setTabListener(new TabListener<TimerFragment>(this, "Timer", TimerFragment.class));
        actionbar.addTab(tab);
        
    }

    
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    */
    
    /*
     * TabListener copy from http://developer.android.com/guide/topics/ui/actionbar.html
     */
    public static class TabListener<T extends SherlockFragment> implements ActionBar.TabListener {
    	private SherlockFragment mFragment;
    	private final SherlockFragmentActivity mActivity;
    	private final String	mTag;
    	private final Class<T> mClass;
    	
    	
    	/** Constructor used each time a new tab is created.
         * @param activity  The host Activity, used to instantiate the fragment
         * @param tag  The identifier tag for the fragment
         * @param clz  The fragment's Class, used to instantiate the fragment
         */
    	public TabListener(SherlockFragmentActivity activity, String tag, Class<T> clz) {
    		mActivity = activity ;
    		mTag = tag;
    		mClass = clz;
    	}
    	

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// check if the fragment is already initialized
			if (mFragment == null) {
				// if not, instantiate and add it to the activity
				mFragment = (SherlockFragment)SherlockFragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				// If it exists, simply attach it in order to show it
				ft.attach(mFragment);
			}
			
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				ft.detach(mFragment);
			}
			
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// User selected the already selected tab. Usually do nothing
			
		}
    	
    }
    
}
