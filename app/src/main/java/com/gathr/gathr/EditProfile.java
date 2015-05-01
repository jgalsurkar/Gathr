package com.gathr.gathr;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.facebook.widget.ProfilePictureView;
import org.json.JSONArray;
import org.json.JSONException;

public class EditProfile extends ActionBarActivity {

    private ProfilePictureView profilePictureView;
    private TextView userNameView;
    private EditText about_me;

    MyGlobals global = new MyGlobals(this);
    QueryDB DBconn = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);

    public String userId = AuthUser.user_id, category, categoryId;
    String results;

    @Override protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        try {

            userNameView = (TextView) findViewById(R.id.user_name);
            profilePictureView = (ProfilePictureView) findViewById(R.id.selection_profile_pic);
            profilePictureView.setCropped(true);
            about_me = (EditText) findViewById(R.id.about_me);

            final EditText instagram = (EditText) findViewById(R.id.instagram);
            final EditText twitter = (EditText) findViewById(R.id.twitter);
            final EditText facebook = (EditText) findViewById(R.id.facebook);
            final EditText my_interests = (EditText) findViewById(R.id.my_interests);

            Intent i = getIntent();
            category = i.getStringExtra("category");
            categoryId = i.getStringExtra("categoryId");
            my_interests.setText(category);
            //DBconn.executeQuery("SELECT * FROM USERS WHERE Id = " + userId + ";");
            results = global.getUserJSON();//DBconn.getResults();
            if (!results.contains("ERROR")) {
                JSONArray json = new JSONArray(results);
                //int n = json.length();
                userNameView.setText(json.getJSONObject(0).getString("First_Name") + " " + json.getJSONObject(0).getString("Last_Name"));
                profilePictureView.setProfileId(json.getJSONObject(0).getString("Facebook_Id"));
                about_me.setText(json.getJSONObject(0).getString("About_Me"));
                instagram.setText(json.getJSONObject(0).getString("Instagram"));
                facebook.setText(json.getJSONObject(0).getString("Facebook"));
                twitter.setText(json.getJSONObject(0).getString("Twitter"));
            }
        }  catch(Exception e){
            global.errorHandler(e);
        }
    }

    public void openCategory(View view){
        Intent i = new Intent(this, ListViewMultipleSelectionActivity.class);
        //i.putExtra("userId", AuthUser.user_id);
        i.putExtra("categoryId",categoryId);
        startActivity(i);
        finish();
    }

    public void saveChanges (View view) {
        String about_me = DBconn.escapeString(getElementText(R.id.about_me));
        //String my_interests = DBconn.escapeString(getElementText(R.id.my_interests));
        String instagram = DBconn.escapeString(getElementText(R.id.instagram));
        String twitter = DBconn.escapeString(getElementText(R.id.twitter));
        String facebook =DBconn.escapeString(getElementText(R.id.facebook));
        try{
            DBconn.executeQuery("UPDATE USERS " +
                    "SET `About_Me`='"+about_me+"',`Instagram`='"+instagram+"',`Facebook`='"+facebook+"',`Twitter`='"+twitter+"' "+
                    "where `Id` ="+userId+";");
            DBconn.getResults();

            global.getUserJSON(1);

            DBconn.executeQuery("ADD INTERESTS " +categoryId+" FOR "+userId);
            //DBconn.getResults(); We dont really need to wait
        }catch(Exception e){
            global.errorHandler(e);
        }
        Intent i = new Intent(this, Profile.class);
        startActivity(i);
        finish();
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