package com.gathr.gathr;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ListActivity;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;

public class GathringsList extends ListActivity {

    static final String[] eventNames =
            new String[] { "eventName1", "eventName2", "eventName3", "eventName4", "eventName5", "eventName6", "eventName7", "eventName8", "eventName9", "eventName10"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new GathringArrayAdapter(this, eventNames));

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        //get selected items
        String selectedValue = (String) getListAdapter().getItem(position);
        Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();

    }

}