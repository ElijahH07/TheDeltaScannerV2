// rfid activity for honeywell
package com.honeywell.barcodeexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ZebraRFIDActivity extends BaseActivity {

    private static final String INTENT_ACTION = "com.zebra.datacapture1.ACTION";
    private static final String PROFILE_NAME = "DeltaOneInternProject";
    private static final String ACTION_DATAWEDGE = "com.symbol.datawedge.api.ACTION";
    private static final String EXTRA_CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE";
    private static final String EXTRA_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG";
    private static final String LOG_TAG = "error: ";
    //endregion

    //region
    private ListView barcodeList;
    private TextView counter;
    private TextView timer;
    private Button homeButton;
    private Button settingsButton;
    private Button clearButton;
    private Timer myTimer;
    private ArrayList<ArrayList<String>> scannedData;
    private ArrayList<String> scannedItems;
    private int currCount;
    private int maxCount;
    private int startTime;
    private int currTime;
    private boolean isTimerOn;
    private boolean soundEnabled;
    private MediaPlayer sonicDeathSound;
    private MediaPlayer sonicDeathSound2; //same sound as sonicDeathSound
    private MediaPlayer sonicSound;
    private MediaPlayer sonicSound2; //same sound as sonicSound
    private MediaPlayer sonicSound3; //same sound as sonicSound
    private MediaPlayer sonicTallySound;
    private final boolean defaultValue = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_screen);

        setUp();

        updateDWProfile(this);

        IntentFilter filter = new IntentFilter(); // wait for broadcast intents from the DataWedge app
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(INTENT_ACTION);
        registerReceiver(myBroadcastReceiver, filter);

        ActivitySetting();
    }
    public void updateDWProfile(Context context) {
        Bundle profileConfig = new Bundle(); //profileConfig is the main Bundle which holds PARAM_LIST Bundle and PLUGIN_CONFIG Bundle
        profileConfig.putString("PROFILE_NAME", PROFILE_NAME);
        profileConfig.putString("PROFILE_ENABLED", "true"); //  Seems these are all strings
        profileConfig.putString("CONFIG_MODE", "UPDATE"); // creates if not exist, updates if exists

        //disable barcode scanning

        profileConfig.remove("PLUGIN_CONFIG");
        Bundle barcodeConfig = new Bundle();
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE");
        barcodeConfig.putString("RESET_CONFIG", "false");

        Bundle barcodeProps = new Bundle();
        barcodeProps.putString("barcode_trigger_mode", "0");
        barcodeProps.putString("scanner_input_enabled", "false");
        barcodeConfig.putBundle("PARAM_LIST", barcodeProps);
        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig);
        sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);

        //enable rfid
        Bundle rfidConfig = new Bundle();
        rfidConfig.putString("PLUGIN_NAME", "RFID");
        rfidConfig.putString("RESET_CONFIG", "false");

        Bundle rfidConfigParamList  = new Bundle();
        rfidConfigParamList.putString("rfid_key_mapping", "2");
        rfidConfigParamList.putString("rfid_input_enabled", "true");
        rfidConfigParamList.putString("rfid_beeper_enable", "true");
        rfidConfigParamList.putString("rfid_led_enable", "true");
        rfidConfigParamList.putString("rfid_antenna_transmit_power", "30");
        rfidConfigParamList.putString("rfid_memory_bank", "4");
        rfidConfigParamList.putString("rfid_session", "1");
        rfidConfigParamList.putString("rfid_trigger_mode", "1");
        rfidConfigParamList.putString("rfid_filter_duplicate_tags", "true");
        rfidConfigParamList.putString("rfid_hardware_trigger_enabled", "true");
        rfidConfigParamList.putString("rfid_hardware_key_mode", "2");
        rfidConfigParamList.putString("rfid_tag_read_duration", "1");

        // Pre-filter
        rfidConfigParamList.putString("rfid_pre_filter_enable", "true");
        rfidConfigParamList.putString("rfid_pre_filter_tag_pattern", "3EC");
        rfidConfigParamList.putString("rfid_pre_filter_target", "2");
        rfidConfigParamList.putString("rfid_pre_filter_memory_bank", "4");
        rfidConfigParamList.putString("rfid_pre_filter_offset", "2");
        rfidConfigParamList.putString("rfid_pre_filter_action", "2");

        // Post-filter
        rfidConfigParamList.putString("rfid_post_filter_enable", "true");
        rfidConfigParamList.putString("rfid_post_filter_no_of_tags_to_read", "2");
        rfidConfigParamList.putString("rfid_post_filter_rssi", "-54");


        rfidConfig.putBundle("PARAM_LIST", rfidConfigParamList);
        profileConfig.putBundle("PLUGIN_CONFIG", rfidConfig);

        sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);



    }

    private static void sendDataWedgeIntentWithExtra(Context context, String action, String extraKey, Bundle extras) {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extras);
        context.sendBroadcast(dwIntent);
    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();

            if (action.equals(INTENT_ACTION)) {
                //  Received a barcode scan
                try {
                    displayScanResult(intent, "via Broadcast"); //display broadcast result
                } catch (Exception e) {
                    Log.d(LOG_TAG, e.toString());
                }
            } else {
                Log.d(LOG_TAG, "action does not equal");
            }
        }
    };
    private void displayScanResult(Intent initiatingIntent, String howDataReceived) {
        if (maxCount <= 0 || currCount < maxCount) {
            if (timer.isShown() && !isTimerOn) {
                isTimerOn = true;
                startTime = (int) (System.currentTimeMillis());
                startTimer();
            }
            String decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
            String numericDecodedData = decodedData;
            while (numericDecodedData.indexOf("\u001D") >= 0) {
                numericDecodedData = numericDecodedData.substring(0, numericDecodedData.indexOf("\u001D")) + numericDecodedData.substring(numericDecodedData.indexOf("\u001D") + 1);
            }
            ArrayList<String> list = new ArrayList<String>();
            list.add(decodedData);
            String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));
            list.add(decodedLabelType);

            for (ArrayList<String> l : scannedData) {
                if (l.get(0).equals(list.get(0))) {
                    if (soundEnabled) {
                        if (!sonicDeathSound.isPlaying()) {
                            sonicDeathSound.start();
                        } else if (!sonicDeathSound2.isPlaying()) {
                            sonicDeathSound2.start();
                        }
                    }
                    return;
                }
            }
            if (soundEnabled) {
                if (!sonicSound.isPlaying()) {
                    sonicSound.start();
                } else if (!sonicSound2.isPlaying()) {
                    sonicSound2.start();
                } else if (!sonicSound3.isPlaying()) {
                    sonicSound3.start();
                }
            }
            scannedData.add(0, list);
            scannedItems.add(0, "" + scannedData.size() + ".) " + numericDecodedData);
            barcodeList.setAdapter(new ArrayAdapter<String>(ZebraRFIDActivity.this, R.layout.list_layout, scannedItems) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setTextSize(16);

                    return textView;
                }
            });
            currCount = scannedItems.size();
            setCounter();
        } else if (soundEnabled) {
            if (!sonicDeathSound.isPlaying()) {
                sonicDeathSound.start();
            } else if (!sonicDeathSound2.isPlaying()) {
                sonicDeathSound2.start();
            }
        }
    }

    //endregion

    //region Activity Methods


    //endregion

    //region Activity Methods
    @Override
    public void onResume() {
        super.onResume();
        secondSetUp();
        updateDWProfile(ZebraRFIDActivity.this);
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        registerReceiver(myBroadcastReceiver, filter);
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
    //endregion


    IntentFilter filter = new IntentFilter();

    //endregion

    //region other
    public void ActivitySetting() {
        homeButton = (Button) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
                finish();
            }
        });

        barcodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast = Toast.makeText(getApplicationContext(), "not available yet", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent intent = new Intent("android.intent.action.SETTINGS");
                startActivity(intent);
            }
        });

        clearButton = (Button) findViewById(R.id.clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scannedItems.clear();
                scannedData.clear();
                currCount = 0;
                isTimerOn = false;
                if (myTimer != null) {
                    myTimer.cancel();
                }
                currTime = 0;
                timer.setText(R.string.time);
                final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ZebraRFIDActivity.this, android.R.layout.simple_list_item_1, scannedItems);
                barcodeList.setAdapter(dataAdapter);
                setCounter();
            }
        });
    }

    private void setCounter() {
        if (maxCount > 0) {
            counter.setText("COUNT: " + currCount + "/" + maxCount);
            if (soundEnabled && currCount >= maxCount) {
                sonicTallySound.start();
            }
        } else {
            counter.setText("COUNT: " + currCount);
        }
    }

    private String getCurrTime(int initalTime) {
        String time = "";
        int millis = initalTime + (int) (System.currentTimeMillis()) - startTime;
        currTime = millis;
        int sec = millis / 1000;
        millis %= 1000;
        millis /= 10;
        if (sec >= 60) {
            int min = sec / 60;
            sec %= 60;
            min %= 60;
            if (min < 10) {
                time += "0" + min + ":";
            } else {
                time += min + ":";
            }
        } else {
            time += "00:";
        }
        if (sec < 10) {
            time += "0" + sec + ".";
        } else {
            time += sec + ".";
        }
        if (millis < 10) {
            time += "0";
        }
        time += "" + millis;
        return time;
    }

    private void startTimer() {
        myTimer = new Timer();
        int initalTime = currTime;
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ZebraRFIDActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (maxCount > 0 && currCount >= maxCount) {
                            isTimerOn = false;
                            myTimer.cancel();
                            return;
                        }
                        timer.setText("TIME: " + getCurrTime(initalTime));
                    }
                });
            }
        }, 0, 10);
    }

    private void setUp() {
        //LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("Settings"));
        soundEnabled = true;
        scannedData = new ArrayList<>();
        scannedItems = new ArrayList<>();
        currCount = 0;
        maxCount = -1;
        startTime = -1;
        isTimerOn = false;
        currTime = 0;
        setContentView(R.layout.scan_screen);
        counter = (TextView) findViewById(R.id.counter);
        timer = (TextView) findViewById(R.id.timer);
        barcodeList = (ListView) findViewById(R.id.listViewBarcodeData);

        sonicSound = MediaPlayer.create(getApplicationContext(), R.raw.sonic_sound);
        sonicSound2 = MediaPlayer.create(getApplicationContext(), R.raw.sonic_sound);
        sonicSound3 = MediaPlayer.create(getApplicationContext(), R.raw.sonic_sound);
        sonicDeathSound = MediaPlayer.create(getApplicationContext(), R.raw.sonic_death_sound);
        sonicDeathSound2 = MediaPlayer.create(getApplicationContext(), R.raw.sonic_death_sound);
        sonicTallySound = MediaPlayer.create(getApplicationContext(), R.raw.sonic_tally_sound);

        if (sharedPref.getBoolean("timer", false)) {
            timer.setVisibility(View.VISIBLE);
        } else {
            timer.setVisibility(View.INVISIBLE);
            startTime = -1;
            timer.setText(R.string.time);
        }
        maxCount = sharedPref.getInt("count", -1);
        if (maxCount != -1) {
            counter.setVisibility(View.VISIBLE);
            setCounter();
        } else {
            counter.setVisibility(View.INVISIBLE);
        }
        soundEnabled = sharedPref.getBoolean("sound", true);
    }

    private void secondSetUp() {
        if (sharedPref.getBoolean("timer", false)) {
            timer.setVisibility(View.VISIBLE);
        } else {
            timer.setVisibility(View.INVISIBLE);
            startTime = -1;
            timer.setText(R.string.time);
        }
        maxCount = sharedPref.getInt("count", -1);
        if (maxCount != -1) {
            counter.setVisibility(View.VISIBLE);
            setCounter();
        } else {
            counter.setVisibility(View.INVISIBLE);
        }
        soundEnabled = sharedPref.getBoolean("sound", true);
    }
}
//endregion