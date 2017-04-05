package com.example.soutrikbarua.earlybird;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by soutrikbarua on 2017-04-03.
 */

public class RouteAdapter extends BaseAdapter {
    private ArrayList<String> source;
    private ArrayList<String> destination;
     private ArrayList<String> duration;
    private Activity mActivity;

    public RouteAdapter(Activity activity, ArrayList<String>source,ArrayList<String>destination,ArrayList<String> duration){
        this.mActivity = activity;

        this.source=source;
        this.destination=destination;
        this.duration=duration;

    }

    @Override
    public int getCount() {
        return source.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        LayoutInflater layoutInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = layoutInflater.inflate(R.layout.route_list,null);
        TextView source = (TextView) row.findViewById(R.id.source);
        TextView destination = (TextView) row.findViewById(R.id.destination);
        TextView duration = (TextView)row.findViewById(R.id.duration);


        source.setText(this.source.get(position));
        destination.setText(this.destination.get(position));
        duration.setText(this.duration.get(position));


        return row;
    }
}
