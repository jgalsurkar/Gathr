package com.gathr.gathr;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.facebook.widget.ProfilePictureView;
import com.gathr.gathr.classes.AuthUser;
import com.gathr.gathr.classes.MyGlobals;
import com.gathr.gathr.classes.SidebarGenerator;
import com.gathr.gathr.database.DatabaseCallback;
import com.gathr.gathr.database.QueryDB;

import org.json.JSONArray;
import org.json.JSONObject;


public class Profile extends ActionBarActivity {
    public String userId = AuthUser.getUserId(this), interests = "", categoryId = "", results = "", inst, fb, tw;
    TextView past_events, followers, events_created, events_attended;
    Boolean following = false, blocking = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setActionBar();

        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this);

        try {
            Intent i = getIntent();
            String temp = i.getStringExtra("userId");
            QueryDB DBconn = new QueryDB(this, "getProfile.php");
            if (temp != null)
                userId = temp;

            DBconn.executeQuery(userId, new getUser());
        }
        catch(Exception e) {
            global.errorHandler(e);
        }
    }

    public void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        //displaying custom ActionBar
        View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);
        actionBar.setCustomView(mActionBarView);
        TextView title= (TextView)mActionBarView.findViewById(R.id.title);
        title.setText(R.string.title_activity_profile);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }
    public void openSideBar(View view){
        DrawerLayout sidebar = (DrawerLayout) findViewById(R.id.drawer_layout);
        sidebar.openDrawer(Gravity.LEFT);
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

                TextView my_interests = (TextView) findViewById(R.id.my_interests);
                my_interests.setText(elem1.getString("Interests"));

                events_created = (TextView) findViewById(R.id.events_created);
                events_created.append(elem1.getString("Num_Created_Events").trim());

                past_events = (TextView) findViewById(R.id.past_events);
                past_events.setText(elem1.getString("Past_Events"));

                events_attended = (TextView) findViewById(R.id.events_attended);
                events_attended.append(elem1.getString("Num_Joined_Events"));

                followers = (TextView) findViewById(R.id.followers);
                followers.append(elem1.getString("Num_Friends"));

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



    public void setImages(String sid,int vid, int dr_id,int gdr_id)
    {
        if (!sid.equals("")) {
            (findViewById(vid)).setBackgroundResource(dr_id);
            (findViewById(vid)).setVisibility(View.VISIBLE);
            (findViewById(vid)).setClickable(true);
        } else {
            (findViewById(vid)).setBackgroundResource(gdr_id);
            (findViewById(vid)).setClickable(false);
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
    public String parseUN(String u){
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
        QueryDB DBconn7 = new QueryDB(this);
        QueryDB DBconn8= new QueryDB(this);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_block) {
            if (!blocking) {
                try {
                    DBconn7.executeQuery("INSERT INTO BLOCKED_USERS (`User_Id`, `Blocked_User_Id`)" +
                            " VALUES " +
                            "('" + AuthUser.getUserId(this) + "', '" + userId + "');");
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
                            "Blocked_User_Id = " + userId + " AND User_Id = " + AuthUser.getUserId(this) + " ;");
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
        QueryDB DBconn9 = new QueryDB(this);
        QueryDB DBconn10 = new QueryDB(this);
        if (id == R.id.action_follow) {
            if (!following) {
                try {
                    DBconn9.executeQuery("INSERT INTO FRIENDS (`User_Id`, `Friend_User_Id`)" +
                            " VALUES " +
                            "('" + AuthUser.getUserId(this) + "', '" + userId + "');");
                    String [] text = followers.getText().toString().split(" ");
                    Integer count = Integer.valueOf(text[1])+1;
                    followers.setText("Followers: " + count);
                    following = true;
                } catch (Exception e) {
                    global.errorHandler(e);
                }
            }
            else {
                try {
                    DBconn10.executeQuery("DELETE FROM FRIENDS WHERE Friend_User_Id = "+userId + " AND User_Id = "+ AuthUser.getUserId(this) +" ;");
                    String[] text = followers.getText().toString().split(" ");
                    Integer count = Integer.valueOf(text[1])-1;
                    followers.setText("Followers: "+count);
                    following = false;
                } catch (Exception e){
                    global.errorHandler(e);
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

        if(!(AuthUser.getUserId(this).equals(userId))){
            try {
                QueryDB DBConn11 = new QueryDB(this);
                DBConn11.executeQuery("SELECT COUNT(Friend_User_Id) AS Count FROM FRIENDS WHERE User_Id = " + AuthUser.getUserId(this) + " AND Friend_User_Id = " + userId + ";", new getFollowing());

                QueryDB DBConn12 = new QueryDB(this);
                DBConn12.executeQuery("SELECT COUNT(Blocked_User_Id) AS Count FROM BLOCKED_USERS  WHERE User_Id = " + AuthUser.getUserId(this) + " AND Blocked_User_Id = " + userId + ";", new getBlocking());
            }catch(Exception e){
                global.errorHandler(e);
            }
        }else{
            menu.findItem(R.id.action_edit_profile).setVisible(true);
        }
        return true;
    }

}