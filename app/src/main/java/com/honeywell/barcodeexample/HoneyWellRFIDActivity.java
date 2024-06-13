//package com.honeywell.barcodeexample;
//
//import com.honeywell.rfidservice.*;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//
//import android.os.Bundle;
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.content.pm.ActivityInfo;
//import android.media.MediaPlayer;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//import com.honeywell.aidc.BarcodeFailureEvent;
//import com.honeywell.aidc.BarcodeReadEvent;
//import com.honeywell.aidc.BarcodeReader;
//import com.honeywell.aidc.ScannerNotClaimedException;
//import com.honeywell.aidc.ScannerUnavailableException;
//import com.honeywell.aidc.TriggerStateChangeEvent;
//import com.honeywell.aidc.UnsupportedPropertyException;
//
//import com.honeywell.rfidservice.RfidManager;
//import com.honeywell.rfidservice.rfid.RfidReader;
//import com.honeywell.rfidservice.TriggerMode;
//import com.honeywell.rfidservice.EventListener;
//import com.honeywell.rfidservice.RfidManager;
//import com.honeywell.rfidservice.TriggerMode;
//import com.honeywell.rfidservice.rfid.Gen2;
//import com.honeywell.rfidservice.rfid.RfidReader;
//import com.honeywell.rfidservice.rfid.RfidReaderException;
//
//import java.util.ArrayList;
//import java.lang.Object;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class HoneyWellRFIDActivity extends BaseActivity implements 	RfidManager.CreatedCallback, BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener{
//    private com.honeywell.rfidservice.rfid.RfidReader rfidreader;
//    private ListView barcodeList;
//    private TextView counter;
//    private TextView timer;
//    private Button homeButton;
//    private Button settingsButton;
//    private Button clearButton;
//    private ArrayList<ArrayList<String>> scannedData;
//    private ArrayList<String> scannedItems;
//    private Timer myTimer;
//    private int mode;
//    private int currCount;
//    private int maxCount;
//    private int startTime;
//    private int currTime;
//    private boolean isTimerOn;
//    private boolean soundEnabled;
//    private MediaPlayer sonicDeathSound;
//    private MediaPlayer sonicDeathSound2;//same sound as sonicDeathSound
//    private MediaPlayer sonicSound;
//    private MediaPlayer sonicSound2; //same sound as sonicSound
//    private MediaPlayer sonicSound3; //same sound as sonicSound
//    private MediaPlayer sonicTallySound;
//    private boolean flash;
//    boolean defaultValue=true;
//
//    private static final String LOG = "[IH40]";
//    private final String READER_STATUS = "READER_STATUS";
//    private final String TRIGGER_STATUS = "TRIGGER_STATUS";
//    private final String WRITE_TAG_STATUS = "WRITE_TAG_STATUS";
//    private final String BATTERY_STATUS = "BATTERY_STATUS";
//    private final String TAG = "TAG";
//    private final String TAGS = "TAGS";
//    private static RfidManager mRfidMgr;
//    private static RfidReader mRfidReader;
//    private static RfidReader myRfidReader;
//    private static boolean isSingleRead = false;
//    private static boolean isReading = false;
//    private static Handler handler;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        //try catch function to indicate error during Honeywell RFID Activity
//        try {
//            super.onCreate(savedInstanceState);
//            Context tmpContext = getApplicationContext();
//            checkPermissions();
//            handler = new Handler(Looper.getMainLooper());
//
//            init();
//            myRfidReader = new RfidReader();
//            m
//            mRfidMgr = RfidManager.getInstance(this);
//            // did not check permissions, is it necessary???
//            //flash is turned off in default
//            flash = sharedPref.getBoolean("flash", false);
//            setUp();
//            HoneywellSetup();
//            ActivitySetting();
//        } catch(Error e) {
//            System.out.println(e.getMessage());
//            Toast toast = Toast.makeText(getApplicationContext(), "Error during Honeywell RFID Activity: "+e.getMessage(), Toast.LENGTH_LONG);
//            toast.show();
//            finish();
//        }
//    }
//
//
//    private void checkPermissions() {
////        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
////            // Demande de toutes les autorisations nécessaires
////            ActivityCompat.requestPermissions(this, new String[]{
////                    Manifest.permission.WRITE_EXTERNAL_STORAGE
////            },);
////        } else {
////        // Toutes les autorisations sont accordées, connectez-vous
//////            Toast.makeText(getApplicationContext(), "connect", Toast.LENGTH_SHORT).show();
//        connect("BLE");
//    }
//    public void connect(final String name) {
////        Toast.makeText(getApplicationContext(), "je suis dans connect", Toast.LENGTH_SHORT).show();
//        try {
//            if (mRfidMgr != null && mRfidMgr.isConnected()) {
//                doDisconnect();
//            }
//            scanner.startScan(scanCallback);
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, BLUETOOTH_SCAN);
//                            Log.d("MainActivity", "PAS DE PERMISSION BLUETOOTH_CONNECT");
//                        } else {
//                            scanner.stopScan(scanCallback);
//                        }
//                        Toast.makeText(getApplicationContext(), "Failed to connect reader...Please make sure your reader is power on.", Toast.LENGTH_SHORT).show();
//
//                    }
//                }, 8 * 1000);
//            }
//        } catch (Exception err) {
//            Toast.makeText(getApplicationContext(), "Exception err: "+err, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void doConnect() {
////        Toast.makeText(getApplicationContext(), "7", Toast.LENGTH_SHORT).show();
//        if (mRfidMgr != null && mRfidMgr.isConnected()) {
//            doDisconnect();
//        }
//
//        if (mRfidMgr == null) {
////            Toast.makeText(getApplicationContext(), "8", Toast.LENGTH_SHORT).show();
//            init();
//        }
//
//        if (mDevice != null) {
//
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
////                    Toast.makeText(getApplicationContext(), "connect"+mDevice.getAddress(), Toast.LENGTH_SHORT).show();
//                    mRfidMgr.connect(mDevice.getAddress());
//                }
//            }, 3 * 1000);
//        }
//    }
//
//    private void doDisconnect() {
//        if (mRfidMgr != null) {
//            mRfidMgr.removeEventListener(mEventListener);
//            mRfidMgr.disconnect();
//
//            mRfidMgr = null;
//        }
//
//        if (mRfidReader != null) {
////            mRfidReader.removeOnTagReadListener(dataListener);
//
//            mRfidReader = null;
//        }
//    }
//
//
//
//    private void init() {
//    }
//
//    private void HoneywellSetup() {
//        // dep on model changes orientation
//        if (Build.MODEL.startsWith("VM1A")) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
//
//        if (mRfidMgr==null){Toast.makeText(getApplicationContext(), " mRfidMgr==null", Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(getApplicationContext(), "mRfidMgr!=null", Toast.LENGTH_SHORT).show();
//            mRfidMgr.createReader();
//        }
//
//
//
//
//
//    }
//    @Override
//    public void onBarcodeEvent(final BarcodeReadEvent event) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (maxCount <= 0 || currCount < maxCount) {
//                    if (timer.isShown() && !isTimerOn) {
//                        isTimerOn = true;
//                        startTime = (int) (System.currentTimeMillis());
//                        startTimer();
//                    }
//                    String numericDecodedData = event.getBarcodeData();
//                    while (numericDecodedData.indexOf("\u001D") >= 0) {
//                        numericDecodedData = numericDecodedData.substring(0, numericDecodedData.indexOf("\u001D")) + numericDecodedData.substring(numericDecodedData.indexOf("\u001D") + 1);
//                    }
//                    // update UI to reflect the data
//                    String timeScanned = "" + event.getTimestamp().substring(0, 10) + "   " + event.getTimestamp().substring(11, 16);
//                    ArrayList<String> list = new ArrayList<String>();
//                    list.add("Barcode data: " + numericDecodedData);
//                    list.add("Character Set: " + event.getCharset());
//                    list.add("Code ID: " + event.getCodeId());
//                    list.add("AIM ID: " + event.getAimId());
//                    list.add("Timestamp: " + timeScanned);
//                    for (ArrayList<String> l : scannedData) {
//                        if (l.get(0).equals(list.get(0))) {
//                            if (soundEnabled) {
//                                if (!sonicDeathSound.isPlaying()) {
//                                    sonicDeathSound.start();
//                                } else if (!sonicDeathSound2.isPlaying()) {
//                                    sonicDeathSound2.start();
//                                }
//                            }
//                            return;
//                        }
//                    }
//                    if (soundEnabled) {
//                        if (!sonicSound.isPlaying()) {
//                            sonicSound.start();
//                        } else if (!sonicSound2.isPlaying()) {
//                            sonicSound2.start();
//                        } else if (!sonicSound3.isPlaying()) {
//                            sonicSound3.start();
//                        }
//                    }
//                    scannedData.add(0, list);
//                    scannedItems.add(0, "" + scannedData.size() + ".) " + numericDecodedData);
//                    final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(HoneywellScanActivity.this, R.layout.list_layout, scannedItems);
//                    barcodeList.setAdapter(dataAdapter);
//                    currCount = scannedItems.size();
//                    setCounter();
//                } else if (soundEnabled) {
//                    if (!sonicDeathSound.isPlaying()) {
//                        sonicDeathSound.start();
//                    } else if (!sonicDeathSound2.isPlaying()) {
//                        sonicDeathSound2.start();
//                    }
//                }
//            }
//        });
//    }
//
//
//
//
//
//
//
//    // When using Automatic Trigger control do not need to implement the
//    // onTriggerEvent function
//    @Override
//    public void onTriggerEvent(TriggerStateChangeEvent event) {
//        if (mode == 0) {
//            if (event.getState()) {
//                try {
//                    //This will start continuous scanning
//                    barcodeReader.aim(true);
//                    barcodeReader.light(true);
//                    barcodeReader.decode(true);
//                } catch (ScannerUnavailableException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
//                } catch (ScannerNotClaimedException e) {
//                    //throw new RuntimeException(e);
//                    e.printStackTrace();
//                    Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                // Add these lines to stop scanning when the application is paused
//                try {
//                    //This will stop scanning in continuous mode
//                    barcodeReader.aim(false);
//                    barcodeReader.light(false);
//                    barcodeReader.decode(false);
//                } catch (ScannerUnavailableException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
//                } catch (ScannerNotClaimedException e) {
//                    //throw new RuntimeException(e);
//                    e.printStackTrace();
//                    Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }
//
//
//    @Override
//    public void onFailureEvent(BarcodeFailureEvent arg0) {
//        // TODO Auto-generated method stub
//    }
//
//
//    //endregion
//
//
//    //region Activity Methods
//    @Override
//    public void onResume() {
//        super.onResume();
//        secondSetUp();
//        HoneywellSetup();
//        if (barcodeReader != null) {
//            try {
//                barcodeReader.claim();
//            } catch (ScannerUnavailableException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (barcodeReader != null) {
//            // release the scanner claim so we don't get any scanner
//            // notifications while paused.
//            barcodeReader.release();
//        }
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (rfidreader != null) {
//            // unregister barcode event listener
//            rfidreader.removeEventListener(this);
//
//
//            // unregister trigger state change listener
//            barcodeReader.removeTriggerListener(this);
//        }
//    }
//
//
//    @Override  //Zebra
//    protected void onStart() {
//        super.onStart();
//    }
//
//
//    @Override  //Zebra
//    protected void onStop() {
//        super.onStop();
//    }
//    //endregion
//
//
//
//
//    IntentFilter filter = new IntentFilter();
//
//
//    //endregion
//
//
//    //region other
//    public void ActivitySetting() {
//        homeButton = (Button) findViewById(R.id.homeButton);
//        homeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onDestroy();
//                finish();
//            }
//        });
//
//
//        barcodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent("android.intent.action.ANALYSIS");
//                intent.putStringArrayListExtra("data", scannedData.get(position));
//                startActivity(intent);
//            }
//        });
//
//
//        settingsButton = (Button) findViewById(R.id.settingsButton);
//        settingsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // get the intent action string from AndroidManifest.xml
//                isTimerOn = false;
//                Intent intent = new Intent("android.intent.action.SETTINGS");
//                intent.putExtra("frag", mode);
//                intent.putExtra("timer", timer.getVisibility() == View.VISIBLE);
//                intent.putExtra("countAmnt", maxCount);
//                intent.putExtra("sound", soundEnabled);
//                startActivity(intent);
//            }
//        });
//
//
//        //clears values
//        clearButton = (Button) findViewById(R.id.clear);
//        clearButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scannedItems.clear();
//                scannedData.clear();
//                currCount = 0;
//                isTimerOn = false;
//                if (myTimer != null) {
//                    myTimer.cancel();
//                }
//                currTime = 0;
//                timer.setText(R.string.time);
//                final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(HoneywellScanActivity.this, android.R.layout.simple_list_item_1, scannedItems);
//                barcodeList.setAdapter(dataAdapter);
//                setCounter();
//            }
//        });
//    }
//
//
//    private void setCounter() {
//        if (maxCount > 0) {
//            counter.setText("COUNT: " + currCount + "/" + maxCount);
//            if (soundEnabled && currCount >= maxCount) {
//                sonicTallySound.start();
//            }
//        } else {
//            counter.setText("COUNT: " + currCount);
//        }
//    }
//
//
//    private String getCurrTime(int initalTime) {
//        String time = "";
//        int millis = initalTime +  (int) (System.currentTimeMillis()) - startTime;
//        currTime = millis;
//        int sec = millis/1000;
//        millis%=1000;
//        millis/=10;
//        if (sec >= 60) {
//            int min = sec / 60;
//            sec %= 60;
//            min %= 60;
//            if (min < 10) {
//                time += "0" + min + ":";
//            } else {
//                time += min + ":";
//            }
//        } else {
//            time += "00:";
//        }
//        if (sec < 10) {
//            time += "0" + sec + ".";
//        } else {
//            time += sec + ".";
//        }
//        if(millis<10){
//            time += "0";
//        }
//        time += "" + millis;
//        return time;
//    }
//
//
//    private void startTimer() {
//        myTimer = new Timer();
//        int initalTime = currTime;
//        myTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                HoneywellScanActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (maxCount > 0 && currCount >= maxCount) {
//                            isTimerOn = false;
//                            myTimer.cancel();
//                            return;
//                        }
//                        timer.setText("TIME: " + getCurrTime(initalTime));
//                    }
//                });
//            }
//        }, 0, 10);
//    }
//
//
//    private void setUp() {
//        soundEnabled = true;
//        mode = getIntent().getIntExtra("mode", 0);
//        scannedData = new ArrayList<>();
//        scannedItems = new ArrayList<>();
//        currCount = 0;
//        maxCount = -1;
//        startTime = -1;
//        isTimerOn = false;
//        currTime = 0;
//        setContentView(R.layout.scan_screen);
//        counter = (TextView) findViewById(R.id.counter);
//        timer = (TextView) findViewById(R.id.timer);
//        barcodeList = (ListView) findViewById(R.id.listViewBarcodeData);
//        sonicSound = MediaPlayer.create(getApplicationContext(), R.raw.sonic_sound);
//        sonicSound2 = MediaPlayer.create(getApplicationContext(), R.raw.sonic_sound);
//        sonicSound3 = MediaPlayer.create(getApplicationContext(), R.raw.sonic_sound);
//        sonicDeathSound = MediaPlayer.create(getApplicationContext(), R.raw.sonic_death_sound);
//        sonicDeathSound2 = MediaPlayer.create(getApplicationContext(), R.raw.sonic_death_sound);
//        sonicTallySound = MediaPlayer.create(getApplicationContext(), R.raw.sonic_tally_sound);
//    }
//    private void secondSetUp() {
//        if (sharedPref.getBoolean("timer", false)) {
//            timer.setVisibility(View.VISIBLE);
//        } else {
//            timer.setVisibility(View.INVISIBLE);
//            startTime = -1;
//            timer.setText(R.string.time);
//        }
//        maxCount = sharedPref.getInt("count", -1);
//        if (maxCount != -1) {
//            counter.setVisibility(View.VISIBLE);
//            setCounter();
//        } else {
//            counter.setVisibility(View.INVISIBLE);
//        }
//        soundEnabled = sharedPref.getBoolean("sound", true);
//    }
//
//
//    //endregion
//}
//
