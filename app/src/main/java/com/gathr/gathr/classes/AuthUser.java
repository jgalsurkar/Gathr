/**************************************************************************************************
 Title : AuthUser.java
 Author : Gathr Team
 Purpose : Class used for instantiating/loading a user and retrieving some of their information
 *************************************************************************************************/

package com.gathr.gathr.classes;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthUser {

    private static String user_id = null;
    private static String fb_id = null;
    private static String user_fname = null;
    private static String user_lname = null;


    public static String getFullName(Context c){
        if(user_id == null)
            loadUser(c);

        if(user_id == null)
            return "0";

        return user_fname + " " + user_lname;
    }

    public static String getLogin(Context c){
        if(user_id == null)
            loadUser(c);

        if(user_id == null)
            return "0";


        return user_fname  + user_id;
    }
    public static String getUserId(Context c){
        if(user_id == null)
            loadUser(c);

        if(user_id == null)
            return "0";


        return user_id;
    }
    public static String getFBId(Context c){
        if(user_id == null)
            loadUser(c);

        if(user_id == null)
            return "0";

        return fb_id;
    }

    public static void setUser(Context c, String _uid, String _fbid, String _fname, String _lname){
        user_id = _uid;
        fb_id = _fbid;
        user_fname = _fname;
        user_lname = _lname;
        SharedPreferences settings = c.getSharedPreferences("AuthUser", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("notifications", false);
        editor.apply();
        editor.putString("userid", user_id);
        editor.putString("fbid",  fb_id);
        editor.putString("fname", user_fname);
        editor.putString("lname", user_lname);
        editor.commit();
    }

    public static void loadUser(Context c){
        SharedPreferences settings = c.getSharedPreferences("AuthUser", 0);
        user_id = settings.getString("userid", "");
        fb_id = settings.getString("fbid", "");
        user_fname = settings.getString("fname", "");
        user_lname = settings.getString("lname", "");
    }
}