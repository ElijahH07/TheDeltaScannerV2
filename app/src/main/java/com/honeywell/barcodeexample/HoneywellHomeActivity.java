package com.honeywell.barcodeexample;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.AidcManager.CreatedCallback;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.InvalidScannerNameException;

public class HoneywellHomeActivity extends BaseActivity {
//region
    private static BarcodeReader barcodeReader;
    //private com.honeywell.rfidservice.rfid.RfidReader rfidreader;
    private AidcManager manager;
    private Button btnScannerSelectBarcode;
    private Button paintButton;
    private Button scanButton;
    private Button rfidButton;
    private Button swiftScanner;
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.home_screen);
            Toast toast = Toast.makeText(getApplicationContext(), "honeywell", Toast.LENGTH_SHORT);
            toast.show();
            //depending on the model, show diff orientations
            if(Build.MODEL.startsWith("VM1A")) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            else{
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            // create the AidcManager providing a Context and a
            // CreatedCallback implementation.
            AidcManager.create(this, new CreatedCallback() {

                @Override
                public void onCreated(AidcManager aidcManager) {
                    manager = aidcManager;
                    //try catch function that results from exceptions
                    try{
                        barcodeReader = manager.createBarcodeReader();
                    }
                    catch (InvalidScannerNameException e){
                        Toast.makeText(HoneywellHomeActivity.this, "Invalid Scanner Name Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        Toast.makeText(HoneywellHomeActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ActivitySetting();
        } catch(Error e) {
            System.out.println(e.getMessage());
            Toast toast = Toast.makeText(getApplicationContext(), "error during Honeywell Scan Activity: "+e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    static BarcodeReader getBarcodeObject() {
        return barcodeReader;
    }

    public void ActivitySetting() {
        //creates the Scanner Select button which allows user to choose what scanner to use
        btnScannerSelectBarcode = (Button) findViewById(R.id.buttonScannerSelectBarcode);
        btnScannerSelectBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent(
                        "android.intent.action.SCANNERSELECTBARCODEACTIVITY");
                startActivity(barcodeIntent);
            }
        });

        swiftScanner = (Button) findViewById(R.id.swiftScanner);
        swiftScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent swiftIntent = new Intent("android.intent.action.SWIFTSCANNERACTIVITY");
                startActivity(swiftIntent);
            }
        });

        //paint button allows user to rapidly log barcodes
        paintButton = (Button) findViewById(R.id.paint);
        paintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent intent = new Intent("android.intent.action.HONEYSCAN");
                intent.putExtra("mode", 0);
                startActivity(intent);
            }
        });

        //scan button allows user to log barcodes
        scanButton = (Button) findViewById(R.id.scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent intent = new Intent("android.intent.action.HONEYSCAN");
                intent.putExtra("mode", 1);
                startActivity(intent);
            }
        });


    }

//once cleared, cleans and closes resources
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (barcodeReader != null) {
            // close BarcodeReader to clean up resources.
            barcodeReader.close();
            barcodeReader = null;
        }

        if (manager != null) {
            // close AidcManager to disconnect from the scanner service.
            // once closed, the object can no longer be used.
            manager.close();
        }
    }
}
