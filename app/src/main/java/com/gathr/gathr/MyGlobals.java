package com.gathr.gathr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class MyGlobals {

    Context c;
    MyGlobals(Context _c){
        c = _c;
    }
    MyGlobals(){ }

    public String nDate(String uDate){// uDate = XXXX-XX-XX
        String[] temp = uDate.split("-");

        return temp[1] + "-" + temp[2] + "-" + temp[0];
    }

    public String mTime(String nTime){
        SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat nFormat = new SimpleDateFormat("h:mm a");

        try {
            Date d = nFormat.parse(nTime);
            return mFormat.format(d).toString();
        }catch(ParseException x){
            x.printStackTrace();
        }
        return "";
    }

    public String normalTime(String mTime){
        SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat nFormat = new SimpleDateFormat("h:mm a");

        try {
            Date d = mFormat.parse(mTime);
            return nFormat.format(d).toString();
        }catch(ParseException x){
            x.printStackTrace();
        }
        return "";
    }
    public void PushNotification(int uniqueID, String tickerText, String nTitle, String nText, Class<?> cls,  Context c){
        //Lets you build new notification
        NotificationCompat.Builder notification;

        notification = new NotificationCompat.Builder(c);
        notification.setAutoCancel(true); //This is to make the notification go away when you get to the proper intent screen
        notification.setSmallIcon(R.mipmap.ic_launcher); //Used to set picture or logo of app for the notification
        notification.setTicker(tickerText); //Notification Text
        notification.setWhen(System.currentTimeMillis()); // Notification Time
        notification.setContentTitle(nTitle);
        notification.setContentText(nText);

        //send them back to screen (in this case MainActivity
        Intent intent = new Intent(c, cls);
        //Gives device access to all intents in the app

        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        //Builds the notification and issues it
        NotificationManager nm = (NotificationManager) c.getSystemService(c.NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());

    }

}






