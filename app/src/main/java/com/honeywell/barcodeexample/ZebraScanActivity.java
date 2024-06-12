package com.honeywell.barcodeexample;

import static android.provider.ContactsContract.Intents.Insert.ACTION;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.ScannerUnavailableException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ZebraScanActivity extends BaseActivity {

    //region Zebra
    // DataWedge Sample supporting DataWedge APIs up to DW 7.0

    // DataWedge Actions
    private static final String PROFILE_NAME = "DeltaOneInternProject";
    private static final String ACTION_DATAWEDGE = "com.symbol.datawedge.api.ACTION";
    private static final String EXTRA_CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE";
    private static final String EXTRA_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG";

    // private variables
    final String LOG_TAG = "DataCapture1";
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
    private int mode;
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
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setUp();
            UpdateDWProfile(this, mode, sharedPref, defaultValue); // create DataWedge profile that links to this app



            IntentFilter filter = new IntentFilter(); // wait for broadcast intents from the DataWedge app
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
            registerReceiver(myBroadcastReceiver, filter); // receives broadcast intents

            ActivitySetting();

        } catch(Error e) {
            System.out.println(e.getMessage());
            Toast toast = Toast.makeText(getApplicationContext(), "error during Zebra Scan Activity: "+e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
            finish();
        }


    }

    public static void UpdateDWProfile(Context context, int mode, SharedPreferences sharedPref, boolean defaultValue) {
        // fixed loading time issues with update (creates profile in zebra home)

        //  Requires DataWedge 6.4 downloaded on zebra device

        //  Now configure that created profile to apply to our application
        Bundle profileConfig = new Bundle(); //profileConfig is the main Bundle which holds PARAM_LIST Bundle and PLUGIN_CONFIG Bundle
        profileConfig.putString("PROFILE_NAME", PROFILE_NAME);
        profileConfig.putString("PROFILE_ENABLED", "true"); //  Seems these are all strings
        profileConfig.putString("CONFIG_MODE", "UPDATE"); // updates

        //disable rfid scanning

        Bundle rfidConfig = new Bundle();
        rfidConfig.putString("PLUGIN_NAME", "RFID");
        rfidConfig.putString("RESET_CONFIG", "false");

        Bundle rfidProps = new Bundle();
        rfidProps.putString("rfid_key_mapping", "1");
        rfidProps.putString("rfid_hardware_trigger_enabled", "false");
        rfidProps.putString("rfid_input_enabled", "false");
        rfidConfig.putBundle("PARAM_LIST", rfidProps);
        profileConfig.putBundle("PLUGIN_CONFIG", rfidConfig);
        sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);

        // enable barcode stuff
        profileConfig.remove("PLUGIN_CONFIG");
        Bundle barcodeConfig = new Bundle();
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE");
        barcodeConfig.putString("RESET_CONFIG", "false");

        Bundle barcodeProps = new Bundle();
        barcodeProps.putString("barcode_trigger_mode", "2");
        barcodeProps.putString("scanner_selection", "auto");
        barcodeProps.putString("configure_all_scanners", "true");
        barcodeProps.putString("scanner_input_enabled", "true");

        if (mode == 1){                                              // if in painter mode, trigger mode is set to continous
            barcodeProps.putString("aim_type", "5");
        } else {
            barcodeProps.putString("aim_type", "0");
        }

        // add extra features in barcodeprops Bundle (SEE https://techdocs.zebra.com/datawedge/8-1/guide/api/setconfig/)

        //barcodeProps.putString("name", "key");
        if (sharedPref.getBoolean("flash", false)) {
            barcodeProps.putString("illumination_mode", "torch"); // torch is on, off is off
        }else {
            barcodeProps.putString("illumination_mode", "off"); // torch is on, off is off
        }
        barcodeProps.putString("decoder_datamatrix", ""+sharedPref.getBoolean("DataMatrix", defaultValue));
        barcodeProps.putString("decoder_gs1_datamatrix", ""+sharedPref.getBoolean("DataMatrix", defaultValue));

        barcodeProps.putString("decoder_code128", ""+sharedPref.getBoolean("Code 128", defaultValue));

        barcodeProps.putString("decoder_gs1_databar", ""+sharedPref.getBoolean("GS1-128", defaultValue));

        barcodeProps.putString("decoder_qrcode", ""+sharedPref.getBoolean("QR", defaultValue));

        barcodeProps.putString("decoder_code39", ""+sharedPref.getBoolean("Code 39", defaultValue));

        barcodeProps.putString("decoder_upce0", ""+sharedPref.getBoolean("UPC/EAN", defaultValue));
        barcodeProps.putString("decoder_upce1", ""+sharedPref.getBoolean("UPC/EAN", defaultValue));
        barcodeProps.putString("decoder_upca", ""+sharedPref.getBoolean("UPC/EAN", defaultValue));
        barcodeProps.putString("decoder_ean13", ""+sharedPref.getBoolean("UPC/EAN", defaultValue));
        barcodeProps.putString("decoder_ean8", ""+sharedPref.getBoolean("UPC/EAN", defaultValue));

        barcodeProps.putString("decoder_aztec", ""+sharedPref.getBoolean("Aztec", defaultValue));

        barcodeProps.putString("decoder_d2of5", ""+sharedPref.getBoolean("I. 2 of 5", defaultValue));

        barcodeProps.putString("decoder_pdf417", ""+sharedPref.getBoolean("PDF-417", defaultValue));

        // End of extra features

        barcodeConfig.putBundle("PARAM_LIST", barcodeProps);
        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig);

        sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);


    }


    //region Broadcast
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();


            if (action.equals(getResources().getString(R.string.activity_intent_filter_action))) {
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

    //endregion

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
            if (mode == 0) {
                List<Bundle> barcode = (List<Bundle>) initiatingIntent.getSerializableExtra("com.symbol.datawedge.barcodes");
                if (barcode != null) {
                    for (int i = 0; i < barcode.size(); i++) {
                        Bundle thisBarcode = barcode.get(i);
                        String decodedLabelType = thisBarcode.getString("com.symbol.datawedge.label_type");
                        list.add(decodedLabelType);
                    }
                }
            } else {
                String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));
                list.add(decodedLabelType);
            }
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
            barcodeList.setAdapter(new ArrayAdapter<String>(ZebraScanActivity.this, R.layout.list_layout, scannedItems) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setTextSize(20);

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

    private static void sendDataWedgeIntentWithExtra(Context context, String action, String extraKey, String extraValue) {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extraValue);
        context.sendBroadcast(dwIntent);
    }

    private static void sendDataWedgeIntentWithExtra(Context context, String action, String extraKey, Bundle extras) {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extras);
        context.sendBroadcast(dwIntent);
    }
    //endregion

    //region Activity Methods


    //endregion

    //region Activity Methods
    @Override
    public void onResume() {
        super.onResume();
        secondSetUp();
        UpdateDWProfile(ZebraScanActivity.this, mode, sharedPref, defaultValue);
        //registerReceiver(mReceiver, filter);
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

    //region BroadcastReceiver from settings
//    BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getBooleanExtra("time", false)) {
//                timer.setVisibility(View.VISIBLE);
//            } else {
//                timer.setVisibility(View.INVISIBLE);
//                startTime = -1;
//                timer.setText(R.string.time);
//            }
//            maxCount = intent.getIntExtra("count", 0);
//            if (maxCount != -1) {
//                counter.setVisibility(View.VISIBLE);
//                setCounter();
//            } else {
//                counter.setVisibility(View.INVISIBLE);
//            }
//            soundEnabled = intent.getBooleanExtra("sound", true);
//        }
//    };
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
                Intent intent = new Intent("android.intent.action.ANALYSIS");
                intent.putStringArrayListExtra("data", scannedData.get(position));
                startActivity(intent);
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
                final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ZebraScanActivity.this, android.R.layout.simple_list_item_1, scannedItems);
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
                ZebraScanActivity.this.runOnUiThread(new Runnable() {
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
        mode = getIntent().getIntExtra("mode", 0);
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