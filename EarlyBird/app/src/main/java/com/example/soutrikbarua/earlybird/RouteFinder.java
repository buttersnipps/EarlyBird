package com.example.soutrikbarua.earlybird;

import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
        listener.onDirectionFinderStart();
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
            return null;
        }
    }


}
