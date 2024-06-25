package com.honeywell.barcodeexample;

import com.honeywell.barcode.CaptureRequestBuilderListener;
import com.honeywell.barcode.HSMDecodeComponent;
import com.honeywell.barcode.HSMDecodeResult;
import com.honeywell.barcode.HSMDecoder;
import com.honeywell.plugins.decode.DecodeResultListener;
import com.honeywell.misc.HSMLog;
import java.util.regex.*;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.camera2.CaptureRequest;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.os.Build;
import android.widget.ListView;

import java.util.ArrayList;

public class RapidDecoderActivity extends Activity implements DecodeResultListener
{
    //private EditText editTextDisplay;
    private ListView barcodeList;
    private ArrayList<String> scannedData = new ArrayList<>();;
    ArrayAdapter<String> adapter;
    private HSMDecoder hsmDecoder;
    private HSMDecodeComponent decCom;
    private int scanCount = 0;
    private String barcodeString= " ";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //stop the device from going to sleep and hide the title bar
        setUp();
    }
    private void setUp() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.rapid_decoder);

        decCom = (HSMDecodeComponent)findViewById(R.id.hsm_decodeComponent);

        //editTextDisplay = (EditText)findViewById(R.id.editTextDisplay);
        //editTextDisplay.setEnabled(false);
        //editTextDisplay.setTextColor(Color.WHITE);
        //scannedData.add("hello");
        adapter = new ArrayAdapter<String>(RapidDecoderActivity.this, R.layout.list_layout, scannedData);
        barcodeList = (ListView) findViewById(R.id.listViewBarcodeData);
        barcodeList.setAdapter(adapter);

        barcodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Matcher matcher = Pattern.compile("Data: (\\d+)").matcher(scannedData.get(position));

                if (matcher.find()) {
                    Intent intent = new Intent("android.intent.action.ANALYSIS");
                    System.out.println(matcher.group(1));

                    intent.putExtra("swiftData", matcher.group(1));
                    startActivity(intent);
                }

            }
        });
        //get the singleton instance to HSMDecoder
        hsmDecoder = HSMDecoder.getInstance(this.getApplicationContext());

        decCom.enableScanning(true);
        scanCount = 0;
        hsmDecoder.addResultListener(this);

        hsmDecoder.setCaptureRequestBuilderListener(new CaptureRequestBuilderListener() {
            @Override
            public void OnCaptureRequestBuilderAvailable(CaptureRequest.Builder captureRequestBuilder) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    //Un-comment this code , if manual camera control is required
					/*
                    captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_OFF);
                    captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);
                    captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    captureRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, 400);
                    captureRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, (long) 41666666);
                    */

                }
            }
        });
    }
    @Override
    public void onStart()
    {
        super.onStart();


    }
    @Override
    public void onResume()
    {
        super.onResume();
        decCom.enableScanning(true);
        hsmDecoder.addResultListener(this);

    }

    @Override
    public void onStop()
    {
        super.onStop();
        decCom.enableScanning(false);
        hsmDecoder.setCaptureRequestBuilderListener(null);

        //we need to remove this activity as a listener each time we stop it, because our main activity can't disable default decoding if there are any active listeners

    }

    @Override
    public void onBackPressed() {
        HSMLog.trace();
        //System.out.println("editTextDisplay = " + editTextDisplay);
        hsmDecoder.removeResultListener(this);
        hsmDecoder.setCaptureRequestBuilderListener(null);

        try {
            finish();
        } catch (Exception e) {
            HSMLog.e(e);
        }
    }
    private void displayBarcodeData(final HSMDecodeResult[] barcodeData)
    {

        HSMDecodeResult firstResult = barcodeData[0];
        String msg = "Scan Count: " + ++scanCount + "\n\n" +
                "Data: " + firstResult.getBarcodeData() + "\n" +
                "Symbology: " + firstResult.getSymbology() + "\n" +
                "Length: " + firstResult.getBarcodeDataLength()  + "\n" +
                "Decode Time: " + firstResult.getDecodeTime() + "ms";

        if (!barcodeString.contains(firstResult.getBarcodeData())){
            scannedData.add(msg);
            System.out.println(scannedData);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
        barcodeString = barcodeString+firstResult.getBarcodeData();
    }
    @Override
    public void onHSMDecodeResult(HSMDecodeResult[] barcodeData){
        displayBarcodeData(barcodeData);
        // do something (application specific task)
    }
}