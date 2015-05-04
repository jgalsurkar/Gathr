package com.gathr.gathr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import static java.sql.DriverManager.println;

public class QueryDB {

    protected String result = null;
    protected String user_id = "0";
    protected String fb_id = "0";
    String link;
    boolean custom = false;

    MyGlobals global;
    QueryDB(Context c, String _fb_id){
        fb_id = _fb_id;
        global = new MyGlobals(c);
        link = "http://aarshv.siteground.net/webservice.php?fid=" + fb_id + "&uid=" + user_id;

    }
    QueryDB(Context c, String path, boolean _custom){
        custom = true;
        global = new MyGlobals(c);
        link = "http://aarshv.siteground.net/" + path;

    }
    QueryDB(Context c, String _fb_id, String _user_id){
        global = new MyGlobals(c);
        fb_id = _fb_id;
        user_id = _user_id;
        link = "http://aarshv.siteground.net/webservice.php?fid=" + fb_id + "&uid=" + user_id;

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
                String line = null;

                while ((line = reader.readLine()) != null)
                    sb.append(line + "\n");

                byte[] decoded = Base64.decode(sb.toString().getBytes("CP1252"), Base64.DEFAULT);
                //result = new String(decoded,"CP1252");

                listener.onTaskCompleted( new String(decoded,"CP1252"));

                return result;
            } catch (Exception e) {
                return "Exception: " + e.getMessage();
            }

        }
    }

    public void executeQuery(String query, DatabaseCallback callback) throws GathrException{
        global.checkInternet();
        if(!custom && fb_id.equals("0")){
            throw new GathrException("NO FID - PERMISSION DENIED");
        }else {
            result = null;
            new innerQueryDB(callback).execute(query);
        }
    }

    public void executeQuery(String query) throws GathrException{
       /* global.checkInternet();
        if(!custom && fb_id.equals("0")){
            throw new GathrException("NO FID - PERMISSION DENIED");
        }else {
            result = null;*/
        class dothis implements DatabaseCallback{
            public void onTaskCompleted(String results){
                result = results;
            }
        }
        executeQuery(query, new dothis());
        // }
    }


    public String getResults() throws GathrException{
        global.checkInternet();
        while(result == null){
            //We must wait till we actually have stuff
        }

        if(result.contains("ERROR")){
            throw new GathrException(result.split(":")[1].trim());
        }

        return result;
    }

    public String escapeString(String escapeThis){
        escapeThis = escapeThis.replace("\\","\\\\");
        escapeThis = escapeThis.replace("'","\\'");
        escapeThis = escapeThis.replace("\"","\\\"");
        return escapeThis;
    }
}