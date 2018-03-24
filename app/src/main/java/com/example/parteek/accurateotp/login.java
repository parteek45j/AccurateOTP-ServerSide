package com.example.parteek.accurateotp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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

public class login extends AppCompatActivity {

    CardView cardView;
    EditText Phone;
    ProgressBar pb;
    RequestQueue requestQueue;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String name = "";
    String phone ="";
    String count="";
    int id=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences=getSharedPreferences(Util.AcuPrefs,MODE_PRIVATE);
        editor=preferences.edit();
        Phone = findViewById(R.id.Phone);
        cardView= findViewById(R.id.cardView);
        pb = findViewById(R.id.progressbar);
        requestQueue= Volley.newRequestQueue(this);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = Phone.getText().toString().trim();
                if(phone.isEmpty())
                {
                    Phone.setError("Phone is required");
                    Phone.requestFocus();
                    return;
                }
                loginUser();

            }
        });
    }

    void loginUser(){
        pb.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.POST, Util.login1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object=new JSONObject(response);
                    JSONArray array=object.getJSONArray("students");
                    String message=object.getString("message");
                    if (message.contains("Login Sucessful")){
                        pb.setVisibility(View.GONE);
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
                        startActivity(new Intent(login.this,MainActivity.class));
                        finishAffinity();
                    }else{
                        pb.setVisibility(View.GONE);
                        Toast.makeText(login.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    pb.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pb.setVisibility(View.GONE);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<>();
                map.put("Phone",Phone.getText().toString());
                return map;
            }
        };
        requestQueue.add(request);
    }



}
