package com.honeywell.barcodeexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.sql.Array;
import java.util.ArrayList;

public class AnalysisActivity extends Activity {
    private Button backButton;
    private ListView barcodeList;
    private ArrayList<String> dataList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis_screen);
        barcodeList = (ListView) findViewById(R.id.listViewBarcodeData);
        dataList = getIntent().getStringArrayListExtra("data");
        String numericDecodedData = dataList.get(0);
        while (numericDecodedData.indexOf("\u001D") >= 0) {
            numericDecodedData = numericDecodedData.substring(0, numericDecodedData.indexOf("\u001D")) + numericDecodedData.substring(numericDecodedData.indexOf("\u001D") + 1);
        }
        dataList.set(0, numericDecodedData);
        final ArrayAdapter<String> analysisData = new ArrayAdapter<String>(AnalysisActivity.this, R.layout.list_layout, dataList);
        barcodeList.setAdapter(analysisData);
        ActivitySetting();
        GS1Analysis(dataList.get(0));
    }

    public void ActivitySetting() {
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void GS1Analysis(String code) {
        String[][] identifiers = new String[75][4];  //[0][x]=AI     [x][1]=data name    [x][2]=fixed, var length      [x][3]=length or min/max
        boolean validGS1 = true;
        //region
        identifiers[0][0] = "00"; //first digit is an extension digit and the 18th digit is the check digit
        identifiers[0][1] = "SSCC";
        identifiers[0][2] = "fixed";
        identifiers[0][3] = "18";

        identifiers[1][0] = "01";
        identifiers[1][1] = "GTIN";
        identifiers[1][2] = "fixed";
        identifiers[1][3] = "14";  //gtin       last digit is check digit

        identifiers[2][0] = "02";
        identifiers[2][1] = "CONTENT";
        identifiers[2][2] = "fixed";
        identifiers[2][3] = "14";   //gtin       last digit is check digit

        identifiers[3][0] = "10";
        identifiers[3][1] = "BATCH/LOT";
        identifiers[3][2] = "var";
        identifiers[3][3] = "1 20";

        //region dates
        identifiers[4][0] = "11";                       //dates are in year month day format
        identifiers[4][1] = "PROD DATE";
        identifiers[4][2] = "fixed";
        identifiers[4][3] = "6";

        identifiers[5][0] = "12";
        identifiers[5][1] = "DUE DATE";
        identifiers[5][2] = "fixed";
        identifiers[5][3] = "6";

        identifiers[6][0] = "13";
        identifiers[6][1] = "PACK DATE";
        identifiers[6][2] = "fixed";
        identifiers[6][3] = "6";

        identifiers[7][0] = "15";
        identifiers[7][1] = "BEST BY";
        identifiers[7][2] = "fixed";
        identifiers[7][3] = "6";

        identifiers[8][0] = "16";
        identifiers[8][1] = "SELL BY";
        identifiers[8][2] = "fixed";
        identifiers[8][3] = "6";

        identifiers[9][0] = "17";
        identifiers[9][1] = "USE BY";
        identifiers[9][2] = "fixed";
        identifiers[9][3] = "6";
        //endregion

        identifiers[10][0] = "20";
        identifiers[10][1] = "VARIANT";
        identifiers[10][2] = "fixed";
        identifiers[10][3] = "2";

        identifiers[11][0] = "21";
        identifiers[11][1] = "SERIAL";
        identifiers[11][2] = "var";
        identifiers[11][3] = "1 20";

        identifiers[12][0] = "235";
        identifiers[12][1] = "TPX";
        identifiers[12][2] = "var";
        identifiers[12][3] = "1 28";

        identifiers[13][0] = "240";
        identifiers[13][1] = "ADDITIONAL ID";
        identifiers[13][2] = "var";
        identifiers[13][3] = "1 30";

        identifiers[14][0] = "241";
        identifiers[14][1] = "CUST. PART No.";
        identifiers[14][2] = "var";
        identifiers[14][3] = "1 30";

        identifiers[15][0] = "242";
        identifiers[15][1] = "MTO VARIANT";
        identifiers[15][2] = "var";
        identifiers[15][3] = "1 6";

        identifiers[16][0] = "243";
        identifiers[16][1] = "PCN";
        identifiers[16][2] = "var";
        identifiers[16][3] = "1 20";

        identifiers[17][0] = "250";
        identifiers[17][1] = "SECONDARY SERIAL";
        identifiers[17][2] = "var";
        identifiers[17][3] = "1 30";

        identifiers[18][0] = "251";
        identifiers[18][1] = "REF. TO SOURCE";
        identifiers[18][2] = "var";
        identifiers[18][3] = "1 30";

        identifiers[19][0] = "253"; //pg 162 has optional serial component???
        identifiers[19][1] = "GDTI";
        identifiers[19][2] = "fixed";
        identifiers[19][3] = "13"; //13 is check digit

        identifiers[20][0] = "254";
        identifiers[20][1] = "GLN EXTENSION COMPONENT";
        identifiers[20][2] = "var";
        identifiers[20][3] = "1 20";

        identifiers[21][0] = "255";//pg 163 optional serial component
        identifiers[21][1] = "GCN";
        identifiers[21][2] = "fixed";
        identifiers[21][3] = "13";//13 is check digit

        identifiers[22][0] = "30";
        identifiers[22][1] = "VAR. COUNT";
        identifiers[22][2] = "var";
        identifiers[22][3] = "1 8";

        identifiers[23][0] = "310n";
        identifiers[23][1] = "Net weight(kg)";
        identifiers[23][2] = "fixed";
        identifiers[23][3] = "6";

        identifiers[24][0] = "311n";
        identifiers[24][1] = "Length or first dimension(m)";
        identifiers[24][2] = "fixed";
        identifiers[24][3] = "6";

        identifiers[25][0] = "312n";
        identifiers[25][1] = "Width, diameter, or second dimension(m)";
        identifiers[25][2] = "fixed";
        identifiers[25][3] = "6";

        identifiers[26][0] = "313n";
        identifiers[26][1] = "Depth, thickness, height, or third dimension(m)";
        identifiers[26][2] = "fixed";
        identifiers[26][3] = "6";

        identifiers[27][0] = "314n";
        identifiers[27][1] = "Area(m^2)";
        identifiers[27][2] = "fixed";
        identifiers[27][3] = "6";

        identifiers[28][0] = "315n";
        identifiers[28][1] = "Net Volume(L)";
        identifiers[28][2] = "fixed";
        identifiers[28][3] = "6";

        identifiers[29][0] = "316n";
        identifiers[29][1] = "Net Volume(m^3)";
        identifiers[29][2] = "fixed";
        identifiers[29][3] = "6";

        identifiers[30][0] = "320n";
        identifiers[30][1] = "Net Weight(lbs)";
        identifiers[30][2] = "fixed";
        identifiers[30][3] = "6";

        identifiers[31][0] = "321n";
        identifiers[31][1] = "Length or first dimension(in)";
        identifiers[31][2] = "fixed";
        identifiers[31][3] = "6";

        identifiers[32][0] = "322n";
        identifiers[32][1] = "Length or first dimension(ft)";
        identifiers[32][2] = "fixed";
        identifiers[32][3] = "6";

        identifiers[33][0] = "322n";
        identifiers[33][1] = "Length or first dimension(yd)";
        identifiers[33][2] = "fixed";
        identifiers[33][3] = "6";

        identifiers[34][0] = "324n";
        identifiers[34][1] = "Width, diameter, or second dimension(in)";
        identifiers[34][2] = "fixed";
        identifiers[34][3] = "6";

        identifiers[35][0] = "325n";
        identifiers[35][1] = "Width, diameter, or second dimension(ft)";
        identifiers[35][2] = "fixed";
        identifiers[35][3] = "6";

        identifiers[36][0] = "326n";
        identifiers[36][1] = "Width, diameter, or second dimension(yd)";
        identifiers[36][2] = "fixed";
        identifiers[36][3] = "6";

        identifiers[37][0] = "327n";
        identifiers[37][1] = "Depth, thickness, height, or third dimension(in)";
        identifiers[37][2] = "fixed";
        identifiers[37][3] = "6";

        identifiers[38][0] = "328n";
        identifiers[38][1] = "Depth, thickness, height, or third dimension(ft)";
        identifiers[38][2] = "fixed";
        identifiers[38][3] = "6";

        identifiers[39][0] = "329n";
        identifiers[39][1] = "Depth, thickness, height, or third dimension(yd)";
        identifiers[39][2] = "fixed";
        identifiers[39][3] = "6";

        identifiers[40][0] = "350n";
        identifiers[40][1] = "Area(in^2)";
        identifiers[40][2] = "fixed";
        identifiers[40][3] = "6";

        identifiers[41][0] = "351n";
        identifiers[41][1] = "Area(ft^2)";
        identifiers[41][2] = "fixed";
        identifiers[41][3] = "6";

        identifiers[42][0] = "352n";
        identifiers[42][1] = "Area(yd^2)";
        identifiers[42][2] = "fixed";
        identifiers[42][3] = "6";

        identifiers[43][0] = "356n";
        identifiers[43][1] = "Net weight(Troy ounces)";
        identifiers[43][2] = "fixed";
        identifiers[43][3] = "6";

        identifiers[44][0] = "357n";
        identifiers[44][1] = "Net weight(ounces)";
        identifiers[44][2] = "fixed";
        identifiers[44][3] = "6";

        identifiers[45][0] = "360n";
        identifiers[45][1] = "Net volume(quarts)";
        identifiers[45][2] = "fixed";
        identifiers[45][3] = "6";

        identifiers[46][0] = "361n";
        identifiers[46][1] = "Net volume(gallons(US))";
        identifiers[46][2] = "fixed";
        identifiers[46][3] = "6";

        identifiers[47][0] = "364n";
        identifiers[47][1] = "Net volume(in^3)";
        identifiers[47][2] = "fixed";
        identifiers[47][3] = "6";

        identifiers[48][0] = "365n";
        identifiers[48][1] = "Net volume(ft^3)";
        identifiers[48][2] = "fixed";
        identifiers[48][3] = "6";

        identifiers[49][0] = "366n";
        identifiers[49][1] = "Net volume(yd^3)";
        identifiers[49][2] = "fixed";
        identifiers[49][3] = "6";

        identifiers[50][0] = "330n";
        identifiers[50][1] = "Logistic weight(kg)";
        identifiers[50][2] = "fixed";
        identifiers[50][3] = "6";

        identifiers[51][0] = "331n";
        identifiers[51][1] = "Length or first dimension(m)";
        identifiers[51][2] = "fixed";
        identifiers[51][3] = "6";

        identifiers[52][0] = "332n";
        identifiers[52][1] = "Width, diameter, or second dimension(m)";
        identifiers[52][2] = "fixed";
        identifiers[52][3] = "6";

        identifiers[53][0] = "333n";
        identifiers[53][1] = "Depth, thickness, height, or third dimension(m)";
        identifiers[53][2] = "fixed";
        identifiers[53][3] = "6";

        identifiers[54][0] = "334n";
        identifiers[54][1] = "Area(m^2)";
        identifiers[54][2] = "fixed";
        identifiers[54][3] = "6";

        identifiers[55][0] = "335n";
        identifiers[55][1] = "Logistic Volume(L)";
        identifiers[55][2] = "fixed";
        identifiers[55][3] = "6";

        identifiers[56][0] = "336n";
        identifiers[56][1] = "Logistic Volume(m^3)";
        identifiers[56][2] = "fixed";
        identifiers[56][3] = "6";

        identifiers[57][0] = "340n";
        identifiers[57][1] = "Logistic Weight(lbs)";
        identifiers[57][2] = "fixed";
        identifiers[57][3] = "6";

        identifiers[58][0] = "341n";
        identifiers[58][1] = "Length or first dimension(in)";
        identifiers[58][2] = "fixed";
        identifiers[58][3] = "6";

        identifiers[59][0] = "342n";
        identifiers[59][1] = "Length or first dimension(ft)";
        identifiers[59][2] = "fixed";
        identifiers[59][3] = "6";

        identifiers[60][0] = "342n";
        identifiers[60][1] = "Length or first dimension(yd)";
        identifiers[60][2] = "fixed";
        identifiers[60][3] = "6";

        identifiers[61][0] = "344n";
        identifiers[61][1] = "Width, diameter, or second dimension(in)";
        identifiers[61][2] = "fixed";
        identifiers[61][3] = "6";

        identifiers[62][0] = "345n";
        identifiers[62][1] = "Width, diameter, or second dimension(ft)";
        identifiers[62][2] = "fixed";
        identifiers[62][3] = "6";

        identifiers[63][0] = "346n";
        identifiers[63][1] = "Width, diameter, or second dimension(yd)";
        identifiers[63][2] = "fixed";
        identifiers[63][3] = "6";

        identifiers[64][0] = "347n";
        identifiers[64][1] = "Depth, thickness, height, or third dimension(in)";
        identifiers[64][2] = "fixed";
        identifiers[64][3] = "6";

        identifiers[65][0] = "348n";
        identifiers[65][1] = "Depth, thickness, height, or third dimension(ft)";
        identifiers[65][2] = "fixed";
        identifiers[65][3] = "6";

        identifiers[66][0] = "349n";
        identifiers[66][1] = "Depth, thickness, height, or third dimension(yd)";
        identifiers[66][2] = "fixed";
        identifiers[66][3] = "6";

        identifiers[67][0] = "353n";
        identifiers[67][1] = "Area(in^2)";
        identifiers[67][2] = "fixed";
        identifiers[67][3] = "6";

        identifiers[68][0] = "354n";
        identifiers[68][1] = "Area(ft^2)";
        identifiers[68][2] = "fixed";
        identifiers[68][3] = "6";

        identifiers[69][0] = "355n";
        identifiers[69][1] = "Area(yd^2)";
        identifiers[69][2] = "fixed";
        identifiers[69][3] = "6";

        identifiers[70][0] = "362n";
        identifiers[70][1] = "Logistic volume(quarts)";
        identifiers[70][2] = "fixed";
        identifiers[70][3] = "6";

        identifiers[71][0] = "363n";
        identifiers[71][1] = "Logistic volume(gallons(US))";
        identifiers[71][2] = "fixed";
        identifiers[71][3] = "6";

        identifiers[72][0] = "367n";
        identifiers[72][1] = "Logistic volume(in^3)";
        identifiers[72][2] = "fixed";
        identifiers[72][3] = "6";

        identifiers[73][0] = "368n";
        identifiers[73][1] = "Logistic volume(ft^3)";
        identifiers[73][2] = "fixed";
        identifiers[73][3] = "6";

        identifiers[74][0] = "369n";
        identifiers[74][1] = "Logistic volume(yd^3)";
        identifiers[74][2] = "fixed";
        identifiers[74][3] = "6";
        //endregion
        String field = "";
        String ai = "";
        String gs = "\u001D";
        for (int i = 0; i < code.length(); i++) {
            if ("0123456789".contains(code.substring(i, i + 1))) {
                ai += code.charAt(i);
                if(ai.length()>4){
                    return;
                }
                for (int ident = 0; ident < identifiers.length; ident++) {
                    if (identifiers[ident][0].equals(ai)) {
                        String info = identifiers[ident][1] + ": ";
                        if (identifiers[ident][2].equals("fixed")) {
                            if (code.length() >= i + 1 + Integer.parseInt(identifiers[ident][3])) {
                                if (ident >= 4 && ident <= 9) {
                                    String date = date(Integer.parseInt(code.substring(i + 1, i + 1 + Integer.parseInt(identifiers[ident][3]))));
                                    if(date.equals("invalid")){
                                        dataList.add("Invalid GS1 Barcode");
                                        final ArrayAdapter<String> analysisData = new ArrayAdapter<String>(AnalysisActivity.this, R.layout.list_layout, dataList);
                                        barcodeList.setAdapter(analysisData);
                                        return;
                                    }
                                    info += date;
                                } else {
                                    info += code.substring(i + 1, i + 1 + Integer.parseInt(identifiers[ident][3]));
                                }
                                i += Integer.parseInt(identifiers[ident][3]);
                            } else {
                                info += "error";
                            }
                        } else if (identifiers[ident][2].equals("var")) {
                            int delim;
                            String str = "";
                            if (code.substring(i).contains(gs)) {
                                delim = code.substring(i).indexOf(gs) + i - 1;
                                for (int n = i + 1; n < code.length() && n <= delim; n++) {
                                    str += code.charAt(n);
                                }
                                i = delim;
                            } else {
                                str += code.substring(i + 1);
                                i = code.length();
                            }
                            if(str.length()>Integer.parseInt(identifiers[ident][3].substring(identifiers[ident][3].indexOf(" ")+1))){
                                dataList.add("Invalid GS1 Barcode");
                                final ArrayAdapter<String> analysisData = new ArrayAdapter<String>(AnalysisActivity.this, R.layout.list_layout, dataList);
                                barcodeList.setAdapter(analysisData);
                                return;
                            }
                            info += str;
                        }
                        dataList.add(info);
                        ai = "";
                        info = "";
                        ident = identifiers.length;
                    } else if (identifiers[ident][0].equals(ai + "n")) {
                        String info = identifiers[ident][1] + ": ";
                        if (code.length() >= i + 2 + Integer.parseInt(identifiers[ident][3])) {
                            int decimal = Integer.parseInt("" + code.charAt(i + 1));
                            double num = (double) Integer.parseInt(code.substring(i + 2, i + 2 + Integer.parseInt(identifiers[ident][3])));
                            info += num / (Math.pow(10, decimal));
                            i += 1 + Integer.parseInt(identifiers[ident][3]);
                        } else {
                            info += "error";
                        }
                        dataList.add(info);
                        ai = "";
                        info = "";
                        ident = identifiers.length;
                    }
                }
            }
        }
        final ArrayAdapter<String> analysisData = new ArrayAdapter<String>(AnalysisActivity.this, R.layout.list_layout, dataList);
        barcodeList.setAdapter(analysisData);
    }

    private String date(int yymmdd) {
        String date = "";
        String[] months = {" January", " February", " March", " April", " May", " June", " July", " August", " September", " October", " November", " December"};
        int day = yymmdd % 100;
        yymmdd /= 100;
        int mon = yymmdd % 100;
        yymmdd /= 100;
        int year = yymmdd % 100;
        if(mon>12 || day>31){
            return "invalid";
        }
        date += "20";
        if (year < 10) {
            date += "0";
        }
        date += year;
        date += months[mon - 1];
        if (day > 0) {
            date += " " + day;
        }
        return date;
    }
}
