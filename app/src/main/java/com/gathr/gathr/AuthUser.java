package com.gathr.gathr;

public class AuthUser {
    public static String user_id = null;
    public static String fb_id = null;
    public static String user_fname = null;
    public static String user_lname = null;

    public void setUser(String _uid, String _fbid, String _fname, String _lname){
        user_id = _uid;
        fb_id = _fbid;
        user_fname = _fname;
        user_lname = _lname;
    }

}
