package com.example.parteek.accurateotp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    TextView txtresult;
    Button scan;
    Uri uri, imageUri;
    public Bitmap mbitmap;
    public static final int PICK_IMAGE = 1;
    Handler handler;
    CardView cardViewButton=null;
    CardView cardViewButton1=null;
    EditText editText=null;
    String newString="";
    String value1="",value0="";
    String LMG="",MG="",RMG="";
    String PLMG="",PMG="",PRMG="";
    int c=0;
    String name="";
    String version="";
    int printCount=0;
    TextView textViewVersion;
    EditText textViewName;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences=getSharedPreferences(Util.AcuPrefs,MODE_PRIVATE);
        editor=preferences.edit();
        scan = (findViewById(R.id.scan));
        img = (findViewById(R.id.imgview));
        txtresult = (findViewById(R.id.txtResult));
        scan.setVisibility(View.GONE);
        handler=new Handler();
    }


    public void btnScan(View view) {


        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        Frame frame = new Frame.Builder()
                .setBitmap(mbitmap)
                .build();

        SparseArray<Barcode> barcodeSparseArray= detector.detect(frame);

        if (barcodeSparseArray.size()>0) {
            Barcode result = barcodeSparseArray.valueAt(0);
//            txtresult.setText(result.rawValue);
            String serviceOTP=scanTxt(result.rawValue);
            if (serviceOTP.contains("#") ||serviceOTP.contains("@") || serviceOTP.contains("%") || serviceOTP.contains("&")) {
                String[] otp=serviceOTP.split(";");
                name=otp[1];
                version=otp[2];
                showDialouge(otp[0]);
            }else {
                shareWhatsapp(serviceOTP);
            }
        }else {
            Toast.makeText(this, "Select a QR code Or QR Code is Not Clear", Toast.LENGTH_SHORT).show();
        }
    }

    public void SelectImg(View view) {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select Qr Code"),PICK_IMAGE);
        txtresult.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            uri = data.getData();
            try {
                mbitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                img.setImageBitmap(mbitmap);
                scan.setVisibility(View.VISIBLE);
            }
            catch (IOException e)
            {

            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,101,0,"Logout");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 101:
                editor.clear();
                editor.apply();
                editor.commit();
                startActivity(new Intent(this,login.class));
                finishAffinity();
                break;
        }
        return true;
    }

    String scanTxt(String raw) {
        StringBuilder Id = new StringBuilder();
        if (raw.contains("#") || raw.contains("@") || raw.contains("%") || raw.contains("&")) {
            return raw;
        } else {
            for (int i = 0; i <= raw.length() - 1; i++) {
                if (i == 4 || i == 8) {
                    Id.append(" ");
                }
                char d = raw.charAt(i);

                if (Character.isLetter(d)) {
                    int ASCII = d;
                    ASCII = ASCII + i;
                    if (ASCII == 91) {
                        d = 'o';
                    } else if (ASCII == 92) {
                        d = 'p';
                    } else if (ASCII == 93) {
                        d = 'q';
                    } else if (ASCII == 94) {
                        d = 'r';
                    } else if (ASCII == 95) {
                        d = 's';
                    } else if (ASCII == 96) {
                        d = 't';
                    } else {
                        d = (char) ASCII;
                    }
                    Id.append(d);
                } else {
                    Id.append(d);
                }
            }
            Toast.makeText(this, Id, Toast.LENGTH_SHORT).show();
            return String.valueOf(Id);
        }
    }


    void shareWhatsapp(String text){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Service OTP");
        intent.putExtra(Intent.EXTRA_TEXT,"This is Service OTP:- "+text);
        startActivity(Intent.createChooser(intent,"Share Using"));
    }

    void showDialouge(String Otp){
        final String Otp1=Otp;
        final Dialog dialog=new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.print_count_dialouge);
        dialog.setTitle("Print Count OTP");
        dialog.setCancelable(false);
        cardViewButton=(CardView) dialog.findViewById(R.id.generate);
        cardViewButton1=(CardView) dialog.findViewById(R.id.cancel);
        editText=(EditText) dialog.findViewById(R.id.editText3);
        textViewName=dialog.findViewById(R.id.textName);
        textViewVersion=dialog.findViewById(R.id.textVersion);
        textViewName.setText(name);
        textViewVersion.setText(version);
        cardViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVarified()){
                    printCount=Integer.parseInt(editText.getText().toString());
                    String hi=compileOTP(Otp1);
                    shareWhatsapp(hi);
                    dialog.dismiss();
                    finish();
                    Toast.makeText(MainActivity.this, hi, Toast.LENGTH_SHORT).show();
                }
            }
        });
        cardViewButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    boolean isVarified(){
        boolean isValidate=true;
        String i=editText.getText().toString();
        if (!(editText.getText().toString().contains("00") || editText.getText().toString().contains("000"))){
            isValidate=false;
            editText.setError("Not Multiple of 100");
        }else if(editText.getText().toString().length()<0){
            isValidate=false;
            editText.setError("Enter the Print Count");
        }else if(Integer.parseInt(i)>9999){
            isValidate=false;
            editText.setError("Greater Than 9999");
        }
        return isValidate;
    }

    public String compileOTP(String raw){
        raw=raw+" ";
        String [] val=new String[raw.length()];
        for (int i=0;i<=raw.length()-1;i++) {
            if(i%4==0) {
                val[i]=raw.substring(c, i);
                c=i;
            }
        }

        LMG=val[4];
        MG=val[8];
        RMG=val[12];

//		                        Left Most Group Processing

        String LMD1=LMG.substring(0, 1);
        String RD1=LMG.substring(1,4);
        String PDG1=firstAlgo(RD1);

//                              Middle Group Processing

        String LMD2=MG.substring(0, 1);
        String SLMD2=MG.substring(1,2);
        String RD2=MG.substring(2, 4);
        String PDG2=firstAlgo(RD2);

//                              Right Most Group Processing

        String LMD3=RMG.substring(0, 1);
        String RD3=RMG.substring(1,4);
        String PDG3=firstAlgo(RD3);

        String TPS=PDG1+PDG2+PDG3;

        for (int i=0;i<=TPS.length()-1;i++) {
            char d=TPS.charAt(i);
            if (Character.isLetter(d)){
                int ASCII=d;
                ASCII=ASCII+i;
                if(ASCII==91) {
                    d='o';
                }else if(ASCII==92) {
                    d='p';
                }else if(ASCII==93) {
                    d='q';
                }else if(ASCII==94) {
                    d='r';
                }else if(ASCII==95) {
                    d='s';
                }else if(ASCII==96) {
                    d='t';
                }else {
                    d=(char)ASCII;
                }
                newString=newString+d;
            }else {
                newString=newString+d;
            }
        }

        PLMG=newString.substring(0, 3);
        PMG=newString.substring(3, 5);
        PRMG=newString.substring(5, 8);

        //                             Print Count Otp

        int divide=printCount/100;
        if(String.valueOf(divide).length()==1) {
            value1="0";
            value0=String.valueOf(divide).substring(0, 1);
        }else {
            value1=String.valueOf(divide).substring(0, 1);
            value0=String.valueOf(divide).substring(1, 2);
        }

        char a=LMD1.charAt(0);

        char b=LMD2.charAt(0);
        char e=SLMD2.charAt(0);

        char d=LMD3.charAt(0);

        a=(char)((int)a+Integer.parseInt(value1));            //LMD1

        d=(char)((int)d+Integer.parseInt(value0));  //LMD3

        b=(char)((int)b+Integer.parseInt(value1));      //LMD2

        e=(char)((int)e+Integer.parseInt(value0));    //SLMD2

        String NLMG=finalAlgo(a)+PLMG;
        String NRMG=finalAlgo(d)+PRMG;
        String NMG=finalAlgo(b)+""+finalAlgo(e)+""+PMG;

        return NLMG+" "+NMG+" "+NRMG;
    }

    public String firstAlgo(String val) {
        StringBuilder PD=new StringBuilder();
        for (int i=0;i<=val.length()-1;i++) {
            char d=val.charAt(i);
            if (Character.isLetter(d)){
                if(d=='A') {
                    d='@';
                }else if(d=='B') {
                    d='!';
                }else if(d=='J') {
                    d='%';
                }else if(d=='W') {
                    d='&';
                }else if(d=='X') {
                    d='*';
                }
            }
            PD.append(d);
        }
        return String.valueOf(PD);
    }

    public char finalAlgo(char val1) {
        if((int)val1==39) {
            val1='a';
        }else if((int)val1==44) {
            val1='b';
        }else if((int)val1==45) {
            val1='c';
        }else if((int)val1==46) {
            val1='d';
        }else if((int)val1==58) {
            val1='e';
        }else if((int)val1==59) {
            val1='f';
        }
        return val1;
    }
}
