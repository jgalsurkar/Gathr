package com.gathr.gathr;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import android.content.Intent;

public class ViewGathring extends ActionBarActivity {

    private boolean partOf = false;
    private QueryDB DBConn = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
    private String eventId = "1";
    private MyGlobals global = new MyGlobals(this);
    private boolean loggedin = false;
    private String event_json = "";
    String eventOrganizer;
    private boolean cancelled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gathring);
        try{
            Intent intent = getIntent();

            //If we get to this page from inside the app (they should be passing EventId)
            Bundle extras = intent.getExtras();
            if(extras != null)
                eventId = (String)extras.get("eventId");

            //If we get to this page from http://wegathr.tk/viewEvent/{eventId}
            Uri data = intent.getData();
            if(data != null) {
                String[] url = data.toString().split("/");
                eventId = url[(url.length - 1)];
            }

            if(AuthUser.user_id != null){
                loggedin = true;
                new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, global.titles, global.links );
            }else{
                String[] title = {"Login"};
                Class<?>[] link = {MainActivity.class};
                new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, title, link );
                loggedin = false;
            }

            QueryDB getEvent = new QueryDB(this,"getEvent.php",true);
            class loadEvent implements DatabaseCallback{
                public void onTaskCompleted(String results){
                    try {
                        event_json = results;
                        JSONArray json;
                        json = new JSONArray(results);
                        final String eventName = json.getJSONObject(0).getString("Name");
                        final String description = json.getJSONObject(0).getString("Desc");
                        final String address = json.getJSONObject(0).getString("Address");
                        final String city = json.getJSONObject(0).getString("City");
                        final String state = json.getJSONObject(0).getString("State");
                        final String time = json.getJSONObject(0).getString("Time");
                        final String date = json.getJSONObject(0).getString("Date");
                        final String capacity = json.getJSONObject(0).getString("Capacity");
                        final String population = json.getJSONObject(0).getString("Population");
                        final String status = json.getJSONObject(0).getString("Status");
                        if (status.equals("CLOSED")) {
                            cancelled = true;
                        }
                        final String event_organizer = json.getJSONObject(0).getString("Organizer").trim();
                        eventOrganizer = event_organizer;
                        if (cancelled){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView buttonText = (TextView) findViewById(R.id.join_leave_button);
                                    buttonText.setVisibility(View.GONE);
                                    TextView va = (TextView) findViewById(R.id.va_button);
                                    va.setVisibility(View.GONE);
                                }
                            });
                        }else if(!loggedin) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView buttonText = (TextView) findViewById(R.id.join_leave_button);
                                    buttonText.setText("Login");
                                    TextView va = (TextView) findViewById(R.id.va_button);
                                    va.setVisibility(View.GONE);
                                }
                            });
                        } else if (!event_organizer.equals(AuthUser.user_id)) {
                            class getCount implements DatabaseCallback{
                                public void onTaskCompleted(String results) {
                                    try {
                                        JSONArray json2 = new JSONArray(results);
                                        String count = json2.getJSONObject(0).getString("Count").trim();

                                        if (count.equals("0")) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    TextView buttonText = (TextView) findViewById(R.id.join_leave_button);
                                                    buttonText.setText("Join");
                                                    partOf = false;
                                                }
                                            });

                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    TextView buttonText = (TextView) findViewById(R.id.join_leave_button);
                                                    buttonText.setText("Leave");
                                                    partOf = true;
                                                }
                                            });
                                        }
                                    }catch(Exception e){
                                        global.errorHandler(e);
                                    }
                                }
                            }
                            DBConn.executeQuery("SELECT COUNT(User_Id) AS Count FROM JOINED_EVENTS WHERE User_Id = " + AuthUser.user_id + " AND Event_Id = " + eventId + ";", new getCount());
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView buttonText = (TextView) findViewById(R.id.join_leave_button);
                                    buttonText.setVisibility(View.GONE);

                                    //menuText.setVisible(false);
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) findViewById(R.id.gathring_name_text)).setText(eventName);
                                ((TextView) findViewById(R.id.gathring_description_text)).setText(description);
                                ((TextView) findViewById(R.id.gathring_address_text)).setText(address);
                                ((TextView) findViewById(R.id.gathring_city_text)).setText(city);
                                ((TextView) findViewById(R.id.gathring_state_text)).setText(state);
                                ((TextView) findViewById(R.id.gathring_limit_text)).setText(population + "/" + capacity);
                                if(cancelled){
                                    ((TextView) findViewById(R.id.gathring_date_text)).setText("Cancelled");
                                    ((TextView) findViewById(R.id.gathring_time_text)).setText("");
                                }else {
                                    ((TextView) findViewById(R.id.gathring_time_text)).setText(global.normalTime(time));
                                    ((TextView) findViewById(R.id.gathring_date_text)).setText(global.nDate(date));
                                }
                            }
                        });
                    }catch(Exception e){
                        global.errorHandler(e);
                    }
                }
            }
            getEvent.executeQuery(eventId, new loadEvent());
        }catch(Exception e){
            global.errorHandler(e);
        }
    }

    public void setActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        //displaying custom ActionBar
        View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);
        actionBar.setCustomView(mActionBarView);
        TextView title= (TextView)mActionBarView.findViewById(R.id.title);
        title.setText(R.string.title_activity_view_gathring);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }
    public void openSideBar(View view)
    {
        DrawerLayout sidebar = (DrawerLayout) findViewById(R.id.drawer_layout);
        sidebar.openDrawer(Gravity.LEFT);
    }
    public void joinOrLeave(View view){
        try {
            TextView buttonText = (TextView) findViewById(R.id.join_leave_button);
            if(!loggedin){
                Intent intent = new Intent(this, MainActivity.class); // Send them back to login page
                startActivity(intent);
            }else {
                String[] pAndC = ((TextView) findViewById(R.id.gathring_limit_text)).getText().toString().split("/");

                if (!partOf) {
                    DBConn.executeQuery("INSERT INTO JOINED_EVENTS(User_Id, Event_Id) VALUES (" + AuthUser.user_id + "," + eventId + ");");
                    global.tip("Welcome to the Gathring");
                    partOf = true;
                    buttonText.setText("Leave");
                    ((TextView) findViewById(R.id.gathring_limit_text)).setText(Integer.parseInt(pAndC[0]) + 1 + "/" + pAndC[1]);
                } else {
                    DBConn.executeQuery("DELETE FROM JOINED_EVENTS WHERE User_Id = " + AuthUser.user_id + " and Event_Id = " + eventId + ";");
                    global.tip("You have left the Gathring");
                    partOf = false;
                    buttonText.setText("Join");
                    ((TextView) findViewById(R.id.gathring_limit_text)).setText(Integer.parseInt(pAndC[0]) - 1 + "/" + pAndC[1]);
                }
            }
        }catch(Exception e){
            global.errorHandler(e);
        }
    }

    public void showOnMap(View view){

    }

    public void viewMembers(View view){
        Intent i = new Intent(this, FollowingList.class);
        i.putExtra("EventId", eventId);
        startActivity(i);
        //We do not finish because most users will go back
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_gathring, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(!cancelled && AuthUser.user_id.equals(eventOrganizer)){
            menu.findItem(R.id.action_edit).setVisible(true);
            menu.findItem(R.id.action_cancel).setVisible(true);
            menu.findItem(R.id.action_transfer).setVisible(true);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this event on Gathr: http://www.wegathr.tk/viewEvent/"+ eventId +" !" );
            startActivity(Intent.createChooser(shareIntent, "Share this event"));

            return true;
        }

        if (id == R.id.action_edit) {
            Intent i = new Intent(this, CreateEvent.class);
            i.putExtra("prefill", event_json );
            startActivity(i);
            finish();
            return true;
        }

        if (id == R.id.action_transfer) {
            return true;
        }

        if (id == R.id.action_cancel) {
            MsgBox("Confirm","Are you sure you want to cancel this event? You can not undo this action.", this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void MsgBox(String Title, String Message, final Context c){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            DBConn.executeQuery("UPDATE EVENTS SET Status = 'CLOSED' WHERE Id = " + eventId);
                        } catch (Exception e) {
                            global.errorHandler(e);
                        }
                        global.tip("Event was Cancelled!");
                        Intent i = new Intent(c, GathringsListActivity.class);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
        ;

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}