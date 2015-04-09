package com.gathr.gathr;
import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;
        import java.net.URI;
        import java.net.URL;
        import java.net.URLConnection;
        import java.net.URLEncoder;
        import android.os.AsyncTask;
        import android.util.Log;
        import static java.sql.DriverManager.println;

public class QueryDB {

    protected String result = "";

    private class innerQueryDB extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] arg0) {
            try {
                String link = "http://aarshv.siteground.net/gathr_db.php";
                String data = URLEncoder.encode("query", "UTF-8") + "=" + URLEncoder.encode(arg0[0], "UTF-8");

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
        result = null;
        new innerQueryDB().execute(query);
    }

    public String getResults(){
        while(result == null){
            //We must wait till we actually have stuff
        }
        //Log.i("Test3", result);
        return result;
    }


}
