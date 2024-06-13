package com.honeywell.barcodeexample;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

//All activities extend BaseActivity so that shared preferences can be properly shared
public class BaseActivity extends Activity {

    protected boolean setInHistory;
    protected SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setInHistory = true;
    }
}
