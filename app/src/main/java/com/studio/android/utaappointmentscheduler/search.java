package com.studio.android.utaappointmentscheduler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import common_settings.HttpHandler;

public class search extends AppCompatActivity {
    private static final String TAG = HttpHandler.class.getSimpleName();
    ListView lstvw_search;
    String professorName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        lstvw_search = (ListView) findViewById(R.id.list_view_search);


       /* lstvw_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int n = search_result.length;
                for (int j = 0; j < n; j++) {


                }

            }
        });*/

        ImageButton searchButton = (ImageButton) findViewById(R.id.imgbtn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = (EditText) findViewById(R.id.et_search);
                professorName = name.getText().toString();
                    new search.DownloadImageTask().execute("UTASchedulerServer/sSearchController");
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
    String JsonString = null;

    private class DownloadImageTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];
            EditText name = (EditText) findViewById(R.id.et_search);
            professorName = name.getText().toString();
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("request", "search");
                jsonObj.put("keyword",professorName);

                HttpHandler httpHandler = new HttpHandler(getResources());
                JsonString = httpHandler.getJsonCall(url, jsonObj);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> pNetId=new ArrayList<>();
                JSONObject jsonObj = new JSONObject(JsonString);
                JSONArray searchArr = jsonObj.getJSONArray("professors");
                for (int i=0; i<searchArr.length(); i++) {
                  JSONObject searchObj = searchArr.getJSONObject(i);
                    names.add(searchObj.getString("name"));
                    pNetId.add(searchObj.getString("pNetId"));
                }
                String[] namesArr = names.toArray(new String[names.size()]);
                final String[] netIdArr=pNetId.toArray(new String[pNetId.size()]);
               // Log.e(TAG, "123456: "+ java.util.Arrays.toString(netIdArr));
                ArrayAdapter<String> arrayAdapter_search = new ArrayAdapter<String>(search.this,android.R.layout.simple_list_item_1,namesArr);
                lstvw_search.setAdapter(arrayAdapter_search);

                lstvw_search.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        String pNetId = netIdArr[position];
                        Log.e(TAG, "123456: "+ pNetId);
                        String pName= (String) parent.getItemAtPosition(position);
                        Intent intent_book_appointment=new Intent(getApplicationContext(),course_details.class);

                        SharedPreferences settings = getSharedPreferences("UTA_APPT_SCHEDULER", 0);
                        String username=settings.getString("Username", "0");

                        intent_book_appointment.putExtra("professorid",pNetId);
                        intent_book_appointment.putExtra("professorname",pName);
                        intent_book_appointment.putExtra("netId",username);


                        getApplicationContext().startActivity(intent_book_appointment);
                    }

                });
            }
            catch (Exception e) {

            }
        }

        public void search_click(View v) {
            //code for retrieving search results
        }
    }
}