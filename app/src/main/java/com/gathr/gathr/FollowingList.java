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
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.gathr.gathr.adapters.FriendArrayAdapter;
import com.gathr.gathr.classes.MyGlobals;
import com.gathr.gathr.classes.SidebarGenerator;
import org.json.JSONArray;

public class FollowingList extends ActionBarActivityPlus {

    @Override
    //Sets up the layout, action bar, and sidebar when entering the Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_list);
        setActionBar("Following List");
        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }
    //Fragment that represents the followinglist
    public static class PlaceholderFragment extends ListFragment {
        //Arrays to store necessary information
        static String[] friendNames;
        static String[]    images;
        static String[] friendIds;
        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final MyGlobals global = new MyGlobals(getActivity());
            try{
                String results =  global.getFollowersJSON();
                if(results.contains("ERROR")){ //If the user has no friends
                    friendNames = new String[]{"You are not following anyone"};
                    friendIds = new String[]{"-1"};
                    images = new String[]{"-1"};
                }else{ // Populate the list with the user's friends
                    JSONArray json = new JSONArray(results);
                    int numFriends = json.length();
                    friendNames = new String[numFriends];
                    images = new String[numFriends];
                    friendIds = new String[numFriends];
                    for (int i = 0; i < numFriends; i++) {
                        friendIds[i] = json.getJSONObject(i).getString("Id");
                        friendNames[i] = json.getJSONObject(i).getString("First_Name") + " " + json.getJSONObject(i).getString("Last_Name");
                        images[i] = json.getJSONObject(i).getString("Facebook_Id");
                    }
                }
                setListAdapter(new FriendArrayAdapter(getActivity(), friendNames, images));//Set the proper adapter to populate the corresponding layout
            }catch(Exception e){
                global.errorHandler(e);
            }
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) { // What happens when someone the list item is clicked on?
            String friendID = friendIds[position]; //get the proper id
            if(!friendID.equals( "-1")) {//This is someone you are actually following
                Intent i = new Intent(getActivity(), Profile.class); // go to that user's profile
                i.putExtra("userId", friendID);
                startActivity(i);
            }

        }

    }
}