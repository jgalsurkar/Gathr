/**************************************************************************************************
 Title : FollowingList.java
 Author : Gathr Team
 Purpose : Activity which represents a list view of who the user is following. Clicking on any
           of list items, brings the user to the appropriate profile page
 *************************************************************************************************/

package com.gathr.gathr;

import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.gathr.gathr.adapters.FriendArrayAdapter;
import com.gathr.gathr.classes.AuthUser;
import com.gathr.gathr.classes.MyGlobals;
import com.gathr.gathr.classes.SidebarGenerator;
import com.gathr.gathr.database.DatabaseCallback;
import com.gathr.gathr.database.QueryDB;

import org.json.JSONArray;


public class FollowingList extends ActionBarActivityPlus {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_list);
        setActionBar(R.string.title_activity_following_list);
        MyGlobals global = new MyGlobals();
        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this);

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

    public static class PlaceholderFragment extends ListFragment {
        static String[] friendNames;
        static String[]    images;
        static String[] friendIds;
        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            QueryDB DBConn = new QueryDB(getActivity());
            final MyGlobals global = new MyGlobals(getActivity());

            try{
                class load implements DatabaseCallback {
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
                DBConn.executeQuery("SELECT Facebook_Id, First_Name, Last_Name, Id FROM (USERS JOIN (SELECT  Friend_User_Id FROM FRIENDS WHERE User_Id = " + AuthUser.getUserId(getActivity()) + " )  AS JOINED) WHERE Id = Friend_User_Id", new load());

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