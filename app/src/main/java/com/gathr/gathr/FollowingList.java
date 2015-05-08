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


public class FollowingList extends ActionBarActivity {
    static String eid = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_list);
        setActionBar();
        MyGlobals global = new MyGlobals();
        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, global.titles, global.links );

        Intent i = getIntent();
        eid = i.getStringExtra("EventId");
        if(eid != null)
            getSupportActionBar().setTitle("Attendee List");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_following_list, menu);
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
        title.setText(R.string.title_activity_following_list);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }
    public void openSideBar(View view)
    {
        DrawerLayout sidebar = (DrawerLayout) findViewById(R.id.drawer_layout);
        sidebar.openDrawer(Gravity.LEFT);
    }
    public static class PlaceholderFragment extends ListFragment {
        static String[] friendNames;
        static String[]    images;
        static String[] friendIds;
        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            QueryDB DBConn = new QueryDB(getActivity(), AuthUser.fb_id, AuthUser.user_id);
            final MyGlobals global = new MyGlobals(getActivity());

            try{
                class load implements DatabaseCallback{
                    public void onTaskCompleted(String results){
                        if(results.contains("ERROR")){
                            friendNames = new String[]{"No friends to show"};
                            friendIds = new String[]{"-1"};
                            images = new String[]{"-1"};

                        }else{
                            try {
                                JSONArray json = new JSONArray(results);
                                int numFriends = json.length();
                                friendNames = new String[numFriends];
                                images = new String[numFriends];
                                friendIds = new String[numFriends];

                                for (int i = 0; i < json.length(); i++) {
                                    friendIds[i] = json.getJSONObject(i).getString("Id");
                                    friendNames[i] = json.getJSONObject(i).getString("First_Name") + " " + json.getJSONObject(i).getString("Last_Name");
                                    images[i] = json.getJSONObject(i).getString("Facebook_Id");
                                }
                            }catch(Exception e){
                                global.errorHandler(e);
                            }
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override public void run() {
                                setListAdapter(new FriendArrayAdapter(getActivity(), friendNames, images));
                            }
                        });
                    }
                }


                if(eid == null) {
                    DBConn.executeQuery("SELECT Facebook_Id, First_Name, Last_Name, Id FROM (USERS JOIN (SELECT  Friend_User_Id FROM FRIENDS WHERE User_Id = " + AuthUser.user_id + " )  AS JOINED) WHERE Id = Friend_User_Id", new load());
                }else{
                    DBConn.executeQuery("SELECT Facebook_Id, First_Name, Last_Name, Id FROM (USERS JOIN (SELECT User_Id FROM JOINED_EVENTS WHERE Event_Id = " + eid + " )  AS JOINED) WHERE Id = User_Id", new load());
                }
            }catch(Exception e){
                global.errorHandler(e);
            }
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            String friendID = friendIds[position];
            if(!friendID.equals( "-1")) {
                Intent i = new Intent(getActivity(), Profile.class);
                i.putExtra("userId", friendID);
                startActivity(i);
            }

        }

    }
}