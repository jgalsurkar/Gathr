/**************************************************************************************************
 Title : CreateEvent.java
 Author : Gathr Team
 Purpose : Activity used to create a Gathring with all necessary information such as name, time,
           description, etc. Validation is done as well
           as well
 *************************************************************************************************/

package com.gathr.gathr;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.gathr.gathr.classes.AuthUser;
import com.gathr.gathr.classes.Event;
import com.gathr.gathr.classes.GCoder;
import com.gathr.gathr.classes.MyGlobals;
import com.gathr.gathr.classes.SidebarGenerator;
import com.gathr.gathr.database.DatabaseCallback;
import com.gathr.gathr.database.QueryDB;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

public class CreateEvent extends ActionBarActivityPlus {
    MyGlobals global = new MyGlobals(this);
    QueryDB DBconn = new QueryDB(this);
    String date = "CURDATE() + INTERVAL 1 DAY"; //Default date if they do not select anything
    public String category, categoryId;
    EditText my_interests;
    Boolean update = false;
    String eventId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        setActionBar(R.string.title_activity_create_event);
        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this);
       // ((ImageButton)(findViewById(R.id.select_category))).setBackgroundResource(R.drawable.create_contact);
        my_interests = (EditText) findViewById(R.id.gathring_category);
        Intent i = getIntent();
        String prefillJson = i.getStringExtra("prefill");
        if(prefillJson != null){
            try {
                update = true;
                Event e = new Event(prefillJson);
                eventId = e.id;
                ((TextView) findViewById(R.id.gathring_name)).setText(e.name);
                ((TextView) findViewById(R.id.gathring_description)).setText(e.description);
                ((TextView) findViewById(R.id.gathring_address)).setText(e.address);
                ((TextView) findViewById(R.id.gathring_city)).setText(e.city);
                ((TextView) findViewById(R.id.gathring_state)).setText(e.state);
                ((TextView) findViewById(R.id.gathring_limit)).setText(e.capacity);
                ((TextView) findViewById(R.id.gathring_time)).setText(global.normalTime(e.time));
                ((TextView) findViewById(R.id.gathring_category)).setText(e.categories);
            }catch(Exception e){
                global.errorHandler(e);
            }
        }
    }

    public void viewGathring(View view){
        try {
            //Error Checking
            if (getElementText(R.id.gathring_name).length() < 5) {
                global.tip("Your Gathring Name must have at least 5 characters!");
                return;
            }
            if (getElementText(R.id.gathring_description).length() < 10) {
                global.tip("Your Gathring Description must have at least 10 characters!");
                return;
            }
            //Get Address and Validate it (Get Lat/Lon)
            GCoder getLatLong = new GCoder(this);
            LatLng x = getLatLong.addressToCoor(getElementText(R.id.gathring_address) + " " + getElementText(R.id.gathring_city) + "," + getElementText(R.id.gathring_state));
            if (x.latitude == 0 && x.longitude == 0) {
                global.tip("You must provide a valid address!");
                return;
            }
            if (Integer.parseInt(getElementText(R.id.gathring_limit)) < 2) {
                global.tip("You must provide a Gathring Capacity greater than 3!");
                return;
            }

            //Escape everything and create the event
            String name = DBconn.escapeString(getElementText(R.id.gathring_name));
            String desc = DBconn.escapeString(getElementText(R.id.gathring_description));
            String address = DBconn.escapeString(getElementText(R.id.gathring_address));
            String city = DBconn.escapeString(getElementText(R.id.gathring_city));
            String state = DBconn.escapeString(getElementText(R.id.gathring_state));
            String time = DBconn.escapeString(global.mTime(((TextView) findViewById(R.id.gathring_time)).getText().toString()));
            String capacity = DBconn.escapeString(getElementText(R.id.gathring_limit));

            //Run the Query to add the event

            final Intent i = new Intent(this, ViewGathring.class);
            class createEvent implements DatabaseCallback {
                public void onTaskCompleted(String r){
                    i.putExtra("eventId", r);
                    startActivity(i);
                    finish();
                }
            }

            if(!update) {
                QueryDB DBconn2 = new QueryDB(this, "createEvent.php?fid=" + AuthUser.getFBId(this) + "&uid=" + AuthUser.getUserId(this));
                DBconn2.executeQuery("('" + name + "', '" + desc + "', '" + address + "', '" + city + "','" + state + "', '" + time + "', " + date + ",'" + capacity + "', '1', 'OPEN', " + AuthUser.getUserId(this) + ", '" + x.latitude + "', '" + x.longitude + "'); CATEGORIES " + categoryId, new createEvent());
            }else{
                QueryDB DBconn2 = new QueryDB(this, "createEvent.php?fid=" + AuthUser.getFBId(this) + "&uid=" + AuthUser.getUserId(this) + "&eid=" + eventId);
                DBconn2.executeQuery("`Name` = '" + name + "', `Desc` = '" + desc + "', `Address` = '" + address + "', `City` = '"+city+"', `State` = '"+state+"', `Time` = '"+ time +"', `Date` = " + date + ", `Capacity`  = '"+capacity+"', `Latitude` = '"+ x.latitude +"', `Longitude` = '"+ x.longitude + "' CATEGORIES " + categoryId, new createEvent());
            }

        }catch(Exception e){
            global.errorHandler(e);
        }
    }
    public void openCategory(View view){
        Intent i = new Intent(this, ListViewMultipleSelectionActivity.class);
        i.putExtra("from",CreateEvent.class);
        i.putExtra("categoryId",categoryId);
        startActivityForResult(i,0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                category = data.getStringExtra("category");
                categoryId = data.getStringExtra("categoryId");
                my_interests.setText(category);
            }
        }
    }

    public void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = new TimePickerFragment((TextView)findViewById(R.id.gathring_time));
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public String getElementText(int viewId){
        return ((EditText)findViewById(viewId)).getText().toString().trim();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
        return true;
    }

    public void onToggleClicked(View view) {
        if (((ToggleButton) view).isChecked()) {
            date = "CURDATE()";
        } else {
            date = "CURDATE() + INTERVAL 1 DAY";
        }
    }

}