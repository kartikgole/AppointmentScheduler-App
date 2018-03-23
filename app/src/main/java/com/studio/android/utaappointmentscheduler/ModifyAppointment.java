package com.studio.android.utaappointmentscheduler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Kartik on 27-11-2017.
 */

public class ModifyAppointment extends AppCompatActivity {

    private TextView modify_Date, modify_StartTime, modify_EndTime;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mStartTimeSetListener,mEndTimeSetListener;
    private Button btnModify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_page);
        modify_Date = (TextView) findViewById(R.id.textView4);
        modify_StartTime = (TextView) findViewById(R.id.textView7);
        modify_StartTime = (TextView) findViewById(R.id.textView8);
        //btnModify = (Button) findViewById(R.id.buttonModify);

        modify_Date.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ModifyAppointment.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month +1;
                String date  = month + "/" + day + "/" + year;
                modify_Date.setText(date);
            }
        };

        modify_StartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        ModifyAppointment.this, mStartTimeSetListener,
                        hour,minute, true  );
                timePickerDialog.show();
            }
        });

        mStartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String time = hour +"" +":" + minute;
                modify_StartTime.setText(time);
            }
        };

        modify_EndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        ModifyAppointment.this, mEndTimeSetListener,
                        hour,minute, true  );
                timePickerDialog.show();
            }
        });
        mEndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String time = hour +"" +":" + minute;
                modify_EndTime.setText(time);
            }
        };
    }
}
