package com.studio.android.utaappointmentscheduler;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

import common_settings.HttpHandler;

/**
 * Created by Kartik on 27-11-2017.
 */

public class course_details extends AppCompatActivity{
    private static final String TAG = HttpHandler.class.getSimpleName();
    String ProfessorNetid;
    String Professorname;
    String Netid;
    View mView;
    Hashtable<String,ArrayList<String>> mapping=new Hashtable<String,ArrayList<String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.course_dialog);
        ProfessorNetid= getIntent().getStringExtra("professorid");
        Professorname= getIntent().getStringExtra(" professorname");
       // Log.e(TAG, "123456: "+ Professorname);
        Netid= getIntent().getStringExtra("netId");

        new course_details.DownloadImageTask().execute("UTASchedulerServer/sSlot");
        super.onCreate(savedInstanceState);

        Button lButton=(Button) findViewById(R.id.request_button);
        lButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(course_details.this);
                LayoutInflater inflater = LayoutInflater.from(course_details.this);
                mView = getLayoutInflater().inflate(R.layout.request_appointment,null );
                Button lbuttonConfirm=(Button)mView.findViewById(R.id.buttonrequestConfirm);

                lbuttonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View parent = (View) v.getParent();
                        EditText lComment = (EditText)mView.findViewById(R.id.request_comment);
                        String CommentTest=lComment.getText().toString();
                       // Log.e(TAG,  CommentTest);
                        if(CommentTest.trim().equals("")){
                            TextView lCommentalert = (TextView)mView.findViewById(R.id.requestalert);
                            lCommentalert.setVisibility(View.VISIBLE);
                        }
                        else{

                            String[] pram={"UTASchedulerServer/sAppointmentController",ProfessorNetid,CommentTest};
                            new course_details.RequestTask().execute(pram);
                        }
                    }
                });
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                Button lbuttonClose=(Button)mView.findViewById(R.id.buttonCancel);
                lbuttonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        SharedPreferences settings = getSharedPreferences("UTA_APPT_SCHEDULER", 0);
        String usertype=settings.getString("user_type", "0");
        if(usertype.trim().equals("2")){
            lButton.setVisibility(View.GONE);

        }

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

    String JsonString=null;
    private class DownloadImageTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];

            JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("netid",ProfessorNetid );
            jsonObj.put("get", "slotlist");
            jsonObj.put("currdatelist", "no");
            jsonObj.put("slotDetails", "");


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

            LinearLayout SlotList= findViewById(R.id.officehours_list);
            SlotList.setOrientation(LinearLayout.VERTICAL);


            try{

                jsonObj = new JSONObject(JsonString);
               // Log.e(TAG, "Exception: " + JsonString);
                TextView profName = (TextView) findViewById(R.id.prof_name);
                TextView deptName = (TextView) findViewById(R.id.depatment_name);
                TextView profEmail = (TextView) findViewById(R.id.prof_email);

                profName.setText(jsonObj.get("professorname").toString());
                deptName.setText(jsonObj.get("depatmentname").toString());
                profEmail.setText(jsonObj.get("email").toString());
                Professorname=jsonObj.get("professorname").toString();
                JSONArray JsonSlotList= jsonObj.getJSONArray("slots");

                for(int i=0;i<JsonSlotList.length();i++) {
                    try{
                        JSONObject jsonObj2 =(JSONObject) JsonSlotList.get(i);

                        LinearLayout Slot= new LinearLayout(getApplicationContext());
                        Slot.setOrientation(LinearLayout.VERTICAL);
                        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                        SimpleDateFormat lDbFormat=new SimpleDateFormat("MMM dd yyyy");

                        Date lBookingDate= formatter.parse(jsonObj2.get("gStartDate").toString());

                        String slotype=jsonObj2.get("gSlotType").toString();
                        if(slotype.trim().equals("1")){
                            slotype="Office hours";
                        }else{
                            slotype="Reserved";
                        }
                        TextView textView=getSlotDisplayList("Type",slotype);
                        TextView textView2=getSlotDisplayList("Date",lDbFormat.format(lBookingDate));

                        TextView textView3=getSlotDisplayList("Time",jsonObj2.get("gSlotStartTime").toString()+" - "+jsonObj2.get("gSlotEndTime").toString());
                        TextView textView4=getSlotDisplayList("Building No",jsonObj2.get("gBuildingId").toString());
                        TextView textView5=getSlotDisplayList("Room No",jsonObj2.get("gRoomNo").toString());

                        ArrayList<String> SlotContent=new ArrayList<String>();
                        SlotContent.add(jsonObj2.get("OfficeSlotId").toString());
                        SlotContent.add(jsonObj2.get("gStartDate").toString());
                        SlotContent.add(jsonObj2.get("gSlotStartTime").toString());
                        SlotContent.add(jsonObj2.get("gSlotEndTime").toString());
                        SlotContent.add(jsonObj.get("professorname").toString());

                        mapping.put(jsonObj2.get("OfficeSlotId").toString(),SlotContent);

                        LinearLayout SlotDescription = new LinearLayout(getApplicationContext());
                        SlotDescription.setOrientation(LinearLayout.VERTICAL);
                        SlotDescription.addView(textView);
                        SlotDescription.addView(textView2);
                        SlotDescription.addView(textView3);
                        SlotDescription.addView(textView4);
                        SlotDescription.addView(textView5);
                        Slot.addView(SlotDescription);

                        Button lButton=new Button(getApplicationContext());
                        lButton.setText("BOOK");
                        lButton.setTag(jsonObj2.get("OfficeSlotId").toString());

                        lButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(course_details.this);
                                LayoutInflater inflater = LayoutInflater.from(course_details.this);
                                View mView = getLayoutInflater().inflate(R.layout.booking_confirmation,null );
                                Button lbuttonConfirm=(Button)mView.findViewById(R.id.buttonconfirm);

                                TextView textView=mView.findViewById(R.id.professor_booking);
                                textView.setText("Professor : "+mapping.get(v.getTag()).get(4));

                                TextView textView2=mView.findViewById(R.id.date_booking);
                                textView2.setText("Date : "+mapping.get(v.getTag()).get(1));

                                TextView textView3=mView.findViewById(R.id.time_booking);
                                textView3.setText("Time : "+mapping.get(v.getTag()).get(2)+" - "+mapping.get(v.getTag()).get(3));
                                //View parent = (View) v.getParent();
                               // EditText lComment = (EditText)mView.findViewById(R.id.request_comment);

                                lbuttonConfirm.setTag(v.getTag());
                                lbuttonConfirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String[] pram={"UTASchedulerServer/sAppointmentController",ProfessorNetid,mapping.get(v.getTag()).get(0),
                                                mapping.get(v.getTag()).get(1),v.getTag().toString()};
                                        new course_details.BookingTask().execute(pram);

                                    }
                                });
                                mBuilder.setView(mView);
                                final AlertDialog dialog = mBuilder.create();
                                Button lbuttonClose=(Button)mView.findViewById(R.id.buttonCancel);
                                lbuttonClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();

                            }
                        });
                        LinearLayout Booklayout = new  LinearLayout(getApplicationContext());
                        SharedPreferences settings = getSharedPreferences("UTA_APPT_SCHEDULER", 0);
                        String usertype=settings.getString("user_type", "0");

                        Booklayout.addView(lButton);
                        if(usertype.trim().equals("2")){
                            lButton.setVisibility(View.GONE);

                        }
                        Slot.addView(Booklayout);
                        SlotList.addView(Slot);

                    }catch(Exception Ex){
                        Ex.printStackTrace();
                    }

                }

            }catch(Exception Ex){
                Log.e(TAG, "Exception: " + Ex);
            }

            super.onPostExecute(result);

        }

        public TextView getSlotDisplayList(String rName,String lType){
            TextView textView=new TextView(getApplicationContext());
            LinearLayout.LayoutParams layoutsettings = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutsettings.setMargins(0, 0, 0, 0);
            //textView.setHeight(5);
            textView.setLayoutParams(layoutsettings);
            textView.setText(rName+": "+lType);
            return textView;
        }
    }
