package com.studio.android.utaappointmentscheduler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by Kartik on 27-11-2017.
 */

public class Summary_Tab extends Fragment {
    private static final String TAG = HttpHandler.class.getSimpleName();
    private Context mContext;

    View  rootView; Hashtable<String,ArrayList<String>> mapping=new Hashtable<String,ArrayList<String>>();
    String netId="";
    View  cancelView;
    String professor;
    String usertype;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences settings = getActivity().getSharedPreferences("UTA_APPT_SCHEDULER", 0);
         usertype=settings.getString("user_type", "0");

         rootView = inflater.inflate(R.layout.summary_tab, container, false);
        mContext=getActivity();
        netId=getActivity().getIntent().getStringExtra("netId");
        //Log.e(TAG, "netId: " +netId);
        super.onCreate(savedInstanceState);

        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        new Summary_Tab.DownloadImageTask().execute("UTASchedulerServer/sDashboardController");

    }

    String JsonString=null;
    private class DownloadImageTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];

            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("netid", netId);
                jsonObj.put("get", "summarytab");

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
            TextView textView=null;
            LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.appointment_list);
            LinearLayout linearLayout2 = (LinearLayout) rootView.findViewById(R.id.course_list);
            try{
                jsonObj = new JSONObject(JsonString);
                JSONArray appointmentList= jsonObj.getJSONArray("apptData");

                for(int i=0;i<5&&i<appointmentList.length();i++){
                    JSONObject jsonObj2= (JSONObject)appointmentList.get(i);
                    String DateString=jsonObj2.get("appointmentdate").toString();
                    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                    SimpleDateFormat lDbFormat=new SimpleDateFormat("MMM dd yyyy");
                    Date lBookingDate= formatter.parse(DateString);

                    ArrayList<String> SlotContent=new ArrayList<String>();
                    SlotContent.add(jsonObj2.get("professorname").toString());
                    SlotContent.add(lDbFormat.format(lBookingDate));
                    SlotContent.add(jsonObj2.get("slotstartTime").toString());
                    SlotContent.add(jsonObj2.get("slotid").toString());
                    SlotContent.add(jsonObj2.get("appointmentid").toString());/////////////////////////////

                    mapping.put(Integer.toString(i),SlotContent);
                    //Log.e(TAG, "Exception: " +mapping.get(i).get(1) );
                    textView=new TextView(getContext());
                    textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    textView.setText(jsonObj2.get("professorname").toString()+" ["+lDbFormat.format(lBookingDate)+" "+jsonObj2.get("slotstartTime").toString()+"]");
                    textView.setPadding(20, 20, 20, 20);
                    textView.setTag(Integer.toString(i));
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                            View mView = getLayoutInflater().inflate(R.layout.appointment_dialog,null);


                            String tag=(String)v.getTag();
                            String professorname=mapping.get(tag).get(0);
                            String date=mapping.get(tag).get(1);
                            String time=mapping.get(tag).get(2);
                          // Log.e(TAG, "Exception: " +professorname );

                           TextView textView=mView.findViewById(R.id.prof_app_name);
                            textView.setText("Professor : "+professorname);

                            TextView textView2=mView.findViewById(R.id.prof_app_date);
                            textView2.setText("Date : "+date);

                            TextView textView3=mView.findViewById(R.id.prof_app_time);
                            textView3.setText("Time : "+time);

                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();

                            Button lbuttonApptClose1=(Button)mView.findViewById(R.id.cancel_app);
                            lbuttonApptClose1.setTag(tag);
                            lbuttonApptClose1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

////////////////////////////////////////////////////////////////////
                                    String tag=(String)v.getTag();
                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                                    cancelView = getLayoutInflater().inflate(R.layout.cancel_appointment,null);
                                    Button lbuttonConfirm=(Button)cancelView.findViewById(R.id.cancel_confirm);
                                    lbuttonConfirm.setTag(tag);
                                    lbuttonConfirm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String tag=(String)v.getTag();

                                            EditText lComment = (EditText)cancelView.findViewById(R.id.cancel_comment);
                                            String CommentTest=lComment.getText().toString();

                                            if(CommentTest.trim().equals("")){
                                                TextView lCommentalert = (TextView)cancelView.findViewById(R.id.cancelalert);
                                                lCommentalert.setVisibility(View.VISIBLE);
                                            }
                                            else{
                                               // Log.e(TAG,  "=====>"+tag);
                                                professor=mapping.get(tag).get(0);
                                                String[] pram={"UTASchedulerServer/sAppointmentController",mapping.get(tag).get(4),CommentTest};
                                                new Summary_Tab.CancelTask().execute(pram);
                                            }
                                        }
                                    });
                                    mBuilder.setView(cancelView);
                                    final AlertDialog dialog = mBuilder.create();
                                    Button lbuttonClose=(Button)cancelView.findViewById(R.id.cancel_cancel);
                                    lbuttonClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
