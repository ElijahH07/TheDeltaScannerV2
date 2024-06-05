package com.honeywell.barcodeexample;

import static android.provider.ContactsContract.Intents.Insert.ACTION;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ZebraScanActivity extends Activity {

    //region Zebra
    // DataWedge Sample supporting DataWedge APIs up to DW 7.0

    private static final String EXTRA_PROFILENAME = "DWDataCapture1";

    // DataWedge Extras
    private static final String EXTRA_GET_VERSION_INFO = "com.symbol.datawedge.api.GET_VERSION_INFO";
    private static final String EXTRA_CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE";
    private static final String EXTRA_KEY_APPLICATION_NAME = "com.symbol.datawedge.api.APPLICATION_NAME";
    private static final String EXTRA_KEY_NOTIFICATION_TYPE = "com.symbol.datawedge.api.NOTIFICATION_TYPE";
    private static final String EXTRA_SOFT_SCAN_TRIGGER = "com.symbol.datawedge.api.SOFT_SCAN_TRIGGER";
    private static final String EXTRA_RESULT_NOTIFICATION = "com.symbol.datawedge.api.NOTIFICATION";
    private static final String EXTRA_REGISTER_NOTIFICATION = "com.symbol.datawedge.api.REGISTER_FOR_NOTIFICATION";
    private static final String EXTRA_UNREGISTER_NOTIFICATION = "com.symbol.datawedge.api.UNREGISTER_FOR_NOTIFICATION";
    private static final String EXTRA_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG";

    private static final String EXTRA_RESULT_NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
    private static final String EXTRA_KEY_VALUE_SCANNER_STATUS = "SCANNER_STATUS";
    private static final String EXTRA_KEY_VALUE_PROFILE_SWITCH = "PROFILE_SWITCH";
    private static final String EXTRA_KEY_VALUE_CONFIGURATION_UPDATE = "CONFIGURATION_UPDATE";
    private static final String EXTRA_KEY_VALUE_NOTIFICATION_STATUS = "STATUS";
    private static final String EXTRA_KEY_VALUE_NOTIFICATION_PROFILE_NAME = "PROFILE_NAME";
    private static final String EXTRA_SEND_RESULT = "SEND_RESULT";

    private static final String EXTRA_EMPTY = "";

    private static final String EXTRA_RESULT_GET_VERSION_INFO = "com.symbol.datawedge.api.RESULT_GET_VERSION_INFO";
    private static final String EXTRA_RESULT = "RESULT";
    private static final String EXTRA_RESULT_INFO = "RESULT_INFO";
    private static final String EXTRA_COMMAND = "COMMAND";

    // DataWedge Actions
    private static final String ACTION_DATAWEDGE = "com.symbol.datawedge.api.ACTION";
    private static final String ACTION_RESULT_NOTIFICATION = "com.symbol.datawedge.api.NOTIFICATION_ACTION";
    private static final String ACTION_RESULT = "com.symbol.datawedge.api.RESULT_ACTION";

    // private variables
    private Boolean bRequestSendResult = false;
    final String LOG_TAG = "DataCapture1";
    //endregion

    //region
    private ListView barcodeList;
    private ArrayAdapter<String> dataAdapter;
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
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp();
        Toast toast = Toast.makeText(getApplicationContext(), "Zebra", Toast.LENGTH_SHORT);
        toast.show();
        ZebraSetup();
        ActivitySetting();
    }

    private void ZebraSetup() {
        // Main bundle properties
        Bundle profileConfig = new Bundle();
        profileConfig.putString("PROFILE_NAME", EXTRA_PROFILENAME);
        profileConfig.putString("PROFILE_ENABLED", "true");
        profileConfig.putString("CONFIG_MODE", "UPDATE");  // Update specified settings in profile

        // PLUGIN_CONFIG bundle properties
        Bundle barcodeConfig = new Bundle();
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE");
        barcodeConfig.putString("RESET_CONFIG", "true");

        Bundle bParamsDevice = new Bundle();
        bParamsDevice.putString("device_id", "BARCODE");
        bParamsDevice.putString("enabled", "true");
        bParamsDevice.putString("alldecoders", "true");
        bParamsDevice.putString("all_label_ids", "true");
        ArrayList<Bundle> bParamsRule = new ArrayList<Bundle>();
        bParamsRule.add(bParamsDevice);
        Bundle bParamsLabelID1 = new Bundle();
        bParamsLabelID1.putString("device_id", "BARCODE");
        bParamsLabelID1.putString("label_id", "UDI_GS1");
        bParamsLabelID1.putString("enabled", "true");
        ArrayList<Bundle> bParamsLabelIDList = new ArrayList<Bundle>();
        bParamsLabelIDList.add(bParamsLabelID1);
        Bundle bParamsRule1 = new Bundle();
        bParamsRule1.putParcelableArrayList("DEVICES", bParamsRule);
        bParamsRule1.putParcelableArrayList("LABEL_IDS", bParamsLabelIDList);
        ArrayList<Bundle> bParamsList = new ArrayList<Bundle>();
        bParamsList.add(bParamsRule1);
        barcodeConfig.putParcelableArrayList("PARAM_LIST", bParamsList);
        // PARAM_LIST bundle properties
        Bundle barcodeProps = new Bundle();
        barcodeProps.putString("scanner_selection", "auto");
        barcodeProps.putString("scanner_input_enabled", "true");


        if (mode == 0) {
            // barcodeProps.putInt("aim_type", 5);
            barcodeProps.putInt("picklist", 0);
            barcodeProps.putInt("scanning_mode", 3);
            barcodeProps.putBoolean("instant_reporting_enable", true);
        } else {
            barcodeProps.putInt("aim_type", 0);
            barcodeProps.putInt("picklist", 1);
        }
        barcodeProps.putInt("aim_timer", 0);
        barcodeProps.putInt("beam_timer", 0);
        barcodeProps.putString("aim_mode", "on");

        barcodeProps.putBoolean("decoder_gs1_datamatrix", true);
        barcodeProps.putBoolean("decoder_microqr", true);
        barcodeProps.putBoolean("decoder_i2of5", true);
        barcodeProps.putInt("remote_scanner_audio_feedback_mode", 3);

        // Bundle "barcodeProps" within bundle "barcodeConfig"
        barcodeConfig.putBundle("PARAM_LIST", barcodeProps);
        // Place "barcodeConfig" bundle within main "profileConfig" bundle
        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig);
        // Create APP_LIST bundle to associate app with profile
        Bundle appConfig = new Bundle();
        appConfig.putString("PACKAGE_NAME", getPackageName());
        appConfig.putStringArray("ACTIVITY_LIST", new String[]{"*"});
        profileConfig.putParcelableArray("APP_LIST", new Bundle[]{appConfig});
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);


        // Register for status change notification
        // Use REGISTER_FOR_NOTIFICATION: http://techdocs.zebra.com/datawedge/latest/guide/api/registerfornotification/
        Bundle b = new Bundle();
        b.putString(EXTRA_KEY_APPLICATION_NAME, getPackageName());
        b.putString(EXTRA_KEY_NOTIFICATION_TYPE, "SCANNER_STATUS");     // register for changes in scanner status
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE, EXTRA_REGISTER_NOTIFICATION, b);

        registerReceivers();
        // Get DataWedge version
        // Use GET_VERSION_INFO: http://techdocs.zebra.com/datawedge/latest/guide/api/getversioninfo/
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE, EXTRA_GET_VERSION_INFO, EXTRA_EMPTY);    // must be called after registering BroadcastReceiver
    }

    //region Zebra
    // Create filter for the broadcast intent
    private void registerReceivers() {

        Log.d(LOG_TAG, "registerReceivers()");

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RESULT_NOTIFICATION);   // for notification result
        filter.addAction(ACTION_RESULT);                // for error code result
        filter.addCategory(Intent.CATEGORY_DEFAULT);    // needed to get version info

        // register to received broadcasts via DataWedge scanning
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        filter.addAction(getResources().getString(R.string.activity_action_from_service));
        registerReceiver(myBroadcastReceiver, filter);
    }

    // Unregister scanner status notification
    public void unRegisterScannerStatus() {
        Log.d(LOG_TAG, "unRegisterScannerStatus()");
        Bundle b = new Bundle();
        b.putString(EXTRA_KEY_APPLICATION_NAME, getPackageName());
        b.putString(EXTRA_KEY_NOTIFICATION_TYPE, EXTRA_KEY_VALUE_SCANNER_STATUS);
        Intent i = new Intent();
        i.setAction(ACTION);
        i.putExtra(EXTRA_UNREGISTER_NOTIFICATION, b);
        this.sendBroadcast(i);
    }

    //region Broadcast
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();

            Log.d(LOG_TAG, "DataWedge Action:" + action);

            // Get DataWedge version info
            if (intent.hasExtra(EXTRA_RESULT_GET_VERSION_INFO)) {
                Bundle versionInfo = intent.getBundleExtra(EXTRA_RESULT_GET_VERSION_INFO);
                String DWVersion = versionInfo.getString("DATAWEDGE");

                // TextView txtDWVersion = (TextView) findViewById(R.id.txtGetDWVersion);
                //txtDWVersion.setText(DWVersion);
                Log.i(LOG_TAG, "DataWedge Version: " + DWVersion);
            }

            if (action.equals(getResources().getString(R.string.activity_intent_filter_action))) {
                //  Received a barcode scan
                try {
                    displayScanResult(intent, "via Broadcast");
                } catch (Exception e) {
                    //  Catch error if the UI does not exist when we receive the broadcast...
                }
            } else if (action.equals(ACTION_RESULT)) {
                // Register to receive the result code
                if ((intent.hasExtra(EXTRA_RESULT)) && (intent.hasExtra(EXTRA_COMMAND))) {
                    String command = intent.getStringExtra(EXTRA_COMMAND);
                    String result = intent.getStringExtra(EXTRA_RESULT);
                    String info = "";

                    if (intent.hasExtra(EXTRA_RESULT_INFO)) {
                        Bundle result_info = intent.getBundleExtra(EXTRA_RESULT_INFO);
                        Set<String> keys = result_info.keySet();
                        for (String key : keys) {
                            Object object = result_info.get(key);
                            if (object instanceof String) {
                                info += key + ": " + object + "\n";
                            } else if (object instanceof String[]) {
                                String[] codes = (String[]) object;
                                for (String code : codes) {
                                    info += key + ": " + code + "\n";
                                }
                            }
                        }
                        Log.d(LOG_TAG, "Command: " + command + "\n" + "Result: " + result + "\n" + "Result Info: " + info + "\n");
                        Toast.makeText(getApplicationContext(), "Error Resulted. Command:" + command + "\nResult: " + result + "\nResult Info: " + info, Toast.LENGTH_LONG).show();
                    }
                }

            }

            // Register for scanner change notification
            else if (action.equals(ACTION_RESULT_NOTIFICATION)) {
                if (intent.hasExtra(EXTRA_RESULT_NOTIFICATION)) {
                    Bundle extras = intent.getBundleExtra(EXTRA_RESULT_NOTIFICATION);
                    String notificationType = extras.getString(EXTRA_RESULT_NOTIFICATION_TYPE);
                    if (notificationType != null) {
                        switch (notificationType) {
                            case EXTRA_KEY_VALUE_SCANNER_STATUS:
                                // Change in scanner status occurred
                                String displayScannerStatusText = extras.getString(EXTRA_KEY_VALUE_NOTIFICATION_STATUS) + ", profile: " + extras.getString(EXTRA_KEY_VALUE_NOTIFICATION_PROFILE_NAME);
                                //Toast.makeText(getApplicationContext(), displayScannerStatusText, Toast.LENGTH_SHORT).show();
                                //final TextView lblScannerStatus = (TextView) findViewById(R.id.lblScannerStatus);
                                // lblScannerStatus.setText(displayScannerStatusText);
                                Log.i(LOG_TAG, "Scanner status: " + displayScannerStatusText);
                                break;

                            case EXTRA_KEY_VALUE_PROFILE_SWITCH:
                                // Received change in profile
                                // For future enhancement
                                break;

                            case EXTRA_KEY_VALUE_CONFIGURATION_UPDATE:
                                // Configuration change occurred
                                // For future enhancement
                                break;
                        }
                    }
                }
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
            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ZebraScanActivity.this, R.layout.list_layout, scannedItems);
            barcodeList.setAdapter(dataAdapter);
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

    private void sendDataWedgeIntentWithExtra(String action, String extraKey, Bundle extras) {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extras);
        if (bRequestSendResult) dwIntent.putExtra(EXTRA_SEND_RESULT, "true");
        this.sendBroadcast(dwIntent);
    }

    private void sendDataWedgeIntentWithExtra(String action, String extraKey, String extraValue) {
        Intent dwIntent = new Intent();
        dwIntent.setAction(action);
        dwIntent.putExtra(extraKey, extraValue);
        if (bRequestSendResult) dwIntent.putExtra(EXTRA_SEND_RESULT, "true");
        this.sendBroadcast(dwIntent);
    }
    //endregion

    //region Activity Methods
    @Override
    public void onResume() {
        super.onResume();
        registerReceivers();
        filter.addAction("Settings");
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(myBroadcastReceiver); //Zebra
        unRegisterScannerStatus(); //Zebra
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
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            soundEnabled = intent.getBooleanExtra("sound", true);
            if (intent.getBooleanExtra("time", false)) {
                timer.setVisibility(View.VISIBLE);
            } else {
                timer.setVisibility(View.INVISIBLE);
                startTime = -1;
                timer.setText(R.string.time);
            }
            maxCount = intent.getIntExtra("count", 0);
            if (maxCount != -1) {
                counter.setVisibility(View.VISIBLE);
                setCounter();
            } else {
                counter.setVisibility(View.INVISIBLE);
            }
        }
    };
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
                isTimerOn = false;
                Intent intent = new Intent("android.intent.action.SETTINGS");
                intent.putExtra("frag", mode);
                intent.putExtra("timer", timer.getVisibility() == View.VISIBLE);
                intent.putExtra("countAmnt", maxCount);
                intent.putExtra("sound", soundEnabled);
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
            if(soundEnabled && currCount>=maxCount){
                sonicTallySound.start();
            }
        } else {
            counter.setText("COUNT: " + currCount);
        }
    }

    private String getCurrTime(int initalTime) {
        String time = "";
        int millis = initalTime +  (int) (System.currentTimeMillis()) - startTime;
        currTime = millis;
        int sec = millis/1000;
        millis%=1000;
        millis/=10;
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
        if(millis<10){
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("Settings"));
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
    }
    //endregion
}