String bookingJsonString=null;
    private class BookingTask extends AsyncTask<String,Void,Void> {

        String mappingID;
        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];
            String profid = urls[1];
            String date = urls[3];
            String slotid = urls[2];
            mappingID= urls[4];

            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("get","book" );
                jsonObj.put("snetid", ""+Netid);

                jsonObj.put("slotid", slotid);
                jsonObj.put("bookingdate",date );

                HttpHandler httpHandler=new HttpHandler(getResources());
                bookingJsonString=httpHandler.getJsonCall(url,jsonObj);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            JSONObject jsonObj=null;

            LinearLayout SlotList= findViewById(R.id.officehours_list);
            SlotList.setOrientation(LinearLayout.VERTICAL);

            try{

                jsonObj = new JSONObject(bookingJsonString);
                String lreturn = jsonObj.getString("bookingconfirm");
                if(lreturn.equals("yes")){
                    notifyer lNotifyer=new notifyer();
                    lNotifyer.mNotify(getApplicationContext(),course_details.class,"Booked appointment with "+ Professorname);


///////////////////////////
                    try{

                        String Date=mapping.get(mappingID).get(1);
                        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                        Date lBookingDate= formatter.parse(Date);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(lBookingDate);

                        TimeZone tz = TimeZone.getDefault();

                        ContentResolver cr = getContentResolver();
                        ContentValues values = new ContentValues();
                        values.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis());
                        values.put(CalendarContract.Events.DTEND, cal.getTimeInMillis());
                        values.put(CalendarContract.Events.TITLE, "Appointment with "+Professorname);
                        values.put(CalendarContract.Events.CALENDAR_ID, 3);
                        values.put(CalendarContract.Events.DESCRIPTION, "Appointment with "+Professorname);
                        values.put(CalendarContract.Events.EVENT_TIMEZONE,  tz.getID());
                        values.put(CalendarContract.Events.HAS_ALARM, 1);

                        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.READ_CALENDAR)
                                != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getApplicationContext(), "Application does not have Calendar permission",
                                    Toast.LENGTH_SHORT).show();
                        }else{

                            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                            long eventID = Long.parseLong(uri.getLastPathSegment());
                            Toast.makeText(getApplicationContext(), "Appointment added to Calendar",
                                    Toast.LENGTH_SHORT).show();



                        }



                        // get the event ID that is the last element in the Uri


                    }catch(Exception Ex){
                        Log.e(TAG, "======>: " + Ex);
                    }
