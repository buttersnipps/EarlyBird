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
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

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


public class RouteActivity extends AppCompatActivity implements RouteFinderListener{
    Uri my_request;
    AutoCompleteTextView source;
    AutoCompleteTextView dest;
    String my_source;
    String my_dest;
    List<Route> routes;
    private RouteAdapter adapter;
    ArrayList<String> list_source;
    ArrayList<String> list_destination;
    ArrayList<String> list_duration;

    public class RouteFinder{
        ArrayList<String> list_source;
        ArrayList<String> list_destination;
        ArrayList<String> list_duration;
        Activity activity;
        //Route route;
        private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
        private static final String GOOGLE_API_KEY ="AIzaSyCb14xML7qnQ4AXGQ5ymUzgQwSmvcGa3IE";
        private RouteFinderListener listener;
        private String origin;
        private String destination;


        public RouteFinder(RouteFinderListener listener, String origin, String destination , Activity activity){

            list_source = new ArrayList<>();
            list_destination = new ArrayList<>();
            list_duration = new ArrayList<>();
            this.listener = listener;
            this.origin=origin;
            this.destination=destination;
            this.activity = activity;
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

    }

    public class DownloadRawData extends AsyncTask<String,Void,String>{

        public DownloadRawData(){

        }
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

    /**
     * This is the method that parse the Json to be used by the local SQL database
     * Sets the Start and End addresses and the duration of the trip
     * @param data
     * @throws JSONException
     */
    private void JSONparser (String data) throws JSONException
    {
        if (data==null)
        {
            return;
        }

        routes = new ArrayList<>();
        Route route = new Route();

        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes  = jsonData.getJSONArray("routes");
        for (int i=0;i<jsonRoutes.length();i++)
        {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            route = new Route();

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
            //Adding the route start address,end address and duration to the database
            /**
             * Use the fourth parameter to pass what database action needs to be done.
             * Create = 1
             * Read = 2 (Read One, must pass Id with it)
             *        3 (Read All, No Id required)
             * Update = 4 (Update One, Id Required)
             * Delete = 5 (Delete One, Id Required)
             *
             * The fifth variables holds the Id of the row that needs to be Updated/Read/Deleted.
             * Send the Id accordingly or send zero when none of the operations are needed to be
             * done.
             */

        }
        AsyncSaveChanges asyncSaveChanges = new AsyncSaveChanges(route.startAddress,route.endAddress
                ,String.valueOf(route.duration.text),this);
        asyncSaveChanges.execute();

    }

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

    public int calculateDurationDifference(int databaseValue,int realtimeValue){
        return 0;
    }

//SQL database part


    // 1)Defining the schema
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME ="UserRoute";
        public static final String SOURCE_COLUMN ="Source";
        public static final String DESTINATION_COLUMN = "Destination";
        public static final String DURATION_COLUMN ="Duration";

    }
    public static final int DB_VERSION = 2;
    public static final String DB_NAME = "MyDatabase.db";

    //Obtaining references to the database
    public class FeedReaderDbHelper extends SQLiteOpenHelper {


        public FeedReaderDbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);//call the super (base) class's constructor
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            //System.out.println("Executing Query: SQL_CREATE_TABLE " + SQL_CREATE_ENTRIES);
            String CREATE_ROUTES_TABLE ="CREATE TABLE " + FeedEntry.TABLE_NAME + " ("
                    + FeedEntry._ID + " INTEGER PRIMARY KEY," + FeedEntry.SOURCE_COLUMN + " TEXT,"
                    + FeedEntry.DESTINATION_COLUMN+ " TEXT," + FeedEntry.DURATION_COLUMN+" " +
                    "INTEGER)";
            db.execSQL(CREATE_ROUTES_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS "+ FeedEntry.TABLE_NAME;
            db.execSQL(SQL_DROP_TABLE);
            onCreate(db);
        }
    }
    //Async task to store the data of the route to the database
    public class AsyncSaveChanges extends AsyncTask<String,Void,String>{
        String Json_source;
        String Json_destination;
        String Json_duration;
        int code;
        int rowId;
        //Database stuff
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(getApplicationContext());
        SQLiteDatabase db;
        Route oneRoute;
        Activity activity;

