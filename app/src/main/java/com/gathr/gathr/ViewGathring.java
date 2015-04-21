package com.gathr.gathr;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import android.content.Intent;
import android.widget.Toast;

public class ViewGathring extends ActionBarActivity {

    private boolean partOf = false;
    private QueryDB x = new QueryDB(AuthUser.fb_id, AuthUser.user_id);
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gathring);


        String[] titles = new String[]{"Map","Create Gathring", "My Profile","My Gathrings","Friends","Settings"};
        Class<?>[] links = { MapsActivity.class, CreateEvent.class, Profile.class, GathringsList.class, MapsActivity.class, MapsActivity.class};
        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, titles, links );

        MyGlobals global = new MyGlobals();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        eventId = "1";
        if(extras != null)
            eventId =(String)extras.get("eventId");

        Log.i("EVENT ID IS: ", "" + eventId);

        x.executeQuery("SELECT * FROM EVENTS WHERE Id =" + eventId);

        String result = x.getResults();
        Log.i("RESULTS: ", result);

        JSONArray json;
        try {
            json = new JSONArray(result);
            String eventName = json.getJSONObject(json.length()-1).getString("Name");
            String description = json.getJSONObject(json.length()-1).getString("Desc");
            String address = json.getJSONObject(json.length()-1).getString("Address");
            String city = json.getJSONObject(json.length()-1).getString("City");
            String state = json.getJSONObject(json.length()-1).getString("State");
            String time = json.getJSONObject(json.length()-1).getString("Time");
            String capacity = json.getJSONObject(json.length()-1).getString("Capacity");

            TextView a =(TextView)findViewById(R.id.gathring_name_text);
            a.setText(eventName);
            TextView b =(TextView)findViewById(R.id.gathring_description_text);
            b.setText(description);
            TextView c =(TextView)findViewById(R.id.gathring_address_text);
            c.setText(address);
            TextView d =(TextView)findViewById(R.id.gathring_city_text);
            d.setText(city);
            TextView e =(TextView)findViewById(R.id.gathring_state_text);
            e.setText(state);
            TextView f =(TextView)findViewById(R.id.gathring_limit_text);
            f.setText(capacity);
            TextView g =(TextView)findViewById(R.id.gathring_time_text);
            g.setText(global.normalTime(time));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        x.executeQuery("SELECT COUNT(User_Id) AS Count FROM JOINED_EVENTS WHERE User_Id = "+AuthUser.user_id+" AND Event_Id = "+eventId+";");
        result = x.getResults();
        try{
            json = new JSONArray(result);
            String count = json.getJSONObject(json.length()-1).getString("Count");
            TextView buttonText = (TextView) findViewById(R.id.join_leave_button);
            if( count == "0"){
                buttonText.setText("Join");
                partOf = false;
            }
            else {
                buttonText.setText("Leave");
                partOf = true;
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void joinOrLeave(View view){
        TextView buttonText = (TextView) findViewById(R.id.join_leave_button);
        if(!partOf) {
            x.executeQuery("INSERT INTO JOINED_EVENTS(User_Id, Event_Id) VALUES ("+AuthUser.user_id+","+eventId+");");
            x.getResults();
            Toast.makeText(this, "Welcome to the Gathring", Toast.LENGTH_SHORT).show();
            partOf = true;
            buttonText.setText("Leave");
        }
        else{
            x.executeQuery("DELETE FROM JOINED_EVENTS WHERE User_Id="+AuthUser.user_id+" and Event_Id= "+eventId+";");
            x.getResults();
            Toast.makeText(this, "You have left the Gathring", Toast.LENGTH_SHORT).show();
            partOf = false;
            buttonText.setText("Join");
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
