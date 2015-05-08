package com.gathr.gathr;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthUser {
    public static String user_id = null;
    public static String fb_id = null;
    public static String user_fname = null;
    public static String user_lname = null;
    public static String latitude = null;
    public static String longitude = null;

    public static void setUser(Context c, String _uid, String _fbid, String _fname, String _lname, String _lat, String _lon){
        user_id = _uid;
        fb_id = _fbid;
        user_fname = _fname;
        user_lname = _lname;
        latitude = _lat;
        longitude = _lon;
        SharedPreferences settings = c.getSharedPreferences("AuthUser", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userid", user_id);
        editor.putString("fbid",  fb_id);
        editor.putString("fname", user_fname);
        editor.putString("lname", user_lname);
        editor.putString("lat", latitude);
        editor.putString("lon", longitude);
        editor.commit();
    }

    public static void loadUser(Context c){
        SharedPreferences settings = c.getSharedPreferences("AuthUser", 0);
        user_id = settings.getString("userid", "");
        fb_id = settings.getString("fbid", "");
        user_fname = settings.getString("fname", "");
        user_lname = settings.getString("lname", "");
        latitude = settings.getString("lat", "");
        longitude = settings.getString("lon", "");

    }
}