package com.example.soutrikbarua.earlybird;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;
import java.util.logging.StreamHandler;

public class MainActivity extends AppCompatActivity {
    TimePicker timePicker;
    AlarmManager alarmManager;
    Context context;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_layout);
        Toolbar toolbar = (Toolbar)findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);

        this.context = this;
        //initilize alarm service

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //initiate our timepicker
        timePicker = (TimePicker)findViewById(R.id.timePicker);

        //Create Instance of a Calender
        final Calendar calendar = Calendar.getInstance();


        //Create an intent to the Alarm_Manager class
       final Intent alarm_intent =new Intent(getApplicationContext(),Alarm_Manager.class);
        //Initilize the start alarm button
        Button start_alarm = (Button) findViewById(R.id.setAlarm);



        //Onclick listener to set the alarm
        start_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setting calender instance with the hour and minutes we set
                calendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
                calendar.set(Calendar.MINUTE,timePicker.getMinute());

                //get the value of the hours and minutes
                int hour = timePicker.getHour();
                int minutes =timePicker.getMinute();



                //convert the int values to string
                String hour_string = String.valueOf(hour);
                String minutes_string =String.valueOf(minutes);

                if(hour > 12){
                    hour_string = String.valueOf(hour-12);
                }

                if(minutes < 10){
                    minutes_string = "0" + String.valueOf(minutes);
                }
                //put an extra string and pass it through the intent
                //tell the app user has pressed the on button

                alarm_intent.putExtra("Extra","alarm on");

                //create a pending intent that delays the intent
                //until the specified calender time
                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,
                        alarm_intent,PendingIntent.FLAG_UPDATE_CURRENT);

                //set the alarm manager
                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);



                Toast.makeText(getApplicationContext(),"Alarm set to " + hour_string+ ":"
                        +minutes_string,Toast.LENGTH_LONG).show();



            }
        });



        //Initilize the stop alarm button
        Button stop_alarm = (Button) findViewById(R.id.stopAlarm);

        //Onclick listener to stop the alarm
        stop_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel the intent
                alarmManager.cancel(pendingIntent);

                //put an extra string and pass it through the intent
                //tell the app user has pressed the off button

                alarm_intent.putExtra("Extra","alarm off");

                //stop the ringtone
                sendBroadcast(alarm_intent);


            }
        });

        Button set_route = (Button) findViewById(R.id.setRoute);

        set_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent route_intent = new Intent(getApplicationContext(),RouteActivity.class);
                startActivity(route_intent);
            }
        });






    }
}
