package com.example.soutrikbarua.earlybird;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.app.Service;
import android.util.Log;


/**
 * Created by soutrikbarua on 2017-03-28.
 */

public class Ringtoneservice extends Service{

    MediaPlayer ringtone;
    Context context;


    int my_startId;
    boolean check;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        my_startId =startId;
        Log.i("LocalService", "Received start id " + my_startId + ": " + intent);

        //fetch the extra string values
        String state = intent.getExtras().getString("Extra");
        Log.e("Ringtone state is",state);

        assert state != null;
        switch (state) {
            case "alarm on":
                my_startId = 1;

                break;
            case "alarm off":
                my_startId = 0;
                break;
            default:
                my_startId = 0;
                break;
        }

        //if there is no ringtone and user pressed alarm on
        //ringtone should start playing
        if(!this.check && my_startId == 1){
            Log.e("No music","Music on");
            ringtone = MediaPlayer.create(this,R.raw.pager);
            ringtone.start();

            this.check=true;
            this.my_startId = 0;
        }

        else if(this.check && my_startId == 0){
            Log.e(" music","Music off");

            //stop the ringtone
            ringtone.stop();
            ringtone.reset();

            this.check=false;
            this.my_startId=0;
        }
        else if(!this.check && my_startId == 0 ){
            Log.e("No music","Music off");
            this.check = false;
            this.my_startId = 0;

        }
        else if(this.check && my_startId ==1){
            Log.e(" music","Music on ");
            this.check=true;
            this.my_startId=1;
        }
        else{
            Log.e("Else","why am I here");
        }
        return START_NOT_STICKY;
    }



}
