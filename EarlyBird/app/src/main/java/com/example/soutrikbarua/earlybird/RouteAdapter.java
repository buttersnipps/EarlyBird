package com.example.soutrikbarua.earlybird;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by soutrikbarua on 2017-04-03.
 */

public class RouteAdapter extends BaseAdapter {
    private ArrayList<String> sourceArray;
    private ArrayList<String> destinationArray;
    private ArrayList<String> durationArray;
    private Activity mActivity;

    public RouteAdapter(Activity activity, ArrayList<String>source,ArrayList<String>destination,ArrayList<String> duration){
        this.mActivity = activity;

        this.sourceArray =source;
        this.destinationArray=destination;
        this.durationArray=duration;

    }

    @Override
    public int getCount() {
        return sourceArray.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        LayoutInflater layoutInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = layoutInflater.inflate(R.layout.route_list,null);
        TextView source = (TextView) row.findViewById(R.id.source);
        TextView destination = (TextView) row.findViewById(R.id.destination);
        final TextView duration = (TextView)row.findViewById(R.id.duration);


        source.setText(this.sourceArray.get(position));
        destination.setText(this.destinationArray.get(position));
        duration.setText(this.durationArray.get(position));

        row.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int rowPosition = position;
                String source = sourceArray.get(position);
                String destination =destinationArray.get(position);
                String duration =durationArray.get(position);
                //String output = "From " + source + " To "+ destination;

                Route obj = new Route();
               // obj.duration.value = Integer.parseInt(durationArray.get(position));
                obj.startAddress = sourceArray.get(position);
                obj.endAddress = destinationArray.get(position);


               /* SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(v.getContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("string_id",output);
                editor.commit();*/
                Intent routeSetIntent = new Intent(v.getContext(),MainActivity.class);
               // routeSetIntent.putExtra("route",(Serializable) obj);
                routeSetIntent.putExtra("Source",source);
                routeSetIntent.putExtra("Destination",destination);
                routeSetIntent.putExtra("My time",duration);
                mActivity.startActivity(routeSetIntent);



               // Log.e("row data:", String.valueOf(rowPosition) + "duration: " + String.valueOf(durationArray.get(position)) + " Route: " + output);
            }});
        return row;
    }
}
