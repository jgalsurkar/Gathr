package com.gathr.gathr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import static java.sql.DriverManager.println;

public class QueryDB {

    protected String result = "";
    protected String user_id = "0";
    protected String fb_id = "0";

    QueryDB(String _fb_id){
        fb_id = _fb_id;
    }
    QueryDB(String _fb_id, String _user_id){
        fb_id = _fb_id;
        user_id = _user_id;
    }

    private class innerQueryDB extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String[] arg0) {
            try {
                String link = "http://aarshv.siteground.net/gathr_db.php?fid=" + fb_id + "&uid=" + user_id;

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

                //Read JSON from Server
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null)
                    sb.append(line + "\n");
                result = sb.toString();
                //Log.i("Test", sb.toString());
                return sb.toString();
            } catch (Exception e) {
                return "Exception: " + e.getMessage();
            }

        }
    }

    public void executeQuery(String query){
        Log.i("HHEREHERHHER", fb_id + " " + user_id);
        if(fb_id == "0"){
            //THROW
        }else {
            result = null;
            new innerQueryDB().execute(query);
        }
    }

    public String getResults() {//throws java.lang.Throwable{
        while(result == null){
            //We must wait till we actually have stuff
        }

        // if(result.contains("ERROR")){
        //    throw (result.split(":")[1].trim());
        //}

        return result;
    }

    public String escapeString(String escapeThis){
        escapeThis = escapeThis.replace("\\","\\\\");
        escapeThis = escapeThis.replace("'","\\'");
        escapeThis = escapeThis.replace("\"","\\\"");
        return escapeThis;
    }
}
