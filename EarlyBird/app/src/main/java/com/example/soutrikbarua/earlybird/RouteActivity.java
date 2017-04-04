package com.example.soutrikbarua.earlybird;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.List;


public class RouteActivity extends AppCompatActivity implements RouteFinderListener{
    Uri my_request;
    AutoCompleteTextView source;
    AutoCompleteTextView dest;
    String my_source;
    String my_dest;
    private RouteAdapter adapter;
    ArrayList<String> list_source;
    ArrayList<String> list_destination;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

         source = (AutoCompleteTextView)findViewById(R.id.source_text);
         dest = (AutoCompleteTextView)findViewById(R.id.destination_text);
         my_request = Uri.parse("https://maps.googleapis.com/maps/api/directions/json?origin="+ my_source+
                "&destination="+my_dest+"&key=AIzaSyCb14xML7qnQ4AXGQ5ymUzgQwSmvcGa3IE");

        Button my_route = (Button)findViewById(R.id.setRoute);
        my_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendRequest();
               // Intent route_list_intent = new Intent(getApplicationContext(),RouteList.class);
               // startActivity(route_list_intent);
            }
        });

    }

    private void sendRequest(){
        my_source = source.getText().toString();
        my_dest = dest.getText().toString();
        try {
            new RouteFinder(this, my_source, my_dest,this).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onRouteFinderStart() {

    }

    @Override
    public void onRouteFinderSuccess(List<Route> route) {

    }



}

