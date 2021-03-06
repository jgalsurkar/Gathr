/**************************************************************************************************
 Title : SidebarGenerator.java
 Author : Gathr Team
 Purpose : Class to represent the app's sidebar, which is on each of the main activities, as well as
           starting the intent to go to the proper activities when the correct sidebar option is
           clicked
 *************************************************************************************************/

package com.gathr.gathr.classes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import android.widget.ListView;

import com.gathr.gathr.CreateEvent;
import com.gathr.gathr.FollowingList;
import com.gathr.gathr.GathringsListActivity;
import com.gathr.gathr.MapsActivity;
import com.gathr.gathr.Profile;
import com.gathr.gathr.Settings;

public class SidebarGenerator {
    private String[] menu = new String[]{"Map","Create Gathring", "My Profile","My Gathrings","Following","Settings"};
    private Class<?>[] classesArray = { MapsActivity.class, CreateEvent.class, Profile.class, GathringsListActivity.class, FollowingList.class, Settings.class};

    Context c;

    public SidebarGenerator(final DrawerLayout dLayout, final ListView dList, int resource, Context _c) { //
        c = _c;

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(_c, resource, menu);
        dList.setAdapter(adapter);
        dList.setSelector(android.R.color.darker_gray);
        dList.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                dLayout.closeDrawers();
                Bundle args = new Bundle();
                args.putString("Menu", menu[position]);
                View view = dLayout;
                changeIntent(dLayout, position);
            }
        });
    }
    public void changeIntent(View view, int pos){
        Intent i = new Intent(c , classesArray[pos]);
        c.startActivity(i);
    }
}