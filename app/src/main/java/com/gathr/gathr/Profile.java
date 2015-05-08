package com.gathr.gathr;

import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class Profile extends ActionBarActivity {
    public String userId = AuthUser.user_id,
            interests = "",
            categoryId = "",
            results = "",
            inst,
            fb,
            tw;
    TextView past_events,
            followers,
            events_created,
            events_attended;
    Boolean following = false,
            blocking = false;
    MyGlobals global = new MyGlobals(this);

    class getUser implements DatabaseCallback {
        public void onTaskCompleted(final String results){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setProfileInfo(results);
                }
            });
        }
    }
    class getCategory implements DatabaseCallback {
        public void onTaskCompleted(final String results){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCategory(results);
                }
            });
        }
    }
    class getEventsAttended implements DatabaseCallback {
        public void onTaskCompleted(final String results){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setEventsAttended(results);
                }
            });
        }
    }
    class getFollowers implements DatabaseCallback {
        public void onTaskCompleted(final String results){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setFollowers(results);
                }
            });
        }
    }
    class getEventsCreated implements DatabaseCallback {
        public void onTaskCompleted(final String results){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setEventsCreated(results);
                }
            });
        }
    }
    class getPastEvent implements DatabaseCallback {
        public void onTaskCompleted(final String results){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setPastEvents(results);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setActionBar();
        QueryDB DBconn = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, global.titles, global.links );

        try {


            Intent i = getIntent();
            String temp = i.getStringExtra("userId");
            if (temp != null) {
                userId = temp;
                DBconn.executeQuery("SELECT * FROM USERS WHERE Id = " + userId + ";", new getUser());
            }else{
                setProfileInfo(global.getUserJSON());
            }
            QueryDB DBconn2 = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
            DBconn2.executeQuery("SELECT Category_Id, Name FROM  CATEGORIES JOIN (SELECT * FROM USER_INTERESTS WHERE User_Id = " + userId + ") AS JOINED WHERE JOINED.Category_Id = Id",new getCategory());
            QueryDB DBconn5 = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
            DBconn5.executeQuery("SELECT Count(Event_Id) as Count FROM JOINED_EVENTS WHERE User_Id= " + userId+";",new getEventsAttended());
            QueryDB DBconn3 = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
            DBconn3.executeQuery("SELECT Count(User_Id) as Count FROM FRIENDS WHERE Friend_User_Id= " + userId+";",new getFollowers());
            QueryDB DBconn4 = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
            DBconn4.executeQuery("SELECT Count(Id) as Count FROM EVENTS WHERE Organizer= " + userId+";", new getEventsCreated());
            QueryDB DBconn6 = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
            DBconn6.executeQuery("SELECT Name FROM EVENTS WHERE Organizer= " + userId + " and Date < DATE(NOW()) ORDER BY Date DESC LIMIT 15", new getPastEvent());
        }
        catch(Exception e) {
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
        title.setText(R.string.title_activity_profile);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }
    public void openSideBar(View view)
    {
        // DrawerLayout sidebar = (DrawerLayout) findViewById(R.id.left_drawer);
        //sidebar.openDrawer(Gravity.LEFT);
        global.tip("Open Category");
    }
    public void setFollowers(String r){
        try{
            followers = (TextView) findViewById(R.id.followers);
            JSONArray json = new JSONArray(r);
            if (!r.contains("ERROR")) {
                String count = json.getJSONObject(0).getString("Count").trim();
                followers.append(count);
            }
        }
        catch(Exception e) {
            global.errorHandler(e);
        }
    }


    public void setEventsAttended(String r)
    {
        try{
            events_attended = (TextView) findViewById(R.id.events_attended);
            if (!r.contains("ERROR")) {
                JSONArray json = new JSONArray(r);
                String count = json.getJSONObject(0).getString("Count").trim();
                events_attended.append(count);
            }
        }
        catch(Exception e) {
            global.errorHandler(e);
        }
    }
    public void setProfileInfo(String r)
    {
        try {
            TextView userNameView = (TextView) findViewById(R.id.user_name);
            ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.selection_profile_pic);
            profilePictureView.setCropped(true);
            TextView about_me = (TextView) findViewById(R.id.about_me);
            if(!r.contains("ERROR")) {
                JSONArray json = new JSONArray(r);
                JSONObject elem1 = json.getJSONObject(0);
                userNameView.setText(elem1.getString("First_Name") + " " + elem1.getString("Last_Name"));
                profilePictureView.setProfileId(elem1.getString("Facebook_Id"));
                about_me.setText(elem1.getString("About_Me"));
                inst = elem1.getString("Instagram");
                fb = elem1.getString("Facebook");
                tw = elem1.getString("Twitter");
                setImages(inst, R.id.insta, R.drawable.insta, R.drawable.inst_g);
                setImages(fb, R.id.face, R.drawable.facebook, R.drawable.fb_g);
                setImages(tw, R.id.twit, R.drawable.twit, R.drawable.tw_g);
            }
        }catch(Exception e){
            global.errorHandler(e);
        }
    }
    public void setCategory(String r){
        try{
            TextView my_interests = (TextView) findViewById(R.id.my_interests);
            if(!r.contains("ERROR")) {
                if (!r.equals("")) {
                    JSONArray json = new JSONArray(r);
                    for (int j = 0; j < json.length(); j++) {
                        interests = interests + json.getJSONObject(j).getString("Name") + ", ";
                        categoryId = categoryId + json.getJSONObject(j).getString("Category_Id") + ",";
                    }
                    interests = interests.substring(0, interests.length() - 2);
                    my_interests.setText(interests);
                } else my_interests.setText("");
            }
        }
        catch(Exception e) {
            global.errorHandler(e);
        }

    }
    public void setEventsCreated(String r)
    {
        try {
            events_created = (TextView) findViewById(R.id.events_created);
            if (!r.contains("ERROR")) {
                JSONArray json = new JSONArray(r);
                String count = json.getJSONObject(0).getString("Count").trim();
                events_created.append(count);
            }
        } catch (Exception e) {
            global.errorHandler(e);
        }
    }
    public void setPastEvents(String r)
    {
        try{
            past_events = (TextView) findViewById(R.id.past_events);
            String events="";
            if(!r.contains("ERROR")) {
                if (!r.equals("")) {
                    JSONArray json = new JSONArray(r);
                    for (int j = 0; j < json.length(); j++)
                        events = events + json.getJSONObject(j).getString("Name") + ", ";
                    events = events.substring(0, events.length() - 2);
                    past_events.setText(events);
                }
            }
        }
        catch(Exception e) {
            global.errorHandler(e);
        }
    }
    public void setImages(String sid,int vid, int dr_id,int gdr_id)
    {
        if (!sid.equals("")) {
            ((ImageButton) findViewById(vid)).setBackgroundResource(dr_id);
            ((ImageButton) findViewById(vid)).setVisibility(View.VISIBLE);
            ((ImageButton) findViewById(vid)).setClickable(true);
        } else {
            ((ImageButton) findViewById(vid)).setBackgroundResource(gdr_id);
            ((ImageButton) findViewById(vid)).setClickable(false);
        }
    }
    public void goToInsta (View view ) {
        goToUrl ("https://instagram.com/_u/"+ parseUN(inst));
    }
    public void goToFace (View view) {
        goToUrl ( "fb://profile/"+fb);
    }
    public void goToTwit (View view) {
        goToUrl ( "http://twitter.com/"+ parseUN(tw));
    }
    public String parseUN(String u)
    {
        if (u.charAt(0)=='@')
            u = u.substring(1, u.length());
        return u;
    }
    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        QueryDB DBconn7 = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
        QueryDB DBconn8= new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_block) {
            if (!blocking) {
                try {
                    DBconn7.executeQuery("INSERT INTO BLOCKED_USERS (`User_Id`, `Blocked_User_Id`)" +
                            " VALUES " +
                            "('" + AuthUser.user_id + "', '" + userId + "');");
                    // results = DBconn4.getResults();
                    blocking = true;
                } catch (Exception e) {
                    if (!e.getMessage().equals("NO RESULTS")) {
                        global.errorHandler(e);}
                }
            } else {
                try {
                    DBconn8.executeQuery("DELETE FROM BLOCKED_USERS " +
                            " WHERE " +
                            "Blocked_User_Id = " + userId + " AND User_Id = " + AuthUser.user_id + " ;");
                    //results = DBconn4.getResults();
                    blocking = false;
                } catch (Exception e) {
                    if (!e.getMessage().equals("NO RESULTS")) {
                        global.errorHandler(e);}
                }
            }

            return true;
        }
        //noinspection SimplifiableIfStatement
        QueryDB DBconn9 = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
        QueryDB DBconn10 = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
        if (id == R.id.action_follow) {
            if (!following) {
                try {
                    DBconn9.executeQuery("INSERT INTO FRIENDS (`User_Id`, `Friend_User_Id`)" +
                            " VALUES " +
                            "('" + AuthUser.user_id + "', '" + userId + "');");
                    String [] text = followers.getText().toString().split(" ");
                    Integer count = Integer.valueOf(text[1])+1;
                    followers.setText("Followers: " + count);
                    following = true;
                } catch (Exception e) {
                    if (!e.getMessage().equals("NO RESULTS")) {
                        global.errorHandler(e);
                    }
                }
            }
            else {
                try {
                    DBconn10.executeQuery("DELETE FROM FRIENDS " +
                            " WHERE " +
                            "Friend_User_Id = "+userId + " AND User_Id = "+AuthUser.user_id+" ;");
                    //results = DBconn4.getResults();
                    String [] text = followers.getText().toString().split(" ");
                    Integer count = Integer.valueOf(text[1])-1;
                    followers.setText("Followers: "+count);
                    following = false;
                } catch (Exception e){
                    if (!e.getMessage().equals("NO RESULTS")) {global.errorHandler(e);}
                }
            }
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit_profile) {
            Intent i = new Intent(this, EditProfile.class);
            i.putExtra("category",interests);
            i.putExtra("categoryId",categoryId);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean onPrepareOptionsMenu(final Menu menu)
    {

        class getFollowing implements DatabaseCallback {
            public void onTaskCompleted(final String results){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if (!results.contains("ERROR")) {
                                JSONArray json = new JSONArray(results);
                                String count = json.getJSONObject(0).getString("Count").trim();
                                if (count.equals("0")) {
                                    menu.findItem(R.id.action_follow).setTitle("Follow");
                                    following = false;
                                } else {
                                    menu.findItem(R.id.action_follow).setTitle("Unfollow");
                                    following = true;
                                }
                                menu.findItem(R.id.action_follow).setVisible(true);
                            }
                        }catch(Exception e) {
                            if (!e.getMessage().equals("NO RESULTS")) {
                                global.errorHandler(e);}
                        }
                    }
                });
            }
        }
        class getBlocking implements DatabaseCallback {
            public void onTaskCompleted(final String results){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if (!results.contains("ERROR")) {
                                JSONArray json = new JSONArray(results);
                                String count = json.getJSONObject(0).getString("Count").trim();
                                if (count.equals("0")) {
                                    menu.findItem(R.id.action_block).setTitle("Block");
                                    blocking = false;
                                } else {
                                    menu.findItem(R.id.action_block).setTitle("Unblock");
                                    blocking = true;
                                }
                                menu.findItem(R.id.action_block).setVisible(true);
                            }
                        }catch(Exception e) {
                            if (!e.getMessage().equals("NO RESULTS")) {
                                global.errorHandler(e);}
                        }
                    }
                });
            }
        }

        if(!(AuthUser.user_id.equals(userId))){
            try {
                QueryDB DBConn11 = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
                DBConn11.executeQuery("SELECT COUNT(Friend_User_Id) AS Count FROM FRIENDS WHERE User_Id = " + AuthUser.user_id + " AND Friend_User_Id = " + userId + ";", new getFollowing());

                QueryDB DBConn12 = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
                DBConn12.executeQuery("SELECT COUNT(Blocked_User_Id) AS Count FROM BLOCKED_USERS  WHERE User_Id = " + AuthUser.user_id + " AND Blocked_User_Id = " + userId + ";", new getBlocking());
            }catch(Exception e){
                global.errorHandler(e);
            }


        }else{
            menu.findItem(R.id.action_edit_profile).setVisible(true);
        }
        return true;
    }

}