////////////////////////////////////////////////////////////////////////
                                }
                            });
                            if(usertype.trim().equals("1"))
                                lbuttonApptClose1.setVisibility(View.VISIBLE);
                            Button lbuttonClose=(Button)mView.findViewById(R.id.buttonCancel);
                            lbuttonClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialog.dismiss();
                                }
                            });

                            Button lbuttonClose2=(Button)mView.findViewById(R.id.live_status);
                            lbuttonClose2.setTag(tag);
                            lbuttonClose2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //Log.e(TAG, "Exception: " + v.getTag());
                                    Intent intent_professor_slotList = new Intent(getContext(),live_status.class);

                                    try{

                                        intent_professor_slotList.putExtra("netId", netId);
                                        intent_professor_slotList.putExtra("slotid", mapping.get( v.getTag()).get(3));
                                        intent_professor_slotList.putExtra("apptdate", mapping.get( v.getTag()).get(1));

                                    }catch(Exception Ex){
                                        Log.e(TAG, "Exception: " + Ex);
                                    }
                                    getContext().startActivity(intent_professor_slotList);

                                }
                            });

                            dialog.show();
                        }
                    });
                    linearLayout.addView(textView);
                }
                if(appointmentList.length()>5){
                    TextView more= (TextView) rootView.findViewById(R.id.appointment_more);
                    more.setVisibility(View.VISIBLE);
                }
                JSONArray coursetList= jsonObj.getJSONArray("courseData");

                for(int i=0;i<5&&i<coursetList.length();i++){
                    final JSONObject jsonObj2= (JSONObject)coursetList.get(i);
                    String trimmedString=jsonObj2.get("professorid").toString().substring(1,jsonObj2.get("professorid").toString().length()-1);

                    textView=new TextView(getContext());
                    textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    textView.setText(jsonObj2.get("coursename").toString()+" ["+jsonObj2.get("professorname").toString()+"]");
                    textView.setPadding(20, 20, 20, 20);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent_professor_slotList = new Intent(getContext(),course_details.class);

                            try{

                                intent_professor_slotList.putExtra("netId", netId);
                                intent_professor_slotList.putExtra("professorid", jsonObj2.get("professorid").toString());
                                intent_professor_slotList.putExtra("professorname",  jsonObj2.get("professorname").toString());

                            }catch(Exception Ex){
                                Log.e(TAG, "Exception: " + Ex);
                            }
                            getContext().startActivity(intent_professor_slotList);

                        }
                    });
                    linearLayout2.addView(textView);
                }
                if(coursetList.length()>5){
                    TextView more= (TextView) rootView.findViewById(R.id.course_more);
                    more.setVisibility(View.VISIBLE);
                }
            }catch(Exception Ex){
                Log.e(TAG, "Exception: " + Ex);
            }

            super.onPostExecute(result);

        }
    }

    String cancelJsonString=null;
    private class CancelTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];
            String appointmentid = urls[1];
            String comment = urls[2];

            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("get","cancelbooking" );
                jsonObj.put("snetid", ""+netId);
                jsonObj.put("comment", comment);
                jsonObj.put("appointmentid",appointmentid );

                HttpHandler httpHandler=new HttpHandler(getResources());
                cancelJsonString=httpHandler.getJsonCall(url,jsonObj);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            JSONObject jsonObj=null;



            try{

                jsonObj = new JSONObject(cancelJsonString);
                String lreturn = jsonObj.getString("cancelconfirm");
                if(lreturn.equals("yes")){

                    notifyer lNotifyer=new notifyer();
                    lNotifyer.mNotify(getContext(),course_details.class,"Appointment canceled with "+ professor);

                    Intent lDashbord = new Intent(getContext(),Dashboard.class);
                    lDashbord.putExtra("netId", netId);
                    getContext().startActivity(lDashbord);
                }


            }catch(Exception Ex){
                Log.e(TAG, "Exception: " + Ex);
            }

            super.onPostExecute(result);

        }

    }
}
