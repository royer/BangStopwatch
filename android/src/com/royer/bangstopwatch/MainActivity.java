package com.royer.bangstopwatch;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;

import com.royer.bangstopwatch.*;

public class MainActivity extends SherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
    }

    
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    */
    
}
