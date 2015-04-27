package com.gathr.gathr;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;

public class CreateEvent extends ActionBarActivity {
    MyGlobals global = new MyGlobals(this);
    QueryDB DBconn = new QueryDB(AuthUser.fb_id, AuthUser.user_id);
    String date = "CURDATE()";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //Generate Sidebar
        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, global.titles, global.links );
    }

    public void viewGathring(View view){
        if(getElementText(R.id.gathring_name).length() < 5){
            Toast.makeText(this, "Your Gathring Name must have at least 5 characters!", Toast.LENGTH_LONG).show();
            return;
        }
        String name = DBconn.escapeString(getElementText(R.id.gathring_name));
        if(getElementText(R.id.gathring_description).length() < 10){
            Toast.makeText(this, "Your Gathring Description must have at least 10 characters!", Toast.LENGTH_LONG).show();
            return;
        }
        String desc = DBconn.escapeString(getElementText(R.id.gathring_description));

        //Get Address and Validate it (Get Lat/Lon)
        String address = DBconn.escapeString(getElementText(R.id.gathring_address));
        String city = DBconn.escapeString(getElementText(R.id.gathring_city));
        String state = DBconn.escapeString(getElementText(R.id.gathring_state));
        GCoder getLatLong = new GCoder(this);
        LatLng x = getLatLong.addressToCoor(getElementText(R.id.gathring_address) + " " + getElementText(R.id.gathring_city) + "," + getElementText(R.id.gathring_state));
        if(x.latitude == 0 && x.longitude == 0){
            Toast.makeText(this, "You must provide a valid address!", Toast.LENGTH_LONG).show();
            return;
        }

        String time = DBconn.escapeString(global.mTime(((TextView) findViewById(R.id.gathring_time)).getText().toString()));

        if(Integer.parseInt(getElementText(R.id.gathring_limit)) < 2){
            Toast.makeText(this, "You must provide a Gathring Capacity greater than 3!", Toast.LENGTH_LONG).show();
            return;
        }
        String capacity = DBconn.escapeString(getElementText(R.id.gathring_limit));

        //Run the Query to add the event
        DBconn.executeQuery("INSERT INTO EVENTS " +
                "(`Name`, `Desc`, `Address`, `City`, `State`, `Time`, `Date`, `Capacity`, `Population`, `Status`, `Organizer`, `Latitude`, `Longitude`)" +
                " VALUES " +
                "('"+name+"', '"+desc+"', '"+address+"', '"+city+"','"+state+"', '"+time+"', "+date+",'"+capacity+"', '1', 'OPEN', "+ AuthUser.user_id +", '"+x.latitude+"', '"+ x.longitude+"');");
        String results = DBconn.getResults();

        //Go to the event page that we just created
        Intent i = new Intent(this, ViewGathring.class);
        i.putExtra("eventId", results);
        startActivity(i);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onToggleClicked(View view) {
        if (((ToggleButton) view).isChecked()) {
            date = "CURDATE()";
        } else {
            date = "CURDATE() + INTERVAL 1 DAY";
        }
    }

}