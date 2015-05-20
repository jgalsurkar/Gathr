/**************************************************************************************************
 Title : GathringListActivity.java
 Author : Gathr Team
 Purpose : Activity which represents a list view of the user's gathrings. Clicking on any
 of list items, brings the user to the appropriate gathring activity (ViewGathring)
 *************************************************************************************************/

package com.gathr.gathr;

import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.gathr.gathr.adapters.GathringArrayAdapter;
import com.gathr.gathr.classes.AuthUser;
import com.gathr.gathr.classes.MyGlobals;
import com.gathr.gathr.classes.SidebarGenerator;
import com.gathr.gathr.database.DatabaseCallback;
import com.gathr.gathr.database.QueryDB;

import org.json.JSONArray;

public class GathringsListActivity extends ActionBarActivityPlus {

    @Override
    //Sets up the layout, action bar, and sidebar when entering the Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gathrings_list_activity);
        setActionBar("My Gathrings");
        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }
    //Fragment that represents the Gathring List
    public static class PlaceholderFragment extends ListFragment {
        //Arrays to store necessary Gathring information
        String[] eventNames;
        String[] eventDescriptions;
        String[] eventIds;

        public PlaceholderFragment() { }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            QueryDB DBConn = new QueryDB(getActivity());
            final MyGlobals global = new MyGlobals(getActivity());

            class load implements DatabaseCallback {
                public void onTaskCompleted(String results) {
                    if(results.contains("ERROR")){ //If they do not belong to any gathrings
                        eventNames = new String[]{"Sorry"};
                        eventDescriptions = new String[]{"You are not part of any events"};
                        eventIds = new String[]{"-1"};

                    }else {
                        try { //Load the events
                            JSONArray json = new JSONArray(results);
                            int numEvents = json.length();
                            eventNames = new String[numEvents];
                            eventDescriptions = new String[numEvents];
                            eventIds = new String[numEvents];
                            for (int i = 0; i < numEvents; i++) {
                                eventIds[i] = json.getJSONObject(i).getString("Id");
                                eventNames[i] = json.getJSONObject(i).getString("Name");
                                eventDescriptions[i] = json.getJSONObject(i).getString("Desc");
                            }
                        } catch (Exception e) {
                            global.errorHandler(e);
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setListAdapter(new GathringArrayAdapter(getActivity(), eventNames, eventDescriptions));//Set the proper adapter to populate the names and descriptions in the layout
                        }
                    });
                }
            }
            DBConn.executeQuery("SELECT DISTINCT Id, `Name`, `Desc`  FROM (EVENTS JOIN (SELECT Event_Id FROM JOINED_EVENTS WHERE JOINED_EVENTS.User_Id = " + AuthUser.getUserId(getActivity()) + ") AS JOINED) WHERE ((EVENTS.Id = JOINED.Event_Id) AND (EVENTS.Date > DATE(NOW()) OR (EVENTS.Date = DATE(NOW()) AND EVENTS.TIME >= time(NOW()))))", new load());

        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) { // What happens when they click on a gathring
            String eventID = eventIds[position];
            if (!eventID.equals("-1")) { //If there is an event
                Intent i = new Intent(getActivity(), ViewGathring.class); //Go to the appropriate viewGathring
                i.putExtra("eventId", eventID);
                startActivity(i);
            }
        }
    }

}