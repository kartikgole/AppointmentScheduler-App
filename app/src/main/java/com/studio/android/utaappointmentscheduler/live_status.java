package com.studio.android.utaappointmentscheduler;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import common_settings.HttpHandler;

/**
 * Created by Deathlord on 12/1/2017.
 */

public class live_status extends AppCompatActivity {
    private static final String TAG = HttpHandler.class.getSimpleName();
    String slotid;
    String Netid;
    String apptdate;
    View mView;

    Hashtable<String,ArrayList<String>> mapping=new Hashtable<String,ArrayList<String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.live_status);
        slotid= getIntent().getStringExtra("slotid");
        Netid= getIntent().getStringExtra("netId");
        apptdate= getIntent().getStringExtra("apptdate");


        super.onCreate(savedInstanceState);
        new live_status.StudentListTask().execute("UTASchedulerServer/sAppointmentController");

        Button lbuttonConfirm=(Button)findViewById(R.id.next_button);
        SharedPreferences settings = getSharedPreferences("UTA_APPT_SCHEDULER", 0);

        String userdept=settings.getString("user_type", "0");
        if(!userdept.trim().equals("2")){
            lbuttonConfirm.setVisibility(View.GONE);
        }
        lbuttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new live_status.NextStudentListTask().execute("UTASchedulerServer/sAppointmentController");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.History_of_appointments:
                Intent intent_history = new Intent(this,history_of_appointments.class);
                intent_history.putExtra("netId", getIntent().getStringExtra("netId").toString());
                this.startActivity(intent_history);
                return true;
            case R.id.Create_appointment_slot:
                Intent intent_create_appointment = new Intent(this,create_appointment_slot.class);
                intent_create_appointment.putExtra("netId", getIntent().getStringExtra("netId").toString());
                this.startActivity(intent_create_appointment);
                return true;
            case R.id.Messenger:
                Intent intent_messenger = new Intent(this,Messenger.class);
                intent_messenger.putExtra("netId", getIntent().getStringExtra("netId").toString());
                this.startActivity(intent_messenger);
                return true;
            case R.id.Search:
                Intent intent_search = new Intent(this,search.class);
                this.startActivity(intent_search);
                return true;
            case R.id.Logout:
                Intent intent_Logout = new Intent(this,LoginActivity.class);
                SharedPreferences settings = getSharedPreferences("UTA_APPT_SCHEDULER", 0);

                settings.edit()
                        .putString("UserName", "NA")
                        .putString("Password", "NA")
                        .putString("user_name", "NA")
                        .putString("user_dept", "NA")
                        .putString("user_type",  "NA")
                        .putString("user_email", "NA")
                        .putString("user_deptname", "NA")
                        .apply();
                intent_Logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent_Logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent_Logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent_Logout);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    String ApptJsonString=null;
    private class StudentListTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];
            Log.e(TAG, "Exception: " +url);
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("get","appointmentlist" );
                jsonObj.put("snetid", ""+Netid);
                jsonObj.put("slotid", slotid);
                jsonObj.put("date",apptdate );

                HttpHandler httpHandler=new HttpHandler(getResources());
                ApptJsonString=httpHandler.getJsonCall(url,jsonObj);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            JSONObject jsonObj=null;

           LinearLayout SlotList = (LinearLayout) findViewById(R.id.studentList);
            ((LinearLayout) SlotList).removeAllViews();
            TextView textView;
            try{
                JSONArray List=new JSONArray(ApptJsonString);
                for(int i=0;i<List.length();i++){
                    final JSONObject jsonObj2= (JSONObject)List.get(i);

                    textView=new TextView(getApplicationContext());
                    textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    textView.setText((i+1)+". "+jsonObj2.get("studentname").toString());
                    Log.e(TAG, "status: " +jsonObj2.get("statusid").toString());
                    if(jsonObj2.get("statusid").toString().equals("1")){//past
                        textView.setTextColor(Color.GREEN);
                    }else if(jsonObj2.get("statusid").toString().equals("2")){//current
                        textView.setTextColor(Color.RED);
                    }
                    textView.setPadding(20, 20, 20, 20);

                    SlotList.addView(textView);
                }


            }catch(Exception Ex){
                Log.e(TAG, "Exception: " + Ex);
            }

            super.onPostExecute(result);

        }

    }

    String nextJsonString=null;
    private class NextStudentListTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];

            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("get","liveupdate" );
                jsonObj.put("snetid", ""+Netid);
                jsonObj.put("slotid", slotid);
                jsonObj.put("date",apptdate );

                HttpHandler httpHandler=new HttpHandler(getResources());
                nextJsonString=httpHandler.getJsonCall(url,jsonObj);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            JSONObject jsonObj=null;


            LinearLayout SlotList = (LinearLayout) findViewById(R.id.studentList);
            TextView textView;
            try{


                JSONObject lreturned=new JSONObject(nextJsonString);

                boolean lresult=lreturned.getBoolean("updated");
                Log.e(TAG, "Exception: " +lresult);
                if(lresult){
                    new live_status.StudentListTask().execute("UTASchedulerServer/sAppointmentController");
                }



            }catch(Exception Ex){
                Log.e(TAG, "Exception: " + Ex);
            }

            super.onPostExecute(result);

        }

    }


}
