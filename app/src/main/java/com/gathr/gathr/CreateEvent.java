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

public class CreateEvent extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        String[] titles = new String[]{"Map","My Profile","Gathrings","Friends","Settings","Notifications","Log Out"};
        Class<?>[] links = { MapsActivity.class, Profile.class, CreateEvent.class, CreateEvent.class, CreateEvent.class, CreateEvent.class, MainActivity.class};
        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, titles, links );
    }

    public void viewGathring(View view){
        MyGlobals global = new MyGlobals(this);

        QueryDB DBconn = new QueryDB(AuthUser.fb_id, AuthUser.user_id);

        String name = DBconn.escapeString(getElementText(R.id.gathring_name));
        String desc = DBconn.escapeString(getElementText(R.id.gathring_description));
        String address = DBconn.escapeString(getElementText(R.id.gathring_address));
        String city = DBconn.escapeString(getElementText(R.id.gathring_city));
        String state = DBconn.escapeString(getElementText(R.id.gathring_state));
        String time = DBconn.escapeString(global.mTime(((TextView) findViewById(R.id.gathring_time)).getText().toString()));
        String capacity = DBconn.escapeString(getElementText(R.id.gathring_limit));

        DBconn.executeQuery("INSERT INTO EVENTS " +
                "(`Name`, `Desc`, `Address`, `City`, `State`, `Time`, `Date`, `Capacity`, `Population`, `Status`, `Organizer`, `Latitude`, `Longitude`)" +
                " VALUES " +
                "('"+name+"', '"+desc+"', '"+address+"', '"+city+"','"+state+"', '"+time+"', DATE(NOW()),'"+capacity+"', '1', 'OPEN', '1', '40.768947', '-73.958845');");


        String results = DBconn.getResults();

        Intent i = new Intent(this, ViewGathring.class);
        i.putExtra("eventId", results);
        startActivity(i);
    }



    public void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = new TimePickerFragment((TextView)findViewById(R.id.gathring_time));
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public String getElementText(int viewId){
        return ((EditText)findViewById(viewId)).getText().toString();
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
}

