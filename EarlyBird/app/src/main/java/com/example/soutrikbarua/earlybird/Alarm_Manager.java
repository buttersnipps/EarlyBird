package com.example.soutrikbarua.earlybird;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by soutrikbarua on 2017-03-28.
 */

public class Alarm_Manager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("Hey","Im here");

        //fetch the key and value from the intent
        String f_string = intent.getExtras().getString("Extra");

        Log.e("Key",f_string);
        // an intent to the ringtone service
        Intent ringtone_intent = new Intent(context,Ringtoneservice.class);

        //pass the extra string from the activity to the Ringtoneservice class
        ringtone_intent.putExtra("Extra",f_string);

        //Start the ringtone service
        context.startService(ringtone_intent);


    }
}
