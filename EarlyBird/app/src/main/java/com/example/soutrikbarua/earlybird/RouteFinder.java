package com.example.soutrikbarua.earlybird;

import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by soutrikbarua on 2017-03-31.
 */

public class RouteFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY ="AIzaSyCb14xML7qnQ4AXGQ5ymUzgQwSmvcGa3IE";
    private RouteFinderListener listener;
    private String origin;
    private String destination;

    public RouteFinder(RouteFinderListener listener,String origin,String destination){
        this.listener = listener;
        this.origin=origin;
        this.destination=destination;
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onRouteFinderStart();
        new DownloadRawData().execute(createUrl());
    }
    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");

        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY;
    }

    private class DownloadRawData extends AsyncTask<String,Void,String>{



        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream inputStream =url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null)
                {
                    buffer.append(line+"\n");
                }

                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONparser(s);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void JSONparser (String data) throws JSONException
    {
        if (data==null)
        {
            return;
        }

        List<Route> routes = new ArrayList<>();

        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes  = jsonData.getJSONArray("routes");
        for (int i=0;i<jsonRoutes.length();i++)
        {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Route route = new Route();

            /*
             * This is where we retrieve the data from the JSON file we downloaded from google maps.
             */
            //JSONObject overview_polyline_json = jsonRoute.getJSONObject("overview_polyline");
            JSONArray legs_json = jsonRoute.getJSONArray("legs");
            JSONObject leg_json = legs_json.getJSONObject(0);
            JSONObject distance_json = leg_json.getJSONObject("distance");
            JSONObject duration_json = leg_json.getJSONObject("duration");
            JSONObject start_location_json = leg_json.getJSONObject("start_location");
            JSONObject end_location_json = leg_json.getJSONObject("end_location");

            route.distance = new Distance(distance_json.getString("text"),distance_json.getInt
                    ("value"));
            route.duration = new Duration(duration_json.getString("text"),duration_json.getInt
                    ("value"));
            route.endAddress = leg_json.getString("end_address");
            route.startAddress = leg_json.getString("start_address");
            route.startLocation = new LatLng(start_location_json.getDouble("lat"),
                    start_location_json.getDouble("lng"));
            route.endLocation = new LatLng(end_location_json.getDouble("lat"),end_location_json
                    .getDouble("lng"));

            routes.add(route);
        }
    }

}
