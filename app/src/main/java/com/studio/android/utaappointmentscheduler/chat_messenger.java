package com.studio.android.utaappointmentscheduler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import common_settings.HttpHandler;

public class chat_messenger extends AppCompatActivity {
    Context lContext = null;
    String lNetId = null;
    String lSelectedNetId = null;
    String JsonString = null;
    String requestId = null;
    LinearLayout linearLayout = null;
    String messageSent = null;
    ScrollView scrollView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_messenger);
        lNetId = getIntent().getStringExtra("netId");
        lSelectedNetId = getIntent().getStringExtra("selectedId");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lContext = getApplicationContext();
        linearLayout = (LinearLayout) findViewById(R.id.chat_messages);
        scrollView = (ScrollView) findViewById(R.id.messages_scroll);
        new chat_messenger.DownloadImageTask().execute("UTASchedulerServer/sMessengerController");
    }

    public void submit_click(View v) {
        new chat_messenger.DownloadImageTask2().execute("UTASchedulerServer/sMessengerController");
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

            TextView textView = null;
            try {
                JSONObject jsonObj = new JSONObject(JsonString);
                JSONArray messagesArr = jsonObj.getJSONArray("messages");

                for (int i=0; i<messagesArr.length(); i++) {
                    JSONObject messageObj = messagesArr.getJSONObject(i);

                    if (lSelectedNetId.equals(messageObj.getString("sNetId"))
                            || lSelectedNetId.equals(messageObj.getString("pNetId"))) {
                        textView = new TextView(lContext);
                        textView.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                        );
                        textView.setText(messageObj.getString("sendername")
                                + ": " + messageObj.getString("comment")
                                + "\nSent: " + messageObj.getString("sentDate") + " "
                                + messageObj.getString("sentTime"));
                        textView.setPadding(0, 20, 0, 20);

                        if (!lSelectedNetId.equals(messageObj.getString("senderNetId"))) {
                            textView.setGravity(Gravity.RIGHT);
                        }

                        linearLayout.addView(textView);
                        requestId = messageObj.getString("requestId");
                    }
                }
            } catch (Exception Ex) {
                Ex.printStackTrace();
            }

            super.onPostExecute(result);
        }
    }

    private class DownloadImageTask2 extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];

            JSONObject jsonObj = new JSONObject();
            try {
                EditText messageField = (EditText) findViewById(R.id.et_comments);
                messageSent = messageField.getText().toString();
                jsonObj.put("request", "1");
                jsonObj.put("netId", lNetId);
                jsonObj.put("requestId", requestId);
                jsonObj.put("message", messageSent);

                HttpHandler httpHandler = new HttpHandler(getResources());
                JsonString = httpHandler.getJsonCall(url,jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.chat_messages);
            try {
                JSONObject jsonObj = new JSONObject(JsonString);
                boolean reply = jsonObj.getBoolean("reply");

                if (reply) {
                    TextView textView = new TextView(lContext);
                    textView.setLayoutParams(
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                    );
                    SharedPreferences settings = getSharedPreferences("UTA_APPT_SCHEDULER", 0);
                    String username=settings.getString("user_name", "Me");

                    textView.setText(username + ": " + messageSent + "\nSent: Now");
                    textView.setPadding(0, 20, 0, 20);
                    textView.setGravity(Gravity.RIGHT);
                    linearLayout.addView(textView);
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            } catch (Exception Ex) {
                Ex.printStackTrace();
            }

            super.onPostExecute(result);
        }
    }
}