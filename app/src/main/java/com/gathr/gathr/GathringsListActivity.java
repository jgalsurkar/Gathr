package com.gathr.gathr;

import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ListView;

import org.json.JSONArray;


public class GathringsListActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gathrings_list_activity);
        MyGlobals global = new MyGlobals();
        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, global.titles, global.links );

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gathrings_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends ListFragment {

        String[] eventNames;
        String[] eventDescriptions;
        String[] eventIds;

        public PlaceholderFragment() { }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //super(this, R.layout.fragment_gathr_list_view, values);

            // View rootView = inflater.inflate(R.layout.fragment_gathr_list_view, container, false);
            QueryDB DBConn = new QueryDB(getActivity(), AuthUser.fb_id, AuthUser.user_id);
            MyGlobals global = new MyGlobals(getActivity());

            try {

                String results;
                DBConn.executeQuery("SELECT DISTINCT Id, `Name`, `Desc`  FROM (EVENTS JOIN (SELECT Event_Id FROM JOINED_EVENTS WHERE JOINED_EVENTS.User_Id = " + AuthUser.user_id + ") AS JOINED) WHERE ((EVENTS.Id = JOINED.Event_Id) AND (EVENTS.Date > DATE(NOW()) OR (EVENTS.Date = DATE(NOW()) AND EVENTS.TIME >= time(NOW()))))");
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

            } catch (Exception e) {
                if (e.getMessage().equals("NO RESULTS")) {
                    eventNames = new String[]{"Sorry"};
                    eventDescriptions = new String[]{"You are not part of any events"};
                    eventIds = new String[]{"-1"};
                } else {
                    global.errorHandler(e);
                }
            }

            setListAdapter(new GathringArrayAdapter(getActivity(), eventNames, eventDescriptions));

            // return rootView;
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            String eventID = eventIds[position];
            if (!eventID.equals("-1")) {
                Intent i = new Intent(getActivity(), ViewGathring.class);
                i.putExtra("eventId", eventID);
                startActivity(i);
            }
        }
    }
}
