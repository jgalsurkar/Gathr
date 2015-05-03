package com.gathr.gathr;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
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


public class Profile extends ActionBarActivity {
    public String userId = AuthUser.user_id,
            interests = "",
            categoryId = "",
            results = "",
            inst,
            fb,
            tw;
    TextView past_events;
    MyGlobals global = new MyGlobals(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        try {
            new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, global.titles, global.links );

            Intent i = getIntent();
            String temp = i.getStringExtra("userId");

            QueryDB DBconn = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
            if (temp != null) {
                userId = temp;
                DBconn.executeQuery("SELECT * FROM USERS WHERE Id = " + userId + ";");
                results = DBconn.getResults();
            }else{
                results = global.getUserJSON();
            }
        }
        catch(Exception e) {
            if (e.getMessage().equals("NO RESULTS")) {

            } else {
                global.errorHandler(e);

            }
        }
        try{
            QueryDB DBconn2 = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);

            DBconn2.executeQuery("SELECT Category_Id, Name FROM  CATEGORIES JOIN (SELECT * FROM USER_INTERESTS WHERE User_Id = " + userId + ") AS JOINED WHERE JOINED.Category_Id = Id");

            TextView userNameView = (TextView) findViewById(R.id.user_name);
            ProfilePictureView profilePictureView = (ProfilePictureView)findViewById(R.id.selection_profile_pic);
            profilePictureView.setCropped(true);
            TextView about_me =(TextView)findViewById(R.id.about_me);
            JSONArray json = new JSONArray(results);
            JSONObject elem1 = json.getJSONObject(0);
            userNameView.setText(elem1.getString("First_Name")+" "+elem1.getString("Last_Name"));
            profilePictureView.setProfileId(elem1.getString("Facebook_Id"));
            about_me.setText(elem1.getString("About_Me"));
            inst = elem1.getString("Instagram");
            fb = elem1.getString("Facebook");
            tw = elem1.getString("Twitter");
            if(!inst.equals("")) {
                ((ImageButton) findViewById(R.id.insta)).setBackgroundResource(R.drawable.insta);
                ((ImageButton) findViewById(R.id.insta)).setVisibility(View.VISIBLE);
            }
            if(!fb.equals("")) {
                ((ImageButton) findViewById(R.id.face)).setBackgroundResource(R.drawable.facebook);
                ((ImageButton) findViewById(R.id.face)).setVisibility(View.VISIBLE);
            }
            if(!tw.equals("")) {
                ((ImageButton) findViewById(R.id.twit)).setBackgroundResource(R.drawable.twit);
                ((ImageButton) findViewById(R.id.twit)).setVisibility(View.VISIBLE);
            }

            past_events = (TextView) findViewById(R.id.past_events);
            TextView my_interests = (TextView) findViewById(R.id.my_interests);
            results = DBconn2.getResults();
            if (!results.equals(""))
            {json = new JSONArray(results);

                for (int j = 0; j < json.length(); j++) {
                    interests = interests + json.getJSONObject(j).getString("Name") + ", ";
                    categoryId = categoryId + json.getJSONObject(j).getString("Category_Id")+",";
                }
                interests = interests.substring(0, interests.length() - 2);
                my_interests.setText(interests);}
            else my_interests.setText("");

        }catch(Exception e) {
            if (e.getMessage().equals("NO RESULTS")) {

            } else {
                global.errorHandler(e);

            }
        }
        try{
            QueryDB DBconn3 = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
            DBconn3.executeQuery("SELECT Name FROM EVENTS WHERE Organizer= " + userId + " ORDER BY Date DESC LIMIT 15");
            results = DBconn3.getResults();
            String events="";
            if (!results.equals(""))
            {
                JSONArray  json = new JSONArray(results);
                for (int j = 0; j < json.length(); j++)
                    events = events + json.getJSONObject(j).getString("Name") + ", ";
                events = events.substring(0, events.length() - 1);
                past_events.setText(events);
            }
        }
        catch(Exception e) {
            if (e.getMessage().equals("NO RESULTS")) {

            } else {
                global.errorHandler(e);

            }
        }
    }
    public void goToInsta (View view ) {
        goToUrl ("https://instagram.com/_u/"+inst);
    }
    public void goToFace (View view) {
        goToUrl ( "fb://profile/"+fb);
    }
    public void goToTwit (View view) {
        goToUrl ( "http://twitter.com/"+tw);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_block) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_follow) {
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
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(AuthUser.user_id != userId){
            menu.findItem(R.id.action_block).setVisible(true);
            menu.findItem(R.id.action_follow).setVisible(true);
        }else{
            menu.findItem(R.id.action_edit_profile).setVisible(true);
        }
        return true;
    }

}