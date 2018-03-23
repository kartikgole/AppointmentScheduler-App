package com.studio.android.utaappointmentscheduler;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import common_settings.HttpHandler;

/**
 * Created by Kartik on 27-11-2017.
 */

public class Department_Tab extends Fragment {
    private Context mContext;
    private static final String TAG = HttpHandler.class.getSimpleName();
    View  rootView;
    String netId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.department_tab, container, false);
        mContext=getActivity();
        netId=getActivity().getIntent().getStringExtra("netId");

        super.onCreate(savedInstanceState);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        new Department_Tab.StudentListTask().execute("UTASchedulerServer/sSearchController");


    }

    String ApptJsonString=null;
    private class StudentListTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];
            Log.e(TAG, "Exception: " +url);
            JSONObject jsonObj = new JSONObject();
            try {

                jsonObj.put("request", "get");
                jsonObj.put("netId", ""+netId);

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

            LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.dept_prof_list);
            ArrayList<String> arrayList=new ArrayList<String>();
            TextView textView;
            ListView listView;
            try{
                Log.e(TAG, "Exception1: " + ApptJsonString);
                jsonObj=new JSONObject(ApptJsonString);
                JSONArray List=jsonObj.getJSONArray("professors");
                for(int i=0;i<List.length();i++){
                    final JSONObject jsonObj2= (JSONObject)List.get(i);

                    textView=new TextView(getContext());
                    textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    textView.setText(jsonObj2.get("name").toString());
                    textView.setPadding(20, 20, 20, 20);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent_professor_slotList = new Intent(getContext(),course_details.class);

                            try{

                                intent_professor_slotList.putExtra("netId", netId);
                                intent_professor_slotList.putExtra("professorid", jsonObj2.get("pNetId").toString());
                                intent_professor_slotList.putExtra("professorname",   jsonObj2.get("name").toString());


                                //Log.e(TAG, "Exception: " + jsonObj2.get("pNetId").toString());

                            }catch(Exception Ex){
                                Log.e(TAG, "Exception: " + Ex);
                            }
                            getContext().startActivity(intent_professor_slotList);

                        }
                    });
                    linearLayout.addView(textView);
                }


            }catch(Exception Ex){
                Log.e(TAG, "Exception: " + Ex);
            }

            super.onPostExecute(result);

        }

    }
}
