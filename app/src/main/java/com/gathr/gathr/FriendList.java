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

public class FriendList extends ListActivity {

    QueryDB DBConn = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
    MyGlobals global = new MyGlobals(this);

    static String[] friendNames;
    static String[]    images;
    static String[] friendIds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String results = "";
        try{
            DBConn.executeQuery("SELECT Facebook_Id, First_Name, Last_Name, Id FROM (USERS JOIN (SELECT  Friend_User_Id FROM FRIENDS WHERE User_Id = "+AuthUser.user_id+" )  AS JOINED) WHERE Id = Friend_User_Id");
            results = DBConn.getResults();
            JSONArray json = new JSONArray(results);
            int numFriends = json.length();
            friendNames = new String[numFriends];
            images = new String[numFriends];
            friendIds = new String[numFriends];

            for (int i = 0; i < json.length(); i++) {
                friendIds[i] = json.getJSONObject(i).getString("Id");
                friendNames[i] = json.getJSONObject(i).getString("First_Name") + " " + json.getJSONObject(i).getString("Last_Name");
                images[i] =    json.getJSONObject(i).getString("Facebook_Id");
            }
        }catch(Exception e){
            if(e.getMessage() == "NO RESULTS") {
                friendNames = new String[]{"No friends to show"};
                friendIds = new String[]{"-1"};
            }else{
                global.errorHandler(e);
            }
        }

        setListAdapter(new GathringArrayAdapter(this, friendNames, images));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String friendID = friendIds[position];
        if(friendID != "-1") {
            Intent i = new Intent(this, Profile.class);
            i.putExtra("userId", friendID);
            startActivity(i);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_list, menu);
        return true;
    }

}

