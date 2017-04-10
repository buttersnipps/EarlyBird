package com.example.soutrikbarua.earlybird;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    TimePicker timePicker;
    AlarmManager alarmManager;
    Context context;
    PendingIntent pendingIntent;
    Uri request;
    String src;
    String dest;

    /**
     * This class request the current service from Google
     */
    public class startGoogleService extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try{
                URL myurl = new URL(link);
                InputStream inputStream= myurl.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null)
                {
                    buffer.append(line+"\n");
                }

                return buffer.toString();
            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }


            return null;
        }
    }

    public void routeProcess(String src,String dest){
        this.src=src;
        this.dest=dest;

    }





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

      /*  SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String data = pref.getString("string_id","no id");*/
        String extracted_source = getIntent().getStringExtra("Source");
        String extracted_destination = getIntent().getStringExtra("Destination");
        String time=getIntent().getStringExtra("My time");
        request =Uri.parse("https://maps.googleapis.com/maps/api/directions/json?origin="
        + extracted_source+"&destination="+extracted_destination+
                "&key=AIzaSyCb14xML7qnQ4AXGQ5ymUzgQwSmvcGa3IE");


        String data = "From" + extracted_source + "To" + extracted_destination;

        if(extracted_source == null && extracted_destination == null){
            data = "No route selected";
        }


        //this is where the current route is set
        TextView mydata = (TextView)findViewById(R.id.myRoute);

        mydata.setText(data);
        System.out.println(data);
        routeProcess(extracted_source,extracted_destination);



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
                //pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarm_intent,
                // PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,
                        alarm_intent,0);
                //set the alarm manager
                //alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                // pendingIntent);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis()-(18*1000), pendingIntent);
               /* alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis()-(18*1000),2000,pendingIntent);*/


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
        ArrayList<String> list_source;
        ArrayList<String> list_destination;
        ArrayList<String> list_duration;
        RouteAdapter adapter;
        list_duration = new ArrayList<>();
        list_destination = new ArrayList<>();
        list_source = new ArrayList<>();
        adapter = new RouteAdapter(this,list_source,list_destination,list_duration);



//        RouteActivity routeActivity = new RouteActivity();
//        routeActivity.executeAsync();
        final Bundle bundle = new Bundle();
        bundle.putStringArrayList("List1",list_duration);
        bundle.putStringArrayList("List2",list_destination);
        bundle.putStringArrayList("List3",list_source);
        adapter.notifyDataSetChanged();


        Button pick_route =(Button) findViewById(R.id.routePick);

        pick_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pick_route = new Intent(getApplicationContext(),RouteActivity.class);

                pick_route.putExtra("flag",1);
                startActivity(pick_route);
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
