package com.honeywell.barcodeexample;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.honeywell.barcodeexample.R;

public class StartActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        try {
            super.onCreate(savedInstanceState);

            //reset all memory upon reloading the app
            sharedPref.edit().clear().commit();

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
        } catch(Error e) {
            System.out.println(e.getMessage());
            Toast toast = Toast.makeText(getApplicationContext(), "error starting event: "+e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }




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
