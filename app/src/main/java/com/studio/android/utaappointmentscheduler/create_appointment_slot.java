package com.studio.android.utaappointmentscheduler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Button;
import android.widget.RelativeLayout;


import java.util.Calendar;


import org.json.JSONException;
import org.json.JSONObject;

import common_settings.HttpHandler;

public class create_appointment_slot extends AppCompatActivity{

    private static final String TAG = "Create_appointment_slot";
    private TextView mDisplayDate,mDisplayStartTime,mDisplayEndTime,tv_reserved_appt,tv_ofc_appt;
    private TextView ofcDisplayStartDate, ofcDisplayEndDate, ofcDisplayStartTime,ofcDisplayEndTime;
    private DatePickerDialog.OnDateSetListener mDateSetListener,ofcStartDateSetListener,ofcEndDateSetListener;
    private TimePickerDialog.OnTimeSetListener mStartTimeSetListener,mEndTimeSetListener,ofcStartTimeSetListener,ofcEndTimeSetListener;
    private RadioButton rb;
    private RadioGroup rg;
    private RelativeLayout relativeLayout =null;
    String rDate=null;
    String startDate=null;
    String endDate=null;
    String oStartTime=null;
    String oEndTime=null;
    String rStartTime=null;
    String rEndTime=null;
    int Slot=0;
    String slotDetails=null;
    String day=null;
    EditText buildingName = null;
    EditText roomNo = null;
    String BuildingName=null;
    String RoomNo=null;
    String lNetId = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_appointment_slot);
        buildingName = (EditText) findViewById(R.id.et_building_name);
        roomNo = (EditText) findViewById(R.id.et_room_no);
        lNetId = getIntent().getStringExtra("netId");
        Button btnCreateAppointmentSlot = findViewById(R.id.btn_create_slot);
        btnCreateAppointmentSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = findViewById(R.id.error_message);
                BuildingName=buildingName.getText().toString();
                RoomNo=roomNo.getText().toString();
                switch (Slot) {
                    case 1:
                        if (startDate != null && endDate != null && oStartTime != null &&
                                oEndTime != null&& day!=null && buildingName != null && RoomNo != null ) {
                            slotDetails = Slot + "," + startDate + "," + endDate + "," + oStartTime + "," + oEndTime+ "," + day+ "," + BuildingName+"," + RoomNo;
                            textView.setVisibility(View.INVISIBLE);
                            new DownloadImageTask().execute("UTASchedulerServer/sSlot");
                        } else {
                            textView.setVisibility(View.VISIBLE);
                            textView.setTextColor(Color.RED);
                            textView.setText("Enter all details. Try again");
                        }
                        break;
                    case 2:
                        if (rStartTime != null && rEndTime != null && rDate != null&& day!=null && !BuildingName.equals("") && !RoomNo.equals("")) {
                            slotDetails = Slot + "," + rDate + "," + rStartTime + "," + rEndTime+ ","+day+","+BuildingName+","+RoomNo;
                            textView.setVisibility(View.INVISIBLE);
                            new DownloadImageTask().execute("UTASchedulerServer/sSlot");
                        } else {
                            textView.setVisibility(View.VISIBLE);
                            textView.setTextColor(Color.RED);
                            textView.setText("Enter all details. Try again");
                        }
                        break;
                        default:
                            break;
                }

            }

        });



        tv_reserved_appt = (TextView) findViewById(R.id.tv_reserved_appt);
        tv_ofc_appt = (TextView) findViewById(R.id.tv_ofc_appt);
        mDisplayDate = (TextView) findViewById(R.id.tv_date_reserved_appt);
        mDisplayStartTime = (TextView) findViewById(R.id.tv_start_time_reserved_appt);
        mDisplayEndTime = (TextView) findViewById(R.id.tv_end_time_reserved_appt);
        ofcDisplayStartDate = (TextView) findViewById(R.id.tv_start_date_ofc_appt);
        ofcDisplayEndDate = (TextView) findViewById(R.id.tv_end_date_ofc_appt);
        ofcDisplayStartTime = (TextView) findViewById(R.id.tv_start_time_ofc_appt);
        ofcDisplayEndTime = (TextView) findViewById(R.id.tv_end_time_ofc_appt);
        rg = (RadioGroup) findViewById(R.id.rgroup);


        mDisplayDate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        create_appointment_slot.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month +1;
                rDate  = month + "/" + day + "/" + year;
                mDisplayDate.setText(rDate);
            }
        };

        mDisplayStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        create_appointment_slot.this, mStartTimeSetListener,
                        hour,minute, true  );
                timePickerDialog.show();
            }
        });

        mStartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                rStartTime = hour +"" +":" + minute;
                mDisplayStartTime.setText(rStartTime);
            }
        };

        mDisplayEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        create_appointment_slot.this, mEndTimeSetListener,
                        hour,minute, true  );
                timePickerDialog.show();
            }
        });
        mEndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                rEndTime = hour +"" +":" + minute;
                mDisplayEndTime.setText(rEndTime);
            }
        };

        ofcDisplayStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        create_appointment_slot.this, ofcStartTimeSetListener,
                        hour,minute, true  );
                timePickerDialog.show();
            }
        });
        ofcStartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                oStartTime = hour +"" +":" + minute;
                ofcDisplayStartTime.setText(oStartTime);
            }
        };
        ofcDisplayEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        create_appointment_slot.this, ofcEndTimeSetListener,
                        hour,minute, true  );
                timePickerDialog.show();
            }
        });
        ofcEndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                oEndTime = hour +"" +":" + minute;
                ofcDisplayEndTime.setText(oEndTime);
            }
        };
        ofcDisplayStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        create_appointment_slot.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, ofcStartDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        ofcStartDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month +1;
                startDate  = month + "/" + day + "/" + year;
                ofcDisplayStartDate.setText(startDate);
            }
        };
        ofcDisplayEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        create_appointment_slot.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, ofcEndDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        ofcEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month +1;
                endDate  = month + "/" + day + "/" + year;
                ofcDisplayEndDate.setText(endDate);
            }
        };
        Spinner days_spinner = (Spinner) findViewById(R.id.spinner_days);
        ArrayAdapter<String> days_adapter = new ArrayAdapter<String>(create_appointment_slot.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Days));
        days_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        days_spinner.setAdapter(days_adapter);

        days_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 1){
                    day="1";
                }
                else if (i == 2) {
                    day="2";
                }
                else if (i == 3){
                    day="3";
                }
                else if(i == 4){
                    day="4";
                }
                else if(i == 5){
                    day="5";
                }
                else if(i == 6){
                    day="6";
                }
                else if(i == 7){
                    day="7";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
    String JsonString=null;
    private class DownloadImageTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];

            JSONObject jsonObj = new JSONObject();
            try {
                System.out.println(lNetId);
                jsonObj.put("netid", lNetId);
                jsonObj.put("get", "create");
                jsonObj.put("slotDetails", slotDetails);

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
            TextView textView = findViewById(R.id.error_message);
            textView.setVisibility(View.VISIBLE);
            JSONObject jsonObj=null;
            boolean create = false;
            try {
                Log.e(TAG, "Exception: " + JsonString);
                jsonObj = new JSONObject(JsonString);
                //  Log.e("error", JsonString);
                  if(jsonObj.getBoolean("created")){
                      textView.setTextColor(Color.BLUE);
                      textView.setText("Slot created successfully");
                  }
                  else{
                      textView.setTextColor(Color.RED);
                      textView.setText("Slot time already exists.Check the previous slot times");
                  }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }




    public void rb_clicked(View v)
    {
        int radiobutttonid = rg.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(radiobutttonid);
        //String rb1 = toString(rb);
        if ((rb.getText().equals("Reserved"))){
            Slot=2;
            tv_ofc_appt.setVisibility(View.INVISIBLE);
            ofcDisplayStartDate.setVisibility(View.INVISIBLE);
            ofcDisplayEndDate.setVisibility(View.INVISIBLE);
            ofcDisplayStartTime.setVisibility(View.INVISIBLE);
            ofcDisplayEndTime.setVisibility(View.INVISIBLE);
            tv_reserved_appt.setVisibility(View.VISIBLE);
            mDisplayDate.setVisibility(View.VISIBLE);
            mDisplayStartTime.setVisibility(View.VISIBLE);
            mDisplayEndTime.setVisibility(View.VISIBLE);
        }
        if ((rb.getText().equals("Office_Hours"))){
            Slot=1;
            tv_reserved_appt.setVisibility(View.INVISIBLE);
            mDisplayDate.setVisibility(View.INVISIBLE);
            mDisplayStartTime.setVisibility(View.INVISIBLE);
            mDisplayEndTime.setVisibility(View.INVISIBLE);
            tv_ofc_appt.setVisibility(View.VISIBLE);
            ofcDisplayStartDate.setVisibility(View.VISIBLE);
            ofcDisplayEndDate.setVisibility(View.VISIBLE);
            ofcDisplayStartTime.setVisibility(View.VISIBLE);
            ofcDisplayEndTime.setVisibility(View.VISIBLE);
        }

    }

}