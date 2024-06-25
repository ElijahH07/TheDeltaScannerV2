package com.honeywell.barcodeexample;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;

public class ZebraHomeActivity extends BaseActivity {
    //region
    private Button btnScannerSelectBarcode;
    private Button paintButton;
    private Button scanButton;

    private Button rfidButton;
    private Button swiftScanner;
    private Button thingButton;
    private static final String PROFILE_NAME = "DeltaOneInternProject";
    private static final String ACTION_DATAWEDGE = "com.symbol.datawedge.api.ACTION";
    private static final String EXTRA_CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE";
    private static final String EXTRA_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG";
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.home_screen);

            CreateDWProfile(this);

            ActivitySetting();
        }catch(Error e) {

            System.out.println(e.getMessage());
            Toast toast = Toast.makeText(getApplicationContext(), "Error in Home Activity: "+e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    public void CreateDWProfile(Context context) {
        try {
            //  Requires DataWedge 6.4

            //  Now configure that created profile to apply to our application
            Bundle profileConfig = new Bundle(); //profileConfig is the main Bundle which holds PARAM_LIST Bundle and PLUGIN_CONFIG Bundle
            profileConfig.putString("PROFILE_NAME", PROFILE_NAME);
            profileConfig.putString("PROFILE_ENABLED", "true"); //  Seems these are all strings
            profileConfig.putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST"); // creates if not exist, updates if exists

            Bundle appConfig = new Bundle();
            appConfig.putString("PACKAGE_NAME", context.getPackageName());      //  Associate the profile with this app
            appConfig.putStringArray("ACTIVITY_LIST", new String[]{"*"});
            profileConfig.putParcelableArray("APP_LIST", new Bundle[]{appConfig});
            sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);

            //  You can only configure one plugin at a time, we have done the barcode input, now do the intent output
            profileConfig.remove("PLUGIN_CONFIG");
            Bundle intentConfig = new Bundle();
            intentConfig.putString("PLUGIN_NAME", "INTENT");
            intentConfig.putString("RESET_CONFIG", "true");
            Bundle intentProps = new Bundle();
            intentProps.putString("intent_output_enabled", "true");
            intentProps.putString("intent_action", context.getResources().getString(R.string.activity_intent_filter_action)); // I DONT KNOW WHAT TO PUT YET (it works now)
            intentProps.putString("intent_delivery", "2");  //  broadcast intent
            intentConfig.putBundle("PARAM_LIST", intentProps);
            profileConfig.putBundle("PLUGIN_CONFIG", intentConfig);
            sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);

            //  Disable keyboard output
            profileConfig.remove("PLUGIN_CONFIG");
            Bundle keystrokeConfig = new Bundle();
            keystrokeConfig.putString("PLUGIN_NAME", "KEYSTROKE");
            keystrokeConfig.putString("RESET_CONFIG", "true");
            Bundle keystrokeProps = new Bundle();
            keystrokeProps.putString("keystroke_output_enabled", "false");
            keystrokeConfig.putBundle("PARAM_LIST", keystrokeProps);
            profileConfig.putBundle("PLUGIN_CONFIG", keystrokeConfig);
            sendDataWedgeIntentWithExtra(context, ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig);
        } catch(Error e) {
            System.out.println(e.getMessage());
            Toast toast = Toast.makeText(getApplicationContext(), "Error creating DW Profile: "+e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

    }
    public void ActivitySetting() {
        try {

            rfidButton = (Button)findViewById(R.id.RFID);
            rfidButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get the intent action string from AndroidManifest.xml
                    Intent rfidIntent = new Intent("android.intent.action.ZEBRARFIDSELECTACTIVITY");
                    startActivity(rfidIntent);
                }
            });



            btnScannerSelectBarcode = (Button) findViewById(R.id.buttonScannerSelectBarcode);
            btnScannerSelectBarcode.setVisibility(View.INVISIBLE);

            paintButton = (Button) findViewById(R.id.paint);
            paintButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get the intent action string from AndroidManifest.xml
                    Intent intent = new Intent("android.intent.action.ZEBRASCAN");
                    intent.putExtra("mode", 1);
                    startActivity(intent);
                }
            });
            scanButton = (Button) findViewById(R.id.scan);
            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get the intent action string from AndroidManifest.xml
                    Intent intent = new Intent("android.intent.action.ZEBRASCAN");
                    intent.putExtra("mode", 0);
                    startActivity(intent);
                }
            });

            swiftScanner = (Button) findViewById(R.id.swiftScanner);
            swiftScanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Does not have Camera", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        } catch(Error e) {
            System.out.println(e.getMessage());
            Toast toast = Toast.makeText(getApplicationContext(), "error setting up Zebra Home Page: "+e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}
