package com.studio.android.utaappointmentscheduler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

import common_settings.HttpHandler;

public class Messenger extends AppCompatActivity {
    Context lContext = null;
    String lNetId = null;
    String JsonString = null;
    Hashtable<String,String> mapping=new Hashtable<String,String>();
    Hashtable<String,String> mappingreverse=new Hashtable<String,String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messenger);
        lNetId = getIntent().getStringExtra("netId");
        lContext = getApplicationContext();
        new Messenger.DownloadImageTask().execute("UTASchedulerServer/sMessengerController");
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
    private class DownloadImageTask extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];

            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("request", "2");
                jsonObj.put("netId", lNetId);

                HttpHandler httpHandler = new HttpHandler(getResources());
                JsonString = httpHandler.getJsonCall(url,jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                JSONObject jsonObj = new JSONObject(JsonString);
                JSONArray messagesArr = jsonObj.getJSONArray("messages");
                ArrayList<String> netIds = new ArrayList<>();
mapping=new Hashtable<String,String>();
mappingreverse=new Hashtable<String,String>();
                for (int i=0; i<messagesArr.length(); i++) {
                    ArrayList<String> message = new ArrayList<>();
                    JSONObject messageObj = messagesArr.getJSONObject(i);

                    if (!mapping.contains(messageObj.getString("proffname"))
                            && !messageObj.getString("pNetId").equals(lNetId)) {
                        netIds.add(messageObj.getString("proffname"));///----------------------------------->changed
                        mapping.put(messageObj.getString("pNetId"),messageObj.getString("proffname"));
                        mappingreverse.put(messageObj.getString("proffname"),messageObj.getString("pNetId"));
                    }

                    if (!mapping.contains(messageObj.getString("studentname"))
                            && !messageObj.getString("sNetId").equals(lNetId)) {
                        netIds.add(messageObj.getString("studentname"));///----------------------------------->changed
                        mapping.put(messageObj.getString("sNetId"),messageObj.getString("studentname"));
                        mappingreverse.put(messageObj.getString("studentname"),messageObj.getString("sNetId"));
                    }
                }

                final String[] netIdsArr = netIds.toArray(new String[netIds.size()]);
                ListView lstvw_messenger = (ListView) findViewById(R.id.list_view_messenger);
                ArrayAdapter<String> arrayAdapter_messenger = new ArrayAdapter<String>(
                        lContext, android.R.layout.simple_list_item_1, netIdsArr);
                lstvw_messenger.setAdapter(arrayAdapter_messenger);

                lstvw_messenger.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent chat_intent = new Intent(Messenger.this, chat_messenger.class);
                        chat_intent.putExtra("selectedId", mappingreverse.get(netIdsArr[i]));
                        chat_intent.putExtra("netId", getIntent().getStringExtra("netId"));
                        Messenger.this.startActivity(chat_intent);
                    }
                });
            } catch (Exception Ex) {
                Ex.printStackTrace();
            }

            super.onPostExecute(result);
        }
    }
}
