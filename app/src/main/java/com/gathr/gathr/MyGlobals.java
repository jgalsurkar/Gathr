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

import com.gathr.gathr.chat.ui.activities.SplashActivity;

import static android.support.v4.app.ActivityCompat.startActivity;

public class MyGlobals {
    String[] titles = new String[]{"Map","Create Gathring", "My Profile","My Gathrings","Friends","Settings"};
    Class<?>[] links = { MapsActivity.class, CreateEvent.class, Profile.class, GathringsListActivity.class, FollowingList.class, SplashActivity.class};
    Context c;

    MyGlobals(Context _c){ c = _c; }
    MyGlobals(){  }

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

    public void errorHandler(Exception e){
        e.printStackTrace();
        tip(e.getClass() + ": " + e.getMessage());
        //Intent intent = new Intent(c, MainActivity.class);
        //c.startActivity(intent);
    }
    public void tip(String message){
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }




    public String getUserJSON() { return getJSON("UserFile", "SELECT * FROM USERS WHERE Id = " + AuthUser.user_id + ";", false); }
    public String getUserJSON(int x) { return getJSON("UserFile", "SELECT * FROM USERS WHERE Id = " + AuthUser.user_id + ";", true); }
    public String getFollowersJSON() { return getJSON("Followers", "SELECT Facebook_Id, First_Name, Last_Name, Id, Friend_User_Id FROM (USERS JOIN (SELECT  Friend_User_Id FROM FRIENDS WHERE User_Id = "+AuthUser.user_id+" )  AS JOINED) WHERE Id = Friend_User_Id", false); }
    public String getFollowersJSON(int x) { return getJSON("Followers", "SELECT Facebook_Id, First_Name, Last_Name, Id, Friend_User_Id FROM (USERS JOIN (SELECT  Friend_User_Id FROM FRIENDS WHERE User_Id = "+AuthUser.user_id+" )  AS JOINED) WHERE Id = Friend_User_Id", true); }

    public void setJSON(String fileName, String JSON){
        FileOutputStream outputStream;
        try {
            outputStream = c.openFileOutput(fileName, Context.MODE_PRIVATE);
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
    public String getJSON(final String fileName, String query, boolean force){
        String JSON = "";
        FileInputStream inputStream;
        try {
            if(!fileExists(fileName) || force){
                try {
                    QueryDB DBconn = new QueryDB(c, AuthUser.fb_id, AuthUser.user_id);

                    class get implements DatabaseCallback{
                        public void onTaskCompleted(String JSON) {
                            setJSON(fileName, JSON);
                        }
                    }

                    DBconn.executeQuery(query, new get());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                inputStream = c.openFileInput(fileName);

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
}