package com.gathr.gathr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by JohnnyXD1 on 4/9/2015.
 */
public class MyGlobals {

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
}
