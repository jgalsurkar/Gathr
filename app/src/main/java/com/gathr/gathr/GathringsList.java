package com.gathr.gathr;

import android.content.Intent;
import android.location.Address;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ListActivity;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class GathringsList extends ListActivity {


    static String[] eventNames;// =
            //new String[] { "eventName1", "eventName2", "eventName3"};

    static String[] eventDescriptions;// =
           // new String[] { "eventDescrihgkgfjhfjhf hjjgfv hjfhvvghv hgfvgf gfgjfjhption1", "eventDescription2", "eventDescription3"};

    static String[] eventIds;// =
          //  new String[] { "1", "2", "3"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QueryDB DBConn = new QueryDB(AuthUser.fb_id, AuthUser.user_id);
        DBConn.executeQuery("SELECT DISTINCT Id, `Name`, `Desc`  FROM (EVENTS JOIN JOINED_EVENTS) WHERE (((EVENTS.Id = JOINED_EVENTS.Event_Id AND JOINED_EVENTS.User_Id = "+AuthUser.user_id+") OR EVENTS.Organizer = "+AuthUser.user_id+") AND (EVENTS.Date > DATE(NOW()) OR (EVENTS.Date = DATE(NOW()) AND EVENTS.TIME >= time(NOW()))));");


        String results = DBConn.getResults();

        if(results.contains("ERROR")){
            eventNames = new String[] {"Sorry"};
            eventDescriptions =  new String[] {"You are not part of any events"};
            eventIds = new String[] {"-1"};

        }else {

            try {
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    setListAdapter(new GathringArrayAdapter(this, eventNames, eventDescriptions));

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //get selected items
        //String selectedValue = (String) getListAdapter().getItem(position);
        //Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();

        String eventID = eventIds[position];
        if(eventID != "-1") {
            Intent i = new Intent(this, ViewGathring.class);
            i.putExtra("eventId", eventID);
            startActivity(i);
        }

    }

}