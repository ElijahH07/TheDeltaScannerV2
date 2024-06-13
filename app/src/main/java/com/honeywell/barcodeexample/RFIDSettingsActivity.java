package com.honeywell.barcodeexample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RFIDSettingsActivity extends BaseActivity {
    //region
    private Button backButton;
    private CheckBox timer;
    private CheckBox counter;
    private CheckBox sound;
    private TextView counterText;
    private EditText counterInput;
    private Button barcodeselectbutton;
    private EditText delay;

    //endregion
    @SuppressLint("SetTextI18n")
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.rfid_settings_screen);

            timer = (CheckBox) findViewById(R.id.timer2);
            sound = (CheckBox) findViewById(R.id.sound2);
            counter = (CheckBox) findViewById(R.id.counter2);
            delay = (EditText) findViewById(R.id.delay);
            counterText = (TextView) findViewById(R.id.amntScan2);
            counterInput = (EditText) findViewById(R.id.amntScanInput2);

            setInput();
            ActivitySetting();

        } catch (Error e) {
            System.out.println(e.getMessage());
            Toast toast = Toast.makeText(getApplicationContext(), "error during Settings Activity: "+e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
            finish();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        setInput();
        ActivitySetting();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override  //Zebra
    protected void onStart() {
        super.onStart();
    }

    @Override  //Zebra
    protected void onStop() {
        super.onStop();
    }

    @SuppressLint("SetTextI18n")
    public void setInput() {
        //delay.setChecked(sharedPref.getInt("delay", 250));
        if(sharedPref.getInt("count", -1) >= 0) {
            counter.setVisibility(View.VISIBLE);
            counterInput.setVisibility(View.VISIBLE);
            counterText.setVisibility(View.VISIBLE);
            counter.setChecked(true);
            counterInput.setText(""+sharedPref.getInt("count", 0));
        } else {
            counterInput.setVisibility(View.INVISIBLE);
            counterText.setVisibility(View.INVISIBLE);
        }
        timer.setChecked(sharedPref.getBoolean("timer", false));
        sound.setChecked(sharedPref.getBoolean("sound", true));
        if (sharedPref.getInt("delay", -1) >= 0) {
            delay.setText(""+sharedPref.getInt("delay", 0));
        }

    }
    public void ActivitySetting() {
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean("timer", timer.isChecked());
                editor.putBoolean("sound", sound.isChecked());
                if (delay.getText().toString() == null || delay.getText().toString().equals("") ) {
                    editor.putInt("delay", -1);
                } else {
                    editor.putInt("delay", Integer.parseInt(delay.getText().toString()));
                }

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

                finish();
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
