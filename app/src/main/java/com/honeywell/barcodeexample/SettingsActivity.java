package com.honeywell.barcodeexample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SettingsActivity extends BaseActivity {
    //region
    private Button backButton;
    private CheckBox timer;
    private CheckBox counter;
    private CheckBox flash;
    private CheckBox sound;
    private TextView counterText;
    private EditText counterInput;
    private Button barcodeselectbutton;

    //endregion
    @SuppressLint("SetTextI18n")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);
        timer = (CheckBox) findViewById(R.id.timer);
        sound = (CheckBox) findViewById(R.id.sound);
        counter = (CheckBox) findViewById(R.id.counter);
        flash = (CheckBox) findViewById(R.id.flash);
        counterText = (TextView) findViewById(R.id.amntScan);
        counterInput = (EditText) findViewById(R.id.amntScanInput);
//        if(getIntent().getIntExtra("frag", 0)==0)
//        {
//            counter.setVisibility(View.VISIBLE);
//            if(getIntent().getIntExtra("countAmnt", -1)!=-1){
//                counter.setChecked(true);
//                counterInput.setVisibility(View.VISIBLE);
//                counterText.setVisibility(View.VISIBLE);
//                counterInput.setText(""+getIntent().getIntExtra("countAmnt", 0));
//            }
//        }
        flash.setChecked(sharedPref.getBoolean("flash", false));
        if(sharedPref.getInt("count", -1) >= 0) {
            counter.setVisibility(View.VISIBLE);
            counterInput.setVisibility(View.VISIBLE);
            counterText.setVisibility(View.VISIBLE);
            counter.setChecked(true);
            counterInput.setText(""+sharedPref.getInt("count", 0));
        }
        if(sharedPref.getBoolean("timer", false)){
            timer.setChecked(true);
        }
        if(sharedPref.getBoolean("sound", true)){
            sound.setChecked(true);
        }
        ActivitySetting();
    }

    public void ActivitySetting() {
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean("timer", timer.isChecked());
                editor.putBoolean("sound", sound.isChecked());
                editor.putBoolean("flash", sound.isChecked());

                if(counter.isChecked()){
                    if(!counterInput.getText().toString().isEmpty()) {
                        editor.putInt("count", Integer.parseInt(counterInput.getText().toString()));
                    }
                    else{
                        editor.putInt("count", 0);
                    }
                }
                else{
                    editor.putInt("count", -1);
                }
                editor.apply();

//                Intent intent = new Intent("Settings");
//                intent.putExtra("time", timer.isChecked());
//                intent.putExtra("sound", sound.isChecked());
//                if(counter.isChecked()){
//                   if(!counterInput.getText().toString().isEmpty()) {
//                       intent.putExtra("count", Integer.parseInt(counterInput.getText().toString()));
//                   }
//                   else{
//                       intent.putExtra("count", 0);
//                   }
//                }
//                else{
//                    intent.putExtra("count", -1);
//                }
//                LocalBroadcastManager.getInstance(SettingsActivity.this).sendBroadcast(intent);
                finish();
            }
        });

        barcodeselectbutton = (Button) findViewById(R.id.barcodeselectbutton);
        barcodeselectbutton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.BARCODETYPE");
                startActivity(intent);
            }
        });


        counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(counter.isChecked()){
                    counterInput.setVisibility(View.VISIBLE);
                    counterText.setVisibility(View.VISIBLE);
                }
                else{
                    counterInput.setVisibility(View.INVISIBLE);
                    counterText.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
