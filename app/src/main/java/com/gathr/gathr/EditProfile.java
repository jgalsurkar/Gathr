package com.gathr.gathr;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.facebook.widget.ProfilePictureView;
import com.gathr.gathr.classes.AuthUser;
import com.gathr.gathr.classes.MyGlobals;
import com.gathr.gathr.database.DatabaseCallback;
import com.gathr.gathr.database.QueryDB;

import org.json.JSONArray;

public class EditProfile extends ActionBarActivity {

    private ProfilePictureView profilePictureView;
    private TextView userNameView;
    private EditText about_me;

    MyGlobals global = new MyGlobals(this);
    QueryDB DBconn = new QueryDB(this);

    public String userId = AuthUser.getUserId(this), category, categoryId;
    String results;
    EditText my_interests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setActionBar();
        try {
            ((ImageButton)(findViewById(R.id.add_category))).setBackgroundResource(R.drawable.create_contact);
            userNameView = (TextView) findViewById(R.id.user_name);
            profilePictureView = (ProfilePictureView) findViewById(R.id.selection_profile_pic);
            profilePictureView.setCropped(true);
            about_me = (EditText) findViewById(R.id.about_me);
            final EditText instagram = (EditText) findViewById(R.id.instagram);
            instagram.setHint("@username");
            final EditText twitter = (EditText) findViewById(R.id.twitter);
            twitter.setHint("@username");
            final EditText facebook = (EditText) findViewById(R.id.facebook);
            facebook.setHint("username");
            my_interests = (EditText) findViewById(R.id.my_interests);
            Intent i = getIntent();
            category = i.getStringExtra("category");
            categoryId = i.getStringExtra("categoryId");
            my_interests.setText(category);
            results = global.getUserJSON();
            if (!results.contains("ERROR")) {
                JSONArray json = new JSONArray(results);
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
        i.putExtra("categoryId",categoryId);
        i.putExtra("from","edit");
        startActivityForResult(i,0);
    }
    public void setActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        //displaying custom ActionBar
        View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);
        actionBar.setCustomView(mActionBarView);
        ((ImageButton)mActionBarView.findViewById(R.id.btn_slide)).setVisibility(View.INVISIBLE);
        ((TextView)mActionBarView.findViewById(R.id.title)).setText(R.string.title_activity_edit_profile);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                category = data.getStringExtra("category");
                categoryId = data.getStringExtra("categoryId");
                my_interests.setText(category);
            }
        }
    }
    public void saveChanges (View view) {
        String about_me = DBconn.escapeString(getElementText(R.id.about_me));
        String instagram = DBconn.escapeString(getElementText(R.id.instagram));
        if(!instagram.equals(""))
        {
            if (instagram.charAt(0)!='@')
                instagram = "@"+ instagram;
        }
        String twitter = DBconn.escapeString(getElementText(R.id.twitter));
        if(!twitter.equals("")) {
            if (twitter.charAt(0) != '@')
                twitter = "@" + twitter;
        }
        String facebook =DBconn.escapeString(getElementText(R.id.facebook));
        try{
            final Intent i = new Intent(this, Profile.class);
            class UpdateUser implements DatabaseCallback {
                public void onTaskCompleted(final String results) {
                    startActivity(i);
                    finish();
                }
            }
            DBconn.executeQuery("UPDATE USERS " +
                    "SET `About_Me`='"+about_me+"',`Instagram`='"+instagram+"',`Facebook`='"+facebook+"',`Twitter`='"+twitter+"' "+
                    "where `Id` ="+userId+";",new UpdateUser());
            global.getUserJSON(1);
            DBconn.executeQuery("ADD INTERESTS " +categoryId+" FOR "+userId);
            // We dont really need to wait
        }catch(Exception e){
            global.errorHandler(e);
        }
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
}