////////////////////////

                    Intent lDashbord = new Intent(getApplicationContext(),Dashboard.class);
                    lDashbord.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    lDashbord.putExtra("netId", Netid);
                    getApplicationContext().startActivity(lDashbord);
                }


            }catch(Exception Ex){
                Log.e(TAG, "Exception: " + Ex);
            }

            super.onPostExecute(result);

        }

    }

    String RequestJsonString=null;
    private class RequestTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];
            String profid = urls[1];
            String comment = urls[2];



            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("get","requestbooking" );
                jsonObj.put("snetid", ""+Netid);
                jsonObj.put("comment", comment);
                jsonObj.put("professorid",profid );

                HttpHandler httpHandler=new HttpHandler(getResources());
                bookingJsonString=httpHandler.getJsonCall(url,jsonObj);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            JSONObject jsonObj=null;

            LinearLayout SlotList= findViewById(R.id.officehours_list);
            SlotList.setOrientation(LinearLayout.VERTICAL);

            try{

                jsonObj = new JSONObject(bookingJsonString);
                String lreturn = jsonObj.getString("bookingconfirm");
                if(lreturn.equals("yes")){

                    notifyer lNotifyer=new notifyer();
                    lNotifyer.mNotify(getApplicationContext(),course_details.class,"Request for appointment sent to "+ Professorname);

                    Intent lDashbord = new Intent(getApplicationContext(),Dashboard.class);
                    lDashbord.putExtra("netId", Netid);
                    getApplicationContext().startActivity(lDashbord);
                }


            }catch(Exception Ex){
                Log.e(TAG, "Exception: " + Ex);
            }

            super.onPostExecute(result);

        }



    }
}
