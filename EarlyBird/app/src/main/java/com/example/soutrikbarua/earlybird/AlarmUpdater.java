package com.example.soutrikbarua.earlybird;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by faiqm on 2017-04-13.
 */

public class AlarmUpdater extends BroadcastReceiver {

    String timeString;
    int timeDiff;
    String urlLink;
    int tempTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        urlLink = intent.getStringExtra("UrlLink");
        tempTime = intent.getIntExtra("tripTimeInSec",0);
        startGoogleService service = new startGoogleService();
        service.execute(urlLink);
        Intent returnIntent = new Intent(context,MainActivity.class);
        returnIntent.putExtra("timeDiff",timeDiff);
        returnIntent.putExtra("timeString",timeString);
        context.startActivity(returnIntent);
    }
    /**
     * This class request the current service from Google
     */
    public class startGoogleService extends AsyncTask<String,Void,String> {

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

        @Override
        protected void onPostExecute(String s) {
        // super.onPostExecute(s);
            try {
                timeDiff=JsonDurationParser(s);
                timeString = ConvertToHrsAndMin(calculateDurationDifference(tempTime,timeDiff));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param databaseValue int value in seconds
     * @param realTimeValue int value in seconds
     * @return
     */
    public int calculateDurationDifference(int databaseValue,int realTimeValue){
        int seconds = realTimeValue - databaseValue;
        if(seconds>0)
        {
            return seconds;
        }
        return 0;
    }

    public String ConvertToHrsAndMin(int timeInSec)
    {

        String time;
        int seconds = timeInSec;
        int hours = seconds/3600;
        seconds = seconds%3600;
        int minutes = seconds/60;
        time = hours+" ";
        if(hours>1)
        {
            time = time + "hrs ";
        }
        else
        {
            time = time + "hr ";
        }

        time = time + minutes +" min";
        return time;

    }

    /**
     * This function will send request to the Google directions API to fetch the current duration of the set route
     * @param data
     * @return
     * @throws JSONException
     */

    public int JsonDurationParser(String data) throws JSONException{
        int tempDuration = 0;
        if (data==null)
        {
            return tempDuration;
        }

        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes  = jsonData.getJSONArray("routes");
        for (int i=0;i<jsonRoutes.length();i++)
        {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            /*
             * This is where we retrieve the data from the JSON file we downloaded from google maps.
             */
            //JSONObject overview_polyline_json = jsonRoute.getJSONObject("overview_polyline");
            JSONArray legs_json = jsonRoute.getJSONArray("legs");
            JSONObject leg_json = legs_json.getJSONObject(0);
            JSONObject duration_json = leg_json.getJSONObject("duration");
            tempDuration= duration_json.getInt("value");

        }
        return tempDuration;
    }

}
