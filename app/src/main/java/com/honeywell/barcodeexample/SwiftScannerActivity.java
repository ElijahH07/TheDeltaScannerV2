package com.honeywell.barcodeexample;

import com.honeywell.barcode.HSMDecodeResult;
import com.honeywell.barcode.HSMDecoder;
import com.honeywell.barcode.Symbology;
import com.honeywell.barcodeexample.BaseActivity;
import com.honeywell.license.ActivationManager;
import com.honeywell.license.ActivationResult;
import com.honeywell.plugins.PluginResultListener;
import com.honeywell.plugins.decode.DecodeResultListener;
//import com.honeywell.barcodeexample.plugin.MyCustomPlugin;
//import com.honeywell.barcodeexample.plugin.MyCustomPluginResultListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.os.Build;
import android.Manifest;
public class SwiftScannerActivity extends BaseActivity implements DecodeResultListener
{
    private HSMDecoder hsmDecoder;
    private TextView tvResult, tvSymb, tvLength, tvDecTime, tvDeviceId;
    private ImageView ivDecode;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    public  String entitlementID = BuildConfig.SWIFT_DECODER_API_KEY;

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    onBackPressed();
                }
                return;
            }
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);

            //initialize GUI
            initGuiElements();

            LinearLayout mainLayout = findViewById(R.id.main_layout);
            //activate the API with your license key
            ActivationResult activationResult = ActivationManager.activateEntitlement(this, entitlementID);
            Toast.makeText(this, "Activation Result: " + activationResult, Toast.LENGTH_LONG).show();

            //get the singleton instance of the decoder
            hsmDecoder = HSMDecoder.getInstance(this);

            //set all decoder related settings
            hsmDecoder.enableSymbology(Symbology.UPCA);
            hsmDecoder.enableSymbology(Symbology.CODE128);
            hsmDecoder.enableSymbology(Symbology.CODE39);
            hsmDecoder.enableSymbology(Symbology.QR);
            hsmDecoder.enableFlashOnDecode(false);
            hsmDecoder.enableSound(true);
            hsmDecoder.enableAimer(true);
            hsmDecoder.setAimerColor(Color.RED);
            hsmDecoder.setOverlayText("Place barcode completely inside viewfinder!");
            hsmDecoder.setOverlayTextColor(Color.WHITE);
            hsmDecoder.addResultListener(this);

            //instantiate custom plug-in and set this activity as a listener
            //customPlugin = new MyCustomPlugin(getApplicationContext());
            //customPlugin.addResultListener(this);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        //dispose of the decoder instance, this stops the underlying camera service and releases all associated resources
        HSMDecoder.disposeInstance();

        //dispose of you custom plug-in
        //customPlugin.dispose();
    }

    @Override
    public void onHSMDecodeResult(HSMDecodeResult[] barcodeData)
    {
        //handle results from the default decoding functionality
        displayBarcodeData(barcodeData);
    }


    private void displayBarcodeData(final HSMDecodeResult[] barcodeData)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(barcodeData.length > 0)
                {
                    HSMDecodeResult firstResult = barcodeData[0];
                    System.out.println("firstResult.toString() = " + firstResult.getBarcodeData());
                    tvResult.setText("Result: " + firstResult.getBarcodeData());
                    tvSymb.setText("Symbology: " + firstResult.getSymbology());
                    tvLength.setText("Length: " + firstResult.getBarcodeDataLength());
                    tvDecTime.setText("Decode Time: " + firstResult.getDecodeTime() + "ms");
                    ivDecode.setImageBitmap(hsmDecoder.getLastBarcodeImage(firstResult.getBarcodeBounds()));
                }
            }
        });

    }

    private void initGuiElements()
    {
        //stop the device from going to sleep and hide the title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //inflate the base UI layer
        setContentView(R.layout.main_swift_decoder);

        Button buttonDecode = (Button)findViewById(R.id.buttonDecode);
        buttonDecode.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //let HSMDecoder handle the decoding UI for you
                hsmDecoder.scanBarcode();
            }
        });

        Button buttonDecodeComponent = (Button)findViewById(R.id.buttonDecodeLocal);
        buttonDecodeComponent.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //create the DecodeComponentActivity intent
                Intent decActivityIntent = new Intent("android.intent.action.RAPIDDECODEACTIVITY");


                //start our own activity with an embedded HSMDecodeComponent for greater customization
                startActivity(decActivityIntent);
            }
        });

        tvResult = (TextView)findViewById(R.id.textViewRes);
        tvSymb = (TextView)findViewById(R.id.textViewSymb);
        tvLength = (TextView)findViewById(R.id.textViewLength);
        tvDecTime = (TextView)findViewById(R.id.textViewDecTime);
        ivDecode = (ImageView)findViewById(R.id.imageViewDec);
        tvDeviceId = (TextView)findViewById(R.id.textViewdeviceId);
        tvDeviceId.setText("Device Id: "+getDeviceID());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
                return;
            }
        }


    }


    public String getDeviceID(){
        return ActivationManager.getDeviceId(this,entitlementID);
    }
}