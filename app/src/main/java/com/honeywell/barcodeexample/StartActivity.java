package com.honeywell.barcodeexample;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.honeywell.barcodeexample.R;

public class StartActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        int device = GetAndroidMacFromManufactureAPI();
        if(device==0){
            Toast toast = Toast.makeText(getApplicationContext(), "honeywell", Toast.LENGTH_LONG);
            toast.show();
            startActivity(new Intent("android.intent.action.HONEYHOME"));
        }
        else if (device==1) {
            Toast toast = Toast.makeText(getApplicationContext(), "zebra", Toast.LENGTH_LONG);
            toast.show();
            startActivity(new Intent("android.intent.action.ZEBRAHOME"));
        }
        setContentView(R.layout.incompatible_device);
    }

    public static int GetAndroidMacFromManufactureAPI() {
        try
        {
            String manufacturer =  Build.MANUFACTURER;
            if (manufacturer.equals("Honeywell") || manufacturer.equals("Universal Global Scientific Industrial Co., Ltd.")) {
                return 0;
            }
            else if (manufacturer.equals("Zebra Technologies")){
                return 1;
            }
        }
        catch (Exception ex)
        {
            try {
                throw ex;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return -1;
    }
}
