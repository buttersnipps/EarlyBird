package com.example.soutrikbarua.earlybird;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;


public class RouteActivity extends AppCompatActivity implements RouteFinderListener{
    Uri my_request;
    AutoCompleteTextView source;
    AutoCompleteTextView dest;
    String my_source;
    String my_dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

         source = (AutoCompleteTextView)findViewById(R.id.source_text);
         dest = (AutoCompleteTextView)findViewById(R.id.destination_text);





         my_request = Uri.parse("https://maps.googleapis.com/maps/api/directions/json?origin="+ my_source+
                "&destination="+my_dest+"&key=AIzaSyCb14xML7qnQ4AXGQ5ymUzgQwSmvcGa3IE");



    }

    private void sendRequest(){
        my_source = source.getText().toString();
        my_dest = dest.getText().toString();
        try {
            new RouteFinder(this, my_source, my_dest).execute();
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

