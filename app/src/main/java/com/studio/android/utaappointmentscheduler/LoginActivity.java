package com.studio.android.utaappointmentscheduler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import common_settings.HttpHandler;


public class LoginActivity extends AppCompatActivity {
    public static final String SETTING_INFOS = "UTA_APPT_SCHEDULER";

    String userName=null;
    String password=null;
    private RelativeLayout relativeLayout =null;
    ColorStateList oldColors =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences settings = getSharedPreferences(SETTING_INFOS, 0);

        userName=settings.getString("UserName", "NA");
        password=settings.getString("Password", "NA");

        relativeLayout=findViewById(R.id.loader);
        relativeLayout.setVisibility(View.VISIBLE);

        relativeLayout=findViewById(R.id.relativeLayout);
        relativeLayout.setVisibility(View.INVISIBLE);


        if(!userName.trim().equals("NA")){
            new DownloadImageTask().execute("UTASchedulerServer/sLogin");
        }

        relativeLayout=findViewById(R.id.relativeLayout);
        relativeLayout.setVisibility(View.VISIBLE);

        relativeLayout=findViewById(R.id.loader);
        relativeLayout.setVisibility(View.INVISIBLE);


        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button btnlogin = (Button) findViewById(R.id.btnlogin);
        oldColors=  etUsername.getTextColors();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                relativeLayout=findViewById(R.id.loader);
                relativeLayout.setVisibility(View.VISIBLE);

                relativeLayout=findViewById(R.id.relativeLayout);
                relativeLayout.setVisibility(View.INVISIBLE);

                userName=etUsername.getText().toString();
                password=etPassword.getText().toString();

              new DownloadImageTask().execute("UTASchedulerServer/sLogin");

            }
        });


    }

    String JsonString=null;
    private class DownloadImageTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];

            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("username", userName);
                jsonObj.put("password", password);
               // Log.e("error",jsonObj.toString());
                HttpHandler httpHandler=new HttpHandler(getResources());
                JsonString=httpHandler.getJsonCall(url,jsonObj);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            JSONObject jsonObj=null;
            boolean authentication=false;
            try{
                Log.e("before dashbord ",JsonString);
                 jsonObj = new JSONObject(JsonString);

                if(jsonObj.getBoolean("login")){
                    authentication=true;
                }

            }catch(Exception Ex){

            }
            relativeLayout=findViewById(R.id.loader);
            relativeLayout.setVisibility(View.GONE);
            if(authentication){

                TextView textView=findViewById(R.id.error_message);
                textView.setVisibility(View.INVISIBLE);


                EditText etUsername = (EditText) findViewById(R.id.etUsername);
                etUsername.getBackground().setColorFilter(oldColors.getDefaultColor(), PorterDuff.Mode.SRC_ATOP);

                EditText etPassword = (EditText) findViewById(R.id.etPassword);
                etPassword.getBackground().setColorFilter(oldColors.getDefaultColor(), PorterDuff.Mode.SRC_ATOP);
                SharedPreferences settings = getSharedPreferences(SETTING_INFOS, 0);

                try{ settings.edit()
                        .putString("UserName", userName)
                        .putString("Password", password)
                        .putString("user_name", jsonObj.getString("user_name"))
                        .putString("user_dept", jsonObj.getString("user_dept"))
                        .putString("user_type", jsonObj.getString("user_type"))
                        .putString("user_email", jsonObj.getString("user_email"))
                        .putString("user_deptname", jsonObj.getString("user_deptname"))
                        .putBoolean("Notifications", settings.getBoolean("Notifications",true))
                        .apply();
                }catch(Exception Ex){

                }


                Intent loginIntent = new Intent(LoginActivity.this, Dashboard.class);
                loginIntent.putExtra("netId", userName);
                LoginActivity.this.startActivity(loginIntent);
            }else{
                userName=null;
                password=null;
                TextView textView=findViewById(R.id.error_message);
                textView.setVisibility(View.VISIBLE);

                EditText etUsername = (EditText) findViewById(R.id.etUsername);
                etUsername.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

                EditText etPassword = (EditText) findViewById(R.id.etPassword);
                etPassword.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            }
            relativeLayout=findViewById(R.id.relativeLayout);
            relativeLayout.setVisibility(View.VISIBLE);

            relativeLayout=findViewById(R.id.loader);
            relativeLayout.setVisibility(View.INVISIBLE);
            super.onPostExecute(result);

        }
    }
}
