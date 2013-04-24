/*
 * Copyright (C) 2013 Royer Wang
 *
 * TabManager is copy from ActionBarSherlock Fragments sample
 * http://actionbarsherlock.com
 * Author: JakeWharton

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 */

package com.royer.bangstopwatch;

import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.TabHost;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import com.royer.bangstopwatch.*;
import com.royer.bangstopwatch.app.SaveRestoreMyData;
import com.royer.bangstopwatch.app.StopwatchFragment;
import com.royer.bangstopwatch.app.TimerFragment;

public class MainActivity extends SherlockFragmentActivity {
	
    TabHost mTabHost;
    TabManager mTabManager;
    
    private static final String TAG = "MainActivity" ;
    private static final String STATE_ACTIVITED_TAB = 
    		"royer.bangstopwatch.ACTIVITED_TAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.mainactivity);
        
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();
        //actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionbar.setDisplayShowHomeEnabled(false);
        //actionbar.setDisplayShowTitleEnabled(false);

        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        
        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
        mTabManager.addTab(mTabHost.newTabSpec("stopwatch").setIndicator("Stopwatch"), 
        		StopwatchFragment.class, null) ;
        mTabManager.addTab(mTabHost.newTabSpec("timer").setIndicator("Timer"), 
        		TimerFragment.class, null);
        
        Log.d(TAG,"onCreate") ;
        
        if (savedInstanceState != null) {
        	// TODO bug , countdownwindow which in StopWatchFragment is null
        	//mTabHost.setCurrentTab(savedInstanceState.getInt(STATE_ACTIVITED_TAB));
        }

        
        /*
        Tab tab = actionbar.newTab()
        		.setText("Stopwatch")
        		.setTabListener(new TabListener<StopwatchFragment>(this, "Stopwatch",StopwatchFragment.class));
        actionbar.addTab(tab);
       
        tab = actionbar.newTab()
        		.setText("Timer")
        		.setTabListener(new TabListener<TimerFragment>(this, "Timer", TimerFragment.class));
        actionbar.addTab(tab);
        */
        
    }
    
    
    
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
    	int currenttab = mTabHost.getCurrentTab();
    	Log.d(TAG,"onSaveInstanceState , current tab = " + currenttab) ;
    	outState.putInt(STATE_ACTIVITED_TAB, currenttab);
	}



	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		int currenttab = savedInstanceState.getInt(STATE_ACTIVITED_TAB,0) ;
		Log.d(TAG,"onRestorInstanceState, currenttab = " + currenttab) ;
		mTabHost.setCurrentTab(currenttab);
	}



	public void EnableTab(int tabindex,boolean enabled) {
    	mTabHost.getTabWidget().getChildTabViewAt(tabindex).setEnabled(enabled);
    }
    
    

    
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    */
    
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		if (mTabHost.getCurrentTab() == 0) {
    			StopwatchFragment fragment = (StopwatchFragment)this.getSupportFragmentManager().findFragmentByTag(mTabHost.getCurrentTabTag());
    			fragment.onAppWillQuit();
    		}
    	}
		return super.onKeyDown(keyCode, event);
	}




	/**
     * This is a helper class that implements a generic mechanism for
     * associating fragments with the tabs in a tab host.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between fragments.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabManager supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct fragment shown in a separate content area
     * whenever the selected tab changes.
     */
    public static class TabManager implements TabHost.OnTabChangeListener {
        private final FragmentActivity mActivity;
        private final TabHost mTabHost;
        private final int mContainerId;
        private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
        TabInfo mLastTab;

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;
            private Bundle	forTabswitch ;	// because if switch tab, the fragment lost his own data ;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
                forTabswitch = new Bundle();
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
            mActivity = activity;
            mTabHost = tabHost;
            mContainerId = containerId;
            mTabHost.setOnTabChangedListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mActivity));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }

            mTabs.put(tag, info);
            mTabHost.addTab(tabSpec);
        }

        @Override
        public void onTabChanged(String tabId) {
            TabInfo newTab = mTabs.get(tabId);
            if (mLastTab != newTab) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                if (mLastTab != null) {
                    if (mLastTab.fragment != null) {
                    	// before detach call interface OnBeforeDetach, fragment can save data when attach back
                    	// call fragment.OnBeforeDetach(bundle)
                    	((SaveRestoreMyData)mLastTab.fragment).onSaveMyData(mLastTab.forTabswitch);
                        ft.detach(mLastTab.fragment);
                    }
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = Fragment.instantiate(mActivity,
                                newTab.clss.getName(), newTab.args);
                        ft.add(mContainerId, newTab.fragment, newTab.tag);
                    } else {
                    	// TODO call fragment.OnBeforeAttach(bundle) ;
                    	if (mLastTab != null) {
                    		// if mLastTab == null , the TabChanged just because screen roate, 
                    		// the newTab.fragment is restore from a previously saved stated. 
                    		// and this situation that caused by TabManager.addTab()
                    		((SaveRestoreMyData)newTab.fragment).OnRestoreMyData(newTab.forTabswitch);
                    	}
                        ft.attach(newTab.fragment);
                    }
                }

                mLastTab = newTab;
                ft.commit();
                mActivity.getSupportFragmentManager().executePendingTransactions();
            }
        }
    }
    
    
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
