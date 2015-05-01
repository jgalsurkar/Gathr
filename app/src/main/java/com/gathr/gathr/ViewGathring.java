package com.gathr.gathr;

import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONArray;
import android.content.Intent;
import android.widget.Toast;

import com.facebook.android.Facebook;

public class ViewGathring extends ActionBarActivity {

    private boolean partOf = false;
    private QueryDB DBConn = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
    private String eventId= "1";
    MyGlobals global = new MyGlobals(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gathring);

        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, global.titles, global.links );
/*
        Facebook fb = new Facebook(R.string.facebook_app_id);
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                .build();
        ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
        shareButton.setShareContent(content);
*/
        try{
            String event_organizer;
            Bundle extras = getIntent().getExtras();
            if(extras != null)
                eventId = (String)extras.get("eventId");

            String result;

            DBConn.executeQuery("SELECT * FROM EVENTS WHERE Id =" + eventId);
            result = DBConn.getResults();

            JSONArray json;
            json = new JSONArray(result);
            String eventName = json.getJSONObject(0).getString("Name");
            String description = json.getJSONObject(0).getString("Desc");
            String address = json.getJSONObject(0).getString("Address");
            String city = json.getJSONObject(0).getString("City");
            String state = json.getJSONObject(0).getString("State");
            String time = json.getJSONObject(0).getString("Time");
            String capacity = json.getJSONObject(0).getString("Capacity");
            event_organizer = json.getJSONObject(0).getString("Organizer").trim();

            ((TextView)findViewById(R.id.gathring_name_text)).setText(eventName);
            ((TextView)findViewById(R.id.gathring_description_text)).setText(description);
            ((TextView)findViewById(R.id.gathring_address_text)).setText(address);
            ((TextView)findViewById(R.id.gathring_city_text)).setText(city);
            ((TextView)findViewById(R.id.gathring_state_text)).setText(state);
            ((TextView)findViewById(R.id.gathring_limit_text)).setText(capacity);
            ((TextView)findViewById(R.id.gathring_time_text)).setText(global.normalTime(time));

            TextView buttonText = (TextView) findViewById(R.id.join_leave_button);

            if(!event_organizer.equals(AuthUser.user_id)) {
                DBConn.executeQuery("SELECT COUNT(User_Id) AS Count FROM JOINED_EVENTS WHERE User_Id = " + AuthUser.user_id + " AND Event_Id = " + eventId + ";");
                result = DBConn.getResults();
                json = new JSONArray(result);
                String count = json.getJSONObject(json.length() - 1).getString("Count").trim();

                if (count.equals("0")) {
                    buttonText.setText("Join");
                    partOf = false;
                } else {
                    buttonText.setText("Leave");
                    partOf = true;
                }
            }else{
                buttonText.setVisibility(View.GONE);
            }
        }catch(Exception e){
            global.errorHandler(e);
        }
    }

    public void joinOrLeave(View view){
        try {
            TextView buttonText = (TextView) findViewById(R.id.join_leave_button);
            if(!partOf) {
                DBConn.executeQuery("INSERT INTO JOINED_EVENTS(User_Id, Event_Id) VALUES (" + AuthUser.user_id + "," + eventId + ");");
                //DBConn.getResults(); // We do not need to wait
                global.tip("Welcome to the Gathring");
                partOf = true;
                buttonText.setText("Leave");
            }else{
                DBConn.executeQuery("DELETE FROM JOINED_EVENTS WHERE User_Id=" + AuthUser.user_id + " and Event_Id= " + eventId + ";");
                //DBConn.getResults(); // We do not need to wait
                global.tip("You have left the Gathring");
                partOf = false;
                buttonText.setText("Join");
            }
        }catch(GathrException e){
            global.errorHandler (e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_gathring, menu);
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