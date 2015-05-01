package com.gathr.gathr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import static android.support.v4.app.ActivityCompat.startActivity;

public class MyGlobals {
    String[] titles = new String[]{"Map","Create Gathring", "My Profile","My Gathrings","Friends","Settings"};
    Class<?>[] links = { MapsActivity.class, CreateEvent.class, Profile.class, GathringsListActivity.class, FollowingList.class, Settings.class};
    Context c;

    MyGlobals(Context _c){ c = _c; loadUser();}
    MyGlobals(){  }

    public void loadUser(){
        if(AuthUser.user_id == null || AuthUser.user_id == ""){
            try {
                SharedPreferences settings = c.getSharedPreferences("AuthUser", 0);
                AuthUser.user_id = settings.getString("userid", "");
                AuthUser.fb_id = settings.getString("fbid", "");
                AuthUser.user_fname = settings.getString("fname", "");
                AuthUser.user_lname = settings.getString("lname", "");

                if (AuthUser.user_id.equals(""))
                    c.startActivity(new Intent(c, MainActivity.class));

            }catch(Exception e){
                //Intent intent = new Intent(c, MainActivity.class); // Send them back to login page
                //c.startActivity(intent);
            }
        }
    }

    public void checkInternet(){
        if(!isNetworkAvailable(c))
            c.startActivity(new Intent(c, ConnectionError.class));
    }
    public void checkInternet(Context c){
        if(!isNetworkAvailable(c))
            c.startActivity(new Intent(c, ConnectionError.class));
    }

    public boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
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
    public void errorHandler(Exception e){
        e.printStackTrace();
        tip(e.getClass() + ": " + e.getMessage());
        //Intent intent = new Intent(c, MainActivity.class);
        //c.startActivity(intent);
    }
    public void tip(String message){
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    public String getUserJSON(){
        String JSON = "";
        FileInputStream inputStream;
        try {
            if(!fileExists("UserFile")){
                JSON = getUserJSON(1);
            }else{
                inputStream = c.openFileInput("UserFile");

                InputStreamReader in = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(in);
                JSON = br.readLine();

                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON;
    }
    public String getUserJSON(int x){
        String JSON = "";
        FileInputStream inputStream;
        try {
            QueryDB DBconn = new QueryDB(c, AuthUser.fb_id, AuthUser.user_id);
            DBconn.executeQuery("SELECT * FROM USERS WHERE Id = " + AuthUser.user_id + ";");
            JSON = DBconn.getResults();
            setUserJSON(JSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSON;
    }

    public void setUserJSON(String JSON){
        FileOutputStream outputStream;
        try {
            outputStream = c.openFileOutput("UserFile", Context.MODE_PRIVATE);
            outputStream.write(JSON.getBytes());
            outputStream.close();
        } catch (Exception e) {
            errorHandler(e);
        }

    }

    public boolean fileExists( String filename) {
        File file = c.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }
}