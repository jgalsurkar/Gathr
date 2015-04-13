package com.gathr.gathr;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;

public class ProfileActivity extends Activity{
    private ProfilePictureView profilePictureView;
    private TextView userNameView,aboutMe;
    private static final String PR = "Profile";
    public String userId;
    String results;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Gathr");

        String[] titles = new String[]{"Map","My Profile","Gathrings","Friends","Settings","Notifications","Log Out"};
        Class<?>[] links = { MapsActivity.class, ProfileActivity.class, CreateEvent.class, CreateEvent.class, CreateEvent.class, CreateEvent.class, MainActivity.class};
        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, titles, links );

        userNameView = (TextView)findViewById(R.id.user_name);

        final ImageButton insta = (ImageButton) findViewById(R.id.insta);
        insta.setBackgroundResource(R.drawable.insta);
        final ImageButton twit = (ImageButton) findViewById(R.id.twit);
        twit.setBackgroundResource(R.drawable.twit);
        final ImageButton face = (ImageButton) findViewById(R.id.face);
        face.setBackgroundResource(R.drawable.facebook);

        profilePictureView = (ProfilePictureView)findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);
        aboutMe =(TextView)findViewById(R.id.about_me);
        Intent i = getIntent();
        userId =i.getStringExtra("userId");
        //String userId = AuthUser.fb_id;
        QueryDB DBconn = new QueryDB(AuthUser.fb_id, AuthUser.user_id);
        DBconn.executeQuery("SELECT * FROM USERS WHERE Id = "+userId+";");
        results = DBconn.getResults();
        if (!results.contains("ERROR"))
        {
            JSONArray json;
            try {
                json = new JSONArray(results);
                int n = json.length();
                //String p = n+"";
                // Log.i(TAG,"JSON: "+p);
                //AuthUser.id = json.getJSONObject(n-1).getString("Id");
                //AuthUser.user_id = json.getJSONObject(n-1).getString("Facebook_Id");
                userNameView.setText(json.getJSONObject(n-1).getString("First_Name")+" "+json.getJSONObject(n-1).getString("Last_Name"));
                profilePictureView.setProfileId(json.getJSONObject(n-1).getString("Facebook_Id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        aboutMe.setText("About Me\n"+" The id is:"+ results);
        // profilePictureView.setProfileId(AuthUser.fb_id);
        // profilePictureView.setVisibility(View.VISIBLE);
        //userNameView.setText(AuthUser.user_fname+" "+AuthUser.user_lname);


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


}
