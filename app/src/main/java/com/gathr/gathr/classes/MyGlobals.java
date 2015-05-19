/**************************************************************************************************
 Title : MyGlobals.java
 Author : Gathr Team
 Purpose : Class that provides general functionality used across the app such as testing
           network connectivity, handling errors, getting JSON objects from a database query, etc.
 *************************************************************************************************/

package com.gathr.gathr.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.gathr.gathr.ConnectionError;
import com.gathr.gathr.database.DatabaseCallback;
import com.gathr.gathr.database.QueryDB;

public class MyGlobals {
    Context c;

    public MyGlobals(Context _c){ c = _c; }
    public MyGlobals(){  }

    //Functions to check if the phone is connected to the internet
    public void checkInternet(){
        if(!isNetworkAvailable(c))
            c.startActivity(new Intent(c, ConnectionError.class));
    }
    public boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //Functions to convert time and date from "Computer" language to Normal Language
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

    //Our Universal errorHandler
    public void errorHandler(Exception e){
        e.printStackTrace();
        tip(e.getClass() + ": " + e.getMessage());
        //Intent intent = new Intent(c, MainActivity.class);
        //c.startActivity(intent);
    }

    //A function to quickly create a Toast Message
    public void tip(String message){
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }


    //Cache Functions
    private String JSON = "";
    public String getUserJSON() { return getJSON("UserFile", "SELECT * FROM USERS WHERE Id = " + AuthUser.getUserId(c) + ";", false); }
    public String getUserJSON(int x) { return getJSON("UserFile", "SELECT * FROM USERS WHERE Id = " + AuthUser.getUserId(c) + ";", true); }
    public String getFollowersJSON() { return getJSON("Followers", "SELECT Facebook_Id, First_Name, Last_Name, Id, Friend_User_Id FROM (USERS JOIN (SELECT  Friend_User_Id FROM FRIENDS WHERE User_Id = "+AuthUser.getUserId(c)+" )  AS JOINED) WHERE Id = Friend_User_Id", false); }
    public String getFollowersJSON(int x) { return getJSON("Followers", "SELECT Facebook_Id, First_Name, Last_Name, Id, Friend_User_Id FROM (USERS JOIN (SELECT  Friend_User_Id FROM FRIENDS WHERE User_Id = "+AuthUser.getUserId(c)+" )  AS JOINED) WHERE Id = Friend_User_Id", true); }

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
        JSON = "";
        FileInputStream inputStream;
        try {
            if(!fileExists(fileName) || force){
                try {
                    QueryDB DBconn = new QueryDB(c);

                    class get implements DatabaseCallback {
                        public void onTaskCompleted(String j) {
                            JSON = j; setJSON(fileName, j);
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
        while(JSON.equals(""));
        return JSON;


    }
}