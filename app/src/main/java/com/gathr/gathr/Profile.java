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


public class Profile extends ActionBarActivity {
    private ProfilePictureView profilePictureView;
    private TextView userNameView,about_me, past_events;
    public String userId = AuthUser.user_id,interests = "",categoryId="", results;

    MyGlobals global = new MyGlobals(this);
    QueryDB DBconn = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        global.checkInternet();
        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, global.titles, global.links );

        userNameView = (TextView)findViewById(R.id.user_name);

        ((ImageButton) findViewById(R.id.insta)).setBackgroundResource(R.drawable.insta);
        ((ImageButton) findViewById(R.id.twit)).setBackgroundResource(R.drawable.twit);
        ((ImageButton) findViewById(R.id.face)).setBackgroundResource(R.drawable.facebook);
        past_events = (TextView) findViewById(R.id.past_events);

        profilePictureView = (ProfilePictureView)findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);
        TextView my_interests = (TextView) findViewById(R.id.my_interests);
        about_me =(TextView)findViewById(R.id.about_me);
        Intent i = getIntent();

        String temp = i.getStringExtra("userId");
        if (temp != null)
            userId = temp;

        try {
            DBconn.executeQuery("SELECT * FROM USERS WHERE Id = " + userId + ";");
            results = DBconn.getResults();
            JSONArray json;
            try {
                json = new JSONArray(results);
                int n = json.length();
                userNameView.setText(json.getJSONObject(n-1).getString("First_Name")+" "+json.getJSONObject(n-1).getString("Last_Name"));
                profilePictureView.setProfileId(json.getJSONObject(n-1).getString("Facebook_Id"));
                about_me.setText(json.getJSONObject(n-1).getString("About_Me"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }catch(GathrException e){
            Log.i("Exception", e.error);
        }

       try {
           DBconn.executeQuery("SELECT Category_Id, Name FROM USER_INTERESTS JOIN CATEGORIES ON CATEGORIES.Id = USER_INTERESTS.Category_Id WHERE User_Id = " + userId + ";");
           results = DBconn.getResults();
           JSONArray json1;
           try {
               json1 = new JSONArray(results);
               int n = json1.length();
               for (int j = 0; j < n; j++) {
                   interests = interests + json1.getJSONObject(j).getString("Name") + ",";
                   categoryId = categoryId + json1.getJSONObject(j).getString("Category_Id")+",";
               }
               interests = interests.substring(0, interests.length() - 1);
               my_interests.setText(interests);
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }catch(GathrException e){
           Log.i ("Exception", e.error);
       }

       try {
           DBconn.executeQuery("SELECT Name FROM EVENTS WHERE Organizer= " + userId + ";");
           results = DBconn.getResults();
           JSONArray json;
           try {
               String events="";
               json = new JSONArray(results);
               int n = json.length();
               for (int j = 0; j < n; j++)
                   events = events + json.getJSONObject(j).getString("Name") + ",";
               events = events.substring(0, events.length() - 1);
               past_events.setText(events);
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }catch(GathrException e){
           Log.i ("Exception", e.error);
       }
    }
    public void goToInsta (View view ) {
        goToUrl ("https://instagram.com/p/");
    }

    public void goToFace (View view) {
        goToUrl ( "fb://profile/");
    }

    public void goToTwit (View view) {
        goToUrl ( "http://twitter.com/");
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
            //Log.i(PR, "USER FROM: " + AuthUser.user_id);
            //i.putExtra("userId", AuthUser.user_id);
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