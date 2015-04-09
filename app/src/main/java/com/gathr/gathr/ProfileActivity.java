package com.gathr.gathr;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class ProfileActivity extends Activity{
    private ProfilePictureView profilePictureView;
    private TextView userNameView;
    private static final String PR = "Profile";
    @Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Gathr");
       // View view = inflater.inflate(R.layout.activity_profile, container, false);
        userNameView = (TextView)findViewById(R.id.user_name);

        final ImageButton insta = (ImageButton) findViewById(R.id.insta);
        insta.setBackgroundResource(R.drawable.insta);
        final ImageButton twit = (ImageButton) findViewById(R.id.twit);
        twit.setBackgroundResource(R.drawable.twit);
        final ImageButton face = (ImageButton) findViewById(R.id.face);
        face.setBackgroundResource(R.drawable.facebook);
        // Find the user's profile picture custom view
        profilePictureView = (ProfilePictureView)findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);
        Intent i = getIntent();
        String userId =i.getStringExtra("userId");
        //Log.i("Second Page","USER_ID:1 "+userId );
        AuthUser.user_id = userId;
        //Log.i("Second Page","USER_ID:2 "+AuthUser.user_id );
        profilePictureView.setProfileId(AuthUser.user_id);
        profilePictureView.setVisibility(View.VISIBLE);
        userNameView.setText(AuthUser.user_fname+" "+AuthUser.user_lname);


    }
}

