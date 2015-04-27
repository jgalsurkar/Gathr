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


public class Profile extends ActionBarActivity {
    private ProfilePictureView profilePictureView;
    private TextView userNameView,about_me;
    private static final String PR = "Profile";
    public String userId,interests = "",categoryId="";
    String results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String[] titles = new String[]{"Map","My Profile","Gathrings","Friends","Settings","Notifications","Log Out"};
        Class<?>[] links = { MapsActivity.class, Profile.class, CreateEvent.class, CreateEvent.class, CreateEvent.class, CreateEvent.class, MainActivity.class};
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
        TextView my_interests = (TextView) findViewById(R.id.my_interests);
        about_me =(TextView)findViewById(R.id.about_me);
        Intent i = getIntent();
        userId =i.getStringExtra("userId");
        if (userId==null)
            userId=AuthUser.user_id;
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        DBconn.executeQuery("SELECT Category_Id, Name FROM USER_INTERESTS JOIN CATEGORIES ON CATEGORIES.Id = USER_INTERESTS.Category_Id WHERE User_Id = " + userId + ";");
        results = DBconn.getResults();
        Log.i("result",results);
        if (!results.contains("ERROR")) {
            JSONArray json1;
            try {
                json1 = new JSONArray(results);
                int n = json1.length();
                for (int j = 0; j < n; j++) {
                    interests = interests + json1.getJSONObject(j).getString("Name") + ",";
                    categoryId = categoryId + json1.getJSONObject(j).getString("Category_Id")+",";
                }
                interests=interests.substring(0, interests.length() - 1);
                Log.i("list","category "+interests+" : "+categoryId );
                my_interests.setText(interests);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            Log.i(PR, "USER FROM: " + AuthUser.user_id);
            i.putExtra("userId", AuthUser.user_id);
            i.putExtra("category",interests);
            i.putExtra("categoryId",categoryId);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}