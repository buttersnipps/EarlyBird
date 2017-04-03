package com.example.soutrikbarua.earlybird;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

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

    ArrayList<String> list_source;
    ArrayList<String> list_destination;
    ArrayList<String> list_duration;
    List<Route> routes;
    //Route route;
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY ="AIzaSyCb14xML7qnQ4AXGQ5ymUzgQwSmvcGa3IE";
    private RouteFinderListener listener;
    private String origin;
    private String destination;
    private Activity activity;

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

         routes = new ArrayList<>();

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


            //Adding the route start address,end address and duration to the database
            AsyncSaveChanges routesaves = new AsyncSaveChanges("Hello","World"
            ,"Java");
            routesaves.execute();
        }
    }

//SQL database part


    // 1)Defining the schema
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME ="UserRoute";
        public static final String SOURCE_COLUMN ="Source";
        public static final String DESTINATION_COLUMN = "Destination";
        public static final String DURATION_COLUMN ="Duration";

    }
    //2)Creating a table
    public static final String SQL_CREATE_ENTIRIES ="CREATE TABLE " + FeedEntry.TABLE_NAME + " ("
            +FeedEntry._ID + " INTEGER PRIMARY KEY," + FeedEntry.SOURCE_COLUMN + " TEXT,"
            + FeedEntry.DESTINATION_COLUMN+ " TEXT," + FeedEntry.DURATION_COLUMN+"TEXT )";
    //3)Deleting a table
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    //Obtaining references to the database
    public class FeedReaderDbHelper extends SQLiteOpenHelper {

        public static final int DB_VERSION = 1;
        public static final String DB_NAME = "MyDatabase.db";

        public FeedReaderDbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);//call the super (base) class's constructor
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            System.out.println("Executing Query: SQL_CREATE_TABLE " + SQL_CREATE_ENTIRIES);
            //db.execSQL(SQL_DELETE_ENTRIES);
            //execute the query on the databse
            db.execSQL(SQL_CREATE_ENTIRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

    }

    //Async task to store the data of the route to the database
    private class AsyncSaveChanges extends AsyncTask<String,Void,String> {

        private String Json_source;
        private String Json_destination;
        private String Json_duration;

        public AsyncSaveChanges(String Json_source,String Json_destination,String Json_duration){
            this.Json_source = Json_source;
            this.Json_destination = Json_destination;
            this.Json_duration = Json_duration;
        }
        @Override
        protected void onPreExecute() {


        }

        @Override
        protected String doInBackground(String... params) {
            if(Json_source != null){
                try{
                FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(activity);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues newrow = new ContentValues();
                newrow.put(FeedEntry.SOURCE_COLUMN,Json_source);
                newrow.put(FeedEntry.DESTINATION_COLUMN,Json_destination);
                newrow.put(FeedEntry.DURATION_COLUMN,Json_duration);


                System.out.println("Writing the data to database" + Json_source + " " + Json_destination
                        + " " +Json_duration);

                long newRowID = db.insert(FeedEntry.TABLE_NAME,null,newrow);
                System.out.println("Result of database insertion " + newRowID);
            }catch (Exception e){
            e.printStackTrace();
            }
            }
            FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(activity);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            //Define which columns to include to our query
            String[] query_columns = {
                    FeedEntry._ID,
                    FeedEntry.SOURCE_COLUMN,
                    FeedEntry.DESTINATION_COLUMN,
                    FeedEntry.DURATION_COLUMN
            };

            //Construct a select query (String)
            String selectQuery = FeedEntry.DESTINATION_COLUMN + " =?";

            //add arguments to the selected list
            String[] selectedList = {"Filter String"};

            //define how we want the database to be ordered
            String sortOrder = FeedEntry.DESTINATION_COLUMN + "DESC";

            //from the database ..get a cursor object
           /* Cursor cursor = db.query(
                    FeedEntry.TABLE_NAME,
                    query_columns,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );*/

            Cursor cursor = db.rawQuery("SELECT * FROM UserRoute;", null);
            String[] columnNames = cursor.getColumnNames();

            //if the database has more data move the cursor to the first object
            boolean hasMoreData = cursor.moveToFirst();
            list_source.clear();
            list_destination.clear();
            list_duration.clear();

            while(hasMoreData){
                //get the value out of each column
                long key = cursor.getLong(cursor.getColumnIndexOrThrow(FeedEntry._ID));
                String source = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.SOURCE_COLUMN));
                String destination = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.DESTINATION_COLUMN));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.DURATION_COLUMN));

                System.out.println("Key" + key + "Source" + source + "Destination" + destination +"Duration" + duration );

                //move to the next row
                hasMoreData = cursor.moveToNext();
                list_source.add(source);
                list_destination.add(destination);
                list_duration.add(duration);
            }
            return null;
        }
    }




}
