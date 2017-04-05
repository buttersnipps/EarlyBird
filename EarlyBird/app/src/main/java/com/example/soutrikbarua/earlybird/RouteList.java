package com.example.soutrikbarua.earlybird;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RouteList extends AppCompatActivity {
   private RouteAdapter adapter;
    ArrayList<String> rootsrc;
    ArrayList<String> rootdes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);

        Bundle bundle = (Bundle) getIntent().getExtras().getBundle("lists");


        ArrayList<String> list_duration = bundle.getStringArrayList("List1");
        ArrayList<String> list_destination = bundle.getStringArrayList("List2");
        ArrayList<String> list_source = bundle.getStringArrayList("List3");


        adapter = new RouteAdapter(this,list_source,list_destination,list_duration);


        ListView listView = (ListView)findViewById(R.id.myList);
        listView.setAdapter(adapter);


    }
}