        /**
         * Use the fourth parameter to pass what database action needs to be done.
         * Create = 1
         * Read = 2 (Read One, must pass Id with it)
         *        3 (Read All, No Id required)
         * Update = 4 (Update One, Id Required)
         * Delete = 5 (Delete One, Id Required)
         *
         * The fifth variables holds the Id of the row that needs to be Updated/Read/Deleted.
         * Send the Id accordingly or send zero when none of the operations are needed to be
         * done.
         */
        public AsyncSaveChanges(String Json_source, String Json_destination, String
                Json_duration, Activity activity){
            this.Json_source = Json_source;
            this.Json_destination = Json_destination;
            this.Json_duration = Json_duration;
            this.activity = activity;

        }

        /**
         * Get one specific user route after calling the asynctask case statement 2
         */
        public Route getOneRoute()
        {
            return oneRoute;
        }


        @Override
        protected void onPostExecute(String s) {
                adapter.notifyDataSetChanged();
            super.onPostExecute(s);
            dbHelper.close();

        }

        @Override
        protected String doInBackground(String... params) {

            ContentValues values = new ContentValues();
            try{
                    //Create
                    if(Json_source != null) {
                            db = dbHelper.getWritableDatabase();
                            values.put(FeedEntry.SOURCE_COLUMN,Json_source);
                            values.put(FeedEntry.DESTINATION_COLUMN,Json_destination);
                            values.put(FeedEntry.DURATION_COLUMN,Json_duration);

                            System.out.println("Writing the data to database " + Json_source + " " +
                                    Json_destination
                                    + " " +Json_duration);

                            long newRowID = db.insert(FeedEntry.TABLE_NAME,null,values);
                            System.out.println("Result of database insertion " + newRowID);

                        }

                FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String [] query_column = {
                        FeedEntry._ID,
                        FeedEntry.SOURCE_COLUMN,
                        FeedEntry.DESTINATION_COLUMN,
                        FeedEntry.DURATION_COLUMN
                };


                String sortOrder = FeedEntry.SOURCE_COLUMN;

                Cursor cursor = db.query(FeedEntry.TABLE_NAME,
                        query_column,
                        null,
                        null,
                        null,
                        null,
                        sortOrder);

                boolean hasMoreData = cursor.moveToFirst();
                list_source.clear();
                list_destination.clear();
                list_duration.clear();

                while(hasMoreData){
                    long key = cursor.getLong(cursor.getColumnIndexOrThrow(FeedEntry._ID));
                    String source = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.SOURCE_COLUMN));
                    String destination = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.DESTINATION_COLUMN));
                    String duration = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.DURATION_COLUMN));
                    hasMoreData = cursor.moveToNext();
                    list_source.add(source);
                    list_destination.add(destination);
                    list_duration.add(duration);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

         source = (AutoCompleteTextView)findViewById(R.id.source_text);
         dest = (AutoCompleteTextView)findViewById(R.id.destination_text);
         my_request = Uri.parse("https://maps.googleapis.com/maps/api/directions/json?origin="+ my_source+
                "&destination="+my_dest+"&key=AIzaSyCb14xML7qnQ4AXGQ5ymUzgQwSmvcGa3IE");

        list_duration = new ArrayList<>();
        list_destination = new ArrayList<>();
        list_source = new ArrayList<>();
        adapter = new RouteAdapter(this,list_source,list_destination,list_duration);

        AsyncSaveChanges asyncSaveChanges = new AsyncSaveChanges(null,null,null,this);
        asyncSaveChanges.execute();

        Button addRoute = (Button)findViewById(R.id.addRoute);
        Button viewRoutes = (Button) findViewById(R.id.viewAll);

        addRoute.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        viewRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent route_list_intent = new Intent(getApplicationContext(),RouteList.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("List1",list_duration);
                bundle.putStringArrayList("List2",list_destination);
                bundle.putStringArrayList("List3",list_source);

                route_list_intent.putExtra("lists",bundle);
                startActivity(route_list_intent);

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

