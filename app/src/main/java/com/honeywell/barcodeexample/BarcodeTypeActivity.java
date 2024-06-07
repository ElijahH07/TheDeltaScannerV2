package com.honeywell.barcodeexample;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;


public class BarcodeTypeActivity extends BaseActivity {
    protected boolean setInHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get sharedPref
//        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);


        boolean defaultValue = true; //checkBoxes are defaulted to checked

        setContentView(R.layout.activity_barcode_type);


        Button button;
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // check boxes

        CheckBox UPC_EAN;
        UPC_EAN = findViewById(R.id.checkBox9);

        UPC_EAN.setChecked(sharedPref.getBoolean("UPC/EAN", defaultValue));
        UPC_EAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean("UPC/EAN", UPC_EAN.isChecked());

                editor.apply();

            }
        });

        CheckBox GS1_128;
        GS1_128 = findViewById(R.id.checkBox);

        GS1_128.setChecked(sharedPref.getBoolean("GS1-128", defaultValue));
        GS1_128.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean("GS1-128", GS1_128.isChecked());

                editor.apply();
            }
        });

        CheckBox Code128;
        Code128 = findViewById(R.id.checkBox2);

        Code128.setChecked(sharedPref.getBoolean("Code 128", defaultValue));
        Code128.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean("Code 128", Code128.isChecked());

                editor.apply();
            }
        });

        CheckBox Code39;
        Code39 = findViewById(R.id.checkBox3);

        Code39.setChecked(sharedPref.getBoolean("Code 39", defaultValue));
        Code39.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean("Code 39", Code39.isChecked());

                editor.apply();
            }
        });

        CheckBox DataMatrix;
        DataMatrix = findViewById(R.id.checkBox4);

        DataMatrix.setChecked(sharedPref.getBoolean("DataMatrix", defaultValue));
        DataMatrix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean("DataMatrix", DataMatrix.isChecked());

                editor.apply();
            }
        });

        CheckBox PDF_417;
        PDF_417 = findViewById(R.id.checkBox5);

        PDF_417.setChecked(sharedPref.getBoolean("PDF-417", defaultValue));
        PDF_417.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean("PDF-417", PDF_417.isChecked());

                editor.apply();
            }
        });

        CheckBox QR;
        QR = findViewById(R.id.checkBox6);

        QR.setChecked(sharedPref.getBoolean("QR", defaultValue));
        QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean("QR", QR.isChecked());

                editor.apply();
            }
        });

        CheckBox I;
        I = findViewById(R.id.checkBox7);

        I.setChecked(sharedPref.getBoolean("I. 2 of 5", defaultValue));
        I.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean("I. 2 of 5", I.isChecked());

                editor.apply();
            }
        });

        CheckBox Aztec;
        Aztec = findViewById(R.id.checkBox8);

        Aztec.setChecked(sharedPref.getBoolean("Aztec", defaultValue));
        Aztec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean("Aztec", Aztec.isChecked());

                editor.apply();
            }
        });
    }
}