package com.gathr.gathr;

import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.model.people.Person;

import java.util.jar.Attributes;

public class CreateEvent extends ActionBarActivity {
    MyGlobals global = new MyGlobals(this);
    QueryDB DBconn = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
    String date = "CURDATE()"; //Default date if they do not select anything
    public String userId, category, categoryId;
    EditText my_interests;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, global.titles, global.links );
        my_interests = (EditText) findViewById(R.id.gathring_category);
        Intent i = getIntent();
        userId = AuthUser.user_id;
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
            String results;

            DBconn.executeQuery("INSERT INTO EVENTS " +
                    "(`Name`, `Desc`, `Address`, `City`, `State`, `Time`, `Date`, `Capacity`, `Population`, `Status`, `Organizer`, `Latitude`, `Longitude`)" +
                    " VALUES " +
                    "('" + name + "', '" + desc + "', '" + address + "', '" + city + "','" + state + "', '" + time + "', " + date + ",'" + capacity + "', '1', 'OPEN', " + AuthUser.user_id + ", '" + x.latitude + "', '" + x.longitude + "');");
            results = DBconn.getResults();
            global.tip(results);
            QueryDB DBconn2 = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
            DBconn2.executeQuery("ADD CATEGORIES " +categoryId+" FOR "+results);
            DBconn2.getResults();
            global.tip(DBconn2.getResults());

            //Go to the event page that we just created
            Intent i = new Intent(this, ViewGathring.class);
            i.putExtra("eventId", results);
            startActivity(i);
            finish();

        }catch(Exception e){
            global.errorHandler(e);
        }
    }
    public void openCategory(View view){
        Intent i = new Intent(this, ListViewMultipleSelectionActivity.class);
        i.putExtra("from","event");
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