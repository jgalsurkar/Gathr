package com.gathr.gathr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ListActivity;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;

public class GathringsList extends ListActivity {

    QueryDB DBConn = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
    MyGlobals global = new MyGlobals(this);

    static String[] eventNames;
    static String[] eventDescriptions;
    static String[] eventIds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String results = "";
        try{
            DBConn.executeQuery("SELECT DISTINCT Id, `Name`, `Desc`  FROM (EVENTS JOIN (SELECT Event_Id FROM JOINED_EVENTS WHERE JOINED_EVENTS.User_Id = "+AuthUser.user_id+") AS JOINED) WHERE (((EVENTS.Id = JOINED.Event_Id) OR EVENTS.Organizer = "+AuthUser.user_id+") AND (EVENTS.Date > DATE(NOW()) OR (EVENTS.Date = DATE(NOW()) AND EVENTS.TIME >= time(NOW()))));");
            results = DBConn.getResults();
            JSONArray json = new JSONArray(results);
            int numEvents = json.length();
            eventNames = new String[numEvents];
            eventDescriptions = new String[numEvents];
            eventIds = new String[numEvents];

            for (int i = 0; i < json.length(); i++) {
                eventIds[i] = json.getJSONObject(i).getString("Id");
                eventNames[i] = json.getJSONObject(i).getString("Name");
                eventDescriptions[i] = json.getJSONObject(i).getString("Desc");
            }
        }catch(Exception e){
            if(e.getMessage() == "NO RESULTS") {
                eventNames = new String[]{"Sorry"};
                eventDescriptions = new String[]{"You are not part of any events"};
                eventIds = new String[]{"-1"};
            }else{
                global.errorHandler(e);
            }
        }

        setListAdapter(new GathringArrayAdapter(this, eventNames, eventDescriptions));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String eventID = eventIds[position];
        if(eventID != "-1") {
            Intent i = new Intent(this, ViewGathring.class);
            i.putExtra("eventId", eventID);
            startActivity(i);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gathrings_list, menu);
        return true;
    }

}