package com.studio.android.utaappointmentscheduler;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import common_settings.HttpHandler;

public class Dashboard extends AppCompatActivity  {
    private static final String TAG = HttpHandler.class.getSimpleName();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    String Username="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Username= getIntent().getStringExtra("netId");

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("SUMMARY"));
        tabLayout.addTab(tabLayout.newTab().setText("DEPARTMENT"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


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
            case R.id.Settings:
                Intent intent_settings = new Intent(this,settings.class);
                this.startActivity(intent_settings);
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

     /*  //noinspection SimplifiableIfStatement
        if (id == R.id.drawer_layout) {
            return true;
        }

        return super.onOptionsItemSelected(item);   */



    public void perform_action(View v)
    {


       /* AlertDialog.Builder mBuilder = new AlertDialog.Builder(Dashboard.this);
        View mView = getLayoutInflater().inflate(R.layout.course_dialog,null);
        TextView tv= (TextView) mView.findViewById(R.id.textView15);
        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show(); */

        //TextView txtView15=(TextView)findViewById(R.id.textView15);
        //Intent loginIntent = new Intent(Dashboard.this, course_details.class);
        //Dashboard.this.startActivity(loginIntent);
        //Toast.makeText(getApplicationContext(),"text clicked", Toast.LENGTH_LONG).show();
        Intent intent_course_expand = new Intent(this,course_details.class);
        this.startActivity(intent_course_expand);

    }


    public void perform_action1(View v)
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Dashboard.this);
        View mView = getLayoutInflater().inflate(R.layout.appointment_dialog,null);
        //TextView tv= (TextView) mView.findViewById(R.id.btnAddExpense);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        //Button btnModify= (Button) mView.findViewById(R.id.buttonModify);







    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Summary_Tab summary = new Summary_Tab();
                    return summary;

                case 1:
                    Department_Tab department = new Department_Tab();
                    return department;

                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle (int position)
        {
            switch(position)
            {
                case 0:
                    return "SUMMARY";
                case 1:
                    return "DEPARTMENT";
            }
            return null;
        }

    }


}

