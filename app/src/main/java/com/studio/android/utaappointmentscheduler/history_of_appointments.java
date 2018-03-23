package com.studio.android.utaappointmentscheduler;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import common_settings.HttpHandler;

public class history_of_appointments extends AppCompatActivity {
    ListView lstvw_history;
    String[] namesArr;
    String[] statusesArr;
    String[] datesArr;
    String lNetId = null;
    String JsonString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_of_appointments);
        lNetId = getIntent().getStringExtra("netId");
        new history_of_appointments.DownloadImageTask().execute("UTASchedulerServer/sAppointmentController");
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
    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return namesArr.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.customlayout_history_of_appointments,null);
            TextView name = (TextView)view.findViewById(R.id.tv_prof_name_history);
            TextView status = (TextView)view.findViewById(R.id.tv_location_history);
            TextView date = (TextView)view.findViewById(R.id.tv_appt_date_history);

            name.setText("Name: " + namesArr[i]);
            status.setText("Status: " + statusesArr[i]);
            date.setText("Date: " + datesArr[i]);
            return view;
        }
    }

    private class DownloadImageTask extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];

            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("get", "pastAppts");
                jsonObj.put("snetid", lNetId);

                HttpHandler httpHandler = new HttpHandler(getResources());
                JsonString = httpHandler.getJsonCall(url,jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ArrayList<String> names = new ArrayList<>();
            ArrayList<String> dates = new ArrayList<>();
            ArrayList<String> statuses = new ArrayList<>();
            try {
                JSONObject jsonObj = new JSONObject(JsonString);
                JSONArray apptsArr = jsonObj.getJSONArray("pastAppts");

                for (int i=0; i<apptsArr.length(); i++) {
                    JSONObject apptObj = apptsArr.getJSONObject(i);

                    if (!apptObj.getString("pNetId").equals(lNetId)) {
                        names.add(apptObj.getString("pNetId"));
                    } else {
                        names.add(apptObj.getString("sNetId"));
                    }

                    if (apptObj.getString("statusId").equals("1")) {
                        statuses.add("Past");
                    } else if (apptObj.getString("statusId").equals("3")) {
                        statuses.add("Cancelled");
                    } else {
                        statuses.add("Closed");
                    }

                    statuses.add(apptObj.getString("statusId"));
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat lDbFormat=new SimpleDateFormat("MMM dd yyyy");
                    java.util.Date lBookingDate= formatter.parse(apptObj.getString("apptDate"));
                    dates.add(lDbFormat.format(lBookingDate));
                }

                namesArr = names.toArray(new String[names.size()]);
                statusesArr = statuses.toArray(new String[statuses.size()]);
                datesArr = dates.toArray(new String[dates.size()]);

                lstvw_history = (ListView) findViewById(R.id.list_view_history);
                CustomAdapter customAdapter = new CustomAdapter();
                lstvw_history.setAdapter(customAdapter);
            } catch (Exception Ex) {
                Ex.printStackTrace();
            }

            super.onPostExecute(result);
        }
    }
}
