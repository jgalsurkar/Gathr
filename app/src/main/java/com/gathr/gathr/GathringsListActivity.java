package com.gathr.gathr;

import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;


public class GathringsListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gathrings_list_activity);
        setActionBar();
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
    public void setActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        //displaying custom ActionBar
        View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);
        actionBar.setCustomView(mActionBarView);
        TextView title= (TextView)mActionBarView.findViewById(R.id.title);
        title.setText(R.string.title_activity_gathrings_list);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }
    public void openSideBar(View view)
    {
        DrawerLayout sidebar = (DrawerLayout) findViewById(R.id.drawer_layout);
        sidebar.openDrawer(Gravity.LEFT);
    }

    public static class PlaceholderFragment extends ListFragment {

        String[] eventNames;
        String[] eventDescriptions;
        String[] eventIds;

        public PlaceholderFragment() { }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            QueryDB DBConn = new QueryDB(getActivity());
            final MyGlobals global = new MyGlobals(getActivity());

            try {
                class load implements DatabaseCallback {
                    public void onTaskCompleted(String results) {
                        if(results.contains("ERROR")){
                            eventNames = new String[]{"Sorry"};
                            eventDescriptions = new String[]{"You are not part of any events"};
                            eventIds = new String[]{"-1"};

                        }else {
                            try {
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
                                setListAdapter(new GathringArrayAdapter(getActivity(), eventNames, eventDescriptions));
                            }
                        });
                    }
                }
                DBConn.executeQuery("SELECT DISTINCT Id, `Name`, `Desc`  FROM (EVENTS JOIN (SELECT Event_Id FROM JOINED_EVENTS WHERE JOINED_EVENTS.User_Id = " + AuthUser.getUserId(getActivity()) + ") AS JOINED) WHERE ((EVENTS.Id = JOINED.Event_Id) AND (EVENTS.Date > DATE(NOW()) OR (EVENTS.Date = DATE(NOW()) AND EVENTS.TIME >= time(NOW()))))", new load());
            } catch (Exception e) {
                global.errorHandler(e);
            }
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