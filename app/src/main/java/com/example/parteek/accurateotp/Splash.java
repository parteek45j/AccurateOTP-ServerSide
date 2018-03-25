package com.example.parteek.accurateotp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Splash extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    RequestQueue requestQueue;
    String number="";
    String name = "";
    String phone ="";
    String count="";
    int id=0;
    ProgressBar pb;
    Handler handler;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar a=getSupportActionBar();
        a.hide();
        pb = findViewById(R.id.progressbar);
        pb.setVisibility(View.VISIBLE);
        preferences=getSharedPreferences(Util.AcuPrefs,MODE_PRIVATE);
        editor=preferences.edit();
        requestQueue= Volley.newRequestQueue(this);
        number=preferences.getString(Util.Phone,"");
        handler=new Handler();
        if (ConnectionCheck.isConnected(connectivityManager,networkInfo,this)) {
            if (number.length() > 0 && !(number.isEmpty())) {
                loginUser();
            } else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Splash.this, login.class);
                        startActivity(intent);
                        finishAffinity();
                        pb.setVisibility(View.GONE);
                    }
                }, 3000);
            }
        }else{
            Toast.makeText(this, "Internet Connection Required", Toast.LENGTH_LONG).show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finishAffinity();
                }
            }, 2000);
        }

//        Log.e("Number",number+" "+phone);
//        if (loginUser()) {
//            handler.sendEmptyMessageDelayed(101, 5000);
//        }else {
//            handler.sendEmptyMessageDelayed(102, 3000);
//        }
    }
//    Handler handler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what==101){
//                pb.setVisibility(View.GONE);
//                startActivity(new Intent(Splash.this,MainActivity.class));
//                finish();
//            }else if (msg.what==102){
//                pb.setVisibility(View.GONE);
//                startActivity(new Intent(Splash.this,login.class));
//                finish();
//            }
//        }
//    };

     public void loginUser(){
        StringRequest request=new StringRequest(Request.Method.POST, Util.login1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object=new JSONObject(response);
                    JSONArray array=object.getJSONArray("students");
                    String message=object.getString("message");
                    if (message.contains("Sucessful")){
                        for (int i=0;i<array.length();i++){
                            JSONObject object1=array.getJSONObject(i);
                            id = object1.getInt("User_ID");
                            name = object1.getString("User_name");
                            phone = object1.getString("Phone");
                            count = object1.getString("Count");
                        }
                        editor.putString(Util.Name,name);
                        editor.putString(Util.Phone,phone);
                        editor.putString(Util.count,count);
                        editor.putInt(Util.id,id);
                        editor.apply();
                        Log.e("Data",name+" "+phone+" "+count+" "+id);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent=new Intent(Splash.this,MainActivity.class);
                                startActivity(intent);
                                finishAffinity();
                                pb.setVisibility(View.GONE);
                            }
                        },4000);
                    }else {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent=new Intent(Splash.this,login.class);
                                startActivity(intent);
                                finishAffinity();
                                pb.setVisibility(View.GONE);
                            }
                        },3000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<>();
                map.put("Phone",number);
                return map;
            }
        };
        requestQueue.add(request);
    }

}
