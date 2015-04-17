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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;


public class EditProfile extends ActionBarActivity {
    private ProfilePictureView profilePictureView;
    private TextView userNameView;
    private EditText twitter, facebook, instagram,about_me,my_interests;
    private static final String PR = "Profile";
    public String userId;
    String results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        String[] titles = new String[]{"Map","My Profile","Gathrings","Friends","Settings","Notifications","Log Out"};
        Class<?>[] links = { MapsActivity.class, Profile.class, CreateEvent.class, CreateEvent.class, CreateEvent.class, CreateEvent.class, MainActivity.class};
        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, titles, links );

        userNameView = (TextView)findViewById(R.id.user_name);

        final EditText instagram = (EditText) findViewById(R.id.instagram);

        final EditText twitter = (EditText) findViewById(R.id.twitter);

        final EditText facebook = (EditText) findViewById(R.id.facebook);


        profilePictureView = (ProfilePictureView)findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);
        about_me =(EditText)findViewById(R.id.about_me);

        Intent i = getIntent();
        userId = i.getStringExtra("userId");
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
                userNameView.setText(json.getJSONObject(n-1).getString("First_Name")+" "+json.getJSONObject(n-1).getString("Last_Name"));
                profilePictureView.setProfileId(json.getJSONObject(n-1).getString("Facebook_Id"));
                about_me.setText(json.getJSONObject(n-1).getString("About_Me"));
                instagram.setText(json.getJSONObject(n-1).getString("Instagram"));
                facebook.setText(json.getJSONObject(n-1).getString("Facebook"));
                twitter.setText(json.getJSONObject(n-1).getString("Twitter"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveChanges (View view) {
        MyGlobals global = new MyGlobals(this);

        QueryDB DBconn = new QueryDB(AuthUser.fb_id, AuthUser.user_id);

        String about_me = DBconn.escapeString(getElementText(R.id.about_me));
        String my_interests = DBconn.escapeString(getElementText(R.id.my_interests));
        String instagram = DBconn.escapeString(getElementText(R.id.instagram));
        String twitter = DBconn.escapeString(getElementText(R.id.twitter));
        String facebook =DBconn.escapeString(getElementText(R.id.facebook));

        DBconn.executeQuery("UPDATE USERS " +
                "SET `About_Me`='"+about_me+"',`Instagram`='"+instagram+"',`Facebook`='"+facebook+"',`Twitter`='"+twitter+"' "+
                "where `Id` ="+userId+";");

        String results = DBconn.getResults();
        Log.i("Results","HERE is result: "+userId);


        Intent i = new Intent(this, Profile.class);
        i.putExtra("userId", userId);
        startActivity(i);
    }
    public String getElementText(int viewId){
        return ((EditText)findViewById(viewId)).getText().toString();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
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
