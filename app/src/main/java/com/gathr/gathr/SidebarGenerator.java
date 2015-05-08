package com.gathr.gathr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import android.widget.ListView;

public class SidebarGenerator {
    Class<?>[] classesArray;
    Context c;

    public SidebarGenerator(final DrawerLayout dLayout, final ListView dList, int resource, Context _c, final String[] menu, Class<?>[] cls) { //
        c = _c; //this
        classesArray = cls;
        //menu = _menu;

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
                changeIntent(view, position);
            }
        });
    }
    public void changeIntent(View view, int pos){
        Intent i = new Intent(c , classesArray[pos]);
        c.startActivity(i);
    }
}