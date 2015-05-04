package com.gathr.gathr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    private MyGlobals global;
    private Context context;

    NotificationReceiver(Context c){
        context = c;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //First call function to get info of best event
        //This will call the function to create and send a notification based on info

        //global.PushNotification(1,"Testing Notifications", "Aarsh its working", "Yayyyy", MapsActivity.class, );

        //PushNotification(int uniqueID, String tickerText, String nTitle, String nText, Class<?> cls,  Context c)

       Toast.makeText(context,"Heres a notification",Toast.LENGTH_SHORT).show();

    }
}
