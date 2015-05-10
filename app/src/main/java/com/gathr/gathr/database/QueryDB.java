/**************************************************************************************************
 Title : QueryDB.java
 Author : Gathr Team
 Purpose : Class that accesses the webservice for Gathr, providing all database functionality
 *************************************************************************************************/

package com.gathr.gathr.database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.gathr.gathr.classes.AuthUser;
import com.gathr.gathr.classes.MyGlobals;

public class QueryDB {

    MyGlobals global;
    String link;

    public QueryDB(Context c){
        global = new MyGlobals(c);
        link = "http://aarshv.siteground.net/webservice.php?fid=" + AuthUser.getFBId(c) + "&uid=" + AuthUser.getUserId(c);
    }

    public QueryDB(Context c, String path){
        global = new MyGlobals(c);
        link = "http://aarshv.siteground.net/" + path;
    }

    private class innerQueryDB extends AsyncTask<String, Object, String> {
        DatabaseCallback listener;
        innerQueryDB(DatabaseCallback _listener){
            listener = _listener;
        }

        @Override
        protected String doInBackground(String[] arg0) {
            try {
                byte[] encoded = Base64.encode(arg0[0].getBytes("CP1252"), Base64.DEFAULT);
                String str = new String(encoded, "CP1252");
                String data = URLEncoder.encode("query", "UTF-8") + "=" + URLEncoder.encode(str, "UTF-8");

                //Open URL
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                //Send POST Arguments
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                //Read Encoded JSON from Server
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null)
                    sb.append(line).append("\n");

                byte[] decoded = Base64.decode(sb.toString().getBytes("CP1252"), Base64.DEFAULT);

                String result = new String(decoded,"CP1252");
                listener.onTaskCompleted( result );

                return result;
            } catch (Exception e) {
                return "Exception: " + e.getMessage();
            }
        }
    }

    public void executeQuery(String query, DatabaseCallback callback){
        global.checkInternet();
        new innerQueryDB(callback).execute(query);
    }

    public void executeQuery(String query){ //For queries where the result doesnt matter
       class doThis implements DatabaseCallback{
            public void onTaskCompleted(String results){
                //They do not care about the result
            }
        }
        executeQuery(query, new doThis());
    }

  public String escapeString(String escapeThis){
        escapeThis = escapeThis.replace("\\","\\\\");
        escapeThis = escapeThis.replace("'","\\'");
        escapeThis = escapeThis.replace("\"","\\\"");
        return escapeThis;
    }
}