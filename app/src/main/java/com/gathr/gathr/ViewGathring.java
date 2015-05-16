/**************************************************************************************************
 Title : ViewGathring.java
 Author : Gathr Team
 Purpose : Activity which represents a Gathring and all information associated with it. This
           infomration is loaded from the databse based on the Gathring id. Button functionality
           to share the gathring, join/leave, and enter the chatroom are found here as well.
 *************************************************************************************************/

package com.gathr.gathr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import android.content.Intent;

import com.devsmart.android.ui.HorizontalListView;
import com.facebook.widget.ProfilePictureView;
import com.gathr.gathr.chat.ui.activities.SplashActivity;
import com.gathr.gathr.classes.AuthUser;
import com.gathr.gathr.classes.Event;
import com.gathr.gathr.classes.MyGlobals;
import com.gathr.gathr.classes.SidebarGenerator;
import com.gathr.gathr.database.DatabaseCallback;
import com.gathr.gathr.database.QueryDB;

public class ViewGathring extends ActionBarActivityPlus {

private Context c = this;
    private String[] attendees, attendeeIds;
    private boolean partOf = false, loggedin = false, cancelled = false;
    private QueryDB DBConn = new QueryDB(this);
    private String eventId = "1", event_json = "", eventOrganizer, userId = AuthUser.getUserId(this);
    private MyGlobals global = new MyGlobals(this);
    private Event event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gathring);
        setActionBar(R.string.title_activity_view_gathring);

        final HorizontalListView listview = (HorizontalListView) findViewById(R.id.listview);

        class load implements DatabaseCallback {
            public void onTaskCompleted(String results) {
                if (results.contains("ERROR")) {
                    attendees = new String[]{};
                    attendeeIds = new String[]{};

                } else {
                    try {
                        JSONArray json = new JSONArray(results);
                        int numFriends = json.length();
                        attendeeIds = new String[numFriends];
                        attendees = new String[numFriends];

                        for (int i = 0; i < json.length(); i++) {
                            attendeeIds[i] = json.getJSONObject(i).getString("Id");
                            attendees[i] = json.getJSONObject(i).getString("Facebook_Id");
                        }
                    } catch (Exception e) {
                        global.errorHandler(e);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listview.setAdapter(mAdapter);
                    }
                });
            }
        }

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

            DBConn.executeQuery("SELECT Facebook_Id, Id FROM (USERS JOIN (SELECT User_Id FROM JOINED_EVENTS WHERE Event_Id = " + eventId + " )  AS JOINED) WHERE Id = User_Id", new load());

            if(AuthUser.getUserId(this) != null){
                loggedin = true;
                new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this);
            }else{
                loggedin = false;
            }

            QueryDB getEvent = new QueryDB(this,"getEvent.php");
            class loadEvent implements DatabaseCallback{
                public void onTaskCompleted(String results){
                    try {
                        event_json = results;
                        event = new Event(results);
                        eventOrganizer = event.event_organizer;

                        if (event.status.equals("CLOSED")) {
                            cancelled = true;
                        }
                        if (cancelled){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView buttonText = (TextView) findViewById(R.id.join_leave_button);
                                    buttonText.setVisibility(View.GONE);
                                }
                            });
                        }else if(!loggedin) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView buttonText = (TextView) findViewById(R.id.join_leave_button);
                                    buttonText.setText("Login");
                                }
                            });
                        } else if (!eventOrganizer.equals(userId)) {
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
                                                    if (event.pop.equals(event.capacity))
                                                        ((Button)findViewById(R.id.join_leave_button)).setVisibility(View.INVISIBLE);
                                                    partOf = false;
                                                }
                                            });

                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    TextView buttonText = (TextView) findViewById(R.id.join_leave_button);
                                                    buttonText.setText("Leave");
                                                    ((Button)findViewById(R.id.joinChat)).setVisibility(View.VISIBLE);
                                                    partOf = true;
                                                }
                                            });
                                        }
                                    }catch(Exception e){
                                        global.errorHandler(e);
                                    }
                                }
                            }
                            DBConn.executeQuery("SELECT COUNT(User_Id) AS Count FROM JOINED_EVENTS WHERE User_Id = " + userId + " AND Event_Id = " + eventId + ";", new getCount());
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView buttonText = (TextView) findViewById(R.id.join_leave_button);
                                    buttonText.setVisibility(View.GONE);
                                    ((Button)findViewById(R.id.joinChat)).setVisibility(View.VISIBLE);

                                    //menuText.setVisible(false);
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) findViewById(R.id.gathring_name_text)).setText(event.name);
                                ((TextView) findViewById(R.id.gathring_description_text)).setText(event.description);
                                ((TextView) findViewById(R.id.gathring_address_text)).setText(event.address);
                                ((TextView) findViewById(R.id.gathring_category_text)).setText(event.categories);
                                ((TextView) findViewById(R.id.gathring_limit_text)).setText(event.pop + "/" + event.capacity);
                                if(cancelled){
                                    ((TextView) findViewById(R.id.gathring_date_text)).setText("Cancelled");
                                    ((TextView) findViewById(R.id.gathring_time_text)).setText("");
                                }else {
                                    ((TextView) findViewById(R.id.gathring_time_text)).setText(global.normalTime(event.time));
                                    ((TextView) findViewById(R.id.gathring_date_text)).setText(global.nDate(event.date));
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


    public void joinOrLeave(View view){
        try {
            TextView buttonText = (TextView) findViewById(R.id.join_leave_button);
            if(!loggedin){
                Intent intent = new Intent(this, MainActivity.class); // Send them back to login page
                startActivity(intent);
            }else {
                String[] pAndC = ((TextView) findViewById(R.id.gathring_limit_text)).getText().toString().split("/");

                if (!partOf) {
                    DBConn.executeQuery("INSERT INTO JOINED_EVENTS(User_Id, Event_Id) VALUES (" + userId + "," + eventId + ");");
                    global.tip("Welcome to the Gathring");
                    partOf = true;
                    buttonText.setText("Leave");
                    ((TextView) findViewById(R.id.gathring_limit_text)).setText(Integer.parseInt(pAndC[0]) + 1 + "/" + pAndC[1]);
                } else {
                    DBConn.executeQuery("DELETE FROM JOINED_EVENTS WHERE User_Id = " + userId + " and Event_Id = " + eventId + ";");
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
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("event_json", event_json);

        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_gathring, menu);
        return true;
    }

    public void share(View v){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this event on Gathr: http://www.wegathr.tk/viewEvent/"+ eventId +" !" );
        startActivity(Intent.createChooser(shareIntent, "Share this event"));
    }

    public void openChat(View v){
        Intent i = new Intent(this, SplashActivity.class);
        i.putExtra("EventId", eventId );
        i.putExtra("EventName", event.name );
        startActivity(i);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(!cancelled && userId.equals(eventOrganizer)){
            menu.findItem(R.id.action_edit).setVisible(true);
            menu.findItem(R.id.action_cancel).setVisible(true);
            menu.findItem(R.id.action_transfer).setVisible(true);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private BaseAdapter mAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return attendees.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, null);
            ProfilePictureView imgView = (ProfilePictureView) retval.findViewById(R.id.attendee_profile_pic);
            //new MyGlobals(context).tip(images[position]);
            imgView.setCropped(true);
            imgView.setProfileId(attendees[position]);

            imgView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String friendID = attendeeIds[position];
                    Intent i = new Intent(c, Profile.class);
                        i.putExtra("userId", friendID);
                        startActivity(i);

                }
            });

            return retval;
        }
    };
}